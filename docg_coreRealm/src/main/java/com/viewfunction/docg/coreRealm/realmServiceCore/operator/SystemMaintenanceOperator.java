package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DataStatusSnapshotInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.SystemStatusSnapshotInfo;

public interface SystemMaintenanceOperator {

    public DataStatusSnapshotInfo getDataStatusSnapshot();

    public SystemStatusSnapshotInfo getSystemStatusSnapshot();
}
