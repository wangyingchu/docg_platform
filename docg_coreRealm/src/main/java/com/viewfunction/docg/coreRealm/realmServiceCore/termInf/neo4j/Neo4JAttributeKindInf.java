package com.viewfunction.docg.coreRealm.realmServiceCore.termInf.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JMetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JMetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;

import java.util.List;

public interface Neo4JAttributeKindInf extends AttributeKind,Neo4JMetaConfigItemFeatureSupportable, Neo4JMetaAttributeFeatureSupportable, Neo4JClassificationKindAttachable {

}
