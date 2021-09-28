package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import java.util.HashSet;
import java.util.Set;

public class ConceptionKindComputeConfig {

    private String conceptionKindName;
    private Set<String> conceptionKindAttributes;

    public ConceptionKindComputeConfig(String conceptionKindName){
        this.conceptionKindName = conceptionKindName;
        this.conceptionKindAttributes = new HashSet<>();
    }

    public String getConceptionKindName() {
        return conceptionKindName;
    }

    public void setConceptionKindName(String conceptionKindName) {
        this.conceptionKindName = conceptionKindName;
    }

    public Set<String> getConceptionKindAttributes() {
        return conceptionKindAttributes;
    }

    public void setConceptionKindAttributes(Set<String> conceptionKindAttributes) {
        this.conceptionKindAttributes = conceptionKindAttributes;
    }
}
