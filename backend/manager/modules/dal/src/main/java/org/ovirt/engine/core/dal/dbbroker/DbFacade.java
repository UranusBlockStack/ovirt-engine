package org.ovirt.engine.core.dal.dbbroker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.ovirt.engine.core.common.ExternalVariable;
import org.ovirt.engine.core.common.VdcObjectType;
import org.ovirt.engine.core.common.businessentities.BusinessEntity;
import org.ovirt.engine.core.common.businessentities.CommandEntity;
import org.ovirt.engine.core.common.businessentities.CpuStatistics;
import org.ovirt.engine.core.common.businessentities.DwhHistoryTimekeeping;
import org.ovirt.engine.core.common.businessentities.EngineSession;
import org.ovirt.engine.core.common.businessentities.FenceAgent;
import org.ovirt.engine.core.common.businessentities.HostDevice;
import org.ovirt.engine.core.common.businessentities.IscsiBond;
import org.ovirt.engine.core.common.businessentities.MacPool;
import org.ovirt.engine.core.common.businessentities.Permission;
import org.ovirt.engine.core.common.businessentities.Provider;
import org.ovirt.engine.core.common.businessentities.Role;
import org.ovirt.engine.core.common.businessentities.Snapshot;
import org.ovirt.engine.core.common.businessentities.StorageDomain;
import org.ovirt.engine.core.common.businessentities.StorageDomainDynamic;
import org.ovirt.engine.core.common.businessentities.StorageDomainStatic;
import org.ovirt.engine.core.common.businessentities.StorageDomainStatus;
import org.ovirt.engine.core.common.businessentities.StorageDomainType;
import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.businessentities.StoragePool;
import org.ovirt.engine.core.common.businessentities.StoragePoolIsoMap;
import org.ovirt.engine.core.common.businessentities.VdsDynamic;
import org.ovirt.engine.core.common.businessentities.VdsKdumpStatus;
import org.ovirt.engine.core.common.businessentities.VdsNumaNode;
import org.ovirt.engine.core.common.businessentities.VdsStatic;
import org.ovirt.engine.core.common.businessentities.VdsStatistics;
import org.ovirt.engine.core.common.businessentities.VmDevice;
import org.ovirt.engine.core.common.businessentities.VmDynamic;
import org.ovirt.engine.core.common.businessentities.VmInit;
import org.ovirt.engine.core.common.businessentities.VmJob;
import org.ovirt.engine.core.common.businessentities.VmNumaNode;
import org.ovirt.engine.core.common.businessentities.VmStatic;
import org.ovirt.engine.core.common.businessentities.VmStatistics;
import org.ovirt.engine.core.common.businessentities.VmTemplate;
import org.ovirt.engine.core.common.businessentities.network.Network;
import org.ovirt.engine.core.common.businessentities.network.VmNetworkInterface;
import org.ovirt.engine.core.common.businessentities.network.VmNetworkStatistics;
import org.ovirt.engine.core.common.businessentities.network.VmNic;
import org.ovirt.engine.core.common.businessentities.network.VnicProfile;
import org.ovirt.engine.core.common.businessentities.network.VnicProfileView;
import org.ovirt.engine.core.common.businessentities.profiles.DiskProfile;
import org.ovirt.engine.core.common.businessentities.storage.BaseDisk;
import org.ovirt.engine.core.common.businessentities.storage.DiskImage;
import org.ovirt.engine.core.common.businessentities.storage.DiskImageDynamic;
import org.ovirt.engine.core.common.businessentities.storage.Image;
import org.ovirt.engine.core.common.businessentities.storage.ImageStorageDomainMap;
import org.ovirt.engine.core.common.businessentities.vds_spm_id_map;
import org.ovirt.engine.core.common.job.Job;
import org.ovirt.engine.core.common.job.Step;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dao.ActionGroupDAO;
import org.ovirt.engine.core.dao.AsyncTaskDAO;
import org.ovirt.engine.core.dao.AuditLogDAO;
import org.ovirt.engine.core.dao.BaseDiskDao;
import org.ovirt.engine.core.dao.BookmarkDAO;
import org.ovirt.engine.core.dao.BusinessEntitySnapshotDAO;
import org.ovirt.engine.core.dao.CommandEntityDao;
import org.ovirt.engine.core.dao.DAO;
import org.ovirt.engine.core.dao.DbGroupDAO;
import org.ovirt.engine.core.dao.DbUserDAO;
import org.ovirt.engine.core.dao.DiskDao;
import org.ovirt.engine.core.dao.DiskImageDAO;
import org.ovirt.engine.core.dao.DiskImageDynamicDAO;
import org.ovirt.engine.core.dao.DiskLunMapDao;
import org.ovirt.engine.core.dao.EngineSessionDAO;
import org.ovirt.engine.core.dao.EventDAO;
import org.ovirt.engine.core.dao.ExternalVariableDao;
import org.ovirt.engine.core.dao.FenceAgentDAO;
import org.ovirt.engine.core.dao.GenericDao;
import org.ovirt.engine.core.dao.HostDeviceDao;
import org.ovirt.engine.core.dao.ImageDao;
import org.ovirt.engine.core.dao.ImageStorageDomainMapDao;
import org.ovirt.engine.core.dao.IscsiBondDao;
import org.ovirt.engine.core.dao.JobDao;
import org.ovirt.engine.core.dao.JobSubjectEntityDao;
import org.ovirt.engine.core.dao.LunDAO;
import org.ovirt.engine.core.dao.MacPoolDao;
import org.ovirt.engine.core.dao.PermissionDAO;
import org.ovirt.engine.core.dao.QuotaDAO;
import org.ovirt.engine.core.dao.RepoFileMetaDataDAO;
import org.ovirt.engine.core.dao.RoleDAO;
import org.ovirt.engine.core.dao.RoleGroupMapDAO;
import org.ovirt.engine.core.dao.SnapshotDao;
import org.ovirt.engine.core.dao.StepDao;
import org.ovirt.engine.core.dao.StorageDomainDAO;
import org.ovirt.engine.core.dao.StorageDomainDynamicDAO;
import org.ovirt.engine.core.dao.StorageDomainOvfInfoDao;
import org.ovirt.engine.core.dao.StorageDomainStaticDAO;
import org.ovirt.engine.core.dao.StoragePoolDAO;
import org.ovirt.engine.core.dao.StoragePoolIsoMapDAO;
import org.ovirt.engine.core.dao.StorageServerConnectionDAO;
import org.ovirt.engine.core.dao.StorageServerConnectionLunMapDAO;
import org.ovirt.engine.core.dao.TagDAO;
import org.ovirt.engine.core.dao.UnregisteredOVFDataDAO;
import org.ovirt.engine.core.dao.VdcOptionDAO;
import org.ovirt.engine.core.dao.VdsCpuStatisticsDAO;
import org.ovirt.engine.core.dao.VdsDAO;
import org.ovirt.engine.core.dao.VdsDynamicDAO;
import org.ovirt.engine.core.dao.VdsGroupDAO;
import org.ovirt.engine.core.dao.VdsKdumpStatusDao;
import org.ovirt.engine.core.dao.VdsNumaNodeDAO;
import org.ovirt.engine.core.dao.VdsSpmIdMapDAO;
import org.ovirt.engine.core.dao.VdsStaticDAO;
import org.ovirt.engine.core.dao.VdsStatisticsDAO;
import org.ovirt.engine.core.dao.VmAndTemplatesGenerationsDAO;
import org.ovirt.engine.core.dao.VmDAO;
import org.ovirt.engine.core.dao.VmDeviceDAO;
import org.ovirt.engine.core.dao.VmDynamicDAO;
import org.ovirt.engine.core.dao.VmGuestAgentInterfaceDao;
import org.ovirt.engine.core.dao.VmInitDAO;
import org.ovirt.engine.core.dao.VmJobDao;
import org.ovirt.engine.core.dao.VmNumaNodeDAO;
import org.ovirt.engine.core.dao.VmPoolDAO;
import org.ovirt.engine.core.dao.VmStaticDAO;
import org.ovirt.engine.core.dao.VmStatisticsDAO;
import org.ovirt.engine.core.dao.VmTemplateDAO;
import org.ovirt.engine.core.dao.dwh.DwhHistoryTimekeepingDao;
import org.ovirt.engine.core.dao.gluster.GlusterBrickDao;
import org.ovirt.engine.core.dao.gluster.GlusterClusterServiceDao;
import org.ovirt.engine.core.dao.gluster.GlusterGeoRepDao;
import org.ovirt.engine.core.dao.gluster.GlusterHooksDao;
import org.ovirt.engine.core.dao.gluster.GlusterOptionDao;
import org.ovirt.engine.core.dao.gluster.GlusterServerDao;
import org.ovirt.engine.core.dao.gluster.GlusterServerServiceDao;
import org.ovirt.engine.core.dao.gluster.GlusterServiceDao;
import org.ovirt.engine.core.dao.gluster.GlusterVolumeDao;
import org.ovirt.engine.core.dao.gluster.GlusterVolumeSnapshotConfigDao;
import org.ovirt.engine.core.dao.gluster.GlusterVolumeSnapshotDao;
import org.ovirt.engine.core.dao.gluster.GlusterVolumeSnapshotScheduleDao;
import org.ovirt.engine.core.dao.gluster.StorageDeviceDao;
import org.ovirt.engine.core.dao.network.HostNetworkQosDao;
import org.ovirt.engine.core.dao.network.HostNicVfsConfigDao;
import org.ovirt.engine.core.dao.network.InterfaceDao;
import org.ovirt.engine.core.dao.network.NetworkClusterDao;
import org.ovirt.engine.core.dao.network.NetworkDao;
import org.ovirt.engine.core.dao.network.NetworkQoSDao;
import org.ovirt.engine.core.dao.network.NetworkViewDao;
import org.ovirt.engine.core.dao.network.VmNetworkInterfaceDao;
import org.ovirt.engine.core.dao.network.VmNetworkStatisticsDao;
import org.ovirt.engine.core.dao.network.VmNicDao;
import org.ovirt.engine.core.dao.network.VnicProfileDao;
import org.ovirt.engine.core.dao.network.VnicProfileViewDao;
import org.ovirt.engine.core.dao.profiles.CpuProfileDao;
import org.ovirt.engine.core.dao.profiles.DiskProfileDao;
import org.ovirt.engine.core.dao.provider.ProviderDao;
import org.ovirt.engine.core.dao.qos.CpuQosDao;
import org.ovirt.engine.core.dao.qos.QosBaseDao;
import org.ovirt.engine.core.dao.qos.StorageQosDao;
import org.ovirt.engine.core.dao.scheduling.AffinityGroupDao;
import org.ovirt.engine.core.dao.scheduling.ClusterPolicyDao;
import org.ovirt.engine.core.dao.scheduling.PolicyUnitDao;
import org.ovirt.engine.core.utils.linq.LinqUtils;
import org.ovirt.engine.core.utils.linq.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

