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

    public T executeCommand(DataTransformer<T> dataTransformer, String commandType, String commandContent){
        T commandResult = null;

        database.begin();
        ResultSet executeResult = database.command("sql", "create vertex type Production if not exists");
        System.out.println(executeResult.getQueryStats());
        database.commit();

        return dataTransformer != null ? dataTransformer.transformResult(executeResult):null;

        /*
        database.transaction(() -> {
            ResultSet executeResult = database.command("sql", "create vertex type Customer if not exists");
        });
        */
    }

    @Override
    public void close() {
        if(database != null){
            database.close();
        }
    }
}
