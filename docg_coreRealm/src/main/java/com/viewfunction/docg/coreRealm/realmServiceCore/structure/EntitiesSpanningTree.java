package com.viewfunction.docg.coreRealm.realmServiceCore.structure;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public class EntitiesSpanningTree {

    private String rootConceptionEntityType;
    private String rootConceptionEntityUID;
    private List<ConceptionEntity> treeConceptionEntities;
    private List<RelationEntity> treeRelationEntities;

    public EntitiesSpanningTree(String rootConceptionEntityType,String rootConceptionEntityUID,
                                List<ConceptionEntity> treeConceptionEntities,List<RelationEntity> treeRelationEntities){
        this.rootConceptionEntityType = rootConceptionEntityType;
        this.rootConceptionEntityUID = rootConceptionEntityUID;
        this.treeConceptionEntities = treeConceptionEntities;
        this.treeRelationEntities = treeRelationEntities;
    }

    public String getRootConceptionEntityType() {
        return rootConceptionEntityType;
    }

    public String getRootConceptionEntityUID() {
        return rootConceptionEntityUID;
    }

    public List<ConceptionEntity> getTreeConceptionEntities() {
        return treeConceptionEntities;
    }

    public List<RelationEntity> getTreeRelationEntities() {
        return treeRelationEntities;
    }
}