@Singleton
public class DbFacade {

    private final static Logger log = LoggerFactory.getLogger(DbFacade.class);

    @SuppressWarnings("serial")
    private final static Map<Class<?>, Class<?>> mapEntityToDao = new HashMap<Class<?>, Class<?>>()
    {
        {
            put(StoragePool.class, StoragePoolDAO.class);
            put(StoragePoolIsoMap.class, StoragePoolIsoMapDAO.class);
            put(StorageDomainStatic.class, StorageDomainStaticDAO.class);
            put(StorageDomainDynamic.class, StorageDomainDynamicDAO.class);
            put(VdsStatic.class, VdsStaticDAO.class);
            put(VdsDynamic.class, VdsDynamicDAO.class);
            put(VdsStatistics.class, VdsStatisticsDAO.class);
            put(vds_spm_id_map.class, VdsSpmIdMapDAO.class);
            put(Role.class, RoleDAO.class);
            put(VmTemplate.class, VmTemplateDAO.class);
            put(VmDynamic.class, VmDynamicDAO.class);
            put(VmStatic.class, VmStaticDAO.class);
            put(VmStatistics.class, VmStatisticsDAO.class);
            put(BaseDisk.class, BaseDiskDao.class);
            put(Schedule.class, SchedulesDao.class);
            put(DiskImage.class, BaseDiskDao.class);
            put(DiskImageDynamic.class, DiskImageDynamicDAO.class);
            put(VmNic.class, VmNicDao.class);
            put(VmNetworkInterface.class, VmNicDao.class);
            put(VmNetworkStatistics.class, VmNetworkStatisticsDao.class);
            put(Network.class, NetworkDao.class);
            put(Provider.class, ProviderDao.class);
            put(Snapshot.class, SnapshotDao.class);
            put(VmDevice.class, VmDeviceDAO.class);
            put(ImageStorageDomainMap.class, ImageStorageDomainMapDao.class);
            put(Permission.class, PermissionDAO.class);
            put(Image.class, ImageDao.class);
            put(Job.class, JobDao.class);
            put(Step.class, StepDao.class);
            put(VnicProfile.class, VnicProfileDao.class);
            put(VnicProfileView.class, VnicProfileDao.class);
            put(DwhHistoryTimekeeping.class, DwhHistoryTimekeepingDao.class);
            put(IscsiBond.class, IscsiBondDao.class);
            put(VmInit.class, VmInitDAO.class);
            put(CpuStatistics.class, VdsCpuStatisticsDAO.class);
            put(VdsNumaNode.class, VdsNumaNodeDAO.class);
            put(VmNumaNode.class, VmNumaNodeDAO.class);
            put(CommandEntity.class, CommandEntityDao.class);
            put(ExternalVariable.class, ExternalVariableDao.class);
            put(VdsKdumpStatus.class, VdsKdumpStatusDao.class);
            put(VmJob.class, VmJobDao.class);
            put(MacPool.class, MacPoolDao.class);
            put(DiskProfile.class, DiskProfileDao.class);
            put(FenceAgent.class, FenceAgentDAO.class);
            put(EngineSession.class, EngineSessionDAO.class);
            put(HostDevice.class, HostDeviceDao.class);
        }
    };

