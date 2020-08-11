package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;

public class AttributeValue {
    private String attributeName;
    private AttributeDataType attributeDataType;
    private Object attributeValue;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public AttributeDataType getAttributeDataType() {
        return attributeDataType;
    }

    public void setAttributeDataType(AttributeDataType attributeDataType) {
        this.attributeDataType = attributeDataType;
    }

    public Object getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(Object attributeValue) {
        this.attributeValue = attributeValue;
    }
}
