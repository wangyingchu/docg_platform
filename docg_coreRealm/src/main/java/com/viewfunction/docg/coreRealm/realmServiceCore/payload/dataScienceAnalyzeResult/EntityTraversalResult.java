package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;

import java.util.List;

public class EntityTraversalResult {

    private String startConceptionEntityUID;
    private List<String> entityTraversalFootprints;
    private List<ConceptionEntity> traveledEntities;

    public EntityTraversalResult(String startConceptionEntityUID,List<String> entityTraversalFootprints,
                                 List<ConceptionEntity> traveledEntities){
        this.startConceptionEntityUID = startConceptionEntityUID;
        this.entityTraversalFootprints = entityTraversalFootprints;
        this.traveledEntities = traveledEntities;
    }

    public String getStartConceptionEntityUID() {
        return startConceptionEntityUID;
    }

    public List<String> getEntityTraversalFootprints() {
        return entityTraversalFootprints;
    }

    public List<ConceptionEntity> getTraveledEntities() {
        return traveledEntities;
    }
}
