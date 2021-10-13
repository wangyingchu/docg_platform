package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.ModularityOptimizationAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModularityOptimizationAlgorithmResult {

    private String graphName;
    private ModularityOptimizationAlgorithmConfig modularityOptimizationAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<CommunityDetectionResult> communityDetectionResults;

    public ModularityOptimizationAlgorithmResult(String graphName, ModularityOptimizationAlgorithmConfig modularityOptimizationAlgorithmConfig){
        this.graphName = graphName;
        this.modularityOptimizationAlgorithmConfig = modularityOptimizationAlgorithmConfig;
        this.communityDetectionResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public ModularityOptimizationAlgorithmConfig getModularityOptimizationAlgorithmConfig() {
        return modularityOptimizationAlgorithmConfig;
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

    public List<CommunityDetectionResult> getModularityOptimizationCommunities() {
        return communityDetectionResults;
    }
}
