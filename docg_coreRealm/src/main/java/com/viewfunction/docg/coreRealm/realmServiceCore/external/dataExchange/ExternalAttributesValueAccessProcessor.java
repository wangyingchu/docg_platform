package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.List;
import java.util.Map;

public interface ExternalAttributesValueAccessProcessor {

    public List<Map<String,Object>> getEntityExternalAttributesValues(AttributesViewKind attributesViewKind, QueryParameters queryParameters,Map<String,Object> entityAttributesValues);
}
