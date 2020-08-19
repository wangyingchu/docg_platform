package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import org.neo4j.cypherdsl.core.*;
import org.neo4j.cypherdsl.core.renderer.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class CypherBuilder {

    private static Logger logger = LoggerFactory.getLogger(CypherBuilder.class);
    private static final Renderer cypherRenderer = Renderer.getDefaultRenderer();
    public static final String operationResultName = "operationResult";
    public static final String neo4jID_propertyName = "identity";
    public enum CypherFunctionType{
        COUNT,ID,KEYS,PROPERTIES,EXISTS
    }
    private static final ZoneId systemDefaultZoneId = ZoneId.systemDefault();

    public static String matchLabelWithSinglePropertyValue(String labelName,String propertyName,Object propertyValue,int matchValue){
        Node m = Cypher.node(labelName).named(operationResultName).withProperties(propertyName, Cypher.literalOf(propertyValue));
        Statement statement = Cypher.match(m)
                .returning(m)
                .limit(matchValue)
                .build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String createLabeledNodeWithProperties(String labelName, Map<String,Object> properties){
        Node m = Cypher.node(labelName).named(operationResultName);
        Statement statement;
        if(properties != null && properties.size()>0){
            Operation[] propertiesArray = getPropertiesSettingArray(m,properties);
            statement = Cypher.create(m).set(propertiesArray).returning(m).build();
        }else{
            m = Cypher.node(labelName).named(operationResultName);
            statement = Cypher.create(m).returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    private static Operation[] getPropertiesSettingArray(Node m,Map<String,Object> properties){
        Map<String,Object> realPropertiesData = CommonOperationUtil.reformatPropertyValues(properties);
        properties.clear();
        List<Operation> propertyValuesList = new ArrayList<>();
        java.util.Set<String> propertyNameSet = realPropertiesData.keySet();
        if(!propertyNameSet.isEmpty()){
            Iterator<String> propertyNameIterator = propertyNameSet.iterator();
            while(propertyNameIterator.hasNext()){
                String propertyName = propertyNameIterator.next();
                Object propertyValue = realPropertiesData.get(propertyName);
                if(propertyValue instanceof CharSequence || propertyValue instanceof Number ||
                        propertyValue instanceof Iterable || propertyValue instanceof Boolean){
                    propertyValuesList.add(m.property(propertyName).to(Cypher.literalOf(propertyValue)));
                }
                else if(propertyValue instanceof ZonedDateTime){
                    ZonedDateTime targetZonedDateTime = (ZonedDateTime)propertyValue;
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    propertyValuesList.add(m.property(propertyName).
                            to(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)))
                    );
                }
                else if(propertyValue instanceof Date){
                    ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date)propertyValue).toInstant(), systemDefaultZoneId);
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    propertyValuesList.add(m.property(propertyName).
                            to(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)))
                    );
                }
                else if(propertyValue instanceof Date[]){
                    Date[] dateValueArray = (Date[])propertyValue;
                    Expression[] dataValueExpressArray = new Expression[dateValueArray.length];
                    for(int i=0;i<dateValueArray.length;i++){
                        Date currentValue = dateValueArray[i];
                        ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(currentValue.toInstant(), systemDefaultZoneId);
                        String targetZonedDateTimeString = targetZonedDateTime.toString();
                        dataValueExpressArray[i] = Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
                    }
                    propertyValuesList.add(m.property(propertyName).to(Cypher.listOf(dataValueExpressArray)));
                }
            }
        }
        Operation[] propertiesArray=propertyValuesList.toArray(new Operation[propertyValuesList.size()]);
        return propertiesArray;
    }

    public static String createMultiLabeledNodesWithProperties(String labelName, List<Map<String,Object>> propertiesList){
        if(propertiesList != null && propertiesList.size() > 0){
            Node[] targetNodeArray = new Node[propertiesList.size()];
            Map<String,Object> currentPropertyMap = propertiesList.get(0);
            MapExpression targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(currentPropertyMap));
            Node m = Cypher.node(labelName).named(operationResultName+0).withProperties(targetMapExpression);
            targetNodeArray[0] = m;
            StatementBuilder.OngoingUpdate currentOngoingUpdate =Cypher.create(targetNodeArray[0]);
            for( int i=1;i<propertiesList.size();i++){
                currentPropertyMap = propertiesList.get(i);
                targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(currentPropertyMap));
                Node currentN = Cypher.node(labelName).named(operationResultName+i).withProperties(targetMapExpression);
                targetNodeArray[i] = currentN;
                currentOngoingUpdate =currentOngoingUpdate.create(targetNodeArray[i]);
            }
            Statement statement = currentOngoingUpdate.returning(targetNodeArray).build();

            String rel = cypherRenderer.render(statement);
            logger.debug("Generated Cypher Statement: {}",rel);
            return rel;
        }
        return null;
    }

    public static String matchLabelWithSinglePropertyValueAndFunction(String labelName,CypherFunctionType cypherFunctionType,String propertyName,Object propertyValue){
        Node m;
        if(propertyName != null){
            m = Cypher.node(labelName).named(operationResultName).withProperties(propertyName, Cypher.literalOf(propertyValue));
        }else{
            m = Cypher.node(labelName).named(operationResultName);
        }
        StatementBuilder.OngoingReadingWithoutWhere currentOngoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn;
        switch(cypherFunctionType){
            case COUNT:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(Functions.count(m));
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(m);
        }
        Statement statement = ongoingReadingAndReturn.build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String deleteLabelWithSinglePropertyValueAndFunction(String labelName,CypherFunctionType cypherFunctionType,String propertyName,Object propertyValue){
        Node m;
        if(propertyName != null){
            m = Cypher.node(labelName).named(operationResultName).withProperties(propertyName, Cypher.literalOf(propertyValue));
        }else{
            m = Cypher.node(labelName).named(operationResultName);
        }
        StatementBuilder.OngoingReadingWithoutWhere currentOngoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn;
        switch(cypherFunctionType){
            case COUNT:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.detachDelete(m).returning(Functions.count(m));
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.detachDelete(m).returning(m);
        }
        Statement statement = ongoingReadingAndReturn.build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String matchNodeWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType,Object propertyValue,CypherFunctionType returnFunctionType,String additionalPropertyName){
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch(propertyFunctionType){
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement = null;
        if(returnFunctionType !=null) {
            switch (returnFunctionType) {
                case KEYS:
                    if(ongoingReadingWithWhere != null){
                        statement = ongoingReadingWithWhere.returning(Functions2.keys(m)).build();
                    }else{
                        statement = ongoingReadingWithoutWhere.returning(Functions2.keys(m)).build();
                    }
                    break;
                case PROPERTIES:
                    if(ongoingReadingWithWhere != null){
                        statement = ongoingReadingWithWhere.returning(Functions2.properties(m)).build();
                    }else{
                        statement = ongoingReadingWithoutWhere.returning(Functions2.properties(m)).build();
                    }
                    break;
                case EXISTS:
                    if(ongoingReadingWithWhere != null){
                        statement = ongoingReadingWithWhere.returning(Functions.exists(m.property(additionalPropertyName))).build();
                    }else{
                        statement = ongoingReadingWithoutWhere.returning(Functions.exists(m.property(additionalPropertyName))).build();
                    }
            }
        }else{
            if(ongoingReadingWithWhere != null){
                statement = ongoingReadingWithWhere.returning(m).build();
            }else{
                statement = ongoingReadingWithoutWhere.returning(m).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String deleteNodeWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType,Object propertyValue,CypherFunctionType returnFunctionType,String additionalPropertyName){
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch(propertyFunctionType){
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement = null;
        if(returnFunctionType !=null) {
            switch (returnFunctionType) {
                case KEYS:
                    if(ongoingReadingWithWhere != null){
                        statement = ongoingReadingWithWhere.detachDelete(m).returning(Functions2.keys(m)).build();
                    }else{
                        statement = ongoingReadingWithoutWhere.detachDelete(m).returning(Functions2.keys(m)).build();
                    }
                    break;
                case PROPERTIES:
                    if(ongoingReadingWithWhere != null){
                        statement = ongoingReadingWithWhere.detachDelete(m).returning(Functions2.properties(m)).build();
                    }else{
                        statement = ongoingReadingWithoutWhere.detachDelete(m).returning(Functions2.properties(m)).build();
                    }
                    break;
                case EXISTS:
                    if(ongoingReadingWithWhere != null){
                        statement = ongoingReadingWithWhere.detachDelete(m).returning(Functions.exists(m.property(additionalPropertyName))).build();
                    }else{
                        statement = ongoingReadingWithoutWhere.detachDelete(m).returning(Functions.exists(m.property(additionalPropertyName))).build();
                    }
            }
        }else{
            if(ongoingReadingWithWhere != null){
                statement = ongoingReadingWithWhere.detachDelete(m).returning(m).build();
            }else{
                statement = ongoingReadingWithoutWhere.detachDelete(m).returning(m).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String matchNodePropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType,Object propertyValue,String[] targetPropertyNames){
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch(propertyFunctionType){
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement;
        if(targetPropertyNames !=null && targetPropertyNames.length>0) {
            Property[] targetPropertiesArray = new Property[targetPropertyNames.length];
            for (int i = 0; i < targetPropertyNames.length; i++) {
                targetPropertiesArray[i] = m.property(targetPropertyNames[i]);
            }
            statement = ongoingReadingWithWhere.returning(targetPropertiesArray).build();
        }else{
            statement = ongoingReadingWithWhere.returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String setNodePropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType,Object propertyValue,Map<String,Object> originalTargetPropertiesMap){
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch(propertyFunctionType){
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement;
        Map<String,Object> targetPropertiesMap = CommonOperationUtil.reformatPropertyValues(originalTargetPropertiesMap);
        if(targetPropertiesMap != null && targetPropertiesMap.size()>0) {
            Expression[] targetPropertiesArray = new Expression[targetPropertiesMap.size()];
            Property[] targetNewAddPropertiesArray = new Property[targetPropertiesMap.size()];
            Object[] keysObjectArray = targetPropertiesMap.keySet().toArray();
            for (int i = 0; i < keysObjectArray.length; i++) {
                String currentKey = keysObjectArray[i].toString();
                Object currentValue = targetPropertiesMap.get(currentKey);
                targetNewAddPropertiesArray[i] = m.property(currentKey);

                if(currentValue instanceof ZonedDateTime){
                    ZonedDateTime targetZonedDateTime = (ZonedDateTime)currentValue;
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    targetPropertiesArray[i] = m.property(currentKey).to(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)));
                }
                else if(currentValue instanceof Date){
                    ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date)currentValue).toInstant(), systemDefaultZoneId);
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    targetPropertiesArray[i] = m.property(currentKey).to(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)));
                }
                else if(currentValue instanceof CharSequence || currentValue instanceof Number ||
                        currentValue instanceof Iterable || currentValue instanceof Boolean){
                    targetPropertiesArray[i] = m.property(currentKey).to(Cypher.literalOf(currentValue));
                }
                else if(currentValue instanceof Date[]){
                    Date[] dateValueArray = (Date[])currentValue;
                    Expression[] dataValueExpressArray = new Expression[dateValueArray.length];
                    for(int j=0;j<dateValueArray.length;j++){
                        Date currentInnerValue = dateValueArray[j];
                        ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(currentInnerValue.toInstant(), systemDefaultZoneId);
                        String targetZonedDateTimeString = targetZonedDateTime.toString();
                        dataValueExpressArray[j] = Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
                    }
                    targetPropertiesArray[i] = m.property(currentKey).to(Cypher.listOf(dataValueExpressArray));
                }
            }
            statement = ongoingReadingWithWhere.set(targetPropertiesArray).returning(targetNewAddPropertiesArray).build();
        }else{
            statement = ongoingReadingWithWhere.returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String removeNodePropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType,Object propertyValue,List<String> targetPropertiesList){
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch(propertyFunctionType){
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement;
        if(targetPropertiesList != null && targetPropertiesList.size()>0) {
            Property[] targetPropertiesRemoveArray = new Property[targetPropertiesList.size()];
            for (int i = 0; i < targetPropertiesList.size(); i++) {
                String currentPropertyName = targetPropertiesList.get(i);
                targetPropertiesRemoveArray[i] = m.property(currentPropertyName);
            }
            statement = ongoingReadingWithWhere.remove(targetPropertiesRemoveArray).returning(Functions2.keys(m)).build();
        }else{
            statement = ongoingReadingWithWhere.returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String matchRelatedNodesFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType,Object sourcePropertyValue,
                                                                String targetConceptionKind,String relationKind,
                                                                RelationDirection relationDirection,CypherFunctionType returnFunctionType){
        Node sourceNode = Cypher.anyNode().named("SourceNode");
        Node resultNodes = Cypher.node(targetConceptionKind).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = null;
        switch(relationDirection){
            case FROM: ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipFrom(resultNodes,relationKind));
                break;
            case TO:ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipTo(resultNodes,relationKind));
                break;
            case TWO_WAY:
                ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipBetween(resultNodes,relationKind));
        }

        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch(sourcePropertyFunctionType){
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                break;
            default:
        }

        Statement statement = null;

        if(returnFunctionType !=null) {
            switch (returnFunctionType) {
                case KEYS:
                    if(ongoingReadingWithWhere != null){
                        statement = ongoingReadingWithWhere.returning(Functions2.keys(resultNodes)).build();
                    }else{
                        statement = ongoingReadingWithoutWhere.returning(Functions2.keys(resultNodes)).build();
                    }
                    break;
                case PROPERTIES:
                    if(ongoingReadingWithWhere != null){
                        statement = ongoingReadingWithWhere.returning(Functions2.properties(resultNodes)).build();
                    }else{
                        statement = ongoingReadingWithoutWhere.returning(Functions2.properties(resultNodes)).build();
                    }
                    break;
            }
        }else{
            if(ongoingReadingWithWhere != null){
                statement = ongoingReadingWithWhere.returning(resultNodes).build();
            }else{
                statement = ongoingReadingWithoutWhere.returning(resultNodes).build();
            }
        }

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }

    public static String createNodesRelationshipByIdMatch(Long sourceNodeId, Long targetNodeId,String relationKind, Map<String,Object> relationProperties){
        Node sourceNode = Cypher.anyNode().named("sourceNode");
        Node targetNode = Cypher.anyNode().named("targetNode");
        Relationship relation;
        if(relationProperties != null && relationProperties.size()>0){
            MapExpression targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(relationProperties));
            relation = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName).withProperties(targetMapExpression);
        }else{
            relation = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName);
        }
        Statement statement = Cypher.match(sourceNode,targetNode).
                where(sourceNode.internalId().isEqualTo(Cypher.literalOf(sourceNodeId))
                        .and(targetNode.internalId().isEqualTo(Cypher.literalOf(targetNodeId)))).create(relation).returning(relation).build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
    }
}
