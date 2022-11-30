package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class GraphOperationExecutor<T> implements AutoCloseable{

    private static final String uri = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_URI);
    private static final String user = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_USER);
    private static final String password = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_PASSWORD);

    private Driver driver;

    public GraphOperationExecutor(){
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    public T executeWrite(DataTransformer<T> dataTransformer, String operationMessage, Object... keysAndValues){
        try (Session session = driver.session() ){

            return session.executeWrite(new TransactionCallback<T>() {
                @Override
                public T execute(TransactionContext transactionContext) {
                    Query query = new Query(operationMessage, parameters(keysAndValues));
                    Result result = transactionContext.run(query);
                    return dataTransformer.transformResult(result);
                }
            });
        }
    }

    public T executeRead(DataTransformer<T> dataTransformer,String operationMessage,Object... keysAndValues){
        try (Session session = driver.session() ){
            return session.executeRead(new TransactionCallback<T>() {
                @Override
                public T execute(TransactionContext transactionContext) {
                    Query query = new Query(operationMessage, parameters(keysAndValues));
                    Result result = transactionContext.run(query);
                    return dataTransformer.transformResult(result);
                }
            });
        }
    }

    @Override
    public void close(){
        //Need add this logic for neo4j v5 to close connection
        driver.session().close();
        driver.close();
    }
}