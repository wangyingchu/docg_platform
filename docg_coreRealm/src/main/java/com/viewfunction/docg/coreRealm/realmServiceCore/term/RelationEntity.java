package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;

public interface RelationEntity extends AttributesMeasurable {
    public String getRelationEntityUID();
    public String getRelationKindName();
    public String getFromConceptionEntityUID() throws CoreRealmServiceRuntimeException;
    public String getToConceptionEntityUID() throws CoreRealmServiceRuntimeException;
}
