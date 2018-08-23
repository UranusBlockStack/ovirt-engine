package org.ovirt.engine.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.businessentities.ScheduleType;
import org.ovirt.engine.core.common.utils.EnumUtils;
import org.ovirt.engine.core.compat.Guid;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class SchedulesDaoDbFacadeImpl extends DefaultGenericDaoDbFacade<Schedule, Guid> implements SchedulesDao {

    private static final ParameterizedRowMapper<Schedule> ROW_MAPPER = new ScheduleRowMapper();

    private static final ParameterizedRowMapper<Schedule> NO_CONFIG_ROW_MAPPER = new ScheduleRowMapperWithConfigurationAvailable();

    public SchedulesDaoDbFacadeImpl() {
        super("Schedules");
    }

    @Override
    public Schedule get(Guid id) {
        List<Schedule> lists= getCallsHandler().executeReadList("getschedulesbyid", ROW_MAPPER, createIdParameterMapper(id));
        if(lists != null && lists.size() > 0) {
            return lists.get(0);
        }
        return new Schedule();
    }

    @Override
    public List<Schedule> getAll() {
        return getCallsHandler().executeReadList("getschedules", ROW_MAPPER,
                getCustomMapSqlParameterSource());
    }

    @Override
    protected MapSqlParameterSource createFullParametersMapper(Schedule entity) {
        return createIdParameterMapper(entity.getId())
                .addValue("schedule_id", entity.getId())
                .addValue("schedule_name", entity.getName())
                .addValue("schedule_type", EnumUtils.nameOrNull(entity.getType()))
                .addValue("schedule_object", entity.getObject())
                .addValue("schedule_strategy", entity.getStrategy());
    }

    @Override
    protected MapSqlParameterSource createIdParameterMapper(Guid id) {
        return getCustomMapSqlParameterSource().addValue("schedule_id", id);
    }

    @Override
    protected ParameterizedRowMapper<Schedule> createEntityRowMapper() {
        return ROW_MAPPER;
    }

    private static class ScheduleRowMapper implements ParameterizedRowMapper<Schedule> {

        @Override
        public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
            Schedule schedule = createInitialScheduleEntity(rs);
            schedule.setId(Guid.createGuidFromString(rs.getString("schedule_id")));
            schedule.setName(rs.getString("schedule_name"));
            schedule.setType(ScheduleType.valueOf(rs.getString("schedule_type")));
            schedule.setObject(rs.getString("schedule_object"));
            schedule.setStrategy(rs.getString("schedule_strategy"));
            return schedule;
        }

        protected Schedule createInitialScheduleEntity(ResultSet rs) throws SQLException {
            return new Schedule();
        }
    }

    private static class ScheduleRowMapperWithConfigurationAvailable extends ScheduleRowMapper {

        @Override
        protected Schedule createInitialScheduleEntity(ResultSet rs) throws SQLException {
            return new Schedule(Guid.createGuidFromString(rs.getString("schedule_id")));
        }
    }
}
