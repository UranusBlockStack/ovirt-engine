package org.ovirt.engine.core.bll;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ovirt.engine.core.bll.interfaces.BackendInternal;
import org.ovirt.engine.core.common.AuditLogType;
import org.ovirt.engine.core.common.action.AddVmTemplateParameters;
import org.ovirt.engine.core.common.action.CreateAllSnapshotsFromVmParameters;
import org.ovirt.engine.core.common.action.FenceVdsActionParameters;
import org.ovirt.engine.core.common.action.MaintenanceNumberOfVdssParameters;
import org.ovirt.engine.core.common.action.MoveVmParameters;
import org.ovirt.engine.core.common.action.RunVmParams;
import org.ovirt.engine.core.common.action.ShutdownVmParameters;
import org.ovirt.engine.core.common.action.VdcActionType;
import org.ovirt.engine.core.common.action.VdsActionParameters;
import org.ovirt.engine.core.common.action.VmOperationParameterBase;
import org.ovirt.engine.core.common.businessentities.FenceActionType;
import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.businessentities.ScheduleType;
import org.ovirt.engine.core.common.businessentities.Snapshot.SnapshotType;
import org.ovirt.engine.core.common.businessentities.StorageDomain;
import org.ovirt.engine.core.common.businessentities.StorageDomainType;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;
import org.ovirt.engine.core.dal.dbbroker.auditloghandling.AuditLogDirector;
import org.ovirt.engine.core.dal.dbbroker.auditloghandling.AuditLogableBase;
import org.ovirt.engine.core.utils.ejb.BeanProxyType;
import org.ovirt.engine.core.utils.ejb.BeanType;
import org.ovirt.engine.core.utils.ejb.EjbUtils;
import org.ovirt.engine.core.utils.log.Log;
import org.ovirt.engine.core.utils.log.LogFactory;
import org.ovirt.engine.core.utils.timer.OnTimerMethodAnnotation;
import org.ovirt.engine.core.utils.timer.SchedulerUtil;
import org.ovirt.engine.core.utils.timer.SchedulerUtilQuartzImpl;

@Singleton
@Startup
@DependsOn({"Backend", "Scheduler"})
public class InitScheduleManager {

    private static Log log = LogFactory.getLog(InitScheduleManager.class);
    private static InitScheduleManager instance = new InitScheduleManager();
    private static HashMap<Guid, String> scheduleJobMap = new HashMap<Guid, String>();

    public static InitScheduleManager getInstance() {
        return instance;
    }

    public static SchedulerUtil getScheduler() {
        return SchedulerUtilQuartzImpl.getInstance();
    }

    @PostConstruct
    public void init() {
        log.infoFormat("InitScheduleManager: {0}", new Date());
        try {
            List<Schedule> schedule = DbFacade.getInstance().getSchedulesDao().getAll();
            log.infoFormat("InitScheduleManager query schedule list success, size {0}.", schedule.size());
            for (int i = 0; i < schedule.size(); i++) {
                addScheduleJob(schedule.get(i).getId());
            }
        } catch(Exception e) {
            log.errorFormat("InitScheduleManager init error, message = {0} ", e.getMessage());
        }
    }

