package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.ArticleRankAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticleRankAlgorithmResult {

    private String graphName;
    private ArticleRankAlgorithmConfig articleRankAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<EntityAnalyzeResult> articleRankScores;

    public ArticleRankAlgorithmResult(String graphName,ArticleRankAlgorithmConfig articleRankAlgorithmConfig){
        this.graphName = graphName;
        this.articleRankAlgorithmConfig = articleRankAlgorithmConfig;
        this.articleRankScores = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
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

    public ArticleRankAlgorithmConfig getArticleRankAlgorithmConfig() {
        return articleRankAlgorithmConfig;
    }

    public List<EntityAnalyzeResult> getArticleRankScores() {
        return articleRankScores;
    }
}
