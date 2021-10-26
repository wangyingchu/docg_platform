package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeSystemInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DataStatusSnapshotInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.SearchIndexInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.SystemStatusSnapshotInfo;

import java.util.List;
import java.util.Set;

public interface SystemMaintenanceOperator {
    //听风雪喧嚷 看流星在飞翔 我的心向我呼唤 去动荡的远方
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

    public enum SearchIndexType {BTREE, FULLTEXT, LOOKUP}

    public boolean createConceptionKindSearchIndex(String indexName, SearchIndexType indexType, String conceptionKindName, Set<String> indexAttributeNames) throws CoreRealmServiceRuntimeException;

    public boolean createRelationKindSearchIndex(String indexName, SearchIndexType indexType, String relationKindName, Set<String> indexAttributeNames) throws CoreRealmServiceRuntimeException;

    public Set<SearchIndexInfo> listConceptionKindSearchIndex();

    public Set<SearchIndexInfo> listRelationKindSearchIndex();

    public boolean removeConceptionKindSearchIndex(String indexName) throws CoreRealmServiceRuntimeException;

    public boolean removeRelationKindSearchIndex(String indexName) throws CoreRealmServiceRuntimeException;

}
