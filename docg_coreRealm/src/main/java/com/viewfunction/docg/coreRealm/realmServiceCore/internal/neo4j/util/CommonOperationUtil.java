package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Literal;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

public class CommonOperationUtil {

    private static final String COMMON_DATA_ORIGIN = PropertiesHandler.getPropertyValue(PropertiesHandler.COMMON_DATA_ORIGIN);

    public static void generateEntityMetaAttributes(Map<String,Object> propertiesMap){
        if(propertiesMap != null) {
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            propertiesMap.put(RealmConstant._createDateProperty, currentDateTime);
            propertiesMap.put(RealmConstant._lastModifyDateProperty, currentDateTime);
            propertiesMap.put(RealmConstant._dataOriginProperty, COMMON_DATA_ORIGIN);
        }
    }

    public static List<String> clearSystemBuiltinAttributeNames(List<String> attributeNameList){
        List<String> cleanAttributeNameList = new ArrayList<>();
        if(attributeNameList != null){
            for(String currentName:attributeNameList){
                if(!currentName.equals(RealmConstant._createDateProperty)&&
                        !currentName.equals(RealmConstant._lastModifyDateProperty)&&
                        !currentName.equals(RealmConstant._dataOriginProperty)){
                    cleanAttributeNameList.add(currentName);
                }
            }
        }
        return cleanAttributeNameList;
    }

