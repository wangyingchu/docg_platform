package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JMetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JMetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;

public interface Neo4JRelationKind extends RelationKind, Neo4JMetaConfigItemFeatureSupportable, Neo4JMetaAttributeFeatureSupportable, Neo4JClassificationKindAttachable {
}