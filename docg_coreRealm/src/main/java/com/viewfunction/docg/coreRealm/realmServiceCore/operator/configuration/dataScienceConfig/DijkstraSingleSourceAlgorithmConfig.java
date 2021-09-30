package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class DijkstraSingleSourceAlgorithmConfig extends ResultPaginationableConfig{

    private String relationshipWeightAttribute;
    private String sourceConceptionEntityUID;
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

    public DataScienceOperator.ValueSortingLogic getPathWeightSortingLogic() {
        return valueSortingLogic;
    }

    public void setPathWeightSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
