package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class BetweennessCentralityAlgorithmConfig extends ResultPaginationableConfig{

    private Integer samplingSize;
    private Integer samplingSeed;
    private String relationWeightAttribute;
    private DataScienceOperator.ValueScalerLogic valueScalerLogic;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public Integer getSamplingSize() {
        return samplingSize;
    }

    public void setSamplingSize(Integer samplingSize) {
        this.samplingSize = samplingSize;
    }

    public Integer getSamplingSeed() {
        return samplingSeed;
    }

    public void setSamplingSeed(Integer samplingSeed) {
        this.samplingSeed = samplingSeed;
    }

    public DataScienceOperator.ValueScalerLogic getScoreScalerLogic() {
        return valueScalerLogic;
    }

    public void setScoreScalerLogic(DataScienceOperator.ValueScalerLogic valueScalerLogic) {
        this.valueScalerLogic = valueScalerLogic;
    }

    public DataScienceOperator.ValueSortingLogic getScoreSortingLogic() {
        return valueSortingLogic;
    }

    public void setScoreSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }

    public String getRelationWeightAttribute() {
        return relationWeightAttribute;
    }

    public void setRelationWeightAttribute(String relationWeightAttribute) {
        this.relationWeightAttribute = relationWeightAttribute;
    }
}
