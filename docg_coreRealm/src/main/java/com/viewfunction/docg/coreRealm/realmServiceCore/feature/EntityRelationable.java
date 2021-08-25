package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.RelationKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.ResultEntitiesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;
import java.util.Map;

public interface EntityRelationable {

    public enum JumpStopLogic {
        AT,TO }

    /**
     * 计算当前实体的所有数据关联数量
     *
     * @return 关联数量
     */
    public Long countAllRelations();

    /**
     * 计算当前实体的特定关系类型下的数据关联数量
     *
     * @param relationType String 关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     *
     * @return 符合条件的关联数量
     */
    public Long countAllSpecifiedRelations(String relationType, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;

    /**
     * 计算当前实体的符合特定查询条件的数据关联数量
     *
     * @param exploreParameters QueryParameters 关系属性查询条件
     * @param relationDirection RelationDirection 关系关联方向
     *
     * @return 符合条件的关联数量
     */
    public Long countSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;

    /**
     * 获取与当前实体相关的所有数据关系实体
     *
     * @return 关系实体列表
     */
    public List<RelationEntity> getAllRelations();

    /**
     * 获取当前实体的特定关系类型下的关系实体
     *
     * @param relationKind String 关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     *
     * @return 关系实体列表
     */
    public List<RelationEntity> getAllSpecifiedRelations(String relationKind, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前实体的符合特定查询条件的关系实体
     *
     * @param exploreParameters QueryParameters 关系属性查询条件
     * @param relationDirection RelationDirection 关系关联方向
     *
     * @return 关系实体列表
     */
    public List<RelationEntity> getSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前实体附着源数据关联
     *
     * @param targetRelationableUID String 目标实体唯一ID
     * @param relationKind String 关系类型名称
     * @param initRelationProperties Map<String,Object> 新建的关系实体上的初始属性信息
     * @param repeatable boolean 是否允许重复建立已有关系类型的数据关联
     *
     * @return 新建的关系实体
     */
    public RelationEntity attachFromRelation(String targetRelationableUID, String relationKind, Map<String,Object> initRelationProperties, boolean repeatable) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前实体附着目标数据关联
     *
     * @param targetRelationableUID String 目标实体唯一ID
     * @param relationKind String 关系类型名称
     * @param initRelationProperties Map<String,Object> 新建的关系实体上的初始属性信息
     * @param repeatable boolean 是否允许重复建立已有关系类型的数据关联
     *
     * @return 新建的关系实体
     */
    public RelationEntity attachToRelation(String targetRelationableUID, String relationKind, Map<String,Object> initRelationProperties, boolean repeatable) throws CoreRealmServiceRuntimeException;

    /**
     * 根据关系实体唯一ID删除当前实体的特定数据关联
     *
     * @param relationEntityUID String 需要删除的关系实体唯一ID
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean detachRelation(String relationEntityUID) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前实体的所有数据关联
     *
     * @return 删除成功的关系实体唯一ID列表
     */
    public List<String> detachAllRelations();

    /**
     * 删除当前实体的特定关系类型下的关系实体
     *
     * @param relationType String 关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     *
     * @return 删除成功的关系实体唯一ID列表
     */
    public List<String> detachAllSpecifiedRelations(String relationType, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前实体的符合特定查询条件的关系实体
     *
     * @param exploreParameters QueryParameters 关系属性查询条件
     * @param relationDirection RelationDirection 关系关联方向
     *
     * @return 删除成功的关系实体唯一ID列表
     */
    public List<String> detachSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;

    /**
     * 计算与当前实体关联的概念实体数量
     *
     * @param targetConceptionKind String 目标概念类型名称
     * @param relationKind String 目标关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     * @param maxJump int 关联传播的最大跳数
     *
     * @return 符合条件的概念实体数量
     */
    public Long countRelatedConceptionEntities(String targetConceptionKind, String relationKind,RelationDirection relationDirection,int maxJump);

    /**
     * 获取与当前实体关联的概念实体
     *
     * @param targetConceptionKind String 目标概念类型名称
     * @param relationKind String 目标关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     * @param maxJump int 关联传播的最大跳数
     *
     * @return 符合条件的概念实体对象列表
     */
    public List<ConceptionEntity> getRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump);

    /**
     * 获取与当前实体关联的概念实体,并根据输入的属性类型返回相应的属性值
     *
     * @param targetConceptionKind String 目标概念类型名称
     * @param attributeNames List<String> 返回属性类型列表
     * @param relationKind String 目标关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     * @param maxJump int 关联传播的最大跳数
     *
     * @return 概念实体属性查询结果集
     */
    public ConceptionEntitiesAttributesRetrieveResult getAttributesOfRelatedConceptionEntities(String targetConceptionKind, List<String> attributeNames,String relationKind, RelationDirection relationDirection, int maxJump);

    /**
     * 计算与当前实体关联的概念实体数量
     *
     * @param targetConceptionKind String 目标概念类型名称
     * @param relationKind String 目标关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     * @param maxJump int 关联传播的最大跳数
     * @param relationAttributesParameters AttributesParameters 需要获取的数据的关系实体属性查询条件
     * @param conceptionAttributesParameters AttributesParameters 需要获取的数据的概念实体属性查询条件
     * @param isDistinctMode boolean 是否不允许重复数据
     *
     * @return 符合条件的概念实体数量
     */
    public Long countRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump,
                                               AttributesParameters relationAttributesParameters, AttributesParameters conceptionAttributesParameters, boolean isDistinctMode) throws CoreRealmServiceEntityExploreException;

    /**
     * 获取与当前实体关联的概念实体
     *
     * @param targetConceptionKind String 目标概念类型名称
     * @param relationKind String 目标关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     * @param maxJump int 关联传播的最大跳数
     * @param relationAttributesParameters AttributesParameters 需要获取的数据的关系实体属性查询条件
     * @param conceptionAttributesParameters AttributesParameters 需要获取的数据的概念实体属性查询条件
     * @param resultEntitiesParameters ResultEntitiesParameters 返回概念实体数据的控制参数
     *
     * @return 符合条件的概念实体对象列表
     */
    public List<ConceptionEntity> getRelatedConceptionEntities(String targetConceptionKind, String relationKind, RelationDirection relationDirection, int maxJump,
                                                               AttributesParameters relationAttributesParameters, AttributesParameters conceptionAttributesParameters, ResultEntitiesParameters resultEntitiesParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 获取与当前实体关联的概念实体,并根据输入的属性类型返回相应的属性值
     *
     * @param targetConceptionKind String 目标概念类型名称
     * @param attributeNames List<String> 返回属性类型列表
     * @param relationKind String 目标关系类型名称
     * @param relationDirection RelationDirection 关系关联方向
     * @param maxJump int 关联传播的最大跳数
     * @param relationAttributesParameters AttributesParameters 需要获取的数据的关系实体属性查询条件
     * @param conceptionAttributesParameters AttributesParameters 需要获取的数据的概念实体属性查询条件
     * @param resultEntitiesParameters ResultEntitiesParameters 返回概念实体数据的控制参数
     *
     * @return 概念实体属性查询结果集
     */
    public ConceptionEntitiesAttributesRetrieveResult getAttributesOfRelatedConceptionEntities(String targetConceptionKind, List<String> attributeNames,String relationKind, RelationDirection relationDirection, int maxJump,
                                                               AttributesParameters relationAttributesParameters, AttributesParameters conceptionAttributesParameters, ResultEntitiesParameters resultEntitiesParameters) throws CoreRealmServiceEntityExploreException;

    public List<ConceptionEntity> getRelatedConceptionEntities(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch, JumpStopLogic jumpStopLogic, int jumpNumber,
                                                               AttributesParameters conceptionAttributesParameters, ResultEntitiesParameters resultEntitiesParameters) throws CoreRealmServiceEntityExploreException;

    public Long countRelatedConceptionEntities(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,JumpStopLogic jumpStopLogic,int jumpNumber,
                                                                     AttributesParameters conceptionAttributesParameters) throws CoreRealmServiceEntityExploreException;

    //public Map<Long,List<ConceptionEntity>> getRelatedConceptionEntitiesByJump(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,int jumpNumber,
    //                                                           AttributesParameters conceptionAttributesParameters, ResultEntitiesParameters resultEntitiesParameters) throws CoreRealmServiceEntityExploreException;
}