    private static DbFacade instance;

    private final DbFacadeLocator dbFacadeLocator;
    private final JdbcTemplate jdbcTemplate;
    private final DbEngineDialect dbEngineDialect;
    private final SimpleJdbcCallsHandler callsHandler;

    @Inject
    private Instance<DAO> daos;


    private int onStartConnectionTimeout;

    private int connectionCheckInterval;

    @Inject
    public DbFacade(JdbcTemplate jdbcTemplate,
            DbEngineDialect dbEngineDialect,
            SimpleJdbcCallsHandler callsHandler,
            DbFacadeLocator dbFacadeLocator) {

        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate cannot be null");
        Objects.requireNonNull(dbEngineDialect, "dbEngineDialect cannot be null");
        Objects.requireNonNull(callsHandler, "callsHandler cannot be null");
        Objects.requireNonNull(dbFacadeLocator, "dbFacadeLocator cannot be null");

        this.dbFacadeLocator = dbFacadeLocator;
        this.jdbcTemplate = jdbcTemplate;
        this.dbEngineDialect = dbEngineDialect;
        this.callsHandler = callsHandler;

        init();
    }

    private void init() {
        log.info("Initializing the DbFacade");
        dbFacadeLocator.configure(this);

        instance = this;
    }

    public DbEngineDialect getDbEngineDialect() {
        return dbEngineDialect;
    }

