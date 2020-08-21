package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.time.ZonedDateTime;
import java.util.*;

public interface MetaConfigItemFeatureSupportable extends KeyResourcesRetrievable{

    default boolean addOrUpdateMetaConfigItem(String itemName,Object itemValue) {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                Long storageNodeUID = getStorageNodeUID(workingGraphOperationExecutor);
                Map<String,Object> attributeDataMap = new HashMap<>();
                attributeDataMap.put(itemName,itemValue);
                String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,storageNodeUID,attributeDataMap);
                DataTransformer updateItemDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record returnRecord = result.next();
                            Map<String,Object> returnValueMap = returnRecord.asMap();
                            String attributeNameFullName= CypherBuilder.operationResultName+"."+ itemName;
                            Object attributeValueObject = returnValueMap.get(attributeNameFullName);
                            if(attributeValueObject!= null){
                                return attributeValueObject;
                            }
                        }
                        return null;
                    }
                };
                Object resultRes = workingGraphOperationExecutor.executeWrite(updateItemDataTransformer,updateCql);
                return resultRes != null ? true: false;
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    default Map<String,Object> getMetaConfigItems(){
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                Long storageNodeUID = getStorageNodeUID(workingGraphOperationExecutor);
                String queryStorageNodeCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,storageNodeUID,null,null);
                DataTransformer queryNodeDataTransformer = new DataTransformer() {
                    @Override
                    public Map<String,Object> transformResult(Result result) {
                        Record nodeRecord = result.next();
                        if(nodeRecord != null){
                            Node resultStorageNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            return resultStorageNode.asMap();
                        }
                        return null;
                    }
                };
                Map<String,Object> resultRes = (Map<String,Object>)workingGraphOperationExecutor.executeRead(queryNodeDataTransformer,queryStorageNodeCql);
                return reformatConfigItems(resultRes);
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default Object getMetaConfigItem(String itemName){
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                Long storageNodeUID = getStorageNodeUID(workingGraphOperationExecutor);
                String queryStorageNodeCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,storageNodeUID,null,null);
                DataTransformer queryNodeDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        Record nodeRecord = result.next();
                        if(nodeRecord != null){
                            Node resultStorageNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            Object resultValueObj = resultStorageNode.get(itemName).asObject();
                            if(resultValueObj instanceof ZonedDateTime){
                                ZonedDateTime currentZonedDateTime = (ZonedDateTime)resultValueObj;
                                Date currentDate = Date.from(currentZonedDateTime.toInstant());
                                return currentDate;
                            }else{
                                return resultValueObj;
                            }
                        }
                        return null;
                    }
                };
                return workingGraphOperationExecutor.executeRead(queryNodeDataTransformer,queryStorageNodeCql);
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default boolean deleteMetaConfigItem(String itemName){
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                Long storageNodeUID = getStorageNodeUID(workingGraphOperationExecutor);
                List<String> targetAttributeNameList = new ArrayList<>();
                targetAttributeNameList.add(itemName);
                String deleteCql = CypherBuilder.removeNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,storageNodeUID,targetAttributeNameList);
                GetListFormatAggregatedReturnValueTransformer getListFormatAggregatedReturnValueTransformer = new GetListFormatAggregatedReturnValueTransformer("keys");
                Object removeResultRes = workingGraphOperationExecutor.executeWrite(getListFormatAggregatedReturnValueTransformer,deleteCql);
                List<String> returnAttributeNameList = (List<String>)removeResultRes;
                if(returnAttributeNameList.contains(itemName)){
                    return false;
                }else{
                    return true;
                }
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    private Long getStorageNodeUID(GraphOperationExecutor workingGraphOperationExecutor){
        /*
            MATCH (sourceNode) WHERE id(sourceNode) = 1248
            MERGE (sourceNode)-[:`DOCG_MetaConfigItemsStorageIs`]->(operationResult:`DOCG_MetaConfigItemsStorage`)
            RETURN operationResult
        */
        String storageNodeQueryCql = CypherBuilder.mergeRelatedNodesFromSpecialStartNodes(
                CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.getEntityUID()),
                RealmConstant.MetaConfigItemsStorageClass,RealmConstant.Kind_MetaConfigItemsStorageRelationClass, RelationDirection.TO, null);
        DataTransformer<Long> queryNodeDataTransformer = new DataTransformer() {
            @Override
            public Long transformResult(Result result) {
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Node resultStorageNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                    return resultStorageNode.id();
                }
                return null;
            }
        };
        Long storageNodeUID = (Long)workingGraphOperationExecutor.executeWrite(queryNodeDataTransformer,storageNodeQueryCql);
        return storageNodeUID;
    }

    private Map<String,Object> reformatConfigItems(Map<String,Object> orgValueMap){
        Map<String,Object> resultMap = new HashMap<>();
        if(orgValueMap != null){
            Iterator<String> keyItor = orgValueMap.keySet().iterator();
            while(keyItor.hasNext()){
                String key = keyItor.next();
                Object value = orgValueMap.get(key);
                if(value instanceof ZonedDateTime){
                    ZonedDateTime currentZonedDateTime = (ZonedDateTime)value;
                    Date currentDate = Date.from(currentZonedDateTime.toInstant());
                    resultMap.put(key,currentDate);
                }else{
                    resultMap.put(key,value);
                }
            }
        }
        return resultMap;
    }
}
