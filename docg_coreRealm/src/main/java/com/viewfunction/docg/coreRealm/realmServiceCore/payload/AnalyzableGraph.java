package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;
import java.util.List;

public class AnalyzableGraph {

    private String graphName;
    private GraphDegreeDistributionInfo graphDegreeDistribution;
    private long conceptionEntityCount;
    private long relationEntityCount;
    private float graphDensity;
    private Date createTime;
    private Date lastModifyTime;
    private List<String> containsConceptionKinds;
    private List<String> containsRelationKinds;

    public AnalyzableGraph(String graphName,Date createTime){
        this.graphName = graphName;
        this.createTime = createTime;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public GraphDegreeDistributionInfo getGraphDegreeDistribution() {
        return graphDegreeDistribution;
    }

    public void setGraphDegreeDistribution(GraphDegreeDistributionInfo graphDegreeDistribution) {
        this.graphDegreeDistribution = graphDegreeDistribution;
    }

    public long getConceptionEntityCount() {
        return conceptionEntityCount;
    }

    public void setConceptionEntityCount(long conceptionEntityCount) {
        this.conceptionEntityCount = conceptionEntityCount;
    }

    public long getRelationEntityCount() {
        return relationEntityCount;
    }

    public void setRelationEntityCount(long relationEntityCount) {
        this.relationEntityCount = relationEntityCount;
    }

    public float getGraphDensity() {
        return graphDensity;
    }

    public void setGraphDensity(float graphDensity) {
        this.graphDensity = graphDensity;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public List<String> getContainsConceptionKinds() {
        return containsConceptionKinds;
    }

    public void setContainsConceptionKinds(List<String> containsConceptionKinds) {
        this.containsConceptionKinds = containsConceptionKinds;
    }

    public List<String> getContainsRelationKinds() {
        return containsRelationKinds;
    }

    public void setContainsRelationKinds(List<String> containsRelationKinds) {
        this.containsRelationKinds = containsRelationKinds;
    }
}
