package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

public class PageRankAlgorithmConfig extends DataScienceBaseAlgorithmConfig{

    public ScoreSortingLogic getScoreSortingLogic() {
        return scoreSortingLogic;
    }

    public void setScoreSortingLogic(ScoreSortingLogic scoreSortingLogic) {
        this.scoreSortingLogic = scoreSortingLogic;
    }

    public enum ScoreSortingLogic { ASC, DESC }

    private float dampingFactor = 0.85f;
    private int maxIterations = 20;
    private float tolerance = 0.0000001f;
    private String relationshipWeightAttribute;
    private String scoreScaler;
    private int pageSize;
    private int startPage;
    private int endPage;
    private int resultNumber;
    private ScoreSortingLogic scoreSortingLogic;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    public float getDampingFactor() {
        return dampingFactor;
    }

    public void setDampingFactor(float dampingFactor) {
        this.dampingFactor = dampingFactor;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public float getTolerance() {
        return tolerance;
    }

    public void setTolerance(float tolerance) {
        this.tolerance = tolerance;
    }

    public String getRelationshipWeightAttribute() {
        return relationshipWeightAttribute;
    }

    public void setRelationshipWeightAttribute(String relationshipWeightAttribute) {
        this.relationshipWeightAttribute = relationshipWeightAttribute;
    }

    public String getScoreScaler() {
        return scoreScaler;
    }

    public void setScoreScaler(String scoreScaler) {
        this.scoreScaler = scoreScaler;
    }
}
