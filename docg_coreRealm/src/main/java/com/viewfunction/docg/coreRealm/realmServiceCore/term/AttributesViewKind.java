package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;

import java.util.List;
import java.util.Map;

public interface AttributesViewKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable {
    public enum AttributesViewKindDataForm {SINGLE_VALUE, LIST_VALUE, RELATED_VALUE, EXTERNAL_VALUE}
    public String getAttributesViewKindUID();
    public String getAttributesViewKindName();
    public String getAttributesViewKindDesc();
    public boolean isCollectionAttributesViewKind();
    public AttributesViewKindDataForm getAttributesViewKindDataForm();
    public boolean attachAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;
    public boolean attachAttributeKind(String attributeKindUID, Map<String,Object> properties) throws CoreRealmServiceRuntimeException;
    public List<String> setAttributeKindAttachMetaInfo(String attributeKindUID,Map<String,Object> properties);
    public boolean removeAttributeKindAttachMetaInfo(String attributeKindUID,String metaPropertyName) throws CoreRealmServiceRuntimeException;
    public Map<String,Object> getAttributeKindsAttachMetaInfo(String attributeKindUID,String metaPropertyName);
    public boolean detachAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;
    public List<AttributeKind> getContainsAttributeKinds();
    public List<ConceptionKind> getContainerConceptionKinds();
}
