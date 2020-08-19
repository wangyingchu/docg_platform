package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;

public interface RelationEntity extends AttributesMeasurable {
    public String getRelationEntityUID();
    public String getRelationKindName();
    public String getFromConceptionEntityUID();
    public String getToConceptionEntityUID();
}
