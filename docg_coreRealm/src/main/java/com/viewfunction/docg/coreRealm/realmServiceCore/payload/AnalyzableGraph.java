package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;
import java.util.Set;

public class AnalyzableGraph {

    private String graphName;
    private GraphDegreeDistributionInfo graphDegreeDistribution;
    private long conceptionEntityCount;
    private long relationEntityCount;
    private float graphDensity;
    private Date createTime;
    private Date lastModifyTime;
    private Set<String> containsConceptionKinds;
    private Set<String> containsRelationKinds;

    public AnalyzableGraph(String graphName,Date createTime){
        this.graphName = graphName;
        this.createTime = createTime;
    }

    public String getGraphName() {
        return graphName;
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

    public Set<String> getContainsConceptionKinds() {
        return containsConceptionKinds;
    }

    public void setContainsConceptionKinds(Set<String> containsConceptionKinds) {
        this.containsConceptionKinds = containsConceptionKinds;
    }

    public Set<String> getContainsRelationKinds() {
        return containsRelationKinds;
    }

    public void setContainsRelationKinds(Set<String> containsRelationKinds) {
        this.containsRelationKinds = containsRelationKinds;
    }
}
