package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;

public interface CoreRealm {
    public CoreRealmStorageImplTech getStorageImplTech();
    public ConceptionKind getConceptionKind(String conceptionKindName);
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc);
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc,String parentConceptionKindName)
            throws CoreRealmFunctionNotSupportedException;
    public boolean removeConceptionKind(String conceptionKindName,boolean deleteExistEntities) throws CoreRealmServiceRuntimeException;

    public AttributesViewKind getAttributesViewKind(String attributesViewKindUID);
    public AttributesViewKind createAttributesViewKind(String attributesViewKindName,String attributesViewKindDesc, AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm);
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;

    public AttributeKind getAttributeKind(String attributeKindUID);
    public AttributeKind createAttributeKind(String attributeKindName,String attributeKindDesc, AttributeDataType attributeDataType);
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;
}
