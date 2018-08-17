package org.ovirt.engine.ui.webadmin.section.main.view.popup.configure;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.core.common.businessentities.ScheduleType;
import org.ovirt.engine.ui.common.MainTableHeaderlessResources;
import org.ovirt.engine.ui.common.MainTableResources;
import org.ovirt.engine.ui.common.system.ClientStorage;
import org.ovirt.engine.ui.common.widget.table.SimpleActionTable;
import org.ovirt.engine.ui.common.widget.table.column.TextColumnWithTooltip;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicompat.EnumTranslator;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.uicommon.model.SchedulesModelProvider;
import org.ovirt.engine.ui.webadmin.widget.action.WebAdminButtonDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.inject.Inject;

public class SchedulesView extends Composite {

    interface ViewUiBinder extends UiBinder<SimplePanel, SchedulesView> {
        ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
    }

    private ApplicationConstants constants;

    @UiField
    SplitLayoutPanel content;

    private SimpleActionTable<Schedule> table;

    private final SchedulesModelProvider modelProvider;

    private final EventBus eventBus;

    private final ClientStorage clientStorage;

    @Inject
    public SchedulesView(ApplicationConstants constants,
            SchedulesModelProvider modelProvider,
            EventBus eventBus, ClientStorage clientStorage) {
        super();
        this.constants = constants;
        this.modelProvider = modelProvider;
        this.eventBus = eventBus;
        this.clientStorage = clientStorage;

        initWidget(ViewUiBinder.uiBinder.createAndBindUi(this));
        init();
    }

    /**
     * Init
     */
    public void init(){
        if(table != null ){
            return;
        }
        // init Tables
        table = new SimpleActionTable<Schedule>(modelProvider,
                getTableHeaderlessResources(), getTableResources(), eventBus, clientStorage);

        content.add(table);

        table.addColumn(new TextColumnWithTooltip<Schedule>() {

            @Override
            public String getValue(Schedule object) {
                return object.getName();
            }

        }, constants.nameSchedule());

        table.addColumn(new TextColumnWithTooltip<Schedule>() {

            @Override
            public String getValue(Schedule object) {
                return EnumTranslator.createAndTranslate(object.getType());
            }

        }, constants.scheduleSchedule());

        table.addColumn(new TextColumnWithTooltip<Schedule>() {

            @Override
            public String getValue(Schedule object) {
                String names = ""; // $NON-NLS-1$
                try {
                    JSONArray jsonArray = (JSONArray) JSONParser.parseLenient(object.getObject());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.get(i).isObject();
                        String name = jsonObject.get("name").isString().stringValue(); //$NON-NLS-1$
                        if(null != name && name.length() > 0) {
                            names += (names.length() > 0?",":"") + name; //$NON-NLS-1$ $NON-NLS-2$
                        }
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                return names;
            }

        }, constants.objectSchedule());

        table.addColumn(new TextColumnWithTooltip<Schedule>() {

            @Override
            public String getValue(Schedule object) {
                String strategy = ""; // $NON-NLS-1$
                try {
                    JSONObject jsonObject = (JSONObject) JSONParser.parseLenient(object.getStrategy());
                    ScheduleType scheduleType = ScheduleType.valueOf(jsonObject.get("strategy1").isString().stringValue()); // $NON-NLS-1$
                    strategy = EnumTranslator.createAndTranslate(scheduleType) + " : " + // $NON-NLS-1$
                            jsonObject.get("strategy2").isString().stringValue(); // $NON-NLS-1$
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                return strategy;
            }

        }, constants.strategySchedule());

        // Init Action Button
        table.addActionButton(new WebAdminButtonDefinition<Schedule>(constants.addSchedule()) {
            @Override
            protected UICommand resolveCommand() {
                return modelProvider.getModel().getAddCommand();
            }
        });

        table.addActionButton(new WebAdminButtonDefinition<Schedule>(constants.removeSchedule()) {
            @Override
            protected UICommand resolveCommand() {
                return modelProvider.getModel().getRemoveCommand();
            }
        });

        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                modelProvider.setSelectedItems(table.getSelectionModel().getSelectedList());
            }
        });
    }

    protected Resources getTableHeaderlessResources() {
        return (Resources) GWT.create(MainTableHeaderlessResources.class);
    }

    protected Resources getTableResources() {
        return (Resources) GWT.create(MainTableResources.class);
    }

}
