package org.ovirt.engine.core.bll;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.businessentities.ScheduleType;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.queries.GetScheduleParameters;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;

public class GetSchedulesQuery<P extends GetScheduleParameters> extends QueriesCommandBase<P> {

    public GetSchedulesQuery(P parameters) {
        super(parameters);
    }

    @Override
    protected void executeQueryCommand() {
        try {
            List<Schedule> schedules = DbFacade.getInstance().getSchedulesDao().getAll();
            for (int i = 0; i < schedules.size(); i++) {
                Schedule schedule = schedules.get(i);
                ScheduleType searchType = ScheduleType.getSearchType(schedule.getType());
                JSONArray jsonArray = new JSONArray(schedule.getObject());
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    Guid id = Guid.createGuidFromString(jsonObject.getString("id"));
                    String name = "";
                    if(searchType == ScheduleType.SearchVM) {
                        VM vm = DbFacade.getInstance().getVmDao().get(id);
                        if(null != vm) {
                            name = vm.getName();
                        }
                    } else if(searchType == ScheduleType.SearchVDS) {
                        VDS vds = DbFacade.getInstance().getVdsDao().get(id);
                        if(null != vds) {
                            name = vds.getName();
                        }
                    }
                    jsonObject.put("name", name);
                }
                schedule.setObject(jsonArray.toString());
            }
            getQueryReturnValue().setReturnValue(schedules);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
