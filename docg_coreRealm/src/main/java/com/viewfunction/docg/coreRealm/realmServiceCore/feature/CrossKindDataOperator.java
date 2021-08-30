package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public interface CrossKindDataOperator {

    public List<RelationEntity> getRelationInfoOfConceptionEntityPairs(List<String> conceptionEntityUIDs);
}
