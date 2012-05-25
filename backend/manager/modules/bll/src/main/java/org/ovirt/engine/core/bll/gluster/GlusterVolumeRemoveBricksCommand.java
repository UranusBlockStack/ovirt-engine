package org.ovirt.engine.core.bll.gluster;

import java.util.ArrayList;
import java.util.List;

import org.ovirt.engine.core.bll.NonTransactiveCommandAttribute;
import org.ovirt.engine.core.common.AuditLogType;
import org.ovirt.engine.core.common.action.gluster.GlusterVolumeRemoveBricksParameters;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterBrickEntity;
import org.ovirt.engine.core.common.businessentities.gluster.GlusterVolumeEntity;
import org.ovirt.engine.core.common.validation.group.gluster.RemoveBrick;
import org.ovirt.engine.core.common.vdscommands.VDSCommandType;
import org.ovirt.engine.core.common.vdscommands.VDSReturnValue;
import org.ovirt.engine.core.common.vdscommands.gluster.GlusterVolumeRemoveBricksVDSParameters;
import org.ovirt.engine.core.dal.VdcBllMessages;

/**
 * BLL command to Remove Bricks from Gluster volume
 */
@NonTransactiveCommandAttribute
public class GlusterVolumeRemoveBricksCommand extends GlusterVolumeCommandBase<GlusterVolumeRemoveBricksParameters> {
    private static final long serialVersionUID = 1465299601226267507L;
    private List<GlusterBrickEntity> bricks = new ArrayList<GlusterBrickEntity>();

    public GlusterVolumeRemoveBricksCommand(GlusterVolumeRemoveBricksParameters params) {
        super(params);
    }

    @Override
    protected List<Class<?>> getValidationGroups() {
        addValidationGroup(RemoveBrick.class);
        return super.getValidationGroups();
    }

    @Override
    protected void setActionMessageParameters() {
        addCanDoActionMessage(VdcBllMessages.VAR__ACTION__REMOVE);
        addCanDoActionMessage(VdcBllMessages.VAR__TYPE__GLUSTER_VOLUME_BRICK);
    }

    @Override
    protected boolean canDoAction() {
        if (!super.canDoAction()) {
            return false;
        }
        if (getParameters().getBricks() == null || getParameters().getBricks().size() == 0) {
            addCanDoActionMessage(VdcBllMessages.ACTION_TYPE_FAILED_BRICKS_REQUIRED);
            return false;
        }
        if (getGlusterVolume().getBricks().size() == 1 ||
                getGlusterVolume().getBricks().size() <= getParameters().getBricks().size()) {
            addCanDoActionMessage(VdcBllMessages.ACTION_TYPE_FAILED_CAN_NOT_REMOVE_ALL_BRICKS_FROM_VOLUME);
            return false;
        }
        return validateBricks(getParameters().getBricks());
    }

    @Override
    protected void executeCommand() {

        VDSReturnValue returnValue =
                runVdsCommand(
                        VDSCommandType.GlusterVolumeRemoveBricks,
                        new GlusterVolumeRemoveBricksVDSParameters(getUpServer().getId(),
                                getGlusterVolumeName(), bricks));
        setSucceeded(returnValue.getSucceeded());
        if (getSucceeded()) {
            removeBricksFromVolumeInDb(getGlusterVolume(), bricks);
        } else {
            getReturnValue().getExecuteFailedMessages().add(returnValue.getVdsError().getMessage());
            return;
        }
    }

    /**
     * Checks that all brick ids passed are valid, also populating the class level bricks list with populated brick
     * objects obtained from the volume.
     *
     * @param bricks The bricks to validate
     * @return true if all bricks have valid ids, else false
     */
    private boolean validateBricks(List<GlusterBrickEntity> bricks) {
        GlusterVolumeEntity volume = getGlusterVolume();
        for (GlusterBrickEntity brick : bricks) {
            if (brick.getServerName() != null && brick.getBrickDirectory() != null) {
                // brick already contains required info.
                this.bricks.add(brick);
                continue;
            }

            if (brick.getId(false) == null) {
                addCanDoActionMessage(VdcBllMessages.ACTION_TYPE_FAILED_BRICKS_REQUIRED);
                return false;
            }

            GlusterBrickEntity brickFromVolume = volume.getBrickWithId(brick.getId());
            if (brickFromVolume == null) {
                addCanDoActionMessage(VdcBllMessages.ACTION_TYPE_FAILED_GLUSTER_BRICK_INVALID);
                return false;
            } else {
                this.bricks.add(brickFromVolume);
            }
        }

        return true;
    }

    @Override
    public AuditLogType getAuditLogTypeValue() {
        if (getSucceeded()) {
            return AuditLogType.GLUSTER_VOLUME_REMOVE_BRICKS;
        } else {
            return AuditLogType.GLUSTER_VOLUME_REMOVE_BRICKS_FAILED;
        }
    }

    private void removeBricksFromVolumeInDb(GlusterVolumeEntity volume, List<GlusterBrickEntity> brickList) {
        for (GlusterBrickEntity brick : brickList) {
            getGlusterBrickDao().removeBrick(brick.getId());
        }
    }
}
