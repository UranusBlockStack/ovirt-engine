package org.ovirt.engine.core.common.queries;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.compat.Guid;

public class GetScheduleParameters extends VdcQueryParametersBase {

    private static final long serialVersionUID = 1L;

    private Guid id;
    private Schedule schedule;

    public GetScheduleParameters() {
    }

    public GetScheduleParameters(Guid id) {
        this.id = id;
    }

    public GetScheduleParameters(Schedule schedule) {
        this.schedule = schedule;
        if(null != schedule) {
            this.id = schedule.getId();
        }
    }

    public Guid getScheduleId() {
        return id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

}
