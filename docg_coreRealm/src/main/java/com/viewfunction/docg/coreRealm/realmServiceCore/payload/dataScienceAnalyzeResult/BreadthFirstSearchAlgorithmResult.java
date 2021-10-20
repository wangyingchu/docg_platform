package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.BreadthFirstSearchAlgorithmConfig;

import java.util.Date;

public class BreadthFirstSearchAlgorithmResult {

    private EntityTraversalResult entityTraversalResult;

    private String graphName;
    private BreadthFirstSearchAlgorithmConfig breadthFirstSearchAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;

    public BreadthFirstSearchAlgorithmResult(String graphName, BreadthFirstSearchAlgorithmConfig breadthFirstSearchAlgorithmConfig){
        this.graphName = graphName;
        this.breadthFirstSearchAlgorithmConfig = breadthFirstSearchAlgorithmConfig;
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public BreadthFirstSearchAlgorithmConfig getBreadthFirstSearchAlgorithmConfig() {
        return breadthFirstSearchAlgorithmConfig;
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

    public EntityTraversalResult getEntityTraversalResult() {
        return entityTraversalResult;
    }

    public void setEntityTraversalResult(EntityTraversalResult entityTraversalResult) {
        this.entityTraversalResult = entityTraversalResult;
    }
}
