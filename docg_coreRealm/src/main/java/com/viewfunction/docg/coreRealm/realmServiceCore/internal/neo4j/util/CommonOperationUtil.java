package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListRelationAttachKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationAttachKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import org.neo4j.cypherdsl.core.*;
import org.neo4j.driver.Result;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Set;
import java.util.*;

public class CommonOperationUtil {

    private static final String COMMON_DATA_ORIGIN = PropertiesHandler.getPropertyValue(PropertiesHandler.COMMON_DATA_ORIGIN);
    private static final ZoneId systemDefaultZoneId = ZoneId.systemDefault();
    private static final String CORE_REALM_STORAGE_IMPL_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.CORE_REALM_STORAGE_IMPL_TECH);
    private static DateTimeFormatter zonedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void generateEntityMetaAttributes(Map<String, Object> propertiesMap) {
        if (propertiesMap != null) {
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            propertiesMap.put(RealmConstant._createDateProperty, currentDateTime);
            propertiesMap.put(RealmConstant._lastModifyDateProperty, currentDateTime);
            propertiesMap.put(RealmConstant._dataOriginProperty, COMMON_DATA_ORIGIN);
        }
    }

    public static void generateEntityMetaAttributes(Map<String, Object> propertiesMap, ZonedDateTime currentDateTime) {
        if (propertiesMap != null) {
            propertiesMap.put(RealmConstant._createDateProperty, currentDateTime);
            propertiesMap.put(RealmConstant._lastModifyDateProperty, currentDateTime);
            propertiesMap.put(RealmConstant._dataOriginProperty, COMMON_DATA_ORIGIN);
        }
    }

    public static List<String> clearSystemBuiltinAttributeNames(List<String> attributeNameList) {
        List<String> cleanAttributeNameList = new ArrayList<>();
        if (attributeNameList != null) {
            for (String currentName : attributeNameList) {
                if (!currentName.equals(RealmConstant._createDateProperty) &&
                        !currentName.equals(RealmConstant._lastModifyDateProperty) &&
                        !currentName.equals(RealmConstant._dataOriginProperty)) {
                    cleanAttributeNameList.add(currentName);
                }
            }
        }
        return cleanAttributeNameList;
    }

    public static Map<String, Object> reformatPropertyValues(Map<String, Object> originalPropertiesMap) {
        if (originalPropertiesMap != null) {
            Map<String, Object> formattedValueMap = new HashMap<>();

            for (String key : originalPropertiesMap.keySet()) {
                Object propertyValue = originalPropertiesMap.get(key);
                if (propertyValue instanceof Boolean[] ||
                        propertyValue instanceof Integer[] ||
                        propertyValue instanceof Short[] ||
                        propertyValue instanceof Long[] ||
                        propertyValue instanceof Float[] ||
                        propertyValue instanceof Double[] ||
                        propertyValue instanceof BigDecimal[] ||
                        propertyValue instanceof String[] ||
                        propertyValue instanceof byte[] ||
                        propertyValue instanceof Byte[] ||
                        propertyValue instanceof LocalDate[] ||
                        propertyValue instanceof LocalTime[] ||
                        propertyValue instanceof LocalDateTime[]) {
                    List<Literal> literalList = new ArrayList<>();

                    if (propertyValue instanceof Boolean[]) {
                        Boolean[] orgValue = (Boolean[]) propertyValue;
                        for (Boolean currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof Integer[]) {
                        Integer[] orgValue = (Integer[]) propertyValue;
                        for (Integer currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof Short[]) {
                        Short[] orgValue = (Short[]) propertyValue;
                        for (Short currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof Long[]) {
                        Long[] orgValue = (Long[]) propertyValue;
                        for (Long currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof Float[]) {
                        Float[] orgValue = (Float[]) propertyValue;
                        for (Float currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof Double[]) {
                        Double[] orgValue = (Double[]) propertyValue;
                        for (Double currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof BigDecimal[]) {
                        BigDecimal[] orgValue = (BigDecimal[]) propertyValue;
                        for (BigDecimal currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof String[]) {
                        String[] orgValue = (String[]) propertyValue;
                        for (String currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof byte[]) {
                        byte[] orgValue = (byte[]) propertyValue;
                        for (byte currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof Byte[]) {
                        Byte[] orgValue = (Byte[]) propertyValue;
                        for (Byte currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof LocalDate[]) {
                        LocalDate[] orgValue = (LocalDate[]) propertyValue;
                        for (LocalDate currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof LocalTime[]) {
                        LocalTime[] orgValue = (LocalTime[]) propertyValue;
                        for (LocalTime currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    if (propertyValue instanceof LocalDateTime[]) {
                        LocalDateTime[] orgValue = (LocalDateTime[]) propertyValue;
                        for (LocalDateTime currentValue : orgValue) {
                            literalList.add(Cypher.literalOf(currentValue));
                        }
                    }
                    formattedValueMap.put(key, literalList);
                } else {
                    formattedValueMap.put(key, propertyValue);
                }
            }
            return formattedValueMap;
        }
        return null;
    }

    public static Object[] generatePropertiesValueArray(Map<String, Object> originalPropertiesMap) {
        if (originalPropertiesMap != null) {
            List<Object> propertiesValueList = new ArrayList<>();

            for (String key : originalPropertiesMap.keySet()) {
                propertiesValueList.add(key);
                Object propertyValue = originalPropertiesMap.get(key);
                if (propertyValue instanceof Boolean[] ||
                        propertyValue instanceof Integer[] ||
                        propertyValue instanceof Short[] ||
                        propertyValue instanceof Long[] ||
                        propertyValue instanceof Float[] ||
                        propertyValue instanceof Double[] ||
                        propertyValue instanceof BigDecimal[] ||
                        propertyValue instanceof String[] ||
                        propertyValue instanceof Byte[] ||
                        propertyValue instanceof LocalDate[] ||
                        propertyValue instanceof LocalTime[] ||
                        propertyValue instanceof LocalDateTime[]) {
                    Object[] dataValueArray = (Object[]) propertyValue;
                    Literal[] orgLiteralValue = new Literal[dataValueArray.length];
                    for (int i = 0; i < dataValueArray.length; i++) {
                        orgLiteralValue[i] = Cypher.literalOf(dataValueArray[i]);
                    }
                    propertiesValueList.add(Cypher.listOf(orgLiteralValue));
                } else if (propertyValue instanceof byte[]) {
                    byte[] orgValue = (byte[]) propertyValue;
                    Literal[] orgLiteralValue = new Literal[orgValue.length];
                    for (int i = 0; i < orgValue.length; i++) {
                        orgLiteralValue[i] = Cypher.literalOf(orgValue[i]);
                    }
                    propertiesValueList.add(Cypher.listOf(orgLiteralValue));
                } else if (propertyValue instanceof Date[]) {
                    Date[] dateValueArray = (Date[]) propertyValue;
                    Literal[] orgLiteralValue = new Literal[dateValueArray.length];
                    for (int j = 0; j < dateValueArray.length; j++) {
                        Date currentInnerValue = dateValueArray[j];
                        ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(currentInnerValue.toInstant(), systemDefaultZoneId);
                        String targetZonedDateTimeString = targetZonedDateTime.toString();
                        orgLiteralValue[j] = new CustomContentLiteral("datetime('" + targetZonedDateTimeString + "')");
                    }
                    propertiesValueList.add(Cypher.listOf(orgLiteralValue));
                } else if (propertyValue instanceof ZonedDateTime) {
                    if (CoreRealmStorageImplTech.MEMGRAPH.toString().equals(CORE_REALM_STORAGE_IMPL_TECH)) {
                        if (zonedDateTimeFormatter == null) {
                            zonedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        }
                        ZonedDateTime targetZonedDateTime = (ZonedDateTime) propertyValue;
                        String targetZonedDateTimeString = targetZonedDateTime.format(zonedDateTimeFormatter);
                        propertiesValueList.add( Functions2.localDateTime(Cypher.literalOf(targetZonedDateTimeString)));
                    }else{
                        ZonedDateTime targetZonedDateTime = (ZonedDateTime) propertyValue;
                        String targetZonedDateTimeString = targetZonedDateTime.toString();
                        propertiesValueList.add(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)));
                    }
                } else if (propertyValue instanceof Date) {
                    ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date) propertyValue).toInstant(), systemDefaultZoneId);
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    propertiesValueList.add(Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString)));
                } else {
                    propertiesValueList.add(Cypher.literalOf(propertyValue));
                }
            }
            Object[] resultDataArray = new Object[propertiesValueList.size()];
            return propertiesValueList.toArray(resultDataArray);
        }
        return null;
    }

    public static List<AttributeValue> getAttributeValueList(Map attributesValueMap) {
        List<AttributeValue> attributeValueList = new ArrayList<>();
        if (attributesValueMap != null) {
            for (Object key : attributesValueMap.keySet()) {
                if (!key.equals(RealmConstant._createDateProperty) &&
                        !key.equals(RealmConstant._lastModifyDateProperty) &&
                        !key.equals(RealmConstant._dataOriginProperty)) {
                    Object attributeValueObject = attributesValueMap.get(key);
                    AttributeValue currentAttributeValue = getAttributeValue(key.toString(), attributeValueObject);
                    attributeValueList.add(currentAttributeValue);
                }
            }
        }
        return attributeValueList;
    }

    public static AttributeValue getAttributeValue(String attributeName, Object attributeValueObject) {
        AttributeDataType currentAttributeDataType = null;
        if (attributeValueObject instanceof List && ((List<?>) attributeValueObject).size() > 0) {
            Object firstAttributeValue = ((List<?>) attributeValueObject).get(0);
            AttributeDataType firstValueType = checkAttributeDataType(firstAttributeValue);
            switch (firstValueType) {
                case BOOLEAN:
                    currentAttributeDataType = AttributeDataType.BOOLEAN_ARRAY;
                    break;
                case INT:
                    currentAttributeDataType = AttributeDataType.INT_ARRAY;
                    break;
                case SHORT:
                    currentAttributeDataType = AttributeDataType.SHORT_ARRAY;
                    break;
                case LONG:
                    currentAttributeDataType = AttributeDataType.LONG_ARRAY;
                    break;
                case FLOAT:
                    currentAttributeDataType = AttributeDataType.FLOAT_ARRAY;
                    break;
                case DOUBLE:
                    currentAttributeDataType = AttributeDataType.DOUBLE_ARRAY;
                    break;
                case DECIMAL:
                    currentAttributeDataType = AttributeDataType.DECIMAL_ARRAY;
                    break;
                case STRING:
                    currentAttributeDataType = AttributeDataType.STRING_ARRAY;
                    break;
                case BYTE:
                    currentAttributeDataType = AttributeDataType.BYTE_ARRAY;
                    break;
                case TIMESTAMP:
                    currentAttributeDataType = AttributeDataType.TIMESTAMP_ARRAY;
                    break;
                case DATE:
                    currentAttributeDataType = AttributeDataType.DATE_ARRAY;
                    break;
                case TIME:
                    currentAttributeDataType = AttributeDataType.TIME_ARRAY;
                    break;
                case DATETIME:
                    currentAttributeDataType = AttributeDataType.DATETIME_ARRAY;
                    break;
            }
        } else {
            currentAttributeDataType = checkAttributeDataType(attributeValueObject);
        }

        AttributeValue currentAttributeValue = new AttributeValue();
        currentAttributeValue.setAttributeName(attributeName);

        boolean needSetAttributeValue = true;
        switch (currentAttributeDataType) {
            case TIMESTAMP:
                ZonedDateTime currentZonedDateTime = (ZonedDateTime) attributeValueObject;
                Date currentDate = Date.from(currentZonedDateTime.toInstant());
                currentAttributeValue.setAttributeValue(currentDate);
                needSetAttributeValue = false;
                break;
            case TIMESTAMP_ARRAY:
                List<ZonedDateTime> valueList = (List<ZonedDateTime>) attributeValueObject;
                Date[] returnDateValueArray = new Date[valueList.size()];
                for (int i = 0; i < valueList.size(); i++) {
                    returnDateValueArray[i] = Date.from(valueList.get(i).toInstant());
                }
                currentAttributeValue.setAttributeValue(returnDateValueArray);
                needSetAttributeValue = false;
                break;
            case INT_ARRAY:
                List<Integer> intValueList = (List<Integer>) attributeValueObject;
                Integer[] returnIntValueArray = intValueList.toArray(new Integer[intValueList.size()]);
                currentAttributeValue.setAttributeValue(returnIntValueArray);
                needSetAttributeValue = false;
                break;
            case LONG_ARRAY:
                List<Long> longValueList = (List<Long>) attributeValueObject;
                Long[] returnLongValueArray = longValueList.toArray(new Long[longValueList.size()]);
                currentAttributeValue.setAttributeValue(returnLongValueArray);
                needSetAttributeValue = false;
                break;
            case FLOAT_ARRAY:
                List<Float> floatValueList = (List<Float>) attributeValueObject;
                Float[] returnFloatValueArray = floatValueList.toArray(new Float[floatValueList.size()]);
                currentAttributeValue.setAttributeValue(returnFloatValueArray);
                needSetAttributeValue = false;
                break;
            case SHORT_ARRAY:
                List<Short> shortValueList = (List<Short>) attributeValueObject;
                Short[] returnShortValueArray = shortValueList.toArray(new Short[shortValueList.size()]);
                currentAttributeValue.setAttributeValue(returnShortValueArray);
                needSetAttributeValue = false;
                break;
            case BYTE_ARRAY:
                List<Byte> _ByteValueList = (List<Byte>) attributeValueObject;
                Byte[] returnByteValueArray2 = _ByteValueList.toArray(new Byte[_ByteValueList.size()]);
                currentAttributeValue.setAttributeValue(returnByteValueArray2);
                needSetAttributeValue = false;
                break;
            case DOUBLE_ARRAY:
                List<Double> doubleValueList = (List<Double>) attributeValueObject;
                Double[] returnDoubleValueArray = doubleValueList.toArray(new Double[doubleValueList.size()]);
                currentAttributeValue.setAttributeValue(returnDoubleValueArray);
                needSetAttributeValue = false;
                break;
            case STRING_ARRAY:
                List<String> stringValueList = (List<String>) attributeValueObject;
                String[] returnStringValueArray = stringValueList.toArray(new String[stringValueList.size()]);
                currentAttributeValue.setAttributeValue(returnStringValueArray);
                needSetAttributeValue = false;
                break;
            case BOOLEAN_ARRAY:
                List<Boolean> booleanValueList = (List<Boolean>) attributeValueObject;
                Boolean[] returnBooleanValueArray = booleanValueList.toArray(new Boolean[booleanValueList.size()]);
                currentAttributeValue.setAttributeValue(returnBooleanValueArray);
                needSetAttributeValue = false;
                break;
            case DECIMAL_ARRAY:
                List<BigDecimal> bigDecimalValueList = (List<BigDecimal>) attributeValueObject;
                BigDecimal[] returnBigDecimalValueArray = bigDecimalValueList.toArray(new BigDecimal[bigDecimalValueList.size()]);
                currentAttributeValue.setAttributeValue(returnBigDecimalValueArray);
                needSetAttributeValue = false;
                break;
            case DATETIME_ARRAY:
                List<LocalDateTime> dateTimeValueList = (List<LocalDateTime>) attributeValueObject;
                LocalDateTime[] returnDateTimeValueArray = dateTimeValueList.toArray(new LocalDateTime[dateTimeValueList.size()]);
                currentAttributeValue.setAttributeValue(returnDateTimeValueArray);
                needSetAttributeValue = false;
                break;
            case DATE_ARRAY:
                List<LocalDate> dateValueList = (List<LocalDate>) attributeValueObject;
                LocalDate[] returnLocalDateValueArray = dateValueList.toArray(new LocalDate[dateValueList.size()]);
                currentAttributeValue.setAttributeValue(returnLocalDateValueArray);
                needSetAttributeValue = false;
                break;
            case TIME_ARRAY:
                List<LocalTime> timeValueList = (List<LocalTime>) attributeValueObject;
                LocalTime[] returnTimeValueArray = timeValueList.toArray(new LocalTime[timeValueList.size()]);
                currentAttributeValue.setAttributeValue(returnTimeValueArray);
                needSetAttributeValue = false;
                break;
        }
        if (needSetAttributeValue) {
            currentAttributeValue.setAttributeValue(attributeValueObject);
        }
        currentAttributeValue.setAttributeDataType(currentAttributeDataType);
        return currentAttributeValue;
    }

    public static AttributeDataType checkAttributeDataType(Object attributeValueObject) {
        if (attributeValueObject instanceof Boolean) {
            return AttributeDataType.BOOLEAN;
        }
        if (attributeValueObject instanceof Integer) {
            return AttributeDataType.INT;
        }
        if (attributeValueObject instanceof Short) {
            return AttributeDataType.SHORT;
        }
        if (attributeValueObject instanceof Long) {
            return AttributeDataType.LONG;
        }
        if (attributeValueObject instanceof Float) {
            return AttributeDataType.FLOAT;
        }
        if (attributeValueObject instanceof Double) {
            return AttributeDataType.DOUBLE;
        }
        if (attributeValueObject instanceof BigDecimal) {
            return AttributeDataType.DECIMAL;
        }
        if (attributeValueObject instanceof String) {
            return AttributeDataType.STRING;
        }
        if (attributeValueObject instanceof Byte) {
            return AttributeDataType.BINARY;
        }
        if (attributeValueObject instanceof ZonedDateTime) {
            return AttributeDataType.TIMESTAMP;
        }
        if (attributeValueObject instanceof LocalDateTime) {
            return AttributeDataType.DATETIME;
        }
        if (attributeValueObject instanceof LocalDate) {
            return AttributeDataType.DATE;
        }
        if (attributeValueObject instanceof LocalTime) {
            return AttributeDataType.TIME;
        }
        return null;
    }

    public static boolean validateValueFormat(AttributeDataType attributeDataType, Object dataValue) {
        if (attributeDataType == null || dataValue == null) {
            return false;
        } else {
            switch (attributeDataType) {
                case BOOLEAN:
                    if (dataValue instanceof Boolean) {
                        return true;
                    }
                    break;
                case INT:
                    if (dataValue instanceof Integer) {
                        return true;
                    }
                    break;
                case SHORT:
                    if (dataValue instanceof Short) {
                        return true;
                    }
                    break;
                case LONG:
                    if (dataValue instanceof Short ||
                            dataValue instanceof Byte ||
                            dataValue instanceof Integer ||
                            dataValue instanceof Long) {
                        return true;
                    }
                    break;
                case FLOAT:
                    if (dataValue instanceof Float) {
                        return true;
                    }
                    break;
                case DOUBLE:
                    if (dataValue instanceof Float ||
                            dataValue instanceof BigDecimal ||
                            dataValue instanceof Double) {
                        return true;
                    }
                    break;
                case TIMESTAMP:
                    if (dataValue instanceof Date) {
                        return true;
                    }
                    break;
                case STRING:
                    if (dataValue instanceof String) {
                        return true;
                    }
                    break;
                case BINARY:
                    if (dataValue instanceof byte[]) {
                        return true;
                    }
                    break;
                case BYTE:
                    if (dataValue instanceof Byte) {
                        return true;
                    }
                    break;
                case DECIMAL:
                    if (dataValue instanceof BigDecimal) {
                        return true;
                    }
                    break;
                case DATE:
                    if (dataValue instanceof LocalDate) {
                        return true;
                    }
                    break;
                case TIME:
                    if (dataValue instanceof LocalTime) {
                        return true;
                    }
                    break;
                case DATETIME:
                    if (dataValue instanceof LocalDateTime) {
                        return true;
                    }
                    break;
                case BOOLEAN_ARRAY:
                    if (dataValue instanceof Boolean[]) {
                        return true;
                    }
                    break;
                case INT_ARRAY:
                    if (dataValue instanceof Integer[]) {
                        return true;
                    }
                    break;
                case SHORT_ARRAY:
                    if (dataValue instanceof Short[]) {
                        return true;
                    }
                    break;
                case LONG_ARRAY:
                    if (dataValue instanceof Short[] ||
                            dataValue instanceof Byte[] ||
                            dataValue instanceof byte[] ||
                            dataValue instanceof Integer[] ||
                            dataValue instanceof Long[]) {
                        return true;
                    }
                    break;
                case FLOAT_ARRAY:
                    if (dataValue instanceof Float[]) {
                        return true;
                    }
                    break;
                case DOUBLE_ARRAY:
                    if (dataValue instanceof Float[] ||
                            dataValue instanceof BigDecimal[] ||
                            dataValue instanceof Double[]) {
                        return true;
                    }
                    break;
                case TIMESTAMP_ARRAY:
                    if (dataValue instanceof Date[]) {
                        return true;
                    }
                    break;
                case STRING_ARRAY:
                    if (dataValue instanceof String[]) {
                        return true;
                    }
                    break;
                case BYTE_ARRAY:
                    if (dataValue instanceof Byte[]) {
                        return true;
                    }
                    break;
                case DECIMAL_ARRAY:
                    if (dataValue instanceof BigDecimal[]) {
                        return true;
                    }
                    break;
                case DATE_ARRAY:
                    if (dataValue instanceof LocalDate[]) {
                        return true;
                    }
                    break;
                case TIME_ARRAY:
                    if (dataValue instanceof LocalTime[]) {
                        return true;
                    }
                    break;
                case DATETIME_ARRAY:
                    if (dataValue instanceof LocalDateTime[]) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public static void updateEntityMetaAttributes(GraphOperationExecutor workingGraphOperationExecutor, String entityUID, boolean isRelationEntity) {
        if (entityUID != null) {
            Map<String, Object> metaAttributesMap = new HashMap<>();
            metaAttributesMap.put(RealmConstant._lastModifyDateProperty, new Date());
            String updateMetaInfoCql;
            if (isRelationEntity) {
                updateMetaInfoCql = CypherBuilder.setRelationPropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(entityUID), metaAttributesMap);
            } else {
                updateMetaInfoCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(entityUID), metaAttributesMap);
            }
            workingGraphOperationExecutor.executeWrite(new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    return null;
                }
            }, updateMetaInfoCql);
        }
    }

    public static Condition getQueryCondition(PropertyContainer targetPropertyContainer, FilteringItem filteringItem) {
        if (filteringItem instanceof BetweenFilteringItem) {
            BetweenFilteringItem currentFilteringItem = (BetweenFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object fromValue = internalValueConvert(currentFilteringItem.getAttributeFromValue());
            Object toValue = internalValueConvert(currentFilteringItem.getAttributeToValue());
            if (propertyName != null & fromValue != null & toValue != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).lt(Cypher.literalOf(fromValue)).or(
                            targetPropertyContainer.property(filteringItem.getAttributeName()).gt(Cypher.literalOf(toValue))
                    );
                } else {
                    return targetPropertyContainer.property(propertyName).gte(Cypher.literalOf(fromValue)).and(
                            targetPropertyContainer.property(filteringItem.getAttributeName()).lte(Cypher.literalOf(toValue))
                    );
                }
            }
        } else if (filteringItem instanceof EqualFilteringItem) {
            EqualFilteringItem currentFilteringItem = (EqualFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = internalValueConvert(currentFilteringItem.getAttributeValue());
            if (propertyName != null & propertyValue != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).isNotEqualTo(Cypher.literalOf(propertyValue));
                } else {
                    return targetPropertyContainer.property(propertyName).isEqualTo(Cypher.literalOf(propertyValue));
                }
            }
        } else if (filteringItem instanceof GreaterThanEqualFilteringItem) {
            GreaterThanEqualFilteringItem currentFilteringItem = (GreaterThanEqualFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = internalValueConvert(currentFilteringItem.getAttributeValue());
            if (propertyName != null & propertyValue != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).lt(Cypher.literalOf(propertyValue));
                } else {
                    return targetPropertyContainer.property(propertyName).gte(Cypher.literalOf(propertyValue));
                }
            }
        } else if (filteringItem instanceof GreaterThanFilteringItem) {
            GreaterThanFilteringItem currentFilteringItem = (GreaterThanFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = internalValueConvert(currentFilteringItem.getAttributeValue());
            if (propertyName != null & propertyValue != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).lte(Cypher.literalOf(propertyValue));
                } else {
                    return targetPropertyContainer.property(propertyName).gt(Cypher.literalOf(propertyValue));
                }
            }
        } else if (filteringItem instanceof InValueFilteringItem) {
            InValueFilteringItem currentFilteringItem = (InValueFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            List<Object> propertyValue = currentFilteringItem.getAttributeValues();
            Literal[] listLiteralValue = generateListLiteralValue(propertyValue);
            if (filteringItem.isReversedCondition()) {
                return targetPropertyContainer.property(propertyName).in(Cypher.listOf(listLiteralValue)).not();
            } else {
                return targetPropertyContainer.property(propertyName).in(Cypher.listOf(listLiteralValue));
            }
        } else if (filteringItem instanceof LessThanEqualFilteringItem) {
            LessThanEqualFilteringItem currentFilteringItem = (LessThanEqualFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = internalValueConvert(currentFilteringItem.getAttributeValue());
            if (propertyName != null & propertyValue != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).gt(Cypher.literalOf(propertyValue));
                } else {
                    return targetPropertyContainer.property(propertyName).lte(Cypher.literalOf(propertyValue));
                }
            }
        } else if (filteringItem instanceof LessThanFilteringItem) {
            LessThanFilteringItem currentFilteringItem = (LessThanFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = internalValueConvert(currentFilteringItem.getAttributeValue());
            if (propertyName != null & propertyValue != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).gte(Cypher.literalOf(propertyValue));
                } else {
                    return targetPropertyContainer.property(propertyName).lt(Cypher.literalOf(propertyValue));
                }
            }
        } else if (filteringItem instanceof NotEqualFilteringItem) {
            NotEqualFilteringItem currentFilteringItem = (NotEqualFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = internalValueConvert(currentFilteringItem.getAttributeValue());
            if (propertyName != null & propertyValue != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).isEqualTo(Cypher.literalOf(propertyValue));
                } else {
                    return targetPropertyContainer.property(propertyName).isNotEqualTo(Cypher.literalOf(propertyValue));
                }
            }
        } else if (filteringItem instanceof NullValueFilteringItem) {
            NullValueFilteringItem currentFilteringItem = (NullValueFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            if (propertyName != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).isNotNull();
                } else {
                    return targetPropertyContainer.property(propertyName).isNull();
                }
            }
        } else if (filteringItem instanceof RegularMatchFilteringItem) {
            RegularMatchFilteringItem currentFilteringItem = (RegularMatchFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            String propertyValue = currentFilteringItem.getAttributeValue();
            if (propertyName != null & propertyValue != null) {
                if (filteringItem.isReversedCondition()) {
                    return targetPropertyContainer.property(propertyName).matches(Cypher.literalOf(propertyValue)).not();
                } else {
                    return targetPropertyContainer.property(propertyName).matches(Cypher.literalOf(propertyValue));
                }
            }
        } else if (filteringItem instanceof SimilarFilteringItem) {
            SimilarFilteringItem currentFilteringItem = (SimilarFilteringItem) filteringItem;
            String propertyName = currentFilteringItem.getAttributeName();
            Object propertyValue = internalValueConvert(currentFilteringItem.getAttributeValue());
            SimilarFilteringItem.MatchingType matchingType = currentFilteringItem.getMatchingType();
            if (propertyName != null & propertyValue != null) {
                if (filteringItem.isReversedCondition()) {
                    switch (matchingType) {
                        case BeginWith:
                            return targetPropertyContainer.property(propertyName).startsWith(Cypher.literalOf(propertyValue)).not();
                        case Contain:
                            return targetPropertyContainer.property(propertyName).contains(Cypher.literalOf(propertyValue)).not();
                        case EndWith:
                            return targetPropertyContainer.property(propertyName).endsWith(Cypher.literalOf(propertyValue)).not();
                    }
                } else {
                    switch (matchingType) {
                        case BeginWith:
                            return targetPropertyContainer.property(propertyName).startsWith(Cypher.literalOf(propertyValue));
                        case Contain:
                            return targetPropertyContainer.property(propertyName).contains(Cypher.literalOf(propertyValue));
                        case EndWith:
                            return targetPropertyContainer.property(propertyName).endsWith(Cypher.literalOf(propertyValue));
                    }
                }
            }
        } else if (filteringItem instanceof UIDMatchFilteringItem) {
            UIDMatchFilteringItem currentFilteringItem = (UIDMatchFilteringItem) filteringItem;
            String uidValue = currentFilteringItem.getUidValue();
            if (filteringItem.isReversedCondition()) {
                if (targetPropertyContainer instanceof Node) {
                    return Functions2.id((Node) targetPropertyContainer).isNotEqualTo(Cypher.literalOf(Long.parseLong(uidValue)));
                }
                if (targetPropertyContainer instanceof Relationship) {
                    return Functions2.id((Relationship) targetPropertyContainer).isNotEqualTo(Cypher.literalOf(Long.parseLong(uidValue)));
                }
            } else {
                if (targetPropertyContainer instanceof Node) {
                    return Functions2.id((Node) targetPropertyContainer).isEqualTo(Cypher.literalOf(Long.parseLong(uidValue)));
                }
                if (targetPropertyContainer instanceof Relationship) {
                    return Functions2.id((Relationship) targetPropertyContainer).isEqualTo(Cypher.literalOf(Long.parseLong(uidValue)));
                }
            }
        } else if (filteringItem instanceof UIDInValueFilteringItem) {
            UIDInValueFilteringItem currentFilteringItem = (UIDInValueFilteringItem) filteringItem;
            Set<String> uidValuesSet = currentFilteringItem.getUidValues();
            List<Object> uidsValueList = new ArrayList<>();
            for (String currentUidStr : uidValuesSet) {
                uidsValueList.add(Long.parseLong(currentUidStr));
            }
            Literal[] listLiteralValue = generateListLiteralValue(uidsValueList);
            if (filteringItem.isReversedCondition()) {
                return Functions2.id((Node) targetPropertyContainer).in(Cypher.listOf(listLiteralValue)).not();
            } else {
                return Functions2.id((Node) targetPropertyContainer).in(Cypher.listOf(listLiteralValue));
            }
        }
        return null;
    }

    private static Object internalValueConvert(Object orgValue) {
        if (orgValue instanceof Date) {
            ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date) orgValue).toInstant(), systemDefaultZoneId);
            return targetZonedDateTime;
        }
        return orgValue;
    }

    public static Literal[] generateListLiteralValue(List<Object> propertyValue) {
        Literal[] listLiteralValue = new Literal[propertyValue.size()];
        if (propertyValue != null) {
            for (int i = 0; i < propertyValue.size(); i++) {
                Object currentValue = propertyValue.get(i);
                if (currentValue instanceof ZonedDateTime) {
                    ZonedDateTime targetZonedDateTime = (ZonedDateTime) currentValue;
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    listLiteralValue[i] = new CustomContentLiteral("datetime('" + targetZonedDateTimeString + "')");
                } else if (currentValue instanceof Date) {
                    ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date) currentValue).toInstant(), systemDefaultZoneId);
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    listLiteralValue[i] = new CustomContentLiteral("datetime('" + targetZonedDateTimeString + "')");
                } else {
                    listLiteralValue[i] = Cypher.literalOf(currentValue);
                }
            }
        }
        return listLiteralValue;
    }

    public static List<Literal> formatListLiteralValue(List<Long> propertyValue) {
        List<Literal> literalList = new ArrayList<>();
        for (Long currentLongValue : propertyValue) {
            literalList.add(Cypher.literalOf(currentLongValue));
        }
        return literalList;
    }

    public static void attachEntities(String entityKind, List<String> entityUIDList, GraphOperationExecutor workingGraphOperationExecutor) {
        try {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setResultNumber(1000000);
            queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachSourceKind, entityKind));
            queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachTargetKind, entityKind), QueryParameters.FilteringLogic.OR);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.RelationAttachKindClass, queryParameters, null);
            GetListRelationAttachKindTransformer getListRelationAttachKindTransformer = new GetListRelationAttachKindTransformer(null, workingGraphOperationExecutor);
            Object relationAttachKindsRes = workingGraphOperationExecutor.executeWrite(getListRelationAttachKindTransformer, queryCql);
            List<RelationAttachKind> relationAttachKindList = relationAttachKindsRes != null ? (List<RelationAttachKind>) relationAttachKindsRes : null;

            for (RelationAttachKind currentRelationAttachKind : relationAttachKindList) {
                Neo4JRelationAttachKindImpl neo4JRelationAttachKindImpl = (Neo4JRelationAttachKindImpl) currentRelationAttachKind;
                neo4JRelationAttachKindImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                String sourceKindName = neo4JRelationAttachKindImpl.getSourceConceptionKindName();
                String targetKindName = neo4JRelationAttachKindImpl.getTargetConceptionKindName();
                if (sourceKindName.equals(entityKind)) {
                    if (entityUIDList.size() == 1) {
                        neo4JRelationAttachKindImpl.newRelationEntities(entityUIDList.get(0), RelationAttachKind.EntityRelateRole.SOURCE, null);
                    } else {
                        neo4JRelationAttachKindImpl.newRelationEntities(entityUIDList, RelationAttachKind.EntityRelateRole.SOURCE, null);
                    }
                }
                if (targetKindName.equals(entityKind)) {
                    if (entityUIDList.size() == 1) {
                        neo4JRelationAttachKindImpl.newRelationEntities(entityUIDList.get(0), RelationAttachKind.EntityRelateRole.TARGET, null);
                    } else {
                        neo4JRelationAttachKindImpl.newRelationEntities(entityUIDList, RelationAttachKind.EntityRelateRole.TARGET, null);
                    }
                }
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
    }

    public static Expression getAttributeValueExpressForMemgraph(Object propertyValue) {
        if (propertyValue instanceof CharSequence || propertyValue instanceof Number ||
                propertyValue instanceof Iterable || propertyValue instanceof Boolean
        ){
            return Cypher.literalOf(propertyValue);
        }else if(propertyValue instanceof Temporal){
            if (propertyValue instanceof ZonedDateTime) {
                ZonedDateTime targetZonedDateTime = (ZonedDateTime) propertyValue;
                String targetZonedDateTimeString;
                if (CoreRealmStorageImplTech.MEMGRAPH.toString().equals(CORE_REALM_STORAGE_IMPL_TECH)) {
                    if (zonedDateTimeFormatter == null) {
                        zonedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    }
                    targetZonedDateTimeString = targetZonedDateTime.format(zonedDateTimeFormatter);
                    return Functions2.localDateTime(Cypher.literalOf(targetZonedDateTimeString));
                } else {
                    targetZonedDateTimeString = targetZonedDateTime.toString();
                    return Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
                }
            } else{
                return Cypher.literalOf(propertyValue);
            }
        }else if (propertyValue instanceof Date) {
            ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date) propertyValue).toInstant(), systemDefaultZoneId);
            String targetZonedDateTimeString;
            if (CoreRealmStorageImplTech.MEMGRAPH.toString().equals(CORE_REALM_STORAGE_IMPL_TECH)) {
                if (zonedDateTimeFormatter == null) {
                    zonedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                }
                targetZonedDateTimeString = targetZonedDateTime.format(zonedDateTimeFormatter);
                return Functions2.localDateTime(Cypher.literalOf(targetZonedDateTimeString));
            } else {
                targetZonedDateTime = ZonedDateTime.ofInstant(((Date) propertyValue).toInstant(), systemDefaultZoneId);
                targetZonedDateTimeString = targetZonedDateTime.toString();
                return Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
            }
        }else if (propertyValue instanceof Date[]) {
            Date[] dateValueArray = (Date[]) propertyValue;
            Expression[] dataValueExpressArray = new Expression[dateValueArray.length];
            for (int i = 0; i < dateValueArray.length; i++) {
                Date currentValue = dateValueArray[i];
                ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(currentValue.toInstant(), systemDefaultZoneId);
                String targetZonedDateTimeString;
                if (CoreRealmStorageImplTech.MEMGRAPH.toString().equals(CORE_REALM_STORAGE_IMPL_TECH)) {
                    if (zonedDateTimeFormatter == null) {
                        zonedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    }
                    targetZonedDateTimeString = targetZonedDateTime.format(zonedDateTimeFormatter);
                } else {
                    targetZonedDateTimeString = targetZonedDateTime.toString();
                }
                dataValueExpressArray[i] = Functions2.localDateTime(Cypher.literalOf(targetZonedDateTimeString));
            }
            return Cypher.listOf(dataValueExpressArray);
        } else {
            return null;
        }
    }

    public static Expression getAttributeValueExpress(Object propertyValue) {
        //convert Enum to String directly
        if(propertyValue instanceof Enum<?>){
            propertyValue = propertyValue.toString();
        }
        if (propertyValue instanceof CharSequence || propertyValue instanceof Number ||
                propertyValue instanceof Iterable || propertyValue instanceof Boolean) {
            return Cypher.literalOf(propertyValue);
        } else if (propertyValue instanceof Temporal) {
            if(propertyValue instanceof ZonedDateTime){
                if (CoreRealmStorageImplTech.MEMGRAPH.toString().equals(CORE_REALM_STORAGE_IMPL_TECH)) {
                    if (zonedDateTimeFormatter == null) {
                        zonedDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    }
                    ZonedDateTime targetZonedDateTime = (ZonedDateTime) propertyValue;
                    String targetZonedDateTimeString = targetZonedDateTime.format(zonedDateTimeFormatter);
                    return Functions2.localDateTime(Cypher.literalOf(targetZonedDateTimeString));
                }else{
                    ZonedDateTime targetZonedDateTime = (ZonedDateTime) propertyValue;
                    String targetZonedDateTimeString = targetZonedDateTime.toString();
                    return Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
                }
            }else{
                return Cypher.literalOf(propertyValue);
            }
        } else if (propertyValue instanceof Date) {
            ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(((Date) propertyValue).toInstant(), systemDefaultZoneId);
            String targetZonedDateTimeString = targetZonedDateTime.toString();
            return Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
        } else if (propertyValue instanceof Date[]) {
            Date[] dateValueArray = (Date[]) propertyValue;
            Expression[] dataValueExpressArray = new Expression[dateValueArray.length];
            for (int i = 0; i < dateValueArray.length; i++) {
                Date currentValue = dateValueArray[i];
                ZonedDateTime targetZonedDateTime = ZonedDateTime.ofInstant(currentValue.toInstant(), systemDefaultZoneId);
                String targetZonedDateTimeString = targetZonedDateTime.toString();
                dataValueExpressArray[i] = Functions2.datetime(Cypher.literalOf(targetZonedDateTimeString));
            }
            return Cypher.listOf(dataValueExpressArray);
        }else{
            return Cypher.literalOf(propertyValue);
        }
    }
}
