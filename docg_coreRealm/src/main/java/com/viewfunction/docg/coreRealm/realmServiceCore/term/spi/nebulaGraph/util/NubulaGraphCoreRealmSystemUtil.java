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

    public static CoreRealm getDefaultCoreRealm(){
        Set<String> coreRealmsSet = new HashSet<>();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(10);
            List<HostAddress> addresses = Arrays.asList(new HostAddress(server,Integer.valueOf(portString)));
            NebulaPool pool = new NebulaPool();
            pool.init(addresses, nebulaPoolConfig);
            Session session = pool.getSession(user, password, false);
            ResultSet resultSet = session.execute("DESCRIBE SPACE "+defaultCoreRealmName+";");

            if(resultSet.getErrorMessage() != null &&resultSet.getErrorMessage().startsWith("SpaceNotFound")){
                //default core realm not creat yet
                session.execute("CREATE SPACE IF NOT EXISTS "+ defaultCoreRealmName +
                        "(\n" +
                        "    [partition_num = <partition_number>,]\n" +
                        "    [replica_factor = <replica_number>,]\n" +
                        "    vid_type = {FIXED_STRING(<N>) | INT[64]}\n" +
                        "    )"
                );
            }else{
                return new NebulaGraphCoreRealmImpl(defaultCoreRealmName);
            }
            session.release();
            pool.close();
        } catch (UnknownHostException | NotValidConnectionException | IOErrorException | AuthFailedException |
                 ClientServerIncompatibleException e) {
            throw new RuntimeException(e);
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

    public static CoreRealm createCoreRealm(String coreRealmName){
        return null;
    }
}
