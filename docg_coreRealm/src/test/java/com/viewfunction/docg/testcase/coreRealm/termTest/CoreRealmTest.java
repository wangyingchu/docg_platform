package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j.Neo4JConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CoreRealmTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for CoreRealmTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testCoreRealmFunction(){
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind("kind01");
        Assert.assertNull(_ConceptionKind01);
        _ConceptionKind01 = coreRealm.createConceptionKind("kind01","kind01Desc+中文描述");
        Assert.assertNotNull(_ConceptionKind01);
        Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),"kind01");
        Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"kind01Desc+中文描述");
        Assert.assertNotNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getConceptionKindUID());
        Assert.assertNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getCoreRealmName());

        _ConceptionKind01 = coreRealm.createConceptionKind("kind01","kind01Desc+中文描述");
        Assert.assertNull(_ConceptionKind01);

        ConceptionKind _ConceptionKind02 = coreRealm.createConceptionKind("kind02","kind02Desc+中文描述");
        Assert.assertNotNull(_ConceptionKind02);

        _ConceptionKind01 = coreRealm.getConceptionKind("kind01");
        Assert.assertNotNull(_ConceptionKind01);
        Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),"kind01");
        Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"kind01Desc+中文描述");
        Assert.assertNotNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getConceptionKindUID());
        Assert.assertNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getCoreRealmName());
    }
}
