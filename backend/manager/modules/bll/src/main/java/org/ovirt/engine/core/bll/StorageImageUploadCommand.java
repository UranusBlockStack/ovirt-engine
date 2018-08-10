package org.ovirt.engine.core.bll;

import org.ovirt.engine.core.common.action.ImagesHandlerActionParameters;

public class StorageImageUploadCommand<T extends ImagesHandlerActionParameters> extends AdminOperationCommandBase<T> {

    private static final long serialVersionUID = 1L;

    public StorageImageUploadCommand(T parameters) {
        super(parameters);
    }

    @Override
    protected void executeCommand() {
        StorageImageManager storageImageManager = new StorageImageManager(getParameters().getSdId());
        if(getParameters().isMount()) {
            setSucceeded(storageImageManager.mount());
            if(getSucceeded()) {
                getParameters().setNfsLocalPath(storageImageManager.getNfsLocalPath());
                getParameters().setFullTargetPath(storageImageManager.getFullTargetPath());
            }
        } else {
            storageImageManager.setNfsLocalPath(getParameters().getNfsLocalPath());
            setSucceeded(storageImageManager.umount());
        }
    }

}
