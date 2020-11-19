package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.EntityRelationable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MultiKindsSupportable;

import java.util.List;

public interface ConceptionEntity extends AttributesMeasurable, EntityRelationable, ClassificationAttachable, MultiKindsSupportable {
    public String getConceptionEntityUID();
    public String getConceptionKindName();
    public List<String> getAllConceptionKindNames();
}
