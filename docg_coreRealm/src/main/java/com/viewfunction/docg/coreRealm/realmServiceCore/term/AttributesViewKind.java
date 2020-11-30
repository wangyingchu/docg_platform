package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;

import java.util.List;
import java.util.Map;

public interface AttributesViewKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable {
    /**
     * 属性视图类型的数据存储结构
     * SINGLE_VALUE : 该视图中包含的属性值为单值，每一个 Entity 对象中该视图中包含的属性只有一个值。属性值存储在实体对象内部。
     * LIST_VALUE : 该视图中包含的属性值为列表值，每一个 Entity 对象中该视图中包含的属性可以有多个值。属性值存储在实体对象内部。
     * RELATED_VALUE :该视图中包含的属性值为列表值，每一个 Entity 对象中该视图中包含的属性可以有多个值。属性值存储在与该实体相关的其他实体对象中。
     * EXTERNAL_VALUE :该视图中包含的属性值为列表值，每一个 Entity 对象中该视图中包含的属性可以有多个值。属性值存储在与该实体相关的其他外部数据源中。
     */
    public enum AttributesViewKindDataForm {SINGLE_VALUE, LIST_VALUE, RELATED_VALUE, EXTERNAL_VALUE}

    /**
     * 获取当前属性视图类型对象唯一ID
     *
     * @return 属性视图类型对象唯一ID
     */
    public String getAttributesViewKindUID();

    /**
     * 获取当前属性视图类型对象名称
     *
     * @return 属性视图类型对象名称
     */
    public String getAttributesViewKindName();

    /**
     * 获取当前属性视图类型对象描述
     *
     * @return 属性视图类型对象描述
     */
    public String getAttributesViewKindDesc();

    /**
     * 判断当前属性视图类型是否是集合类属性视图
     *
     * @return 如果数据存储结构是 LIST_VALUE, RELATED_VALUE, EXTERNAL_VALUE 则返回 true
     */
    public boolean isCollectionAttributesViewKind();

    /**
     * 获取当前属性视图类型的数据存储结构
     *
     * @return 数据存储结构枚举值
     */
    public AttributesViewKindDataForm getAttributesViewKindDataForm();

    /**
     * 为当前属性视图类型附加属性类型
     *
     * @param attributeKindUID String 需要附加的属性类型唯一ID
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean attachAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前属性视图类型附加属性类型,并在链接中添加自定义元数据信息
     *
     * @param attributeKindUID String 需要附加的属性类型唯一ID
     * @param properties Map<String,Object> 链接上的自定义属性
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean attachAttributeKind(String attributeKindUID, Map<String,Object> properties) throws CoreRealmServiceRuntimeException;

    /**
     * 为已经附加在当前属性视图类型上的属性类型设置链接上的元数据信息
     *
     * @param attributeKindUID String 需要附加的属性类型唯一ID
     * @param properties Map<String,Object> 链接上的自定义属性
     *
     * @return 操作成功的元数据名称列表
     */
    public List<String> setAttributeKindAttachMetaInfo(String attributeKindUID,Map<String,Object> properties);

    /**
     * 从已经附加在当前属性视图类型上的属性类型链接中删除指定的元数据属性
     *
     * @param attributeKindUID String 需要附加的属性类型唯一ID
     * @param metaPropertyName String 需要删除的元数据属性名称
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean removeAttributeKindAttachMetaInfo(String attributeKindUID,String metaPropertyName) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前属性视图类型中的某一特定元数据属性信息
     *
     * @param metaPropertyName String 需要获取的元数据属性名称
     *
     * @return 查询结果 Map，其中 Key 为含有该元数据的属性类型的唯一ID，Value 该元数据属性的属性值
     */
    public Map<String,Object> getAttributeKindsAttachMetaInfo(String metaPropertyName);

    /**
     * 获取当前属性视图类型中包含的某一特定属性类型的指定元数据属性信息
     *
     * @param attributeKindUID String 需要获取的属性类型唯一ID
     * @param metaPropertyName String 需要获取的元数据属性名称
     *
     * @return 查询结果 该元数据属性的属性值
     */
    public Object getAttributeKindAttachMetaInfo(String attributeKindUID,String metaPropertyName);

    /**
     * 从当前属性视图类型上移除已附加的属性类型链接
     *
     * @param attributeKindUID String 需要移除链接的属性类型唯一ID
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean detachAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前属性视图类型包含的所有属性类型对象
     *
     * @return 属性类型对象列表
     */
    public List<AttributeKind> getContainsAttributeKinds();

    /**
     * 获取所有包含当前属性视图类型的概念类型对象
     *
     * @return 概念类型对象列表
     */
    public List<ConceptionKind> getContainerConceptionKinds();
}
