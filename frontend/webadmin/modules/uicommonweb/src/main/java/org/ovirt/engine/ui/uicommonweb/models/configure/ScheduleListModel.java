package org.ovirt.engine.ui.uicommonweb.models.configure;

import org.ovirt.engine.core.common.businessentities.Schedule;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;
import org.ovirt.engine.ui.uicompat.ConstantsManager;

@SuppressWarnings("unused")
public class ScheduleListModel extends SearchableListModel {

    private final Schedule schedule;

    public Schedule getSchedule() {
        return schedule;
    }

    public ScheduleListModel() {
        setTitle(ConstantsManager.getInstance().getConstants().scheduleTitle());
        schedule = new Schedule();
    }

    @Override
    protected String getListName() {
        return "scheduleListModel"; //$NON-NLS-1$
    }
}
