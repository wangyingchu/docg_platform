package com.viewfunction.docg.coreRealm.realmServiceCore.termInf.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;

import java.util.List;

public interface Neo4JConceptionEntityInf extends AttributesMeasurable {
    public String getConceptionEntityUID();
    public String getConceptionKindName();
    public List<String> getAllConceptionKindNames();
}
