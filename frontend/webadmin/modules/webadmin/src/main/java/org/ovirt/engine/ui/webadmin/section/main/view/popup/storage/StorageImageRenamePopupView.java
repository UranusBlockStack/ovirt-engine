package org.ovirt.engine.ui.webadmin.section.main.view.popup.storage;

import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.view.popup.AbstractModelBoundPopupView;
import org.ovirt.engine.ui.common.widget.dialog.SimpleDialogPanel;
import org.ovirt.engine.ui.common.widget.editor.TextBoxChanger;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.storage.StorageImageModel;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.ApplicationResources;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.storage.StorageImageRenamePopupPresenterWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

public class StorageImageRenamePopupView extends AbstractModelBoundPopupView<StorageImageModel> implements StorageImageRenamePopupPresenterWidget.ViewDef {

    interface ViewUiBinder extends UiBinder<SimpleDialogPanel, StorageImageRenamePopupView> {
        ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
    }

    interface ViewIdHandler extends ElementIdHandler<StorageImageRenamePopupView> {
        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    @UiField
    @Ignore
    public TextBoxChanger imageNewNameText;

    @UiField
    Label message;

    @UiField
    Label extension;

    private StorageImageModel model;

    @Inject
    public StorageImageRenamePopupView(EventBus eventBus, ApplicationResources resources, ApplicationConstants constants) {
        super(eventBus, resources);
        initWidget(ViewUiBinder.uiBinder.createAndBindUi(this));
        localize(constants);
        ViewIdHandler.idHandler.generateAndSetIds(this);

        initEvent();
    }

    void localize(ApplicationConstants constants) {
    }

    public void initEvent() {
        imageNewNameText.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                EntityModel imageNewName = model.getImageNewName();
                imageNewName.setEntity(imageNewNameText.getText() + extension.getText());
                message.setText(""); //$NON-NLS-1$
                if(!model.ValidateImageNewName()) {
                    if(imageNewName.getInvalidityReasons().size() > 0) {
                        message.setText(imageNewName.getInvalidityReasons().get(0));
                    }
                }
            }
        });
    }

    @Override
    public void focusInput() {
        imageNewNameText.setFocus(true);
    }

    @Override
    public void edit(StorageImageModel object) {
    }

    @Override
    public StorageImageModel flush() {
        return null;
    }

    @Override
    public void init(StorageImageModel model) {
        this.model = model;
        String imageName = model.getImageName().getTitle();
        int idx = imageName.lastIndexOf("."); //$NON-NLS-1$
        if(idx > -1) {
            imageNewNameText.setText(imageName.substring(0, idx));
            extension.setText(imageName.substring(idx));
        }
    }

}
