package org.ovirt.engine.core.bll;

import org.ovirt.engine.core.common.action.ScheduleParameters;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;

public class RemoveScheduleCommand<T extends ScheduleParameters> extends AdminOperationCommandBase<T> {

    private static final long serialVersionUID = 1L;

    public RemoveScheduleCommand(T parameters) {
        super(parameters);
    }

    @Override
    protected void executeCommand() {
        DbFacade.getInstance().getSchedulesDao().remove(getParameters().getScheduleId());
        // Remove Schedules
        InitScheduleManager.removeScheduleJob(getParameters().getScheduleId());
        setSucceeded(true);
    }

}
