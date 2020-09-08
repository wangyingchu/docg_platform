package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;

import java.util.List;

public interface AttributesViewKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable {
    public enum AttributesViewKindDataForm {SINGLE_VALUE, LIST_VALUE, RELATED_VALUE, EXTERNAL_VALUE}
    public String getAttributesViewKindUID();
    public String getAttributesViewKindName();
    public String getAttributesViewKindDesc();
    public boolean isCollectionAttributesViewKind();
    public AttributesViewKindDataForm getAttributesViewKindDataForm();
    public boolean attachAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;
    public boolean detachAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;
    public List<AttributeKind> getContainsAttributeKinds();
    public List<ConceptionKind> getContainerConceptionKinds();
}
