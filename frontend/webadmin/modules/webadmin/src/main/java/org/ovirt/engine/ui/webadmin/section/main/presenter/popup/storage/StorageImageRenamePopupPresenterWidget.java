package org.ovirt.engine.ui.webadmin.section.main.presenter.popup.storage;

import org.ovirt.engine.ui.common.presenter.AbstractModelBoundPopupPresenterWidget;
import org.ovirt.engine.ui.uicommonweb.models.storage.StorageImageModel;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;

public class StorageImageRenamePopupPresenterWidget extends AbstractModelBoundPopupPresenterWidget<StorageImageModel, StorageImageRenamePopupPresenterWidget.ViewDef> {

    public interface ViewDef extends AbstractModelBoundPopupPresenterWidget.ViewDef<StorageImageModel> {
        void init(StorageImageModel model);
    }

    @Inject
    public StorageImageRenamePopupPresenterWidget(EventBus eventBus, ViewDef view) {
        super(eventBus, view);
    }

    @Override
    public void init(StorageImageModel model) {
        super.init(model);
        getView().init(model);
    }

}
