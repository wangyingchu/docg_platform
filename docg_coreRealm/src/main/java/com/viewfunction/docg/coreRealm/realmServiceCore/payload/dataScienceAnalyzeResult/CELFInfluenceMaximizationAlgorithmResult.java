package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.CELFInfluenceMaximizationAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CELFInfluenceMaximizationAlgorithmResult {

    private String graphName;
    private CELFInfluenceMaximizationAlgorithmConfig _CELFInfluenceMaximizationAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<EntityAnalyzeResult> influenceMaximizationSpreads;

    public CELFInfluenceMaximizationAlgorithmResult(String graphName, CELFInfluenceMaximizationAlgorithmConfig _CELFInfluenceMaximizationAlgorithmConfig){
        this.graphName = graphName;
        this._CELFInfluenceMaximizationAlgorithmConfig = _CELFInfluenceMaximizationAlgorithmConfig;
        this.influenceMaximizationSpreads = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public CELFInfluenceMaximizationAlgorithmConfig getCELFInfluenceMaximizationAlgorithmConfig() {
        return _CELFInfluenceMaximizationAlgorithmConfig;
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

    public List<EntityAnalyzeResult> getInfluenceMaximizationSpreads() {
        return influenceMaximizationSpreads;
    }
}
