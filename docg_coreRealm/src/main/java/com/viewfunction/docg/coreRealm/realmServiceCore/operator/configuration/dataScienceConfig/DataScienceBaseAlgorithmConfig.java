package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import java.util.Set;

public class DataScienceBaseAlgorithmConfig {

    private Set<String> conceptionKindsForCompute;
    private Set<String> relationKindsForCompute;

    public Set<String> getConceptionKindsForCompute() {
        return conceptionKindsForCompute;
    }

    public void setConceptionKindsForCompute(Set<String> conceptionKindsForCompute) {
        this.conceptionKindsForCompute = conceptionKindsForCompute;
    }

    public Set<String> getRelationKindsForCompute() {
        return relationKindsForCompute;
    }

    public void setRelationKindsForCompute(Set<String> relationKindsForCompute) {
        this.relationKindsForCompute = relationKindsForCompute;
    }
}
