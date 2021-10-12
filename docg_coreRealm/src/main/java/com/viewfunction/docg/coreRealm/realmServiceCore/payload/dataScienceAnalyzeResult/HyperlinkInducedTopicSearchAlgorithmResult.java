package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.HyperlinkInducedTopicSearchAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HyperlinkInducedTopicSearchAlgorithmResult {

    private String graphName;
    private HyperlinkInducedTopicSearchAlgorithmConfig hyperlinkInducedTopicSearchAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<HITSDetectionResult> hitsScores;

    public HyperlinkInducedTopicSearchAlgorithmResult(String graphName, HyperlinkInducedTopicSearchAlgorithmConfig hyperlinkInducedTopicSearchAlgorithmConfig){
        this.graphName = graphName;
        this.hyperlinkInducedTopicSearchAlgorithmConfig = hyperlinkInducedTopicSearchAlgorithmConfig;
        this.hitsScores = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public HyperlinkInducedTopicSearchAlgorithmConfig getHyperlinkInducedTopicSearchAlgorithmConfig() {
        return hyperlinkInducedTopicSearchAlgorithmConfig;
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

    public List<HITSDetectionResult> getHITSScores() {
        return hitsScores;
    }
}
