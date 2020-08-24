package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;

import java.util.List;

public interface Neo4JConceptionEntity extends ConceptionEntity,AttributesMeasurable {
    public String getConceptionEntityUID();
    public String getConceptionKindName();
    public List<String> getAllConceptionKindNames();
}
