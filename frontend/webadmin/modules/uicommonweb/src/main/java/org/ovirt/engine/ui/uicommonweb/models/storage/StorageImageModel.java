package org.ovirt.engine.ui.uicommonweb.models.storage;

import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.Model;
import org.ovirt.engine.ui.uicommonweb.validation.IValidation;
import org.ovirt.engine.ui.uicommonweb.validation.LengthValidation;
import org.ovirt.engine.ui.uicommonweb.validation.NotEmptyValidation;
import org.ovirt.engine.ui.uicommonweb.validation.RegexValidation;
import org.ovirt.engine.ui.uicompat.ConstantsManager;

public class StorageImageModel extends Model {

    private EntityModel imageName;
    private EntityModel imageNewName;
    private Guid sdId = Guid.Empty;

    public EntityModel getImageName() {
        return imageName;
    }

    public void setImageName(EntityModel imageName) {
        this.imageName = imageName;
    }

    public EntityModel getImageNewName() {
        if(null == imageNewName) {
            imageNewName = new EntityModel();
        }
        return imageNewName;
    }

    public void setImageNewName(EntityModel imageNewName) {
        this.imageNewName = imageNewName;
    }

    public Guid getSdId() {
        return sdId;
    }

    public void setSdId(Guid sdId) {
        this.sdId = sdId;
    }

    public StorageImageModel() {
    }

    public StorageImageModel(EntityModel imageName) {
        setImageName(imageName);
    }

    public boolean ValidateImageNewName() {
        String nameExpr = "^[-\\u4e00-\\u9fa5\\w\\.]{1,255}$"; //$NON-NLS-1$
        String nameMsg = ConstantsManager.getInstance().getMessages().nameMustConataionOnlyAlphanumericChars(255);
        getImageNewName().validateEntity(
                new IValidation[] {
                        new NotEmptyValidation(),
                        new LengthValidation(255),
                        new RegexValidation(nameExpr, nameMsg)
                });
        return getImageNewName().getIsValid();
    }
}
