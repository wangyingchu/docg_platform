package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class LouvainAlgorithmConfig extends ResultPaginationableConfig{

    private String seedProperty = null;
    private int maxLevels = 10;
    private int maxIterations = 10;
    private float tolerance = 0.0001f;
    private boolean includeIntermediateCommunities = false;
    private boolean consecutiveIds = false;
    private String relationshipWeightAttribute = null;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public String getSeedProperty() {
        return seedProperty;
    }

    public void setSeedProperty(String seedProperty) {
        this.seedProperty = seedProperty;
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public void setMaxLevels(int maxLevels) {
        this.maxLevels = maxLevels;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public float getTolerance() {
        return tolerance;
    }

    public void setTolerance(float tolerance) {
        this.tolerance = tolerance;
    }

    public boolean isIncludeIntermediateCommunities() {
        return includeIntermediateCommunities;
    }

    public void setIncludeIntermediateCommunities(boolean includeIntermediateCommunities) {
        this.includeIntermediateCommunities = includeIntermediateCommunities;
    }

    public boolean isConsecutiveIds() {
        return consecutiveIds;
    }

    public void setConsecutiveIds(boolean consecutiveIds) {
        this.consecutiveIds = consecutiveIds;
    }

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }

    public DataScienceOperator.ValueSortingLogic getCommunityIdSortingLogic() {
        return valueSortingLogic;
    }

    public void setCommunityIdSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
