package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl.MemGraphCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

public class MemGraphCoreRealmSystemUtil {

    private static boolean supportMultiMemGraphGraph =
            Boolean.parseBoolean(PropertiesHandler.getPropertyValue(PropertiesHandler.MEMGRAPH_SUPPORT_MULTI_GRAPH));
    private static String defaultCoreRealmName = PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);

    public static CoreRealm getDefaultCoreRealm(){
        return new MemGraphCoreRealmImpl();
    }
}
