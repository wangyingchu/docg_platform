package com.viewfunction.docg.externalDataExchange;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.ExternalAttributesValueAccessProcessor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestExternalAttributesValueAccessProcessor implements ExternalAttributesValueAccessProcessor {

    @Override
    public List<Map<String, Object>> getEntityExternalAttributesValues(AttributesViewKind attributesViewKind, QueryParameters queryParameters, List<AttributeValue> attributeValueList) {
        System.out.println(attributesViewKind);
        System.out.println(queryParameters);
        System.out.println(attributeValueList);
        Map<String, Object> resultDataMap = new HashMap<>();
        resultDataMap.put("attr001", "attr001Value");
        resultDataMap.put("attr002", 1000);
        return List.of(resultDataMap);
    }

    @Override
    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        System.out.println(attributesViewKind);
        System.out.println(attributesParameters);
        System.out.println(attributeValueList);
        return 100000L;
    }
}
