package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.NodeSimilarityAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NodeSimilarityAlgorithmResult {

    private String graphName;
    private NodeSimilarityAlgorithmConfig nodeSimilarityAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<SimilarityDetectionResult> similarityDetectionResult;

    public NodeSimilarityAlgorithmResult(String graphName, NodeSimilarityAlgorithmConfig nodeSimilarityAlgorithmConfig){
        this.graphName = graphName;
        this.nodeSimilarityAlgorithmConfig = nodeSimilarityAlgorithmConfig;
        this.similarityDetectionResult = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public NodeSimilarityAlgorithmConfig getNodeSimilarityAlgorithmConfig() {
        return nodeSimilarityAlgorithmConfig;
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

    public List<SimilarityDetectionResult> getNodeSimilarityScores() {
        return similarityDetectionResult;
    }
}
