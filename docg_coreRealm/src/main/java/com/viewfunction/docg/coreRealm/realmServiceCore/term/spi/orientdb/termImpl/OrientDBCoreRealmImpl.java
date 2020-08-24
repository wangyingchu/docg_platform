package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.orientdb.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;

public class OrientDBCoreRealmImpl implements CoreRealm {

    public OrientDBCoreRealmImpl(String coreRealmName){}

    public OrientDBCoreRealmImpl(){}

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.ORIENTDB;
    }

    @Override
    public ConceptionKind getConceptionKind(String conceptionKindName) {
        return null;
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc) {
        return null;
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName, String conceptionKindDesc,String parentConceptionKindName) {
        return null;
    }

    @Override
    public boolean removeConceptionKind(String conceptionKindName, boolean deleteExistEntities) {
        return false;
    }

    @Override
    public AttributesViewKind getAttributesViewKind(String attributesViewKindUID) {
        return null;
    }

    @Override
    public AttributesViewKind createAttributesViewKind(String attributesViewKindName, String attributesViewKindDesc, AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm) {
        return null;
    }

    @Override
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public AttributeKind getAttributeKind(String attributeKindUID) {
        return null;
    }

    @Override
    public AttributeKind createAttributeKind(String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType) {
        return null;
    }

    @Override
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public RelationKind getRelationKind(String relationKindName) {
        return null;
    }

    @Override
    public RelationKind createRelationKind(String relationKindName, String relationKindDesc) {
        return null;
    }

    @Override
    public RelationKind createRelationKind(String relationKindName, String relationKindDesc, String parentRelationKindName) throws CoreRealmFunctionNotSupportedException {
        return null;
    }

    @Override
    public boolean removeRelationKind(String relationKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException {
        return false;
    }
}
