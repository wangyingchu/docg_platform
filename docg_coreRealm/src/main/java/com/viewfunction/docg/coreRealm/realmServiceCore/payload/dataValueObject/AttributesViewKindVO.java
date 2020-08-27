package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.io.Serializable;

public class AttributesViewKindVO implements Serializable {

    private String attributesViewKindUID;
    private String attributesViewKindName;
    private String attributesViewKindDesc;
    private AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm;

    public AttributesViewKindVO(String attributesViewKindName, String attributesViewKindDesc,
                                AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm,String attributesViewKindUID){
        this.attributesViewKindName = attributesViewKindName;
        this.attributesViewKindDesc = attributesViewKindDesc;
        this.attributesViewKindDataForm = attributesViewKindDataForm;
        this.attributesViewKindUID = attributesViewKindUID;
    }

    public AttributesViewKindVO(){}

    public String getAttributesViewKindUID() {
        return attributesViewKindUID;
    }

    public void setAttributesViewKindUID(String attributesViewKindUID) {
        this.attributesViewKindUID = attributesViewKindUID;
    }

    public String getAttributesViewKindName() {
        return attributesViewKindName;
    }

    public void setAttributesViewKindName(String attributesViewKindName) {
        this.attributesViewKindName = attributesViewKindName;
    }

    public String getAttributesViewKindDesc() {
        return attributesViewKindDesc;
    }

    public void setAttributesViewKindDesc(String attributesViewKindDesc) {
        this.attributesViewKindDesc = attributesViewKindDesc;
    }

    public AttributesViewKind.AttributesViewKindDataForm getAttributesViewKindDataForm() {
        return attributesViewKindDataForm;
    }

    public void setAttributesViewKindDataForm(AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm) {
        this.attributesViewKindDataForm = attributesViewKindDataForm;
    }
}
