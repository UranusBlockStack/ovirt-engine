package org.ovirt.engine.ui.webadmin.section.main.presenter;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.ui.common.presenter.AbstractModelBoundPopupPresenterWidget;
import org.ovirt.engine.ui.uicommonweb.models.configure.ScheduleListModel;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;

public class AbstractSchedulesPopupPresenterWidget<V extends AbstractSchedulesPopupPresenterWidget.ViewDef<M>, M extends ScheduleListModel>
        extends AbstractModelBoundPopupPresenterWidget<M, V> {

    public interface ViewDef<A extends ScheduleListModel> extends AbstractModelBoundPopupPresenterWidget.ViewDef<A> {
        void initTableData();
        void setSchedule(Schedule schedule);
    }

    @Inject
    public AbstractSchedulesPopupPresenterWidget(EventBus eventBus, V view) {
        super(eventBus, view);
    }

    @Override
    public void init(M model) {
        // TODO Auto-generated method stub
        super.init(model);
        // Set Schedule
        getView().setSchedule(model.getSchedule());
        getView().initTableData();
    }

    @Override
    protected void onBind() {
        super.onBind();
    }

    @Override
    protected void onHide() {
        super.onHide();
    }

    @Override
    protected void onReveal() {
        super.onReveal();
    }
}
