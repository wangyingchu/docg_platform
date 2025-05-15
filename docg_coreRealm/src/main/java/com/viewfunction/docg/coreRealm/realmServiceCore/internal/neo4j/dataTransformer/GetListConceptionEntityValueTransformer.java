package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

public class GetListConceptionEntityValueTransformer implements DataTransformer<List<ConceptionEntityValue>>{

    private List<AttributeKind> containsAttributeKindList;
    private List<String> returnedAttributeList;
    private Map<String,AttributeDataType> attributeDataTypeMap;
    private boolean useIDMatchLogic = true;

    public GetListConceptionEntityValueTransformer(List<String> returnedAttributeList){
        this.returnedAttributeList = returnedAttributeList;
        this.setUseIDMatchLogic(false);
        this.attributeDataTypeMap = new HashMap<>();
    }

    public GetListConceptionEntityValueTransformer(List<String> returnedAttributeList,List<AttributeKind> containsAttributeKindList){
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
            List<String> allConceptionKindNames = null;
            Record nodeRecord = result.next();
            String conceptionEntityUID;
            Map<String,Object> valueMap;
            if(this.isUseIDMatchLogic()){
                valueMap = nodeRecord.asMap();
                String idKey = "id("+CypherBuilder.operationResultName+")";
                Long uidValue = (Long)valueMap.get(idKey);
                conceptionEntityUID = ""+uidValue.longValue();
            }else{
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                allConceptionKindNames = Lists.newArrayList(nodeRecord.get(CypherBuilder.operationResultName).asNode().labels());
                long nodeUID = resultNode.id();
                conceptionEntityUID = ""+nodeUID;
                valueMap = resultNode.asMap();
            }

            Map<String,Object> entityAttributesValue = new HashMap<>();
            ConceptionEntityValue currentConceptionEntityValue = new ConceptionEntityValue(conceptionEntityUID,entityAttributesValue);
            currentConceptionEntityValue.setAllConceptionKindNames(allConceptionKindNames);
            conceptionEntityValueList.add(currentConceptionEntityValue);
            if(this.isUseIDMatchLogic()){
                if(returnedAttributeList != null){
                    for(String currentAttributeName:returnedAttributeList){
                        String entityAttributeName = CypherBuilder.operationResultName+"."+currentAttributeName;
                        Object objectValue = valueMap.get(entityAttributeName);
                        if(objectValue == null){
                            //if attribute contains space for example : attribute a, will returned in `` such as .`attribute a`
                            entityAttributeName = CypherBuilder.operationResultName+".`"+currentAttributeName+"`";
                            objectValue = valueMap.get(entityAttributeName);
                        }
                        Object resultAttributeValue = getFormattedValue(currentAttributeName,objectValue);
                        if(resultAttributeValue != null){
                            entityAttributesValue.put(currentAttributeName,resultAttributeValue);
                        }
                    }
                }
            }else{
                if(returnedAttributeList!= null){
                    for(String currentAttributeName:returnedAttributeList){
                        Object objectValue = valueMap.get(currentAttributeName);
                        Object resultAttributeValue = getFormattedValue(currentAttributeName,objectValue);
                        if(resultAttributeValue != null){
                            entityAttributesValue.put(currentAttributeName,resultAttributeValue);
                        }
                    }
                }else{
                    for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if(validateValueFormat(value)){
                            entityAttributesValue.put(key,value);
                        }
                    }
                }
            }
        }
        return conceptionEntityValueList;
    }

    private Object getFormattedValue(String attributeName,Object attributeValue){
        if(attributeValue != null) {
            AttributeDataType targetAttributeDataType = this.attributeDataTypeMap.get(attributeName);
            if(attributeValue instanceof Boolean || attributeValue instanceof String) {
                return attributeValue;
            }else if (attributeValue instanceof ZonedDateTime) {
                ZonedDateTime targetZonedDateTime = (ZonedDateTime) attributeValue;
                Date currentDate = Date.from(targetZonedDateTime.toInstant());
                return currentDate;
            }else if (attributeValue instanceof Long) {
                if(targetAttributeDataType != null) {
                    switch (targetAttributeDataType) {
                        case INT:
                            return ((Long) attributeValue).intValue();
                        case BYTE:
                            return ((Long) attributeValue).byteValue();
                        case LONG:
                            return ((Long) attributeValue).longValue();
                        case SHORT:
                            return ((Long) attributeValue).shortValue();
                    }
                }else {
                    return attributeValue;
                }
            }else if (attributeValue instanceof Double) {
                if(targetAttributeDataType != null) {
                    switch (targetAttributeDataType) {
                        case FLOAT:
                            return ((Double) attributeValue).floatValue();
                        case DOUBLE:
                            return ((Double) attributeValue).doubleValue();
                        case DECIMAL:
                            return new BigDecimal((Double) attributeValue);
                    }
                }else {
                    return attributeValue;
                }
            }else if (attributeValue instanceof List && ((List<?>) attributeValue).size() > 0) {
                Object firstAttributeValue = ((List<?>) attributeValue).get(0);
                if (firstAttributeValue instanceof Boolean) {
                    List<Boolean> booleanValueList = (List<Boolean>) attributeValue;
                    Boolean[] returnBooleanValueArray = booleanValueList.toArray(new Boolean[booleanValueList.size()]);
                    return returnBooleanValueArray;
                }else if (firstAttributeValue instanceof ZonedDateTime) {
                    List<ZonedDateTime> valueList = (List<ZonedDateTime>) attributeValue;
                    Date[] returnDateValueArray = new Date[valueList.size()];
                    for (int i = 0; i < valueList.size(); i++) {
                        returnDateValueArray[i] = Date.from(valueList.get(i).toInstant());
                    }
                    return returnDateValueArray;
                }else if (firstAttributeValue instanceof String) {
                    List<String> stringValueList = (List<String>) attributeValue;
                    String[] returnStringValueArray = stringValueList.toArray(new String[stringValueList.size()]);
                    return returnStringValueArray;
                }else if (firstAttributeValue instanceof Long) {
                    List<Long> longValueList = (List<Long>) attributeValue;
                    if (targetAttributeDataType != null) {
                        switch (targetAttributeDataType) {
                            case INT_ARRAY:
                                Integer[] returnIntValueArray = new Integer[longValueList.size()];
                                for (int i = 0; i < longValueList.size(); i++) {
                                    returnIntValueArray[i] = longValueList.get(i).intValue();
                                }
                                return returnIntValueArray;
                            case LONG_ARRAY:
                                Long[] returnLongValueArray = new Long[longValueList.size()];
                                for (int i = 0; i < longValueList.size(); i++) {
                                    returnLongValueArray[i] = longValueList.get(i).longValue();
                                }
                                return returnLongValueArray;
                            case SHORT_ARRAY:
                                Short[] returnShortValueArray = new Short[longValueList.size()];
                                for (int i = 0; i < longValueList.size(); i++) {
                                    returnShortValueArray[i] = longValueList.get(i).shortValue();
                                }
                                return returnShortValueArray;
                            case BYTE_ARRAY:
                                Byte[] returnByteValueArray = new Byte[longValueList.size()];
                                for (int i = 0; i < longValueList.size(); i++) {
                                    returnByteValueArray[i] = longValueList.get(i).byteValue();
                                }
                                return returnByteValueArray;
                            case BINARY:
                                byte[] returnBinaryValueArray = new byte[longValueList.size()];
                                for (int i = 0; i < longValueList.size(); i++) {
                                    returnBinaryValueArray[i] = longValueList.get(i).byteValue();
                                }
                                return returnBinaryValueArray;
                        }
                    }else {
                        Long[] returnLongValueArray = new Long[longValueList.size()];
                        for (int i = 0; i < longValueList.size(); i++) {
                            returnLongValueArray[i] = longValueList.get(i).longValue();
                        }
                        return returnLongValueArray;
                    }
                }else if (firstAttributeValue instanceof Double) {
                    List<Double> doubleValueList = (List<Double>) attributeValue;
                    if (targetAttributeDataType != null) {
                        switch (targetAttributeDataType) {
                            case FLOAT_ARRAY:
                                Float[] returnFloatValueArray = new Float[doubleValueList.size()];
                                for (int i = 0; i < doubleValueList.size(); i++) {
                                    returnFloatValueArray[i] = doubleValueList.get(i).floatValue();
                                }
                                return returnFloatValueArray;
                            case DOUBLE_ARRAY:
                                Double[] returnDoubleValueArray = new Double[doubleValueList.size()];
                                for (int i = 0; i < doubleValueList.size(); i++) {
                                    returnDoubleValueArray[i] = doubleValueList.get(i).doubleValue();
                                }
                                return returnDoubleValueArray;
                            case DECIMAL_ARRAY:
                                BigDecimal[] returnDecimalValueArray = new BigDecimal[doubleValueList.size()];
                                for (int i = 0; i < doubleValueList.size(); i++) {
                                    returnDecimalValueArray[i] = new BigDecimal(doubleValueList.get(i).doubleValue());
                                }
                                return returnDecimalValueArray;
                        }
                    }else {
                        Double[] returnDoubleValueArray = new Double[doubleValueList.size()];
                        for (int i = 0; i < doubleValueList.size(); i++) {
                            returnDoubleValueArray[i] = doubleValueList.get(i).doubleValue();
                        }
                        return returnDoubleValueArray;
                    }
                }
            }else {
                return attributeValue;
            }
        }
        return null;
    }

    private boolean validateValueFormat(Object attributeValueObject){
        if (attributeValueObject instanceof Boolean) {
            return true;
        }
        if (attributeValueObject instanceof Integer) {
            return true;
        }
        if (attributeValueObject instanceof Short) {
            return true;
        }
        if (attributeValueObject instanceof Long) {
            return true;
        }
        if (attributeValueObject instanceof Float) {
            return true;
        }
        if (attributeValueObject instanceof Double) {
            return true;
        }
        if (attributeValueObject instanceof BigDecimal) {
            return true;
        }
        if (attributeValueObject instanceof String) {
            return true;
        }
        if (attributeValueObject instanceof Byte) {
            return true;
        }
        if (attributeValueObject instanceof ZonedDateTime) {
            return true;
        }
        if (attributeValueObject instanceof LocalDateTime) {
            return true;
        }
        if (attributeValueObject instanceof LocalDate) {
            return true;
        }
        if (attributeValueObject instanceof LocalTime) {
            return true;
        }
        return false;
    }

    public boolean isUseIDMatchLogic() {
        return useIDMatchLogic;
    }

    public void setUseIDMatchLogic(boolean useIDMatchLogic) {
        this.useIDMatchLogic = useIDMatchLogic;
    }
}
