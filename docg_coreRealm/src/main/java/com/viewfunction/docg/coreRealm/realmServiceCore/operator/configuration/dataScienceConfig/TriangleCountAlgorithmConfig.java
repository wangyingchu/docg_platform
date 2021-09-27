package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class TriangleCountAlgorithmConfig extends ResultPaginationableConfig{

    private Integer maxDegree;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public Integer getMaxDegree() {
        return maxDegree;
    }

    public void setMaxDegree(Integer maxDegree) {
        this.maxDegree = maxDegree;
    }

    public DataScienceOperator.ValueSortingLogic getTriangleCountSortingLogic() {
        return valueSortingLogic;
    }

    public void setTriangleCountSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
