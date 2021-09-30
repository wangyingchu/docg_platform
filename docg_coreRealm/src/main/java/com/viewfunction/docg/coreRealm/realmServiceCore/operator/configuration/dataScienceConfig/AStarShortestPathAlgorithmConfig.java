package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

public class AStarShortestPathAlgorithmConfig extends DataScienceBaseAlgorithmConfig{

    private String relationshipWeightAttribute;
    private String sourceConceptionEntityUID;
    private String targetConceptionEntityUID;
    private String latitudeAttribute;
    private String longitudeAttribute;
    private Integer maxPathNumber;

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

    public String getLatitudeAttribute() {
        return latitudeAttribute;
    }

    public void setLatitudeAttribute(String latitudeAttribute) {
        this.latitudeAttribute = latitudeAttribute;
    }

    public String getLongitudeAttribute() {
        return longitudeAttribute;
    }

    public void setLongitudeAttribute(String longitudeAttribute) {
        this.longitudeAttribute = longitudeAttribute;
    }

    public Integer getMaxPathNumber() {
        return maxPathNumber;
    }

    public void setMaxPathNumber(Integer maxPathNumber) {
        this.maxPathNumber = maxPathNumber;
    }
}
