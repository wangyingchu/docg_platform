package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class LabelPropagationAlgorithmConfig extends ResultPaginationableConfig{

    private String seedProperty = null;
    private int maxIterations = 10;
    private boolean consecutiveIds = false;
    private String relationshipWeightAttribute = null;
    private String nodeWeightAttribute = null;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;


    public String getSeedProperty() {
        return seedProperty;
    }

    public void setSeedProperty(String seedProperty) {
        this.seedProperty = seedProperty;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
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

    public String getNodeWeightAttribute() {
        return nodeWeightAttribute;
    }

    public void setNodeWeightAttribute(String nodeWeightAttribute) {
        this.nodeWeightAttribute = nodeWeightAttribute;
    }

    public DataScienceOperator.ValueSortingLogic getCommunityIdSortingLogic() {
        return valueSortingLogic;
    }

    public void setCommunityIdSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
