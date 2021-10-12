package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.GreedyInfluenceMaximizationAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GreedyInfluenceMaximizationAlgorithmResult {

    private String graphName;
    private GreedyInfluenceMaximizationAlgorithmConfig greedyInfluenceMaximizationAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<EntityAnalyzeResult> influenceMaximizationSpreads;

    public GreedyInfluenceMaximizationAlgorithmResult(String graphName, GreedyInfluenceMaximizationAlgorithmConfig greedyInfluenceMaximizationAlgorithmConfig){
        this.graphName = graphName;
        this.greedyInfluenceMaximizationAlgorithmConfig = greedyInfluenceMaximizationAlgorithmConfig;
        this.influenceMaximizationSpreads = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public GreedyInfluenceMaximizationAlgorithmConfig getGreedyInfluenceMaximizationAlgorithmConfig() {
        return greedyInfluenceMaximizationAlgorithmConfig;
    }

    public Date getAlgorithmExecuteStartTime() {
        return algorithmExecuteStartTime;
    }

    public Date getAlgorithmExecuteEndTime() {
        return algorithmExecuteEndTime;
    }

    public void setAlgorithmExecuteEndTime(Date algorithmExecuteEndTime) {
        this.algorithmExecuteEndTime = algorithmExecuteEndTime;
    }

    public List<EntityAnalyzeResult> getInfluenceMaximizationSpreads() {
        return influenceMaximizationSpreads;
    }
}
