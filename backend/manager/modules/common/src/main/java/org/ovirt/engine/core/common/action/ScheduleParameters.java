package org.ovirt.engine.core.common.action;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.compat.Guid;

public class ScheduleParameters extends VdcActionParametersBase {

    private static final long serialVersionUID = 1L;

    private Guid id;
    private Schedule schedule;

    public ScheduleParameters() {
        super();
    }

    public ScheduleParameters(Guid id) {
        this.id = id;
    }

    public ScheduleParameters(Schedule schedule) {
        this.schedule = schedule;
        if (null != schedule) {
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
