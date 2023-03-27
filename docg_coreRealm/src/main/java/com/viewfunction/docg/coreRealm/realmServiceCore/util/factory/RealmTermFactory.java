package com.viewfunction.docg.coreRealm.realmServiceCore.util.factory;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.termImpl.ArcadeDBCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.util.ArcadeDBCoreRealmSystemUtil;
//import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.termImpl.NebulaGraphCoreRealmImpl;
//import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.util.NubulaGraphCoreRealmSystemUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.util.Neo4JCoreRealmSystemUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import java.util.Set;

public class RealmTermFactory {

    private static String _CORE_REALM_STORAGE_IMPL_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.CORE_REALM_STORAGE_IMPL_TECH);
    private static boolean supportMultiNeo4JGraph =
            Boolean.parseBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_SUPPORT_MULTI_GRAPH));

    public static CoreRealm getCoreRealm(String coreRealmName) throws CoreRealmFunctionNotSupportedException {
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            if(supportMultiNeo4JGraph){
                return new Neo4JCoreRealmImpl(coreRealmName);
            }else{
                CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
                exception.setCauseMessage("Current Neo4J storage implements doesn't support multi Realm");
                throw exception;
            }
        }else if(CoreRealmStorageImplTech.ARCADEDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return new ArcadeDBCoreRealmImpl(coreRealmName);
        }else if(CoreRealmStorageImplTech.NEBULAGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            //return new NebulaGraphCoreRealmImpl(coreRealmName);
        }else{
            return null;
        }
        return null;
    }

    public static CoreRealm getDefaultCoreRealm(){
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return Neo4JCoreRealmSystemUtil.getDefaultCoreRealm();
        }else if(CoreRealmStorageImplTech.ARCADEDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return ArcadeDBCoreRealmSystemUtil.getDefaultCoreRealm();
        }else if(CoreRealmStorageImplTech.NEBULAGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            //return NubulaGraphCoreRealmSystemUtil.getDefaultCoreRealm();
        }else{
            return null;
        }
        return null;
    }

    public static Set<String> listCoreRealms() throws CoreRealmFunctionNotSupportedException{
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return Neo4JCoreRealmSystemUtil.listCoreRealms();
        }else if(CoreRealmStorageImplTech.ARCADEDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return ArcadeDBCoreRealmSystemUtil.listCoreRealms();
        }else if(CoreRealmStorageImplTech.NEBULAGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            //return NubulaGraphCoreRealmSystemUtil.listCoreRealms();
        }else{
            return null;
        }
        return null;
    }

    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return Neo4JCoreRealmSystemUtil.createCoreRealm(coreRealmName);
        }else if(CoreRealmStorageImplTech.ARCADEDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return ArcadeDBCoreRealmSystemUtil.createCoreRealm(coreRealmName);
        }else if(CoreRealmStorageImplTech.NEBULAGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            //return NubulaGraphCoreRealmSystemUtil.createCoreRealm(coreRealmName);
        }else{
            return null;
        }
        return null;
    }
}
