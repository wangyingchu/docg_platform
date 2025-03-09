package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.neo4j.driver.*;

import java.util.concurrent.TimeUnit;

import static org.neo4j.driver.Values.parameters;

public class GraphOperationExecutor<T> implements AutoCloseable{

    private static final String uri = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_URI);
    private static final String user = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_USER) != null?
            PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_USER) : "";
    private static final String password = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_PASSWORD) != null?
            PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_PASSWORD) : "";
    private static final boolean usingConnectionPool = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_USING_CONNECTION_POOL) != null ?
            Boolean.parseBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_USING_CONNECTION_POOL)):false;
    private static final int maxConnectionPoolSize = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_MAX_CONNECTION_POOL_SIZE) != null?
            Integer.parseInt(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_MAX_CONNECTION_POOL_SIZE)):100;
    private static final int connectionAcquisitionTimeoutSeconds = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_CONNECTION_ACQUISITION_TIMEOUT_SECONDS) != null?
            Integer.parseInt(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_CONNECTION_ACQUISITION_TIMEOUT_SECONDS)):5;
    private static final boolean supportExplicitTransaction = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_SUPPORT_EXPLICIT_TRANSACTION) != null ?
            Boolean.parseBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_SUPPORT_EXPLICIT_TRANSACTION)):true;
    private String databaseName = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_DATABASE_NAME) != null?
            PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_DATABASE_NAME) : null;
    private Driver driver;

    public GraphOperationExecutor(){
        if(usingConnectionPool){
            driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ),
                    Config.builder().
                            withMaxConnectionPoolSize(maxConnectionPoolSize).
                            withConnectionAcquisitionTimeout(connectionAcquisitionTimeoutSeconds, TimeUnit.SECONDS).build());
        }else{
            driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ));
        }
    }

    public T executeWrite(DataTransformer<T> dataTransformer, String operationMessage, Object... keysAndValues){
        try (Session session = databaseName != null ? driver.session(SessionConfig.forDatabase(databaseName)) : driver.session()){
            if(supportExplicitTransaction){
                return session.executeWrite(new TransactionCallback<T>() {
                    @Override
                    public T execute(TransactionContext transactionContext) {
                        Query query = new Query(operationMessage, parameters(keysAndValues));
                        Result result = transactionContext.run(query);
                        return dataTransformer.transformResult(result);
                    }
                });
            }else{
                Result result = session.run(operationMessage,parameters(keysAndValues));
                return dataTransformer.transformResult(result);
            }
        }
    }

    public T executeRead(DataTransformer<T> dataTransformer,String operationMessage,Object... keysAndValues){
        try (Session session = databaseName != null ? driver.session(SessionConfig.forDatabase(databaseName)) : driver.session()){
            if(supportExplicitTransaction){
                return session.executeRead(new TransactionCallback<T>() {
                    @Override
                    public T execute(TransactionContext transactionContext) {
                        Query query = new Query(operationMessage, parameters(keysAndValues));
                        Result result = transactionContext.run(query);
                        return dataTransformer.transformResult(result);
                    }
                });
            }else{
                Result result = session.run(operationMessage,parameters(keysAndValues));
                return dataTransformer.transformResult(result);
            }
        }
    }

    @Override
    public void close(){
        //Need add this logic for neo4j v5 to close connection
        try {
            //if driver already closed,below method will throw exception
            driver.session().close();
        }catch(IllegalStateException e){
            //java.lang.IllegalStateException: This driver instance has already been closed
            e.printStackTrace();
        }
        driver.close();
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}