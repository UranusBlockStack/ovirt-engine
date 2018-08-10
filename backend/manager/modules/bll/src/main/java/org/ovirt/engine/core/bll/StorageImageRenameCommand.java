package org.ovirt.engine.core.bll;

import org.ovirt.engine.core.common.action.ImagesHandlerActionParameters;
import org.ovirt.engine.core.common.errors.VdcBllErrors;

public class StorageImageRenameCommand<T extends ImagesHandlerActionParameters> extends AdminOperationCommandBase<T> {

    private static final long serialVersionUID = 1L;

    public StorageImageRenameCommand(T parameters) {
        super(parameters);
    }

    @Override
    protected void executeCommand() {
        ImagesHandlerActionParameters params = getParameters();
        setSucceeded(StorageImageManager.renameFile(params.getSdId(), params.getImageName(), params.getImageNewName()));
        if(!getSucceeded()) {
            getReturnValue().getFault().setError(VdcBllErrors.STORAGE_IMAGE_MANAGER_RENAME_FAILED);
            getReturnValue().getFault().setMessage(VdcBllErrors.STORAGE_IMAGE_MANAGER_RENAME_FAILED.name());
        }
    }

}
