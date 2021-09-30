package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.DijkstraSingleSourceAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DijkstraSingleSourceAlgorithmResult {

    private String graphName;
    private DijkstraSingleSourceAlgorithmConfig dijkstraSingleSourceAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<PathFindingResult> pathFindingResults;

    public DijkstraSingleSourceAlgorithmResult(String graphName, DijkstraSingleSourceAlgorithmConfig dijkstraSingleSourceAlgorithmConfig){
        this.graphName = graphName;
        this.dijkstraSingleSourceAlgorithmConfig = dijkstraSingleSourceAlgorithmConfig;
        this.pathFindingResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public DijkstraSingleSourceAlgorithmConfig getDijkstraSingleSourceAlgorithmConfig() {
        return dijkstraSingleSourceAlgorithmConfig;
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

    public List<PathFindingResult> getDijkstraSingleSourcePaths() {
        return pathFindingResults;
    }
}
