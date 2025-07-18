package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
     * 查询所有概念类型的实体数据中的属性的系统信息
     *
     * @return 所有的概念类型名称与其中包含的属性系统信息列表的映射字典
     */
    public Map<String,List<AttributeSystemInfo>> getAllConceptionKindsAttributesSystemInfo();

    /**
     * 查询指定关系类型的实体数据中的属性的系统信息
     *
     * @param relationKindName String 关系类型名称
     *
     * @return 属性系统信息列表
     */
    public List<AttributeSystemInfo> getRelationKindAttributesSystemInfo(String relationKindName);

    /**
     * 查询所有关系类型的实体数据中的属性的系统信息
     *
     * @return 所有的关系类型名称与其中包含的属性系统信息列表的映射字典
     */
    public Map<String,List<AttributeSystemInfo>> getAllRelationKindsAttributesSystemInfo();

    /**
     * 在当前领域模型中创建针对概念类型的搜索索引
     *
     *  @param indexName String 搜索索引名称
     *  @param conceptionKindName String 搜索概念类型名称
     *  @param indexAttributeNames Set<String> 索引包含的数据属性集合
     *
     * @return 如创建索引成功返回 true
     */
    public boolean createConceptionKindSearchIndex(String indexName, String conceptionKindName, Set<String> indexAttributeNames) throws CoreRealmServiceRuntimeException;

    /**
     * 在当前领域模型中创建针对关系类型的搜索索引
     *
     *  @param indexName String 搜索索引名称
     *  @param relationKindName String 搜索关系类型名称
     *  @param indexAttributeNames Set<String> 索引包含的数据属性集合
     *
     * @return 如创建索引成功返回 true
     */
    public boolean createRelationKindSearchIndex(String indexName, String relationKindName, Set<String> indexAttributeNames) throws CoreRealmServiceRuntimeException;

    /**
     * 显示当前领域模型中的所有概念类型搜索索引
     *
     * @return 概念类型搜索索引信息集合
     */
    public Set<SearchIndexInfo> listConceptionKindSearchIndex();

    /**
     * 显示当前领域模型中的所有关系类型搜索索引
     *
     * @return 关系类型搜索索引信息集合
     */
    public Set<SearchIndexInfo> listRelationKindSearchIndex();

    /**
     * 在当前领域模型中删除指定的概念类型搜索索引
     *
     *  @param indexName String 待删除的搜索索引名称
     *
     * @return 如删除索引成功返回 true
     */
    public boolean removeConceptionKindSearchIndex(String indexName) throws CoreRealmServiceRuntimeException;

    /**
     * 在当前领域模型中删除指定的关系类型搜索索引
     *
     *  @param indexName String 待删除的搜索索引名称
     *
     * @return 如删除索引成功返回 true
     */
    public boolean removeRelationKindSearchIndex(String indexName) throws CoreRealmServiceRuntimeException;

    /**
     * 统计系统中所有概念类型实体与其他概念类型实体之间的实时关联关系信息
     *
     * @return 所有概念类型之间的关联关系信息集合
     */
    public Set<ConceptionKindCorrelationInfo> getSystemConceptionKindsRelationDistributionStatistics();

    /**
     * 统计系统中包含内部概念类型实体在内的所有概念类型实体与其他概念类型实体之间的实时关联关系信息,此方法不计算各个概念类型之间关系实体的真实数量,relationEntityCount值设置为1
     *
     * @return 所有概念类型之间的关联关系信息集合
     */
    public Set<ConceptionKindCorrelationInfo> getAllDataRelationDistributionStatistics();

    /**
     * 统计系统中包含内部概念类型实体在内的所有概念类型实体与其他概念类型实体之间的实时关联关系信息,此方法实时计算各个概念类型之间关系实体的真实数量
     *
     * @return 所有概念类型之间的关联关系信息集合
     */
    public Set<ConceptionKindCorrelationInfo> getAllDataRelationDistributionDetailStatistics();

    /**
     * 获取系统中所有概念类型实体与其他概念类型实体之间的实时关联关系信息,此方法实时计算各个概念类型之间关系实体的真实数量
     *
     *  @param samplingRate float 执行计算时对关系实体数量进行采样的比例，取值范围(0,1]
     *
     * @return 所有概念类型之间的关联关系信息集合
     */
    public Set<ConceptionKindCorrelationInfo> getConceptionKindCorrelationRuntimeInfo(float samplingRate);

    /**
     * 查询系统中实时存在的所有属性名称，其范围也包含不在系统属性类型(AttributeKind)定义管理之外的其他所有属性
     *
     * @return 属性名称集合
     */
    public Set<String> getRealtimeAttributesStatistics();

    /**
     * 输入指定属性名称（该属性不需要已经在属性类型中定义），查询系统中该属性在各个概念类型的实体中实时存在的真实数量
     *
     *  @param attributeName String 需要查询的属性名称
     *
     * @return 属性分布数据信息Map， Key为概念类型名称Set（对应单一概念实体隶属于多个概念类型的情况），Value为该概念类型Set中属性存在的数量
     */
    public Map<Set<String>,Long> getConceptionAttributeValueDistributionStatistic(String attributeName);

    /**
     * 输入指定属性名称（该属性不需要已经在属性类型中定义），查询系统中该属性在各个关系类型的实体中实时存在的真实数量
     *
     *  @param attributeName String 需要查询的属性名称
     *
     * @return 属性分布数据信息Map， Key为关系类型名称，Value为该关系类型中属性存在的数量
     */
    public Map<String,Long> getRelationAttributeValueDistributionStatistic(String attributeName);

    /**
     * 获取与所有分类关联的各类型数据的实时统计信息
     *
     * @return 分类实时统计信息
     */
    public GlobalClassificationsRuntimeStatistics getGlobalClassificationsRuntimeStatistics();

    /**
     * 在当前领域模型中生成针对时间流的搜索索引以提高时间流相关的查询统计性能
     *
     * @return 创建成功的索引名称集合 Set<String>
     */
    public Set<String> generateTimeFlowSearchIndexes() throws CoreRealmServiceRuntimeException;

    /**
     * 在当前领域模型中生成针对地理空间区域范围的搜索索引以提高地理空间区域范围相关的查询统计性能
     *
     * @return 创建成功的索引名称集合 Set<String>
     */
    public Set<String> generateGeospatialRegionSearchIndexes() throws CoreRealmServiceRuntimeException;

    /**
     * 查询所有概念类型中实体数据具有的数据操作能力统计信息
     *
     * @return 所有的概念类型名称与其中概念实体数据可以执行的数据操作能力统计的映射字典
     */
    public Map<String,ConceptionKindDataCapabilityInfo> getConceptionKindsDataCapabilityStatistics();

    /**
     * 查询指定概念类型中实体数据具有的数据操作能力统计信息
     *
     *  @param targetConceptionKindNameList List<String> 需要执行统计的概念类型名称列表
     *
     * @return 所有的概念类型名称与其中概念实体数据可以执行的数据操作能力统计的映射字典
     */
    public Map<String,ConceptionKindDataCapabilityInfo> getConceptionKindsDataCapabilityStatistics(List<String> targetConceptionKindNameList);

    /**
     * 在当前领域模型中为指定的 EXTERNAL_VALUE 数据存储结构的属性视图类型注册数据存取处理器
     *
     *  @param attributesViewKind AttributesViewKind 注册操作目标属性视图类型
     *  @param accessProcessorID String 数据存取处理器ID
     *
     * @return 注册数据存取处理器操作结果
     */
    public boolean registerExternalAttributesValueAccessProcessor(AttributesViewKind attributesViewKind, String accessProcessorID) throws CoreRealmServiceRuntimeException;

    /**
     * 统计操作类型
     */
    public enum OperationType {Create,Update}

    /**
     * 统计时间周期
     */
    public enum OperationPeriod {Year,Month,Day}

    /**
     * 统计类型分类
     */
    public enum kindType {ConceptionKind,RelationKind}

    /**
     * 按照时间周期统计系统中所有类型实体数据的创建、更新操作次数
     *
     *  @param kindName String 类型名称，如输入为 null 则默认统计所有类型实体数据
     *  @param kindType kindType 需要执行统计的类型分类,不可为空
     *  @param operationType OperationType 需要执行统计的操作类型,不可为空
     *  @param operationType OperationType 需要执行统计的时间周期,不可为空
     *
     * @return 每个时间周期间隔中统计到的操作实体数量
     */
    public Map<LocalDate,Long> getKindEntitiesPeriodicOperationStatic(String kindName, kindType kindType, OperationType operationType, OperationPeriod operationPeriod) throws CoreRealmServiceRuntimeException;
}
