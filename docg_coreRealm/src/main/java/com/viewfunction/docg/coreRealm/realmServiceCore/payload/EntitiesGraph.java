package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public class EntitiesGraph {

    private List<ConceptionEntity> graphConceptionEntities;
    private List<RelationEntity> graphRelationEntities;

    public EntitiesGraph(List<ConceptionEntity> graphConceptionEntities,List<RelationEntity> graphRelationEntities){
        this.graphConceptionEntities = graphConceptionEntities;
        this.graphRelationEntities = graphRelationEntities;
    }

    public List<ConceptionEntity> getGraphConceptionEntities() {
        return graphConceptionEntities;
    }

    public List<RelationEntity> getGraphRelationEntities() {
        return graphRelationEntities;
    }
}
