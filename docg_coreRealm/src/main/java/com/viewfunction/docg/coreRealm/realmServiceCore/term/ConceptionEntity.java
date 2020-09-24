package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.EntityRelationable;

import java.util.List;

public interface ConceptionEntity extends AttributesMeasurable, EntityRelationable {
    public String getConceptionEntityUID();
    public String getConceptionKindName();
    public List<String> getAllConceptionKindNames();
}
