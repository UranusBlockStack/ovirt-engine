package org.ovirt.engine.core.common.action;

import org.ovirt.engine.core.compat.Guid;


public class ImagesHandlerActionParameters extends VdcActionParametersBase {
    private static final long serialVersionUID = 586590899729374093L;

    private String imageName;
    private String imageNewName;
    private Guid sdId;

    private boolean mount;
    private String nfsLocalPath;
    private String fullTargetPath;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageNewName() {
        return imageNewName;
    }

    public void setImageNewName(String imageNewName) {
        this.imageNewName = imageNewName;
    }

    public Guid getSdId() {
        return sdId;
    }

    public void setSdId(Guid sdId) {
        this.sdId = sdId;
    }

    public boolean isMount() {
        return mount;
    }

    public void setMount(boolean mount) {
        this.mount = mount;
    }

    public String getNfsLocalPath() {
        return nfsLocalPath;
    }

    public void setNfsLocalPath(String nfsLocalPath) {
        this.nfsLocalPath = nfsLocalPath;
    }

    public String getFullTargetPath() {
        return fullTargetPath;
    }

    public void setFullTargetPath(String fullTargetPath) {
        this.fullTargetPath = fullTargetPath;
    }

    public ImagesHandlerActionParameters() {
    }

    public ImagesHandlerActionParameters(Guid sdId) {
        this.sdId = sdId;
    }

    public ImagesHandlerActionParameters(Guid sdId, String imageName) {
        this.sdId = sdId;
        this.imageName = imageName;
    }

    public ImagesHandlerActionParameters(Guid sdId, String imageName, String imageNewName) {
        this.sdId = sdId;
        this.imageName = imageName;
        this.imageNewName = imageNewName;
    }
}
