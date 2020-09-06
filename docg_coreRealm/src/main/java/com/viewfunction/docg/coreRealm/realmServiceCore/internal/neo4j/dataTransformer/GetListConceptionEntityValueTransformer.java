package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

public class GetListConceptionEntityValueTransformer implements DataTransformer<List<ConceptionEntityValue>>{

    private GraphOperationExecutor workingGraphOperationExecutor;

    private List<AttributeKind> containsAttributeKindList;
    private List<String> returnedAttributeList;
    private Map<String,AttributeDataType> attributeDataTypeMap;

    public GetListConceptionEntityValueTransformer(List<String> returnedAttributeList,List<AttributeKind> containsAttributeKindList,GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;

        this.containsAttributeKindList = containsAttributeKindList;
        this.returnedAttributeList = returnedAttributeList;

        this.attributeDataTypeMap = new HashMap<>();
        for(AttributeKind currentAttributeKind:this.containsAttributeKindList){
            String attributeName = currentAttributeKind.getAttributeKindName();
            AttributeDataType attributeDataType = currentAttributeKind.getAttributeDataType();
            this.attributeDataTypeMap.put(attributeName,attributeDataType);
        }
    }

    @Override
    public List<ConceptionEntityValue> transformResult(Result result) {
        List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            Map<String,Object> valueMap = nodeRecord.asMap();

            String idKey = "id("+CypherBuilder.operationResultName+")";
            Long uidValue = (Long)valueMap.get(idKey);
            String conceptionEntityUID = ""+uidValue.longValue();

            Map<String,Object> entityAttributesValue = new HashMap<>();

            ConceptionEntityValue currentConceptionEntityValue = new ConceptionEntityValue(conceptionEntityUID,entityAttributesValue);
            conceptionEntityValueList.add(currentConceptionEntityValue);

            for(String currentAttributeName:returnedAttributeList){
                String entityAttributeName = CypherBuilder.operationResultName+"."+currentAttributeName;
                Object objectValue = valueMap.get(entityAttributeName);
                Object resultAttributeValue = getFormattedValue(currentAttributeName,objectValue);

                if(resultAttributeValue != null){
                    entityAttributesValue.put(currentAttributeName,resultAttributeValue);
                }
            }
        }
        return conceptionEntityValueList;
    }

    private Object getFormattedValue(String attributeName,Object attributeValue){
        if(attributeValue != null){
            AttributeDataType targetAttributeDataType = this.attributeDataTypeMap.get(attributeName);
            if(targetAttributeDataType != null){
                if(attributeValue instanceof Long){
                    switch(targetAttributeDataType){
                        case INT:
                            return ((Long)attributeValue).intValue();
                        case BYTE:
                            return ((Long)attributeValue).byteValue();
                        case LONG:
                            return ((Long)attributeValue).longValue();
                        case SHORT:
                            return ((Long)attributeValue).shortValue();
                    }
                }else if(attributeValue instanceof Double){
                    switch(targetAttributeDataType){
                        case FLOAT:
                            return ((Double)attributeValue).floatValue();
                        case DOUBLE:
                            return ((Double)attributeValue).doubleValue();
                        case DECIMAL:
                            return new BigDecimal((Double)attributeValue);
                    }
                }else if(attributeValue instanceof Boolean){
                    switch(targetAttributeDataType){
                        case BOOLEAN:
                            return ((Boolean)attributeValue).booleanValue();
                    }
                }else if(attributeValue instanceof ZonedDateTime){
                    switch(targetAttributeDataType){
                        case DATE:
                            ZonedDateTime targetZonedDateTime = (ZonedDateTime) attributeValue;
                            Date currentDate = Date.from(targetZonedDateTime.toInstant());
                            return currentDate;
                    }
                }else if(attributeValue instanceof String){
                    switch(targetAttributeDataType){
                        case STRING:
                            return attributeValue.toString();
                    }
                }else if(attributeValue instanceof List && ((List<?>) attributeValue).size()>0){
                    Object firstAttributeValue = ((List<?>) attributeValue).get(0);
                    if(firstAttributeValue instanceof Long){
                        List<Long> longValueList = (List<Long>)attributeValue;
                        switch(targetAttributeDataType){
                            case INT_ARRAY:
                                Integer[] returnIntValueArray = new Integer[longValueList.size()];
                                for(int i=0;i<longValueList.size();i++){
                                    returnIntValueArray[i] = longValueList.get(i).intValue();
                                }
                                return returnIntValueArray;
                            case LONG_ARRAY:
                                Long[] returnLongValueArray = new Long[longValueList.size()];
                                for(int i=0;i<longValueList.size();i++){
                                    returnLongValueArray[i] = longValueList.get(i).longValue();
                                }
                                return returnLongValueArray;
                            case SHORT_ARRAY:
                                Short[] returnShortValueArray = new Short[longValueList.size()];
                                for(int i=0;i<longValueList.size();i++){
                                    returnShortValueArray[i] = longValueList.get(i).shortValue();
                                }
                                return returnShortValueArray;
                            case BINARY_ARRAY:
                                Byte[] returnByteValueArray = new Byte[longValueList.size()];
                                for(int i=0;i<longValueList.size();i++){
                                    returnByteValueArray[i] = longValueList.get(i).byteValue();
                                }
                                return returnByteValueArray;
                        }
                    }
                    if(firstAttributeValue instanceof Double){
                        List<Double> doubleValueList = (List<Double>)attributeValue;
                        switch(targetAttributeDataType){
                            case FLOAT_ARRAY:
                                Float[] returnFloatValueArray = new Float[doubleValueList.size()];
                                for(int i=0;i<doubleValueList.size();i++){
                                    returnFloatValueArray[i] = doubleValueList.get(i).floatValue();
                                }
                                return returnFloatValueArray;
                            case DOUBLE_ARRAY:
                                Double[] returnDoubleValueArray = new Double[doubleValueList.size()];
                                for(int i=0;i<doubleValueList.size();i++){
                                    returnDoubleValueArray[i] = doubleValueList.get(i).doubleValue();
                                }
                                return returnDoubleValueArray;
                            case DECIMAL_ARRAY:
                                BigDecimal[] returnDecimalValueArray = new BigDecimal[doubleValueList.size()];
                                for(int i=0;i<doubleValueList.size();i++){
                                    returnDecimalValueArray[i] = new BigDecimal(doubleValueList.get(i).doubleValue());
                                }
                                return returnDecimalValueArray;
                        }
                    }
                    if(firstAttributeValue instanceof Boolean){
                        switch(targetAttributeDataType){
                            case BOOLEAN_ARRAY:
                                List<Boolean> booleanValueList = (List<Boolean>)attributeValue;
                                Boolean[] returnBooleanValueArray = booleanValueList.toArray(new Boolean[booleanValueList.size()]);
                                return returnBooleanValueArray;
                        }
                    }
                    if(firstAttributeValue instanceof ZonedDateTime){
                        switch(targetAttributeDataType){
                            case DATE_ARRAY:
                                List<ZonedDateTime> valueList = (List<ZonedDateTime>)attributeValue;
                                Date[] returnDateValueArray = new Date[valueList.size()];
                                for(int i=0;i<valueList.size();i++){
                                    returnDateValueArray[i] = Date.from(valueList.get(i).toInstant());
                                }
                                return returnDateValueArray;
                        }
                    }
                    if(firstAttributeValue instanceof String){
                        switch(targetAttributeDataType){
                            case STRING_ARRAY:
                                List<String> stringValueList = (List<String>)attributeValue;
                                String[] returnStringValueArray = stringValueList.toArray(new String[stringValueList.size()]);
                                return returnStringValueArray;
                        }
                    }
                }else{}
            }else{
                return attributeValue;
            }
        }
        return null;
    }
}