    public SimpleJdbcCallsHandler getCallsHandler() {
        return callsHandler;
    }

    /**
     * Return the correct DAO for the given {@link BusinessEntity} class.
     *
     * @param <T>
     *            The Type of DAO which is returned.
     * @param entityClass
     *            The class of the entity.
     * @return The DAO for the entity.
     */
    public <T extends GenericDao<?, ?>> T getDaoForEntity(Class<? extends BusinessEntity<?>> entityClass) {
        @SuppressWarnings("unchecked")
        Class<T> daoType = (Class<T>) mapEntityToDao.get(entityClass);
        return getDao(daoType);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    private <T extends DAO> T getDao(Class<T> daoType) {
        for (DAO dao : daos) {
            if (daoType.isAssignableFrom(dao.getClass())) {
                return (T) dao;
            }
        }
        log.error("Can't find dao for " + daoType);
        return null;
    }

    /**
     * just convenience so we don't refactor old code
     */
    public static DbFacade getInstance() {
        return instance;
    }

    private CustomMapSqlParameterSource getCustomMapSqlParameterSource() {
        return new CustomMapSqlParameterSource(dbEngineDialect);
    }

    public String getEntityNameByIdAndType(Guid objectId, VdcObjectType vdcObjectType) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource().addValue("entity_id", objectId)
                .addValue("object_type", vdcObjectType.getValue());

        Map<String, Object> dbResults =
                new SimpleJdbcCall(jdbcTemplate).withFunctionName("fn_get_entity_name").execute(
                        parameterSource);

        String resultKey = dbEngineDialect.getFunctionReturnKey();
        return dbResults.get(resultKey) != null ? dbResults.get(resultKey).toString() : null;
    }

    public boolean isStoragePoolMasterUp(Guid storagePoolId) {
        List<StorageDomain> domains = getStorageDomainDao().getAllForStoragePool(storagePoolId);
        StorageDomain master = LinqUtils.firstOrNull(domains, new Predicate<StorageDomain>() {
            @Override
            public boolean eval(StorageDomain storage_domains) {
                return storage_domains.getStorageDomainType() == StorageDomainType.Master;
            }
        });
        return master != null
                && (master.getStatus() == StorageDomainStatus.Active || master.getStatus() == StorageDomainStatus.Unknown);
    }

    public Integer getSystemStatisticsValue(String entity) {
        return getSystemStatisticsValue(entity, "");
    }

