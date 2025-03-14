package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.List;

public interface Classification extends MetaAttributeFeatureSupportable, AttributesMeasurable{
    /**
     * 获取当前分类名称
     *
     * @return 分类名称
     */
    public String getClassificationName();

    /**
     * 获取当前分类描述
     *
     * @return 分类描述
     */
    public String getClassificationDesc();

    /**
     * 获取当前分类的唯一值ID
     *
     * @return 唯一值ID
     */
    public String getClassificationUID();

    /**
     * 更新当前分类名称。如当前系统中不存在与 classificationNewName 值同名的分类，则更新当前分类名称。否则抛出核心领域运行时异常
     *
     * @param classificationNewName String 新的分类名称
     *
     * @return 如操作成功，返回结果为 true，如当前系统中已经存在需要更新的分类的同名
     */
    public boolean updateClassificationName(String classificationNewName) throws CoreRealmServiceRuntimeException;

    /**
     * 更新当前分类描述
     *
     * @param classificationDesc String 新的分类描述
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean updateClassificationDesc(String classificationDesc);

    /**
     * 判断当前分类是否为根节点分类
     *
     * @return 如果当前分类没有父分类则返回 true
     */
    public boolean isRootClassification();

    /**
     * 获取当前分类的父分类
     *
     * @return 父分类对象
     */
    public Classification getParentClassification();

    /**
     * 获取当前分类的子分类列表
     *
     * @return 子分类对象列表
     */
    public List<Classification> getChildClassifications();

    /**
     * 获取当前分类的所有后代分类
     *
     * @return 分类对象继承树
     */
    public InheritanceTree<Classification> getOffspringClassifications();

    /**
     * 为当前分类附加已经存在的子分类
     *
     * @param childClassificationName String 需要附加的子分类名称
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean attachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException;

    /**
     * 从当前分类上移除已经附加的子分类的父子关系
     *
     * @param childClassificationName String 需要移除父子关系的子分类名称
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean detachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前分类创建新的子分类
     *
     * @param classificationName String 新建的子分类名称
     * @param classificationDesc String 新建的子分类描述
     *
     * @return 新建的子分类对象
     */
    public Classification createChildClassification(String classificationName,String classificationDesc) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前分类的子分类
     *
     * @param classificationName String 需要删除的子分类名称
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean removeChildClassification(String classificationName) throws CoreRealmServiceRuntimeException;

    /**
     * 获取与当前分类关联的概念类型对象
     *
     * @param relationKindName String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联的关系方向
     * @param includeOffspringClassifications boolean 是否获取后代分类关联的数据
     * @param offspringLevel int 包含的后代分类层级数
     *
     * @return 概念类型对象列表
     */
    public List<ConceptionKind> getRelatedConceptionKinds(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取与当前分类关联的关系类型对象
     *
     * @param relationKindName String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联的关系方向
     * @param includeOffspringClassifications boolean 是否获取后代分类关联的数据
     * @param offspringLevel int 包含的后代分类层级数
     *
     * @return 关系类型对象列表
     */
    public List<RelationKind> getRelatedRelationKinds(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取与当前分类关联的属性类型对象
     *
     * @param relationKindName String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联的关系方向
     * @param includeOffspringClassifications boolean 是否获取后代分类关联的数据
     * @param offspringLevel int 包含的后代分类层级数
     *
     * @return 属性类型对象列表
     */
    public List<AttributeKind> getRelatedAttributeKinds(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取与当前分类关联的属性视图类型对象
     *
     * @param relationKindName String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联的关系方向
     * @param includeOffspringClassifications boolean 是否获取后代分类关联的数据
     * @param offspringLevel int 包含的后代分类层级数
     *
     * @return 属性视图类型对象列表
     */
    public List<AttributesViewKind> getRelatedAttributesViewKinds(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取与当前分类关联的概念实体对象
     *
     * @param relationKindName String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联的关系方向
     * @param queryParameters QueryParameters 概念实体查询过滤条件
     * @param includeOffspringClassifications boolean 是否获取后代分类关联的数据
     * @param offspringLevel int 包含的后代分类层级数
     *
     * @return 概念实体对象列表
     */
    public List<ConceptionEntity> getRelatedConceptionEntities(String relationKindName, RelationDirection relationDirection, QueryParameters queryParameters, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 获取与当前分类关联的概念实体对象的属性值
     *
     * @param relationKindName String 关联的关系类型名称
     * @param relationDirection RelationDirection 关联的关系方向
     * @param queryParameters QueryParameters 概念实体查询过滤条件
     * @param attributeNames List<String> 查询返回的属性类型列表
     * @param includeOffspringClassifications boolean 是否获取后代分类关联的数据
     * @param offspringLevel int 包含的后代分类层级数
     *
     * @return 概念实体属性查询结果集
     */
    public ConceptionEntitiesAttributesRetrieveResult getRelatedConceptionEntityAttributes(String relationKindName, RelationDirection relationDirection, QueryParameters queryParameters, List<String> attributeNames,boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 获取与当前分类关联的各类型数据的实时统计信息
     *
     * @return 分类实时统计信息
     */
    public ClassificationRuntimeStatistics getClassificationRuntimeStatistics();

    /**
     * 获取所有与当前分类一度直接关联的概念类型对象以及关联信息
     *
     * @return 概念类型关联信息对象列表
     */
    public List<ConceptionKindAttachInfo> getAllDirectRelatedConceptionKindsInfo();

    /**
     * 获取所有与当前分类一度直接关联的关系类型对象以及关联信息
     *
     * @return 关系类型关联信息对象列表
     */
    public List<RelationKindAttachInfo> getAllDirectRelatedRelationKindsInfo();

    /**
     * 获取所有与当前分类一度直接关联的属性类型对象以及关联信息
     *
     * @return 属性类型关联信息对象列表
     */
    public List<AttributeKindAttachInfo> getAllDirectRelatedAttributeKindsInfo();

    /**
     * 获取所有与当前分类一度直接关联的属性视图类型对象以及关联信息
     *
     * @return 属性视图类型关联信息对象列表
     */
    public List<AttributesViewKindAttachInfo> getAllDirectRelatedAttributesViewKindsInfo();
}
