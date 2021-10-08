package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.ClosenessCentralityAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClosenessCentralityAlgorithmResult {

    private String graphName;
    private ClosenessCentralityAlgorithmConfig closenessCentralityAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<EntityAnalyzeResult> closenessCentralityWeights;

    public ClosenessCentralityAlgorithmResult(String graphName, ClosenessCentralityAlgorithmConfig closenessCentralityAlgorithmConfig){
        this.graphName = graphName;
        this.closenessCentralityAlgorithmConfig = closenessCentralityAlgorithmConfig;
        this.closenessCentralityWeights = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public ClosenessCentralityAlgorithmConfig getClosenessCentralityAlgorithmConfig() {
        return closenessCentralityAlgorithmConfig;
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

    public List<EntityAnalyzeResult> getClosenessCentralityWeights() {
        return closenessCentralityWeights;
    }
}
