package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.timeSeriesDB;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.ExternalAttributesValueAccessProcessor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;

public class DefaultTimeSeriesDBExternalAttributesValueAccessProcessor implements ExternalAttributesValueAccessProcessor {

    private static final String JDBC_DRIVER = "org.apache.iotdb.jdbc.IoTDBDriver";
    public final String _ExternalTimeSeriesDB_DefaultDBName = "DOCG_ExternalTimeSeriesDB_DefaultDBName";
    public final String _ExternalTimeSeriesDB_DefaultTableName = "DOCG_ExternalTimeSeriesDB_DefaultTableName";
    public final String _ExternalTimeSeriesDB_Host = "DOCG_ExternalTimeSeriesDB_Host";
    public final String _ExternalTimeSeriesDB_Port = "DOCG_ExternalTimeSeriesDB_Port";
    public final String _ExternalTimeSeriesDB_UserName = "DOCG_ExternalTimeSeriesDB_UserName";
    public final String _ExternalTimeSeriesDB_UserPWD = "DOCG_ExternalTimeSeriesDB_UserPWD";

    @Override
    public List<Map<String, Object>> getEntityExternalAttributesValues(AttributesViewKind attributesViewKind, QueryParameters queryParameters, List<AttributeValue> attributeValueList) {
        if(attributesViewKind != null){
            String dbName = null;
            String tableName = null;
            String host = null;
            String port = null;
            String userName = null;
            String userPWD = null;

            Map<String,Object> metaConfigItems = attributesViewKind.getMetaConfigItems();
            if(metaConfigItems.containsKey(_ExternalTimeSeriesDB_DefaultDBName)){
                dbName = metaConfigItems.get(_ExternalTimeSeriesDB_DefaultDBName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalTimeSeriesDB_DefaultTableName)){
                tableName = attributesViewKind.getMetaConfigItem(_ExternalTimeSeriesDB_DefaultTableName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalTimeSeriesDB_Host)){
                host = attributesViewKind.getMetaConfigItem(_ExternalTimeSeriesDB_Host).toString();
            }
            if(metaConfigItems.containsKey(_ExternalTimeSeriesDB_Port)){
                port = attributesViewKind.getMetaConfigItem(_ExternalTimeSeriesDB_Port).toString();
            }
            if(metaConfigItems.containsKey(_ExternalTimeSeriesDB_UserName)){
                userName = attributesViewKind.getMetaConfigItem(_ExternalTimeSeriesDB_UserName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalTimeSeriesDB_UserPWD)){
                userPWD = attributesViewKind.getMetaConfigItem(_ExternalTimeSeriesDB_UserPWD).toString().trim();
            }

            List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();
            Map<String, AttributeDataType> attributeDataTypeMap = new HashMap<>();
            for(AttributeKind currentAttributeKind:attributeKindList){
                String attributeName = currentAttributeKind.getAttributeKindName();
                AttributeDataType attributeDataType = currentAttributeKind.getAttributeDataType();
                attributeDataTypeMap.put(attributeName,attributeDataType);
            }

            if(!attributeKindList.isEmpty() && dbName != null && tableName != null && host != null && port != null && userName != null && userPWD != null){
                try {
                    String querySQL = TimeSeriesDBQueryBuilder.buildSelectQuerySQL(dbName+"."+tableName,queryParameters);
                    return doQuery(host,port,dbName,userName,userPWD,querySQL,attributeDataTypeMap);
                } catch (CoreRealmServiceEntityExploreException e) {
                    throw new RuntimeException(e);
                }
            }else{
                return null;
            }
        }
        return null;
    }

