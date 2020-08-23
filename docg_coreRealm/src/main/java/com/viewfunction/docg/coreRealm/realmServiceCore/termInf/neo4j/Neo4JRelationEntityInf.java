package com.viewfunction.docg.coreRealm.realmServiceCore.termInf.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;

public interface Neo4JRelationEntityInf extends AttributesMeasurable {
    public String getRelationEntityUID();
    public String getRelationKindName();
    public String getFromConceptionEntityUID();
    public String getToConceptionEntityUID();
}
