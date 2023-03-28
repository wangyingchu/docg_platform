package com.viewfunction.docg.coreRealm.realmServiceCore.util.factory;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.termImpl.NebulaGraphCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.util.NubulaGraphCoreRealmSystemUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import java.util.Set;

public class NG_RealmTermFactory {
    private static String _CORE_REALM_STORAGE_IMPL_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.CORE_REALM_STORAGE_IMPL_TECH);

    public static CoreRealm getCoreRealm(String coreRealmName) {
        if(CoreRealmStorageImplTech.NEBULAGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return new NebulaGraphCoreRealmImpl(coreRealmName);
        }else{
            return null;
        }
    }

    public static CoreRealm getDefaultCoreRealm(){
        if(CoreRealmStorageImplTech.NEBULAGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return NubulaGraphCoreRealmSystemUtil.getDefaultCoreRealm();
        }else{
            return null;
        }
    }

    public static Set<String> listCoreRealms() {
        if(CoreRealmStorageImplTech.NEBULAGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return NubulaGraphCoreRealmSystemUtil.listCoreRealms();
        }else{
            return null;
        }
    }

    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException {
        if(CoreRealmStorageImplTech.NEBULAGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return NubulaGraphCoreRealmSystemUtil.createCoreRealm(coreRealmName);
        }else{
            return null;
        }
    }
}
