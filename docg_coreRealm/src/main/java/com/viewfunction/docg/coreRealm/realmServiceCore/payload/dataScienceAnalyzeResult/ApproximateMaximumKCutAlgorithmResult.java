package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.ApproximateMaximumKCutAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApproximateMaximumKCutAlgorithmResult {

    private String graphName;
    private ApproximateMaximumKCutAlgorithmConfig approximateMaximumKCutAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<CommunityDetectionResult> communityDetectionResults;

    public ApproximateMaximumKCutAlgorithmResult(String graphName, ApproximateMaximumKCutAlgorithmConfig approximateMaximumKCutAlgorithmConfig){
        this.graphName = graphName;
        this.approximateMaximumKCutAlgorithmConfig = approximateMaximumKCutAlgorithmConfig;
        this.communityDetectionResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public ApproximateMaximumKCutAlgorithmConfig getApproximateMaximumKCutAlgorithmConfig() {
        return approximateMaximumKCutAlgorithmConfig;
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

    public List<CommunityDetectionResult> getApproximateMaximumKCutCommunities() {
        return communityDetectionResults;
    }
}
