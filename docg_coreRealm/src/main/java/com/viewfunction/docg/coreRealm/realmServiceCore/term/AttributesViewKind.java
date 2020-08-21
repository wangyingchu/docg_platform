package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JMetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JMetaConfigItemFeatureSupportable;

import java.util.List;

public interface AttributesViewKind  extends Neo4JMetaConfigItemFeatureSupportable, Neo4JMetaAttributeFeatureSupportable, Neo4JClassificationKindAttachable {
    public enum AttributesViewKindDataForm {SINGLE_VALUE, LIST_VALUE, RELATED_VALUE, EXTERNAL_VALUE}
    public String getAttributesViewKindUID();
    public String getAttributesViewKindName();
    public String getAttributesViewKindDesc();
    public boolean isCollectionAttributesViewKind();
    public AttributesViewKindDataForm getAttributesViewKindDataForm();
    public boolean addAttributeKind(String attributeKindUID);
    public boolean addAttributeKind(AttributeKind attributeKind);
    public boolean removeAttributeKind(String attributeKindUID);
    public List<AttributeKind> getContainsAttributeKinds();
    public List<ConceptionKind> getContainerConceptionKinds();
}
