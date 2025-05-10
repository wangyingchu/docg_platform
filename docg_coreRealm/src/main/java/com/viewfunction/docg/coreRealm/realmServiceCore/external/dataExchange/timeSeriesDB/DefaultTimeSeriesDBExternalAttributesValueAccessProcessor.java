package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.timeSeriesDB;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.ExternalAttributesValueAccessProcessor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.List;
import java.util.Map;

public class DefaultTimeSeriesDBExternalAttributesValueAccessProcessor implements ExternalAttributesValueAccessProcessor {
    @Override
    public List<Map<String, Object>> getEntityExternalAttributesValues(AttributesViewKind attributesViewKind, QueryParameters queryParameters, List<AttributeValue> attributeValueList) {
        return List.of();
    }

    @Override
    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
    }

    @Override
    public Long deleteEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
    }
}
