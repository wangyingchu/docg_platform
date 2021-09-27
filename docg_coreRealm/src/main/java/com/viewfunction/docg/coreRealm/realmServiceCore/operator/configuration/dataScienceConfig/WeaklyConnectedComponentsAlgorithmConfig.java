package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class WeaklyConnectedComponentsAlgorithmConfig extends ResultPaginationableConfig{

    private String seedProperty = null;
    private boolean consecutiveIds = false;
    private String relationshipWeightAttribute = null;
    private Float threshold = null;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

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

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }

    public Float getThreshold() {
        return threshold;
    }

    public void setThreshold(Float threshold) {
        this.threshold = threshold;
    }

    public DataScienceOperator.ValueSortingLogic getCommunityIdSortingLogic() {
        return valueSortingLogic;
    }

    public void setCommunityIdSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
