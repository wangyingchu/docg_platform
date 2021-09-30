package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.AStarShortestPathAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AStarShortestPathAlgorithmResult {

    private String graphName;
    private AStarShortestPathAlgorithmConfig aStarShortestPathAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<PathFindingResult> pathFindingResults;

    public AStarShortestPathAlgorithmResult(String graphName, AStarShortestPathAlgorithmConfig aStarShortestPathAlgorithmConfig){
        this.graphName = graphName;
        this.aStarShortestPathAlgorithmConfig = aStarShortestPathAlgorithmConfig;
        this.pathFindingResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public AStarShortestPathAlgorithmConfig getAStarShortestPathAlgorithmConfig() {
        return aStarShortestPathAlgorithmConfig;
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

    public List<PathFindingResult> getAStarPaths() {
        return pathFindingResults;
    }
}
