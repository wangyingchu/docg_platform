package com.viewfunction.docg.testcase.coreRealm.termTest_memgraph;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl.MemGraphCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class RealmTermFactoryTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for RealmTermFactoryTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testRealmTermFactoryFunction(){
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertTrue(coreRealm instanceof MemGraphCoreRealmImpl);
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.MEMGRAPH);
        boolean exceptionShouldCaught = false;
        try {
            RealmTermFactory.getCoreRealm(testRealmName);
        }catch (CoreRealmFunctionNotSupportedException e){
            exceptionShouldCaught = true;
        }
        Assert.assertTrue(exceptionShouldCaught);
    }
}
