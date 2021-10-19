package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class AllPairsShortestPathAlgorithmConfig extends ResultPaginationableConfig {

    private String relationshipWeightAttribute;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }

    public DataScienceOperator.ValueSortingLogic getDistanceSortingLogic() {
        return valueSortingLogic;
    }

    public void setDistanceSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
