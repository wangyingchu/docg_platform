package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;

import java.util.List;

public interface AttributesViewKind  extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationKindAttachable {
    public enum AttributesViewKindDataForm {SINGLE_VALUE, LIST_VALUE, RELATED_VALUE, EXTERNAL_VALUE}
    public String getAttributesViewKindUID();
    public String getAttributesViewKindName();
    public String getAttributesViewKindDesc();
    public boolean isCollectionAttributesViewKind();
    public AttributesViewKindDataForm getAttributesViewKindDataForm();
    public boolean addAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;
    public List<AttributeKind> getContainsAttributeKinds();
    public List<ConceptionKind> getContainerConceptionKinds();
}
