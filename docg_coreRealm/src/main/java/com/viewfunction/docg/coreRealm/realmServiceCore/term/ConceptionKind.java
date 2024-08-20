package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.RelationMatchParameters;
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

public interface ConceptionKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable, StatisticalAndEvaluable {
    //东风夜放花千树，更吹落，星如雨
    /**
     * 获取当前概念类型对象名称
     *
     * @return 概念类型对象名称
     */
    public String getConceptionKindName();

    /**
     * 获取当前概念类型对象描述
     *
     * @return 概念类型对象描述
     */
    public String getConceptionKindDesc();

    /**
     * 更新当前概念类型对象描述
     *
     * @param kindDesc String 新的概念类型描述
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean updateConceptionKindDesc(String kindDesc);

    /**
     * 计算当前概念类型的所有概念实体数量
     *
     * @return 概念实体数量
     */
    public Long countConceptionEntities() throws CoreRealmServiceRuntimeException;

    /**
     * 计算当前概念类型（包含所有后代概念类型）的所有概念实体数量，该方法在 NEO4J 实现类型下无效
     *
     * @return 概念实体数量
     */
    public Long countConceptionEntitiesWithOffspring() throws CoreRealmFunctionNotSupportedException;

    /**
     * 获取当前概念类型的所有子概念类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 概念类型对象列表
     */
    public List<ConceptionKind> getChildConceptionKinds() throws CoreRealmFunctionNotSupportedException;

    /**
     * 获取当前概念类型的父概念类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 概念类型对象
     */
    public ConceptionKind getParentConceptionKind() throws CoreRealmFunctionNotSupportedException;

    /**
     * 获取当前概念类型的所有后代概念类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 概念类型对象继承树
     */
    public InheritanceTree<ConceptionKind> getOffspringConceptionKinds() throws CoreRealmFunctionNotSupportedException;

