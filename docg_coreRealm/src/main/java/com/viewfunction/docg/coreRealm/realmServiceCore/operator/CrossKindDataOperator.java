package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public interface CrossKindDataOperator {
    //像花虽未红 如冰虽不冻 却像有无数说话 可惜我听不懂
    //是杯酒渐浓 或我心真空 何以感震动
    /**
     * 基于图网络拓扑的相关性计算算法
     * AdamicAdar : Adamic Adar算法。
     * CommonNeighbors : Common Neighbors算法。
     * PreferentialAttachment : Preferential Attachment算法。
     * ResourceAllocation : Resource Allocation算法。
     * TotalNeighbors : Tota lNeighbors算法。
     */
    enum TopologySimilarityComputeAlgorithm {AdamicAdar, CommonNeighbors, PreferentialAttachment, ResourceAllocation, TotalNeighbors}

    /**
     * 基于图网络拓扑的相关性计算关系方向
     * BOTH : 忽略关系方向。
     * OUTGOING : 基于出度计算。
     * INCOMING : 基于入度计算。
     */
    enum TopologySimilarityComputeDirection {BOTH, OUTGOING, INCOMING}

    /**
     * 输入一组概念实体的唯一值ID，查询并返回所有包含其中的概念实体两两间的关系实体数据
     *
     * @param conceptionEntityPairUIDs List<String> 概念实体唯一值ID列表
     *
     * @return 查询匹配的关系实体对象列表
     */
    public List<RelationEntity> getRelationsOfConceptionEntityPair(List<String> conceptionEntityPairUIDs) throws CoreRealmServiceEntityExploreException;

    /**
     * 输入一组概念实体的唯一值ID，查询并删除所有包含其中的概念实体两两间的关系实体数据
     *
     * @param conceptionEntityPairUIDs List<String> 概念实体唯一值ID列表
     * @param relationKind String 实体间关联关系的关系类型,如输入null值则忽略类型并删除所有的关系实体
     *
     * @return 删除的关系实体对象数量
     */
    public long removeRelationsOfConceptionEntityPair(List<String> conceptionEntityPairUIDs,String relationKind) throws CoreRealmServiceEntityExploreException;

    /**
     * 输入一组关系实体的唯一值ID，返回所有匹配的关系实体数据
     *
     * @param relationEntityUIDs List<String> 关系实体唯一值ID列表
     *
     * @return 匹配的关系实体对象列表
     */
    public List<RelationEntity> getRelationEntitiesByUIDs(List<String> relationEntityUIDs) throws CoreRealmServiceEntityExploreException;

    /**
     * 输入一组概念实体的唯一值ID，返回所有匹配的概念实体数据
     *
     * @param conceptionEntityUIDs List<String> 概念实体唯一值ID列表
     *
     * @return 匹配的概念实体对象列表
     */
    public List<ConceptionEntity> getConceptionEntitiesByUIDs(List<String> conceptionEntityUIDs) throws CoreRealmServiceEntityExploreException;

    /**
     * 基于图网络的拓扑关联计算两个概念实体的相似度
     *
     * @param conceptionEntityAUID String 概念实体 A 的唯一值ID
     * @param conceptionEntityBUID String 概念实体 B 的唯一值ID
     * @param topologySimilarityComputeAlgorithm TopologySimilarityComputeAlgorithm 计算使用的算法
     * @param topologySimilarityComputeDirection TopologySimilarityComputeDirection 计算使用的关系方向
     * @param relationKindForCompute String 计算使用的关系类型，如为空则基于现存的所有关系进行计算
     *
     * @return double 类型的相似度数值
     */
    public Double computeConceptionEntityPairTopologySimilarity(String conceptionEntityAUID,String conceptionEntityBUID,
                                                                TopologySimilarityComputeAlgorithm topologySimilarityComputeAlgorithm,
                                                                TopologySimilarityComputeDirection topologySimilarityComputeDirection,
                                                                String relationKindForCompute) throws CoreRealmServiceEntityExploreException,CoreRealmServiceRuntimeException;

    /**
     * 输入一组概念实体的唯一值ID和属性列表，返回所有匹配的概念实体数据的目标属性值
     *
     * @param conceptionEntityUIDs List<String> 概念实体唯一值ID列表
     * @param attributeNames List<String> 需要返回值的属性名称列表
     *
     * @return 匹配的概念实体对象的属性值列表
     */
    public List<ConceptionEntityValue> getSingleValueConceptionEntityAttributesByUIDs(List<String> conceptionEntityUIDs, List<String> attributeNames) throws CoreRealmServiceEntityExploreException;

    /**
     * 输入一组关系实体的唯一值ID和属性列表，返回所有匹配的关系实体数据的目标属性值
     *
     * @param relationEntityUIDs List<String> 关系实体唯一值ID列表
     * @param attributeNames List<String> 需要返回值的属性名称列表
     *
     * @return 匹配的关系实体对象的属性值列表
     */
    public List<RelationEntityValue> getSingleValueRelationEntityAttributesByUIDs(List<String> relationEntityUIDs, List<String> attributeNames) throws CoreRealmServiceEntityExploreException;

    /**
     * 融合两个概念类型中的属性，通过源概念类型与目标概念类型的属性值相等匹配，将匹配成功的源概念实例中的指定属性复制到对应的目标概念实例中
     *
     * @param fuseSourceKindName String 融合源概念类型名称
     * @param sourceKindMatchAttributeName String 源概念类型中执行匹配的属性名称
     * @param attributesForFusion List<String> 源概念类型中需要执行复制的属性名称列表
     * @param fuseTargetKindName String 融合目标概念类型名称
     * @param targetKindMatchAttributeName String 目标概念类型中执行匹配的属性名称
     *
     * @return 属性融合操作的执行结果统计
     */
    public EntitiesOperationResult fuseConceptionKindsAttributes(String fuseSourceKindName, String sourceKindMatchAttributeName, List<String> attributesForFusion,String fuseTargetKindName, String targetKindMatchAttributeName) throws CoreRealmServiceEntityExploreException;

    /**
     * 通过源概念类型名称与查询条件选择一组概念实体，将该组概念实体添加入额外的概念类型中
     *
     * @param sourceKindName String 源概念类型名称
     * @param attributesParameters AttributesParameters 源概念实体的查询条件
     * @param newKindNames String[] 需要加入的新概念类型名称列表
     *
     * @return 加入新概念类型操作的执行结果统计
     */
    public EntitiesOperationResult joinEntitiesToConceptionKinds(String sourceKindName, AttributesParameters attributesParameters,String[] newKindNames) throws CoreRealmServiceEntityExploreException;

    /**
     * 通过源概念类型名称与查询条件选择一组概念实体，将该组概念实体从指定的概念类型中移除
     *
     * @param sourceKindName String 源概念类型名称
     * @param attributesParameters AttributesParameters 源概念实体的查询条件
     * @param kindName String 需要从中移除的概念类型名称
     *
     * @return 移除概念类型操作的执行结果统计
     */
    public EntitiesOperationResult retreatEntitiesFromConceptionKind(String sourceKindName,AttributesParameters attributesParameters,String kindName) throws CoreRealmServiceEntityExploreException;

    /**
     * 将源概念类型中具有指定类型的关联关系的概念实体合并到目标概念类型的相关实体中，合并后源概念实体中的属性全部复制到目标概念实体中，源概念实体中的除本方法中指定类型的关联关系全部在目标概念实体中重建。最后删除所有的源概念实体，所有的目标概念实体加入源概念类型中
     *
     * @param sourceKindName String 源概念类型名称
     * @param attributesParameters AttributesParameters 源概念实体的查询条件
     * @param relationKindName String 概念实体关联关系类型
     * @param relationDirection RelationDirection 实体间关联关系的关系方向。该方向必须指定,不能为 TWO_WAY
     * @param targetKindName String 目标概念类型
     *
     * @return 合并概念类型操作的执行结果统计
     */
    public EntitiesOperationResult mergeEntitiesToConceptionKind(String sourceKindName,AttributesParameters attributesParameters,String relationKindName,RelationDirection relationDirection,String targetKindName) throws CoreRealmServiceEntityExploreException,CoreRealmServiceEntityExploreException;

    /**
     * 输入一组概念实体的唯一值ID，查询并返回具有指定关系类型的联接的所有概念实体两两间的关系实体数据
     *
     * @param conceptionEntityUIDs List<String> 概念实体唯一值ID列表
     * @param relationKind String 实体间关联关系的关系类型,如输入null值则忽略类型
     * @param returnedAttributeList List<String> 需要返回的关系实体上的属性值名称列表
     * @param relationDirection RelationDirection 实体间关联关系的关系方向,如输入null值则为 TWO_WAY 方向
     * @param targetConceptionKindName String 实体间关联关系的目标概念类型,如输入null值则忽略类型
     *
     * @return 匹配的关系实体对象的属性值列表
     */
    public List<RelationEntityValue> getRelationAttributesByEntitiesRelation(List<String> conceptionEntityUIDs, String relationKind,List<String> returnedAttributeList,RelationDirection relationDirection,String targetConceptionKindName) throws CoreRealmServiceEntityExploreException;

    /**
     * 输入一对源概念类型与目标概念类型名称和一个桥接概念类型名称以及桥接概念类型的实体查询条件，查询所有符合条件的桥接概念实体，如果这些桥接概念实体具有与其相连的源概念类型与目标概念类型的概念实体，则在源概念类型实体与目标概念类型实体之间建立由源实体指向目标实体的关联关系，关系类型名称由参数 sourceToTargetRelationKindName 决定
     *
     * @param sourceKindName String 源概念类型名称
     * @param targetKindName String 目标概念类型名称
     * @param bridgeKindName String 桥接概念类型名称
     * @param attributesParameters AttributesParameters 桥接概念类型实体查询条件
     * @param sourceToBridgeRelationKindName String 源概念类型指向桥接概念类型的关系类型名称,如输入null值则忽略类型
     * @param bridgeToTargetRelationKindName String 桥接概念类型指向目标概念类型的关系类型名称,如输入null值则忽略类型
     * @param sourceToTargetRelationKindName String 新建的源概念类型指向目标概念类型的关系类型名称,该参数为必填项,不能为null值
     * @param allowRepeat boolean 在关系类型(sourceToTargetRelationKindName)的实体已经存在的情况下,是否允许重复建立关系实体
     *
     * @return 本次操作执行抽取出的所有桥接概念实体相关的源概念类型指向目标概念类型的 sourceToTargetRelationKindName 类型的关系实体列表
     */
    public List<RelationEntity> extractRelationsFromBridgeConceptionEntities(String sourceKindName,String targetKindName, String bridgeKindName,AttributesParameters attributesParameters,String sourceToBridgeRelationKindName,String bridgeToTargetRelationKindName,String sourceToTargetRelationKindName,boolean allowRepeat) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 输入一对源概念类型与目标概念类型名称和一组桥接概念实体唯一值ID，如果这些桥接概念实体具有与其相连的源概念类型与目标概念类型的概念实体，则在源概念类型实体与目标概念类型实体之间建立由源实体指向目标实体的关联关系，关系类型名称由参数 sourceToTargetRelationKindName 决定
     *
     * @param sourceKindName String 源概念类型名称
     * @param targetKindName String 目标概念类型名称
     * @param bridgeConceptionEntityUIDs List<String> 桥接概念实体唯一值ID列表
     * @param sourceToBridgeRelationKindName String 源概念类型指向桥接概念类型的关系类型名称,如输入null值则忽略类型
     * @param bridgeToTargetRelationKindName String 桥接概念类型指向目标概念类型的关系类型名称,如输入null值则忽略类型
     * @param sourceToTargetRelationKindName String 新建的源概念类型指向目标概念类型的关系类型名称,该参数为必填项,不能为null值
     * @param allowRepeat boolean 在关系类型(sourceToTargetRelationKindName)的实体已经存在的情况下,是否允许重复建立关系实体
     *
     * @return 本次操作执行抽取出的所有桥接概念实体相关的源概念类型指向目标概念类型的 sourceToTargetRelationKindName 类型的关系实体列表
     */
    public List<RelationEntity> extractRelationsFromBridgeConceptionEntities(String sourceKindName,String targetKindName, List<String> bridgeConceptionEntityUIDs,String sourceToBridgeRelationKindName,String bridgeToTargetRelationKindName,String sourceToTargetRelationKindName,boolean allowRepeat) throws CoreRealmServiceRuntimeException;

    /**
     * 输入一组关系实体唯一值ID，将每一个关系实体替换为一个新的居中概念实体以及两个新的关联关系，新的关联关系分别从原关系实体的 From节点指向居中概念实体，从居中概念实体指向原关系实体的 To节点. 原关系实体中的所有属性会被复制到居中概念实体中，原关系实体本身被删除.
     *
     * @param relationEntityUIDs List<String> 目标概念实体唯一值ID列表
     * @param intermediateConceptionKindName String 居中概念实体类型名称,不能为null值
     * @param fromRelationKind String 由居中概念实体出发的关系实体的关系类型名称,不能为null值
     * @param toRelationKind String 指向居中概念实体的关系实体的关系类型名称,不能为null值
     *
     * @return 新建的概念实体实例列表
     */
    public List<ConceptionEntity> extractIntermediateConceptionEntitiesFromRelations(List<String> relationEntityUIDs,String intermediateConceptionKindName,String fromRelationKind,String toRelationKind) throws CoreRealmServiceRuntimeException;

    /**
     * 输入一组概念实体的唯一值ID，删除这些实体并使用指定的关系类型创建替代的关联关系来连接所有与这些实体连接的其他概念实体
     *
     * @param conceptionEntityUIDs List<String> 概念实体唯一值ID列表
     * @param relationKindName String 新建的替代用关联关系的关系类型,该参数为必填项,不能为null值
     *
     * @return 新建的关系实体列表
     */
    public List<RelationEntity> collapseConceptionEntities(List<String> conceptionEntityUIDs, String relationKindName) throws CoreRealmServiceRuntimeException;

    /**
     * 输入一组关系实体的唯一值ID，将这些实体的关系类型名称改为 newRelationKind 参数中指定的新类型
     *
     * @param relationEntityUIDs List<String> 关系实体唯一值ID列表
     * @param newRelationKind String 需要修改为的新关系类型的名称,该参数为必填项,不能为null值
     *
     * @return 新建的关系实体列表
     */
    public List<RelationEntity> changeEntitiesRelationKind(List<String> relationEntityUIDs,String newRelationKind) throws CoreRealmServiceRuntimeException;

    /**
     * 输入一组概念实体的唯一值ID，将其中类型名称为 oldConceptionKind 的实体的概念类型改为 newConceptionKind 指定的新类型
     *
     * @param conceptionEntityUIDs List<String> 概念实体唯一值ID列表
     * @param oldConceptionKind String 需要修改的实体的概念类型名称,该参数为必填项,不能为null值
     * @param newConceptionKind String 需要改为的新概念类型的名称,该参数为必填项,不能为null值
     *
     * @return 修改过的的概念实体列表
     */
    public List<ConceptionEntity> changeEntitiesConceptionKind(List<String> conceptionEntityUIDs,String oldConceptionKind,String newConceptionKind) throws CoreRealmServiceRuntimeException;

    /**
     * 输入一组关系实体的唯一值ID，将这些实体所关联的概念实体的指向反转，反转后输入的关系实体的唯一值ID将会重新生成，原唯一值ID失效
     *
     * @param relationEntityUIDs List<String> 关系实体唯一值ID列表
     *
     * @return 反转后的关系实体列表
     */
    public List<RelationEntity> invertRelationEntitiesDirection(List<String> relationEntityUIDs) throws CoreRealmServiceRuntimeException;

    /**
     * 输入一组关系实体的唯一值ID和一个概念实体唯一值ID，根据 relationDirection 指定的关系方向，将关系重新定向到概念实体唯一值ID表示的概念实体上
     *
     * @param relationEntityUIDs List<String> 关系实体唯一值ID列表
     * @param targetConceptionEntityUID String 目标概念实体唯一值ID,该参数为必填项,不能为null值
     * @param relationDirection RelationDirection 需要重定向的关联关系方向,该参数为必填项,不能为null值，且不能为 TWO-WAY 方向
     *
     * @return 重定向后的关系实体列表
     */
    public List<RelationEntity> redirectRelationsToNewConceptionEntity(List<String> relationEntityUIDs,String targetConceptionEntityUID,RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;

    /**
     * 输入一个保留概念实体唯一值ID和一组待合并概念实体唯一值ID列表，将所有待合并概念实体中的属性与关联关系合并到保留概念实体中并删除所有待合并概念实体
     *
     * @param remainsConceptionEntityUID String 保留概念实体唯一值ID,该参数为必填项,不能为null值
     * @param mergedConceptionEntitiesUIDs List<String> 待合并概念实体唯一值ID列表
     *
     * @return 合并操作完成后的保留概念实体实例
     */
    public ConceptionEntity mergeConceptionEntities(String remainsConceptionEntityUID,List<String> mergedConceptionEntitiesUIDs) throws CoreRealmServiceRuntimeException;

    /**
     * 输入一个保留关系实体唯一值ID和一组待合并关系实体唯一值ID列表，所有这些关系实体都必须有相同的起点和终点概念实体。将所有待合并关系实体中的属性合并到保留关系实体中并删除所有待合并关系实体,保留关系实体的关系类型不变
     *
     * @param remainsRelationEntityUID String 保留关系实体唯一值ID,该参数为必填项,不能为null值
     * @param mergedRelationEntitiesUIDs List<String> 待合并关系实体唯一值ID列表
     *
     * @return 合并操作完成后的保留关系实体实例
     */
    public RelationEntity mergeRelationEntities(String remainsRelationEntityUID,List<String> mergedRelationEntitiesUIDs) throws CoreRealmServiceRuntimeException;
}
