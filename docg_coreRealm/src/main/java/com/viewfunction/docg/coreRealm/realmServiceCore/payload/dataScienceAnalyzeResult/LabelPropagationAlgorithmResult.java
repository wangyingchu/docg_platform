package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.LabelPropagationAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LabelPropagationAlgorithmResult {

    private String graphName;
    private LabelPropagationAlgorithmConfig labelPropagationAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<CommunityDetectionResult> communityDetectionResults;

    public LabelPropagationAlgorithmResult(String graphName, LabelPropagationAlgorithmConfig labelPropagationAlgorithmConfig){
        this.graphName = graphName;
        this.labelPropagationAlgorithmConfig = labelPropagationAlgorithmConfig;
        this.communityDetectionResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public LabelPropagationAlgorithmConfig getLabelPropagationAlgorithmConfig() {
        return labelPropagationAlgorithmConfig;
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

    public List<CommunityDetectionResult> getCommunityDetectionResults() {
        return communityDetectionResults;
    }
}
