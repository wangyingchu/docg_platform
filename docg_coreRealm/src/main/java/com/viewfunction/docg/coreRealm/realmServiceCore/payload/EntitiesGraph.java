package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitiesGraph {

    private List<ConceptionEntity> graphConceptionEntities;
    private List<RelationEntity> graphRelationEntities;
    private Map<String,Long> graphConceptionKindsDataStatistic;
    private Map<String,Long> graphRelationKindsDataStatistic;

    public EntitiesGraph(List<ConceptionEntity> graphConceptionEntities,List<RelationEntity> graphRelationEntities){
        this.graphConceptionEntities = graphConceptionEntities;
        this.graphRelationEntities = graphRelationEntities;
        this.graphConceptionKindsDataStatistic = new HashMap<>();
        this.graphRelationKindsDataStatistic = new HashMap<>();
    }

    public List<ConceptionEntity> getGraphConceptionEntities() {
        return graphConceptionEntities;
    }

    public List<RelationEntity> getGraphRelationEntities() {
        return graphRelationEntities;
    }

    public void countConceptionKindsData(String conceptionKindName){
        if(this.graphConceptionKindsDataStatistic.containsKey(conceptionKindName)){
            long currentCount = this.graphConceptionKindsDataStatistic.get(conceptionKindName);
            this.graphConceptionKindsDataStatistic.put(conceptionKindName,currentCount+1);
        }else{
            this.graphConceptionKindsDataStatistic.put(conceptionKindName,1l);
        }
    }

    public void countRelationKindsData(String relationKindName){
        if(this.graphRelationKindsDataStatistic.containsKey(relationKindName)){
            long currentCount = this.graphRelationKindsDataStatistic.get(relationKindName);
            this.graphRelationKindsDataStatistic.put(relationKindName,currentCount+1);
        }else{
            this.graphRelationKindsDataStatistic.put(relationKindName,1l);
        }
    }

    public Map<String, Long> getGraphConceptionKindsDataStatistic() {
        return this.graphConceptionKindsDataStatistic;
    }

    public Map<String, Long> getGraphRelationKindsDataStatistic() {
        return this.graphRelationKindsDataStatistic;
    }
}
