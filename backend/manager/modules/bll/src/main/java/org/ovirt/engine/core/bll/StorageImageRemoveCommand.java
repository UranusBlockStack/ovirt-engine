package org.ovirt.engine.core.bll;

import org.ovirt.engine.core.common.action.ImagesHandlerActionParameters;
import org.ovirt.engine.core.common.errors.VdcBllErrors;

public class StorageImageRemoveCommand<T extends ImagesHandlerActionParameters> extends AdminOperationCommandBase<T> {

    private static final long serialVersionUID = 1L;

    public StorageImageRemoveCommand(T parameters) {
        super(parameters);
    }

    @Override
    protected void executeCommand() {
        setSucceeded(StorageImageManager.removeFile(getParameters().getSdId(), getParameters().getImageName()));
        if(!getSucceeded()) {
            getReturnValue().getFault().setError(VdcBllErrors.STORAGE_IMAGE_MANAGER_REMOVE_FAILED);
            getReturnValue().getFault().setMessage(VdcBllErrors.STORAGE_IMAGE_MANAGER_REMOVE_FAILED.name());
        }
    }

}
