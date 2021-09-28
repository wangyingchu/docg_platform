package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class NodeSimilarityAlgorithmConfig extends ResultPaginationableConfig{

    private Float similarityCutoff = null;
    private int degreeCutoff = 1;
    private int topK = 10;
    private int bottomK = 10;
    private int topN = 0;
    private int bottomN = 0;
    private String relationshipWeightAttribute = null;
    private DataScienceOperator.ValueSortingLogic valueSortingLogic;

    public Float getSimilarityCutoff() {
        return similarityCutoff;
    }

    public void setSimilarityCutoff(Float similarityCutoff) {
        this.similarityCutoff = similarityCutoff;
    }

    public int getDegreeCutoff() {
        return degreeCutoff;
    }

    public void setDegreeCutoff(int degreeCutoff) {
        this.degreeCutoff = degreeCutoff;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public int getBottomK() {
        return bottomK;
    }

    public void setBottomK(int bottomK) {
        this.bottomK = bottomK;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public int getBottomN() {
        return bottomN;
    }

    public void setBottomN(int bottomN) {
        this.bottomN = bottomN;
    }

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }

    public DataScienceOperator.ValueSortingLogic getSimilaritySortingLogic() {
        return valueSortingLogic;
    }

    public void setSimilaritySortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.valueSortingLogic = valueSortingLogic;
    }
}
