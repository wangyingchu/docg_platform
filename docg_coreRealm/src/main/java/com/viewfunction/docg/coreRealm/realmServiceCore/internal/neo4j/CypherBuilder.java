package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.SortingItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
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
    public static final String relationResultName = "relationResult";
    public enum CypherFunctionType {
        COUNT, ID, KEYS, PROPERTIES, EXISTS
    }

    private static final ZoneId systemDefaultZoneId = ZoneId.systemDefault();

    public static String matchLabelWithSinglePropertyValue(String labelName, String propertyName, Object propertyValue, int matchValue) {
        Node m = Cypher.node(labelName).named(operationResultName).withProperties(propertyName, Cypher.literalOf(propertyValue));
        Statement statement = Cypher.match(m)
                .returning(m)
                .limit(matchValue)
                .build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String createLabeledNodeWithProperties(String labelName, Map<String, Object> properties) {
        Node m = Cypher.node(labelName).named(operationResultName);
        Statement statement;
        if (properties != null && properties.size() > 0) {
            Operation[] propertiesArray = getPropertiesSettingArray(m, properties);
            statement = Cypher.create(m).set(propertiesArray).returning(m).build();
        } else {
            m = Cypher.node(labelName).named(operationResultName);
            statement = Cypher.create(m).returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    private static Operation[] getPropertiesSettingArray(Node m, Map<String, Object> properties) {
        Map<String, Object> realPropertiesData = CommonOperationUtil.reformatPropertyValues(properties);
        properties.clear();
        List<Operation> propertyValuesList = new ArrayList<>();
        java.util.Set<String> propertyNameSet = realPropertiesData.keySet();
        if (!propertyNameSet.isEmpty()) {
            Iterator<String> propertyNameIterator = propertyNameSet.iterator();
            while (propertyNameIterator.hasNext()) {
                String propertyName = propertyNameIterator.next();
                Object propertyValue = realPropertiesData.get(propertyName);
                if (propertyValue instanceof CharSequence || propertyValue instanceof Number ||
                        propertyValue instanceof Iterable || propertyValue instanceof Boolean) {
                    propertyValuesList.add(m.property(propertyName).to(Cypher.literalOf(propertyValue)));
                } else if (propertyValue instanceof ZonedDateTime) {
                    ZonedDateTime targetZonedDateTime = (ZonedDateTime) propertyValue;
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    propertyValuesList.add(m.property(propertyName).
                            to(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)))
                    );
                } else if (propertyValue instanceof Date) {
                    ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date) propertyValue).toInstant(), systemDefaultZoneId);
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    propertyValuesList.add(m.property(propertyName).
                            to(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)))
                    );
                } else if (propertyValue instanceof Date[]) {
                    Date[] dateValueArray = (Date[]) propertyValue;
                    Expression[] dataValueExpressArray = new Expression[dateValueArray.length];
                    for (int i = 0; i < dateValueArray.length; i++) {
                        Date currentValue = dateValueArray[i];
                        ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(currentValue.toInstant(), systemDefaultZoneId);
                        String targetZonedDateTimeString = targetZonedDateTime.toString();
                        dataValueExpressArray[i] = Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
                    }
                    propertyValuesList.add(m.property(propertyName).to(Cypher.listOf(dataValueExpressArray)));
                }
            }
        }
        Operation[] propertiesArray = propertyValuesList.toArray(new Operation[propertyValuesList.size()]);
        return propertiesArray;
    }

    public static String createMultiLabeledNodesWithProperties(String labelName, List<Map<String, Object>> propertiesList) {
        if (propertiesList != null && propertiesList.size() > 0) {
            Node[] targetNodeArray = new Node[propertiesList.size()];
            Map<String, Object> currentPropertyMap = propertiesList.get(0);
            MapExpression targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(currentPropertyMap));
            Node m = Cypher.node(labelName).named(operationResultName + 0).withProperties(targetMapExpression);
            targetNodeArray[0] = m;
            StatementBuilder.OngoingUpdate currentOngoingUpdate = Cypher.create(targetNodeArray[0]);
            for (int i = 1; i < propertiesList.size(); i++) {
                currentPropertyMap = propertiesList.get(i);
                targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(currentPropertyMap));
                Node currentN = Cypher.node(labelName).named(operationResultName + i).withProperties(targetMapExpression);
                targetNodeArray[i] = currentN;
                currentOngoingUpdate = currentOngoingUpdate.create(targetNodeArray[i]);
            }
            Statement statement = currentOngoingUpdate.returning(targetNodeArray).build();

            String rel = cypherRenderer.render(statement);
            logger.debug("Generated Cypher Statement: {}", rel);
            return rel;
        }
        return null;
    }

    public static String matchLabelWithSinglePropertyValueAndFunction(String labelName, CypherFunctionType cypherFunctionType, String propertyName, Object propertyValue) {
        Node m;
        if (propertyName != null) {
            m = Cypher.node(labelName).named(operationResultName).withProperties(propertyName, Cypher.literalOf(propertyValue));
        } else {
            m = Cypher.node(labelName).named(operationResultName);
        }
        StatementBuilder.OngoingReadingWithoutWhere currentOngoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn;
        switch (cypherFunctionType) {
            case COUNT:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(Functions.count(m));
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(m);
        }
        Statement statement = ongoingReadingAndReturn.build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteLabelWithSinglePropertyValueAndFunction(String labelName, CypherFunctionType cypherFunctionType, String propertyName, Object propertyValue) {
        Node m;
        if (propertyName != null) {
            m = Cypher.node(labelName).named(operationResultName).withProperties(propertyName, Cypher.literalOf(propertyValue));
        } else {
            m = Cypher.node(labelName).named(operationResultName);
        }
        StatementBuilder.OngoingReadingWithoutWhere currentOngoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn;
        switch (cypherFunctionType) {
            case COUNT:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.detachDelete(m).returning(Functions.count(m));
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.detachDelete(m).returning(m);
        }
        Statement statement = ongoingReadingAndReturn.build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchNodeWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, CypherFunctionType returnFunctionType, String additionalPropertyName) {
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement = null;
        if (returnFunctionType != null) {
            switch (returnFunctionType) {
                case KEYS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions2.keys(m)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions2.keys(m)).build();
                    }
                    break;
                case PROPERTIES:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions2.properties(m)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions2.properties(m)).build();
                    }
                    break;
                case EXISTS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions.exists(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions.exists(m.property(additionalPropertyName))).build();
                    }
            }
        } else {
            if (ongoingReadingWithWhere != null) {
                statement = ongoingReadingWithWhere.returning(m).build();
            } else {
                statement = ongoingReadingWithoutWhere.returning(m).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteNodeWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, CypherFunctionType returnFunctionType, String additionalPropertyName) {
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement = null;
        if (returnFunctionType != null) {
            switch (returnFunctionType) {
                case KEYS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.detachDelete(m).returning(Functions2.keys(m)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.detachDelete(m).returning(Functions2.keys(m)).build();
                    }
                    break;
                case PROPERTIES:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.detachDelete(m).returning(Functions2.properties(m)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.detachDelete(m).returning(Functions2.properties(m)).build();
                    }
                    break;
                case EXISTS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.detachDelete(m).returning(Functions.exists(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.detachDelete(m).returning(Functions.exists(m.property(additionalPropertyName))).build();
                    }
            }
        } else {
            if (ongoingReadingWithWhere != null) {
                statement = ongoingReadingWithWhere.detachDelete(m).returning(m).build();
            } else {
                statement = ongoingReadingWithoutWhere.detachDelete(m).returning(m).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteNodesWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, List<Object> propertyValueList) {






        long[] objectArray = new long[2];
        objectArray[0]=1234l;
        objectArray[1]=2234l;

        List<Long> s = new ArrayList<>();
        s.add(12345l);

        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                //ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).in(Cypher.listOf(Cypher.literalOf(s))));
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).in(Cypher.listOf(Cypher.literalOf(s))));
                break;
            default:
        }
        Statement statement = null;
        if (ongoingReadingWithWhere != null) {
            statement = ongoingReadingWithWhere.detachDelete(m).returning(m).build();
        } else {
            statement = ongoingReadingWithoutWhere.detachDelete(m).returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;





    }

    public static String matchNodePropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, String[] targetPropertyNames) {
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement;
        if (targetPropertyNames != null && targetPropertyNames.length > 0) {
            Property[] targetPropertiesArray = new Property[targetPropertyNames.length];
            for (int i = 0; i < targetPropertyNames.length; i++) {
                targetPropertiesArray[i] = m.property(targetPropertyNames[i]);
            }
            statement = ongoingReadingWithWhere.returning(targetPropertiesArray).build();
        } else {
            statement = ongoingReadingWithWhere.returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String setNodePropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, Map<String, Object> originalTargetPropertiesMap) {
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement;
        Map<String, Object> targetPropertiesMap = CommonOperationUtil.reformatPropertyValues(originalTargetPropertiesMap);
        if (targetPropertiesMap != null && targetPropertiesMap.size() > 0) {
            Expression[] targetPropertiesArray = new Expression[targetPropertiesMap.size()];
            Property[] targetNewAddPropertiesArray = new Property[targetPropertiesMap.size()];
            Object[] keysObjectArray = targetPropertiesMap.keySet().toArray();
            for (int i = 0; i < keysObjectArray.length; i++) {
                String currentKey = keysObjectArray[i].toString();
                Object currentValue = targetPropertiesMap.get(currentKey);
                targetNewAddPropertiesArray[i] = m.property(currentKey);

                if (currentValue instanceof ZonedDateTime) {
                    ZonedDateTime targetZonedDateTime = (ZonedDateTime) currentValue;
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    targetPropertiesArray[i] = m.property(currentKey).to(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)));
                } else if (currentValue instanceof Date) {
                    ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date) currentValue).toInstant(), systemDefaultZoneId);
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    targetPropertiesArray[i] = m.property(currentKey).to(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)));
                } else if (currentValue instanceof CharSequence || currentValue instanceof Number ||
                        currentValue instanceof Iterable || currentValue instanceof Boolean) {
                    targetPropertiesArray[i] = m.property(currentKey).to(Cypher.literalOf(currentValue));
                } else if (currentValue instanceof Date[]) {
                    Date[] dateValueArray = (Date[]) currentValue;
                    Expression[] dataValueExpressArray = new Expression[dateValueArray.length];
                    for (int j = 0; j < dateValueArray.length; j++) {
                        Date currentInnerValue = dateValueArray[j];
                        ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(currentInnerValue.toInstant(), systemDefaultZoneId);
                        String targetZonedDateTimeString = targetZonedDateTime.toString();
                        dataValueExpressArray[j] = Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
                    }
                    targetPropertiesArray[i] = m.property(currentKey).to(Cypher.listOf(dataValueExpressArray));
                }
            }
            statement = ongoingReadingWithWhere.set(targetPropertiesArray).returning(targetNewAddPropertiesArray).build();
        } else {
            statement = ongoingReadingWithWhere.returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String removeNodePropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, List<String> targetPropertiesList) {
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement;
        if (targetPropertiesList != null && targetPropertiesList.size() > 0) {
            Property[] targetPropertiesRemoveArray = new Property[targetPropertiesList.size()];
            for (int i = 0; i < targetPropertiesList.size(); i++) {
                String currentPropertyName = targetPropertiesList.get(i);
                targetPropertiesRemoveArray[i] = m.property(currentPropertyName);
            }
            statement = ongoingReadingWithWhere.remove(targetPropertiesRemoveArray).returning(Functions2.keys(m)).build();
        } else {
            statement = ongoingReadingWithWhere.returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelatedNodesFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                String targetConceptionKind, String relationKind,
                                                                RelationDirection relationDirection, CypherFunctionType returnFunctionType) {
        Node sourceNode = Cypher.anyNode().named("sourceNode");
        Node resultNodes = Cypher.node(targetConceptionKind).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = null;
        switch (relationDirection) {
            case FROM:
                ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipFrom(resultNodes, relationKind));
                break;
            case TO:
                ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipTo(resultNodes, relationKind));
                break;
            case TWO_WAY:
                ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipBetween(resultNodes, relationKind));
        }

        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (sourcePropertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                break;
            default:
        }

        Statement statement = null;

        if (returnFunctionType != null) {
            switch (returnFunctionType) {
                case KEYS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions2.keys(resultNodes)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions2.keys(resultNodes)).build();
                    }
                    break;
                case PROPERTIES:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions2.properties(resultNodes)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions2.properties(resultNodes)).build();
                    }
                    break;
            }
        } else {
            if (ongoingReadingWithWhere != null) {
                statement = ongoingReadingWithWhere.returning(resultNodes).build();
            } else {
                statement = ongoingReadingWithoutWhere.returning(resultNodes).build();
            }
        }

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String createNodesRelationshipByIdMatch(Long sourceNodeId, Long targetNodeId, String relationKind, Map<String, Object> relationProperties) {
        Node sourceNode = Cypher.anyNode().named("sourceNode");
        Node targetNode = Cypher.anyNode().named("targetNode");
        Relationship relation;
        if (relationProperties != null && relationProperties.size() > 0) {
            MapExpression targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(relationProperties));
            relation = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName).withProperties(targetMapExpression);
        } else {
            relation = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName);
        }
        Statement statement = Cypher.match(sourceNode, targetNode).
                where(sourceNode.internalId().isEqualTo(Cypher.literalOf(sourceNodeId))
                        .and(targetNode.internalId().isEqualTo(Cypher.literalOf(targetNodeId)))).create(relation).returning(relation).build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelationshipsByBothNodesId(Long sourceNodeId, Long targetNodeId, String relationKind) {
        Node sourceNode = Cypher.anyNode().named("sourceNode");
        Node targetNode = Cypher.anyNode().named("targetNode");
        Relationship relations = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName);
        Statement statement = Cypher.match(relations).where(sourceNode.internalId().isEqualTo(Cypher.literalOf(sourceNodeId))
                .and(targetNode.internalId().isEqualTo(Cypher.literalOf(targetNodeId)))).returning(relations).build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteRelationWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, CypherFunctionType returnFunctionType, String additionalPropertyName) {
        Node sourceNode = Cypher.anyNode().named("sourceNode");
        Node targetNode = Cypher.anyNode().named("targetNode");
        Relationship relation = sourceNode.relationshipBetween(targetNode).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(relation);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(relation).isEqualTo(Cypher.literalOf(propertyValue)));
                ;
                break;
            default:
        }
        Statement statement = null;
        if (returnFunctionType != null) {
            switch (returnFunctionType) {
                case KEYS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.delete(relation).returning(Functions2.keys(relation)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.delete(relation).returning(Functions2.keys(relation)).build();
                    }
                    break;
                case PROPERTIES:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.delete(relation).returning(Functions2.properties(relation)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.delete(relation).returning(Functions2.properties(relation)).build();
                    }
                    break;
                case EXISTS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.delete(relation).returning(Functions.exists(relation.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.delete(relation).returning(Functions.exists(relation.property(additionalPropertyName))).build();
                    }
            }
        } else {
            if (ongoingReadingWithWhere != null) {
                statement = ongoingReadingWithWhere.delete(relation).returning(relation).build();
            } else {
                statement = ongoingReadingWithoutWhere.delete(relation).returning(relation).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String mergeRelatedNodesFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                String targetConceptionKind, String relationKind,
                                                                RelationDirection relationDirection, CypherFunctionType returnFunctionType) {
        Node sourceNode = Cypher.anyNode().named("sourceNode");
        Node resultNodes = Cypher.node(targetConceptionKind).named(operationResultName);

        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(sourceNode);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (sourcePropertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                break;
            default:
        }
        switch (relationDirection) {
            case FROM:
                ongoingReadingWithWhere.merge(sourceNode.relationshipFrom(resultNodes, relationKind));
                break;
            case TO:
                ongoingReadingWithWhere.merge(sourceNode.relationshipTo(resultNodes, relationKind));
                break;
            case TWO_WAY:
                ongoingReadingWithWhere.merge(sourceNode.relationshipBetween(resultNodes, relationKind));
        }

        Statement statement = null;

        if (returnFunctionType != null) {
            switch (returnFunctionType) {
                case KEYS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions2.keys(resultNodes)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions2.keys(resultNodes)).build();
                    }
                    break;
                case PROPERTIES:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions2.properties(resultNodes)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions2.properties(resultNodes)).build();
                    }
                    break;
            }
        } else {
            if (ongoingReadingWithWhere != null) {
                statement = ongoingReadingWithWhere.returning(resultNodes).build();
            } else {
                statement = ongoingReadingWithoutWhere.returning(resultNodes).build();
            }
        }

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String match2JumpRelatedNodesFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                     String middleConceptionKind, String middleRelationKind, RelationDirection middleRelationDirection,
                                                                     Map<String, Object> middleEntityProperties,
                                                                     String targetConceptionKind, String targetRelationKind, RelationDirection targetRelationDirection,
                                                                     Map<String, Object> targetEntityProperties,
                                                                     CypherFunctionType returnFunctionType) {
        //Cypher.match(finalChain).where(Functions.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
        Node sourceNode = Cypher.anyNode().named("sourceNode");
        Node middleNode;
        MapExpression middleNodeEntityMapExpression = middleEntityProperties != null ?
                Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(middleEntityProperties)) : null;
        if (middleConceptionKind != null) {
            if (middleNodeEntityMapExpression != null) {
                middleNode = Cypher.node(middleConceptionKind).named("middleNode").withProperties(middleNodeEntityMapExpression);
            } else {
                middleNode = Cypher.node(middleConceptionKind).named("middleNode");
            }
        } else {
            if (middleNodeEntityMapExpression != null) {
                middleNode = Cypher.anyNode().named("middleNode").withProperties(middleNodeEntityMapExpression);
            } else {
                middleNode = Cypher.anyNode().named("middleNode");
            }
        }
        Node resultNodes;
        MapExpression resultNodeEntityMapExpression = targetEntityProperties != null ?
                Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(targetEntityProperties)) : null;
        if (targetConceptionKind != null) {
            if (targetConceptionKind != null) {
                resultNodes = Cypher.node(targetConceptionKind).named(operationResultName).withProperties(resultNodeEntityMapExpression);
            } else {
                resultNodes = Cypher.node(targetConceptionKind).named(operationResultName);
            }
        } else {
            if (targetConceptionKind != null) {
                resultNodes = Cypher.anyNode().named(operationResultName).withProperties(resultNodeEntityMapExpression);
            } else {
                resultNodes = Cypher.anyNode().named(operationResultName);
            }
        }
        Relationship jump1Relation = null;
        switch (middleRelationDirection) {
            case FROM:
                if (middleRelationKind != null) {
                    jump1Relation = sourceNode.relationshipFrom(middleNode, middleRelationKind);
                } else {
                    jump1Relation = sourceNode.relationshipFrom(middleNode);
                }
                break;
            case TO:
                if (middleRelationKind != null) {
                    jump1Relation = sourceNode.relationshipTo(middleNode, middleRelationKind);
                } else {
                    jump1Relation = sourceNode.relationshipTo(middleNode);
                }
                break;
            case TWO_WAY:
                if (middleRelationKind != null) {
                    jump1Relation = sourceNode.relationshipBetween(middleNode, middleRelationKind);
                } else {
                    jump1Relation = sourceNode.relationshipBetween(middleNode);
                }
        }
        RelationshipChain finalChain = null;
        switch (targetRelationDirection) {
            case FROM:
                if (targetRelationKind != null) {
                    finalChain = jump1Relation.relationshipFrom(resultNodes, targetRelationKind);
                } else {
                    finalChain = jump1Relation.relationshipFrom(resultNodes);
                }
                break;
            case TO:
                if (targetRelationKind != null) {
                    finalChain = jump1Relation.relationshipTo(resultNodes, targetRelationKind);
                } else {
                    finalChain = jump1Relation.relationshipTo(resultNodes);
                }
                break;
            case TWO_WAY:
                if (targetRelationKind != null) {
                    finalChain = jump1Relation.relationshipBetween(resultNodes, targetRelationKind);
                } else {
                    finalChain = jump1Relation.relationshipBetween(resultNodes);
                }
        }
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(finalChain);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (sourcePropertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                break;
            default:
        }

        Statement statement = null;
        if (returnFunctionType != null) {
            switch (returnFunctionType) {
                case KEYS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions2.keys(resultNodes)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions2.keys(resultNodes)).build();
                    }
                    break;
                case PROPERTIES:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Functions2.properties(resultNodes)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Functions2.properties(resultNodes)).build();
                    }
                    break;
            }
        } else {
            if (ongoingReadingWithWhere != null) {
                statement = ongoingReadingWithWhere.returning(resultNodes).build();
            } else {
                statement = ongoingReadingWithoutWhere.returning(resultNodes).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchNodesWithQueryParameters(String labelName, QueryParameters queryParameters, CypherFunctionType cypherFunctionType) throws CoreRealmServiceEntityExploreException {
        int defaultReturnRecordNumber = 500;
        Node m = null;
        Statement statement = null;
        if (labelName != null) {
            m = Cypher.node(labelName).named(operationResultName);
        } else {
            m = Cypher.anyNode().named(operationResultName);
        }

        Expression returnedFunctionValue = null;
        if (cypherFunctionType != null) {
            switch (cypherFunctionType) {
                case COUNT:
                    returnedFunctionValue = Functions.count(m);
                    break;
                case ID:
                    returnedFunctionValue = Functions.id(m);
                    break;
            }
        }

        if (queryParameters != null) {
            int skipRecordNumber = 0;
            int limitRecordNumber = 0;

            int startPage = queryParameters.getStartPage();
            int endPage = queryParameters.getEndPage();
            int pageSize = queryParameters.getPageSize();
            int resultNumber = queryParameters.getResultNumber();
            boolean isDistinctMode = queryParameters.isDistinctMode();
            List<SortingItem> sortingItemList = queryParameters.getSortingItems();

            SortItem[] sortItemArray = null;
            if (sortingItemList.size() > 0) {
                sortItemArray = new SortItem[sortingItemList.size()];
                for (int i = 0; i < sortingItemList.size(); i++) {
                    SortingItem currentSortingItem = sortingItemList.get(i);
                    String attributeName = currentSortingItem.getAttributeName();
                    QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                    switch (sortingLogic) {
                        case ASC:
                            sortItemArray[i] = Cypher.sort(m.property(attributeName)).ascending();
                            break;
                        case DESC:
                            sortItemArray[i] = Cypher.sort(m.property(attributeName)).descending();
                    }
                }
            }

            if (startPage != 0) {
                if (startPage < 0) {
                    String exceptionMessage = "start page must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                if (pageSize < 0) {
                    String exceptionMessage = "page size must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }

                int runtimePageSize = pageSize != 0 ? pageSize : 50;
                int runtimeStartPage = startPage - 1;

                if (endPage != 0) {
                    //get data from start page to end page, each page has runtimePageSize number of record
                    if (endPage < 0 || endPage <= startPage) {
                        String exceptionMessage = "end page must great than start page";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                    int runtimeEndPage = endPage - 1;

                    skipRecordNumber = runtimePageSize * runtimeStartPage;
                    limitRecordNumber = (runtimeEndPage - runtimeStartPage) * runtimePageSize;
                } else {
                    //filter the data before the start page
                    limitRecordNumber = runtimePageSize * runtimeStartPage;
                }
            } else {
                //if there is no page parameters,use resultNumber to control result information number
                if (resultNumber != 0) {
                    if (resultNumber < 0) {
                        String exceptionMessage = "result number must great then zero";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                    limitRecordNumber = resultNumber;
                }
            }

            if (limitRecordNumber == 0) {
                limitRecordNumber = defaultReturnRecordNumber;
            }

            StatementBuilder.OngoingReadingAndReturn activeOngoingReadingAndReturn;
            if (returnedFunctionValue != null) {
                if (isDistinctMode) {
                    activeOngoingReadingAndReturn = Cypher.match(m).returningDistinct(returnedFunctionValue);
                } else {
                    activeOngoingReadingAndReturn = Cypher.match(m).returning(returnedFunctionValue);
                }
            } else {
                if (isDistinctMode) {
                    activeOngoingReadingAndReturn = Cypher.match(m).returningDistinct(m);
                } else {
                    activeOngoingReadingAndReturn = Cypher.match(m).returning(m);
                }
            }

            FilteringItem defaultFilteringItem = queryParameters.getDefaultFilteringItem();
            List<FilteringItem> andFilteringItemList = queryParameters.getAndFilteringItemsList();
            List<FilteringItem> orFilteringItemList = queryParameters.getOrFilteringItemsList();
            if (defaultFilteringItem == null) {
                if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                        (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }
            } else {
                StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = Cypher.match(m).where(CommonOperationUtil.getQueryCondition(m, defaultFilteringItem));
                if (andFilteringItemList != null && andFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : andFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(m, currentFilteringItem));
                    }
                }
                if (orFilteringItemList != null && orFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : orFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(m, currentFilteringItem));
                    }
                }
                if (returnedFunctionValue != null) {
                    if (isDistinctMode) {
                        activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(returnedFunctionValue);
                    } else {
                        activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(returnedFunctionValue);
                    }
                } else {
                    if (isDistinctMode) {
                        activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(m);
                    } else {
                        activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(m);
                    }
                }
            }

            if (skipRecordNumber != 0) {
                if (sortItemArray != null && returnedFunctionValue == null) {
                    //Function can not use together with sort in this case
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).skip(skipRecordNumber).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.skip(skipRecordNumber).limit(limitRecordNumber).build();
                }
            } else {
                if (sortItemArray != null && returnedFunctionValue == null) {
                    //Function can not use together with sort in this case
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.limit(limitRecordNumber).build();
                }
            }
        } else {
            statement = Cypher.match(m).returning(m).limit(defaultReturnRecordNumber).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchAttributesWithQueryParameters(String labelName, QueryParameters queryParameters,List<String> returnProperties) throws CoreRealmServiceEntityExploreException {
        int defaultReturnRecordNumber = 500;
        Node m = null;
        Statement statement = null;
        if (labelName != null) {
            m = Cypher.node(labelName).named(operationResultName);
        } else {
            m = Cypher.anyNode().named(operationResultName);
        }

        if (returnProperties == null || returnProperties.size() == 0) {
            String exceptionMessage = "must input at least one attribute name";
            CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
            coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
            throw coreRealmServiceEntityExploreException;
        }
        Expression[] returnPropertyArray = new Expression[returnProperties.size()+1];
        for(int i = 0; i < returnProperties.size(); i++){
            returnPropertyArray[i] = m.property(returnProperties.get(i).trim());
        }

        returnPropertyArray[returnProperties.size()] = Functions.id(m);

        if (queryParameters != null) {
            int skipRecordNumber = 0;
            int limitRecordNumber = 0;

            int startPage = queryParameters.getStartPage();
            int endPage = queryParameters.getEndPage();
            int pageSize = queryParameters.getPageSize();
            int resultNumber = queryParameters.getResultNumber();
            boolean isDistinctMode = queryParameters.isDistinctMode();
            List<SortingItem> sortingItemList = queryParameters.getSortingItems();

            SortItem[] sortItemArray = null;
            if (sortingItemList.size() > 0) {
                sortItemArray = new SortItem[sortingItemList.size()];
                for (int i = 0; i < sortingItemList.size(); i++) {
                    SortingItem currentSortingItem = sortingItemList.get(i);
                    String attributeName = currentSortingItem.getAttributeName();
                    QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                    switch (sortingLogic) {
                        case ASC:
                            sortItemArray[i] = Cypher.sort(m.property(attributeName)).ascending();
                            break;
                        case DESC:
                            sortItemArray[i] = Cypher.sort(m.property(attributeName)).descending();
                    }
                }
            }

            if (startPage != 0) {
                if (startPage < 0) {
                    String exceptionMessage = "start page must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                if (pageSize < 0) {
                    String exceptionMessage = "page size must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }

                int runtimePageSize = pageSize != 0 ? pageSize : 50;
                int runtimeStartPage = startPage - 1;

                if (endPage != 0) {
                    //get data from start page to end page, each page has runtimePageSize number of record
                    if (endPage < 0 || endPage <= startPage) {
                        String exceptionMessage = "end page must great than start page";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                    int runtimeEndPage = endPage - 1;

                    skipRecordNumber = runtimePageSize * runtimeStartPage;
                    limitRecordNumber = (runtimeEndPage - runtimeStartPage) * runtimePageSize;
                } else {
                    //filter the data before the start page
                    limitRecordNumber = runtimePageSize * runtimeStartPage;
                }
            } else {
                //if there is no page parameters,use resultNumber to control result information number
                if (resultNumber != 0) {
                    if (resultNumber < 0) {
                        String exceptionMessage = "result number must great then zero";
                        CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                        coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                        throw coreRealmServiceEntityExploreException;
                    }
                    limitRecordNumber = resultNumber;
                }
            }

            if (limitRecordNumber == 0) {
                limitRecordNumber = defaultReturnRecordNumber;
            }

            StatementBuilder.OngoingReadingAndReturn activeOngoingReadingAndReturn;

            if (isDistinctMode) {
                activeOngoingReadingAndReturn = Cypher.match(m).returningDistinct(returnPropertyArray);
            } else {
                activeOngoingReadingAndReturn = Cypher.match(m).returning(returnPropertyArray);
            }

            FilteringItem defaultFilteringItem = queryParameters.getDefaultFilteringItem();
            List<FilteringItem> andFilteringItemList = queryParameters.getAndFilteringItemsList();
            List<FilteringItem> orFilteringItemList = queryParameters.getOrFilteringItemsList();
            if (defaultFilteringItem == null) {
                if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                        (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }
            } else {
                StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = Cypher.match(m).where(CommonOperationUtil.getQueryCondition(m, defaultFilteringItem));
                if (andFilteringItemList != null && andFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : andFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(m, currentFilteringItem));
                    }
                }
                if (orFilteringItemList != null && orFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : orFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(m, currentFilteringItem));
                    }
                }
                if (isDistinctMode) {
                    activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(returnPropertyArray);
                } else {
                    activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(returnPropertyArray);
                }
            }

            if (skipRecordNumber != 0) {
                if (sortItemArray != null) {
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).skip(skipRecordNumber).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.skip(skipRecordNumber).limit(limitRecordNumber).build();
                }
            } else {
                if (sortItemArray != null) {
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.limit(limitRecordNumber).build();
                }
            }
        } else {
            statement = Cypher.match(m).returning(returnPropertyArray).limit(defaultReturnRecordNumber).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                String targetConceptionKind, String relationKind,
                                                                RelationDirection relationDirection,int minJump,int maxJump) {
        Node sourceNode = Cypher.anyNode().named("sourceNode");
        Node resultNodes = Cypher.node(targetConceptionKind).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = null;
        Relationship resultRelationship = null;

        switch (relationDirection) {
            case FROM:
                if(minJump != 0 & maxJump != 0 & maxJump>=minJump) {
                    resultRelationship = sourceNode.relationshipFrom(resultNodes, relationKind).length(minJump,maxJump).named(relationResultName);
                }else{
                    resultRelationship = sourceNode.relationshipFrom(resultNodes, relationKind).unbounded().named(relationResultName);
                }
                ongoingReadingWithoutWhere = Cypher.match(resultRelationship);
                break;
            case TO:
                if(minJump != 0 & maxJump != 0 & maxJump>=minJump) {
                    resultRelationship = sourceNode.relationshipTo(resultNodes, relationKind).length(minJump,maxJump).named(relationResultName);
                }else{
                    resultRelationship = sourceNode.relationshipTo(resultNodes, relationKind).unbounded().named(relationResultName);
                }
                ongoingReadingWithoutWhere = Cypher.match(resultRelationship);
                break;
            case TWO_WAY:
                if(minJump != 0 & maxJump != 0 & maxJump>=minJump) {
                    resultRelationship = sourceNode.relationshipBetween(resultNodes, relationKind).length(minJump,maxJump).named(relationResultName);
                }else{
                    resultRelationship = sourceNode.relationshipBetween(resultNodes, relationKind).unbounded().named(relationResultName);
                }
                ongoingReadingWithoutWhere = Cypher.match(resultRelationship);
        }

        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (sourcePropertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                break;
            default:
        }

        Statement statement;
        if (ongoingReadingWithWhere != null) {
            statement = ongoingReadingWithWhere.returning(resultNodes,resultRelationship).build();
        } else {
            statement = ongoingReadingWithoutWhere.returning(resultNodes,resultRelationship).build();
        }

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }
}