    public Integer getSystemStatisticsValue(String entity, String status) {
        MapSqlParameterSource parameterSource = getCustomMapSqlParameterSource().addValue("entity", entity).addValue(
                "status", status);

        RowMapper<Integer> mapper = new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("val");
            }
        };

        Map<String, Object> dbResults =
                dbEngineDialect.createJdbcCallForQuery(jdbcTemplate).withProcedureName("Getsystem_statistics")
                        .returningResultSet("RETURN_VALUE", mapper).execute(parameterSource);

        return (Integer) DbFacadeUtils.asSingleResult((List<?>) (dbResults.get("RETURN_VALUE")));
    }

    /**
     * User presentation in GUI have a distinction between ADMIN/USER user. The distinction is determined by their
     * permissions or their group's permissions. when Permission with the role type Admin is found, set the DbUser
     * isAdmin flag to ADMIN Type or to USER otherwise. Make the change only if the value is different to what it is
     * saved to db
     *
     * @param userIds
     */
    public void updateLastAdminCheckStatus(Guid... userIds) {
        MapSqlParameterSource parameterSource =
                getCustomMapSqlParameterSource().addValue("userIds", StringUtils.join(userIds, ","));

        new SimpleJdbcCall(jdbcTemplate).withProcedureName("UpdateLastAdminCheckStatus")
                .execute(parameterSource);
    }

    /***
     * CheckDBConnection calls a simple "select 1" SP to verify DB is up & running.
     *
     * @return True if DB is up & running.
     */
    public boolean checkDBConnection() {
        return (new SimpleJdbcCall(jdbcTemplate).withProcedureName("CheckDBConnection").execute() != null);
    }

    /**
     * Returns a singleton instance of {@link BookmarkDAO}.
     *
     * @return the dao
     */
    public BookmarkDAO getBookmarkDao() {
        return getDao(BookmarkDAO.class);

    }

    /**
     * Retrieves the singleton instance of {@link CommandEntityDao}.
     *
     * @return the dao
     */
    public CommandEntityDao getCommandEntityDao() {
        return getDao(CommandEntityDao.class);
    }

    /**
     * Returns the singleton instance of {@link DbUserDAO}.
     *
     * @return the dao
     */
    public DbUserDAO getDbUserDao() {
        return getDao(DbUserDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VdsDAO}.
     *
     * @return the dao
     */
    public VdsDAO getVdsDao() {
        return getDao(VdsDAO.class);
    }

    /**
     * Returns the singleton instance of {@link UnregisteredOVFDataDAO}.
     *
     * @return the dao
     */
    public UnregisteredOVFDataDAO getUnregisteredOVFDataDao() {
        return getDao(UnregisteredOVFDataDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VmAndTemplatesGenerationsDAO}.
     *
     * @return the dao
     */
    public VmAndTemplatesGenerationsDAO getVmAndTemplatesGenerationsDao() {
        return getDao(VmAndTemplatesGenerationsDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VdsStaticDAO}.
     *
     * @return the dao
     */
    public VdsStaticDAO getVdsStaticDao() {
        return getDao(VdsStaticDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VdsDynamicDAO}.
     *
     * @return the dao
     */
    public VdsDynamicDAO getVdsDynamicDao() {
        return getDao(VdsDynamicDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VdsStatisticsDAO}.
     *
     * @return the dao
     */
    public VdsStatisticsDAO getVdsStatisticsDao() {
        return getDao(VdsStatisticsDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VdsSpmIdMapDAO}.
     *
     * @return the dao
     */
    public VdsSpmIdMapDAO getVdsSpmIdMapDao() {
        return getDao(VdsSpmIdMapDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VdsGroupDAO}.
     *
     * @return the dao
     */
    public VdsGroupDAO getVdsGroupDao() {
        return getDao(VdsGroupDAO.class);
    }

    /**
     * Returns the single instance of {@link AuditLogDAO}.
     *
     * @return the dao
     */
    public AuditLogDAO getAuditLogDao() {
        return getDao(AuditLogDAO.class);
    }

    /**
     * Retrieves the singleton instance of {@link LunDAO}.
     *
     * @return the dao
     */
    public LunDAO getLunDao() {
        return getDao(LunDAO.class);
    }

    /**
     * Returns the singleton instance of {@link InterfaceDao}.
     *
     * @return the dao
     */
    public InterfaceDao getInterfaceDao() {
        return getDao(InterfaceDao.class);
    }

    /**
     * Returns the singleton instance of {@link VmNetworkInterfaceDao}.
     *
     * @return the dao
     */
    public VmNetworkInterfaceDao getVmNetworkInterfaceDao() {
        return getDao(VmNetworkInterfaceDao.class);
    }

    /**
     * Returns the singleton instance of {@link VmNetworkInterfaceDao}.
     *
     * @return the dao
     */
    public VmNetworkStatisticsDao getVmNetworkStatisticsDao() {
        return getDao(VmNetworkStatisticsDao.class);
    }

    /**
     * Returns the singleton instance of {@link RoleGroupMapDAO}.
     *
     * @return the dao
     */
    public RoleGroupMapDAO getRoleGroupMapDao() {
        return getDao(RoleGroupMapDAO.class);
    }

    /**
     * * Returns the singleton instance of {@link VmTemplateDAO}.
     *
     * @return the dao
     */
    public VmTemplateDAO getVmTemplateDao() {
        return getDao(VmTemplateDAO.class);
    }

    /**
     * * Returns the singleton instance of {@link VmDAO}.
     *
     * @return the dao
     */
    public VmDAO getVmDao() {
        return getDao(VmDAO.class);
    }

    /**
     * * Returns the singleton instance of {@link VmDynamicDAO}.
     *
     * @return the dao
     */
    public VmDynamicDAO getVmDynamicDao() {
        return getDao(VmDynamicDAO.class);
    }

    /**
     * Returns the singleton instance of {@link TagDAO}.
     *
     * @return the dao
     */
    public TagDAO getTagDao() {
        return getDao(TagDAO.class);
    }

    /**
     * Returns the singleton instance of {@link BaseDiskDao}.
     *
     * @return the dao
     */
    public BaseDiskDao getBaseDiskDao() {
        return getDao(BaseDiskDao.class);
    }

    /**
     * Returns the singleton instance of {@link DiskDao}.
     *
     * @return the dao
     */
    public DiskDao getDiskDao() {
        return getDao(DiskDao.class);
    }

    /**
     * Returns the singleton instance of {@link DiskLunMapDao}.
     *
     * @return the dao
     */
    public DiskLunMapDao getDiskLunMapDao() {
        return getDao(DiskLunMapDao.class);
    }

    /**
     * Returns the singleton instance of {@link ImageDao}.
     *
     * @return the dao
     */
    public ImageDao getImageDao() {
        return getDao(ImageDao.class);
    }

    /**
     * Returns the singleton instance of {@link DiskImageDAO}.
     *
     * @return the dao
     */
    public DiskImageDAO getDiskImageDao() {
        return getDao(DiskImageDAO.class);
    }

    /**
     * Returns the singleton instance of {@link DiskImageDynamicDAO}.
     *
     * @return the dao
     */
    public DiskImageDynamicDAO getDiskImageDynamicDao() {
        return getDao(DiskImageDynamicDAO.class);
    }

    /**
     * Returns the singleton instance of {@link EventDAO}.
     *
     * @return the dao
     */
    public EventDAO getEventDao() {
        return getDao(EventDAO.class);
    }

    /**
     * Returns the singleton instance of {@link ActionGroupDAO}.
     *
     * @return the dao
     */
    public ActionGroupDAO getActionGroupDao() {
        return getDao(ActionGroupDAO.class);
    }

    /**
     * Retrieves the singleton instance of {@link RoleDAO}.
     *
     * @return the dao
     */
    public RoleDAO getRoleDao() {
        return getDao(RoleDAO.class);
    }

    /**
     * Returns the singleton instance of {@link AsyncTaskDAO}.
     *
     * @return the dao
     */
    public AsyncTaskDAO getAsyncTaskDao() {
        return getDao(AsyncTaskDAO.class);
    }

    /**
     * Retrieves the singleton instance of {@link DbGroupDAO}.
     *
     * @return the dao
     */
    public DbGroupDAO getDbGroupDao() {
        return getDao(DbGroupDAO.class);
    }

    /**
     * Returns the singleton instance of {@link EngineSessionDAO}.
     *
     * @return the dao
     */
    public EngineSessionDAO getEngineSessionDao() {
        return getDao(EngineSessionDAO.class);
    }

    /**
     * Returns the singleton instance of {@link ProviderDao}.
     *
     * @return the dao
     */
    public ProviderDao getProviderDao() {
        return getDao(ProviderDao.class);
    }

    /**
     * Returns the singleton instance of {@link NetworkDao}.
     *
     * @return the dao
     */
    public NetworkDao getNetworkDao() {
        return getDao(NetworkDao.class);
    }

    /**
     * Returns the singleton instance of {@link NetworkViewDao}.
     *
     * @return the dao
     */
    public NetworkViewDao getNetworkViewDao() {
        return getDao(NetworkViewDao.class);
    }

    /**
     * Returns the singleton instance of {@link NetworkClusterDao}.
     *
     * @return the dao
     */
    public NetworkClusterDao getNetworkClusterDao() {
        return getDao(NetworkClusterDao.class);
    }

    /**
     * Returns the singleton instance of {@link PermissionDAO}.
     *
     * @return the dao
     */
    public PermissionDAO getPermissionDao() {
        return getDao(PermissionDAO.class);
    }

    /**
     * Returns the singleton instance of {@link StorageDomainDAO}.
     *
     * @return the dao
     */
    public StorageDomainDAO getStorageDomainDao() {
        return getDao(StorageDomainDAO.class);
    }

    /**
     * Returns the singleton instance of {@link StorageDomainOvfInfoDao}.
     *
     * @return the dao
     */
    public StorageDomainOvfInfoDao getStorageDomainOvfInfoDao() {
        return getDao(StorageDomainOvfInfoDao.class);
    }

    /**
     * Returns the singleton instance of {@link StorageDomainDAO}.
     *
     * @return the dao
     */
    public StorageDomainStaticDAO getStorageDomainStaticDao() {
        return getDao(StorageDomainStaticDAO.class);
    }

    /**
     * Returns the singleton instance of {@link StorageDomainDAO}.
     *
     * @return the dao
     */
    public StorageDomainDynamicDAO getStorageDomainDynamicDao() {
        return getDao(StorageDomainDynamicDAO.class);
    }

    /**
     * Returns the singleton instance of {@link RepoFileMetaDataDAO}.
     *
     * @return Repository file meta data dao.
     */
    public RepoFileMetaDataDAO getRepoFileMetaDataDao() {
        return getDao(RepoFileMetaDataDAO.class);
    }

    /**
     * Retrieves the singleton instance of {@link SnapshotDao}.
     *
     * @return the dao
     */
    public SnapshotDao getSnapshotDao() {
        return getDao(SnapshotDao.class);
    }

    /**
     * Retrieves the singleton instance of {@link ImageStorageDomainMapDao}.
     *
     * @return the dao
     */
    public ImageStorageDomainMapDao getImageStorageDomainMapDao() {
        return getDao(ImageStorageDomainMapDao.class);
    }

    /**
     * Retrieves the singleton instance of {@link StoragePoolDAO}.
     *
     * @return the dao
     */
    public StoragePoolDAO getStoragePoolDao() {
        return getDao(StoragePoolDAO.class);
    }

    /**
     * Retrieves the singleton instance of {@link MacPoolDao}.
     *
     * @return the dao
     */
    public MacPoolDao getMacPoolDao() {
        return getDao(MacPoolDao.class);
    }

    /**
     * Retrieves the singleton instance of {@link StoragePoolIsoMapDAO}.
     *
     * @return the dao
     */
    public StoragePoolIsoMapDAO getStoragePoolIsoMapDao() {
        return getDao(StoragePoolIsoMapDAO.class);
    }

    /**
     * Retrieves the singleton instance of {@link StorageServerConnectionDAO}.
     *
     * @return the dao
     */
    public StorageServerConnectionDAO getStorageServerConnectionDao() {
        return getDao(StorageServerConnectionDAO.class);
    }

    /**
     * Retrieves the singleton instance of {@link StorageServerConnectionLunMapDAO}.
     *
     * @return the dao
     */
    public StorageServerConnectionLunMapDAO getStorageServerConnectionLunMapDao() {
        return getDao(StorageServerConnectionLunMapDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VdcOptionDAO}.
     *
     * @return the dao
     */
    public VdcOptionDAO getVdcOptionDao() {
        return getDao(VdcOptionDAO.class);
    }

    /**
     * Returns the singleton instance of {@link BusinessEntitySnapshotDAO}.
     *
     * @return the dao
     */
    public BusinessEntitySnapshotDAO getBusinessEntitySnapshotDao() {
        return getDao(BusinessEntitySnapshotDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VmPoolDAO}.
     *
     * @return the dao
     */
    public VmPoolDAO getVmPoolDao() {
        return getDao(VmPoolDAO.class);
    }

    public VmStaticDAO getVmStaticDao() {
        return getDao(VmStaticDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VmStatisticsDAO}.
     *
     * @return the dao
     */
    public VmStatisticsDAO getVmStatisticsDao() {
        return getDao(VmStatisticsDAO.class);
    }

    /**
     * Returns the singleton instance of {@link QuotaDAO}.
     *
     * @return the dao
     */
    public QuotaDAO getQuotaDao() {
        return getDao(QuotaDAO.class);
    }

    public VmDeviceDAO getVmDeviceDao() {
        return getDao(VmDeviceDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VmJobDao}.
     *
     * @return the dao
     */
    public VmJobDao getVmJobDao() {
        return getDao(VmJobDao.class);
    }

    /**
     *
     * Returns the singleton instance of {@link JobDao}.
     *
     * @return the dao
     */
    public JobDao getJobDao() {
        return getDao(JobDao.class);
    }

    /**
     * Returns the singleton instance of {@link JobSubjectEntityDao}.
     *
     * @return the dao
     */
    public JobSubjectEntityDao getJobSubjectEntityDao() {
        return getDao(JobSubjectEntityDao.class);
    }

    /**
     * Returns the singleton instance of {@link StepDao}.
     *
     * @return the dao
     */
    public StepDao getStepDao() {
        return getDao(StepDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterVolumeSnapshotDao}
     *
     * @return the dao
     */
    public GlusterVolumeSnapshotDao getGlusterVolumeSnapshotDao() {
        return getDao(GlusterVolumeSnapshotDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterVolumeSnapshotConfigDao}
     *
     * @return the dao
     */
    public GlusterVolumeSnapshotConfigDao getGlusterVolumeSnapshotConfigDao() {
        return getDao(GlusterVolumeSnapshotConfigDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterVolumeSnapshotScheduleDao}
     *
     * @return the dao
     */
    public GlusterVolumeSnapshotScheduleDao getGlusterVolumeSnapshotScheduleDao() {
        return getDao(GlusterVolumeSnapshotScheduleDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterVolumeDao}.
     *
     * @return the dao
     */
    public GlusterVolumeDao getGlusterVolumeDao() {
        return getDao(GlusterVolumeDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterBrickDao}.
     *
     * @return the dao
     */
    public GlusterBrickDao getGlusterBrickDao() {
        return getDao(GlusterBrickDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterOptionDao}.
     *
     * @return the dao
     */
    public GlusterOptionDao getGlusterOptionDao() {
        return getDao(GlusterOptionDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterServiceDao}.
     *
     * @return the dao
     */
    public GlusterServiceDao getGlusterServiceDao() {
        return getDao(GlusterServiceDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterServerServiceDao}.
     *
     * @return the dao
     */
    public GlusterServerServiceDao getGlusterServerServiceDao() {
        return getDao(GlusterServerServiceDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterClusterServiceDao}.
     *
     * @return the dao
     */
    public GlusterClusterServiceDao getGlusterClusterServiceDao() {
        return getDao(GlusterClusterServiceDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterHooksDao}.
     *
     * @return the dao
     */
    public GlusterHooksDao getGlusterHooksDao() {
        return getDao(GlusterHooksDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterGeoRepDao}.
     *
     * @return the dao
     */
    public GlusterGeoRepDao getGlusterGeoRepDao() {
        return getDao(GlusterGeoRepDao.class);
    }

    /**
     * Returns the singleton instance of {@link StorageDeviceDao}.
     *
     * @return the dao
     */
    public StorageDeviceDao getStorageDeviceDao() {
        return getDao(StorageDeviceDao.class);
    }

    /**
     * Returns the singleton instance of {@link GlusterServerDao}.
     *
     * @return the dao
     */
    public GlusterServerDao getGlusterServerDao() {
        return getDao(GlusterServerDao.class);
    }

    public void setOnStartConnectionTimeout(int onStartConnectionTimeout) {
        this.onStartConnectionTimeout = onStartConnectionTimeout;
    }

    public int getOnStartConnectionTimeout() {
        return onStartConnectionTimeout;
    }

    public void setConnectionCheckInterval(int connectionCheckInterval) {
        this.connectionCheckInterval = connectionCheckInterval;
    }

    public int getConnectionCheckInterval() {
        return connectionCheckInterval;
    }

    /**
     * Returns the singleton instance of {@link VmGuestAgentInterfaceDao}.
     *
     * @return the dao
     */
    public VmGuestAgentInterfaceDao getVmGuestAgentInterfaceDao() {
        return getDao(VmGuestAgentInterfaceDao.class);
    }

    public NetworkQoSDao getNetworkQosDao() {
        return getDao(NetworkQoSDao.class);
    }

    public HostNetworkQosDao getHostNetworkQosDao() {
        return getDao(HostNetworkQosDao.class);
    }

    public StorageQosDao getStorageQosDao() {
        return getDao(StorageQosDao.class);
    }

    public CpuQosDao getCpuQosDao() {
        return getDao(CpuQosDao.class);
    }

    public QosBaseDao getQosBaseDao() {
        return getDao(QosBaseDao.class);
    }
    /**
     * Returns the singleton instance of {@link PolicyUnitDao}.
     *
     * @return the dao
     */
    public PolicyUnitDao getPolicyUnitDao() {
        return getDao(PolicyUnitDao.class);
    }

    /**
     * Returns the singleton instance of {@link ClusterPolicyDao}.
     *
     * @return the dao
     */
    public ClusterPolicyDao getClusterPolicyDao() {
        return getDao(ClusterPolicyDao.class);
    }

    /**
     * Returns the singleton instance of {@link VnicProfileDao}.
     *
     * @return the dao
     */
    public VnicProfileDao getVnicProfileDao() {
        return getDao(VnicProfileDao.class);
    }

    /**
     * Returns the singleton instance of {@link VnicProfileViewDao}.
     *
     * @return the dao
     */
    public VnicProfileViewDao getVnicProfileViewDao() {
        return getDao(VnicProfileViewDao.class);
    }

    /**
     * Returns the singleton instance of {@link VmNicDao}.
     *
     * @return the dao
     */
    public VmNicDao getVmNicDao() {
        return getDao(VmNicDao.class);
    }

    public VmInitDAO getVmInitDao() {
        return getDao(VmInitDAO.class);
    }

    /**
     * Returns the singleton instance of {@link DwhHistoryTimekeepingDao}.
     *
     * @return the dao instance
     */
    public DwhHistoryTimekeepingDao getDwhHistoryTimekeepingDao() {
        return getDao(DwhHistoryTimekeepingDao.class);
    }

    /**
     * Returns the singleton instance of {@link AffinityGroupDao}.
     *
     * @return the dao instance
     */
    public AffinityGroupDao getAffinityGroupDao() {
        return getDao(AffinityGroupDao.class);
    }

    /**
     * Returns the singleton instance of {@link ExternalVariableDao}.
     *
     * @return the dao instance
     */
    public ExternalVariableDao getExternalVariableDao() {
        return getDao(ExternalVariableDao.class);
    }

    /**
     * Returns the singleton instance of {@link VdsKdumpStatusDao}.
     *
     * @return the dao instance
     */
    public VdsKdumpStatusDao getVdsKdumpStatusDao() {
        return getDao(VdsKdumpStatusDao.class);
    }

    /**
     * This call will populate a translation table of OS Ids to they're name
     * The translation table shall be in use by DWH
     *
     * @param osIdToName
     *            OS id to OS Name map
     */
    public void populateDwhOsInfo(Map<Integer, String> osIdToName) {
        // first clear the table
        new SimpleJdbcCall(jdbcTemplate).withProcedureName("clear_osinfo").execute();
        // batch populate
        List<MapSqlParameterSource> executions = new ArrayList<>();
        for (Map.Entry<Integer, String> e : osIdToName.entrySet()) {
            executions.add(getCustomMapSqlParameterSource()
                    .addValue("os_id", e.getKey())
                    .addValue("os_name", e.getValue()));
        }
        getCallsHandler().executeStoredProcAsBatch("insert_osinfo", executions);
    }

    public IscsiBondDao getIscsiBondDao() {
        return getDao(IscsiBondDao.class);
    }

    /**
     * Returns the singleton instance of {@link VdsCpuStatisticsDAO}.
     *
     * @return the dao instance
     */
    public VdsCpuStatisticsDAO getVdsCpuStatisticsDAO() {
        return getDao(VdsCpuStatisticsDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VdsNumaNodeDAO}.
     *
     * @return the dao instance
     */
    public VdsNumaNodeDAO getVdsNumaNodeDAO() {
        return getDao(VdsNumaNodeDAO.class);
    }

    /**
     * Returns the singleton instance of {@link VmNumaNodeDAO}.
     *
     * @return the dao instance
     */
    public VmNumaNodeDAO getVmNumaNodeDAO() {
        return getDao(VmNumaNodeDAO.class);
    }

    /**
     * Returns the singleton instance of {@link DiskProfileDao}.
     *
     * @return the dao instance
     */
    public DiskProfileDao getDiskProfileDao() {
        return getDao(DiskProfileDao.class);
    }

    /**
     * Returns the singleton instance of {@link SchedulesDao}.
     * @return the dao
     */
    public SchedulesDao getSchedulesDao() {
        return getDao(SchedulesDao.class);
    }

    /**
     * Returns the singleton instance of {@link CpuProfileDao}.
     *
     * @return the dao instance
     */
    public CpuProfileDao getCpuProfileDao() {
        return getDao(CpuProfileDao.class);
    }

    public FenceAgentDAO getFenceAgentDao() {
        return getDao(FenceAgentDAO.class);
    }

    /**
     * Returns the singleton instance of {@link HostDeviceDao}.
     *
     * @return the dao instance
     */
    public HostDeviceDao getHostDeviceDao() {
        return getDao(HostDeviceDao.class);
    }

    /**
     * Returns the singleton instance of {@link org.ovirt.engine.core.common.businessentities.network.HostNicVfsConfig}.
     *
     * @return the dao
     */
    public HostNicVfsConfigDao getHostNicVfsConfigDao() {
        return getDao(HostNicVfsConfigDao.class);
    }
}
