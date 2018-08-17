package org.ovirt.engine.ui.webadmin.uicommon.model;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.ui.common.presenter.AbstractModelBoundPopupPresenterWidget;
import org.ovirt.engine.ui.common.presenter.popup.DefaultConfirmationPopupPresenterWidget;
import org.ovirt.engine.ui.common.presenter.popup.RemoveConfirmationPopupPresenterWidget;
import org.ovirt.engine.ui.common.uicommon.model.SearchableTabModelProvider;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.models.ConfirmationModel;
import org.ovirt.engine.ui.uicommonweb.models.Model;
import org.ovirt.engine.ui.uicommonweb.models.configure.SchedulesListModel;
import org.ovirt.engine.ui.webadmin.section.main.presenter.SchedulesPopupPresenterWidget;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SchedulesModelProvider extends SearchableTabModelProvider<Schedule, SchedulesListModel> {

    private final Provider<RemoveConfirmationPopupPresenterWidget> removeConfirmPopupProvider;
    private final Provider<SchedulesPopupPresenterWidget> schedulesPopupPresenterWidget;

    @Inject
    public SchedulesModelProvider(EventBus eventBus,
            Provider<DefaultConfirmationPopupPresenterWidget> defaultConfirmPopupProvider,
            Provider<SchedulesPopupPresenterWidget> schedulesPopupPresenterWidget,
            Provider<RemoveConfirmationPopupPresenterWidget> removeConfirmPopupProvider ) {
        super(eventBus, defaultConfirmPopupProvider);
        this.removeConfirmPopupProvider = removeConfirmPopupProvider;
        this.schedulesPopupPresenterWidget = schedulesPopupPresenterWidget;
    }

    @Override
    public SchedulesListModel getModel() {
        return getCommonModel().getSchedulesListModel();
    }

    @Override
    public AbstractModelBoundPopupPresenterWidget<? extends Model, ?> getModelPopup(SchedulesListModel source,
            UICommand lastExecutedCommand, Model windowModel) {
        if (lastExecutedCommand == getModel().getAddCommand()) {
            return schedulesPopupPresenterWidget.get();
        } else {
            return super.getModelPopup(source, lastExecutedCommand, windowModel);
        }
    }

    @Override
    public AbstractModelBoundPopupPresenterWidget<? extends ConfirmationModel, ?> getConfirmModelPopup(SchedulesListModel source,
            UICommand lastExecutedCommand) {
        if (lastExecutedCommand == getModel().getRemoveCommand()) {
            return removeConfirmPopupProvider.get();
        } else {
            return super.getConfirmModelPopup(source, lastExecutedCommand);
        }
    }
}
