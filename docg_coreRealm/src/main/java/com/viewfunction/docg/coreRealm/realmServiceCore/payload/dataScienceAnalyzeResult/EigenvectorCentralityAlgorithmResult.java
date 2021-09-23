package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.EigenvectorCentralityAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EigenvectorCentralityAlgorithmResult {

    private String graphName;
    private EigenvectorCentralityAlgorithmConfig eigenvectorCentralityAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<EntityAnalyzeResult> eigenvectorCentralityScores;

    public EigenvectorCentralityAlgorithmResult(String graphName, EigenvectorCentralityAlgorithmConfig eigenvectorCentralityAlgorithmConfig){
        this.graphName = graphName;
        this.eigenvectorCentralityAlgorithmConfig = eigenvectorCentralityAlgorithmConfig;
        this.eigenvectorCentralityScores = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public EigenvectorCentralityAlgorithmConfig getEigenvectorCentralityAlgorithmConfig() {
        return eigenvectorCentralityAlgorithmConfig;
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

    public List<EntityAnalyzeResult> getEigenvectorCentralityScores() {
        return eigenvectorCentralityScores;
    }
}
