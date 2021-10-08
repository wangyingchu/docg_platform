package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class ClosenessCentralityAlgorithmConfig extends ResultPaginationableConfig{

    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public DataScienceOperator.ValueSortingLogic getCentralityWeightSortingLogic() {
        return valueSortingLogic;
    }

    public void setCentralityWeightSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
