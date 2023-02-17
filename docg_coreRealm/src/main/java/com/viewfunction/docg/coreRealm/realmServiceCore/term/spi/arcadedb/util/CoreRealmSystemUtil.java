package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.util;

import com.arcadedb.database.DatabaseFactory;
import com.arcadedb.remote.RemoteDatabase;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.termImpl.ArcadeDBCoreRealmImpl;

public class CoreRealmSystemUtil {

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
}
