package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;

import java.io.Serializable;

public class AttributeKindVO implements Serializable {

    private String attributeKindName;
    private String attributeKindUID;
    private String attributeKindDesc;
    private AttributeDataType attributeDataType;

    public AttributeKindVO(String attributeKindName,String attributeKindDesc,AttributeDataType attributeDataType,String attributeKindUID){
        this.attributeKindName = attributeKindName;
        this.attributeKindUID = attributeKindUID;
        this.attributeKindDesc = attributeKindDesc;
        this.attributeDataType = attributeDataType;
    }

    public AttributeKindVO(){

    }

    public String getAttributeKindName() {
        return attributeKindName;
    }

    public void setAttributeKindName(String attributeKindName) {
        this.attributeKindName = attributeKindName;
    }

    public String getAttributeKindUID() {
        return attributeKindUID;
    }

    public void setAttributeKindUID(String attributeKindUID) {
        this.attributeKindUID = attributeKindUID;
    }

    public String getAttributeKindDesc() {
        return attributeKindDesc;
    }

    public void setAttributeKindDesc(String attributeKindDesc) {
        this.attributeKindDesc = attributeKindDesc;
    }

    public AttributeDataType getAttributeDataType() {
        return attributeDataType;
    }

    public void setAttributeDataType(AttributeDataType attributeDataType) {
        this.attributeDataType = attributeDataType;
    }
}
