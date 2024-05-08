package com.viewfunction.docg.coreRealm.realmServiceCore.util.factory;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.termImpl.ArcadeDBCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.util.ArcadeDBCoreRealmSystemUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import java.util.Set;

public class ADB_RealmTermFactory {
    private static String _CORE_REALM_STORAGE_IMPL_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.CORE_REALM_STORAGE_IMPL_TECH);

    public static CoreRealm getCoreRealm(String coreRealmName) throws CoreRealmFunctionNotSupportedException {
        if(CoreRealmStorageImplTech.ARCADEDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return new ArcadeDBCoreRealmImpl(coreRealmName);
        }else{
            return null;
        }
    }

    public static CoreRealm getDefaultCoreRealm(){
        if(CoreRealmStorageImplTech.ARCADEDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return ArcadeDBCoreRealmSystemUtil.getDefaultCoreRealm();
        }else{
            return null;
        }
    }

    public static Set<String> listCoreRealms() throws CoreRealmFunctionNotSupportedException{
        if(CoreRealmStorageImplTech.ARCADEDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return ArcadeDBCoreRealmSystemUtil.listCoreRealms();
        }else{
            return null;
        }
    }

    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {
        if(CoreRealmStorageImplTech.ARCADEDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return ArcadeDBCoreRealmSystemUtil.createCoreRealm(coreRealmName);
        }else{
            return null;
        }
    }
}