    /**
     * 创建一个属于当前概念类型的概念实体对象
     *
     * @param conceptionEntityValue ConceptionEntityValue 概念实体属性值
     * @param addPerDefinedRelation boolean 是否根据预定义的关联逻辑建立关系链接
     *
     * @return 概念实体对象
     */
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation);

    /**
     * 创建一个属于当前概念类型的概念实体对象
     *
     * @param conceptionEntityValue ConceptionEntityValue 概念实体属性值
     * @param relationAttachKindList List<RelationAttachKind> 建立链接所需的关系附着规则类型列表
     * @param entityRelateRole EntityRelateRole 概念实体在关系中的角色
     *
     * @return 概念实体对象
     */
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole);

    /**
     * 创建多个属于当前概念类型的概念实体对象
     * @param conceptionEntityValues List<ConceptionEntityValue> 概念实体属性值列表
     * @param addPerDefinedRelation boolean 是否根据预定义的关联逻辑建立关系链接
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation);

    /**
     * 创建多个属于当前概念类型的概念实体对象
     *
     * @param conceptionEntityValues List<ConceptionEntityValue> 概念实体属性值列表
     * @param relationAttachKindList List<RelationAttachKind> 建立链接所需的关系附着规则类型列表
     * @param entityRelateRole EntityRelateRole 概念实体在关系中的角色
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole);

    /**
     * 更新一个当前概念类型的概念实体对象的属性信息
     *
     * @param conceptionEntityValueForUpdate ConceptionEntityValue 需要更新的概念实体信息
     *
     * @return 更新后的概念实体对象
     */
    public ConceptionEntity updateEntity(ConceptionEntityValue conceptionEntityValueForUpdate) throws CoreRealmServiceRuntimeException;

    /**
     * 更新多个当前概念类型的概念实体对象的属性信息
     *
     * @param entityValues List<ConceptionEntityValue> 需要更新的概念实体信息
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult updateEntities(List<ConceptionEntityValue> entityValues);

    /**
     * 删除一个当前概念类型的概念实体对象
     *
     * @param conceptionEntityUID String 需要删除的概念实体的唯一ID
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean deleteEntity(String conceptionEntityUID) throws CoreRealmServiceRuntimeException;

    /**
     * 更新多个当前概念类型的概念实体对象
     *
     * @param conceptionEntityUIDs List<String> 需要删除的概念实体的唯一ID列表
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult deleteEntities(List<String> conceptionEntityUIDs) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前概念类型的所有概念实体
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前概念类型的所有独享的概念实体，如目标概念实体同时属于其他的概念类型，将其从本概念类型中撤出，但不删除实体
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult purgeExclusiveEntities() throws CoreRealmServiceRuntimeException;

    /**
     * 计算符合过滤条件的当前概念类型的概念实体对象数量
     *
     * @param attributesParameters AttributesParameters 查询过滤条件
     * @param isDistinctMode boolean 是否不允许重复数据
     *
     * @return 概念实体数量
     */
    public Long countEntities(AttributesParameters attributesParameters,boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException;

    /**
     * 查询符合过滤条件的当前概念类型的概念实体对象
     *
     * @param queryParameters QueryParameters 查询过滤条件
     *
     * @return 概念实体查询结果集
     */
    public ConceptionEntitiesRetrieveResult getEntities(QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 计算符合实体过滤条件，并且与指定的概念类型的实体通过特定的关系类型能够关联匹配的当前概念类型的概念实体对象数量
     *
     * @param attributesParameters AttributesParameters 查询过滤条件
     * @param isDistinctMode boolean 是否不允许重复数据
     * @param relationMatchParameters RelationMatchParameters 概念实体关联关系的匹配条件
     *
     * @return 概念实体数量
     */
    public Long countEntitiesWithRelationsMatch(AttributesParameters attributesParameters,boolean isDistinctMode, RelationMatchParameters relationMatchParameters) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException;

    /**
     * 查询符合实体过滤条件，并且与指定的概念类型的实体通过特定的关系类型能够关联匹配的当前概念类型的概念实体对象
     *
     * @param queryParameters QueryParameters 概念实体查询过滤条件
     * @param relationMatchParameters RelationMatchParameters 概念实体关联关系的匹配条件
     *
     * @return 概念实体查询结果集
     */
    public ConceptionEntitiesRetrieveResult getEntitiesWithRelationsMatch(QueryParameters queryParameters, RelationMatchParameters relationMatchParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 查询符合实体过滤条件，并且与指定的分类通过特定的关系类型能够附着匹配的当前概念类型的概念实体对象
     *
     * @param queryParameters QueryParameters 概念实体查询过滤条件
     * @param classificationAttachParametersSet Set<RelationMatchParameters> 概念实体附着分类的匹配条件
     *
     * @return 概念实体查询结果集
     */
    public ConceptionEntitiesRetrieveResult getEntitiesWithClassificationsAttached(QueryParameters queryParameters, Set<RelationMatchParameters> classificationAttachParametersSet) throws CoreRealmServiceEntityExploreException;

    /**
     * 根据唯一ID获取当前概念类型的概念实体对象
     *
     * @param conceptionEntityUID String 需要获取的概念实体唯一ID
     *
     * @return 概念实体对象
     */
    public ConceptionEntity getEntityByUID(String conceptionEntityUID);

    /**
     * 查询符合过滤条件的当前概念类型的概念实体对象,并根据输入的 SINGLE_VALUE 数据存储结构的属性视图类型列表，合并其中包含的属性类型返回相应的属性值
     *
     * @param attributesViewKindNames List<String> 属性视图类型列表
     * @param exploreParameters QueryParameters 查询过滤条件
     *
     * @return 概念实体属性查询结果集
     */
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKinds(List<String> attributesViewKindNames, QueryParameters exploreParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 查询符合过滤条件的当前概念类型的概念实体对象,并根据输入的属性类型返回相应的属性值
     *
     * @param attributeNames List<String> 属性类型列表
     * @param exploreParameters QueryParameters 查询过滤条件
     *
     * @return 概念实体属性查询结果集
     */
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNames(List<String> attributeNames, QueryParameters exploreParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 查询符合实体过滤条件，并且与指定的概念类型的实体通过特定的关系类型能够关联匹配的当前概念类型的概念实体对象,并根据输入的 SINGLE_VALUE 数据存储结构的属性视图类型列表，合并其中包含的属性类型返回相应的属性值
     *
     * @param attributesViewKindNames List<String> 属性视图类型列表
     * @param exploreParameters QueryParameters 查询过滤条件
     *
     * @return 概念实体属性查询结果集
     */
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKindsWithRelationsMatch(
            List<String> attributesViewKindNames, QueryParameters exploreParameters, RelationMatchParameters relationMatchParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 查询符合实体过滤条件，并且与指定的概念类型的实体通过特定的关系类型能够关联匹配的当前概念类型的概念实体对象,并根据输入的属性类型返回相应的属性值
     *
     * @param attributeNames List<String> 属性类型列表
     * @param exploreParameters QueryParameters 查询过滤条件
     *
     * @return 概念实体属性查询结果集
     */
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNamesWithRelationsMatch(
            List<String> attributeNames, QueryParameters exploreParameters, RelationMatchParameters relationMatchParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 为当前概念类型附加属性视图类型
     *
     * @param attributesViewKindUID String 需要附加的属性视图类型唯一ID
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean attachAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前概念类型附加的全部属性视图类型
     *
     * @return 属性视图类型对象列表
     */
    public List<AttributesViewKind> getContainsAttributesViewKinds();

    /**
     * 获取当前概念类型附加的全部符合名称查询条件的属性视图类型
     *
     * @param attributesViewKindName String 需要返回的属性视图类型名称，本查询的数值匹配规则为 Equal 匹配
     *
     * @return 属性视图类型对象列表
     */
    public List<AttributesViewKind> getContainsAttributesViewKinds(String attributesViewKindName);

    /**
     * 从当前概念类型上移除已经附加的属性视图类型
     *
     * @param attributesViewKindUID String 需要移除的属性视图类型唯一ID
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean detachAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前概念类型包含的全部 SINGLE_VALUE 数据存储结构的属性视图类型中包含的属性类型
     *
     * @return 属性类型对象列表
     */
    public List<AttributeKind> getContainsSingleValueAttributeKinds();

    /**
     * 获取当前概念类型包含的全部符合名称查询条件的 SINGLE_VALUE 数据存储结构的属性视图类型中包含的属性类型
     *
     * @param attributeKindName String 需要返回的属性类型名称，本查询的数值匹配规则为 Equal 匹配
     *
     * @return 属性类型对象列表
     */
    public List<AttributeKind> getContainsSingleValueAttributeKinds(String attributeKindName);

    /**
     * 查询符合过滤条件的与当前概念类型的实体对象直接关联的其他实体对象
     *
     * @param startEntityUIDS List<String> 当前概念类型实体对象 UID 列表，只有与该列表中UID定义的实体对象关联的数据才会计入查询过滤结果，如传入 null 则忽略特定实体关联，从类型上执行全局查询
     * @param relationKind String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联方向
     * @param aimConceptionKind List<String> 查询目标概念类型名称，如传入 null 则忽略类型
     * @param queryParameters QueryParameters 查询返回的概念实体过滤参数
     *
     * @return 概念实体查询结果集
     */
    public ConceptionEntitiesRetrieveResult getKindDirectRelatedEntities(List<String> startEntityUIDS,String relationKind,RelationDirection
            relationDirection,String aimConceptionKind,QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 查询符合过滤条件的与当前概念类型的实体对象直接关联的其他实体对象,并根据输入的属性类型返回相应的属性值
     *
     * @param startEntityUIDS List<String> 当前概念类型实体对象 UID 列表，只有与该列表中UID定义的实体对象关联的数据才会计入查询过滤结果，如传入 null 则忽略特定实体关联，从类型上执行全局查询
     * @param attributeNames List<String> 返回属性类型列表
     * @param relationKind String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联方向
     * @param aimConceptionKind List<String> 查询目标概念类型名称，如传入 null 则忽略类型
     * @param queryParameters QueryParameters 查询返回的概念实体过滤参数
     *
     * @return 概念实体属性查询结果集
     */
    public ConceptionEntitiesAttributesRetrieveResult getAttributesOfKindDirectRelatedEntities(List<String> startEntityUIDS,List<String> attributeNames,String relationKind,RelationDirection
            relationDirection,String aimConceptionKind,QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 查询与符合过滤条件的目标概念类型实体直接关联的实体对象
     *
     * @param relationKind String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联方向
     * @param aimConceptionKind List<String> 查询目标概念类型名称，如传入 null 则忽略类型
     * @param queryParameters QueryParameters 查询的目标关联概念实体过滤参数
     *
     * @return 概念实体查询结果集
     */
    public ConceptionEntitiesRetrieveResult getEntitiesByDirectRelations(String relationKind,RelationDirection
            relationDirection,String aimConceptionKind,QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 根据采样率获取部分概念实体数据并统计其中包含的属性分布统计信息
     *
     * @param sampleRatio double 采样率，介于0到1之间的小数，代表当前概念类型中需要采样数据的百分比
     *
     * @return 属性分布统计的结果信息
     */
    public Set<KindAttributeDistributionInfo> getKindAttributesDistributionStatistics(double sampleRatio) throws CoreRealmServiceRuntimeException;

    /**
     * 根据采样率获取部分概念实体数据并统计其中包含的属性与关联关系数据的分布统计信息
     *
     * @param sampleRatio double 采样率，介于0到1之间的小数，代表当前概念类型中需要采样数据的百分比
     *
     * @return 属性与关联关系数据分布统计的结果信息
     */
    public Set<KindDataDistributionInfo> getKindDataDistributionStatistics(double sampleRatio) throws CoreRealmServiceRuntimeException;

    /**
     * 统计当前概念类型实体与其他概念类型实体之间的实时关联关系信息
     *
     * @return 当前概念类型与其他概念类型之间的关联关系信息集合
     */
    public Set<ConceptionKindCorrelationInfo> getKindRelationDistributionStatistics();

    /**
     * 随机获取若干当前概念类型下的概念实体
     *
     * @param entitiesCount int 需要获取的概念实体数量
     *
     * @return 概念实体集合
     */
    public Set<ConceptionEntity>  getRandomEntities(int entitiesCount) throws CoreRealmServiceEntityExploreException;

    /**
     * 随机获取若干符合过滤条件的当前概念类型的概念实体
     *
     * @param attributesParameters AttributesParameters 查询过滤条件
     * @param isDistinctMode boolean 是否不允许重复数据
     * @param entitiesCount int 需要获取的概念实体数量
     *
     * @return 概念实体集合
     */
    public Set<ConceptionEntity> getRandomEntities(AttributesParameters attributesParameters,boolean isDistinctMode,int entitiesCount) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException;

    /**
     * 为当前概念类型的所有概念实体添加指定的属性，如属性已经存在，则用新的值覆盖原有属性
     *
     * @param attributes Map<String, Object> 需要添加的所有属性
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics setKindScopeAttributes(Map<String, Object> attributes) throws CoreRealmServiceRuntimeException;

    /**
     * 从当前概念类型的所有概念实体中删除指定的属性
     *
     * @param attributeNames Map<String, Object> 需要删除的所有属性的名称集合
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics removeEntityAttributes(Set<String> attributeNames) throws CoreRealmServiceRuntimeException;

    /**
     * 将当前概念类型的所有概念实体中指定的属性转换为 Int 类型，如当前属性值无法合法转换则删除该属性
     *
     * @param attributeName String 需要转换的属性名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToIntType(String attributeName);

    /**
     * 将当前概念类型的所有概念实体中指定的属性转换为 Float 类型，如当前属性值无法合法转换则删除该属性
     *
     * @param attributeName String 需要转换的属性名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToFloatType(String attributeName);

    /**
     * 将当前概念类型的所有概念实体中指定的属性转换为 Boolean 类型，如当前属性值无法合法转换则删除该属性
     *
     * @param attributeName String 需要转换的属性名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToBooleanType(String attributeName);

    /**
     * 将当前概念类型的所有概念实体中指定的属性转换为 String 类型
     *
     * @param attributeName String 需要转换的属性名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToStringType(String attributeName);

    /**
     * 将当前概念类型的所有概念实体中指定的属性的 String 数据类型的表达转换为 Temporal 时间类类型
     *
     * @param attributeName String 需要转换的属性名称
     * @param dateTimeFormatter DateTimeFormatter 需要转换的属性的时间类型数据格式
     * @param temporalScaleType TemporalScaleLevel 需要转换为的时间类类型
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics convertEntityAttributeToTemporalType(String attributeName, DateTimeFormatter dateTimeFormatter,
                                TemporalScaleCalculable.TemporalScaleLevel temporalScaleType) throws CoreRealmServiceRuntimeException;

    /**
     * 将当前概念类型所有概念实体中指定属性的值复制到新的属性中，如已经存在与新属性同名的属性值，则该值将被覆盖
     *
     * @param originalAttributeName String 需要被复制的属性名称
     * @param newAttributeName String 复制目标新属性的名称
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics duplicateEntityAttribute(String originalAttributeName, String newAttributeName) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前概念类型的特定概念实体在指定的时间流上附加时间刻度事件
     *
     * @param queryParameters QueryParameters 概念实体查询过滤条件
     * @param timeEventAttributeName String 在时间流上确定具体时间点的属性名称，该属性类型应当为时间类或 String 类型
     * @param dateTimeFormatter DateTimeFormatter 当 timeEventAttributeName 属性为 String 类型时，需要使用本属性提供日期时间获取的格式信息
     * @param timeFlowName String 指定时间流名称,输入 null 则选择默认时间流
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics attachTimeScaleEvents(QueryParameters queryParameters, String timeEventAttributeName, DateTimeFormatter dateTimeFormatter,
                                String timeFlowName, String eventComment, Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 为当前概念类型的特定概念实体在指定的时间流上附加时间刻度事件
     *
     * @param queryParameters QueryParameters 概念实体查询过滤条件
     * @param timeEventYearAttributeName String 在时间流上确定具体时间点年份的属性名称，概念实体的该属性值应当为数值类型，范围在年份概念内，否则将在操作中忽略该概念实体
     * @param timeEventMonthAttributeName String 在时间流上确定具体时间点月份的属性名称，概念实体的该属性值应当为数值类型，范围在 1 至 12 之间，否则将在操作中忽略该概念实体
     * @param timeEventDayAttributeName String 在时间流上确定具体时间点的日属性名称，概念实体的该属性值应当为数值类型，范围在 1 至 31 之间，否则将在操作中忽略该概念实体
     * @param timeEventHourAttributeName String 在时间流上确定具体时间点的小时属性名称，该属性类型应当为数值类型，范围在 0 至 23 之间，否则将在操作中忽略该概念实体
     * @param timeEventMinuteAttributeName String 在时间流上确定具体时间点的分钟属性名称，概念实体的该属性值应当为数值类型，范围在 0 至 59 之间，否则将在操作中忽略该概念实体
     * @param timeFlowName String 指定时间流名称,输入 null 则选择默认时间流
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics attachTimeScaleEvents(QueryParameters queryParameters, String timeEventYearAttributeName, String timeEventMonthAttributeName,String timeEventDayAttributeName,String timeEventHourAttributeName,String timeEventMinuteAttributeName,
                                       String timeFlowName, String eventComment, Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 为当前概念类型的特定概念实体在指定的地理空间区域上附加地理空间刻度事件
     *
     * @param queryParameters QueryParameters 概念实体查询过滤条件
     * @param geospatialEventAttributeName String 在地理空间区域上确定具体地理空间目标的属性名称，该属性类型应当为 String 类型
     * @param geospatialPropertyType GeospatialRegion.GeospatialProperty 确定属性 geospatialEventAttributeName 内容表示的具体地理空间属性类型
     * @param geospatialRegionName String 指定地理空间区域名称,输入 null 则选择默认地理空间区域
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param geospatialScaleGrade GeospatialRegion.GeospatialScaleGrade 事件地理空间刻度等级
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics attachGeospatialScaleEvents(QueryParameters queryParameters, String geospatialEventAttributeName, GeospatialRegion.GeospatialProperty geospatialPropertyType,
                                                                   String geospatialRegionName, String eventComment, Map<String, Object> eventData, GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 根据当前概念类型实体的地理空间 WKT 数据计算其与系统内置的地理空间区域实体（行政区划）的特定空间关系，并在符合计算逻辑的地理空间区域实体与概念类型实体之间附加地理空间刻度事件
     *
     * @param queryParameters QueryParameters 概念实体查询过滤条件
     * @param spatialScaleLevel GeospatialScaleCalculable.SpatialScaleLevel 空间计算使用的地理空间尺度参考坐标系
     * @param spatialPredicateType GeospatialScaleCalculable.SpatialPredicateType 空间计算使用的空间拓扑关系定义
     * @param geospatialScaleGrade GeospatialRegion.GeospatialScaleGrade 空间计算的目标地理空间区域实体的地理空间刻度等级
     * @param geospatialRegionName String 指定地理空间区域名称,输入 null 则选择默认地理空间区域
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     *
     * @return 操作执行状况的统计结果
     */
    public EntitiesOperationStatistics attachGeospatialScaleEventsByEntityGeometryContent(QueryParameters queryParameters,GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel,
                                                                    GeospatialScaleCalculable.SpatialPredicateType spatialPredicateType, GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade,
                                                                    String geospatialRegionName,String eventComment, Map<String, Object> eventData) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;
}