    public static Map<String,Object> reformatPropertyValues(Map<String,Object> originalPropertiesMap){
        if(originalPropertiesMap != null){
            Map<String,Object> formattedValueMap = new HashMap<>();

            for(String key : originalPropertiesMap.keySet()){
                Object propertyValue = originalPropertiesMap.get(key);
                if(propertyValue instanceof Boolean[]||
                        propertyValue instanceof Integer[]||
                        propertyValue instanceof Short[]||
                        propertyValue instanceof Long[]||
                        propertyValue instanceof Float[]||
                        propertyValue instanceof Double[]||
                        propertyValue instanceof BigDecimal[]||
                        propertyValue instanceof String[]||
                        propertyValue instanceof byte[]){
                    List<Literal> literalList = new ArrayList<>();

                    if(propertyValue instanceof Boolean[]){
                        Boolean[] orgValue = (Boolean[])propertyValue;
                        for(Boolean currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if(propertyValue instanceof Integer[]){
                        Integer[] orgValue = (Integer[])propertyValue;
                        for(Integer currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if(propertyValue instanceof Short[]){
                        Short[] orgValue = (Short[])propertyValue;
                        for(Short currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if(propertyValue instanceof Long[]){
                        Long[] orgValue = (Long[])propertyValue;
                        for(Long currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if(propertyValue instanceof Float[]){
                        Float[] orgValue = (Float[])propertyValue;
                        for(Float currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if(propertyValue instanceof Double[]){
                        Double[] orgValue = (Double[])propertyValue;
                        for(Double currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if(propertyValue instanceof BigDecimal[]){
                        BigDecimal[] orgValue = (BigDecimal[])propertyValue;
                        for(BigDecimal currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if(propertyValue instanceof String[]){
                        String[] orgValue = (String[])propertyValue;
                        for(String currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if(propertyValue instanceof byte[]){
                        byte[] orgValue = (byte[])propertyValue;
                        for(byte currentValue:orgValue){
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    formattedValueMap.put(key,literalList);
                }else{
                    formattedValueMap.put(key,propertyValue);
                }
            }
            return formattedValueMap;
        }
        return null;
    }

    public static List<AttributeValue> getAttributeValueList(Map attributesValueMap){
        List<AttributeValue> attributeValueList = new ArrayList<>();
        if(attributesValueMap != null){
            for(Object key : attributesValueMap.keySet()){
                if(!key.equals(RealmConstant._createDateProperty)&&
                        !key.equals(RealmConstant._lastModifyDateProperty)&&
                        !key.equals(RealmConstant._dataOriginProperty)){
                    Object attributeValueObject = attributesValueMap.get(key);
                    AttributeValue currentAttributeValue = getAttributeValue(key.toString(),attributeValueObject);
                    attributeValueList.add(currentAttributeValue);
                }
            }
        }
        return attributeValueList;
    }

    public static AttributeValue getAttributeValue(String attributeName,Object attributeValueObject){
        AttributeDataType currentAttributeDataType = null;
        if(attributeValueObject instanceof List && ((List<?>) attributeValueObject).size()>0){
            Object firstAttributeValue = ((List<?>) attributeValueObject).get(0);
            AttributeDataType firstValueType = checkAttributeDataType(firstAttributeValue);
            switch(firstValueType){
                case BOOLEAN: currentAttributeDataType = AttributeDataType.BOOLEAN_ARRAY;break;
                case INT: currentAttributeDataType = AttributeDataType.INT_ARRAY;break;
                case SHORT: currentAttributeDataType = AttributeDataType.SHORT_ARRAY;break;
                case LONG: currentAttributeDataType = AttributeDataType.LONG_ARRAY;break;
                case FLOAT: currentAttributeDataType = AttributeDataType.FLOAT_ARRAY;break;
                case DOUBLE: currentAttributeDataType = AttributeDataType.DOUBLE_ARRAY;break;
                case DECIMAL: currentAttributeDataType = AttributeDataType.DECIMAL_ARRAY;break;
                case STRING: currentAttributeDataType = AttributeDataType.STRING_ARRAY;break;
                case BINARY: currentAttributeDataType = AttributeDataType.BINARY_ARRAY;break;
                case DATE: currentAttributeDataType = AttributeDataType.DATE_ARRAY;break;
            }
        }else{
            currentAttributeDataType = checkAttributeDataType(attributeValueObject);
        }

        AttributeValue currentAttributeValue = new AttributeValue();
        currentAttributeValue.setAttributeName(attributeName);

        boolean needSetAttributeValue = true;
        switch(currentAttributeDataType){
            case DATE:
                ZonedDateTime currentZonedDateTime = (ZonedDateTime)attributeValueObject;
                Date currentDate = Date.from(currentZonedDateTime.toInstant());
                currentAttributeValue.setAttributeValue(currentDate);
                needSetAttributeValue = false;
                break;
            case DATE_ARRAY:
                List<ZonedDateTime> valueList = (List<ZonedDateTime>)attributeValueObject;
                Date[] returnDateValueArray = new Date[valueList.size()];
                for(int i=0;i<valueList.size();i++){
                    returnDateValueArray[i] = Date.from(valueList.get(i).toInstant());
                }
                currentAttributeValue.setAttributeValue(returnDateValueArray);
                needSetAttributeValue = false;
                break;
            case INT_ARRAY:
                List<Integer> intValueList = (List<Integer>)attributeValueObject;
                Integer[] returnIntValueArray = intValueList.toArray(new Integer[intValueList.size()]);
                currentAttributeValue.setAttributeValue(returnIntValueArray);
                needSetAttributeValue = false;
                break;
            case LONG_ARRAY:
                List<Long> longValueList = (List<Long>)attributeValueObject;
                Long[] returnLongValueArray = longValueList.toArray(new Long[longValueList.size()]);
                currentAttributeValue.setAttributeValue(returnLongValueArray);
                needSetAttributeValue = false;
                break;
            case FLOAT_ARRAY:
                List<Float> floatValueList = (List<Float>)attributeValueObject;
                Float[] returnFloatValueArray = floatValueList.toArray(new Float[floatValueList.size()]);
                currentAttributeValue.setAttributeValue(returnFloatValueArray);
                needSetAttributeValue = false;
                break;
            case SHORT_ARRAY:
                List<Short> shortValueList = (List<Short>)attributeValueObject;
                Short[] returnShortValueArray = shortValueList.toArray(new Short[shortValueList.size()]);
                currentAttributeValue.setAttributeValue(returnShortValueArray);
                needSetAttributeValue = false;
                break;
            case BINARY_ARRAY:
                List<Byte> byteValueList = (List<Byte>)attributeValueObject;
                Byte[] returnByteValueArray = byteValueList.toArray(new Byte[byteValueList.size()]);
                currentAttributeValue.setAttributeValue(returnByteValueArray);
                needSetAttributeValue = false;
                break;
            case DOUBLE_ARRAY:
                List<Double> doubleValueList = (List<Double>)attributeValueObject;
                Double[] returnDoubleValueArray = doubleValueList.toArray(new Double[doubleValueList.size()]);
                currentAttributeValue.setAttributeValue(returnDoubleValueArray);
                needSetAttributeValue = false;
                break;
            case STRING_ARRAY:
                List<String> stringValueList = (List<String>)attributeValueObject;
                String[] returnStringValueArray = stringValueList.toArray(new String[stringValueList.size()]);
                currentAttributeValue.setAttributeValue(returnStringValueArray);
                needSetAttributeValue = false;
                break;
            case BOOLEAN_ARRAY:
                List<Boolean> booleanValueList = (List<Boolean>)attributeValueObject;
                Boolean[] returnBooleanValueArray = booleanValueList.toArray(new Boolean[booleanValueList.size()]);
                currentAttributeValue.setAttributeValue(returnBooleanValueArray);
                needSetAttributeValue = false;
                break;
            case DECIMAL_ARRAY:
                List<BigDecimal> bigDecimalValueList = (List<BigDecimal>)attributeValueObject;
                BigDecimal[] returnBigDecimalValueArray = bigDecimalValueList.toArray(new BigDecimal[bigDecimalValueList.size()]);
                currentAttributeValue.setAttributeValue(returnBigDecimalValueArray);
                needSetAttributeValue = false;
                break;
        }
        if(needSetAttributeValue) {
            currentAttributeValue.setAttributeValue(attributeValueObject);
        }
        currentAttributeValue.setAttributeDataType(currentAttributeDataType);
        return currentAttributeValue;
    }

    public static AttributeDataType checkAttributeDataType(Object attributeValueObject){
        if(attributeValueObject instanceof Boolean){
            return AttributeDataType.BOOLEAN;
        }
        if(attributeValueObject instanceof Integer){
            return AttributeDataType.INT;
        }
        if(attributeValueObject instanceof Short){
            return AttributeDataType.SHORT;
        }
        if(attributeValueObject instanceof Long){
            return AttributeDataType.LONG;
        }
        if(attributeValueObject instanceof Float){
            return AttributeDataType.FLOAT;
        }
        if(attributeValueObject instanceof Double){
            return AttributeDataType.DOUBLE;
        }
        if(attributeValueObject instanceof BigDecimal){
            return AttributeDataType.DECIMAL;
        }
        if(attributeValueObject instanceof String){
            return AttributeDataType.STRING;
        }
        if(attributeValueObject instanceof Byte){
            return AttributeDataType.BINARY;
        }
        if(attributeValueObject instanceof ZonedDateTime){
            return AttributeDataType.DATE;
        }
        return null;
    }

    public static boolean validateValueFormat(AttributeDataType attributeDataType,Object dataValue){
        System.out.println(attributeDataType);
        System.out.println(dataValue);
        if(attributeDataType == null || dataValue == null){
            return false;
        }else{
            switch(attributeDataType){
                case BOOLEAN:
                    if(dataValue instanceof Boolean){
                        return true;
                    }
                    break;
                case INT:
                    if(dataValue instanceof Integer){
                        return true;
                    }
                    break;
                case SHORT:
                    if(dataValue instanceof Short){
                        return true;
                    }
                    break;
                case LONG:
                    if(dataValue instanceof Short||
                            dataValue instanceof Byte||
                            dataValue instanceof Integer||
                            dataValue instanceof Long){
                        return true;
                    }
                    break;
                case FLOAT:
                    if(dataValue instanceof Float){
                        return true;
                    }
                    break;
                case DOUBLE:
                    if(dataValue instanceof Float||
                            dataValue instanceof BigDecimal||
                            dataValue instanceof Double){
                        return true;
                    }
                    break;
                case DATE:
                    if(dataValue instanceof Date){
                        return true;
                    }
                    break;
                case STRING:
                    if(dataValue instanceof String){
                        return true;
                    }
                    break;
                case BINARY:
                    if(dataValue instanceof byte[]){
                        return true;
                    }
                    break;
                case BYTE:
                    if(dataValue instanceof Byte){
                        return true;
                    }
                    break;
                case DECIMAL:
                    if(dataValue instanceof BigDecimal){
                        return true;
                    }
                    break;
                case BOOLEAN_ARRAY:
                    if(dataValue instanceof Boolean[]){
                        return true;
                    }
                    break;
                case INT_ARRAY:
                    if(dataValue instanceof Integer[]){
                        return true;
                    }
                    break;
                case SHORT_ARRAY:
                    if(dataValue instanceof Short[]){
                        return true;
                    }
                    break;
                case LONG_ARRAY:
                    if(dataValue instanceof Short[]||
                            dataValue instanceof Byte[]||
                            dataValue instanceof byte[]||
                            dataValue instanceof Integer[]||
                            dataValue instanceof Long[]){
                        return true;
                    }
                    break;
                case FLOAT_ARRAY:
                    if(dataValue instanceof Float[]){
                        return true;
                    }
                    break;
                case DOUBLE_ARRAY:
                    if(dataValue instanceof Float[]||
                            dataValue instanceof BigDecimal[]||
                            dataValue instanceof Double[]){
                        return true;
                    }
                    break;
                case DATE_ARRAY:
                    if(dataValue instanceof Date[]){
                        return true;
                    }
                    break;
                case STRING_ARRAY:
                    if(dataValue instanceof String[]){
                        return true;
                    }
                    break;
                case BINARY_ARRAY:
                    if(dataValue instanceof Byte[][]){
                        return true;
                    }
                    break;
                case DECIMAL_ARRAY:
                    if(dataValue instanceof BigDecimal[]){
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
}
