package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DynamicContentValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.internal.value.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.*;

public class GetListDynamicContentValueTransformer implements DataTransformer<List<Map<String,DynamicContentValue>>>{

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
    public List<Map<String,DynamicContentValue>> transformResult(Result result) {
        List<Map<String,DynamicContentValue>> dynamicContentValueList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Map<String,DynamicContentValue> currentRowMap = new HashMap<>();
                dynamicContentValueList.add(currentRowMap);
                Record nodeRecord = result.next();
                nodeRecord.fields().stream().forEach(recordField -> {
                    String key = recordField.key();
                    Object value = recordField.value();
                    createAttributeEntity(key,value,currentRowMap,dynamicContentAttributesValueTypeMap);
                });
            }
        }
        return dynamicContentValueList;
    }

    private void createAttributeEntity(String entityKey,Object entityObject,Map<String,DynamicContentValue> currentRowMap,
                                       Map<String,DynamicContentValue.ContentValueType> dynamicContentAttributesValueTypeMap){
        DynamicContentValue dynamicContentValue = new DynamicContentValue();
        dynamicContentValue.setValueName(entityKey);
        boolean isValidObject = true;
        if(entityObject instanceof BooleanValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.BOOLEAN);
            }
            dynamicContentValue.setValueObject(((BooleanValue)entityObject).asBoolean());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.BOOLEAN);
        }else if(entityObject instanceof BytesValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.BYTE_ARRAY);
            }
            dynamicContentValue.setValueObject(((BytesValue)entityObject).asByteArray());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.BYTE_ARRAY);
        }
        else if(entityObject instanceof FloatValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.FLOAT);
            }
            dynamicContentValue.setValueObject(((FloatValue)entityObject).asFloat());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.FLOAT);
        }
        else if(entityObject instanceof IntegerValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.INT);
            }
            dynamicContentValue.setValueObject(((IntegerValue)entityObject).asInt());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.INT);
        }
        else if(entityObject instanceof DateValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.DATE);
            }
            dynamicContentValue.setValueObject(((DateValue)entityObject).asLocalDate());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.DATE);
        }
        else if(entityObject instanceof LocalTimeValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.TIME);
            }
            dynamicContentValue.setValueObject(((LocalTimeValue)entityObject).asLocalTime());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.TIME);
        }
        else if(entityObject instanceof LocalDateTimeValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.DATETIME);
            }
            dynamicContentValue.setValueObject(((LocalDateTimeValue)entityObject).asLocalDateTime());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.DATETIME);
        }
        else if(entityObject instanceof DateTimeValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.TIMESTAMP);
            }
            dynamicContentValue.setValueObject(((DateTimeValue)entityObject).asZonedDateTime());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.TIMESTAMP);
        }
        else if(entityObject instanceof NodeValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.CONCEPTION_ENTITY);
            }
            dynamicContentValue.setValueObject(getConceptionEntityFromNode((NodeValue)entityObject));
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.CONCEPTION_ENTITY);
        }
        else if(entityObject instanceof PathValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.ENTITIES_PATH);
            }
            dynamicContentValue.setValueObject(getEntitiesPathFromPath((PathValue)entityObject));
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.ENTITIES_PATH);
        }
        else if(entityObject instanceof RelationshipValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.RELATION_ENTITY);
            }
            dynamicContentValue.setValueObject(getRelationEntityFromRelationship((RelationshipValue)entityObject));
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.RELATION_ENTITY);
        }
        else if(entityObject instanceof StringValue){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.STRING);
            }
            dynamicContentValue.setValueObject(((StringValue)entityObject).asString());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.STRING);
        }
        else if(entityObject instanceof NumberValueAdapter<?>){
            if(!dynamicContentAttributesValueTypeMap.containsKey(entityKey)){
                dynamicContentAttributesValueTypeMap.put(entityKey, DynamicContentValue.ContentValueType.NUMBER);
            }
            dynamicContentValue.setValueObject(((NumberValueAdapter)entityObject).asNumber());
            dynamicContentValue.setValueType(DynamicContentValue.ContentValueType.NUMBER);
        }
        else if(entityObject instanceof ListValue){
            ((ListValue)entityObject).asList().forEach(entityObjectItem->{
                System.out.println(entityObjectItem.getClass());
            });
            isValidObject = false;
        }
        else{
            isValidObject = false;
        }
        if(isValidObject){
            currentRowMap.put(entityKey,dynamicContentValue);
        }
    }

    private ConceptionEntity getConceptionEntityFromNode(NodeValue resultNode){
        List<String> allConceptionKindNames = Lists.newArrayList(resultNode.asNode().labels());
        String targetConceptionKindName = allConceptionKindNames.get(0);
        long nodeUID = resultNode.asNode().id();
        String conceptionEntityUID = ""+nodeUID;
        Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                new Neo4JConceptionEntityImpl(targetConceptionKindName,conceptionEntityUID);
        neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
        neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
        return neo4jConceptionEntityImpl;
    }

    private RelationEntity getRelationEntityFromRelationship(RelationshipValue resultRelationship){
        String relationType = resultRelationship.asRelationship().type();
        long relationUID = resultRelationship.asRelationship().id();
        String relationEntityUID = ""+relationUID;
        String fromEntityUID = ""+resultRelationship.asRelationship().startNodeId();
        String toEntityUID = ""+resultRelationship.asRelationship().endNodeId();
        Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
        neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
        return neo4jRelationEntityImpl;
    }

    private EntitiesPath getEntitiesPathFromPath(PathValue pathValue){
        String startEntityType = pathValue.asPath().start().labels().iterator().next();
        String startEntityUID = ""+pathValue.asPath().start().id();
        String endEntityType = pathValue.asPath().end().labels().iterator().next();
        String endEntityUID = ""+pathValue.asPath().end().id();
        int pathJumps = pathValue.asPath().length();
        LinkedList<ConceptionEntity> pathConceptionEntities = new LinkedList<>();
        LinkedList<RelationEntity> pathRelationEntities = new LinkedList<>();

        EntitiesPath currentEntitiesPath = new EntitiesPath(startEntityType,startEntityUID,
                endEntityType,endEntityUID,pathJumps,pathConceptionEntities,pathRelationEntities);

        Iterator<Node> nodeIterator = pathValue.asPath().nodes().iterator();
        while(nodeIterator.hasNext()){
            Node currentNode = nodeIterator.next();
            List<String> allConceptionKindNames = Lists.newArrayList(currentNode.labels());
            long nodeUID = currentNode.id();
            String conceptionEntityUID = ""+nodeUID;
            Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                    new Neo4JConceptionEntityImpl(allConceptionKindNames.get(0),conceptionEntityUID);
            neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
            neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            pathConceptionEntities.add(neo4jConceptionEntityImpl);
        }

        Iterator<Relationship> relationIterator = pathValue.asPath().relationships().iterator();
        while(relationIterator.hasNext()){
            Relationship resultRelationship = relationIterator.next();
            String relationType = resultRelationship.type();
            long relationUID = resultRelationship.id();
            String relationEntityUID = ""+relationUID;
            String fromEntityUID = ""+resultRelationship.startNodeId();
            String toEntityUID = ""+resultRelationship.endNodeId();
            Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                    new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
            neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            pathRelationEntities.add(neo4jRelationEntityImpl);
        }

        return currentEntitiesPath;
    }
}
