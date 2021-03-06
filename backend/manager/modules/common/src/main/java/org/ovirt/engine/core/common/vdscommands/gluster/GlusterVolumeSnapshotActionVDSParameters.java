package org.ovirt.engine.core.common.vdscommands.gluster;

import org.ovirt.engine.core.compat.Guid;

public class GlusterVolumeSnapshotActionVDSParameters extends GlusterVolumeVDSParameters {
    String snapshotName;

    public GlusterVolumeSnapshotActionVDSParameters() {
    }

    public GlusterVolumeSnapshotActionVDSParameters(Guid serverId, String volumeName, String snapshotName) {
        super(serverId, volumeName);
        this.snapshotName = snapshotName;
    }

    public String getSnapshotName() {
        return this.snapshotName;
    }

    public void setSnapshotName(String snapshotName) {
        this.snapshotName = snapshotName;
    }

    @Override
    public String toString() {
        return String.format("%s, snapshotName=%s", super.toString(), getSnapshotName());
    }
}
