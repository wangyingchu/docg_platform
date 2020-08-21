package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JMetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JMetaConfigItemFeatureSupportable;

import java.util.List;

public interface AttributeKind extends Neo4JMetaConfigItemFeatureSupportable, Neo4JMetaAttributeFeatureSupportable, Neo4JClassificationKindAttachable {
    public String getAttributeKindName();
    public String getAttributeKindUID();
    public String getAttributeKindDesc();
    public AttributeDataType getAttributeDataType();
    public List<AttributesViewKind> getContainerAttributesViewKinds();
}
