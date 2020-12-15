package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

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

        List<RelationAttachLinkLogic> attachLinkLogicList = targetRelationAttachKind2.getRelationAttachLinkLogic();
        Assert.assertNotNull(attachLinkLogicList);
        Assert.assertEquals(attachLinkLogicList.size(),0);

        RelationAttachLinkLogic relationAttachLinkLogic01 = new RelationAttachLinkLogic(RelationAttachKind.LinkLogicType.DEFAULT, RelationAttachKind.LinkLogicCondition.Equal,"knownPropertyName","unKnownPropertyName");
        RelationAttachLinkLogic resultRelationAttachLinkLogic = targetRelationAttachKind2.createRelationAttachLinkLogic(relationAttachLinkLogic01);
        Assert.assertNotNull(resultRelationAttachLinkLogic);
        Assert.assertNotNull(resultRelationAttachLinkLogic.getRelationAttachLinkLogicUID());

        attachLinkLogicList = targetRelationAttachKind2.getRelationAttachLinkLogic();
        Assert.assertNotNull(attachLinkLogicList);
        Assert.assertEquals(attachLinkLogicList.size(),1);

        Assert.assertEquals(attachLinkLogicList.get(0).getLinkLogicType(), RelationAttachKind.LinkLogicType.DEFAULT);
        Assert.assertEquals(attachLinkLogicList.get(0).getLinkLogicCondition(), RelationAttachKind.LinkLogicCondition.Equal);
        Assert.assertEquals(attachLinkLogicList.get(0).getKnownEntityLinkAttributeName(),"knownPropertyName");
        Assert.assertEquals(attachLinkLogicList.get(0).getUnKnownEntitiesLinkAttributeName(),"unKnownPropertyName");
        Assert.assertEquals(attachLinkLogicList.get(0).getRelationAttachLinkLogicUID(),resultRelationAttachLinkLogic.getRelationAttachLinkLogicUID());

        boolean exceptionShouldBeCaught = false;
        try{
            targetRelationAttachKind2.createRelationAttachLinkLogic(relationAttachLinkLogic01);
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        boolean removeResult = targetRelationAttachKind2.removeRelationAttachLinkLogic(resultRelationAttachLinkLogic.getRelationAttachLinkLogicUID());
        Assert.assertTrue(removeResult);

        attachLinkLogicList = targetRelationAttachKind2.getRelationAttachLinkLogic();
        Assert.assertNotNull(attachLinkLogicList);
        Assert.assertEquals(attachLinkLogicList.size(),0);

        exceptionShouldBeCaught = false;
        try{
            targetRelationAttachKind2.removeRelationAttachLinkLogic(resultRelationAttachLinkLogic.getRelationAttachLinkLogicUID()+"123");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        relationAttachLinkLogic01 = new RelationAttachLinkLogic(RelationAttachKind.LinkLogicType.DEFAULT, RelationAttachKind.LinkLogicCondition.Equal,"knownPropertyName1","unKnownPropertyName1");
        resultRelationAttachLinkLogic = targetRelationAttachKind2.createRelationAttachLinkLogic(relationAttachLinkLogic01);
        Assert.assertNotNull(resultRelationAttachLinkLogic);
        Assert.assertNotNull(resultRelationAttachLinkLogic.getRelationAttachLinkLogicUID());

        relationAttachLinkLogic01 = new RelationAttachLinkLogic(RelationAttachKind.LinkLogicType.AND, RelationAttachKind.LinkLogicCondition.BeginWithSimilar,"knownPropertyName2","unKnownPropertyName2");
        resultRelationAttachLinkLogic = targetRelationAttachKind2.createRelationAttachLinkLogic(relationAttachLinkLogic01);
        Assert.assertNotNull(resultRelationAttachLinkLogic);
        Assert.assertNotNull(resultRelationAttachLinkLogic.getRelationAttachLinkLogicUID());

        relationAttachLinkLogic01 = new RelationAttachLinkLogic(RelationAttachKind.LinkLogicType.OR, RelationAttachKind.LinkLogicCondition.LessThan,"knownPropertyName3","unKnownPropertyName3");
        resultRelationAttachLinkLogic = targetRelationAttachKind2.createRelationAttachLinkLogic(relationAttachLinkLogic01);
        Assert.assertNotNull(resultRelationAttachLinkLogic);
        Assert.assertNotNull(resultRelationAttachLinkLogic.getRelationAttachLinkLogicUID());

        attachLinkLogicList = targetRelationAttachKind2.getRelationAttachLinkLogic();
        Assert.assertNotNull(attachLinkLogicList);
        Assert.assertEquals(attachLinkLogicList.size(),3);

        coreRealm.removeRelationAttachKind(targetRelationAttachKind.getRelationAttachKindUID());
    }
}
