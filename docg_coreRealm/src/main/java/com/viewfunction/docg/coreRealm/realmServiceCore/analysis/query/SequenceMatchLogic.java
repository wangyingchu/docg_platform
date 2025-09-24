package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import java.util.List;

public interface SequenceMatchLogic {

    public String getKindName();
    public AttributesParameters getEntityAttributesFilterParameter();
    public List<String> getReturnAttributeNames();
}
