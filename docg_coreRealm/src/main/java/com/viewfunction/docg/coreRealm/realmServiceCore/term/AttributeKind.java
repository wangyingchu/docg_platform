package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;

import java.util.List;
import java.util.Map;

public interface AttributeKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable {
    /**
     * 获取当前属性类型对象名称
     *
     * @return 属性类型对象名称
     */
    public String getAttributeKindName();

    /**
     * 获取当前属性类型对象唯一ID
     *
     * @return 属性类型对象唯一ID
     */
    public String getAttributeKindUID();

    /**
     * 获取当前属性类型对象描述
     *
     * @return 属性类型对象描述
     */
    public String getAttributeKindDesc();

    /**
     * 更新当前属性类型对象描述
     *
     * @param kindDesc String 新的属性类型描述
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean updateAttributeKindDesc(String kindDesc);

    /**
     * 获取当前属性类型对象数据类型,数据类型可能选项是：
     * BOOLEAN,INT,SHORT,LONG,FLOAT,DOUBLE,DATE,STRING,BYTE,DECIMAL,
     * BOOLEAN_ARRAY,INT_ARRAY,SHORT_ARRAY,LONG_ARRAY,FLOAT_ARRAY,DOUBLE_ARRAY,DATE_ARRAY,STRING_ARRAY,BYTE_ARRAY,DECIMAL_ARRAY,
     * BINARY
     * @return 属性类型对象数据类型枚举值
     */
    public AttributeDataType getAttributeDataType();

    /**
     * 获取所有包含当前属性类型的属性视图类型对象
     *
     * @return 属性视图类型对象列表
     */
    public List<AttributesViewKind> getContainerAttributesViewKinds();

    /**
     * 获取实体对象中拥有当前属性类型定义的属性值的概念类型实体数量。返回值中包含的目标概念类型与当前属性类型通过两者间共同联系的 AttributesViewKind 属性视图类型确定
     * @return 符合条件的概念实体数量统计结果 Map。其中 Key为概念类型名称，Value为符合条件的概念实体数量
     */
    public Map<String,Long> getAttributeInConceptionKindDistributionStatistics();

    /**
     * 获取所有通过属性视图类型中转而包含当前属性类型的概念类型对象
     *
     * @return 概念类型对象列表
     */
    public List<ConceptionKind> getContainerConceptionKinds();

    /**
     * 获取所有通过属性视图类型中转而包含当前属性类型的关系类型对象
     *
     * @return 关系类型对象列表
     */
    public List<RelationKind> getContainerRelationKinds();
}
