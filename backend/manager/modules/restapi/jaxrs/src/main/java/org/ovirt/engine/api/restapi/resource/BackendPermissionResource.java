package org.ovirt.engine.api.restapi.resource;

import java.util.Map;

import org.ovirt.engine.api.model.BaseResource;
import org.ovirt.engine.api.model.Group;
import org.ovirt.engine.api.model.Permission;
import org.ovirt.engine.api.resource.PermissionResource;
import org.ovirt.engine.core.common.businessentities.aaa.DbUser;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.VdcQueryType;
import org.ovirt.engine.core.compat.Guid;

public class BackendPermissionResource
        extends AbstractBackendSubResource<Permission, org.ovirt.engine.core.common.businessentities.Permission>
        implements PermissionResource {

    protected BackendAssignedPermissionsResource parent;
    private Class<? extends BaseResource> suggestedParentType;

    protected BackendPermissionResource(String id,
                                        BackendAssignedPermissionsResource parent,
                                        Class<? extends BaseResource> suggestedParentType) {
        super(id, Permission.class, org.ovirt.engine.core.common.businessentities.Permission.class);
        this.parent = parent;
        this.suggestedParentType = suggestedParentType;
    }

    @Override
    public Permission get() {
        return performGet(VdcQueryType.GetPermissionById,
                          new IdQueryParameters(guid),
                          suggestedParentType);
    }

    @Override
    protected Permission addParents(Permission permission) {
        return parent.addParents(permission);
    }

    @Override
    protected Permission map(org.ovirt.engine.core.common.businessentities.Permission entity, Permission template) {
        Map<Guid, DbUser> users = parent.getUsers();
        return parent.map(entity, users.containsKey(entity.getAdElementId()) ? users.get(entity.getAdElementId()) : null);
    }

    @Override
    protected Permission addLinks(Permission model, Class<? extends BaseResource> suggestedParent, String... subCollectionMembersToExclude) {
        return super.addLinks(model, model.getUser() != null ? suggestedParentType : Group.class);
    }

    @Override
    protected Permission doPopulate(Permission model, org.ovirt.engine.core.common.businessentities.Permission entity) {
        return model;
    }
}
