package org.ovirt.engine.ui.webadmin.section.main.view.popup;

import java.util.ArrayList;
import java.util.Date;

import org.ovirt.engine.core.common.businessentities.IVdcQueryable;
import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.businessentities.ScheduleType;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.interfaces.SearchType;
import org.ovirt.engine.core.common.queries.SearchParameters;
import org.ovirt.engine.core.common.queries.VdcQueryParametersBase;
import org.ovirt.engine.core.common.queries.VdcQueryReturnValue;
import org.ovirt.engine.core.common.queries.VdcQueryType;
import org.ovirt.engine.ui.common.idhandler.WithElementId;
import org.ovirt.engine.ui.common.view.popup.AbstractModelBoundPopupView;
import org.ovirt.engine.ui.common.widget.dialog.SimpleDialogPanel;
import org.ovirt.engine.ui.common.widget.editor.EntityModelCellTable;
import org.ovirt.engine.ui.common.widget.editor.ListModelListBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.TextBoxChanger;
import org.ovirt.engine.ui.common.widget.table.column.EntityModelTextColumn;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.frontend.INewAsyncCallback;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.ListModel;
import org.ovirt.engine.ui.uicommonweb.models.configure.ScheduleListModel;
import org.ovirt.engine.ui.uicompat.EnumTranslator;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.ApplicationResources;
import org.ovirt.engine.ui.webadmin.section.main.presenter.AbstractSchedulesPopupPresenterWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;

public abstract class AbstractSchedulesPopupView<T extends ScheduleListModel> extends AbstractModelBoundPopupView<T> implements AbstractSchedulesPopupPresenterWidget.ViewDef<T> {

    @SuppressWarnings("rawtypes")
    interface ViewUiBinder extends UiBinder<SimpleDialogPanel, AbstractSchedulesPopupView> {
        ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
    }

    @UiField
    @Ignore
    public ListModelListBoxEditor<Object> scheduleScheduleEditor;

    @UiField
    @Ignore
    public ListModelListBoxEditor<Object> strategyScheduleEditor;

    @UiField
    @Ignore
    public TextBoxChanger strategyScheduleText;

    @UiField
    @Ignore
    public TextBoxChanger nameScheduleText;

    @UiField
    public ScrollPanel tableScrollPanel;

    @UiField(provided = true)
    @Ignore
    @WithElementId
    public EntityModelCellTable<ListModel> table;

    private Schedule schedule;

    private ApplicationConstants constants = null;

    private ScheduleType scheduleType;
    private ScheduleType strategyScheduleType;
    private final String strategyTime = "12:00:00"; //$NON-NLS-1$

    public AbstractSchedulesPopupView(EventBus eventBus,
            ApplicationResources resources,
            ApplicationConstants constants) {
        super(eventBus, resources);
        this.table = new EntityModelCellTable<ListModel>(true);
        initWidget(ViewUiBinder.uiBinder.createAndBindUi(this));
        this.constants = constants;
        generateIds();
        initDriver();
        initTable();
        initEvent();
        initEditor();
    }

    private void initTable() {
        // Table Entity Columns
        table.addEntityModelColumn(new EntityModelTextColumn<IVdcQueryable>() {
            @Override
            public String getText(IVdcQueryable entity) {
                String text = null;
                if(scheduleType == ScheduleType.SearchVM) {
                    text = ((VM)entity).getName();
                } else if(scheduleType == ScheduleType.SearchVDS) {
                    text = ((VDS)entity).getName();
                }
                return text;
            }
        }, constants.nameVm());

        table.addEntityModelColumn(new EntityModelTextColumn<IVdcQueryable>() {
            @Override
            public String getText(IVdcQueryable entity) {
                String text = null;
                if(scheduleType == ScheduleType.SearchVM) {
                    text = EnumTranslator.createAndTranslate(((VM)entity).getStatus());
                } else if(scheduleType == ScheduleType.SearchVDS) {
                    text = EnumTranslator.createAndTranslate(((VDS)entity).getStatus());
                }
                return text;
            }
        }, constants.statusVm());
    }

