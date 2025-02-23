package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JMetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JMetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JStatisticalAndEvaluable;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JConceptionKind;

public interface MemGraphConceptionKind extends Neo4JConceptionKind,ConceptionKind, Neo4JMetaConfigItemFeatureSupportable, Neo4JMetaAttributeFeatureSupportable, Neo4JClassificationAttachable, Neo4JStatisticalAndEvaluable {
}
