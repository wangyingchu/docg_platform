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
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

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
        String relationType = resultRelationship.type();
        long relationUID = resultRelationship.id();
        String relationEntityUID = ""+relationUID;
        String fromEntityUID = ""+resultRelationship.startNodeId();
        String toEntityUID = ""+resultRelationship.endNodeId();
        Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
        neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
        return neo4jRelationEntityImpl;
    }

    private EntitiesPath getEntitiesPathFromPath(Path path){
        String startEntityType = path.start().labels().iterator().next();
        String startEntityUID = ""+path.start().id();
        String endEntityType = path.end().labels().iterator().next();
        String endEntityUID = ""+path.end().id();
        int pathJumps = path.length();
        LinkedList<ConceptionEntity> pathConceptionEntities = new LinkedList<>();
        LinkedList<RelationEntity> pathRelationEntities = new LinkedList<>();

        EntitiesPath currentEntitiesPath = new EntitiesPath(startEntityType,startEntityUID,
                endEntityType,endEntityUID,pathJumps,pathConceptionEntities,pathRelationEntities);

        Iterator<Node> nodeIterator = path.nodes().iterator();
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

        Iterator<Relationship> relationIterator = path.relationships().iterator();
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
