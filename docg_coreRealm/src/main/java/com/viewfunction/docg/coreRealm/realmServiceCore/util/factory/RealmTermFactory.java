package com.viewfunction.docg.coreRealm.realmServiceCore.util.factory;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl.MemGraphCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.util.MemGraphCoreRealmSystemUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.util.Neo4JCoreRealmSystemUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import java.util.Set;

public class RealmTermFactory {

    private static String _CORE_REALM_STORAGE_IMPL_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.CORE_REALM_STORAGE_IMPL_TECH);
    private static boolean supportMultiNeo4JGraph =
            Boolean.parseBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_SUPPORT_MULTI_GRAPH));
    private static boolean supportMultiMemGraphGraph =
            Boolean.parseBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.MEMGRAPH_SUPPORT_MULTI_GRAPH));

    public static CoreRealm getCoreRealm(String coreRealmName) throws CoreRealmFunctionNotSupportedException {
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            if(supportMultiNeo4JGraph){
                return new Neo4JCoreRealmImpl(coreRealmName);
            }else{
                CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
                exception.setCauseMessage("Current Neo4J storage implements doesn't support multi Realm");
                throw exception;
            }
        }else if(CoreRealmStorageImplTech.MEMGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            if(supportMultiMemGraphGraph){
                return new MemGraphCoreRealmImpl(coreRealmName);
            }else{
                CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
                exception.setCauseMessage("Current Neo4J storage implements doesn't support multi Realm");
                throw exception;
            }
        }else{
            return null;
        }
    }

    public static CoreRealm getDefaultCoreRealm(){
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return Neo4JCoreRealmSystemUtil.getDefaultCoreRealm();
        }else if(CoreRealmStorageImplTech.MEMGRAPH.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return MemGraphCoreRealmSystemUtil.getDefaultCoreRealm();
        }else{
            return null;
        }
    }

    public static Set<String> listCoreRealms() throws CoreRealmFunctionNotSupportedException{
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return Neo4JCoreRealmSystemUtil.listCoreRealms();
        }else{
            return null;
        }
    }

    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return Neo4JCoreRealmSystemUtil.createCoreRealm(coreRealmName);
        }else{
            return null;
        }
    }
}
