package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class DynamicContentValue {

    public enum ContentValueType{
        BOOLEAN,BYTE_ARRAY,DOUBLE,FLOAT,INT,DATE,TIME,DATETIME,LONG,CONCEPTION_ENTITY,NUMBER,ENTITIES_PATH,RELATION_ENTITY,STRING,TIMESTAMP
    }

    private ContentValueType valueType;
    private String valueName;
    private Object valueObject;

    public ContentValueType getValueType() {
        return valueType;
    }

    public void setValueType(ContentValueType valueType) {
        this.valueType = valueType;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public Object getValueObject() {
        return valueObject;
    }

    public void setValueObject(Object valueObject) {
        this.valueObject = valueObject;
    }
}
