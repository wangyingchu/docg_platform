package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class SingleSourceShortestPathAlgorithmConfig extends ResultPaginationableConfig {

    private String relationshipWeightAttribute;
    private String sourceConceptionEntityUID;
    private float delta = 3.0f;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public SingleSourceShortestPathAlgorithmConfig(String sourceConceptionEntityUID){
        this.sourceConceptionEntityUID = sourceConceptionEntityUID;
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

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public DataScienceOperator.ValueSortingLogic getDistanceSortingLogic() {
        return valueSortingLogic;
    }

    public void setDistanceSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
