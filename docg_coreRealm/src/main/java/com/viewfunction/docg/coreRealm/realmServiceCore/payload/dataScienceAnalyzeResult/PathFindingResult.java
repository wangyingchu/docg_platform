package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesPath;

import java.util.List;

public class PathFindingResult {

    private String sourceConceptionEntityUID;
    private String targetConceptionEntityUID;
    private double totalCost;
    private List<String> pathConceptionEntityUIDs;
    private List<Double> entityTraversalCosts;


    private EntitiesPath entitiesPath;

    public PathFindingResult(String sourceConceptionEntityUID,String targetConceptionEntityUID,double totalCost,
                             List<String> pathConceptionEntityUIDs,List<Double> entityTraversalCosts,EntitiesPath entitiesPath){
        this.sourceConceptionEntityUID = sourceConceptionEntityUID;
        this.targetConceptionEntityUID = targetConceptionEntityUID;
        this.totalCost = totalCost;
        this.pathConceptionEntityUIDs = pathConceptionEntityUIDs;
        this.entityTraversalCosts = entityTraversalCosts;
        this.entitiesPath = entitiesPath;
    }

    public String getSourceConceptionEntityUID() {
        return sourceConceptionEntityUID;
    }

    public String getTargetConceptionEntityUID() {
        return targetConceptionEntityUID;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<String> getPathConceptionEntityUIDs() {
        return pathConceptionEntityUIDs;
    }

    public List<Double> getEntityTraversalCosts() {
        return entityTraversalCosts;
    }

    public EntitiesPath getEntitiesPath() {
        return entitiesPath;
    }
}
