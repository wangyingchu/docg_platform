package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult.SimilarityDetectionResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KNearestNeighborsAlgorithmResult {

    private String graphName;
    private KNearestNeighborsAlgorithmConfig kNearestNeighborsAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<SimilarityDetectionResult> similarityDetectionResult;

    public KNearestNeighborsAlgorithmResult(String graphName, KNearestNeighborsAlgorithmConfig kNearestNeighborsAlgorithmConfig){
        this.graphName = graphName;
        this.kNearestNeighborsAlgorithmConfig = kNearestNeighborsAlgorithmConfig;
        this.similarityDetectionResult = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public KNearestNeighborsAlgorithmConfig getKNearestNeighborsAlgorithmConfig() {
        return kNearestNeighborsAlgorithmConfig;
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
