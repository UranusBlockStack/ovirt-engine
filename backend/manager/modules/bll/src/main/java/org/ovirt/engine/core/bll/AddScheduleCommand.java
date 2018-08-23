package org.ovirt.engine.core.bll;

import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ovirt.engine.core.common.action.ScheduleParameters;
import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.businessentities.ScheduleType;
import org.ovirt.engine.core.common.errors.VdcBLLException;
import org.ovirt.engine.core.common.errors.VdcBllErrors;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;

public class AddScheduleCommand<T extends ScheduleParameters> extends AdminOperationCommandBase<T> {

    private static final long serialVersionUID = 1L;

    public AddScheduleCommand(T parameters) {
        super(parameters);
    }

    @Override
    protected void executeCommand() {
        Schedule schedule = getParameters().getSchedule();
        boolean success = true;
        try {
            // schedule name
            List<Schedule> schedules = DbFacade.getInstance().getSchedulesDao().getAll();
            for(Schedule _schedule : schedules) {
                if(schedule.getName().equals(_schedule.getName())) {
                    success = false;
                    break;
                }
            }
            // objects
            JSONArray jsonArray = new JSONArray(schedule.getObject());
            if(jsonArray.length() < 1) {
                success = false;
            }
            // time
            JSONObject jsonObject = new JSONObject(schedule.getStrategy());
            ScheduleType type = ScheduleType.valueOf(jsonObject.getString("strategy1"));
            String time = jsonObject.getString("strategy2");
            String patternStr = "^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
            if(type == ScheduleType.Time) {
                patternStr = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|"
                        + "[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-"
                        + "(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|"
                        + "1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])"
                        + "|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d)"
                        + ":[0-5]?\\d:[0-5]?\\d$";
            }
            if(!Pattern.compile(patternStr).matcher(time).matches()) {
                success = false;
            }
            if(success) {
                Guid id = Guid.newGuid();
                schedule.setId(id);
                DbFacade.getInstance().getSchedulesDao().save(schedule);
                // Add Schedules
                InitScheduleManager.addScheduleJob(id);
            }
        } catch (Exception e) {
            success = false;
        }
        setSucceeded(success);
        if(!success) {
            throw new VdcBLLException(VdcBllErrors.SCHEDULE_SAVE_FAILED);
        }
    }

}
