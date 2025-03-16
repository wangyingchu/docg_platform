package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.List;
import java.util.Map;

public interface ExternalAttributesValueAccessProcessor {

    public List<Map<String,Object>> getEntityExternalAttributesValues(AttributesViewKind attributesViewKind,
                                                                      QueryParameters queryParameters,
                                                                      List<AttributeValue> attributeValueList);

    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind,
                                                    AttributesParameters attributesParameters,
                                                    List<AttributeValue> attributeValueList);

    public Long deleteEntityExternalAttributesValues(AttributesViewKind attributesViewKind,
                                                    AttributesParameters attributesParameters,
                                                    List<AttributeValue> attributeValueList);
}
