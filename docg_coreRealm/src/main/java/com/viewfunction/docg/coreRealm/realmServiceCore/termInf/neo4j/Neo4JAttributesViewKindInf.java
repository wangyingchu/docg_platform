package com.viewfunction.docg.coreRealm.realmServiceCore.termInf.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JMetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j.Neo4JMetaConfigItemFeatureSupportable;

import java.util.List;

public interface Neo4JAttributesViewKindInf extends Neo4JMetaConfigItemFeatureSupportable, Neo4JMetaAttributeFeatureSupportable, Neo4JClassificationKindAttachable {
    public enum AttributesViewKindDataForm {SINGLE_VALUE, LIST_VALUE, RELATED_VALUE, EXTERNAL_VALUE}
    public String getAttributesViewKindUID();
    public String getAttributesViewKindName();
    public String getAttributesViewKindDesc();
    public boolean isCollectionAttributesViewKind();
    public AttributesViewKindDataForm getAttributesViewKindDataForm();
    public boolean addAttributeKind(String attributeKindUID);
    public boolean addAttributeKind(Neo4JAttributeKindInf attributeKind);
    public boolean removeAttributeKind(String attributeKindUID);
    public List<Neo4JAttributeKindInf> getContainsAttributeKinds();
    public List<Neo4JConceptionKindInf> getContainerConceptionKinds();
}
