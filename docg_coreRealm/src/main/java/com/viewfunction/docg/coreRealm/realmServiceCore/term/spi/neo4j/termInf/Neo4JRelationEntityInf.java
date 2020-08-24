package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;

public interface Neo4JRelationEntityInf extends AttributesMeasurable {
    public String getRelationEntityUID();
    public String getRelationKindName();
    public String getFromConceptionEntityUID();
    public String getToConceptionEntityUID();
}
