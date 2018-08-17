package org.ovirt.engine.ui.webadmin.section.main.presenter;

import org.ovirt.engine.ui.uicommonweb.models.configure.ScheduleListModel;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;

public class SchedulesPopupPresenterWidget extends AbstractSchedulesPopupPresenterWidget<SchedulesPopupPresenterWidget.ViewDef, ScheduleListModel> {

    public interface ViewDef extends AbstractSchedulesPopupPresenterWidget.ViewDef<ScheduleListModel> {

    }

    @Inject
    public SchedulesPopupPresenterWidget(EventBus eventBus, ViewDef view) {
        super(eventBus, view);
    }

}
