package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class K1ColoringAlgorithmConfig extends ResultPaginationableConfig {

    private DataScienceOperator.ValueSortingLogic valueSortingLogic;
    private int maxIterations = 10;

    public DataScienceOperator.ValueSortingLogic getColorSortingLogic() {
        return valueSortingLogic;
    }

    public void setColorSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