    @Override
    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
    }

    @Override
    public Long deleteEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        return 0l;
    }

    private List<Map<String, Object>> doQuery(String host,String port, String dbName,String user,String userPWD,String querySQL,Map<String,AttributeDataType> attributeDataTypeMap){
        int dbPort =Integer.parseInt(port);
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // JDBC driver name and database URL
        String url = "jdbc:iotdb://"+host+":"+port+"/";
        // set rpc compress mode
        // String url = "jdbc:iotdb://127.0.0.1:6667?rpc_compress=true";
        try (Connection connection = DriverManager.getConnection(url, user, userPWD)) {
            List<Map<String, Object>> resultList = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(querySQL);
            // Extract data from result set
            while (resultSet.next()) {
                Map<String, Object> currentRowDataMap = new HashMap<>();
                setRowDataMap(currentRowDataMap,attributeDataTypeMap,resultSet);
                resultList.add(currentRowDataMap);
            }
            // Clean up environment
            resultSet.close();
            statement.close();
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setRowDataMap(Map<String, Object> rowDataMap,Map<String,AttributeDataType> attributeDataTypeMap,ResultSet resultSet){
        attributeDataTypeMap.put("Time",AttributeDataType.TIMESTAMP);
        Set<String> attributeName = attributeDataTypeMap.keySet();
        for(String currenAttribute : attributeName){
            AttributeDataType currentAttributeDataType = attributeDataTypeMap.get(currenAttribute);
            try {
                Map<String,String> tsPropertyNameMapping = new HashMap<>();
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                for(int i =1;i<= resultSetMetaData.getColumnCount();i++){
                    String currentTSAttributeName = resultSetMetaData.getColumnName(i);
                    if(!currentTSAttributeName.contains(".")){
                        tsPropertyNameMapping.put(currentTSAttributeName,currentTSAttributeName);
                    }else{
                        String currentPropertyName = currentTSAttributeName.substring(currentTSAttributeName.lastIndexOf(".")+1,currentTSAttributeName.length());
                        tsPropertyNameMapping.put(currentPropertyName,currentTSAttributeName);
                    }
                }
                //BOOLEAN,INT,SHORT,LONG,FLOAT,DOUBLE,TIMESTAMP,DATE,DATETIME,TIME,STRING,BYTE,DECIMAL
                switch(currentAttributeDataType){
                    case STRING -> rowDataMap.put(currenAttribute,resultSet.getString(tsPropertyNameMapping.get(currenAttribute)));
                    case INT -> rowDataMap.put(currenAttribute,resultSet.getInt(tsPropertyNameMapping.get(currenAttribute)));
                    case BOOLEAN -> rowDataMap.put(currenAttribute,resultSet.getBoolean(tsPropertyNameMapping.get(currenAttribute)));
                    case LONG -> rowDataMap.put(currenAttribute,resultSet.getLong(tsPropertyNameMapping.get(currenAttribute)));
                    case FLOAT -> rowDataMap.put(currenAttribute,resultSet.getFloat(tsPropertyNameMapping.get(currenAttribute)));
                    case DOUBLE -> rowDataMap.put(currenAttribute,resultSet.getDouble(tsPropertyNameMapping.get(currenAttribute)));
                    case BYTE -> rowDataMap.put(currenAttribute,resultSet.getByte(tsPropertyNameMapping.get(currenAttribute)));
                    case SHORT -> rowDataMap.put(currenAttribute,resultSet.getShort(tsPropertyNameMapping.get(currenAttribute)));
                    case DECIMAL -> rowDataMap.put(currenAttribute,resultSet.getBigDecimal(tsPropertyNameMapping.get(currenAttribute)));
                    case DATE -> rowDataMap.put(currenAttribute,getLocalDate(resultSet.getDate(tsPropertyNameMapping.get(currenAttribute)))); //LocalDate
                    case TIME -> rowDataMap.put(currenAttribute,getLocalTime(resultSet.getTime(tsPropertyNameMapping.get(currenAttribute)))); //LocalTime
                    case DATETIME -> rowDataMap.put(currenAttribute,getLocalDateTime(resultSet.getTimestamp(tsPropertyNameMapping.get(currenAttribute)))); //LocalDateTime
                    case TIMESTAMP -> rowDataMap.put(currenAttribute,getZonedDateTime(resultSet.getTimestamp(tsPropertyNameMapping.get(currenAttribute)))); //ZonedDateTime
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private LocalDate getLocalDate(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalTime getLocalTime(Time time){
        return time.toLocalTime();
    }

    private LocalDateTime getLocalDateTime(Timestamp timestamp){
        return timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private ZonedDateTime getZonedDateTime(Timestamp timestamp){
        return timestamp.toInstant().atZone(ZoneId.systemDefault());
    }
}
