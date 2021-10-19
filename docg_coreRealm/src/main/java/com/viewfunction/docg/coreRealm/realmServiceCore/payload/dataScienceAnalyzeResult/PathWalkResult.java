package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import java.util.List;

public class PathWalkResult {

    private String startConceptionEntityUID;
    private List<String> walkEntitiesFootprints;

    public PathWalkResult(String startConceptionEntityUID,List<String> walkEntitiesFootprints){
        this.startConceptionEntityUID = startConceptionEntityUID;
        this.walkEntitiesFootprints = walkEntitiesFootprints;
    }

    public String getStartConceptionEntityUID() {
        return startConceptionEntityUID;
    }

    public List<String> getWalkEntitiesFootprints() {
        return walkEntitiesFootprints;
    }
}
