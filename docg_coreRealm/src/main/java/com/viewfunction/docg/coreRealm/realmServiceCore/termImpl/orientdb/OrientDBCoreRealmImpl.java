package com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.orientdb;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
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
}
