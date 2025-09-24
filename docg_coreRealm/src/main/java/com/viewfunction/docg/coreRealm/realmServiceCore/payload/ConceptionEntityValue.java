package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntityValue;

import java.util.List;
import java.util.Map;

public class ConceptionEntityValue implements PathEntityValue {

    private String conceptionEntityUID;
    private Map<String,Object> entityAttributesValue;
    private List<String> allConceptionKindNames;

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

    public List<String> getAllConceptionKindNames() {
        return allConceptionKindNames;
    }

    public void setAllConceptionKindNames(List<String> allConceptionKindNames) {
        this.allConceptionKindNames = allConceptionKindNames;
    }
}
