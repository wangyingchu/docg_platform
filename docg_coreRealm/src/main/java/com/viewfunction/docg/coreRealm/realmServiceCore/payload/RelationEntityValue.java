package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntityValue;

import java.util.Map;

public class RelationEntityValue implements PathEntityValue {

    private String relationEntityUID;
    private String fromConceptionEntityUID;
    private String toConceptionEntityUID;
    private Map<String,Object> entityAttributesValue;

    public RelationEntityValue(){}

    public RelationEntityValue(String relationEntityUID,String fromConceptionEntityUID,String toConceptionEntityUID,
                               Map<String,Object> entityAttributesValue){
        this.relationEntityUID = relationEntityUID;
        this.fromConceptionEntityUID = fromConceptionEntityUID;
        this.toConceptionEntityUID = toConceptionEntityUID;
        this.entityAttributesValue = entityAttributesValue;
    }

    public String getRelationEntityUID() {
        return relationEntityUID;
    }

    public void setRelationEntityUID(String relationEntityUID) {
        this.relationEntityUID = relationEntityUID;
    }

    public String getFromConceptionEntityUID() {
        return fromConceptionEntityUID;
    }

    public void setFromConceptionEntityUID(String fromConceptionEntityUID) {
        this.fromConceptionEntityUID = fromConceptionEntityUID;
    }

    public String getToConceptionEntityUID() {
        return toConceptionEntityUID;
    }

    public void setToConceptionEntityUID(String toConceptionEntityUID) {
        this.toConceptionEntityUID = toConceptionEntityUID;
    }

    public Map<String, Object> getEntityAttributesValue() {
        return entityAttributesValue;
    }

    public void setEntityAttributesValue(Map<String, Object> entityAttributesValue) {
        this.entityAttributesValue = entityAttributesValue;
    }
}
