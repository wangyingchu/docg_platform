package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.AllPairsShortestPathAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllPairsShortestPathAlgorithmResult {

    private String graphName;
    private AllPairsShortestPathAlgorithmConfig allPairsShortestPathAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<DistanceDetectionResult> allPairsShortestPathDistances;

    public AllPairsShortestPathAlgorithmResult(String graphName, AllPairsShortestPathAlgorithmConfig allPairsShortestPathAlgorithmConfig){
        this.graphName = graphName;
        this.allPairsShortestPathAlgorithmConfig = allPairsShortestPathAlgorithmConfig;
        this.allPairsShortestPathDistances = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public AllPairsShortestPathAlgorithmConfig getAllPairsShortestPathAlgorithmConfig() {
        return allPairsShortestPathAlgorithmConfig;
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

    public List<DistanceDetectionResult> getAllPairsShortestPathDistances() {
        return allPairsShortestPathDistances;
    }
}
