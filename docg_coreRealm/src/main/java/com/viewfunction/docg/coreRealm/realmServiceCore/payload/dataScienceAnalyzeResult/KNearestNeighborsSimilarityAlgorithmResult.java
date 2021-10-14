package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.KNearestNeighborsSimilarityAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KNearestNeighborsSimilarityAlgorithmResult {

    private String graphName;
    private KNearestNeighborsSimilarityAlgorithmConfig kNearestNeighborsSimilarityAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<SimilarityDetectionResult> similarityDetectionResult;

    public KNearestNeighborsSimilarityAlgorithmResult(String graphName, KNearestNeighborsSimilarityAlgorithmConfig kNearestNeighborsSimilarityAlgorithmConfig){
        this.graphName = graphName;
        this.kNearestNeighborsSimilarityAlgorithmConfig = kNearestNeighborsSimilarityAlgorithmConfig;
        this.similarityDetectionResult = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public KNearestNeighborsSimilarityAlgorithmConfig getKNearestNeighborsSimilarityAlgorithmConfig() {
        return kNearestNeighborsSimilarityAlgorithmConfig;
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
