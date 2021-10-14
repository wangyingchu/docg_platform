package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class ApproximateMaximumKCutAlgorithmConfig extends ResultPaginationableConfig {

    private String relationshipWeightAttribute;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;
    private int k = 2;
    private int iterations = 8;
    private int vnsMaxNeighborhoodOrder = 0;

    public DataScienceOperator.ValueSortingLogic getCommunityIdSortingLogic() {
        return valueSortingLogic;
    }

    public void setCommunityIdSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getVnsMaxNeighborhoodOrder() {
        return vnsMaxNeighborhoodOrder;
    }

    public void setVnsMaxNeighborhoodOrder(int vnsMaxNeighborhoodOrder) {
        this.vnsMaxNeighborhoodOrder = vnsMaxNeighborhoodOrder;
    }
}
