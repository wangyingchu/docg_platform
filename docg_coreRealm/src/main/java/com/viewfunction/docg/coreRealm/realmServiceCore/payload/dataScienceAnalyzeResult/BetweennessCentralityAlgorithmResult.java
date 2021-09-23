package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.BetweennessCentralityAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BetweennessCentralityAlgorithmResult {

    private String graphName;
    private BetweennessCentralityAlgorithmConfig betweennessCentralityAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<EntityAnalyzeResult> betweennessCentralityScores;

    public BetweennessCentralityAlgorithmResult(String graphName, BetweennessCentralityAlgorithmConfig betweennessCentralityAlgorithmConfig){
        this.graphName = graphName;
        this.betweennessCentralityAlgorithmConfig = betweennessCentralityAlgorithmConfig;
        this.betweennessCentralityScores = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public BetweennessCentralityAlgorithmConfig getBetweennessCentralityAlgorithmConfig() {
        return betweennessCentralityAlgorithmConfig;
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

    public List<EntityAnalyzeResult> getBetweennessCentralityScores() {
        return betweennessCentralityScores;
    }
}
