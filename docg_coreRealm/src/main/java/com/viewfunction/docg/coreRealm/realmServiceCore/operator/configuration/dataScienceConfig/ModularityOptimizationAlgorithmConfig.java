package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class ModularityOptimizationAlgorithmConfig extends ResultPaginationableConfig {

    private int maxIterations = 10;
    private float tolerance = 0.0001f;
    private String seedProperty;
    private boolean consecutiveIds = false;
    private String relationshipWeightAttribute;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

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

    public String getSeedProperty() {
        return seedProperty;
    }

    public void setSeedProperty(String seedProperty) {
        this.seedProperty = seedProperty;
    }

    public boolean isConsecutiveIds() {
        return consecutiveIds;
    }

    public void setConsecutiveIds(boolean consecutiveIds) {
        this.consecutiveIds = consecutiveIds;
    }

    public DataScienceOperator.ValueSortingLogic getCommunityIdSortingLogic() {
        return valueSortingLogic;
    }

    public void setCommunityIdSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }
}
