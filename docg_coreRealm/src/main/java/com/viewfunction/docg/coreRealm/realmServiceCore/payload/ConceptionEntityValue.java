package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Map;

public class ConceptionEntityValue {

    private String conceptionEntityUID;
    private Map<String,Object> entityAttributesValue;

    public ConceptionEntityValue(){}

    public ConceptionEntityValue(String conceptionEntityUID){
        this.conceptionEntityUID = conceptionEntityUID;
    }

    public ConceptionEntityValue(Map<String,Object> entityAttributesValue){
        this.entityAttributesValue = entityAttributesValue;
    }

    public ConceptionEntityValue(String conceptionEntityUID,Map<String,Object> entityAttributesValue){
        this.conceptionEntityUID = conceptionEntityUID;
        this.entityAttributesValue = entityAttributesValue;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public void setConceptionEntityUID(String conceptionEntityUID) {
        this.conceptionEntityUID = conceptionEntityUID;
    }

    public Map<String, Object> getEntityAttributesValue() {
        return entityAttributesValue;
    }

    public void setEntityAttributesValue(Map<String, Object> entityAttributesValue) {
        this.entityAttributesValue = entityAttributesValue;
    }
}