    /**
     * Add Schedule Job
     * @param id Guid
     * @return add success or fail
     */
    public static boolean addScheduleJob(Guid id) {
        if(scheduleJobMap.containsKey(id)) {
            log.errorFormat("Add schedule {0} job fail, because already exists.", id);
            return false;
        }
        Schedule schedule = DbFacade.getInstance().getSchedulesDao().get(id);
        if(schedule != null) {
            String typeFunction = "";
            if(schedule.getType() == ScheduleType.VmBackup) {
                typeFunction = "_Schedule_VmBackup_Timer_";
            } else if(schedule.getType() == ScheduleType.VmSnapshot) {
                typeFunction = "_Schedule_VmSnapshot_Timer_";
            } else if(schedule.getType() == ScheduleType.VmTemplate) {
                typeFunction = "_Schedule_VmTemplate_Timer_";
            } else if(schedule.getType() == ScheduleType.VmRun) {
                typeFunction = "_Schedule_VmRun_Timer_";
            } else if(schedule.getType() == ScheduleType.VmSuspend) {
                typeFunction = "_Schedule_VmSuspend_Timer_";
            } else if(schedule.getType() == ScheduleType.VmStop) {
                typeFunction = "_Schedule_VmStop_Timer_";
            } else if(schedule.getType() == ScheduleType.VmReboot) {
                typeFunction = "_Schedule_VmReboot_Timer_";
            } else if(schedule.getType() == ScheduleType.VDSStart) {
                typeFunction = "_Schedule_VDSRun_Timer_";
            } else if(schedule.getType() == ScheduleType.VDSReboot) {
                typeFunction = "_Schedule_VDSReboot_Timer_";
            } else if(schedule.getType() == ScheduleType.VDSStop) {
                typeFunction = "_Schedule_VDSStop_Timer_";
            } else if(schedule.getType() == ScheduleType.VDSMaintenance) {
                typeFunction = "_Schedule_VDSMaintenance_Timer_";
            } else if(schedule.getType() == ScheduleType.VDSActivate) {
                typeFunction = "_Schedule_VDSActivate_Timer_";
            } else {
                log.errorFormat("Add schedule {0} job fail, because already exists.", id);
                return false;
            }
            String cronExpression = getScheduleCronExpression(schedule.getStrategy());
            String jobIDS = "";
            try {
                JSONArray jsonArray = new JSONArray(schedule.getObject());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Guid _id = Guid.createGuidFromString(jsonObject.getString("id"));
                    String jobID = getScheduler().scheduleACronJob(getInstance(), typeFunction, new Class[] {}, new Object[] {_id}, cronExpression);
                    jobIDS += (jobIDS.length() > 0 ?"@":"") + jobID;
                }
            } catch (JSONException e) {
            }
            scheduleJobMap.put(id, jobIDS);
            log.infoFormat("Add Schedule {0} Job ID  {1} Success, Expression : {2}.", id, jobIDS, cronExpression );
        } else {
            log.errorFormat("Add Schedule {0} Job fail, because query fail.", id);
        }
        return false;
    }

    /**
     * Remove scheduleJob
     * @param id
     * @return remove success or fail
     */
    public static boolean removeScheduleJob(Guid id) {
        if(scheduleJobMap.containsKey(id)) {
            String[] jobIDS = scheduleJobMap.get(id).split("@");
            for (int i = 0; i < jobIDS.length; i++) {
                getScheduler().deleteJob(jobIDS[i]);
            }
            scheduleJobMap.remove(id);
            log.infoFormat("Remove Schedule Job ID  {0} Success", id);
            return true;
        }
        log.errorFormat("Remove Schedule Job ID  {0} fail.", id);
        return false;
    }

    /**
     * Get Schedule Cron Expression By Strategy
     * @param strategy Strategy
     * @return Cron Expression
     */
    private static String getScheduleCronExpression(String strategy) {
        String cronExpression = "";
        try {
            JSONObject jsonObject = new JSONObject(strategy);
            ScheduleType type = ScheduleType.valueOf(jsonObject.getString("strategy1"));
            String time = jsonObject.getString("strategy2");
            SimpleDateFormat formatter = new SimpleDateFormat((type == ScheduleType.Time?"yyyy-MM-dd ":"") + "HH:mm:ss");
            Date date = formatter.parse(time);
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(date.getTime());
            String expression = "%d %d %d * * ?";
            if(type == ScheduleType.Day) {
                expression = "%d %d %d * * ?";
            } else if(type == ScheduleType.Weekly) {
                expression = "%d %d %d */7 * ?";
            } else if(type == ScheduleType.Month) {
                expression = "%d %d %d */30 * ?";
            } else if(type == ScheduleType.Time) {
                expression = "%d %d %d %d %d ? %d";
            } else {
                log.errorFormat("Get schedule cron expression error, because ScheduleType {0} unknown .", type.name());
                return cronExpression;
            }
           if(type == ScheduleType.Time) {
                cronExpression = String.format(expression, calendar.get(Calendar.SECOND),
                        calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONDAY) + 1,
                        calendar.get(Calendar.YEAR));
            } else {
                cronExpression = String.format(expression, calendar.get(Calendar.SECOND),
                        calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY));
            }
        } catch (Exception e) {
            log.errorFormat("Get schedule cron expression error, message : {0} , strategy : ", e.getMessage(), strategy);
        }
        return cronExpression;
    }

    @OnTimerMethodAnnotation("_Schedule_VmBackup_Timer_")
    public synchronized void scheduleVmBackupTimer(Guid id) {
        log.infoFormat("Schedule VmBackup Timer ID {0}.", id);
        VM vm = DbFacade.getInstance().getVmDao().get(id);
        if(null != vm) {
            Guid poolID = vm.getStoragePoolId();
            DbFacade.getInstance().                                getStorageDomainDao();
            List<StorageDomain> domains = DbFacade.getInstance().getStorageDomainDao().getAllForStoragePool(poolID, null, false);
            StorageDomain domain = null;
            for (int i = 0; i < domains.size(); i++) {
                if(domains.get(i).getStorageDomainType() == StorageDomainType.ImportExport) {
                    domain = domains.get(i);
                    break;
                }
            }
            if(domain == null) {
                log.errorFormat("Schedule VmBackup error , because export domain is null.");
                return;
            }
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            MoveVmParameters params = new MoveVmParameters(id, domain.getId());
            params.setForceOverride(true);
            AuditLogDirector.log(getAuditLogableBaseByVM(id), AuditLogType.SCHEDULE_VMBACKUP);
            backend.runInternalAction(VdcActionType.ExportVm, params);
        } else {
            log.errorFormat("Schedule VmBackup error , because VM is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VmSnapshot_Timer_")
    public synchronized void scheduleVmSnapshotTimer(Guid id) {
        log.infoFormat("Schedule VmSnapshot Timer ID {0}.", id);
        VM vm = DbFacade.getInstance().getVmDao().get(id);
        if(null != vm) {
            String description = "Schedule_" + (new SimpleDateFormat("yy_MM_dd_HH_mm_ss").format(new Date()));
            CreateAllSnapshotsFromVmParameters createAllSnapshotsFromVmParameters = new CreateAllSnapshotsFromVmParameters(vm.getId(), description);
            createAllSnapshotsFromVmParameters.setQuotaId(vm.getQuotaId());
            createAllSnapshotsFromVmParameters.setSnapshotType(SnapshotType.REGULAR);
            AuditLogableBase logable = getAuditLogableBaseByVM(id);
            logable.addCustomValue("VmDesc", description);
            AuditLogDirector.log(logable, AuditLogType.SCHEDULE_VMSNAPSHOT);
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            backend.runInternalAction(VdcActionType.CreateAllSnapshotsFromVm, createAllSnapshotsFromVmParameters);
        } else {
            log.errorFormat("Schedule VmSnapshot error , because VM is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VmTemplate_Timer_")
    public synchronized void scheduleVmTemplateTimer(Guid id) {
        log.infoFormat("Schedule VmTemplate Timer ID {0}.", id);
        VM vm = DbFacade.getInstance().getVmDao().get(id);
        if(null != vm) {
            AddVmTemplateParameters addVmTemplateParameters =
                    new AddVmTemplateParameters(vm,
                            vm.getName() + (new SimpleDateFormat("yy_MM_dd_HH_mm_ss").format(new Date())),
                            (String) vm.getVmDescription());
            addVmTemplateParameters.setPublicUse(true);
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVM(id), AuditLogType.SCHEDULE_VMTEMPLATE);
            backend.runInternalAction(VdcActionType.AddVmTemplate, addVmTemplateParameters);
        } else {
            log.errorFormat("Schedule VmTemplate error , because VM is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VmRun_Timer_")
    public synchronized void scheduleVmRunTimer(Guid id) {
        log.infoFormat("Schedule VmRun Timer ID {0}.", id);
        if(hasVM(id)) {
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVM(id), AuditLogType.SCHEDULE_VMRUN);
            backend.runInternalAction(VdcActionType.RunVm, new RunVmParams(id));
        } else {
            log.errorFormat("Schedule VmRun error , because VM is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VmSuspend_Timer_")
    public synchronized void scheduleVmSuspendTimer(Guid id) {
        log.infoFormat("Schedule VmSuspend Timer ID {0}.", id);
        if(hasVM(id)) {
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVM(id), AuditLogType.SCHEDULE_VMSUSPEND);
            backend.runInternalAction(VdcActionType.HibernateVm, new VmOperationParameterBase(id));
        } else {
            log.errorFormat("Schedule VmSuspend error , because VM is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VmStop_Timer_")
    public synchronized void scheduleVmStopTimer(Guid id) {
        log.infoFormat("Schedule VmStop Timer ID {0}.", id);
        if(hasVM(id)) {
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVM(id), AuditLogType.SCHEDULE_VMSTOP);
            backend.runInternalAction(VdcActionType.ShutdownVm, new ShutdownVmParameters(id, true));
        } else {
            log.errorFormat("Schedule VmStop error , because VM is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VmReboot_Timer_")
    public synchronized void scheduleVmRebootTimer(Guid id) {
        log.infoFormat("Schedule VmReboot Timer ID {0}.", id);
    }

    @OnTimerMethodAnnotation("_Schedule_VDSRun_Timer_")
    public synchronized void scheduleVDSRunTimer(Guid id) {
        log.infoFormat("Schedule VDSStart Timer ID {0}.", id);
        if(hasVDS(id)) {
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVDS(id), AuditLogType.SCHEDULE_VDSSTART);
            backend.runInternalAction(VdcActionType.StartVds, new FenceVdsActionParameters(id, FenceActionType.Start));
        } else {
            log.errorFormat("Schedule VDSStart error , because VDS is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VDSReboot_Timer_")
    public synchronized void scheduleVDSRebootTimer(Guid id) {
        log.infoFormat("Schedule VDSReboot Timer ID {0}.", id);
        if(hasVDS(id)) {
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVDS(id), AuditLogType.SCHEDULE_VDSREBOOT);
            backend.runInternalAction(VdcActionType.RestartVds, new FenceVdsActionParameters(id, FenceActionType.Restart));
        } else {
            log.errorFormat("Schedule VDSReboot error , because VDS is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VDSStop_Timer_")
    public synchronized void scheduleVDSStopTimer(Guid id) {
        log.infoFormat("Schedule VDSStop Timer ID {0}.", id);
        if(hasVDS(id)) {
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVDS(id), AuditLogType.SCHEDULE_VDSSTOP);
            backend.runInternalAction(VdcActionType.StopVds, new FenceVdsActionParameters(id, FenceActionType.Stop));
        } else {
            log.errorFormat("Schedule VDSStop error , because VDS is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VDSMaintenance_Timer_")
    public synchronized void scheduleVDSMaintenanceTimer(Guid id) {
        log.infoFormat("Schedule VDSMaintenance Timer ID {0}.", id);
        if(hasVDS(id)) {
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVDS(id), AuditLogType.SCHEDULE_VDSMAINTENANCE);
            ArrayList<Guid> vdss = new ArrayList<Guid>();
            vdss.add(id);
            backend.runInternalAction(VdcActionType.MaintenanceNumberOfVdss, new MaintenanceNumberOfVdssParameters(vdss, false));
        } else {
            log.errorFormat("Schedule VDSMaintenance error , because VDS is null.");
        }
    }

    @OnTimerMethodAnnotation("_Schedule_VDSActivate_Timer_")
    public synchronized void scheduleVDSActivateTimer(Guid id) {
        log.infoFormat("Schedule VDSActivate Timer ID {0}.", id);
        if(hasVDS(id)) {
            BackendInternal backend = (BackendInternal) EjbUtils.findBean(BeanType.BACKEND, BeanProxyType.LOCAL);
            AuditLogDirector.log(getAuditLogableBaseByVDS(id), AuditLogType.SCHEDULE_VDSACTIVATE);
            backend.runInternalAction(VdcActionType.ActivateVds, new VdsActionParameters(id));
        } else {
            log.errorFormat("Schedule VDSActivate error , because VDS is null.");
        }
    }

    private AuditLogableBase getAuditLogableBaseByVM(Guid id) {
        AuditLogableBase logable = new AuditLogableBase();
        VM vm = DbFacade.getInstance().getVmDao().get(id);
        if(null != vm) {
            logable.addCustomValue("VmName", vm.getName());
        }
        return logable;
    }

    private AuditLogableBase getAuditLogableBaseByVDS(Guid id) {
        AuditLogableBase logable = new AuditLogableBase();
        VDS vds = DbFacade.getInstance().getVdsDao().get(id);
        if(null != vds) {
            logable.addCustomValue("VdsName", vds.getName());
        }
        return logable;
    }

    private boolean hasVM(Guid id) {
        return null != DbFacade.getInstance().getVmDao().get(id);
    }

    private boolean hasVDS(Guid id) {
        return null != DbFacade.getInstance().getVdsDao().get(id);
    }
}
