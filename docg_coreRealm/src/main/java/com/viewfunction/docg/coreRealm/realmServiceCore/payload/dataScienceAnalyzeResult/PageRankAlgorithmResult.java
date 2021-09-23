package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.PageRankAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PageRankAlgorithmResult {

    private String graphName;
    private PageRankAlgorithmConfig pageRankAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<EntityScoreInfo> pageRankScores;

    public PageRankAlgorithmResult(String graphName,PageRankAlgorithmConfig pageRankAlgorithmConfig){
        this.graphName = graphName;
        this.pageRankAlgorithmConfig = pageRankAlgorithmConfig;
        this.pageRankScores = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public PageRankAlgorithmConfig getPageRankAlgorithmConfig() {
        return pageRankAlgorithmConfig;
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

    public List<EntityScoreInfo> getPageRankScores() {
        return pageRankScores;
    }
}
