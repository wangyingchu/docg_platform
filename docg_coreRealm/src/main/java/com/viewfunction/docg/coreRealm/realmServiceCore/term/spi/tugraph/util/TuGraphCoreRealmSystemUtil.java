package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.tugraph.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.tugraph.termImpl.TuGraphCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;

public class TuGraphCoreRealmSystemUtil {
    private static String defaultCoreRealmName = PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);

    public static CoreRealm getDefaultCoreRealm(){
        return new TuGraphCoreRealmImpl();
    }
}
