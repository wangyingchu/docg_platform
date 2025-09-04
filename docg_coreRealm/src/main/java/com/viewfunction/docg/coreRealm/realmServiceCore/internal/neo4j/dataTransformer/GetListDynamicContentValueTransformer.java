package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DynamicContentValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetListDynamicContentValueTransformer implements DataTransformer<List<DynamicContentValue>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;
    private Map<String,DynamicContentValue.ContentValueType> dynamicContentAttributesValueTypeMap;

    public GetListDynamicContentValueTransformer(String currentCoreRealmName,Map<String,DynamicContentValue.ContentValueType> dynamicContentAttributesValueTypeMap,
                                                 GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.dynamicContentAttributesValueTypeMap = dynamicContentAttributesValueTypeMap;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<DynamicContentValue> transformResult(Result result) {
        List<DynamicContentValue> dynamicContentValueList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                nodeRecord.fields().stream().forEach(recordField -> {
                    String key = recordField.key();
                    Object value = recordField.value();
                    createAttributeEntity(key,value,dynamicContentValueList,dynamicContentAttributesValueTypeMap);
                });
            }
        }
        return dynamicContentValueList;
    }

    private void createAttributeEntity(String entityKey,Object entityObject,List<DynamicContentValue> dynamicContentValueList,
                                       Map<String,DynamicContentValue.ContentValueType> dynamicContentAttributesValueTypeMap){
        DynamicContentValue dynamicContentValue = new DynamicContentValue();
        dynamicContentValue.setValueName(entityKey);
        dynamicContentValueList.add(dynamicContentValue);
        if(entityObject instanceof Boolean){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.BOOLEAN);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.BOOLEAN);
        }else if(entityObject instanceof byte[]){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.BYTE_ARRAY);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.BYTE_ARRAY);
        }
        else if(entityObject instanceof Double){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.DOUBLE);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.DOUBLE);
        }
        else if(entityObject instanceof Float){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.FLOAT);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.FLOAT);
        }
        else if(entityObject instanceof Integer){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.INT);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.INT);
        }
        else if(entityObject instanceof LocalDate){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.DATE);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.DATE);
        }
        else if(entityObject instanceof LocalTime){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.TIME);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.TIME);
        }
        else if(entityObject instanceof LocalDateTime){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.DATETIME);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.DATETIME);
        }
        else if(entityObject instanceof Long){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.LONG);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.LONG);
        }
        else if(entityObject instanceof Node){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.CONCEPTION_ENTITY);
            }
            dynamicContentValue.setValueObject(getConceptionEntityFromNode((Node)entityObject));
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.CONCEPTION_ENTITY);
        }
        else if(entityObject instanceof Path){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.ENTITIES_PATH);
            }
            dynamicContentValue.setValueObject(getEntitiesPathFromPath((Path)entityObject));
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.ENTITIES_PATH);
        }
        else if(entityObject instanceof Relationship){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.RELATION_ENTITY);
            }
            dynamicContentValue.setValueObject(getRelationEntityFromRelationship((Relationship)entityObject));
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.RELATION_ENTITY);
        }
        else if(entityObject instanceof String){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.STRING);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.STRING);
        }
        else if(entityObject instanceof ZonedDateTime){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.TIMESTAMP);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.TIMESTAMP);
        }
        else if(entityObject instanceof Number){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.NUMBER);
            }
            dynamicContentValue.setValueObject(entityObject);
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.NUMBER);
        }
        else{

        }
    }

    private ConceptionEntity getConceptionEntityFromNode(Node resultNode){
        List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
        String targetConceptionKindName = allConceptionKindNames.get(0);
        long nodeUID = resultNode.id();
        String conceptionEntityUID = ""+nodeUID;
        Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                new Neo4JConceptionEntityImpl(targetConceptionKindName,conceptionEntityUID);
        neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
        neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
        return neo4jConceptionEntityImpl;
    }

    private RelationEntity getRelationEntityFromRelationship(Relationship resultRelationship){







/*

        Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
        Node sourceNode = nodeRecord.containsKey(CypherBuilder.sourceNodeName) ? nodeRecord.get(CypherBuilder.sourceNodeName).asNode():null;
        Node targetNode = nodeRecord.containsKey(CypherBuilder.targetNodeName) ? nodeRecord.get(CypherBuilder.targetNodeName).asNode():null;
        String relationType = resultRelationship.type();
        boolean isMatchedKind;
        // if the relationEntity is come from a DELETE relation operation,the relationType will be empty string,
        // make isMatchedKind to be true at this case
        if(this.targetRelationKindName == null || relationType.equals("")){
            isMatchedKind = true;
        }else{
            isMatchedKind = relationType.equals(targetRelationKindName)? true : false;
        }
        if(isMatchedKind){
            long relationUID = resultRelationship.id();
            String relationEntityUID = ""+relationUID;
            String fromEntityUID = ""+resultRelationship.startNodeId();
            String toEntityUID = ""+resultRelationship.endNodeId();
            Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                    new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
            if(sourceNode != null){
                Iterable<String> sourceNodeLabels = sourceNode.labels();
                List<String> sourceNodeLabelList = Lists.newArrayList(sourceNodeLabels);
                String sourceNodeId = ""+sourceNode.id();
                if(sourceNodeId.equals(fromEntityUID)){
                    neo4jRelationEntityImpl.setFromEntityConceptionKindList(sourceNodeLabelList);
                }else{
                    neo4jRelationEntityImpl.setToEntityConceptionKindList(sourceNodeLabelList);
                }
            }
            if(targetNode != null){
                Iterable<String> targetNodeLabels = targetNode.labels();
                List<String> targetNodeLabelList = Lists.newArrayList(targetNodeLabels);
                String targetNodeId = ""+targetNode.id();
                if(targetNodeId.equals(toEntityUID)){
                    neo4jRelationEntityImpl.setToEntityConceptionKindList(targetNodeLabelList);
                }else{
                    neo4jRelationEntityImpl.setFromEntityConceptionKindList(targetNodeLabelList);
                }
            }
            neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            return neo4jRelationEntityImpl;
        }else{
            return null;
        }


        */








        return null;
    }

    private EntitiesPath getEntitiesPathFromPath(Path path){
        return null;
    }
}
