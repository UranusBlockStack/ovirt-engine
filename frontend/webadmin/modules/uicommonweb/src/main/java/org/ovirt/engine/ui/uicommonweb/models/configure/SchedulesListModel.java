package org.ovirt.engine.ui.uicommonweb.models.configure;

import java.util.ArrayList;
import java.util.Collection;

import org.ovirt.engine.core.common.action.ScheduleParameters;
import org.ovirt.engine.core.common.action.VdcActionParametersBase;
import org.ovirt.engine.core.common.action.VdcActionType;
import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.queries.GetScheduleParameters;
import org.ovirt.engine.core.common.queries.VdcQueryReturnValue;
import org.ovirt.engine.core.common.queries.VdcQueryType;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.frontend.INewAsyncCallback;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.models.ConfirmationModel;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;
import org.ovirt.engine.ui.uicompat.ConstantsManager;
import org.ovirt.engine.ui.uicompat.FrontendActionAsyncResult;
import org.ovirt.engine.ui.uicompat.FrontendMultipleActionAsyncResult;
import org.ovirt.engine.ui.uicompat.IFrontendActionAsyncCallback;
import org.ovirt.engine.ui.uicompat.IFrontendMultipleActionAsyncCallback;

@SuppressWarnings("unused")
public class SchedulesListModel extends SearchableListModel {

    private final UICommand addCommand;
    private final UICommand editCommand;
    private final UICommand removeCommand;
    private final UICommand cancelCommand;

    public UICommand getAddCommand() {
        return addCommand;
    }

    public UICommand getEditCommand() {
        return editCommand;
    }

    public UICommand getRemoveCommand() {
        return removeCommand;
    }

    public UICommand getCancelCommand() {
        return cancelCommand;
    }

    public SchedulesListModel() {
        setTitle(ConstantsManager.getInstance().getConstants().scheduleTitle());

        addCommand = new UICommand("Add", this); //$NON-NLS-1$
        editCommand  = new UICommand("Edit", this); //$NON-NLS-1$
        removeCommand = new UICommand("Remove", this); //$NON-NLS-1$
        cancelCommand = new UICommand("Cancel", this); //$NON-NLS-1$

        cancelCommand.setIsCancel(true);
        cancelCommand.setTitle(ConstantsManager.getInstance().getConstants().cancel());

        setSearchPageSize(9999);

        UpdateActionAvailability();
    }

    @Override
    public void search() {
        super.search();
    }

    @Override
    protected void syncSearch() {
        super.syncSearch();
        AsyncQuery _asyncQuery = new AsyncQuery();
        _asyncQuery.setModel(this);
        _asyncQuery.asyncCallback = new INewAsyncCallback() {
            @Override
            public void onSuccess(Object model, Object ReturnValue)
            {
                SchedulesListModel schedulesListModel = (SchedulesListModel) model;
                schedulesListModel.setItems((Collection) ((VdcQueryReturnValue) ReturnValue).getReturnValue());
            }
        };
        GetScheduleParameters params = new GetScheduleParameters();
        params.setRefresh(false);

        Frontend.getInstance().runQuery(VdcQueryType.GetSchedules, params, _asyncQuery);
    }

    private void UpdateActionAvailability() {
        boolean allowed = getSelectedItems() != null && getSelectedItems().size() > 0;
        getEditCommand().setIsExecutionAllowed(allowed);
        getRemoveCommand().setIsExecutionAllowed(allowed);
    }

    @Override
    protected void onSelectedItemChanged() {
        super.onSelectedItemChanged();
        UpdateActionAvailability();
    }

    @Override
    protected void selectedItemsChanged() {
        super.selectedItemsChanged();
        UpdateActionAvailability();
    }

