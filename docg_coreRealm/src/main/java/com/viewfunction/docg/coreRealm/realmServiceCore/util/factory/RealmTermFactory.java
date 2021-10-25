package com.viewfunction.docg.coreRealm.realmServiceCore.util.factory;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.orientdb.termImpl.OrientDBCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.HashSet;
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

    /*
    public static Set<String> listCoreRealms() throws CoreRealmFunctionNotSupportedException{
        if(CoreRealmStorageImplTech.NEO4J.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            Set<String> coreRealmsSet = new HashSet<>();
            if(supportMultiNeo4JGraph){
                String queryCQL = "show databases";
                GraphOperationExecutor _GraphOperationExecutor = new GraphOperationExecutor();

                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record currentRecord = result.next();
                            String currentCoreRealm = currentRecord.get("name").asString();
                            boolean isDefaultCoreRealm = currentRecord.get("default").asBoolean();
                            if(isDefaultCoreRealm){
                                coreRealmsSet.add("Default_CoreRealm");
                            }else{
                                if(!currentCoreRealm.equals("system")){
                                    coreRealmsSet.add(currentCoreRealm);
                                }
                            }
                        }
                        return null;
                    }
                };
                _GraphOperationExecutor.executeRead(dataTransformer,queryCQL);
                _GraphOperationExecutor.close();
                return coreRealmsSet;
            }else{
                CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
                exception.setCauseMessage("Current Neo4J storage implements doesn't support multi Realm");
                throw exception;
            }
        }else if(CoreRealmStorageImplTech.ORIENTDB.toString().equals(_CORE_REALM_STORAGE_IMPL_TECH)){
            return null;
        }else{
            return null;
        }
    }

    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {
        Set<String> existCoreRealms = listCoreRealms();
        if(existCoreRealms.contains(coreRealmName)){
            CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
            coreRealmServiceRuntimeException.setCauseMessage("Core Realm with name "+coreRealmName+" already exist.");
            throw coreRealmServiceRuntimeException;
        }else{
            String queryCQL = "create database "+coreRealmName;
            GraphOperationExecutor _GraphOperationExecutor = new GraphOperationExecutor();

            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record currentRecord = result.next();
                        System.out.println(currentRecord);
                    }
                    return null;
                }
            };
            _GraphOperationExecutor.executeWrite(dataTransformer,queryCQL);
            _GraphOperationExecutor.close();
        }
        return null;
    }
    */
}
