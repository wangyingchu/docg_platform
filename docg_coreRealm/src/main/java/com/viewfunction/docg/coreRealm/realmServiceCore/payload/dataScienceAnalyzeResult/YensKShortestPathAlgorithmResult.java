package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.YensKShortestPathAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YensKShortestPathAlgorithmResult {

    private String graphName;
    private YensKShortestPathAlgorithmConfig yensKShortestPathAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<PathFindingResult> pathFindingResults;

    public YensKShortestPathAlgorithmResult(String graphName, YensKShortestPathAlgorithmConfig yensKShortestPathAlgorithmConfig){
        this.graphName = graphName;
        this.yensKShortestPathAlgorithmConfig = yensKShortestPathAlgorithmConfig;
        this.pathFindingResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public YensKShortestPathAlgorithmConfig getYensKShortestPathAlgorithmConfig() {
        return yensKShortestPathAlgorithmConfig;
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

    public List<PathFindingResult> getYensKPaths() {
        return pathFindingResults;
    }
}
