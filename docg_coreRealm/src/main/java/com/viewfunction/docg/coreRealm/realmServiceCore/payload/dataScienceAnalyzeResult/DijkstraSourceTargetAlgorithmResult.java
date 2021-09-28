package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.DijkstraSourceTargetAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DijkstraSourceTargetAlgorithmResult {

    private String graphName;
    private DijkstraSourceTargetAlgorithmConfig dijkstraSourceTargetAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<PathFindingResult> pathFindingResults;

    public DijkstraSourceTargetAlgorithmResult(String graphName, DijkstraSourceTargetAlgorithmConfig dijkstraSourceTargetAlgorithmConfig){
        this.graphName = graphName;
        this.dijkstraSourceTargetAlgorithmConfig = dijkstraSourceTargetAlgorithmConfig;
        this.pathFindingResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public DijkstraSourceTargetAlgorithmConfig getDijkstraSourceTargetAlgorithmConfig() {
        return dijkstraSourceTargetAlgorithmConfig;
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

    public List<PathFindingResult> getDijkstraSourceTargetPaths() {
        return pathFindingResults;
    }
}
