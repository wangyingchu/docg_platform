package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.EntitiesExchangeOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.ClassificationMetaInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;

import java.util.List;
import java.util.Map;

public interface CoreRealm {
    /* 不要说我们一无所有，我们要做天下的主人 */
    /**
     * 获取底层图数据库的实现技术，可能的类型有 NEO4J ,TUGRAPH 或 MEMGRAPH
     *
     * @return 底层图数据库实现技术枚举
     */
    public CoreRealmStorageImplTech getStorageImplTech();

    /**
     * 获取核心领域名称
     *
     * @return 核心领域名称
     */
    public String getCoreRealmName();

    /**
     * 根据名称获取概念类型对象
     *
     * @param conceptionKindName String 需要获取的概念类型名称
     *
     * @return 概念类型对象
     */
    public ConceptionKind getConceptionKind(String conceptionKindName);

    /**
     * 创建新的概念类型
     *
     * @param conceptionKindName String 需要创建的概念类型名称，不能与已有概念类型重名
     * @param conceptionKindDesc String 需要创建的概念类型描述
     *
     * @return 概念类型对象
     */
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc);

    /**
     * 创建新的概念类型并指定父概念类型，该方法在 NEO4J 实现类型下无效
     *
     * @param conceptionKindName String 需要创建的概念类型名称，不能与已有概念类型重名,非空输入值
     * @param conceptionKindDesc String 需要创建的概念类型描述
     * @param parentConceptionKindName String 父概念类型名称
     *
     * @return 概念类型对象
     */
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc,String parentConceptionKindName)
            throws CoreRealmFunctionNotSupportedException;

    /**
     * 根据名称删除概念类型
     *
     * @param conceptionKindName String 需要删除的概念类型名称
     * @param deleteExistEntities boolean 是否删除该概念类型的全部已经存在的概念实体对象
     *
     * @return 如果操作成功返回 true
     */
    public boolean removeConceptionKind(String conceptionKindName,boolean deleteExistEntities) throws CoreRealmServiceRuntimeException;

    /**
     * 重命名指定的概念类型名称及描述，操作完成后该类型下所有的概念实体的类型名称均会改为新的类型名称
     *
     * @param originalConceptionKindName String 原概念类型名称，必须为已存在概念类型,非空输入值
     * @param newConceptionKindName String 需要修改为的概念类型名称，不能与已有概念类型重名,非空输入值
     * @param newConceptionKindDesc String 需要修改为的概念类型描述
     *
     * @return 如果操作成功返回 true
     */
    public boolean renameConceptionKind(String originalConceptionKindName,String newConceptionKindName,String newConceptionKindDesc)
            throws CoreRealmServiceRuntimeException;

    /**
     * 根据输入的自定义配置项内容，匹配条件查询概念类型对象
     *
     * @param itemName String 需要查询的自定义配置项名称
     * @param itemValue Object 需要查询的的自定义配置项值,如该值输入非空,则执行精确值匹配。如该值输入为空,则忽略具体值,执行自定义配置项存在查询
     *
     * @return 概念类型对象列表
     */
    public List<ConceptionKind> getConceptionKindsByMetaConfigItemMatch(String itemName,Object itemValue);

    /**
     * 根据对象唯一ID获取属性视图类型对象
     *
     * @param attributesViewKindUID String 需要获取的属性视图类型唯一ID
     *
     * @return 属性视图类型对象
     */
    public AttributesViewKind getAttributesViewKind(String attributesViewKindUID);

    /**
     * 创建新的属性视图类型
     *
     * @param attributesViewKindName String 需要创建的属性视图类型名称，能够与已有属性视图类型重名,非空输入值
     * @param attributesViewKindDesc String 需要创建的属性视图类型描述
     * @param attributesViewKindDataForm AttributesViewKindDataForm 需要创建的属性视图类型的数据存储结构,默认为 SINGLE_VALUE 类型
     *
     * @return 属性视图类型对象
     */
    public AttributesViewKind createAttributesViewKind(String attributesViewKindName,String attributesViewKindDesc, AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm);

    /**
     * 根据唯一ID删除属性视图类型
     *
     * @param attributesViewKindUID String 需要删除的属性视图类型唯一ID
     *
     * @return 如果操作成功返回 true
     */
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;

    /**
     * 根据输入条件查询属性视图类型对象,所有查询条件均为可选输入，各个查询条件精确匹配输入值，互相间为 AND 的组合关系
     *
     * @param attributesViewKindName String 需要查询的属性视图类型名称
     * @param attributesViewKindDesc String 需要查询的的属性视图类型描述
     * @param attributesViewKindDataForm AttributesViewKindDataForm 需要查询的属性视图类型的数据存储结构
     *
     * @return 属性视图类型对象列表
     */
    public List<AttributesViewKind> getAttributesViewKinds(String attributesViewKindName, String attributesViewKindDesc, AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm);

    /**
     * 根据输入的自定义配置项内容，匹配条件查询属性视图类型对象
     *
     * @param itemName String 需要查询的自定义配置项名称
     * @param itemValue Object 需要查询的的自定义配置项值,如该值输入非空,则执行精确值匹配。如该值输入为空,则忽略具体值,执行自定义配置项存在查询
     *
     * @return 属性视图类型对象列表
     */
    public List<AttributesViewKind> getAttributesViewKindsByMetaConfigItemMatch(String itemName,Object itemValue);

    /**
     * 根据对象唯一ID获取属性类型对象
     *
     * @param attributeKindUID String 需要获取的属性类型唯一ID
     *
     * @return 属性类型对象
     */
    public AttributeKind getAttributeKind(String attributeKindUID);

    /**
     * 创建新的属性类型
     *
     * @param attributeKindName String 需要创建的属性类型名称，能够与已有属性类型重名,非空输入值
     * @param attributeKindDesc String 需要创建的属性类型描述
     * @param attributeDataType AttributeDataType 需要创建的属性类型的数据类型,非空输入值
     *
     * @return 属性类型对象
     */
    public AttributeKind createAttributeKind(String attributeKindName,String attributeKindDesc, AttributeDataType attributeDataType);

    /**
     * 根据唯一ID删除属性类型
     *
     * @param attributeKindUID String 需要删除的属性类型唯一ID
     *
     * @return 如果操作成功返回 true
     */
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;

    /**
     * 根据输入条件查询属性类型对象,所有查询条件均为可选输入，各个查询条件精确匹配输入值，互相间为 AND 的组合关系
     *
     * @param attributeKindName String 需要查询的属性类型名称
     * @param attributeKindDesc String 需要查询的的属性类型描述
     * @param attributeDataType AttributesViewKindDataForm 需要查询的属性类型的数据类型
     *
     * @return 属性类型对象列表
     */
    public List<AttributeKind> getAttributeKinds(String attributeKindName,String attributeKindDesc,AttributeDataType attributeDataType);

    /**
     * 根据输入的自定义配置项内容，匹配条件查询属性类型对象
     *
     * @param itemName String 需要查询的自定义配置项名称
     * @param itemValue Object 需要查询的的自定义配置项值,如该值输入非空,则执行精确值匹配。如该值输入为空,则忽略具体值,执行自定义配置项存在查询
     *
     * @return 属性类型对象列表
     */
    public List<AttributeKind> getAttributeKindsByMetaConfigItemMatch(String itemName,Object itemValue);

    /**
     * 根据名称获取关系类型对象
     *
     * @param relationKindName String 需要获取的关系类型名称
     *
     * @return 关系类型对象
     */
    public RelationKind getRelationKind(String relationKindName);

    /**
     * 创建新的关系类型
     *
     * @param relationKindName String 需要创建的关系类型名称，不能与已有关系类型重名
     * @param relationKindDesc String 需要创建的关系类型描述
     *
     * @return 关系类型对象
     */
    public RelationKind createRelationKind(String relationKindName,String relationKindDesc);

    /**
     * 创建新的关系类型并指定父关系类型，该方法在 NEO4J 实现类型下无效
     *
     * @param relationKindName String 需要创建的关系类型名称，不能与已有关系类型重名,非空输入值
     * @param relationKindDesc String 需要创建的关系类型描述
     * @param parentRelationKindName String 父关系类型名称
     *
     * @return 关系类型对象
     */
    public RelationKind createRelationKind(String relationKindName,String relationKindDesc,String parentRelationKindName)
            throws CoreRealmFunctionNotSupportedException;

    /**
     * 重命名指定的关系类型名称及描述，操作完成后该类型下所有的关系实体的类型名称均会改为新的类型名称
     *
     * @param originalRelationKindName String 原关系类型名称，必须为已存在概念类型,非空输入值
     * @param newRelationKindName String 需要修改为的关系类型名称，不能与已有关系类型重名,非空输入值
     * @param newRelationKindDesc String 需要修改为的关系类型描述
     *
     * @return 如果操作成功返回 true
     */
    public boolean renameRelationKind(String originalRelationKindName,String newRelationKindName,String newRelationKindDesc)
            throws CoreRealmServiceRuntimeException;

    /**
     * 根据名称删除关系类型
     *
     * @param relationKindName String 需要删除的关系类型名称
     * @param deleteExistEntities boolean 是否删除该关系类型的全部已经存在的关系实体对象
     *
     * @return 如果操作成功返回 true
     */
    public boolean removeRelationKind(String relationKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException;

    /**
     * 根据输入的自定义配置项内容，匹配条件查询关系类型对象
     *
     * @param itemName String 需要查询的自定义配置项名称
     * @param itemValue Object 需要查询的的自定义配置项值,如该值输入非空,则执行精确值匹配。如该值输入为空,则忽略具体值,执行自定义配置项存在查询
     *
     * @return 关系类型对象列表
     */
    public List<RelationKind> getRelationKindsByMetaConfigItemMatch(String itemName,Object itemValue);

    /**
     * 根据输入条件查询关系附着规则类型对象,所有查询条件均为可选输入，各个查询条件精确匹配输入值，互相间为 AND 的组合关系
     *
     * @param relationAttachKindName String 需要查询的关系附着规则类型名称
     * @param relationAttachKindDesc String 需要查询的关系附着规则类型描述
     * @param sourceConceptionKindName String 需要查询的关联关系源概念类型名称
     * @param targetConceptionKindName String 需要查询的关联关系目标概念类型名称
     * @param relationKindName String 需要查询的关联关系类型名称
     * @param allowRepeatableRelationKind String 需要查询的关系附着规则类型是否允许重复创建相同关系类型的关联
     *
     * @return 关系附着规则类型列表
     */
    public List<RelationAttachKind> getRelationAttachKinds(String relationAttachKindName, String relationAttachKindDesc, String sourceConceptionKindName,
                                                           String targetConceptionKindName, String relationKindName, Boolean allowRepeatableRelationKind);

    /**
     * 根据对象唯一ID获取关系附着规则类型对象
     *
     * @param relationAttachKindUID String 需要获取的关系附着规则类型唯一ID
     *
     * @return 关系附着规则类型
     */
    public RelationAttachKind getRelationAttachKind(String relationAttachKindUID);

    /**
     * 创建新的关系附着规则类型
     *
     * @param relationAttachKindName String 需要创建的关系附着规则类型名称，不能与已有关系附着规则类型重名,非空输入值
     * @param relationAttachKindDesc String 需要创建的关系附着规则类型描述
     * @param sourceConceptionKindName String 关联关系源概念类型名称,非空输入值
     * @param targetConceptionKindName String 关联关系目标概念类型名称,非空输入值
     * @param relationKindName String 关联关系类型名称,非空输入值
     * @param allowRepeatableRelationKind String 是否允许重复创建相同关系类型的关联
     *
     * @return 关系附着规则类型
     */
    public RelationAttachKind createRelationAttachKind(String relationAttachKindName, String relationAttachKindDesc, String sourceConceptionKindName,
                                                       String targetConceptionKindName,String relationKindName,boolean allowRepeatableRelationKind)
            throws CoreRealmFunctionNotSupportedException;

    /**
     * 根据对象唯一ID删除关系附着规则类型
     *
     * @param relationAttachKindUID String 需要删除的关系附着规则类型唯一ID
     *
     * @return 如果操作成功返回 true
     */
    public boolean removeRelationAttachKind(String relationAttachKindUID) throws CoreRealmServiceRuntimeException;

    /**
     * 根据名称获取分类对象
     *
     * @param classificationName String 需要获取的分类名称
     *
     * @return 分类对象
     */
    public Classification getClassification(String classificationName);

    /**
     * 创建新的分类
     *
     * @param classificationName String 需要创建的分类名称，不能与已有分类重名
     * @param classificationDesc String 需要创建的分类描述
     *
     * @return 分类对象
     */
    public Classification createClassification(String classificationName,String classificationDesc);

    /**
     * 创建新的分类并指定父分类
     *
     * @param classificationName String 需要创建的分类名称，不能与已有分类重名
     * @param classificationDesc String 需要创建的分类描述
     * @param parentClassificationName String 需要创建的分类的父分类名称
     *
     * @return 分类对象
     */
    public Classification createClassification(String classificationName,String classificationDesc,String parentClassificationName)
            throws CoreRealmServiceRuntimeException;

    /**
     * 根据名称删除分类
     *
     * @param classificationName String 需要删除的分类名称
     *
     * @return 如果操作成功返回 true
     */
    public boolean removeClassification(String classificationName) throws CoreRealmServiceRuntimeException;

    /**
     * 根据名称删除分类及所有后代分类
     *
     * @param classificationName String 需要删除的分类名称
     *
     * @return 如果操作成功返回 true
     */
    public boolean removeClassificationWithOffspring(String classificationName) throws CoreRealmServiceRuntimeException;

    /**
     * 创建一个属于多概念类型的概念实体对象
     *
     * @param conceptionKindNames String[] 所属的概念类型数组
     * @param conceptionEntityValue ConceptionEntityValue 概念实体属性值
     * @param addPerDefinedRelation boolean 是否根据预定义的关联逻辑建立关系链接
     *
     * @return 概念实体对象
     */
    public ConceptionEntity newMultiConceptionEntity(String[] conceptionKindNames,ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation) throws CoreRealmServiceRuntimeException;

    /**
     * 创建一个属于多概念类型的概念实体对象并根据输入的关系附着规则类型建立关系链接
     *
     * @param conceptionKindNames String[] 所属的概念类型数组
     * @param conceptionEntityValue ConceptionEntityValue 概念实体属性值
     * @param relationAttachKindList List<RelationAttachKind> 建立链接所需的关系附着规则类型列表
     * @param entityRelateRole EntityRelateRole 概念实体在关系中的角色
     *
     * @return 概念实体对象
     */
    public ConceptionEntity newMultiConceptionEntity(String[] conceptionKindNames,ConceptionEntityValue conceptionEntityValue,List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) throws CoreRealmServiceRuntimeException;

    /**
     * 创建多个属于多概念类型的概念实体对象
     *
     * @param conceptionKindNames String[] 所属的概念类型数组
     * @param conceptionEntityValues List<ConceptionEntityValue> 概念实体属性值列表
     * @param addPerDefinedRelation boolean 是否根据预定义的关联逻辑建立关系链接
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult newMultiConceptionEntities(String[] conceptionKindNames,List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation) throws CoreRealmServiceRuntimeException;

    /**
     * 创建多个属于多概念类型的概念实体对象
     *
     * @param conceptionKindNames String[] 所属的概念类型数组
     * @param conceptionEntityValues List<ConceptionEntityValue> 概念实体属性值列表
     * @param relationAttachKindList List<RelationAttachKind> 建立链接所需的关系附着规则类型列表
     * @param entityRelateRole EntityRelateRole 概念实体在关系中的角色
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult newMultiConceptionEntities(String[] conceptionKindNames,List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) throws CoreRealmServiceRuntimeException;

    /**
     * 根据自定义查询条件获取实体的属性信息
     *
     * @param customQuerySentence String 用户自定义的查询语句，必须确保返回的实体为概念实体或关系实体
     *
     * @return 查询结果实体数据列表，具体数据格式依查询语句的返回条件而定
     */
    public List<Map<String,Map<String,Object>>> executeCustomQuery(String customQuerySentence) throws CoreRealmServiceRuntimeException;

    /**
     * 根据自定义查询条件获取实体的统计类信息
     *
     * @param customQuerySentence String 用户自定义的查询语句，必须确保返回的实体为统计类汇总数值信息
     *
     * @return 查询结果统计信息，具体数据格式依查询语句的返回条件而定
     */
    public Map<String,Number> executeCustomStatistic(String customQuerySentence) throws CoreRealmServiceRuntimeException;

    /**
     * 获取默认时间流,如默认时间流不存在则自动创建
     *
     * @return 时间流对象
     */
    public TimeFlow getOrCreateTimeFlow();

    /**
     * 获取用户自定义时间流,如自定义时间流不存在则自动创建
     *
     * @param timeFlowName String 用户自定义时间流名称
     *
     * @return 时间流对象
     */
    public TimeFlow getOrCreateTimeFlow(String timeFlowName);

    /**
     * 删除默认时间流以及所有包含其中的时间刻度实体
     *
     * @return 删除的时间流实例以及该时间流中包含的时间刻度实体数量的总和
     */
    public long removeTimeFlowWithEntities();

    /**
     * 删除用户自定义时间流以及所有包含其中的时间刻度实体
     *
     * @param timeFlowName String 用户自定义时间流名称
     *
     * @return 删除的时间流实例以及该时间流中包含的时间刻度实体数量的总和
     */
    public long removeTimeFlowWithEntities(String timeFlowName);

    /**
     * 获取领域所有时间流
     *
     * @return 时间流对象列表
     */
    public List<TimeFlow> getTimeFlows();

    /**
     * 获取默认地理空间区域,如默认地理空间区域不存在则自动创建
     *
     * @return 地理空间区域对象
     */
    public GeospatialRegion getOrCreateGeospatialRegion();

    /**
     * 获取用户自定义地理空间区域,如自定义地理空间区域不存在则自动创建
     *
     * @param geospatialRegionName String 用户自定义地理空间区域名称
     *
     * @return 地理空间区域对象
     */
    public GeospatialRegion getOrCreateGeospatialRegion(String geospatialRegionName);

    /**
     * 删除默认地理空间区域以及所有包含其中的地理空间刻度实体
     *
     * @return 删除的地理空间区域实例以及该区域中包含的地理空间刻度实体数量的总和
     */
    public long removeGeospatialRegionWithEntities();

    /**
     * 删除用户自定义地理空间区域以及所有包含其中的地理空间刻度实体
     *
     * @param geospatialRegionName String 用户自定义地理空间区域名称
     *
     * @return 删除的地理空间区域实例以及该区域中包含的地理空间刻度实体数量的总和
     */
    public long removeGeospatialRegionWithEntities(String geospatialRegionName);

    /**
     * 获取领域所有地理空间区域
     *
     * @return 地理空间区域对象列表
     */
    public List<GeospatialRegion> getGeospatialRegions();

    /**
     * 获取领域中所有 ConceptionKind 的实体统计信息
     *
     * @return ConceptionKind的实体统计信息列表
     */
    public List<EntityStatisticsInfo> getConceptionEntitiesStatistics() throws CoreRealmServiceEntityExploreException;

    /**
     * 获取领域中所有 RelationKind 的实体统计信息
     *
     * @return RelationKind的实体统计信息列表
     */
    public List<EntityStatisticsInfo> getRelationEntitiesStatistics();

    /**
     * 获取领域中所有 ConceptionKind 的实体之间的相关关系信息
     *
     * @return 各个关联的概念类型结对与相关的关系类型信息的列表
     */
    public List<ConceptionKindCorrelationInfo> getConceptionKindsCorrelation();

    /**
     * 获取当前领域的跨类型数据操作器
     *
     * @return 当前领域的跨类型数据操作器
     */
    public CrossKindDataOperator getCrossKindDataOperator();

    /**
     * 获取当前领域的系统维护操作器
     *
     * @return 当前领域的系统维护操作器
     */
    public SystemMaintenanceOperator getSystemMaintenanceOperator();

    /**
     * 获取当前领域的数据科学操作器
     *
     * @return 当前领域的数据科学操作器
     */
    public DataScienceOperator getDataScienceOperator();

    /**
     * 获取当前领域的实体数据交换操作器
     *
     * @return 当前领域的实体数据交换操作器
     */
    public EntitiesExchangeOperator getEntitiesExchangeOperator();

    /**
     * 获取当前领域的全部概念类型元数据信息
     *
     * @return 当前领域的全部概念类型元数据信息列表
     */
    public List<KindMetaInfo> getConceptionKindsMetaInfo() throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前领域的全部关系类型元数据信息
     *
     * @return 当前领域的全部关系类型元数据信息列表
     */
    public List<KindMetaInfo> getRelationKindsMetaInfo() throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前领域的全部属性类型元数据信息
     *
     * @return 当前领域的全部属性类型元数据信息列表
     */
    public List<AttributeKindMetaInfo> getAttributeKindsMetaInfo() throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前领域的全部属性视图类型元数据信息
     *
     * @return 当前领域的全部属性视图类型元数据信息列表
     */
    public List<AttributesViewKindMetaInfo> getAttributesViewKindsMetaInfo() throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前领域的全部分类元数据信息
     *
     * @return 当前领域的全部分类元数据信息列表
     */
    public List<ClassificationMetaInfo> getClassificationsMetaInfo() throws CoreRealmServiceEntityExploreException;

    /**
     * 开启全局会话，此操作会创建一个持久化的后端数据库连接，执行该操作后由当前 CoreRealm 所创建的所有对象（以及这些对象创建的后续对象）将继承性的共享该持久化后端数据库连接。
     */
    public void openGlobalSession();

    /**
     * 关闭全局会话，此操作会关闭由 openGlobalSession() 创建的持久化后端数据库连接。该操作后由当前 CoreRealm 所创建的所有对象（以及这些对象创建的后续对象）将各自分别独立的维护其内部的瞬态后端数据库连接。
     */
    public void closeGlobalSession();
}
