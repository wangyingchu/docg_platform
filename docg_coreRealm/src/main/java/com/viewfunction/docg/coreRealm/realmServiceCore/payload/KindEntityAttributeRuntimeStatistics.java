package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;

public class KindEntityAttributeRuntimeStatistics {

    private String kindName;
    private String attributeName;
    private AttributeDataType attributeDataType;
    private long sampleCount;
    private long attributeHitCount;

    public KindEntityAttributeRuntimeStatistics(String kindName,String attributeName,String attributeDataTypeValue,long sampleCount,long attributeHitCount){
        this.kindName = kindName;
        this.attributeName = attributeName;
        this.sampleCount = sampleCount;
        this.attributeHitCount = attributeHitCount;

        if(attributeDataTypeValue.equalsIgnoreCase("String")){
            attributeDataType = AttributeDataType.STRING;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Long")){
            attributeDataType = AttributeDataType.LONG;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Boolean")){
            attributeDataType = AttributeDataType.BOOLEAN;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Byte")){
            attributeDataType = AttributeDataType.BYTE;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Short")){
            attributeDataType = AttributeDataType.SHORT;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Int")){
            attributeDataType = AttributeDataType.INT;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Float")){
            attributeDataType = AttributeDataType.FLOAT;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Double")){
            attributeDataType = AttributeDataType.DOUBLE;
        }else if(attributeDataTypeValue.equalsIgnoreCase("DateTime")){
            attributeDataType = AttributeDataType.TIMESTAMP;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Char")){
            attributeDataType = AttributeDataType.STRING;
        }else if(attributeDataTypeValue.equalsIgnoreCase("StringArray")){
            attributeDataType = AttributeDataType.STRING_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("LongArray")){
            attributeDataType = AttributeDataType.LONG_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("BooleanArray")){
            attributeDataType = AttributeDataType.BOOLEAN_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("ByteArray")){
            attributeDataType = AttributeDataType.BYTE_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("ShortArray")){
            attributeDataType = AttributeDataType.SHORT_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("IntArray")){
            attributeDataType = AttributeDataType.INT_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("FloatArray")){
            attributeDataType = AttributeDataType.FLOAT_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("DoubleArray")){
            attributeDataType = AttributeDataType.DOUBLE_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("DateTimeArray")){
            attributeDataType = AttributeDataType.TIMESTAMP_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("CharArray")){
            attributeDataType = AttributeDataType.STRING_ARRAY;
        }else if(attributeDataTypeValue.equalsIgnoreCase("Date")){
            attributeDataType = AttributeDataType.DATE;
        }else if(attributeDataTypeValue.equalsIgnoreCase("LocalDateTime")){
            attributeDataType = AttributeDataType.DATETIME;
        }else if(attributeDataTypeValue.equalsIgnoreCase("LocalDate")){
            attributeDataType = AttributeDataType.DATE;
        }else if(attributeDataTypeValue.equalsIgnoreCase("LocalTime")){
            attributeDataType = AttributeDataType.TIME;
        }
    }

    public String getKindName() {
        return kindName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public AttributeDataType getAttributeDataType() {
        return attributeDataType;
    }

    public long getSampleCount() {
        return sampleCount;
    }

    public long getAttributeHitCount() {
        return attributeHitCount;
    }
}
