package org.ovirt.engine.ui.webadmin.section.main.view.popup;

import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.uicommonweb.models.configure.ScheduleListModel;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.ApplicationResources;
import org.ovirt.engine.ui.webadmin.section.main.presenter.SchedulesPopupPresenterWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;

public class SchedulesPopupView extends AbstractSchedulesPopupView<ScheduleListModel> implements SchedulesPopupPresenterWidget.ViewDef {

    interface Driver extends SimpleBeanEditorDriver<ScheduleListModel, SchedulesPopupView> {
        Driver driver = GWT.create(Driver.class);
    }

    interface ViewIdHandler extends ElementIdHandler<SchedulesPopupView> {
        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    @Inject
    public SchedulesPopupView(EventBus eventBus, ApplicationResources resources, ApplicationConstants constants) {
        super(eventBus, resources, constants);
    }

    @Override
    public void edit(ScheduleListModel object) {
        super.edit(object);
        Driver.driver.edit(object);
    }

    @Override
    protected void generateIds() {
        ViewIdHandler.idHandler.generateAndSetIds(this);
    }

    @Override
    protected void initDriver() {
        Driver.driver.initialize(this);
    }

    @Override
    protected ScheduleListModel doFlush() {
        return Driver.driver.flush();
    }

}
