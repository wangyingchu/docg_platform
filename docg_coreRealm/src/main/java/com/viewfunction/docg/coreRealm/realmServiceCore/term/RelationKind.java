package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RelationKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable, StatisticalAndEvaluable {
    /**
     * 获取当前关系类型对象名称
     *
     * @return 关系类型对象名称
     */
    public String getRelationKindName();

    /**
     * 获取当前关系类型对象描述
     *
     * @return 关系类型对象描述
     */
    public String getRelationKindDesc();

    /**
     * 更新当前关系类型对象描述
     *
     * @param kindDesc String 新的关系类型描述
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean updateRelationKindDesc(String kindDesc);

    /**
     * 获取当前关系类型的父关系类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 关系类型对象
     */
    public RelationKind getParentRelationKind() throws CoreRealmFunctionNotSupportedException;

    /**
     * 获取当前关系类型的所有子关系类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 关系类型对象列表
     */
    public List<RelationKind> getChildRelationKinds() throws CoreRealmFunctionNotSupportedException;

    /**
     * 获取当前关系类型的所有后代关系类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 关系类型对象继承树
     */
    public InheritanceTree<RelationKind> getOffspringRelationKinds() throws CoreRealmFunctionNotSupportedException;

    /**
     * 计算当前关系类型的所有关系实体数量
     *
     * @return 关系实体数量
     */
    public Long countRelationEntities() throws CoreRealmServiceRuntimeException;

    /**
     * 计算当前关系类型（包含所有后代关系类型）的所有关系实体数量，该方法在 NEO4J 实现类型下无效
     *
     * @return 关系实体数量
     */
    public Long countRelationEntitiesWithOffspring() throws CoreRealmFunctionNotSupportedException;

    /**
     * 计算符合过滤条件的当前关系类型的关系实体对象数量
     *
     * @param attributesParameters AttributesParameters 查询过滤条件
     * @param isDistinctMode boolean 是否不允许重复数据
     *
     * @return 关系实体数量
     */
    public Long countRelationEntities(AttributesParameters attributesParameters, boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException;

    /**
     * 查询符合过滤条件的当前关系类型的关系实体对象
     *
     * @param queryParameters QueryParameters 查询过滤条件
     *
     * @return 关系实体查询结果集
     */
    public RelationEntitiesRetrieveResult getRelationEntities(QueryParameters queryParameters)  throws CoreRealmServiceEntityExploreException;

    /**
     * 删除当前关系类型的所有关系实体
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult purgeAllRelationEntities() throws CoreRealmServiceRuntimeException;

    /**
     * 查询符合过滤条件的当前概念类型的关系实体对象,并根据输入的属性类型返回相应的属性值
     *
     * @param attributeNames List<String> 属性类型列表
     * @param exploreParameters QueryParameters 查询过滤条件
     *
     * @return 关系实体属性查询结果集
     */
    public RelationEntitiesAttributesRetrieveResult getEntityAttributesByAttributeNames(List<String> attributeNames, QueryParameters exploreParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 根据唯一ID获取当前关系类型的关系实体对象
     *
     * @param relationEntityUID String 需要获取的关系实体唯一ID
     *
     * @return 关系实体对象
     */
    public RelationEntity getEntityByUID(String relationEntityUID);

    /**
     * 计算当前关系类型的所有关系实体的度分布
     *
     * @param relationDirection RelationDirection 关系关联方向
     *
     * @return 关系实体度分布信息对象
     */
    public RelationDegreeDistributionInfo computeRelationDegreeDistribution(RelationDirection relationDirection);

    /**
     * 统计使用当前关系类型关联的各个概念类型之间的实时关联信息
     *
     * @return 使用当前关系类型的各个概念类型之间的实体关联信息集合
     */
    public Set<ConceptionKindCorrelationInfo> getConceptionKindsRelationStatistics();

    /**
     * 随机获取若干当前关系类型下的关系实体
     *
     * @param entitiesCount int 需要获取的关系实体数量
     *
     * @return 关系实体集合
     */
    public Set<RelationEntity> getRandomEntities(int entitiesCount) throws CoreRealmServiceEntityExploreException;

    /**
     * 随机获取若干符合过滤条件的当前关系类型的关系实体
     *
     * @param attributesParameters AttributesParameters 查询过滤条件
     * @param isDistinctMode boolean 是否不允许重复数据
     * @param entitiesCount int 需要获取的关系实体数量
     *
     * @return 关系实体集合
     */
    public Set<RelationEntity> getRandomEntities(AttributesParameters attributesParameters,boolean isDistinctMode,int entitiesCount) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException;

    /**
     * 为当前关系类型的所有关系实体添加指定的属性，如属性已经存在，则用新的值覆盖原有属性
     *
     * @param attributes Map<String, Object> 需要添加的所有属性
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics setKindScopeAttributes(Map<String, Object> attributes) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前关系类型的关系实体，这些关系实体指向的源概念实体与目标概念实体相同 (具有相同的唯一ID)
     *
     * @return 操作删除的关系实体数量
     */
    public long purgeRelationsOfSelfAttachedConceptionEntities();

    /**
     * 删除一个当前关系类型的关系实体对象
     *
     * @param relationEntityUID String 需要删除的关系实体的唯一ID
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean deleteEntity(String relationEntityUID) throws CoreRealmServiceRuntimeException;

    /**
     * 更新多个当前关系类型的关系实体对象
     *
     * @param relationEntityUIDs List<String> 需要删除的关系实体的唯一ID列表
     *
     * @return 删除实体对象操作返回结果
     */
    public EntitiesOperationResult deleteEntities(List<String> relationEntityUIDs) throws CoreRealmServiceRuntimeException;

    /**
     * 从当前关系类型的所有关系实体中删除指定的属性
     *
     * @param attributeNames Map<String, Object> 需要删除的所有属性的名称集合
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics removeEntityAttributes(Set<String> attributeNames) throws CoreRealmServiceRuntimeException;

    /**
     * 将当前关系类型的所有关系实体中指定的属性转换为 Int 类型，如当前属性值无法合法转换则删除该属性
     *
     * @param attributeName String 需要转换的属性名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToIntType(String attributeName);

    /**
     * 将当前关系类型的所有关系实体中指定的属性转换为 Float 类型，如当前属性值无法合法转换则删除该属性
     *
     * @param attributeName String 需要转换的属性名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToFloatType(String attributeName);

    /**
     * 将当前关系类型的所有关系实体中指定的属性转换为 Boolean 类型，如当前属性值无法合法转换则删除该属性
     *
     * @param attributeName String 需要转换的属性名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToBooleanType(String attributeName);

    /**
     * 将当前关系类型的所有关系实体中指定的属性转换为 String 类型
     *
     * @param attributeName String 需要转换的属性名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToStringType(String attributeName);

    /**
     * 将当前关系类型的所有关系实体中指定的属性的 String 数据类型的表达转换为 Temporal 时间类类型
     *
     * @param attributeName String 需要转换的属性名称
     * @param dateTimeFormatter DateTimeFormatter 需要转换的属性的时间类型数据格式
     * @param temporalScaleType TemporalScaleLevel 需要转换为的时间类类型
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToTemporalType(String attributeName, DateTimeFormatter dateTimeFormatter,
                                                                            TemporalScaleCalculable.TemporalScaleLevel temporalScaleType) throws CoreRealmServiceRuntimeException;
}
