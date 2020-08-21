package com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public interface Neo4JMetaAttributeFeatureSupportable extends MetaAttributeFeatureSupportable,Neo4JKeyResourcesRetrievable {

    default Date getCreateDateTime() {
        Object returnDataObject = getAttributeValue(RealmConstant._createDateProperty);
        if(returnDataObject != null){
            ZonedDateTime zonedDateTime = (ZonedDateTime)returnDataObject;
            return Date.from(zonedDateTime.toInstant());
        }
        return null;
    }

    default Date getLastModifyDateTime() {
        Object returnDataObject = getAttributeValue(RealmConstant._lastModifyDateProperty);
        if(returnDataObject != null){
            ZonedDateTime zonedDateTime = (ZonedDateTime)returnDataObject;
            return Date.from(zonedDateTime.toInstant());
        }
        return null;
    }

    default String getCreatorId() {
        Object dataOriginObject = getAttributeValue(RealmConstant._creatorIdProperty);
        return dataOriginObject != null? dataOriginObject.toString() : null;
    }

    default String getDataOrigin() {
        Object dataOriginObject = getAttributeValue(RealmConstant._dataOriginProperty);
        return dataOriginObject != null? dataOriginObject.toString() : null;
    }

    default boolean updateLastModifyDateTime() {
        Object resultObject = updateAttributeValue(RealmConstant._lastModifyDateProperty,new Date());
        return resultObject != null ? true : false;
    }

    default boolean updateCreatorId(String creatorId) {
        Object resultObject = updateAttributeValue(RealmConstant._creatorIdProperty,creatorId);
        return resultObject != null ? true : false;
    }

    default boolean updateDataOrigin(String dataOrigin) {
        Object resultObject = updateAttributeValue(RealmConstant._dataOriginProperty,dataOrigin);
        return resultObject != null ? true : false;
    }

    private Object updateAttributeValue(String attributeName,Object attributeValue){
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                Map<String,Object> attributeDataMap = new HashMap<>();
                attributeDataMap.put(attributeName,attributeValue);
                String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.getEntityUID()),attributeDataMap);
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record returnRecord = result.next();
                            Map<String,Object> returnValueMap = returnRecord.asMap();
                            String attributeNameFullName= CypherBuilder.operationResultName+"."+ attributeName;
                            Object attributeValueObject = returnValueMap.get(attributeNameFullName);
                            if(attributeValueObject!= null){
                                return attributeValueObject;
                            }
                        }
                        return null;
                    }
                };
                Object resultRes = workingGraphOperationExecutor.executeWrite(dataTransformer,updateCql);
                return resultRes;
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    private Object getAttributeValue(String attributeName){
        if (getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(getEntityUID()),new String[]{attributeName});
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record returnRecord = result.next();
                            Map<String,Object> returnValueMap = returnRecord.asMap();
                            String attributeNameFullName= CypherBuilder.operationResultName+"."+ attributeName;
                            Object attributeValueObject = returnValueMap.get(attributeNameFullName);
                            if(attributeValueObject!= null){
                                return attributeValueObject;
                            }
                        }
                        return null;
                    }
                };
                Object resultRes = workingGraphOperationExecutor.executeRead(dataTransformer,queryCql);
                return resultRes;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }
}
