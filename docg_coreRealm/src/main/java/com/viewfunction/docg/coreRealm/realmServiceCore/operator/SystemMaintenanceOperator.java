package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeSystemInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DataStatusSnapshotInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.SystemStatusSnapshotInfo;

import java.util.List;

public interface SystemMaintenanceOperator {

    public DataStatusSnapshotInfo getDataStatusSnapshot();

    public SystemStatusSnapshotInfo getSystemStatusSnapshot();

    public List<AttributeSystemInfo> getConceptionKindAttributesSystemInfo(String conceptionKindName);

    public List<AttributeSystemInfo> getRelationKindAttributesSystemInfo(String relationKindName);
}
