package org.ovirt.engine.ui.webadmin.section.main.view.popup.gluster;

import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.idhandler.WithElementId;
import org.ovirt.engine.ui.common.view.popup.AbstractModelBoundPopupView;
import org.ovirt.engine.ui.common.widget.Align;
import org.ovirt.engine.ui.common.widget.dialog.InfoIcon;
import org.ovirt.engine.ui.common.widget.dialog.SimpleDialogPanel;
import org.ovirt.engine.ui.common.widget.editor.generic.EntityModelCheckBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.generic.StringEntityModelLabelEditor;
import org.ovirt.engine.ui.uicommonweb.models.gluster.GlusterVolumeGeoRepActionConfirmationModel;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.ApplicationResources;
import org.ovirt.engine.ui.webadmin.ApplicationTemplates;
import org.ovirt.engine.ui.webadmin.gin.AssetProvider;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.gluster.GlusterVolumeGeoRepActionConfirmPopUpViewPresenterWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

public class GeoRepActionConfirmPopUpView extends AbstractModelBoundPopupView<GlusterVolumeGeoRepActionConfirmationModel> implements GlusterVolumeGeoRepActionConfirmPopUpViewPresenterWidget.ViewDef {

    interface Driver extends SimpleBeanEditorDriver<GlusterVolumeGeoRepActionConfirmationModel, GeoRepActionConfirmPopUpView> {
    }

    interface ViewUiBinder extends UiBinder<SimpleDialogPanel, GeoRepActionConfirmPopUpView> {
        ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
    }

    interface ViewIdHandler extends ElementIdHandler<GeoRepActionConfirmPopUpView> {
        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    @UiField
    WidgetStyle style;

    @UiField
    @Path("masterVolume.entity")
    @WithElementId
    StringEntityModelLabelEditor masterVolumeEditor;

    @UiField
    @Path("slaveVolume.entity")
    @WithElementId
    StringEntityModelLabelEditor slaveVolumeEditor;

    @UiField
    @Path("slaveHost.entity")
    @WithElementId
    StringEntityModelLabelEditor slaveHostEditor;

    @UiField(provided = true)
    @Path("force.entity")
    @WithElementId
    EntityModelCheckBoxEditor forceEditor;

    @UiField(provided = true)
    @Ignore
    InfoIcon geoRepForceHelpIcon;

    @UiField
    @Ignore
    @WithElementId
    Label errorMsg;

    private final Driver driver = GWT.create(Driver.class);

    private final static ApplicationTemplates templates = AssetProvider.getTemplates();
    private final static ApplicationResources resources = AssetProvider.getResources();
    private final static ApplicationConstants constants = AssetProvider.getConstants();

    @Inject
    public GeoRepActionConfirmPopUpView(EventBus eventBus) {
        super(eventBus);
        initEditors();
        initWidget(ViewUiBinder.uiBinder.createAndBindUi(this));
        ViewIdHandler.idHandler.generateAndSetIds(this);
        localize();
        addStyles();
        driver.initialize(this);
        errorMsg.setVisible(false);
    }

    private void addStyles() {
        forceEditor.addContentWidgetContainerStyleName(style.checkBoxEditorWidget());
    }

    private void localize() {
        masterVolumeEditor.setLabel(constants.geoRepMasterVolume());
        slaveVolumeEditor.setLabel(constants.geoRepSlaveVolume());
        slaveHostEditor.setLabel(constants.geoRepSlaveHostIp());
    }

    @Override
    public void setForceLabelMessage(String forceLabelMessage) {
        forceEditor.setLabel(forceLabelMessage == null ? constants.notAvailableLabel() : forceLabelMessage);
        forceEditor.setVisible(forceLabelMessage != null);
    }

    private void initEditors() {
        forceEditor = new EntityModelCheckBoxEditor(Align.RIGHT);
        geoRepForceHelpIcon = new InfoIcon(templates.italicText(constants.notAvailableLabel()));
    }

    @Override
    public void edit(final GlusterVolumeGeoRepActionConfirmationModel object) {
        driver.edit(object);
    }

    @Override
    public void setForceHelp(String forceHelpText) {
        geoRepForceHelpIcon.setText(templates.italicText(forceHelpText));
    }

    @Override
    public GlusterVolumeGeoRepActionConfirmationModel flush() {
        return driver.flush();
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        errorMsg.setText(errorMessage);
        errorMsg.setVisible(errorMessage != null);
    }

    interface WidgetStyle extends CssResource {
        String checkBoxEditorWidget();
    }
}
