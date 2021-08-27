package com.viewfunction.docg.coreRealm.realmServiceCore.structure;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.LinkedList;

public class EntitiesPath {

    private float pathWeight = Float.NaN;
    private String startConceptionEntityType;
    private String startConceptionEntityUID;
    private String endConceptionEntityType;
    private String endConceptionEntityUID;
    private int pathJumps;
    private LinkedList<ConceptionEntity> pathConceptionEntities;
    private LinkedList<RelationEntity> pathRelationEntities;

    public EntitiesPath(String startConceptionEntityType, String startConceptionEntityUID, String endConceptionEntityType, String endConceptionEntityUID,
                        int pathJumps, LinkedList<ConceptionEntity> pathConceptionEntities, LinkedList<RelationEntity> pathRelationEntities){
        this.startConceptionEntityType = startConceptionEntityType;
        this.startConceptionEntityUID = startConceptionEntityUID;
        this.endConceptionEntityType = endConceptionEntityType;
        this.endConceptionEntityUID = endConceptionEntityUID;
        this.pathJumps = pathJumps;
        this.pathConceptionEntities = pathConceptionEntities;
        this.pathRelationEntities = pathRelationEntities;
    }

    public EntitiesPath(String startConceptionEntityType, String startConceptionEntityUID, String endConceptionEntityType, String endConceptionEntityUID,
                        int pathJumps, LinkedList<ConceptionEntity> pathConceptionEntities, LinkedList<RelationEntity> pathRelationEntities,float pathWeight){
        this.startConceptionEntityType = startConceptionEntityType;
        this.startConceptionEntityUID = startConceptionEntityUID;
        this.endConceptionEntityType = endConceptionEntityType;
        this.endConceptionEntityUID = endConceptionEntityUID;
        this.pathJumps = pathJumps;
        this.pathConceptionEntities = pathConceptionEntities;
        this.pathRelationEntities = pathRelationEntities;
        this.pathWeight = pathWeight;
    }

    public String getStartConceptionEntityType() {
        return startConceptionEntityType;
    }

    public String getStartConceptionEntityUID() {
        return startConceptionEntityUID;
    }

    public String getEndConceptionEntityType() {
        return endConceptionEntityType;
    }

    public String getEndConceptionEntityUID() {
        return endConceptionEntityUID;
    }

    public int getPathJumps() {
        return pathJumps;
    }

    public LinkedList<ConceptionEntity> getPathConceptionEntities() {
        return pathConceptionEntities;
    }

    public LinkedList<RelationEntity> getPathRelationEntities() {
        return pathRelationEntities;
    }

    public float getPathWeight() {
        return this.pathWeight;
    }
}
