package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeSystemInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DataStatusSnapshotInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.SystemStatusSnapshotInfo;

import java.util.List;

public interface SystemMaintenanceOperator {

    /**
     * 查询并返回当前领域模型所属数据服务系统的事实数据状态快照信息
     *
     * @return 数据状态实时快照信息
     */
    public DataStatusSnapshotInfo getDataStatusSnapshot();

    /**
     * 查询并返回当前领域模型所属数据服务系统的系统运行状态快照信息
     *
     * @return 系统运行状态快照信息
     */
    public SystemStatusSnapshotInfo getSystemStatusSnapshot();

    /**
     * 查询指定概念类型的实体数据中的属性的系统信息
     *
     * @param conceptionKindName String 概念类型名称
     *
     * @return 属性系统信息列表
     */
    public List<AttributeSystemInfo> getConceptionKindAttributesSystemInfo(String conceptionKindName);

    /**
     * 查询指定关系类型的实体数据中的属性的系统信息
     *
     * @param relationKindName String 关系类型名称
     *
     * @return 属性系统信息列表
     */
    public List<AttributeSystemInfo> getRelationKindAttributesSystemInfo(String relationKindName);
}
