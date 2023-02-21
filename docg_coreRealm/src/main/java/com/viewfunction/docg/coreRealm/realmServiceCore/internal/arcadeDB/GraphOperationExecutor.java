package com.viewfunction.docg.coreRealm.realmServiceCore.internal.arcadeDB;

import com.arcadedb.query.sql.executor.ResultSet;
import com.arcadedb.remote.RemoteDatabase;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.arcadeDB.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

public class GraphOperationExecutor<T> implements AutoCloseable{

    private static final String server = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_SERVER_ADDRESS);
    private static final String portString = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_SERVER_PORT);
    private static final String user = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_USER);
    private static final String password = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_PASSWORD);
    private static String defaultCoreRealmName = PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);

    private RemoteDatabase database;

    public GraphOperationExecutor(){
        database = new RemoteDatabase(server, Integer.valueOf(portString), defaultCoreRealmName, user, password);
    }

    public GraphOperationExecutor(String coreRealmName){
        database = new RemoteDatabase(server, Integer.valueOf(portString), coreRealmName, user, password);
    }

    public T executeCommand(DataTransformer<T> dataTransformer, QueryBuilder.QueryLanguage queryLanguage, String queryContent){
        String languageType = queryLanguage != null ? queryLanguage.toString():"sql";
        database.begin();
        ResultSet executeResult = database.command(languageType, queryContent);
        System.out.println(executeResult.getQueryStats());
        database.commit();
        return dataTransformer != null ? dataTransformer.transformResult(executeResult):null;
    }

    public T executeCommand(DataTransformer<T> dataTransformer, String queryContent){
        database.begin();
        ResultSet executeResult = database.command("sql", queryContent);
        System.out.println(executeResult.getQueryStats());
        database.commit();
        return dataTransformer != null ? dataTransformer.transformResult(executeResult):null;
    }

    @Override
    public void close() {
        if(database != null){
            database.close();
        }
    }
}
