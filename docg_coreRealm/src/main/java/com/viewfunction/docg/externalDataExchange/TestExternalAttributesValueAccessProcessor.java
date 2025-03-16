package com.viewfunction.docg.externalDataExchange;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.ExternalAttributesValueAccessProcessor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.*;

public class TestExternalAttributesValueAccessProcessor implements ExternalAttributesValueAccessProcessor {

    @Override
    public List<Map<String, Object>> getEntityExternalAttributesValues(AttributesViewKind attributesViewKind, QueryParameters queryParameters, List<AttributeValue> attributeValueList) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        System.out.println(attributesViewKind);
        System.out.println(queryParameters);
        System.out.println(attributeValueList);
        List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();

        for(int i=0;i<100000;i++){
            Map<String, Object> resultDataMap = new HashMap<>();
            for(AttributeKind currentAttributeKind:attributeKindList){
                resultDataMap.put(currentAttributeKind.getAttributeKindName(), ""+i+"_"+UUID.randomUUID());
            }
            resultList.add(resultDataMap);
        }

        return resultList;
    }

    @Override
    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        System.out.println(attributesViewKind);
        System.out.println(attributesParameters);
        System.out.println(attributeValueList);
        return 100000L;
    }

    @Override
    public Long deleteEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
    }
}
