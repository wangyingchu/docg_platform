package com.viewfunction.docg.coreRealm.realmServiceCore.internal.arcadeDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SQLBuilder {
    public enum QueryLanguage {sql,sqlscript,cypher}
    public enum KindType {ConceptionKind,RelationKind}
    private static Logger logger = LoggerFactory.getLogger(SQLBuilder.class);
    public static String createKindSQL(KindType kindType,String kindName,String parentKindName){
        if(kindType == null){
            return null;
        }
        String operationSQL = null;
        switch (kindType){
            case ConceptionKind -> operationSQL = "CREATE VERTEX TYPE "+kindName+" IF NOT EXISTS";
            case RelationKind -> operationSQL = "CREATE EDGE TYPE "+kindName+" IF NOT EXISTS";
        }
        if(parentKindName != null){
            operationSQL = operationSQL+" EXTENDS "+parentKindName;
        }
        logger.debug("Generated SQL Statement: {}", operationSQL);
        return operationSQL;
    }

    public static String selectWithSinglePropertyValueMatch(String kindName, String propertyName, Object propertyValue, int matchValue){
        String operationSQL = "SELECT FROM "+kindName+" WHERE "+propertyName+" = '"+propertyValue+"' LIMIT "+matchValue;
        logger.debug("Generated SQL Statement: {}", operationSQL);
        return operationSQL;
    }

    public static String createTypeDataWithProperties(String typeName, Map<String, Object> properties){
        StringBuffer propertiesNameSb = new StringBuffer();
        StringBuffer propertiesValuePlaceHolderSb = new StringBuffer();
        propertiesNameSb.append("(");
        propertiesValuePlaceHolderSb.append("(");
        Set<String> propertyNamesSet = properties.keySet();
        List<String> propertyNamesList = new ArrayList<String>(propertyNamesSet);
        for(int i = 0; i< propertyNamesList.size(); i++){
            String currentDataPropertyName = propertyNamesList.get(i);
            propertiesNameSb.append(currentDataPropertyName);
            propertiesValuePlaceHolderSb.append(getPropertyValueSQLText(properties.get(currentDataPropertyName)));
            if(i < propertyNamesSet.size() - 1){
                propertiesNameSb.append(",");
                propertiesValuePlaceHolderSb.append(",");
            }
        }
        propertiesNameSb.append(")");
        propertiesValuePlaceHolderSb.append(")");

        String operationSQL = "INSERT INTO "+typeName+" "+propertiesNameSb.toString() +" VALUES "+propertiesValuePlaceHolderSb.toString();
        return operationSQL;
    }

    private static String getPropertyValueSQLText(Object propertyValue){
        if(propertyValue instanceof String){
            return "'"+propertyValue.toString()+"'";
        }else if(propertyValue instanceof ZonedDateTime){
            ZonedDateTime zonedDateTimePropertyValue = (ZonedDateTime)propertyValue;
            long longValue = zonedDateTimePropertyValue.toInstant().toEpochMilli();
            return "DATE("+longValue+")";
        }else{
            return propertyValue.toString();
        }
    }
}
