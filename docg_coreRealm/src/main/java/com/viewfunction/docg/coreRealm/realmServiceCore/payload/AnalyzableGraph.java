package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class AnalyzableGraph {

    private String graphName;

    public AnalyzableGraph(String graphName){
        this.graphName = graphName;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
}
