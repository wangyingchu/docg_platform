package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.LinkedList;

public class ConceptionEntitiesPath {

    private String startEntityType;
    private String startEntityUID;
    private String endEntityType;
    private String endEntityUID;
    private int pathJumps;
    private LinkedList<ConceptionEntity> pathConceptionEntities;
    private LinkedList<RelationEntity> pathRelationEntities;

    public ConceptionEntitiesPath(String startEntityType,String startEntityUID,String endEntityType,String endEntityUID,
                                  int pathJumps,LinkedList<ConceptionEntity> pathConceptionEntities,LinkedList<RelationEntity> pathRelationEntities){
        this.startEntityType = startEntityType;
        this.startEntityUID = startEntityUID;
        this.endEntityType = endEntityType;
        this.endEntityUID = endEntityUID;
        this.pathJumps = pathJumps;
        this.pathConceptionEntities = pathConceptionEntities;
        this.pathRelationEntities = pathRelationEntities;
    }

    public String getStartEntityType() {
        return startEntityType;
    }

    public String getStartEntityUID() {
        return startEntityUID;
    }

    public String getEndEntityType() {
        return endEntityType;
    }

    public String getEndEntityUID() {
        return endEntityUID;
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
}
