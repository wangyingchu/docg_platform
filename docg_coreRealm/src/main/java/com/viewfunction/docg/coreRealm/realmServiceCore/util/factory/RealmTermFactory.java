package com.viewfunction.docg.coreRealm.realmServiceCore.util.factory;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.orientdb.OrientDBCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

public class RealmTermFactory {

    private static String _CORE_REALM_STORAGE_IMPL_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.CORE_REALM_STORAGE_IMPL_TECH);
    private static boolean supportMultiNeo4JGraph =
            Boolean.getBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_SUPPORT_MULTI_GRAPH));

    public static CoreRealm getCoreRealm(String coreRealmName) throws CoreRealmFunctionNotSupportedException {
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            if(supportMultiNeo4JGraph){
                return new Neo4JCoreRealmImpl(coreRealmName);
            }else{
                CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
                exception.setCauseMessage("Current Neo4J storage implements doesn't support multi Realm");
                throw exception;
            }
        }else if(CoreRealmStorageImplTech.ORIENTDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return new OrientDBCoreRealmImpl(coreRealmName);
        }else{
            return null;
        }
    }

    public static CoreRealm getDefaultCoreRealm(){
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return new Neo4JCoreRealmImpl();
        }else if(CoreRealmStorageImplTech.ORIENTDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return new OrientDBCoreRealmImpl();
        }else{
            return null;
        }
    }
}
