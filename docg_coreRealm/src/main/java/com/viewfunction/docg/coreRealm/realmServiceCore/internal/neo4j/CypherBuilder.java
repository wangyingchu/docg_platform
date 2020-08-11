package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
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
    public enum CypherFunctionType{
        COUNT,ID,KEYS,PROPERTIES
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
            statement = Cypher.create(m).set(propertiesArray).returning(m).build();
        }else{
            m = Cypher.node(labelName).named(operationResultName);
            statement = Cypher.create(m).returning(m).build();
        }
        String rel = cypherRenderer.render(statement);
        logger.debug("Generated Cypher Statement: {}",rel);
        return rel;
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

    public static String matchNodeWithSingleFunctionValueEqual(CypherFunctionType propertyFunctionType,Object propertyValue,CypherFunctionType returnFunctionType){
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
}
