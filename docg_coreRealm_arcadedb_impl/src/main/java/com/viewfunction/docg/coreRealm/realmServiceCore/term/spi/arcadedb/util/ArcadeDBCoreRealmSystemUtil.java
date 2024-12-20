package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.util;

import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.arcadedb.remote.RemoteDatabase;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.termImpl.ArcadeDBCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.util.httpResponseVO.ListDatabasesVO;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import java.util.*;

public class ArcadeDBCoreRealmSystemUtil {
    private static final String server = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_SERVER_ADDRESS);
    private static final String portString = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_SERVER_PORT);
    private static final String user = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_USER);
    private static final String password = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_PASSWORD);
    private static String defaultCoreRealmName = PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);

    public static CoreRealm getDefaultCoreRealm(){
        String _HTTP_SERVICE_URL = getHttpApiRoot()+"exists/"+defaultCoreRealmName;
        HttpResponse httpResponse = HttpUtil.createGet(_HTTP_SERVICE_URL).header(Header.AUTHORIZATION,getBasicAuthorizationInfo()).execute();
        JSON responseJson = JSONUtil.parse(httpResponse.body());
        Map<String,Object> responseMap = responseJson.toBean(HashMap.class);
        boolean isDefaultCoreRealmExist = (boolean)responseMap.get("result");
        if(!isDefaultCoreRealmExist){
            try {
                createCoreRealm(defaultCoreRealmName);
            } catch (CoreRealmServiceRuntimeException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArcadeDBCoreRealmImpl(defaultCoreRealmName);
    }

    public static Set<String> listCoreRealms(){
        Set<String> coreRealmsSet = new HashSet<>();
        String _HTTP_SERVICE_URL = getHttpApiRoot()+"databases";
        HttpResponse httpResponse = HttpUtil.createGet(_HTTP_SERVICE_URL).header(Header.AUTHORIZATION,getBasicAuthorizationInfo()).execute();
        JSON responseJson = JSONUtil.parse(httpResponse.body());
        ListDatabasesVO listDatabasesVO = responseJson.toBean(ListDatabasesVO.class);
        if(listDatabasesVO.getResult() != null){
            coreRealmsSet.addAll(Set.of(listDatabasesVO.getResult()));
        }
        return coreRealmsSet;
    }

    public static CoreRealm createCoreRealm(String coreRealmName) throws CoreRealmServiceRuntimeException {
        Set<String> existCoreRealms = listCoreRealms();
        if(existCoreRealms.contains(coreRealmName)){
            CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
            coreRealmServiceRuntimeException.setCauseMessage("CoreRealm "+coreRealmName+" already exists");
            throw coreRealmServiceRuntimeException;
        }else{
            RemoteDatabase database = new RemoteDatabase(server, Integer.valueOf(portString), coreRealmName, user, password);
            database.create();
            return new ArcadeDBCoreRealmImpl(coreRealmName);
        }
    }

    public static String getServerHttpLocation(){
        String _HTTP_SERVICE_URL = "http://"+server+":"+portString;
        return _HTTP_SERVICE_URL;
    }

    public static String getHttpApiRoot(){
        String _HTTP_API_ROOT = getServerHttpLocation()+"/api/v1/";
        return _HTTP_API_ROOT;
    }

    public static String getBasicAuthorizationInfo(){
        String _AUTHORIZATION_INFO_String = user+":"+password;
        String _AUTHORIZATION_INFO = Base64.getEncoder().encodeToString(_AUTHORIZATION_INFO_String.getBytes());
        String _AUTHORIZATION_INFOString = "Basic "+_AUTHORIZATION_INFO;
        return _AUTHORIZATION_INFOString;
    }
}
