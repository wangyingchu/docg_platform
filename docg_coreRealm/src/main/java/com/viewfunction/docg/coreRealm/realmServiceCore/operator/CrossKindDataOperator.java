package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;
import java.util.Set;

public interface CrossKindDataOperator {

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

    public EntitiesOperationResult fuseConceptionKindsAttributes(String fuseSourceKindName, String sourceKindMatchAttributeName, Set<String> attributesForFusion,String fuseTargetKindName, String targetKindMatchAttributeName) throws CoreRealmServiceEntityExploreException;

}
