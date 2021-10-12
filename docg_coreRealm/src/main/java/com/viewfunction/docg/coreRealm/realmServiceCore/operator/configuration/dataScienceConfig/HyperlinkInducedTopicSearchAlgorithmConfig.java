package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

public class HyperlinkInducedTopicSearchAlgorithmConfig extends ResultPaginationableConfig{

    private DataScienceOperator.ValueSortingLogic hubValueSortingLogic;
    private DataScienceOperator.ValueSortingLogic authValueSortingLogic;
    private int hitsIterations = 20;
    private String authProperty;
    private String hubProperty;

    public DataScienceOperator.ValueSortingLogic getHubScoreSortingLogic() {
        return hubValueSortingLogic;
    }

    public void setHubScoreSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.hubValueSortingLogic = valueSortingLogic;
    }

    public DataScienceOperator.ValueSortingLogic getAuthScoreSortingLogic() {
        return authValueSortingLogic;
    }

    public void setAuthScoreSortingLogic(DataScienceOperator.ValueSortingLogic valueSortingLogic) {
        this.authValueSortingLogic = valueSortingLogic;
    }

    public int getHitsIterations() {
        return hitsIterations;
    }

    public void setHitsIterations(int hitsIterations) {
        this.hitsIterations = hitsIterations;
    }

    public String getAuthProperty() {
        return authProperty;
    }

    public void setAuthProperty(String authProperty) {
        this.authProperty = authProperty;
    }

    public String getHubProperty() {
        return hubProperty;
    }

    public void setHubProperty(String hubProperty) {
        this.hubProperty = hubProperty;
    }
}