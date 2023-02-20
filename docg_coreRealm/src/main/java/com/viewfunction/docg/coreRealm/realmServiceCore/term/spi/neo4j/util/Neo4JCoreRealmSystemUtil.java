package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

public class Neo4JCoreRealmSystemUtil {

    private static boolean supportMultiNeo4JGraph =
            Boolean.parseBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_SUPPORT_MULTI_GRAPH));
    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {
        if(supportMultiNeo4JGraph){
            return new Neo4JCoreRealmImpl(coreRealmName);
        }else{
            CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
            exception.setCauseMessage("Current Neo4J storage implements doesn't support multi Realm");
            throw exception;
        }
    }
}
