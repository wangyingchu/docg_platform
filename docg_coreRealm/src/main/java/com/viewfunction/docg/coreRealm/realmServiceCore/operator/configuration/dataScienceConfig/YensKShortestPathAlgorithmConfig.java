package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class YensKShortestPathAlgorithmConfig extends DataScienceBaseAlgorithmConfig{

    private String relationshipWeightAttribute;
    private String sourceConceptionEntityUID;
    private String targetConceptionEntityUID;
    private int k = 1;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

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

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public DataScienceOperator.ValueSortingLogic getPathWeightSortingLogic() {
        return valueSortingLogic;
    }

    public void setPathWeightSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
