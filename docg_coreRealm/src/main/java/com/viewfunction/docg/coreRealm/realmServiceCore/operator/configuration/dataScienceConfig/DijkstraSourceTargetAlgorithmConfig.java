package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

public class DijkstraSourceTargetAlgorithmConfig extends DataScienceBaseAlgorithmConfig {

    private Integer maxPathNumber;
    private String relationshipWeightAttribute;
    private String sourceConceptionEntityUID;
    private String targetConceptionEntityUID;

    public Integer getMaxPathNumber() {
        return maxPathNumber;
    }

    public void setMaxPathNumber(Integer maxPathNumber) {
        this.maxPathNumber = maxPathNumber;
    }

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }

    public String getSourceConceptionEntityUID() {
        return sourceConceptionEntityUID;
    }

    public void setSourceConceptionEntityUID(String sourceConceptionEntityUID) {
        this.sourceConceptionEntityUID = sourceConceptionEntityUID;
    }

    public String getTargetConceptionEntityUID() {
        return targetConceptionEntityUID;
    }

    public void setTargetConceptionEntityUID(String targetConceptionEntityUID) {
        this.targetConceptionEntityUID = targetConceptionEntityUID;
    }
}
