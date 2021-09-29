package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;

import java.util.List;
import java.util.Map;

public class PathFindingResult {

    private String startConceptionEntityUID;
    private String endConceptionEntityUID;
    private String startConceptionEntityType;
    private String endConceptionEntityType;
    private double pathWeight;
    private List<String> pathConceptionEntityUIDs;
    private Map<String,Double> pathConceptionEntityTraversalWeights;
    private List<ConceptionEntity> pathConceptionEntities;

    public PathFindingResult(String startConceptionEntityUID, String startConceptionEntityType, String endConceptionEntityUID,
                             String endConceptionEntityType, double pathWeight, List<String> pathConceptionEntityUIDs,
                             Map<String,Double> pathConceptionEntityTraversalWeights, List<ConceptionEntity> pathConceptionEntities){
        this.startConceptionEntityUID = startConceptionEntityUID;
        this.startConceptionEntityType = startConceptionEntityType;
        this.endConceptionEntityUID = endConceptionEntityUID;
        this.endConceptionEntityType = endConceptionEntityType;
        this.pathWeight = pathWeight;
        this.pathConceptionEntityUIDs = pathConceptionEntityUIDs;
        this.pathConceptionEntityTraversalWeights = pathConceptionEntityTraversalWeights;
        this.pathConceptionEntities = pathConceptionEntities;
    }

    public String getStartConceptionEntityUID() {
        return startConceptionEntityUID;
    }

    public String getEndConceptionEntityUID() {
        return endConceptionEntityUID;
    }

    public String getStartConceptionEntityType() {
        return startConceptionEntityType;
    }

    public String getEndConceptionEntityType() {
        return endConceptionEntityType;
    }

    public double getPathWeight() {
        return pathWeight;
    }

    public List<String> getPathConceptionEntityUIDs() {
        return pathConceptionEntityUIDs;
    }

    public Map<String, Double> getPathConceptionEntityTraversalWeights() {
        return pathConceptionEntityTraversalWeights;
    }

    public List<ConceptionEntity> getPathConceptionEntities() {
        return pathConceptionEntities;
    }
}
