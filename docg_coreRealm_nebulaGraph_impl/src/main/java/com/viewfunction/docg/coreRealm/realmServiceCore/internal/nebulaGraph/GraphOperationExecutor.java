package com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph;

import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.nebulaGraph.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class GraphOperationExecutor<T> implements AutoCloseable{

    private static final String server = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_SERVER_ADDRESS);
    private static final String portString = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_SERVER_PORT);
    private static final String user = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_USER);
    private static final String password = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_PASSWORD);
    private static String defaultCoreRealmName = PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);
    private SessionPool sessionPool;
    private static Logger logger = LoggerFactory.getLogger(GraphOperationExecutor.class);

    private SessionPool initSessionPool(String coreRealmName){
        List<HostAddress> addresses = Arrays.asList(new HostAddress(server, Integer.valueOf(portString)));
        String spaceName = coreRealmName;
        SessionPoolConfig sessionPoolConfig = new SessionPoolConfig(addresses, spaceName, user, password);
        SessionPool sessionPool = new SessionPool(sessionPoolConfig);
        if (!sessionPool.init()) {
            logger.error("SESSION POOL INIT FAILED.");
            System.err.println("SESSION POOL INIT FAILED.");
            return null;
        }else{
            return sessionPool;
        }
    }

    public GraphOperationExecutor(){
        sessionPool = initSessionPool(defaultCoreRealmName);
    }

    public GraphOperationExecutor(String coreRealmName){
        sessionPool = initSessionPool(coreRealmName);
    }

    public T executeCommand(DataTransformer<T> dataTransformer,String operationMessage){
        try {
            ResultSet resultSet = this.sessionPool.execute(operationMessage);
            return dataTransformer != null? dataTransformer.transformResult(resultSet) : null;
        } catch (IOErrorException e) {
            throw new RuntimeException(e);
        } catch (ClientServerIncompatibleException e) {
            throw new RuntimeException(e);
        } catch (AuthFailedException e) {
            throw new RuntimeException(e);
        } catch (BindSpaceFailedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if(this.sessionPool != null){
            sessionPool.close();
        }
    }
}
