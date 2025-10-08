package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.StatisticalAndEvaluable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.NumericalAttributeStatisticCondition;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.cypherdsl.core.*;
import org.neo4j.cypherdsl.core.renderer.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.*;

import static com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder.CypherFunctionType.EXISTS;
import static com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder.ReturnRelationableDataType.NODE;
import static com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder.ReturnRelationableDataType.RELATION;

public class CypherBuilder {

    private static Logger logger = LoggerFactory.getLogger(CypherBuilder.class);
    private static final Renderer cypherRenderer = Renderer.getDefaultRenderer();
    public static final String operationResultName = "operationResult";
    public static final String relationResultName = "relationResult";
    public static final String sourceNodeName = "sourceNode";
    public static final String targetNodeName = "targetNode";

    public enum CypherFunctionType {
        COUNT, ID, KEYS, PROPERTIES, EXISTS, LABEL
    }

    public enum ReturnRelationableDataType{
        BOTH,RELATION,NODE,COUNT_NODE,COUNT_RELATION
    }

    public enum LabelOperationType{
        ADD,REMOVE
    }

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

    public static String createLabeledNodeWithProperties(String[] labelNames, Map<String, Object> properties) {
        String primaryLabel = labelNames[0];
        List<String> additionalLabels = null;
        if(labelNames.length>1){
            additionalLabels = new ArrayList<>();
            for(int i= 1;i<labelNames.length;i++){
                additionalLabels.add(labelNames[i]);
            }
        }
        Node m = additionalLabels != null ? Cypher.node(primaryLabel,additionalLabels).named(operationResultName):
                Cypher.node(primaryLabel).named(operationResultName);
        Statement statement;
        if (properties != null && properties.size() > 0) {
            Operation[] propertiesArray = getPropertiesSettingArray(m, properties);
            statement = Cypher.create(m).set(propertiesArray).returning(m).build();
        } else {
            m = additionalLabels != null ? Cypher.node(primaryLabel,additionalLabels).named(operationResultName):
                    Cypher.node(primaryLabel).named(operationResultName);
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
                propertyValuesList.add(m.property(propertyName).to(CommonOperationUtil.getAttributeValueExpress(propertyValue)));
            }
        }
        Operation[] propertiesArray = propertyValuesList.toArray(new Operation[propertyValuesList.size()]);
        return propertiesArray;
    }

    public static String createMultiLabeledNodesWithProperties(String[] labelNames, List<Map<String, Object>> propertiesList) {
        String primaryLabel = labelNames[0];
        List<String> additionalLabels = null;
        if(labelNames.length>1){
            additionalLabels = new ArrayList<>();
            for(int i= 1;i<labelNames.length;i++){
                additionalLabels.add(labelNames[i]);
            }
        }

        if (propertiesList != null && propertiesList.size() > 0) {
            Node[] targetNodeArray = new Node[propertiesList.size()];
            Map<String, Object> currentPropertyMap = propertiesList.get(0);
            MapExpression targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(currentPropertyMap));
            Node m = additionalLabels != null ? Cypher.node(primaryLabel,additionalLabels).named(operationResultName + 0).withProperties(targetMapExpression):
                    Cypher.node(primaryLabel).named(operationResultName + 0).withProperties(targetMapExpression);
            targetNodeArray[0] = m;
            StatementBuilder.OngoingUpdate currentOngoingUpdate = Cypher.create(targetNodeArray[0]);
            for (int i = 1; i < propertiesList.size(); i++) {
                currentPropertyMap = propertiesList.get(i);
                targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(currentPropertyMap));
                Node currentN = additionalLabels != null ? Cypher.node(primaryLabel,additionalLabels).named(operationResultName + i).withProperties(targetMapExpression):
                        Cypher.node(primaryLabel).named(operationResultName + i).withProperties(targetMapExpression);
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
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(Cypher.count(m));
                break;
            case ID:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(Functions2.id(m));
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(m);
        }
        Statement statement = ongoingReadingAndReturn.build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelationWithSinglePropertyValueAndFunction(String relationKind, CypherFunctionType cypherFunctionType, String propertyName, Object propertyValue) {
        Relationship r;
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNodes = Cypher.anyNode().named(targetNodeName);
        if (propertyName != null) {
            r = sourceNode.relationshipTo(targetNodes, relationKind).named(operationResultName).withProperties(propertyName, Cypher.literalOf(propertyValue));
        } else {
            r = sourceNode.relationshipTo(targetNodes, relationKind).named(operationResultName);
        }
        StatementBuilder.OngoingReadingWithoutWhere currentOngoingReadingWithoutWhere = Cypher.match(r);
        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn;
        switch (cypherFunctionType) {
            case COUNT:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(Functions2.count(r));
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(r);
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
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.detachDelete(m).returning(Cypher.count(m));
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.detachDelete(m).returning(m);
        }
        Statement statement = ongoingReadingAndReturn.build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteRelationTypeWithSinglePropertyValueAndFunction(String relationTypeName, CypherFunctionType cypherFunctionType, String propertyName, Object propertyValue) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
        Relationship relation;
        if (propertyName != null) {
            relation = sourceNode.relationshipBetween(targetNode,relationTypeName).named(operationResultName).withProperties(propertyName, Cypher.literalOf(propertyValue));
        } else {
            relation = sourceNode.relationshipBetween(targetNode,relationTypeName).named(operationResultName);
        }
        StatementBuilder.OngoingReadingWithoutWhere currentOngoingReadingWithoutWhere = Cypher.match(relation);
        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn;
        switch (cypherFunctionType) {
            case COUNT:
                CustomContentLiteral returningText = new CustomContentLiteral("count(DISTINCT operationResult)");
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.delete(relation).returning(returningText);
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.delete(relation).returningDistinct(relation);
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
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
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
                        statement = ongoingReadingWithWhere.returning(Cypher.exists(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Cypher.exists(m.property(additionalPropertyName))).build();
                    }
                    break;
                case LABEL:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returning(Cypher.labels(m)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returning(Cypher.labels(m)).build();
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
        //In neo4j version 5, exists(variable.property) is no longer supported. Need use `variable.property IS NOT NULL` instead.
        if(EXISTS.equals(returnFunctionType)){
            //if attribute contains space for example : attribute a, will returned in `` such as .`attribute a`
            if(rel.contains("exists("+operationResultName+".`"+additionalPropertyName+"`)")){
                rel = rel.replace("exists("+operationResultName+".`"+additionalPropertyName+"`)",""+operationResultName+".`"+additionalPropertyName+"` IS NOT NULL AS "+operationResultName);
            }else{
                rel = rel.replace("exists("+operationResultName+"."+additionalPropertyName+")",""+operationResultName+"."+additionalPropertyName+" IS NOT NULL AS "+operationResultName);
            }
        }
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteNodeWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, CypherFunctionType returnFunctionType, String additionalPropertyName) {
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
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
                        statement = ongoingReadingWithWhere.detachDelete(m).returning(Cypher.exists(m.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.detachDelete(m).returning(Cypher.exists(m.property(additionalPropertyName))).build();
                    }
                case ID:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.detachDelete(m).returning(Functions2.id(m)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.detachDelete(m).returning(Functions2.id(m)).build();
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
        //In neo4j version 5, exists(variable.property) is no longer supported. Need use `variable.property IS NOT NULL` instead.
        if(EXISTS.equals(returnFunctionType)){
            //if attribute contains space for example : attribute a, will returned in `` such as .`attribute a`
            if(rel.contains("exists("+operationResultName+".`"+additionalPropertyName+"`)")){
                rel = rel.replace("exists("+operationResultName+".`"+additionalPropertyName+"`)",""+operationResultName+".`"+additionalPropertyName+"` IS NOT NULL AS "+operationResultName);
            }else{
                rel = rel.replace("exists("+operationResultName+"."+additionalPropertyName+")",""+operationResultName+"."+additionalPropertyName+" IS NOT NULL AS "+operationResultName);
            }
        }
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteNodesWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, List<Object> propertyValue) {
        Literal[] listLiteralValue = CommonOperationUtil.generateListLiteralValue(propertyValue);
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(m).in(Cypher.listOf(listLiteralValue)));
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
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
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

    public static String matchRelationPropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, String[] targetPropertyNames) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNodes = Cypher.anyNode().named(targetNodeName);
        Relationship r = sourceNode.relationshipBetween(targetNodes).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(r);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(r).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement;
        if (targetPropertyNames != null && targetPropertyNames.length > 0) {
            Property[] targetPropertiesArray = new Property[targetPropertyNames.length];
            for (int i = 0; i < targetPropertyNames.length; i++) {
                targetPropertiesArray[i] = r.property(targetPropertyNames[i]);
            }
            statement = ongoingReadingWithWhere.returningDistinct(targetPropertiesArray).build();
        } else {
            statement = ongoingReadingWithWhere.returningDistinct(r).build();
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
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
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
                Expression propertyExpression = CommonOperationUtil.getAttributeValueExpress(currentValue);
                targetPropertiesArray[i] = m.property(currentKey).to(propertyExpression);
            }
            statement = ongoingReadingWithWhere.set(targetPropertiesArray).returning(targetNewAddPropertiesArray).build();
        } else {
            statement = ongoingReadingWithWhere.returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String modifyNodeLabelsWithSingleValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, LabelOperationType labelOperationType,String[] labelsArray) {
        Node m = Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement = null;
        switch(labelOperationType){
            case ADD:
                statement = ongoingReadingWithWhere.with(operationResultName).set(m,labelsArray).returning(operationResultName).build();
                break;
            case REMOVE:
                statement = ongoingReadingWithWhere.with(operationResultName).remove(m,labelsArray).returning(operationResultName).build();
                break;
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String modifyLabelsWithLabelMatch(String labelName,LabelOperationType labelOperationType,CypherFunctionType cypherFunctionType,String[] labelsArray) {
        Node m = Cypher.node(labelName).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere currentOngoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn = null;
        switch (cypherFunctionType) {
            case COUNT:
                ongoingReadingAndReturn = switch (labelOperationType) {
                    case ADD -> currentOngoingReadingWithoutWhere.set(m, labelsArray).returning(Cypher.count(m));
                    case REMOVE -> currentOngoingReadingWithoutWhere.remove(m, labelsArray).returning(Cypher.count(m));
                };
                break;
            case ID:
                ongoingReadingAndReturn = switch (labelOperationType) {
                    case ADD -> currentOngoingReadingWithoutWhere.set(m, labelsArray).returning(Functions2.id(m));
                    case REMOVE -> currentOngoingReadingWithoutWhere.remove(m, labelsArray).returning(Functions2.id(m));
                };
                break;
            default:
                ongoingReadingAndReturn = switch (labelOperationType) {
                    case ADD -> currentOngoingReadingWithoutWhere.set(m, labelsArray).returning(m);
                    case REMOVE -> currentOngoingReadingWithoutWhere.remove(m, labelsArray).returning(m);
                };
                break;
        }
        Statement statement = ongoingReadingAndReturn.build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;

        /*

        Node m = Cypher.node(labelName).named(operationResultName);
        Statement statement = null;
        switch(labelOperationType){
            case ADD:
                statement = Cypher.match(m).set(m,labelsArray).returning(operationResultName).build();
                break;
            case REMOVE:
                statement = Cypher.match(m).remove(m,labelsArray).returning(operationResultName).build();
                break;
        }
        String rel = cypherRenderer.render(statement);
        */

        /*
        StatementBuilder.OngoingReadingWithoutWhere currentOngoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn;
        switch (cypherFunctionType) {
            case COUNT:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(Cypher.count(m));
                break;
            case ID:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(Functions2.id(m));
                break;
            default:
                ongoingReadingAndReturn = currentOngoingReadingWithoutWhere.returning(m);
        }
        Statement statement = ongoingReadingAndReturn.build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        */
    }

    public static String setRelationPropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, Map<String, Object> originalTargetPropertiesMap) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNodes = Cypher.anyNode().named(targetNodeName);
        Relationship r = sourceNode.relationshipBetween(targetNodes).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(r);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(r).isEqualTo(Cypher.literalOf(propertyValue)));
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
                targetNewAddPropertiesArray[i] = r.property(currentKey);
                Expression propertyExpression = CommonOperationUtil.getAttributeValueExpress(currentValue);
                targetPropertiesArray[i] = r.property(currentKey).to(propertyExpression);
            }
            statement = ongoingReadingWithWhere.set(targetPropertiesArray).returningDistinct(targetNewAddPropertiesArray).build();
        } else {
            statement = ongoingReadingWithWhere.returningDistinct(r).build();
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
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(m).isEqualTo(Cypher.literalOf(propertyValue)));
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

    public static String removeRelationPropertiesWithSingleValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, List<String> targetPropertiesList) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNodes = Cypher.anyNode().named(targetNodeName);
        Relationship r = sourceNode.relationshipBetween(targetNodes).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(r);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(r).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement;
        if (targetPropertiesList != null && targetPropertiesList.size() > 0) {
            Property[] targetPropertiesRemoveArray = new Property[targetPropertiesList.size()];
            for (int i = 0; i < targetPropertiesList.size(); i++) {
                String currentPropertyName = targetPropertiesList.get(i);
                targetPropertiesRemoveArray[i] = r.property(currentPropertyName);
            }
            statement = ongoingReadingWithWhere.remove(targetPropertiesRemoveArray).returningDistinct(Functions2.keys(r)).build();
        } else {
            statement = ongoingReadingWithWhere.returningDistinct(r).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelatedNodesFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                String targetConceptionKind, String relationKind,
                                                                RelationDirection relationDirection, CypherFunctionType returnFunctionType) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
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
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
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

    public static String matchRelatedNodeAndRelationPairsFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                String targetConceptionKind, String relationKind, RelationDirection relationDirection) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node resultNodes = Cypher.node(targetConceptionKind).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = null;
        Relationship relations = null;

        switch (relationDirection) {
            case FROM:
                relations = sourceNode.relationshipFrom(resultNodes, relationKind).named(relationResultName);
                ongoingReadingWithoutWhere = Cypher.match(relations);
                break;
            case TO:
                relations = sourceNode.relationshipTo(resultNodes, relationKind).named(relationResultName);
                ongoingReadingWithoutWhere = Cypher.match(relations);
                break;
            case TWO_WAY:
                relations = sourceNode.relationshipBetween(resultNodes, relationKind).named(relationResultName);
                ongoingReadingWithoutWhere = Cypher.match(relations);
        }

        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (sourcePropertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                break;
            default:
        }

        Statement statement;

        if (ongoingReadingWithWhere != null) {
            statement = ongoingReadingWithWhere.returning(resultNodes,relations).build();
        } else {
            statement = ongoingReadingWithoutWhere.returning(resultNodes,relations).build();
        }

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelatedPairFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                String targetConceptionKind, String relationKind, RelationDirection relationDirection) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
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
                if(sourcePropertyValue instanceof List){
                    ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).in(Cypher.literalOf(sourcePropertyValue)));
                }else{
                    ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                }
                break;
            default:
        }

        Statement statement = null;
        if (ongoingReadingWithWhere != null) {
            statement = ongoingReadingWithWhere.returning(resultNodes,sourceNode).build();
        } else {
            statement = ongoingReadingWithoutWhere.returning(resultNodes,sourceNode).build();
        }

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelatedNodesFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,QueryParameters targetConceptionKindQueryParameters,
                                                               String relationKind, RelationDirection relationDirection) throws CoreRealmServiceEntityExploreException {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        String targetConceptionKindName = targetConceptionKindQueryParameters != null ? targetConceptionKindQueryParameters.getEntityKind() : null;
        Node resultNodes;
        if(targetConceptionKindName != null ){
            resultNodes = Cypher.node(targetConceptionKindName).named(operationResultName);
        }else{
            resultNodes = Cypher.anyNode().named(operationResultName);
        }

        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = null;
        switch (relationDirection) {
            case FROM:
                if(relationKind != null){
                    ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipFrom(resultNodes, relationKind));
                }else{
                    ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipFrom(resultNodes));
                }
                break;
            case TO:
                if(relationKind != null){
                    ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipTo(resultNodes, relationKind));
                }else{
                    ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipTo(resultNodes));
                }
                break;
            case TWO_WAY:
                if(relationKind != null){
                    ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipBetween(resultNodes, relationKind));
                }else{
                    ongoingReadingWithoutWhere = Cypher.match(sourceNode.relationshipBetween(resultNodes));
                }
        }

        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (sourcePropertyFunctionType) {
            case ID:
                if(sourcePropertyValue instanceof List){
                    ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).in(Cypher.literalOf(sourcePropertyValue)));
                }else{
                    ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                }
                break;
            default:
        }

        Statement statement;

        if (targetConceptionKindQueryParameters != null) {

            int defaultReturnRecordNumber = 10000;
            int skipRecordNumber = 0;
            int limitRecordNumber = 0;

            int startPage = targetConceptionKindQueryParameters.getStartPage();
            int endPage = targetConceptionKindQueryParameters.getEndPage();
            int pageSize = targetConceptionKindQueryParameters.getPageSize();
            int resultNumber = targetConceptionKindQueryParameters.getResultNumber();
            boolean isDistinctMode = targetConceptionKindQueryParameters.isDistinctMode();
            List<SortingItem> sortingItemList = targetConceptionKindQueryParameters.getSortingItems();

            SortItem[] sortItemArray = null;
            if (sortingItemList.size() > 0) {
                sortItemArray = new SortItem[sortingItemList.size()];
                for (int i = 0; i < sortingItemList.size(); i++) {
                    SortingItem currentSortingItem = sortingItemList.get(i);
                    String attributeName = currentSortingItem.getAttributeName();
                    QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                    switch (sortingLogic) {
                        case ASC:
                            sortItemArray[i] = Cypher.sort(resultNodes.property(attributeName)).ascending();
                            break;
                        case DESC:
                            sortItemArray[i] = Cypher.sort(resultNodes.property(attributeName)).descending();
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

            FilteringItem defaultFilteringItem = targetConceptionKindQueryParameters.getDefaultFilteringItem();
            List<FilteringItem> andFilteringItemList = targetConceptionKindQueryParameters.getAndFilteringItemsList();
            List<FilteringItem> orFilteringItemList = targetConceptionKindQueryParameters.getOrFilteringItemsList();
            if (defaultFilteringItem == null) {
                if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                        (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }
            } else {
                ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(resultNodes, defaultFilteringItem));
                if (andFilteringItemList != null && andFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : andFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(resultNodes, currentFilteringItem));
                    }
                }
                if (orFilteringItemList != null && orFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : orFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(resultNodes, currentFilteringItem));
                    }
                }
            }

            if (isDistinctMode) {
                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(resultNodes,sourceNode);
            } else {
                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(resultNodes,sourceNode);
            }

            if (skipRecordNumber != 0) {
                if (sortItemArray != null) {
                    //Function can not use together with sort in this case
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).skip(skipRecordNumber).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.skip(skipRecordNumber).limit(limitRecordNumber).build();
                }
            } else {
                if (sortItemArray != null) {
                    //Function can not use together with sort in this case
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.limit(limitRecordNumber).build();
                }
            }
        }else{
            if (ongoingReadingWithWhere != null) {
                ongoingReadingWithWhere.
                        and(resultNodes.hasLabels(RealmConstant.ConceptionKindClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.AttributesViewKindClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.AttributeKindClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.RelationKindClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.MetaConfigItemsStorageClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.ClassificationClass).not());
                statement = ongoingReadingWithWhere.returning(resultNodes,sourceNode).build();
            } else {
                ongoingReadingWithoutWhere.
                        where(resultNodes.hasLabels(RealmConstant.ConceptionKindClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.AttributesViewKindClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.AttributeKindClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.RelationKindClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.MetaConfigItemsStorageClass).not()).
                        and(resultNodes.hasLabels(RealmConstant.ClassificationClass).not())
                        .returning(resultNodes,sourceNode).build();
                statement = ongoingReadingWithoutWhere.returning(resultNodes,sourceNode).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String createNodesRelationshipByIdsMatch(Long sourceNodeId, Long targetNodeId, String relationKind, Map<String, Object> relationProperties) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
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

    public static String createNodesRelationshipBySingleIdMatch(Long sourceNodeId, String targetNodeVariable, String relationKind, Map<String, Object> relationProperties) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeVariable);
        Relationship relation;
        if (relationProperties != null && relationProperties.size() > 0) {
            MapExpression targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(relationProperties));
            relation = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName).withProperties(targetMapExpression);
        } else {
            relation = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName);
        }
        Statement statement = Cypher.match(sourceNode, targetNode).
                where(sourceNode.internalId().isEqualTo(Cypher.literalOf(sourceNodeId))).create(relation).returning(relation).build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String createNodesRelationshipBySingleIdMatch(String sourceNodeVariable, Long targetNodeId, String relationKind, Map<String, Object> relationProperties) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeVariable);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
        Relationship relation;
        if (relationProperties != null && relationProperties.size() > 0) {
            MapExpression targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(relationProperties));
            relation = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName).withProperties(targetMapExpression);
        } else {
            relation = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName);
        }
        Statement statement = Cypher.match(sourceNode, targetNode).
                where(targetNode.internalId().isEqualTo(Cypher.literalOf(targetNodeId))).create(relation).returning(relation).build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelationshipsByBothNodesId(Long sourceNodeId, Long targetNodeId, String relationKind) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
        Relationship relations = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName);
        Statement statement = Cypher.match(relations).where(sourceNode.internalId().isEqualTo(Cypher.literalOf(sourceNodeId))
                .and(targetNode.internalId().isEqualTo(Cypher.literalOf(targetNodeId)))).returning(relations).build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteRelationWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, CypherFunctionType returnFunctionType, String additionalPropertyName) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
        Relationship relation = sourceNode.relationshipBetween(targetNode).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(relation);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(relation).isEqualTo(Cypher.literalOf(propertyValue)));
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
                        statement = ongoingReadingWithWhere.delete(relation).returning(Cypher.exists(relation.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.delete(relation).returning(Cypher.exists(relation.property(additionalPropertyName))).build();
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
        //In neo4j version 5, exists(variable.property) is no longer supported. Need use `variable.property IS NOT NULL` instead.
        if(EXISTS.equals(returnFunctionType)){
            //if attribute contains space for example : attribute a, will returned in `` such as .`attribute a`
            if(rel.contains("exists("+operationResultName+".`"+additionalPropertyName+"`)")){
                rel = rel.replace("exists("+operationResultName+".`"+additionalPropertyName+"`)",""+operationResultName+".`"+additionalPropertyName+"` IS NOT NULL AS "+operationResultName);
            }else{
                rel = rel.replace("exists("+operationResultName+"."+additionalPropertyName+")",""+operationResultName+"."+additionalPropertyName+" IS NOT NULL AS "+operationResultName);
            }
        }
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelationWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, Object propertyValue, CypherFunctionType returnFunctionType, String additionalPropertyName) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
        Relationship relation = sourceNode.relationshipBetween(targetNode).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(relation);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(relation).isEqualTo(Cypher.literalOf(propertyValue)));
                break;
            default:
        }
        Statement statement = null;
        if (returnFunctionType != null) {
            switch (returnFunctionType) {
                case KEYS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returningDistinct(Functions2.keys(relation)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returningDistinct(Functions2.keys(relation)).build();
                    }
                    break;
                case PROPERTIES:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returningDistinct(Functions2.properties(relation)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returningDistinct(Functions2.properties(relation)).build();
                    }
                    break;
                case EXISTS:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returningDistinct(Cypher.exists(relation.property(additionalPropertyName))).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returningDistinct(Cypher.exists(relation.property(additionalPropertyName))).build();
                    }
                    break;
                case LABEL:
                    if (ongoingReadingWithWhere != null) {
                        statement = ongoingReadingWithWhere.returningDistinct(Cypher.type(relation)).build();
                    } else {
                        statement = ongoingReadingWithoutWhere.returningDistinct(Cypher.type(relation)).build();
                    }
            }
        } else {
            if (ongoingReadingWithWhere != null) {
                statement = ongoingReadingWithWhere.returningDistinct(relation,sourceNode,targetNode).build();
            } else {
                statement = ongoingReadingWithoutWhere.returningDistinct(relation,sourceNode,targetNode).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        //In neo4j version 5, exists(variable.property) is no longer supported. Need use `variable.property IS NOT NULL` instead.
        if(EXISTS.equals(returnFunctionType)){
            //if attribute contains space for example : attribute a, will returned in `` such as .`attribute a`
            if(rel.contains("exists("+operationResultName+".`"+additionalPropertyName+"`)")){
                rel = rel.replace("exists("+operationResultName+".`"+additionalPropertyName+"`)",""+operationResultName+".`"+additionalPropertyName+"` IS NOT NULL AS "+operationResultName);
            }else{
                rel = rel.replace("exists("+operationResultName+"."+additionalPropertyName+")",""+operationResultName+"."+additionalPropertyName+" IS NOT NULL AS "+operationResultName);
            }
        }
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String deleteRelationsWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType, List<Object> propertyValue) {
        Literal[] listLiteralValue = CommonOperationUtil.generateListLiteralValue(propertyValue);
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
        Relationship m = sourceNode.relationshipBetween(targetNode).named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(m);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (propertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(m).in(Cypher.listOf(listLiteralValue)));
                break;
            default:
        }
        Statement statement = null;
        if (ongoingReadingWithWhere != null) {
            statement = ongoingReadingWithWhere.delete(m).returningDistinct(m).build();
        } else {
            statement = ongoingReadingWithoutWhere.delete(m).returningDistinct(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String mergeRelatedNodesFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                String targetConceptionKind, String relationKind,
                                                                RelationDirection relationDirection, CypherFunctionType returnFunctionType) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node resultNodes = Cypher.node(targetConceptionKind).named(operationResultName);

        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(sourceNode);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        switch (sourcePropertyFunctionType) {
            case ID:
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
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
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
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
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
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
        int defaultReturnRecordNumber = 10000;
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
                    returnedFunctionValue = Cypher.count(m);
                    break;
                case ID:
                    returnedFunctionValue = Functions2.id(m);
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
            Expression returnedFunctionValue2 = null;
            if (cypherFunctionType != null) {
                switch (cypherFunctionType) {
                    case COUNT:
                        returnedFunctionValue2 = Cypher.count(m);
                        break;
                    case ID:
                        returnedFunctionValue2 = Functions2.id(m);
                        break;
                }
            }
            if(returnedFunctionValue2 != null){
                statement = Cypher.match(m).returning(returnedFunctionValue2).build();
            }else{
                statement = Cypher.match(m).returning(m).limit(defaultReturnRecordNumber).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchAttributesWithQueryParameters(String labelName, QueryParameters queryParameters,List<String> returnProperties) throws CoreRealmServiceEntityExploreException {
        int defaultReturnRecordNumber = 10000;
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

        returnPropertyArray[returnProperties.size()] = Functions2.id(m);

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
                                                                RelationDirection relationDirection,int minJump,int maxJump,ReturnRelationableDataType returnRelationableDataType) {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node resultNodes = targetConceptionKind != null ? Cypher.node(targetConceptionKind).named(operationResultName):Cypher.anyNode().named(operationResultName);
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
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                break;
            default:
        }

        Statement statement = null;
        if (ongoingReadingWithWhere != null) {
            switch(returnRelationableDataType){
                case BOTH:
                    statement = ongoingReadingWithWhere.returning(resultNodes,resultRelationship).build();
                    break;
                case NODE:
                    statement = ongoingReadingWithWhere.returningDistinct(resultNodes).build();
                    break;
                case RELATION:
                    statement = ongoingReadingWithWhere.returningDistinct(resultRelationship).build();
                    break;
                case COUNT_NODE:
                    statement = ongoingReadingWithWhere.returning(Cypher.countDistinct(resultNodes)).build();
                    break;
                case COUNT_RELATION:
                    statement = ongoingReadingWithWhere.returningDistinct(Functions2.count(resultRelationship)).build();
                    break;
            }
        } else {
            switch(returnRelationableDataType){
                case BOTH:
                    statement = ongoingReadingWithoutWhere.returning(resultNodes,resultRelationship).build();
                    break;
                case NODE:
                    statement = ongoingReadingWithoutWhere.returningDistinct(resultNodes).build();
                    break;
                case RELATION:
                    statement = ongoingReadingWithoutWhere.returningDistinct(resultRelationship).build();
                    break;
                case COUNT_NODE:
                    statement = ongoingReadingWithoutWhere.returning(Cypher.countDistinct(resultNodes)).build();
                    break;
                case COUNT_RELATION:
                    statement = ongoingReadingWithoutWhere.returningDistinct(Functions2.count(resultRelationship)).build();
                    break;
            }
        }

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherFunctionType sourcePropertyFunctionType, Object sourcePropertyValue,
                                                                            String targetConceptionKind, String relationKind,
                                                                            RelationDirection relationDirection, int minJump, int maxJump,
                                                                            AttributesParameters relationAttributesParameters, AttributesParameters conceptionAttributesParameters,
                                                                            ResultEntitiesParameters resultEntitiesParameters,ReturnRelationableDataType returnRelationableDataType) throws CoreRealmServiceEntityExploreException {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node resultNodes = targetConceptionKind != null ? Cypher.node(targetConceptionKind).named(operationResultName):Cypher.anyNode().named(operationResultName);
        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = null;
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        Relationship resultRelationship = null;
        NamedPath relationshipNamedPath = null;
        Statement relationshipNamedPathStatement = null;

        String pathPropName = "path";
        String relationshipUnwindedProp = "middleRelation";

        boolean isDistinctMode = resultEntitiesParameters != null? resultEntitiesParameters.isDistinctMode() : true;
        switch (relationDirection) {
            case FROM:
                if(minJump != 0 & maxJump != 0 & maxJump>=minJump) {
                    resultRelationship = sourceNode.relationshipFrom(resultNodes, relationKind).length(minJump,maxJump).named(relationResultName);
                }else{
                    resultRelationship = sourceNode.relationshipFrom(resultNodes, relationKind).unbounded().named(relationResultName);
                }
                break;
            case TO:
                if(minJump != 0 & maxJump != 0 & maxJump>=minJump) {
                    resultRelationship = sourceNode.relationshipTo(resultNodes, relationKind).length(minJump,maxJump).named(relationResultName);
                }else{
                    resultRelationship = sourceNode.relationshipTo(resultNodes, relationKind).unbounded().named(relationResultName);
                }
                break;
            case TWO_WAY:
                if(minJump != 0 & maxJump != 0 & maxJump>=minJump) {
                    resultRelationship = sourceNode.relationshipBetween(resultNodes, relationKind).length(minJump,maxJump).named(relationResultName);
                }else{
                    resultRelationship = sourceNode.relationshipBetween(resultNodes, relationKind).unbounded().named(relationResultName);
                }
        }
        ongoingReadingWithoutWhere = Cypher.match(resultRelationship);

        if(relationAttributesParameters != null){
            relationshipNamedPath = Cypher.path(pathPropName).definedBy(resultRelationship);
            relationshipNamedPathStatement = Cypher.match(relationshipNamedPath).returning(relationshipNamedPath).build();
            Node unwindRelationAlias = Cypher.anyNode().named(relationshipUnwindedProp);
            FilteringItem defaultRelationFilteringItem = relationAttributesParameters.getDefaultFilteringItem();
            List<FilteringItem> andRelationFilteringItemList = relationAttributesParameters.getAndFilteringItemsList();
            List<FilteringItem> orRelationFilteringItemList = relationAttributesParameters.getOrFilteringItemsList();
            if (defaultRelationFilteringItem == null) {
                if ((andRelationFilteringItemList != null && andRelationFilteringItemList.size() > 0) ||
                        (orRelationFilteringItemList != null && orRelationFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }
            } else {
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(CommonOperationUtil.getQueryCondition(unwindRelationAlias, defaultRelationFilteringItem));
                if (andRelationFilteringItemList != null && andRelationFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : andRelationFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(unwindRelationAlias, currentFilteringItem));
                    }
                }
                if (orRelationFilteringItemList != null && orRelationFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : orRelationFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(unwindRelationAlias, currentFilteringItem));
                    }
                }
            }
        }

        switch (sourcePropertyFunctionType) {
            case ID:
                if(ongoingReadingWithWhere != null){
                    ongoingReadingWithWhere = ongoingReadingWithWhere.and(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                }else{
                    ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(sourcePropertyValue)));
                }
                break;
            default:
        }

        if(conceptionAttributesParameters != null){
            FilteringItem defaultConceptionFilteringItem = conceptionAttributesParameters.getDefaultFilteringItem();
            List<FilteringItem> andConceptionFilteringItemList = conceptionAttributesParameters.getAndFilteringItemsList();
            List<FilteringItem> orConceptionFilteringItemList = conceptionAttributesParameters.getOrFilteringItemsList();
            if (defaultConceptionFilteringItem == null) {
                if ((andConceptionFilteringItemList != null && andConceptionFilteringItemList.size() > 0) ||
                        (orConceptionFilteringItemList != null && orConceptionFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }
            } else {
                if(ongoingReadingWithWhere == null){
                    ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(CommonOperationUtil.getQueryCondition(resultNodes, defaultConceptionFilteringItem));
                }else{
                    ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(resultNodes, defaultConceptionFilteringItem));
                }
                if (andConceptionFilteringItemList != null && andConceptionFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : andConceptionFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(resultNodes, currentFilteringItem));
                    }
                }
                if (orConceptionFilteringItemList != null && orConceptionFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : orConceptionFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(resultNodes, currentFilteringItem));
                    }
                }
            }
        }

        int defaultReturnRecordNumber = 10000;
        int skipRecordNumber = 0;
        int limitRecordNumber = 0;
        SortItem[] sortItemArray = null;
        if(resultEntitiesParameters != null){
            int startPage = resultEntitiesParameters.getStartPage();
            int endPage = resultEntitiesParameters.getEndPage();
            int pageSize = resultEntitiesParameters.getPageSize();
            int resultNumber = resultEntitiesParameters.getResultNumber();
            List<SortingItem> sortingItemList = resultEntitiesParameters.getSortingItems();
            if ((sortingItemList.size() > 0 && returnRelationableDataType.equals(NODE)) || (sortingItemList.size() > 0 && returnRelationableDataType.equals(RELATION))){
                sortItemArray = new SortItem[sortingItemList.size()];
                for (int i = 0; i < sortingItemList.size(); i++) {
                    SortingItem currentSortingItem = sortingItemList.get(i);
                    String attributeName = currentSortingItem.getAttributeName();
                    QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                    switch (sortingLogic) {
                        case ASC:
                            switch(returnRelationableDataType){
                                case NODE:
                                    sortItemArray[i] = Cypher.sort(resultNodes.property(attributeName)).ascending();
                                    break;
                                case RELATION:
                                    sortItemArray[i] = Cypher.sort(resultRelationship.property(attributeName)).ascending();
                                    break;
                            }
                            break;
                        case DESC:
                            switch(returnRelationableDataType){
                                case NODE:
                                    sortItemArray[i] = Cypher.sort(resultNodes.property(attributeName)).descending();
                                    break;
                                case RELATION:
                                    sortItemArray[i] = Cypher.sort(resultRelationship.property(attributeName)).descending();
                                    break;
                            }
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
        }

        StatementBuilder.OngoingReadingAndReturn ongoingReadingAndReturn = null;
        Statement statement = null;
        if (ongoingReadingWithWhere != null) {
            switch(returnRelationableDataType){
                case BOTH:
                    ongoingReadingAndReturn = ongoingReadingWithWhere.returning(resultNodes,resultRelationship);
                    break;
                case NODE:
                    if(isDistinctMode){
                        ongoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(resultNodes);
                    }else{
                        ongoingReadingAndReturn = ongoingReadingWithWhere.returning(resultNodes);
                    }
                    break;
                case RELATION:
                    if(isDistinctMode){
                        ongoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(resultRelationship);
                    }else{
                        ongoingReadingAndReturn = ongoingReadingWithWhere.returning(resultRelationship);
                    }
                    break;
                case COUNT_NODE:
                    if(isDistinctMode){
                        ongoingReadingAndReturn = ongoingReadingWithWhere.returning(Cypher.countDistinct(resultNodes));
                    }else{
                        ongoingReadingAndReturn = ongoingReadingWithWhere.returning(Cypher.count(resultNodes));
                    }
                    break;
                case COUNT_RELATION:
                    if(isDistinctMode){
                        ongoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(Functions2.count(resultRelationship));
                    }else{
                        ongoingReadingAndReturn = ongoingReadingWithWhere.returning(Functions2.count(resultRelationship));
                    }
                    break;
            }
        } else {
            switch(returnRelationableDataType){
                case BOTH:
                    ongoingReadingAndReturn = ongoingReadingWithoutWhere.returning(resultNodes,resultRelationship);
                    break;
                case NODE:
                    if(isDistinctMode){
                        ongoingReadingAndReturn = ongoingReadingWithoutWhere.returningDistinct(resultNodes);
                    }else{
                        ongoingReadingAndReturn = ongoingReadingWithoutWhere.returning(resultNodes);
                    }
                    break;
                case RELATION:
                    if(isDistinctMode){
                        ongoingReadingAndReturn = ongoingReadingWithoutWhere.returningDistinct(resultRelationship);
                    }else{
                        ongoingReadingAndReturn = ongoingReadingWithoutWhere.returning(resultRelationship);
                    }
                    break;
                case COUNT_NODE:
                    if(isDistinctMode){
                        ongoingReadingAndReturn = ongoingReadingWithoutWhere.returning(Cypher.countDistinct(resultNodes));
                    }else{
                        ongoingReadingAndReturn = ongoingReadingWithoutWhere.returning(Cypher.count(resultNodes));
                    }
                    break;
                case COUNT_RELATION:
                    if(isDistinctMode){
                        ongoingReadingAndReturn = ongoingReadingWithoutWhere.returningDistinct(Functions2.count(resultRelationship));
                    }else{
                        ongoingReadingAndReturn = ongoingReadingWithoutWhere.returning(Functions2.count(resultRelationship));
                    }
                    break;
            }
        }
        if (skipRecordNumber != 0){
            if(sortItemArray != null){
                statement = ongoingReadingAndReturn.orderBy(sortItemArray).skip(skipRecordNumber).limit(limitRecordNumber).build();
            }else{
                statement = ongoingReadingAndReturn.skip(skipRecordNumber).limit(limitRecordNumber).build();
            }
        }else{
            if(sortItemArray != null){
                statement = ongoingReadingAndReturn.orderBy(sortItemArray).limit(limitRecordNumber).build();
            }else{
                statement = ongoingReadingAndReturn.limit(limitRecordNumber).build();
            }
        }

        String rel = null;
        if(relationshipNamedPathStatement != null){
            String firstPart =  cypherRenderer.render(relationshipNamedPathStatement);
            int indexOfReturn = firstPart.indexOf("RETURN");
            rel = firstPart.substring(0,indexOfReturn)+" "+
                    "UNWIND relationships("+pathPropName+") AS "+relationshipUnwindedProp+" "+
                    cypherRenderer.render(statement);
        }else{
            rel = cypherRenderer.render(statement);
        }
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelationshipsWithQueryParameters(CypherFunctionType nodePropertyFunctionType,
                                                               String sourceNodeProperty, String targetNodeProperty,boolean ignoreDirection,
                                                               QueryParameters relationshipQueryParameters, CypherFunctionType returnFunctionType) throws CoreRealmServiceEntityExploreException {
        if(nodePropertyFunctionType == null){
            return null;
        }else {
            Node sourceNode = null;
            Node targetNode = null;
            switch (nodePropertyFunctionType) {
                case ID:
                    sourceNode = Cypher.anyNode().named(sourceNodeName);
                    targetNode = Cypher.anyNode().named(targetNodeName);
                    break;
                case LABEL:
                    if (sourceNodeProperty != null) {
                        sourceNode = Cypher.node(sourceNodeProperty).named(sourceNodeName);
                    } else {
                        sourceNode = Cypher.anyNode().named(sourceNodeName);
                    }
                    if (targetNodeProperty != null) {
                        sourceNode = Cypher.node(targetNodeProperty).named(targetNodeName);
                    } else {
                        targetNode = Cypher.anyNode().named(targetNodeName);
                    }
                    break;
            }

            Relationship relations;
            if (relationshipQueryParameters != null && relationshipQueryParameters.getEntityKind() != null) {
                if (ignoreDirection & (sourceNodeProperty == null | targetNodeProperty == null)) {
                    relations = sourceNode.relationshipBetween(targetNode, relationshipQueryParameters.getEntityKind()).named(operationResultName);
                } else {
                    relations = sourceNode.relationshipTo(targetNode, relationshipQueryParameters.getEntityKind()).named(operationResultName);
                }
            } else {
                if (ignoreDirection & (sourceNodeProperty == null | targetNodeProperty == null)) {
                    relations = sourceNode.relationshipBetween(targetNode).named(operationResultName);
                } else {
                    relations = sourceNode.relationshipTo(targetNode).named(operationResultName);
                }
            }

            StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(relations);
            StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
            switch (nodePropertyFunctionType) {
                case ID:
                    if (sourceNodeProperty != null) {
                        ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).isEqualTo(Cypher.literalOf(Long.parseLong(sourceNodeProperty))));
                    }
                    if (targetNodeProperty != null) {
                        if (ongoingReadingWithWhere != null) {
                            ongoingReadingWithWhere = ongoingReadingWithWhere.and(Functions2.id(targetNode).isEqualTo(Cypher.literalOf(Long.parseLong(targetNodeProperty))));
                        } else {
                            ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(targetNode).isEqualTo(Cypher.literalOf(Long.parseLong(targetNodeProperty))));
                        }
                    }
                    break;
            }

            if (relationshipQueryParameters != null) {
                FilteringItem defaultFilteringItem = relationshipQueryParameters.getDefaultFilteringItem();
                List<FilteringItem> andFilteringItemList = relationshipQueryParameters.getAndFilteringItemsList();
                List<FilteringItem> orFilteringItemList = relationshipQueryParameters.getOrFilteringItemsList();
                if (defaultFilteringItem == null) {
                    if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                            (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                        logger.error("Default Filtering Item is required");
                        CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                        e.setCauseMessage("Default Filtering Item is required");
                        throw e;
                    }
                } else {
                    if(ongoingReadingWithWhere != null){
                        ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(relations, defaultFilteringItem));
                    }else{
                        ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(CommonOperationUtil.getQueryCondition(relations, defaultFilteringItem));
                    }
                    if(andFilteringItemList != null && andFilteringItemList.size() > 0) {
                        for (FilteringItem currentFilteringItem : andFilteringItemList) {
                            ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(relations, currentFilteringItem));
                        }
                    }
                    if (orFilteringItemList != null && orFilteringItemList.size() > 0) {
                        for (FilteringItem currentFilteringItem : orFilteringItemList) {
                            ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(relations, currentFilteringItem));
                        }
                    }
                }
            }

            boolean isDistinct = false;
            if(relationshipQueryParameters != null){
                isDistinct = relationshipQueryParameters.isDistinctMode();
            }

            StatementBuilder.OngoingReadingAndReturn activeOngoingReadingAndReturn = null;

            if (returnFunctionType != null) {
                switch (returnFunctionType) {
                    case KEYS:
                        if (ongoingReadingWithWhere != null) {
                            if(isDistinct){
                                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(Functions2.keys(relations));
                            }else{
                                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(Functions2.keys(relations));
                            }
                        } else {
                            if(isDistinct){
                                activeOngoingReadingAndReturn = ongoingReadingWithoutWhere.returningDistinct(Functions2.keys(relations));
                            }else{
                                activeOngoingReadingAndReturn = ongoingReadingWithoutWhere.returning(Functions2.keys(relations));
                            }
                        }
                        break;
                    case PROPERTIES:
                        if (ongoingReadingWithWhere != null) {
                            if(isDistinct){
                                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(Functions2.properties(relations));
                            }else{
                                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(Functions2.properties(relations));
                            }
                        } else {
                            if(isDistinct){
                                activeOngoingReadingAndReturn = ongoingReadingWithoutWhere.returningDistinct(Functions2.properties(relations));
                            }else{
                                activeOngoingReadingAndReturn = ongoingReadingWithoutWhere.returning(Functions2.properties(relations));
                            }
                        }
                        break;
                    case COUNT:
                        if (ongoingReadingWithWhere != null) {
                            if(isDistinct){
                                CustomContentLiteral returningText = new CustomContentLiteral("count(DISTINCT operationResult)");
                                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(returningText);
                            }else{
                                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(Functions2.count(relations));
                            }
                        } else {
                            if(isDistinct){
                                CustomContentLiteral returningText = new CustomContentLiteral("count(DISTINCT operationResult)");
                                activeOngoingReadingAndReturn = ongoingReadingWithoutWhere.returning(returningText);
                            }else{
                                activeOngoingReadingAndReturn = ongoingReadingWithoutWhere.returning(Functions2.count(relations));
                            }
                        }
                        break;
                }
            } else {
                if (ongoingReadingWithWhere != null) {
                    if(isDistinct){
                        activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(relations,sourceNode,targetNode);
                    }else{
                        activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(relations,sourceNode,targetNode);
                    }
                } else {
                    if(isDistinct){
                        activeOngoingReadingAndReturn = ongoingReadingWithoutWhere.returningDistinct(relations,sourceNode,targetNode);
                    }else{
                        activeOngoingReadingAndReturn = ongoingReadingWithoutWhere.returning(relations,sourceNode,targetNode);
                    }
                }
            }

            int defaultReturnRecordNumber = 10000;
            int skipRecordNumber = 0;
            int limitRecordNumber = 0;
            SortItem[] sortItemArray = null;

            if (relationshipQueryParameters != null) {
                int startPage = relationshipQueryParameters.getStartPage();
                int endPage = relationshipQueryParameters.getEndPage();
                int pageSize = relationshipQueryParameters.getPageSize();
                int resultNumber = relationshipQueryParameters.getResultNumber();
                List<SortingItem> sortingItemList = relationshipQueryParameters.getSortingItems();

                if (sortingItemList.size() > 0) {
                    sortItemArray = new SortItem[sortingItemList.size()];
                    for (int i = 0; i < sortingItemList.size(); i++) {
                        SortingItem currentSortingItem = sortingItemList.get(i);
                        String attributeName = currentSortingItem.getAttributeName();
                        QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                        switch (sortingLogic) {
                            case ASC:
                                sortItemArray[i] = Cypher.sort(relations.property(attributeName)).ascending();
                                break;
                            case DESC:
                                sortItemArray[i] = Cypher.sort(relations.property(attributeName)).descending();
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
            }

            Statement statement = null;
            if (limitRecordNumber != 0) {
                if (sortItemArray != null && returnFunctionType == null) {
                    //Function can not use together with sort in this case
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).skip(skipRecordNumber).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.skip(skipRecordNumber).limit(limitRecordNumber).build();
                }
            } else {
                if (sortItemArray != null && returnFunctionType == null) {
                    //Function can not use together with sort in this case
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.build();
                }
            }

            String rel = cypherRenderer.render(statement);
            logger.debug("Generated Cypher Statement: {}", rel);
            return rel;
        }
    }

    public static String statistNodesWithQueryParametersAndStatisticFunctions(String labelName, QueryParameters queryParameters, List<NumericalAttributeStatisticCondition> statisticConditions, String groupByProperty) throws CoreRealmServiceEntityExploreException {
        Node m = null;
        Statement statement = null;
        if (labelName != null) {
            m = Cypher.node(labelName).named(operationResultName);
        } else {
            m = Cypher.anyNode().named(operationResultName);
        }

        Expression[] returnedFunctionValue = groupByProperty != null ? new Expression[statisticConditions.size()+1] : new Expression[statisticConditions.size()];
        int currentStatisticConditionIndex = 0;
        for(NumericalAttributeStatisticCondition currentNumericalAttributeStatisticCondition : statisticConditions){
            Expression currentStatisticFunctionValue = null;
            String currentKey = currentNumericalAttributeStatisticCondition.getAttributeName();
            StatisticalAndEvaluable.StatisticFunction currentStatisticFunction = currentNumericalAttributeStatisticCondition.getStatisticFunction();
            switch(currentStatisticFunction){
                case AVG:
                    currentStatisticFunctionValue = Cypher.avg(m.property(currentKey));
                    break;
                case MAX:
                    currentStatisticFunctionValue = Cypher.max(m.property(currentKey));
                    break;
                case MIN:
                    currentStatisticFunctionValue = Cypher.min(m.property(currentKey));
                    break;
                case SUM:
                    currentStatisticFunctionValue = Cypher.sum(m.property(currentKey));
                    break;
                case COUNT:
                    currentStatisticFunctionValue = Cypher.count(m.property(currentKey));
                    break;
                case STDEV:
                    currentStatisticFunctionValue = Cypher.stDev(m.property(currentKey));
                    break;
            }
            returnedFunctionValue[currentStatisticConditionIndex] = currentStatisticFunctionValue;
            currentStatisticConditionIndex++;
        }
        if(groupByProperty != null){
            returnedFunctionValue[currentStatisticConditionIndex] = m.property(groupByProperty);
        }

        boolean isDistinctMode = queryParameters.isDistinctMode();

        StatementBuilder.OngoingReadingAndReturn activeOngoingReadingAndReturn = null;

        if (isDistinctMode) {
            activeOngoingReadingAndReturn = Cypher.match(m).returningDistinct(returnedFunctionValue);
        } else {
            activeOngoingReadingAndReturn = Cypher.match(m).returning(returnedFunctionValue);
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
                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(returnedFunctionValue);
            } else {
                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(returnedFunctionValue);
            }
        }
        statement = activeOngoingReadingAndReturn.build();

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String statistRelationsWithQueryParametersAndStatisticFunctions(String typeName, QueryParameters queryParameters, List<NumericalAttributeStatisticCondition> statisticConditions, String groupByProperty) throws CoreRealmServiceEntityExploreException {
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
        Relationship relations;
        Statement statement = null;
        if (typeName != null) {
            relations = sourceNode.relationshipBetween(targetNode, typeName).named(operationResultName);
        } else {
            relations = sourceNode.relationshipBetween(targetNode).named(operationResultName);
        }

        Expression[] returnedFunctionValue = groupByProperty != null ? new Expression[statisticConditions.size()+1] : new Expression[statisticConditions.size()];
        int currentStatisticConditionIndex = 0;
        for(NumericalAttributeStatisticCondition currentNumericalAttributeStatisticCondition : statisticConditions){
            Expression currentStatisticFunctionValue = null;
            String currentKey = currentNumericalAttributeStatisticCondition.getAttributeName();
            StatisticalAndEvaluable.StatisticFunction currentStatisticFunction = currentNumericalAttributeStatisticCondition.getStatisticFunction();
            switch(currentStatisticFunction){
                case AVG:
                    currentStatisticFunctionValue = Cypher.avg(relations.property(currentKey));
                    break;
                case MAX:
                    currentStatisticFunctionValue = Cypher.max(relations.property(currentKey));
                    break;
                case MIN:
                    currentStatisticFunctionValue = Cypher.min(relations.property(currentKey));
                    break;
                case SUM:
                    currentStatisticFunctionValue = Cypher.sum(relations.property(currentKey));
                    break;
                case COUNT:
                    currentStatisticFunctionValue = Cypher.count(relations.property(currentKey));
                    break;
                case STDEV:
                    currentStatisticFunctionValue = Cypher.stDev(relations.property(currentKey));
                    break;
            }
            returnedFunctionValue[currentStatisticConditionIndex] = currentStatisticFunctionValue;
            currentStatisticConditionIndex++;
        }
        if(groupByProperty != null){
            returnedFunctionValue[currentStatisticConditionIndex] = relations.property(groupByProperty);
        }

        boolean isDistinctMode = queryParameters.isDistinctMode();

        StatementBuilder.OngoingReadingAndReturn activeOngoingReadingAndReturn = null;

        if (isDistinctMode) {
            activeOngoingReadingAndReturn = Cypher.match(relations).returningDistinct(returnedFunctionValue);
        } else {
            activeOngoingReadingAndReturn = Cypher.match(relations).returning(returnedFunctionValue);
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
            StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = Cypher.match(relations).where(CommonOperationUtil.getQueryCondition(relations, defaultFilteringItem));
            if (andFilteringItemList != null && andFilteringItemList.size() > 0) {
                for (FilteringItem currentFilteringItem : andFilteringItemList) {
                    ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(relations, currentFilteringItem));
                }
            }
            if (orFilteringItemList != null && orFilteringItemList.size() > 0) {
                for (FilteringItem currentFilteringItem : orFilteringItemList) {
                    ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(relations, currentFilteringItem));
                }
            }
            if (isDistinctMode) {
                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returningDistinct(returnedFunctionValue);
            } else {
                activeOngoingReadingAndReturn = ongoingReadingWithWhere.returning(returnedFunctionValue);
            }
        }
        statement = activeOngoingReadingAndReturn.build();

        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    /* Used for APOC query
    Example:
    https://neo4j.com/labs/apoc/4.1/graph-querying/expand-paths-config/#path-expander-paths-config-config-relationship-filters
    */
    public static String generateRelationKindMatchLogicsQuery(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch){
        String relationMatchLogicFullString = null;
        if(relationKindMatchLogics != null && relationKindMatchLogics.size()>0){
            boolean isFirstMatchLogic = true;
            for(RelationKindMatchLogic currentRelationKindMatchLogic:relationKindMatchLogics){
                String currentRelationKindName = currentRelationKindMatchLogic.getRelationKindName();
                if(currentRelationKindName != null){
                    String currentRelationMatchLogicString = null;
                    switch(currentRelationKindMatchLogic.getRelationDirection()){
                        case FROM: currentRelationMatchLogicString = currentRelationKindName+">";
                            break;
                        case TO: currentRelationMatchLogicString = "<"+currentRelationKindName;
                            break;
                        case TWO_WAY:currentRelationMatchLogicString = currentRelationKindName;
                    }
                    if(isFirstMatchLogic){
                        relationMatchLogicFullString = currentRelationMatchLogicString;
                        isFirstMatchLogic = false;
                    }else{
                        relationMatchLogicFullString = relationMatchLogicFullString + "|"+currentRelationMatchLogicString;
                    }
                }
            }
            if(relationMatchLogicFullString == null){
                relationMatchLogicFullString = "";
            }
        }else{
            if(defaultDirectionForNoneRelationKindMatch != null){
                switch(defaultDirectionForNoneRelationKindMatch) {
                    case FROM: relationMatchLogicFullString = ">";
                        break;
                    case TO: relationMatchLogicFullString = "<";
                        break;
                    case TWO_WAY:relationMatchLogicFullString = "";
                }
            }else{
                relationMatchLogicFullString = "";
            }
        }
        return relationMatchLogicFullString;
    }

    /* Used for APOC query
    Example:
    https://neo4j.com/labs/apoc/4.1/graph-querying/expand-paths-config/#path-expander-paths-config-config-relationship-filters
    */
    public static String generateConceptionKindMatchLogicsQuery(List<ConceptionKindMatchLogic> conceptionKindMatchLogics){
        String conceptionMatchLogicFullString = null;
        if(conceptionKindMatchLogics != null && conceptionKindMatchLogics.size()>0){
            boolean isFirstMatchLogic = true;
            for(ConceptionKindMatchLogic currentConceptionKindMatchLogic:conceptionKindMatchLogics){
                String conceptionKindName = currentConceptionKindMatchLogic.getConceptionKindName();
                if(conceptionKindName != null){
                    String currentConceptionMatchLogicString = null;
                    if(currentConceptionKindMatchLogic instanceof MatchAllConceptionKindLogic){
                        currentConceptionMatchLogicString = "*";
                    }else{
                        switch(currentConceptionKindMatchLogic.getConceptionKindExistenceRule()){
                            case NOT_ALLOW: currentConceptionMatchLogicString = "-"+conceptionKindName;
                                break;
                            case END_WITH: currentConceptionMatchLogicString = ">"+conceptionKindName;
                                break;
                            case MUST_HAVE: currentConceptionMatchLogicString = "+"+conceptionKindName;
                                break;
                            case TERMINATE_AT: currentConceptionMatchLogicString = "/"+conceptionKindName;
                                break;
                        }
                    }
                    if(isFirstMatchLogic){
                        conceptionMatchLogicFullString = currentConceptionMatchLogicString;
                        isFirstMatchLogic = false;
                    }else{
                        conceptionMatchLogicFullString = conceptionMatchLogicFullString + "|"+currentConceptionMatchLogicString;
                    }
                }
            }
            if(conceptionMatchLogicFullString == null){
                conceptionMatchLogicFullString = "";
            }
        }else{
            conceptionMatchLogicFullString = "";
        }
        return conceptionMatchLogicFullString;
    }

    public static String generateAttributesParametersQueryLogic(AttributesParameters attributesParameters,String entityNodeName) throws CoreRealmServiceEntityExploreException {
        String tempMatchStringForReplace = "MATCH ("+entityNodeName+") ";
        String tempReturnStringForReplace = " RETURN TEMP_RESULT";
        if(attributesParameters != null){
            Node unwindRelationAlias = Cypher.anyNode().named(entityNodeName);
            StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(unwindRelationAlias);
            StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
            FilteringItem defaultRelationFilteringItem = attributesParameters.getDefaultFilteringItem();
            List<FilteringItem> andRelationFilteringItemList = attributesParameters.getAndFilteringItemsList();
            List<FilteringItem> orRelationFilteringItemList = attributesParameters.getOrFilteringItemsList();
            if (defaultRelationFilteringItem == null) {
                logger.error("Default Filtering Item is required");
                CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                e.setCauseMessage("Default Filtering Item is required");
                throw e;
            } else {
                ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(CommonOperationUtil.getQueryCondition(unwindRelationAlias, defaultRelationFilteringItem));
                if (andRelationFilteringItemList != null && andRelationFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : andRelationFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(unwindRelationAlias, currentFilteringItem));
                    }
                }
                if (orRelationFilteringItemList != null && orRelationFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : orRelationFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(unwindRelationAlias, currentFilteringItem));
                    }
                }
            }

            String fullCql = cypherRenderer.render(ongoingReadingWithWhere.returning("TEMP_RESULT").build());
            String whereLogic = fullCql.replace(tempMatchStringForReplace,"").replace(tempReturnStringForReplace,"");
            return whereLogic;
        }else{
            return "";
        }
    }

    public static String matchNodeWithSpecialRelationAndAttributeFilter(String relationKind, RelationDirection relationDirection, String originalConceptionKind,List<String> originalConceptionEntityUIDS,String aimConceptionKind, QueryParameters targetConceptionKindQueryParameters) throws CoreRealmServiceEntityExploreException {

        List<Long> originalConceptionEntitiesUIDList = null;
        if(originalConceptionEntityUIDS != null){
            originalConceptionEntitiesUIDList = new ArrayList<>();
            for(int i=0;i<originalConceptionEntityUIDS.size();i++){
                originalConceptionEntitiesUIDList.add(Long.parseLong(originalConceptionEntityUIDS.get(i)));
            }
        }

        Node sourceNode = Cypher.node(originalConceptionKind).named(sourceNodeName);
        Node resultNodes = aimConceptionKind != null ? Cypher.node(aimConceptionKind).named(operationResultName):Cypher.anyNode().named(operationResultName);
        Relationship r = null;
        switch(relationDirection){
            case FROM:
                r = sourceNode.relationshipTo(resultNodes,relationKind).named(relationResultName);
                break;
            case TO:
                r = sourceNode.relationshipFrom(resultNodes,relationKind).named(relationResultName);
                break;
            case TWO_WAY:
                r = sourceNode.relationshipBetween(resultNodes,relationKind).named(relationResultName);
        }

        StatementBuilder.OngoingReadingWithoutWhere ongoingReadingWithoutWhere = Cypher.match(r);
        StatementBuilder.OngoingReadingWithWhere ongoingReadingWithWhere = null;
        Statement statement;

        if (targetConceptionKindQueryParameters != null) {
            int defaultReturnRecordNumber = 10000;
            int skipRecordNumber = 0;
            int limitRecordNumber = 0;

            int startPage = targetConceptionKindQueryParameters.getStartPage();
            int endPage = targetConceptionKindQueryParameters.getEndPage();
            int pageSize = targetConceptionKindQueryParameters.getPageSize();
            int resultNumber = targetConceptionKindQueryParameters.getResultNumber();
            boolean isDistinctMode = targetConceptionKindQueryParameters.isDistinctMode();
            List<SortingItem> sortingItemList = targetConceptionKindQueryParameters.getSortingItems();

            SortItem[] sortItemArray = null;
            if (sortingItemList.size() > 0) {
                sortItemArray = new SortItem[sortingItemList.size()];
                for (int i = 0; i < sortingItemList.size(); i++) {
                    SortingItem currentSortingItem = sortingItemList.get(i);
                    String attributeName = currentSortingItem.getAttributeName();
                    QueryParameters.SortingLogic sortingLogic = currentSortingItem.getSortingLogic();
                    switch (sortingLogic) {
                        case ASC:
                            sortItemArray[i] = Cypher.sort(resultNodes.property(attributeName)).ascending();
                            break;
                        case DESC:
                            sortItemArray[i] = Cypher.sort(resultNodes.property(attributeName)).descending();
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

            FilteringItem defaultFilteringItem = targetConceptionKindQueryParameters.getDefaultFilteringItem();
            List<FilteringItem> andFilteringItemList = targetConceptionKindQueryParameters.getAndFilteringItemsList();
            List<FilteringItem> orFilteringItemList = targetConceptionKindQueryParameters.getOrFilteringItemsList();
            if (defaultFilteringItem == null) {
                if ((andFilteringItemList != null && andFilteringItemList.size() > 0) ||
                        (orFilteringItemList != null && orFilteringItemList.size() > 0)) {
                    logger.error("Default Filtering Item is required");
                    CoreRealmServiceEntityExploreException e = new CoreRealmServiceEntityExploreException();
                    e.setCauseMessage("Default Filtering Item is required");
                    throw e;
                }
            } else {
                if(originalConceptionEntitiesUIDList != null){
                    ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).in(Cypher.literalOf(originalConceptionEntitiesUIDList)))
                            .and(CommonOperationUtil.getQueryCondition(resultNodes, defaultFilteringItem));
                }else{
                    ongoingReadingWithWhere = ongoingReadingWithoutWhere.where(CommonOperationUtil.getQueryCondition(resultNodes, defaultFilteringItem));
                }
                if (andFilteringItemList != null && andFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : andFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.and(CommonOperationUtil.getQueryCondition(resultNodes, currentFilteringItem));
                    }
                }
                if (orFilteringItemList != null && orFilteringItemList.size() > 0) {
                    for (FilteringItem currentFilteringItem : orFilteringItemList) {
                        ongoingReadingWithWhere = ongoingReadingWithWhere.or(CommonOperationUtil.getQueryCondition(resultNodes, currentFilteringItem));
                    }
                }
            }

            if (isDistinctMode) {
                activeOngoingReadingAndReturn = ongoingReadingWithWhere != null ?
                        ongoingReadingWithWhere.returningDistinct(resultNodes):
                        ongoingReadingWithoutWhere.returningDistinct(resultNodes);
            } else {
                activeOngoingReadingAndReturn = ongoingReadingWithWhere != null ?
                        ongoingReadingWithWhere.returning(resultNodes):
                        ongoingReadingWithoutWhere.returning(resultNodes);
            }

            if (skipRecordNumber != 0) {
                if (sortItemArray != null) {
                    //Function can not use together with sort in this case
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).skip(skipRecordNumber).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.skip(skipRecordNumber).limit(limitRecordNumber).build();
                }
            } else {
                if (sortItemArray != null) {
                    //Function can not use together with sort in this case
                    statement = activeOngoingReadingAndReturn.orderBy(sortItemArray).limit(limitRecordNumber).build();
                } else {
                    statement = activeOngoingReadingAndReturn.limit(limitRecordNumber).build();
                }
            }
        }else{
            if(originalConceptionEntitiesUIDList != null){
                statement = ongoingReadingWithoutWhere.where(Functions2.id(sourceNode).in(Cypher.literalOf(originalConceptionEntitiesUIDList)))
                        .returning(resultNodes).build();
            }else{
                statement = ongoingReadingWithoutWhere.returning(resultNodes).build();
            }
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchAttributesWithNodeIDs(List<String> nodeIdList,List<String> returnProperties) throws CoreRealmServiceEntityExploreException {
        List<Long> nodeIdValueList = new ArrayList<>();
        for(int i=0;i<nodeIdList.size();i++){
            nodeIdValueList.add(Long.parseLong(nodeIdList.get(i)));
        }

        Node m = Cypher.anyNode().named(operationResultName);
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
        returnPropertyArray[returnProperties.size()] = Functions2.id(m);

        Statement statement = Cypher.match(m).where(Functions2.id(m).in(Cypher.literalOf(nodeIdValueList))).returningDistinct(returnPropertyArray).build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String matchRelationsWithUIDs(List<String> relationIdList) {
        List<Long> nodeIdValueList = new ArrayList<>();
        for(int i=0;i<relationIdList.size();i++){
            nodeIdValueList.add(Long.parseLong(relationIdList.get(i)));
        }
        Node sourceNode = Cypher.anyNode().named(sourceNodeName);
        Node targetNode = Cypher.anyNode().named(targetNodeName);
        Relationship r =  sourceNode.relationshipBetween(targetNode).named(operationResultName);;

        Statement statement = Cypher.match(r).where(Functions2.id(r).in(Cypher.literalOf(nodeIdValueList))).returningDistinct(r,sourceNode,targetNode).build();
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}", rel);
        return rel;
    }

    public static String createEntityProperties(Map<String, Object> entityProperties) {
        if (entityProperties != null && entityProperties.size() > 0) {
            Node sourceNode = Cypher.anyNode().named(sourceNodeName);
            Node targetNode = Cypher.anyNode().named(targetNodeName);
            MapExpression targetMapExpression = Cypher.mapOf(CommonOperationUtil.generatePropertiesValueArray(entityProperties));
            Relationship relation = sourceNode.relationshipTo(targetNode, "relationKind").named(operationResultName).withProperties(targetMapExpression);
            Statement statement = Cypher.create(relation).build();
            String rel = cypherRenderer.render(statement);
            // rel = CREATE (sourceNode)-[operationResult:`relationKind` XXXXXXXXXXXXXXX]->(targetNode)
            rel = rel.replace("CREATE (sourceNode)-[operationResult:`relationKind`","").replace("]->(targetNode)","");
            return rel;
        } else {
            return "{}";
        }
    }

    public static String setRelationKindProperties(String relationKind,Map<String, Object> entityProperties){
        if (relationKind != null && entityProperties != null && entityProperties.size() > 0) {
            Node sourceNode = Cypher.anyNode().named(sourceNodeName);
            Node targetNode = Cypher.anyNode().named(targetNodeName);
            Relationship targetRelationship = sourceNode.relationshipTo(targetNode, relationKind).named(operationResultName);
            Map<String,Object> attributesMap = CommonOperationUtil.reformatPropertyValues(entityProperties);
            Operation[] propertyOperationArray = new Operation[attributesMap.size()];
            Set<String> keySet = entityProperties.keySet();
            Iterator<String> keyIterator = keySet.iterator();
            int idx = 0;
            while(keyIterator.hasNext()){
                String currentKey = keyIterator.next();
                Object currentValue = attributesMap.get(currentKey);
                if(currentValue instanceof List){
                    propertyOperationArray[idx] = sourceNode.property(currentKey).to((Expression) attributesMap.get(currentKey));
                }else{
                    propertyOperationArray[idx] = sourceNode.property(currentKey).to(CommonOperationUtil.getAttributeValueExpress(currentValue));
                }
                idx ++;
            }
            Statement statement = Cypher.match(targetRelationship).set(propertyOperationArray)
                    .returning(Functions2.count(targetRelationship))
                    .build();
            String rel = cypherRenderer.render(statement);
            rel = rel.replaceAll("sourceNode\\.",operationResultName+"\\.");
            logger.debug("Generated Cypher Statement: {}", rel);
            return rel;
        }else{
            return "{}";
        }
    }

    public static String setConceptionKindProperties(String conceptionKind,Map<String, Object> entityProperties){
        if (conceptionKind != null && entityProperties != null && entityProperties.size() > 0) {
            Node sourceNode = Cypher.node(conceptionKind).named(operationResultName);
            Map<String,Object> attributesMap = CommonOperationUtil.reformatPropertyValues(entityProperties);
            Operation[] propertyOperationArray = new Operation[attributesMap.size()];
            Set<String> keySet = entityProperties.keySet();
            Iterator<String> keyIterator = keySet.iterator();
            int idx = 0;
            while(keyIterator.hasNext()){
                String currentKey = keyIterator.next();
                Object currentValue = attributesMap.get(currentKey);
                if(currentValue instanceof List){
                    propertyOperationArray[idx] = sourceNode.property(currentKey).to((Expression) attributesMap.get(currentKey));
                }else{
                    propertyOperationArray[idx] = sourceNode.property(currentKey).to(CommonOperationUtil.getAttributeValueExpress(currentValue));
                }
                idx ++;
            }
            Statement statement = Cypher.match(sourceNode).set(propertyOperationArray)
                    .returning(Cypher.count(sourceNode))
                    .build();
            String rel = cypherRenderer.render(statement);
            logger.debug("Generated Cypher Statement: {}", rel);
            return rel;
        }else{
            return "{}";
        }
    }
}
