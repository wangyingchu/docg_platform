package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.tugraph.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.tugraph.termImpl.TuGraphCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

public class TuGraphCoreRealmSystemUtil {
    private static String defaultCoreRealmName = PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);
    private static String defaultDatabaseName = PropertiesHandler.getPropertyValue(PropertiesHandler.NEO4J_DEFAULT_DATABASE_NAME);
    public static CoreRealm getDefaultCoreRealm(){
        return new TuGraphCoreRealmImpl(defaultDatabaseName);
    }
}
