package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.*;

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

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind("RelationAttachConceptionKind01");
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind("RelationAttachConceptionKind01",true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind("RelationAttachConceptionKind01");
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind("RelationAttachConceptionKind01","");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),"RelationAttachConceptionKind01");
        }

        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind("RelationAttachConceptionKind02");
        if(_ConceptionKind02 != null){
            coreRealm.removeConceptionKind("RelationAttachConceptionKind02",true);
        }
        _ConceptionKind02 = coreRealm.getConceptionKind("RelationAttachConceptionKind02");
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind("RelationAttachConceptionKind02","");
            Assert.assertNotNull(_ConceptionKind02);
            Assert.assertEquals(_ConceptionKind02.getConceptionKindName(),"RelationAttachConceptionKind02");
        }

        ConceptionKind _ConceptionKind03 = coreRealm.getConceptionKind("RelationAttachConceptionKind03");
        if(_ConceptionKind03 != null){
            coreRealm.removeConceptionKind("RelationAttachConceptionKind03",true);
        }
        _ConceptionKind03 = coreRealm.getConceptionKind("RelationAttachConceptionKind03");
        if(_ConceptionKind03 == null){
            _ConceptionKind03 = coreRealm.createConceptionKind("RelationAttachConceptionKind03","");
            Assert.assertNotNull(_ConceptionKind03);
            Assert.assertEquals(_ConceptionKind03.getConceptionKindName(),"RelationAttachConceptionKind03");
        }

        Map<String,Object> newEntityValueMap= new HashMap<>();
        newEntityValueMap.put("prop1",Long.parseLong("12345"));
        newEntityValueMap.put("prop2",Double.parseDouble("12345.789"));
        newEntityValueMap.put("prop3",Integer.parseInt("1234"));
        newEntityValueMap.put("prop4","thi is s string");
        newEntityValueMap.put("prop5",Boolean.valueOf("true"));
        newEntityValueMap.put("prop6", new BigDecimal("5566778890.223344"));
        newEntityValueMap.put("prop7", Short.valueOf("24"));
        newEntityValueMap.put("prop8", Float.valueOf("1234.66"));
        newEntityValueMap.put("prop9", new Long[]{1000l,2000l,3000l});
        newEntityValueMap.put("prop10", new Double[]{1000.1d,2000.2d,3000.3d});
        newEntityValueMap.put("prop11", new Integer[]{100,200,300});
        newEntityValueMap.put("prop12", new String[]{"this is str1","这是字符串2"});
        newEntityValueMap.put("prop13", new Boolean[]{true,true,false,false,true});
        newEntityValueMap.put("prop14", new BigDecimal[]{new BigDecimal("1234567.890"),new BigDecimal("987654321.12345")});
        newEntityValueMap.put("prop15", new Short[]{1,2,3,4,5});
        newEntityValueMap.put("prop16", new Float[]{1000.1f,2000.2f,3000.3f});
        newEntityValueMap.put("prop17", new Date());
        newEntityValueMap.put("prop18", new Date[]{new Date(),new Date(),new Date(),new Date()});
        newEntityValueMap.put("prop19", Byte.valueOf("2"));
        newEntityValueMap.put("prop20", "this is a byte array value".getBytes());
        newEntityValueMap.put("prop21", new Byte[]{Byte.valueOf("1"),Byte.valueOf("3"),Byte.valueOf("5")});

        List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();
        ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValueMap);
        ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValueMap);
        ConceptionEntityValue conceptionEntityValue3 = new ConceptionEntityValue(newEntityValueMap);
        conceptionEntityValueList.add(conceptionEntityValue1);
        conceptionEntityValueList.add(conceptionEntityValue2);
        conceptionEntityValueList.add(conceptionEntityValue3);

        EntitiesOperationResult addEntitiesResult = _ConceptionKind01.newEntities(conceptionEntityValueList,false);












    }
}