    private void add() {
        if (getWindow() != null) {
            return;
        }
        ScheduleListModel model = new ScheduleListModel();
        model.setTitle(ConstantsManager.getInstance().getConstants().addScheduleTitle());

        setWindow(model);
        model.setHashName("add_schedule"); //$NON-NLS-1$

        UICommand tempVar = new UICommand("OnSave", this); //$NON-NLS-1$
        tempVar.setTitle(ConstantsManager.getInstance().getConstants().ok());
        tempVar.setIsDefault(true);
        model.getCommands().add(tempVar);
        UICommand tempVar2 = new UICommand("Cancel", this); //$NON-NLS-1$
        tempVar2.setTitle(ConstantsManager.getInstance().getConstants().cancel());
        tempVar2.setIsCancel(true);
        model.getCommands().add(tempVar2);
    }

    private void edit() {
        if (getWindow() != null) {
            return;
        }
    }

    private void OnSave() {
        if (getWindow() == null) {
            return;
        }
        final ScheduleListModel model = (ScheduleListModel) getWindow();
        Schedule schedule = model.getSchedule();
        if (model.getProgress() != null) {
            return;
        }
        model.startProgress(null);
        Frontend.getInstance().runAction(VdcActionType.AddSchedule, new ScheduleParameters(schedule),
                new IFrontendActionAsyncCallback() {
                    @Override
                    public void executed(FrontendActionAsyncResult result) {
                        ScheduleListModel scheduleListModel = (ScheduleListModel) result.getState();
                        scheduleListModel.stopProgress();
                        Cancel();
                    }
                }, model);
    }

    public void Cancel() {
        setWindow(null);
    }

    private void remove() {
        if (getWindow() != null) {
            return;
        }
        ConfirmationModel model = new ConfirmationModel();
        setWindow(model);
        model.setTitle(ConstantsManager.getInstance().getConstants().removeScheduleTitle());
        model.setHashName("remove_schedule"); //$NON-NLS-1$
        model.setMessage(ConstantsManager.getInstance().getConstants().scheduleTitle());

        ArrayList<String> list = new ArrayList<String>();
        for (Object item : getSelectedItems())
        {
            Schedule schedule = (Schedule) item;
            list.add(schedule.getName());
        }
        model.setItems(list);

        UICommand tempVar = new UICommand("OnRemove", this); //$NON-NLS-1$
        tempVar.setTitle(ConstantsManager.getInstance().getConstants().ok());
        tempVar.setIsDefault(true);
        model.getCommands().add(tempVar);

        model.getCommands().add(cancelCommand);
    }

    private void OnRemove() {
        if (getSelectedItems() != null && getSelectedItems().size() > 0) {
            ConfirmationModel model = (ConfirmationModel) getWindow();

            if (model.getProgress() != null) {
                return;
            }

            ArrayList<VdcActionParametersBase> list = new ArrayList<VdcActionParametersBase>();
            for (Object perm : getSelectedItems()) {
                list.add(new ScheduleParameters((Schedule)perm));
            }

            model.startProgress(null);

            Frontend.getInstance().runMultipleAction(VdcActionType.RemoveSchedule, list,
                    new IFrontendMultipleActionAsyncCallback() {
                        @Override
                        public void executed(FrontendMultipleActionAsyncResult result) {

                            ConfirmationModel localModel = (ConfirmationModel) result.getState();
                            localModel.stopProgress();
                            Cancel();

                        }
                    }, model);
        }
    }

    @Override
    public void executeCommand(UICommand command) {
        super.executeCommand(command);
        if(command == getAddCommand()) {
            add();
        } else if(command == getEditCommand()) {
            edit();
        } else if(command == getRemoveCommand()) {
            remove();
        } else if("OnSave".equalsIgnoreCase(command.getName())) { //$NON-NLS-1$
            OnSave();
        } else if("OnRemove".equalsIgnoreCase(command.getName())) { //$NON-NLS-1$
            OnRemove();
        } else if("Cancel".equalsIgnoreCase(command.getName())){ //$NON-NLS-1$
            Cancel();
        } else if("OnOkModel".equalsIgnoreCase(command.getName())) { //$NON-NLS-1$
            Cancel();
        }
    }

    @Override
    protected String getListName() {
        return "schedulesListModel"; //$NON-NLS-1$
    }
}
