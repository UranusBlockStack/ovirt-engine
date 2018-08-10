package org.ovirt.engine.ui.webadmin.section.main.view.popup.storage;

import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.view.popup.AbstractModelBoundPopupView;
import org.ovirt.engine.ui.common.widget.dialog.SimpleDialogPanel;
import org.ovirt.engine.ui.uicommonweb.models.storage.StorageImageModel;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.ApplicationResources;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.storage.StorageImageUploadPopupPresenterWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;

public class StorageImageUploadPopupView extends AbstractModelBoundPopupView<StorageImageModel> implements StorageImageUploadPopupPresenterWidget.ViewDef {

    interface ViewUiBinder extends UiBinder<SimpleDialogPanel, StorageImageUploadPopupView> {
        ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
    }

    interface ViewIdHandler extends ElementIdHandler<StorageImageUploadPopupView> {
        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    @UiField
    @Ignore
    public IFrameElement imageUpload;

    @Inject
    public StorageImageUploadPopupView(EventBus eventBus, ApplicationResources resources, ApplicationConstants constants) {
        super(eventBus, resources);
        initWidget(ViewUiBinder.uiBinder.createAndBindUi(this));
        ViewIdHandler.idHandler.generateAndSetIds(this);
        initEvent();
        initLayout();
    }

    public void initLayout() {
    }

    public void initEvent() {
    }

    @Override
    public void focusInput() {
    }

    @Override
    public void edit(StorageImageModel object) {
    }

    @Override
    public StorageImageModel flush() {
        return null;
    }

    @Override
    public void submitFormPanel() {
    }

    @Override
    public void init(StorageImageModel model) {
        imageUpload.setSrc("../webadmin/imageUpload.html?sdid=" + model.getSdId().toString());  //$NON-NLS-1$
    }

}
