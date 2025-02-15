package com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.relationDB;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.dataExchange.config.ExternalDataExchangePropertiesHandler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.util.*;

public class RelationDBOperationUtil {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // add rewriteBatchedStatements=true and cachePrepStmts=true in JDBC url
    // set session variables by sessionVariables=group_commit=async_mode in JDBC url
    private static final String URL_PATTERN = "jdbc:mysql://%s:%d/%s?useServerPrepStmts=true&useLocalSessionState=true&rewriteBatchedStatements=true&cachePrepStmts=true&prepStmtCacheSqlLimit=99999&prepStmtCacheSize=50&sessionVariables=group_commit=async_mode";
    private static final String HOST = ExternalDataExchangePropertiesHandler.getPropertyValue(ExternalDataExchangePropertiesHandler.APACHE_DORIS_HOST);
    private static final int PORT = Integer.valueOf(ExternalDataExchangePropertiesHandler.getPropertyValue(ExternalDataExchangePropertiesHandler.APACHE_DORIS_PORT));
    private static final String USER = ExternalDataExchangePropertiesHandler.getPropertyValue(ExternalDataExchangePropertiesHandler.APACHE_DORIS_USER);
    private static final String PASSWD = ExternalDataExchangePropertiesHandler.getPropertyValue(ExternalDataExchangePropertiesHandler.APACHE_DORIS_PASSWD);
    private static final int INSERT_BATCH_SIZE = Integer.valueOf(ExternalDataExchangePropertiesHandler.getPropertyValue(ExternalDataExchangePropertiesHandler.APACHE_DORIS_INSERT_BATCH_SIZE));

    public static void insertBatchData(String dbName, String tableName, Map<String, RelationDBPropertyType> propertiesDataTypeMap, List<Map<String,Object>> batchData) {
        insertBatchData(dbName, tableName, propertiesDataTypeMap,batchData,INSERT_BATCH_SIZE);
    }

    public static void insertBatchData(String dbName, String tableName, Map<String, RelationDBPropertyType> propertiesDataTypeMap, List<Map<String,Object>> batchData,int insertBatchSize) {
        if(dbName != null && tableName != null && propertiesDataTypeMap != null && batchData != null) {
            Set<String> sortedSet = new TreeSet<>(propertiesDataTypeMap.keySet());
            String insertSQL = generateInsertSql(tableName, sortedSet);

            int singlePartitionSize = Math.max((batchData.size() / insertBatchSize), 1);
            int singleBatchSize = batchData.size()/singlePartitionSize;
            List<List<Map<String,Object>>> batchesDataList = Lists.partition(batchData, singleBatchSize);

            try {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try (Connection conn = DriverManager.getConnection(String.format(URL_PATTERN, HOST, PORT, dbName), USER, PASSWD)) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {

                    for(List<Map<String,Object>> singleBatchesDataList : batchesDataList) {
                        //each list's data size equals or less than INSERT_BATCH_SIZE
                        int[] executeResult = executeInsertBatch(preparedStatement,sortedSet,propertiesDataTypeMap,singleBatchesDataList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int[] executeInsertBatch(PreparedStatement preparedStatement, Set<String> sortedPropertiesSet, Map<String, RelationDBPropertyType> propertiesDataType, List<Map<String,Object>> dataList) throws SQLException {
        for(Map<String,Object> currentData:dataList){
            setPreparedStatementData(preparedStatement,sortedPropertiesSet,propertiesDataType,currentData);
            preparedStatement.addBatch();
        }
        return preparedStatement.executeBatch();
    }

    private static void setPreparedStatementData(PreparedStatement preparedStatement, Set<String> sortedPropertiesSet, Map<String, RelationDBPropertyType> propertiesDataType, Map<String,Object> currentData) throws SQLException {
        int propertiesIndex = 1;
        for(String currentProperty:sortedPropertiesSet){
            Object propertyValue = currentData.get(currentProperty);
            RelationDBPropertyType propertyType = propertiesDataType.get(currentProperty);
            preparedStatement.setString(propertiesIndex,currentData.get(currentProperty).toString());
            setPropertyValue(propertiesIndex,propertyType,propertyValue,preparedStatement);
            propertiesIndex++;
        }
    }

    private static void setPropertyValue(int index, RelationDBPropertyType propertyType, Object propertyValue, PreparedStatement preparedStatement) throws SQLException {
        switch (propertyType){
            case INT -> preparedStatement.setInt(index, (Integer)propertyValue);
            case DOUBLE -> preparedStatement.setDouble(index, (Double) propertyValue);
            case STRING -> preparedStatement.setString(index, propertyValue.toString());
            case BIG_DECIMAL -> preparedStatement.setBigDecimal(index, (BigDecimal) propertyValue);
            case BOOLEAN -> preparedStatement.setBoolean(index, (Boolean) propertyValue);
            case DATE -> preparedStatement.setDate(index, (Date) propertyValue);
            case FLOAT -> preparedStatement.setFloat(index, (Float) propertyValue);
            case LONG -> preparedStatement.setLong(index, (Long) propertyValue);
            case SHORT -> preparedStatement.setShort(index, (Short) propertyValue);
            case BYTES -> preparedStatement.setBytes(index, (byte[]) propertyValue);
            case BYTE -> preparedStatement.setByte(index, (Byte) propertyValue);
            case OBJECT -> preparedStatement.setObject(index, propertyValue);
            case TIME -> preparedStatement.setTime(index, (Time) propertyValue);
            case TIMESTAMP -> preparedStatement.setTimestamp(index, (Timestamp) propertyValue);
            case NULL -> preparedStatement.setNull(index, Types.NULL);
        }
    }

    private static String generateInsertSql(String tableName,  Set<String> propertiesNameSet) {
        // "INSERT INTO table (k1, k2,k3,k4) VALUES (?,?,?,?)"
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");
        for (String propertyName : propertiesNameSet) {
            sb.append(propertyName).append(",");
        }
        sb.deleteCharAt(sb.length() - 1); // 删除最后一个逗号
        sb.append(") VALUES (");

        for (int i = 0; i < propertiesNameSet.size(); i++) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1); // 删除最后一个逗号
        sb.append(")");
        return sb.toString();
    }
}
