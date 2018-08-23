package org.ovirt.engine.core.common.businessentities;

public enum ScheduleType {
    VmBackup,
    VmSnapshot,
    VmTemplate,
    VmRun,
    VmSuspend,
    VmStop,
    VmReboot,

    VDSStart,
    VDSStop,
    VDSReboot,
    VDSMaintenance,
    VDSActivate,

    Time,
    Day,
    Month,
    Weekly,

    SearchVM,
    SearchVDS;

    public int getValue() {
        return this.ordinal();
    }

    public static ScheduleType forValue(int value) {
        return values()[value];
    }

    public static ScheduleType getSearchType(ScheduleType scheduleType) {
        ScheduleType resultScheduleType = null;
        if( scheduleType == ScheduleType.VmBackup ||
            scheduleType == ScheduleType.VmSnapshot ||
            scheduleType == ScheduleType.VmTemplate ||
            scheduleType == ScheduleType.VmRun ||
            scheduleType == ScheduleType.VmSuspend ||
            scheduleType == ScheduleType.VmReboot ||
            scheduleType == ScheduleType.VmStop ) {
            resultScheduleType =  SearchVM;
        } else if( scheduleType == ScheduleType.VDSStart ||
            scheduleType == ScheduleType.VDSStop ||
            scheduleType == ScheduleType.VDSReboot ||
            scheduleType == ScheduleType.VDSMaintenance ||
            scheduleType == ScheduleType.VDSActivate ) {
            resultScheduleType =  SearchVDS;
        }
        return resultScheduleType;
    }
}
