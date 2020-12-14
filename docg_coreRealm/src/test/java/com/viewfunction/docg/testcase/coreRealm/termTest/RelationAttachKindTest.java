package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class RelationAttachKindTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for RelationAttachKindTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testRelationAttachKindFunction() throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        RelationAttachKind targetRelationAttachKind = coreRealm.createRelationAttachKind("RelationAttachKindForUnitTest","RelationAttachKind_Desc",
                "RelationAttachKind_SourceKind","RelationAttachKind_TargetKind","RelationAttachKind_RelationKind",true);

        boolean updateResult = targetRelationAttachKind.updateRelationAttachKindDesc("RelationAttachKind_Desc2");
        Assert.assertTrue(updateResult);
        Assert.assertEquals(targetRelationAttachKind.getRelationAttachKindDesc(),"RelationAttachKind_Desc2");
        RelationAttachKind targetRelationAttachKind2 = coreRealm.getRelationAttachKind(targetRelationAttachKind.getRelationAttachKindUID());
        Assert.assertEquals(targetRelationAttachKind2.getRelationAttachKindDesc(),"RelationAttachKind_Desc2");

        Assert.assertEquals(targetRelationAttachKind2.isRepeatableRelationKindAllow(),true);
        updateResult = targetRelationAttachKind.setAllowRepeatableRelationKind(false);
        Assert.assertFalse(updateResult);
        Assert.assertEquals(targetRelationAttachKind.isRepeatableRelationKindAllow(),false);
        targetRelationAttachKind2 = coreRealm.getRelationAttachKind(targetRelationAttachKind.getRelationAttachKindUID());
        Assert.assertEquals(targetRelationAttachKind2.isRepeatableRelationKindAllow(),false);



        coreRealm.removeRelationAttachKind(targetRelationAttachKind.getRelationAttachKindUID());
    }
}
