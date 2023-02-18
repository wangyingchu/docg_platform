package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.util;

import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.arcadedb.database.DatabaseFactory;
import com.arcadedb.remote.RemoteDatabase;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.termImpl.ArcadeDBCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.util.httpResponseVO.ListDatabasesVO;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class CoreRealmSystemUtil {

    private static final String server = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_SERVER_ADDRESS);
    private static final String portString = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_SERVER_PORT);
    private static final String user = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_USER);
    private static final String password = PropertiesHandler.getPropertyValue(PropertiesHandler.ARCADEDB_PASSWORD);

    public static Set<String> listCoreRealms(){
        Set<String> coreRealmsSet = new HashSet<>();
        String _AUTHORIZATION_INFO_String = user+":"+password;
        String _AUTHORIZATION_INFO = Base64.getEncoder().encodeToString(_AUTHORIZATION_INFO_String.getBytes());
        String _HTTP_SERVICE_URL = "http://"+server+":"+portString+"/api/v1/"+"databases";
        HttpResponse httpResponse = HttpUtil.createGet(_HTTP_SERVICE_URL).header(Header.AUTHORIZATION,"Basic "+_AUTHORIZATION_INFO).execute();
        JSON responseJson = JSONUtil.parse(httpResponse.body());
        ListDatabasesVO listDatabasesVO = responseJson.toBean(ListDatabasesVO.class);
        if(listDatabasesVO.getResult() != null){
            coreRealmsSet.addAll(Set.of(listDatabasesVO.getResult()));
        }
        return coreRealmsSet;
    }

    public static CoreRealm createCoreRealm(String coreRealmName){



        DatabaseFactory arcadeDatabaseFactory = new DatabaseFactory("/media/wangychu/HSStorage/Local_Services/Arcadedb/arcadedb-23.1.2/databases/"+coreRealmName);
        boolean alreadyExist = arcadeDatabaseFactory.exists();
        System.out.println(arcadeDatabaseFactory.exists());
        if(!alreadyExist){
            //Database tagetDB = arcadeDatabaseFactory.create();
            //tagetDB.close();
        }


        //DatabaseFactory databaseFactory = new DatabaseFactory("databases/"+coreRealmName);
        //System.out.println(databaseFactory.getDatabasePath());
        //databaseFactory.open();





        RemoteDatabase database = new RemoteDatabase("localhost", 2480, coreRealmName, "root", "wyc19771123");
        database.create();



        //System.out.println( database.getLeaderAddress());
        //System.out.println( database.getReplicaAddresses());



        //List<String> databases = database.databases();
       // System.out.println(databases);

        return new ArcadeDBCoreRealmImpl(coreRealmName);


    }





    public static String httpGet(){
        String val =
        Base64.getEncoder().encodeToString("root:wyc4docg".getBytes());
        System.out.println(val);
        //HttpResponse httpResponse = HttpUtil.createGet("http://127.0.1.1:2480/api/v1/databases").header(Header.AUTHORIZATION,"Basic cm9vdDp3eWM0ZG9jZw==").execute();
        HttpResponse httpResponse = HttpUtil.createGet("http://127.0.1.1:2480/api/v1/databases").header(Header.AUTHORIZATION,"Basic "+val).execute();
        System.out.println(httpResponse.body());

        JSON responseJson = JSONUtil.parse(httpResponse.body());
        System.out.println(responseJson.toStringPretty());
        ListDatabasesVO ListDatabasesVO = responseJson.toBean(ListDatabasesVO.class);
        System.out.println(ListDatabasesVO.getUser());
        System.out.println(ListDatabasesVO.getResult().length);
        return null;





    }





}
