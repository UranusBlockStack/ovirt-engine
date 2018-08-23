package org.ovirt.engine.core.common.businessentities;

import javax.validation.constraints.Size;

import org.ovirt.engine.core.common.validation.group.CreateEntity;
import org.ovirt.engine.core.common.validation.group.UpdateEntity;
import org.ovirt.engine.core.compat.Guid;

public class Schedule extends IVdcQueryable implements BusinessEntity<Guid> {

    private static final long serialVersionUID = 7249605272394212577L;

    private Guid id = Guid.Empty;

    @Size(min = 0, max = BusinessEntitiesDefinitions.SCHEDULE_NAME_SIZE, groups = { CreateEntity.class, UpdateEntity.class })
    private String name;

    private ScheduleType type = ScheduleType.VmBackup;

    private String object;

    private String strategy;

    public Schedule() {
        this.id = Guid.newGuid();
    }

    public Schedule(Guid id) {
        this.id = id;
    }

    public Guid getId() {
        return id;
    }

    public void setId(Guid id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ScheduleType getType() {
        return type;
    }

    public void setType(ScheduleType type) {
        this.type = type;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((strategy == null) ? 0 : strategy.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Schedule other = (Schedule) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (object == null) {
            if (other.object != null)
                return false;
        } else if (!object.equals(other.object))
            return false;
        if (strategy == null) {
            if (other.strategy != null)
                return false;
        } else if (!strategy.equals(other.strategy))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public Object getQueryableId() {
        // TODO Auto-generated method stub
        return getId();
    }

}
