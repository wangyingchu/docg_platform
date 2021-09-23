package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class PageRankAlgorithmConfig extends ResultPaginationableConfig {

    private float dampingFactor = 0.85f;
    private int maxIterations = 20;
    private float tolerance = 0.0000001f;
    private String relationshipWeightAttribute;
    private DataScienceOperator.ValueScalerLogic valueScalerLogic;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public float getDampingFactor() {
        return dampingFactor;
    }

    public void setDampingFactor(float dampingFactor) {
        this.dampingFactor = dampingFactor;
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

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }

    public DataScienceOperator.ValueSortingLogic getScoreSortingLogic() {
        return valueSortingLogic;
    }

    public void setScoreSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }

    public DataScienceOperator.ValueScalerLogic getScoreScalerLogic() {
        return valueScalerLogic;
    }

    public void setScoreScalerLogic(DataScienceOperator.ValueScalerLogic valueScalerLogic) {
        this.valueScalerLogic = valueScalerLogic;
    }
}
