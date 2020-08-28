package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject;

public class ConceptionKindVO {

    private String conceptionKindName;
    private String conceptionKindDesc;

    public ConceptionKindVO(String conceptionKindName,String conceptionKindDesc){
        this.conceptionKindName = conceptionKindName;
        this.conceptionKindDesc = conceptionKindDesc;
    }

    public ConceptionKindVO(){}

    public String getConceptionKindName() {
        return conceptionKindName;
    }

    public void setConceptionKindName(String conceptionKindName) {
        this.conceptionKindName = conceptionKindName;
    }

    public String getConceptionKindDesc() {
        return conceptionKindDesc;
    }

    public void setConceptionKindDesc(String conceptionKindDesc) {
        this.conceptionKindDesc = conceptionKindDesc;
    }
}
