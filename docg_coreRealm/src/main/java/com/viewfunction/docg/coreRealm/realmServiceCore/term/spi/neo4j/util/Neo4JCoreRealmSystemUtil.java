package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.HashSet;
import java.util.Set;

public class Neo4JCoreRealmSystemUtil {

    private static boolean supportMultiNeo4JGraph =
            Boolean.parseBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_SUPPORT_MULTI_GRAPH));
    private static String defaultCoreRealmName = PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);

    public static CoreRealm getDefaultCoreRealm(){
        return new Neo4JCoreRealmImpl();
    }

    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {
        if(supportMultiNeo4JGraph){
            Set<String> existCoreRealms = listCoreRealms();
            if(existCoreRealms.contains(coreRealmName)){
                CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                coreRealmServiceRuntimeException.setCauseMessage("Core Realm with name "+coreRealmName+" already exist.");
                throw coreRealmServiceRuntimeException;
            }else{
                //only Enterprise edition neo4j support create database XXX command
                String queryCQL = "create database "+coreRealmName;
                GraphOperationExecutor _GraphOperationExecutor = new GraphOperationExecutor();

                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record currentRecord = result.next();
                        }
                        return null;
                    }
                };
                _GraphOperationExecutor.executeWrite(dataTransformer,queryCQL);
                _GraphOperationExecutor.close();
            }
            return new Neo4JCoreRealmImpl(coreRealmName);
        }else{
            CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
            exception.setCauseMessage("Current Neo4J storage implements doesn't support multi Realm");
            throw exception;
        }
    }

    public static Set<String> listCoreRealms() throws CoreRealmFunctionNotSupportedException {
        if(supportMultiNeo4JGraph){
            Set<String> coreRealmsSet = new HashSet<>();
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
                            coreRealmsSet.add(defaultCoreRealmName);
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
    }
}
