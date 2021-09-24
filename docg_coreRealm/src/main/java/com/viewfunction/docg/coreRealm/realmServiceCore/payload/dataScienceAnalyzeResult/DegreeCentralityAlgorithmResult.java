package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.DegreeCentralityAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DegreeCentralityAlgorithmResult {

    private String graphName;
    private DegreeCentralityAlgorithmConfig degreeCentralityAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<EntityAnalyzeResult> degreeCentralityScores;

    public DegreeCentralityAlgorithmResult(String graphName, DegreeCentralityAlgorithmConfig degreeCentralityAlgorithmConfig){
        this.graphName = graphName;
        this.degreeCentralityAlgorithmConfig = degreeCentralityAlgorithmConfig;
        this.degreeCentralityScores = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public DegreeCentralityAlgorithmConfig getDegreeCentralityAlgorithmConfig() {
        return degreeCentralityAlgorithmConfig;
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

    public List<EntityAnalyzeResult> getDegreeCentralityScores() {
        return degreeCentralityScores;
    }
}
