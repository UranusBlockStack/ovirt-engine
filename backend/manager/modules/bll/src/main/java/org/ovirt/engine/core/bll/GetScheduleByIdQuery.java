package org.ovirt.engine.core.bll;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.queries.GetScheduleParameters;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;

public class GetScheduleByIdQuery<P extends GetScheduleParameters> extends QueriesCommandBase<P> {

    public GetScheduleByIdQuery(P parameters) {
        super(parameters);
    }

    @Override
    protected void executeQueryCommand() {
        Schedule schedule = DbFacade.getInstance().getSchedulesDao().get(getParameters().getScheduleId());
        getQueryReturnValue().setReturnValue(schedule);
    }

}
