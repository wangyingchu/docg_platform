package com.viewfunction.docg.coreRealm.realmServiceCore.internal.arcadeDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class QueryBuilder {
    public enum QueryLanguage {sql,sqlscript,cypher}
    public enum KindType {ConceptionKind,RelationKind}
    private static Logger logger = LoggerFactory.getLogger(QueryBuilder.class);
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

    public static String createTypeDataWithProperties(String[] typeNames, Map<String, Object> properties){

        return null;
    }

}
