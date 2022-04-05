package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class GeospatialScaleCalculableTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for GeospatialScaleCalculableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testGeospatialScaleCalculableFunction() throws CoreRealmServiceRuntimeException{
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        //coreRealm.openGlobalSession();

        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);



    }
}
