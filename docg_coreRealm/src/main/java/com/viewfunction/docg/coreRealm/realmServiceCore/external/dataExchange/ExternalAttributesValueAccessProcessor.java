package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.List;
import java.util.Map;

public interface ExternalAttributesValueAccessProcessor {

    /**
     * 获取概念类型或概念实体关联的 EXTERNAL_VALUE 类型的属性视图类型中的指定数据
     *
     * @param attributesViewKind AttributesViewKind EXTERNAL_VALUE 类型的属性视图类型对象
     * @param queryParameters QueryParameters 待获取的属性视图类型中数据的查询过滤条件
     * @param attributeValueList List<AttributeValue> 发起数据获取的概念类型或概念实体中的属性列表
     *
     * @return EXTERNAL_VALUE 属性视图类型中符合需求的返回数据列表
     */
    public List<Map<String,Object>> getEntityExternalAttributesValues(AttributesViewKind attributesViewKind,
                                                                      QueryParameters queryParameters,
                                                                      List<AttributeValue> attributeValueList);

    /**
     * 获取概念类型或概念实体关联的 EXTERNAL_VALUE 类型的属性视图类型中的指定数据的数量
     *
     * @param attributesViewKind AttributesViewKind EXTERNAL_VALUE 类型的属性视图类型对象
     * @param attributesParameters AttributesParameters 待获取的属性视图类型中数据的查询过滤条件
     * @param attributeValueList List<AttributeValue> 发起数据获取的概念类型或概念实体中的属性列表
     *
     * @return EXTERNAL_VALUE 属性视图类型中符合需求的数据数量
     */
    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind,
                                                    AttributesParameters attributesParameters,
                                                    List<AttributeValue> attributeValueList);

    /**
     * 删除概念类型或概念实体关联的 EXTERNAL_VALUE 类型的属性视图类型中的指定数据
     *
     * @param attributesViewKind AttributesViewKind EXTERNAL_VALUE 类型的属性视图类型对象
     * @param attributesParameters AttributesParameters 待删除的属性视图类型中数据的查询过滤条件
     * @param attributeValueList List<AttributeValue> 发起数据获取的概念类型或概念实体中的属性列表
     *
     * @return EXTERNAL_VALUE 属性视图类型中成功删除的数据数量
     */
    public Long deleteEntityExternalAttributesValues(AttributesViewKind attributesViewKind,
                                                    AttributesParameters attributesParameters,
                                                    List<AttributeValue> attributeValueList);
}
