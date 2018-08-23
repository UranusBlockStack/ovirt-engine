package org.ovirt.engine.core.dao;

import java.util.List;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.compat.Guid;

public interface SchedulesDao extends GenericDao<Schedule, Guid> {

    /**
     * Get Schedule
     */
    Schedule get(Guid id);

    /**
     * Get All
     * @return Schedules
     */
    List<Schedule> getAll();

}
