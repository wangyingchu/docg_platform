package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject;

import java.io.Serializable;

public class RelationKindVO implements Serializable {

    private String relationKindName;
    private String relationKindDesc;

    public RelationKindVO(String relationKindName,String relationKindDesc){
        this.relationKindName = relationKindName;
        this.relationKindDesc = relationKindDesc;
    }

    public RelationKindVO(){}

    public String getRelationKindName() {
        return relationKindName;
    }

    public void setRelationKindName(String relationKindName) {
        this.relationKindName = relationKindName;
    }

    public String getRelationKindDesc() {
        return relationKindDesc;
    }

    public void setRelationKindDesc(String relationKindDesc) {
        this.relationKindDesc = relationKindDesc;
    }
}
