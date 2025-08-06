package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.relationDB;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.ExternalAttributesValueAccessProcessor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.*;
import java.time.*;
import java.util.*;

public class DefaultRelationDBExternalAttributesValueAccessProcessor implements ExternalAttributesValueAccessProcessor {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // add rewriteBatchedStatements=true and cachePrepStmts=true in JDBC url
    // set session variables by sessionVariables=group_commit=async_mode in JDBC url
    private static final String URL_PATTERN = "jdbc:mysql://%s:%d/%s?useServerPrepStmts=true&useLocalSessionState=true&rewriteBatchedStatements=true&cachePrepStmts=true&prepStmtCacheSqlLimit=99999&prepStmtCacheSize=50&sessionVariables=group_commit=async_mode";
    public final String _ExternalRelationDB_DefaultDBName = "DOCG_ExternalRelationDB_DefaultDBName";
    public final String _ExternalRelationDB_DefaultTableName = "DOCG_ExternalRelationDB_DefaultTableName";
    public final String _ExternalRelationDB_Host = "DOCG_ExternalRelationDB_Host";
    public final String _ExternalRelationDB_Port = "DOCG_ExternalRelationDB_Port";
    public final String _ExternalRelationDB_UserName = "DOCG_ExternalRelationDB_UserName";
    public final String _ExternalRelationDB_UserPWD = "DOCG_ExternalRelationDB_UserPWD";
    private static Logger logger = LoggerFactory.getLogger(DefaultRelationDBExternalAttributesValueAccessProcessor.class);
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
            if(metaConfigItems.containsKey(_ExternalRelationDB_DefaultDBName)){
                dbName = metaConfigItems.get(_ExternalRelationDB_DefaultDBName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_DefaultTableName)){
                tableName = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_DefaultTableName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_Host)){
                host = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_Host).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_Port)){
                port = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_Port).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_UserName)){
                userName = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_UserName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_UserPWD)){
                userPWD = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_UserPWD).toString().trim();
            }

            List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();
            Map<String,AttributeDataType> attributeDataTypeMap = new HashMap<>();
            for(AttributeKind currentAttributeKind:attributeKindList){
                String attributeName = currentAttributeKind.getAttributeKindName();
                AttributeDataType attributeDataType = currentAttributeKind.getAttributeDataType();
                attributeDataTypeMap.put(attributeName,attributeDataType);
            }

            if(!attributeKindList.isEmpty() && dbName != null && tableName != null && host != null && port != null && userName != null && userPWD != null){
                try {
                    String querySQL = RelationDBQueryBuilder.buildSelectQuerySQL(tableName,queryParameters);
                    logger.debug("Generated SQL Statement: {}", querySQL);
                    return doQuery(host,port,dbName,userName,userPWD,querySQL,attributeDataTypeMap);
                } catch (CoreRealmServiceEntityExploreException e) {
                    throw new RuntimeException(e);
                }
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    @Override
    public Long countEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        if(attributesViewKind != null){
            String dbName = null;
            String tableName = null;
            String host = null;
            String port = null;
            String userName = null;
            String userPWD = null;

            Map<String,Object> metaConfigItems = attributesViewKind.getMetaConfigItems();
            if(metaConfigItems.containsKey(_ExternalRelationDB_DefaultDBName)){
                dbName = metaConfigItems.get(_ExternalRelationDB_DefaultDBName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_DefaultTableName)){
                tableName = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_DefaultTableName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_Host)){
                host = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_Host).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_Port)){
                port = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_Port).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_UserName)){
                userName = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_UserName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_UserPWD)){
                userPWD = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_UserPWD).toString().trim();
            }

            List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();
            Map<String,AttributeDataType> attributeDataTypeMap = new HashMap<>();
            for(AttributeKind currentAttributeKind:attributeKindList){
                String attributeName = currentAttributeKind.getAttributeKindName();
                AttributeDataType attributeDataType = currentAttributeKind.getAttributeDataType();
                attributeDataTypeMap.put(attributeName,attributeDataType);
            }

            if(!attributeKindList.isEmpty() && dbName != null && tableName != null && host != null && port != null && userName != null && userPWD != null){
                try {
                    String querySQL = RelationDBQueryBuilder.buildCountQuerySQL(tableName,attributesParameters);
                    logger.debug("Generated SQL Statement: {}", querySQL);
                    return doCount(host,port,dbName,userName,userPWD,querySQL);
                } catch (CoreRealmServiceEntityExploreException e) {
                    throw new RuntimeException(e);
                }
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    @Override
    public Long deleteEntityExternalAttributesValues(AttributesViewKind attributesViewKind, AttributesParameters attributesParameters, List<AttributeValue> attributeValueList) {
        if(attributesViewKind != null){
            String dbName = null;
            String tableName = null;
            String host = null;
            String port = null;
            String userName = null;
            String userPWD = null;

            Map<String,Object> metaConfigItems = attributesViewKind.getMetaConfigItems();
            if(metaConfigItems.containsKey(_ExternalRelationDB_DefaultDBName)){
                dbName = metaConfigItems.get(_ExternalRelationDB_DefaultDBName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_DefaultTableName)){
                tableName = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_DefaultTableName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_Host)){
                host = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_Host).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_Port)){
                port = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_Port).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_UserName)){
                userName = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_UserName).toString();
            }
            if(metaConfigItems.containsKey(_ExternalRelationDB_UserPWD)){
                userPWD = attributesViewKind.getMetaConfigItem(_ExternalRelationDB_UserPWD).toString().trim();
            }

            List<AttributeKind> attributeKindList = attributesViewKind.getContainsAttributeKinds();
            Map<String,AttributeDataType> attributeDataTypeMap = new HashMap<>();
            for(AttributeKind currentAttributeKind:attributeKindList){
                String attributeName = currentAttributeKind.getAttributeKindName();
                AttributeDataType attributeDataType = currentAttributeKind.getAttributeDataType();
                attributeDataTypeMap.put(attributeName,attributeDataType);
            }

            if(!attributeKindList.isEmpty() && dbName != null && tableName != null && host != null && port != null && userName != null && userPWD != null){
                try {
                    String querySQL = RelationDBQueryBuilder.buildDeleteQuerySQL(tableName,attributesParameters);
                    logger.debug("Generated SQL Statement: {}", querySQL);
                    return doDelete(host,port,dbName,userName,userPWD,querySQL);
                } catch (CoreRealmServiceEntityExploreException e) {
                    throw new RuntimeException(e);
                }
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    private List<Map<String, Object>> doQuery(String host,String port, String dbName,String user,String userPWD,String querySQL,Map<String,AttributeDataType> attributeDataTypeMap){
        int dbPort =Integer.parseInt(port);
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(String.format(URL_PATTERN, host, dbPort, dbName), user, userPWD)) {
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

    private Long doCount(String host, String port, String dbName, String user, String userPWD, String QuerySQL){
        int dbPort =Integer.parseInt(port);
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(String.format(URL_PATTERN, host, dbPort, dbName), user, userPWD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(QuerySQL);
            // Extract data from result set
            int count = 0;
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            // Clean up environment
            resultSet.close();
            statement.close();
            return (long) count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

    private Long doDelete(String host, String port, String dbName, String user, String userPWD, String deleteQuery){
        int dbPort =Integer.parseInt(port);
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(String.format(URL_PATTERN, host, dbPort, dbName), user, userPWD)) {
            PreparedStatement preparedStatement = null;
            // Create a prepared statement for delete
            preparedStatement = connection.prepareStatement(deleteQuery);
            int rowsDeleted = preparedStatement.executeUpdate();
            preparedStatement.close();
            return (long) rowsDeleted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

    private void setRowDataMap(Map<String, Object> rowDataMap,Map<String,AttributeDataType> attributeDataTypeMap,ResultSet resultSet){
        Set<String> attributeName = attributeDataTypeMap.keySet();
        for(String currenAttribute : attributeName){
            AttributeDataType currentAttributeDataType = attributeDataTypeMap.get(currenAttribute);
            try {
                //BOOLEAN,INT,SHORT,LONG,FLOAT,DOUBLE,TIMESTAMP,DATE,DATETIME,TIME,STRING,BYTE,DECIMAL
                switch(currentAttributeDataType){
                    case STRING -> rowDataMap.put(currenAttribute,resultSet.getString(currenAttribute));
                    case INT -> rowDataMap.put(currenAttribute,resultSet.getInt(currenAttribute));
                    case BOOLEAN -> rowDataMap.put(currenAttribute,resultSet.getBoolean(currenAttribute));
                    case LONG -> rowDataMap.put(currenAttribute,resultSet.getLong(currenAttribute));
                    case FLOAT -> rowDataMap.put(currenAttribute,resultSet.getFloat(currenAttribute));
                    case DOUBLE -> rowDataMap.put(currenAttribute,resultSet.getDouble(currenAttribute));
                    case BYTE -> rowDataMap.put(currenAttribute,resultSet.getByte(currenAttribute));
                    case SHORT -> rowDataMap.put(currenAttribute,resultSet.getShort(currenAttribute));
                    case DECIMAL -> rowDataMap.put(currenAttribute,resultSet.getBigDecimal(currenAttribute));
                    case DATE -> rowDataMap.put(currenAttribute,getLocalDate(resultSet.getDate(currenAttribute))); //LocalDate
                    case TIME -> rowDataMap.put(currenAttribute,getLocalTime(resultSet.getTime(currenAttribute))); //LocalTime
                    case DATETIME -> rowDataMap.put(currenAttribute,getLocalDateTime(resultSet.getTimestamp(currenAttribute))); //LocalDateTime
                    case TIMESTAMP -> rowDataMap.put(currenAttribute,getZonedDateTime(resultSet.getTimestamp(currenAttribute))); //ZonedDateTime
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
