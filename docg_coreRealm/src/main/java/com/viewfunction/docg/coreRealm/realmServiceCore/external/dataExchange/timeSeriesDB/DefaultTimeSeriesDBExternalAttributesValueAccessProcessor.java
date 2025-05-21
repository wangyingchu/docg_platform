package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.timeSeriesDB;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.ExternalAttributesValueAccessProcessor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.List;
import java.util.Map;

public class DefaultTimeSeriesDBExternalAttributesValueAccessProcessor implements ExternalAttributesValueAccessProcessor {

    private static final String JDBC_DRIVER = "org.apache.iotdb.jdbc.IoTDBDriver";
    private static final String URL_PATTERN = "jdbc:iotdb://%s:%d/";
    public final String _ExternalTimeSeriesDB_DefaultDBName = "DOCG_ExternalTimeSeriesDB_DefaultDBName";
    public final String _ExternalTimeSeriesDB_DefaultTableName = "DOCG_ExternalTimeSeriesDB_DefaultTableName";
    public final String _ExternalTimeSeriesDB_Host = "DOCG_ExternalTimeSeriesDB_Host";
    public final String _ExternalTimeSeriesDB_Port = "DOCG_ExternalTimeSeriesDB_Port";
    public final String _ExternalTimeSeriesDB_UserName = "DOCG_ExternalTimeSeriesDB_UserName";
    public final String _ExternalTimeSeriesDB_UserPWD = "DOCG_ExternalTimeSeriesDB_UserPWD";

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
