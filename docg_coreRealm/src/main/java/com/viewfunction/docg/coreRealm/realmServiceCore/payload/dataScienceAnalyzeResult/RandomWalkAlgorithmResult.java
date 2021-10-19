package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.RandomWalkAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RandomWalkAlgorithmResult {

    private String graphName;
    private RandomWalkAlgorithmConfig randomWalkAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<PathWalkResult> pathFindingResults;

    public RandomWalkAlgorithmResult(String graphName, RandomWalkAlgorithmConfig randomWalkAlgorithmConfig){
        this.graphName = graphName;
        this.randomWalkAlgorithmConfig = randomWalkAlgorithmConfig;
        this.pathFindingResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public RandomWalkAlgorithmConfig getRandomWalkAlgorithmConfig() {
        return randomWalkAlgorithmConfig;
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

    public List<PathWalkResult> getRandomWalkPaths() {
        return pathFindingResults;
    }
}
