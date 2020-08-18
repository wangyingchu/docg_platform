package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;

import java.util.List;

//public interface AttributeKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationKindAttachable{
public interface AttributeKind {
    public String getAttributeKindName();
    public String getAttributeKindUID();
    public String getAttributeKindDesc();
    public AttributeDataType getAttributeDataType();
    public List<AttributesViewKind> getContainerAttributesViewKinds();
}
