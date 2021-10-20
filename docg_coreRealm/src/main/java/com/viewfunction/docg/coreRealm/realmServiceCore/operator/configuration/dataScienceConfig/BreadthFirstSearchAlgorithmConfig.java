package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import java.util.Set;

public class BreadthFirstSearchAlgorithmConfig extends DataScienceBaseAlgorithmConfig {

    private String sourceConceptionEntityUID;
    private Set<String> terminateAtConceptionEntityUIDs;
    private int maxDepth = -1;

    public BreadthFirstSearchAlgorithmConfig(String sourceConceptionEntityUID){
        this.sourceConceptionEntityUID = sourceConceptionEntityUID;
    }

    public String getSourceConceptionEntityUID() {
        return sourceConceptionEntityUID;
    }

    public Set<String> getTerminateAtConceptionEntityUIDs() {
        return terminateAtConceptionEntityUIDs;
    }

    public void setTerminateAtConceptionEntityUIDs(Set<String> terminateAtConceptionEntityUIDs) {
        this.terminateAtConceptionEntityUIDs = terminateAtConceptionEntityUIDs;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