    public void initEvent() {
        scheduleScheduleEditor.asListBox().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                ListBox listBox  = scheduleScheduleEditor.asListBox();
                schedule.setType(ScheduleType.valueOf(listBox.getValue(listBox.getSelectedIndex())));
                initTableData();
            }
        });

        strategyScheduleEditor.asListBox().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                ListBox listBox  = strategyScheduleEditor.asListBox();
                ScheduleType _strategyScheduleType = ScheduleType.valueOf(listBox.getValue(listBox.getSelectedIndex()));
                if( _strategyScheduleType == ScheduleType.Time && strategyScheduleType != ScheduleType.Time ) {
                    strategyScheduleText.setText(DateTimeFormat.getFormat("yyyy-MM-dd").format(new Date()) + " " + strategyTime); //$NON-NLS-1$ $NON-NLS-2$
                    strategyScheduleType = _strategyScheduleType;
                } else if( _strategyScheduleType != ScheduleType.Time && strategyScheduleType == ScheduleType.Time ) {
                    strategyScheduleText.setText(strategyTime);
                    strategyScheduleType = _strategyScheduleType;
                }
                orgStrategy();
            }
        });

        nameScheduleText.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                schedule.setName(nameScheduleText.getText());
            }
        });

        strategyScheduleText.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                orgStrategy();
            }
        });

        table.getSelectionModel().addSelectionChangeHandler(new Handler() {

            @Override
            @SuppressWarnings("unchecked")
            public void onSelectionChange(SelectionChangeEvent event) {
                JSONArray jsonArray = new JSONArray();
                SelectionModel<? super EntityModel> selectionModel = table.getSelectionModel();
                for (EntityModel entity : ((MultiSelectionModel<EntityModel>) selectionModel).getSelectedSet()) {
                    JSONObject object = new JSONObject();
                    if(scheduleType == ScheduleType.SearchVM) {
                        VM vm = (VM)entity.getEntity();
                        object.put("id", new JSONString(vm.getId().toString())); //$NON-NLS-1$
                    } else if(scheduleType == ScheduleType.SearchVDS) {
                        VDS vds = (VDS)entity.getEntity();
                        object.put("id", new JSONString(vds.getId().toString())); //$NON-NLS-1$
                    }
                    jsonArray.set(jsonArray.size(), object);
                }
                schedule.setObject(jsonArray.toString());
            }
        });

    }
    public void initEditor() {
        // Init VM schedule
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VmBackup), ScheduleType.VmBackup.name());
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VmSnapshot), ScheduleType.VmSnapshot.name());
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VmRun), ScheduleType.VmRun.name());
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VmSuspend), ScheduleType.VmSuspend.name());
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VmStop), ScheduleType.VmStop.name());
        // Init VDS schedule
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VDSStart), ScheduleType.VDSStart.name());
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VDSReboot), ScheduleType.VDSReboot.name());
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VDSStop), ScheduleType.VDSStop.name());
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VDSMaintenance), ScheduleType.VDSMaintenance.name());
        scheduleScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.VDSActivate), ScheduleType.VDSActivate.name());
        // strategy
        strategyScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.Day), ScheduleType.Day.name());
        strategyScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.Weekly), ScheduleType.Weekly.name());
        strategyScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.Month), ScheduleType.Month.name());
        strategyScheduleEditor.asListBox().addItem(EnumTranslator.createAndTranslate(ScheduleType.Time), ScheduleType.Time.name());
    }

    public void orgStrategy() {
        ListBox listBox  = strategyScheduleEditor.asListBox();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("strategy1", new JSONString(listBox.getValue(listBox.getSelectedIndex()))); //$NON-NLS-1$
        jsonObject.put("strategy2", new JSONString(strategyScheduleText.getText())); //$NON-NLS-1$
        schedule.setStrategy(jsonObject.toString());
    }

    protected abstract void generateIds();

    protected abstract void initDriver();

    protected abstract T doFlush();

    @Override
    public void edit(final T object) {

    }

    @Override
    public T flush() {
        return doFlush();
    }

    @Override
    public void initTableData() {
        ScheduleType _scheduleType = ScheduleType.getSearchType(schedule.getType());

        if(null == _scheduleType || _scheduleType == scheduleType) {
            return;
        }

        scheduleType = _scheduleType;

        AsyncQuery _asyncQuery = new AsyncQuery();
        _asyncQuery.setModel(this);
        _asyncQuery.asyncCallback = new INewAsyncCallback() {

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object model, Object ReturnValue) {
                ArrayList<EntityModel> datas = new ArrayList<EntityModel>();
                for (IVdcQueryable item : (ArrayList<IVdcQueryable>) ((VdcQueryReturnValue) ReturnValue).getReturnValue()) {
                    EntityModel entityModel = new EntityModel();
                    entityModel.setEntity(item);
                    datas.add(entityModel);
                }
                table.setRowData(datas);
            }
        };

        if(_scheduleType == ScheduleType.SearchVM) {
            // Init VM Lists
            Frontend.getInstance().runQuery(VdcQueryType.GetAllVms, new VdcQueryParametersBase(), _asyncQuery);
        } else if(_scheduleType == ScheduleType.SearchVDS) {
            // Init VDS Lists
            SearchParameters searchParameters = new SearchParameters("Host:", SearchType.VDS); //$NON-NLS-1$
            searchParameters.setMaxCount(999);
            Frontend.getInstance().runQuery(VdcQueryType.Search, searchParameters, _asyncQuery);
        }

    }

    @Override
    public void setSchedule(Schedule schedule) {
        // TODO Auto-generated method stub
        this.schedule = schedule;
        // init data
        if(schedule == null) {
            schedule = new Schedule();
        }
        if(schedule.getName() == null) {
            schedule.setName("NewSchedule"); //$NON-NLS-1$
        }
        if(schedule.getType() == null) {
            schedule.setType(ScheduleType.VmBackup);
        }
        if(schedule.getObject() == null) {
            schedule.setObject("[]"); //$NON-NLS-1$
        }
        if(schedule.getStrategy() == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("strategy1", new JSONString(ScheduleType.Day.name())); //$NON-NLS-1$
            jsonObject.put("strategy2", new JSONString(strategyTime)); //$NON-NLS-1$
            schedule.setStrategy(jsonObject.toString());
        }
        // Set Value
        nameScheduleText.setText(schedule.getName());
        initListItemIndex(scheduleScheduleEditor.asListBox(), schedule.getType());
        initListItemIndex(strategyScheduleEditor.asListBox(), ScheduleType.Day);
        strategyScheduleText.setText(strategyTime);
        strategyScheduleType = ScheduleType.Day;
    }

    public void initListItemIndex(ListBox listBox, ScheduleType scheduleType) {
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if(listBox.getValue(i).equals(scheduleType.name())) {
                listBox.setItemSelected(i, true);
                return;
            }
        }
        // Default Index is 1
        if(listBox.getItemCount() > 0){
            listBox.setItemSelected(0, true);
        }
    }

}
