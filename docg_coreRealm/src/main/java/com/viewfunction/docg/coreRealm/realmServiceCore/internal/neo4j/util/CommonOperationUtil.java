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

                    System.out.println(attributeValueObject.getClass());
                    System.out.println(key+" - "+attributeValueObject);

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
                    System.out.println(currentAttributeDataType);

                    AttributeValue currentAttributeValue = new AttributeValue();
                    currentAttributeValue.setAttributeName(key.toString());

                    //转换 date 类型.....
                    currentAttributeValue.setAttributeValue(attributeValueObject);





                    currentAttributeValue.setAttributeDataType(currentAttributeDataType);
                    attributeValueList.add(currentAttributeValue);
                }
            }
        }
        return attributeValueList;
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
}
