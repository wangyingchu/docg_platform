package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.util;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.nebulaGraph.termImpl.NebulaGraphCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NubulaGraphCoreRealmSystemUtil {

    private static final String server = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_SERVER_ADDRESS);
    private static final String portString = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_SERVER_PORT);
    private static final String user = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_USER);
    private static final String password = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_PASSWORD);
    private static String defaultCoreRealmName = PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);
    private static String partitionNumberString = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_PARTITION_NUMBER);
    private static String replicaFactorString = PropertiesHandler.getPropertyValue(PropertiesHandler.NEBULAGRAPH_REPLICA_FACTOR);

    public static CoreRealm getDefaultCoreRealm(){
        Session session = null;
        NebulaPool pool = null;
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(10);
            List<HostAddress> addresses = Arrays.asList(new HostAddress(server,Integer.valueOf(portString)));
            pool = new NebulaPool();
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession(user, password, false);
            ResultSet resultSet = session.execute("DESCRIBE SPACE "+defaultCoreRealmName+";");
            if(resultSet.getErrorMessage() != null && resultSet.getErrorMessage().startsWith("SpaceNotFound")){
                //default core realm not creat yet
                boolean result = createNebulaGraphSpace(session,defaultCoreRealmName);
                if(result){
                    return new NebulaGraphCoreRealmImpl(defaultCoreRealmName);
                }
            }else{
                return new NebulaGraphCoreRealmImpl(defaultCoreRealmName);
            }
        } catch (UnknownHostException | NotValidConnectionException | IOErrorException | AuthFailedException |
                 ClientServerIncompatibleException e) {
            throw new RuntimeException(e);
        }finally {
            if(session != null){
                session.release();
            }
            if(pool != null){
                pool.close();
            }
        }
        return null;
    }

    public static Set<String> listCoreRealms(){
        Set<String> coreRealmsSet = new HashSet<>();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(10);
            List<HostAddress> addresses = Arrays.asList(new HostAddress(server,Integer.valueOf(portString)));
            NebulaPool pool = new NebulaPool();
            pool.init(addresses, nebulaPoolConfig);
            Session session = pool.getSession(user, password, false);
            ResultSet resultSet = session.execute("SHOW SPACES;");
            if(resultSet != null){
                List<ValueWrapper> namesList = resultSet.colValues("Name");
                if(namesList != null){
                    for(ValueWrapper currentValueWrapper:namesList){
                        coreRealmsSet.add(currentValueWrapper.asString());
                    }
                }
            }
            session.release();
            pool.close();
        } catch (UnknownHostException | NotValidConnectionException | IOErrorException | AuthFailedException |
                 ClientServerIncompatibleException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return coreRealmsSet;
    }

    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException{
        Session session = null;
        NebulaPool pool = null;
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(10);
            List<HostAddress> addresses = Arrays.asList(new HostAddress(server,Integer.valueOf(portString)));
            pool = new NebulaPool();
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession(user, password, false);
            ResultSet resultSet = session.execute("DESCRIBE SPACE "+coreRealmName+";");
            if(resultSet.getErrorMessage() != null && resultSet.getErrorMessage().startsWith("SpaceNotFound")){
                //default core realm not creat yet
                boolean result = createNebulaGraphSpace(session,coreRealmName);
                if(result){
                    return new NebulaGraphCoreRealmImpl(coreRealmName);
                }else{
                    CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                    coreRealmServiceRuntimeException.setCauseMessage("Create Core Realm with name "+coreRealmName+" fail.");
                    throw coreRealmServiceRuntimeException;
                }
            }else{
                CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                coreRealmServiceRuntimeException.setCauseMessage("Core Realm with name "+coreRealmName+" already exist.");
                throw coreRealmServiceRuntimeException;
            }
        } catch (UnknownHostException | NotValidConnectionException | IOErrorException | AuthFailedException |
                 ClientServerIncompatibleException e) {
            throw new RuntimeException(e);
        }finally {
            if (session != null) {
                session.release();
            }
            if (pool != null) {
                pool.close();
            }
        }
    }

    private static boolean createNebulaGraphSpace(Session session,String spaceName) throws IOErrorException {
        String nql = "CREATE SPACE `"+spaceName+"` (partition_num = "+Integer.valueOf(partitionNumberString)+
                ",replica_factor="+Integer.valueOf(replicaFactorString)+",vid_type = INT64);";
        ResultSet resultSet = session.execute(nql);
        if(resultSet.getErrorMessage().equals("")){
            return true;
        }else{
            return false;
        }
    }
}
