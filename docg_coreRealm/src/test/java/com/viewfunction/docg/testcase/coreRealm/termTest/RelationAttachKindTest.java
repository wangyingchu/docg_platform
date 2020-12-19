package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

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
        Assert.assertEquals(attachLinkLogicList.get(0).getSourceEntityLinkAttributeName(),"knownPropertyName");
        Assert.assertEquals(attachLinkLogicList.get(0).getTargetEntitiesLinkAttributeName(),"unKnownPropertyName");
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

        coreRealm.openGlobalSession();

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

        for(int i=0;i<30;i++){
            newEntityValueMap.put("prop6","prop6Value"+i);
            ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValueMap);
            _ConceptionKind01.newEntity(conceptionEntityValue1,false);
        }

        for(int i=0;i<50;i++){
            newEntityValueMap.put("prop7","prop7Value"+i);
            ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValueMap);
            _ConceptionKind02.newEntity(conceptionEntityValue1,false);
        }

        RelationAttachKind targetRelationAttachKind3 = coreRealm.createRelationAttachKind("RelationAttachKindForUnitTest3","RelationAttachKind_Desc3",
                "RelationAttachConceptionKind01","RelationAttachConceptionKind03","RAK_RelationKindA",true);
        RelationAttachLinkLogic relationAttachLinkLogicA = new RelationAttachLinkLogic(RelationAttachKind.LinkLogicType.DEFAULT, RelationAttachKind.LinkLogicCondition.Equal,"prop6","kprop1");
        targetRelationAttachKind3.createRelationAttachLinkLogic(relationAttachLinkLogicA);

        RelationAttachKind targetRelationAttachKind4 = coreRealm.createRelationAttachKind("RelationAttachKindForUnitTest4","RelationAttachKind_Desc4",
                "RelationAttachConceptionKind03","RelationAttachConceptionKind02","RAK_RelationKindB",true);
        RelationAttachLinkLogic relationAttachLinkLogicB = new RelationAttachLinkLogic(RelationAttachKind.LinkLogicType.DEFAULT, RelationAttachKind.LinkLogicCondition.Equal,"kprop2","prop7");
        targetRelationAttachKind4.createRelationAttachLinkLogic(relationAttachLinkLogicB);

        String[] multiConceptionsArray = new String[]{"RelationAttachConceptionKind_Multi","RelationAttachConceptionKind03"};

        Map<String,Object> newEntityValueMap2= new HashMap<>();
        newEntityValueMap2.put("kprop1","prop6Value3");
        newEntityValueMap2.put("kprop2","prop7Value12");
        ConceptionEntityValue conceptionEntityValueC = new ConceptionEntityValue(newEntityValueMap2);
        ConceptionEntity resultConceptionEntity = _ConceptionKind03.newEntity(conceptionEntityValueC,true);
        Assert.assertEquals(resultConceptionEntity.countAllRelations().longValue(),2l);

        newEntityValueMap2 = new HashMap<>();
        newEntityValueMap2.put("kprop1","prop6Value3");
        newEntityValueMap2.put("kprop2","prop7Value12");
        ConceptionEntityValue conceptionEntityValueC_M = new ConceptionEntityValue(newEntityValueMap2);
        ConceptionEntity resultConceptionEntity_m = coreRealm.newMultiConceptionEntity(multiConceptionsArray,conceptionEntityValueC_M,true);
        Assert.assertEquals(resultConceptionEntity_m.countAllRelations().longValue(),2l);

        List<RelationAttachKind> relationAttachKindList = new ArrayList<>();
        relationAttachKindList.add(targetRelationAttachKind3);
        Map<String,Object> newEntityValueMap3= new HashMap<>();
        newEntityValueMap3.put("kprop1","prop6Value6");
        newEntityValueMap3.put("kprop2","prop7Value19");
        ConceptionEntityValue conceptionEntityValueD = new ConceptionEntityValue(newEntityValueMap3);
        ConceptionEntity resultConceptionEntity2 = _ConceptionKind03.newEntity(conceptionEntityValueD,relationAttachKindList, RelationAttachKind.EntityRelateRole.TARGET);
        Assert.assertEquals(resultConceptionEntity2.countAllRelations().longValue(),1l);

        relationAttachKindList = new ArrayList<>();
        relationAttachKindList.add(targetRelationAttachKind3);
        newEntityValueMap3= new HashMap<>();
        newEntityValueMap3.put("kprop1","prop6Value6");
        newEntityValueMap3.put("kprop2","prop7Value19");
        ConceptionEntityValue conceptionEntityValueD_m = new ConceptionEntityValue(newEntityValueMap3);
        ConceptionEntity resultConceptionEntity2_m = coreRealm.newMultiConceptionEntity(multiConceptionsArray,conceptionEntityValueD_m,relationAttachKindList, RelationAttachKind.EntityRelateRole.TARGET);
        Assert.assertEquals(resultConceptionEntity2_m.countAllRelations().longValue(),1l);

        List<ConceptionEntityValue> conceptionEntityValues = new ArrayList<>();
        Map<String,Object> newEntityValueMap4= new HashMap<>();
        newEntityValueMap4.put("kprop1","prop6Value15");
        newEntityValueMap4.put("kprop2","prop7Value21");
        ConceptionEntityValue conceptionEntityValueE = new ConceptionEntityValue(newEntityValueMap4);
        conceptionEntityValues.add(conceptionEntityValueE);
        Map<String,Object> newEntityValueMap5= new HashMap<>();
        newEntityValueMap5.put("kprop1","prop6Value15");
        newEntityValueMap5.put("kprop2","prop7Value21");
        ConceptionEntityValue conceptionEntityValueF = new ConceptionEntityValue(newEntityValueMap5);
        conceptionEntityValues.add(conceptionEntityValueF);

        _ConceptionKind03.newEntities(conceptionEntityValues,true);
        coreRealm.newMultiConceptionEntities(multiConceptionsArray,conceptionEntityValues,true);

        List<ConceptionEntityValue> conceptionEntityValues2 = new ArrayList<>();
        Map<String,Object> newEntityValueMap6= new HashMap<>();
        newEntityValueMap6.put("kprop1","prop6Value23");
        newEntityValueMap6.put("kprop2","prop7Value3");
        ConceptionEntityValue conceptionEntityValueG = new ConceptionEntityValue(newEntityValueMap6);
        conceptionEntityValues2.add(conceptionEntityValueG);
        Map<String,Object> newEntityValueMap7= new HashMap<>();
        newEntityValueMap7.put("kprop1","prop6Value23");
        newEntityValueMap7.put("kprop2","prop7Value3");
        ConceptionEntityValue conceptionEntityValueH = new ConceptionEntityValue(newEntityValueMap7);
        conceptionEntityValues2.add(conceptionEntityValueH);

        relationAttachKindList.clear();
        relationAttachKindList.add(targetRelationAttachKind4);
        _ConceptionKind03.newEntities(conceptionEntityValues2,relationAttachKindList,RelationAttachKind.EntityRelateRole.SOURCE);
        coreRealm.newMultiConceptionEntities(multiConceptionsArray,conceptionEntityValues2,relationAttachKindList,RelationAttachKind.EntityRelateRole.SOURCE);

        ConceptionKind _ConceptionKind04 = coreRealm.getConceptionKind("RelationAttachConceptionKind04");
        if(_ConceptionKind04 != null){
            coreRealm.removeConceptionKind("RelationAttachConceptionKind04",true);
        }
        _ConceptionKind04 = coreRealm.getConceptionKind("RelationAttachConceptionKind04");
        if(_ConceptionKind04 == null){
            _ConceptionKind04 = coreRealm.createConceptionKind("RelationAttachConceptionKind04","");
            Assert.assertNotNull(_ConceptionKind04);
            Assert.assertEquals(_ConceptionKind04.getConceptionKindName(),"RelationAttachConceptionKind04");
        }

        ConceptionKind _ConceptionKind05 = coreRealm.getConceptionKind("RelationAttachConceptionKind05");
        if(_ConceptionKind05 != null){
            coreRealm.removeConceptionKind("RelationAttachConceptionKind05",true);
        }
        _ConceptionKind05 = coreRealm.getConceptionKind("RelationAttachConceptionKind05");
        if(_ConceptionKind05 == null){
            _ConceptionKind05 = coreRealm.createConceptionKind("RelationAttachConceptionKind05","");
            Assert.assertNotNull(_ConceptionKind05);
            Assert.assertEquals(_ConceptionKind05.getConceptionKindName(),"RelationAttachConceptionKind05");
        }

        Map<String,Object> newEntityValueMap8= new HashMap<>();
        newEntityValueMap8.put("prop1",Long.parseLong("12345"));
        newEntityValueMap8.put("prop2",Double.parseDouble("12345.789"));
        newEntityValueMap8.put("prop3",Integer.parseInt("1234"));

        for(int i=0;i<50;i++){
            newEntityValueMap8.put("prop_CK4","CK4Value"+i);
            ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValueMap8);
            _ConceptionKind04.newEntity(conceptionEntityValue1,false);
        }
        for(int i=0;i<2;i++){
            newEntityValueMap8.put("prop_CK4","COMMON_VALUE");
            ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValueMap8);
            _ConceptionKind04.newEntity(conceptionEntityValue1,false);
        }

        newEntityValueMap8.remove("prop_CK4");

        for(int i=0;i<100;i++){
            newEntityValueMap8.put("prop_CK5","CK5Value"+i);
            ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValueMap8);
            _ConceptionKind05.newEntity(conceptionEntityValue1,false);
        }

        for(int i=0;i<5;i++){
            newEntityValueMap8.put("prop_CK5","COMMON_VALUE");
            ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValueMap8);
            _ConceptionKind05.newEntity(conceptionEntityValue1,false);
        }

        RelationAttachKind targetRelationAttachKind5 = coreRealm.createRelationAttachKind("RelationAttachKindForUnitTest5","RelationAttachKind_Desc5",
                "RelationAttachConceptionKind04","RelationAttachConceptionKind05","RAK_RelationKindC01",true);
        RelationAttachLinkLogic relationAttachLinkLogicC = new RelationAttachLinkLogic(RelationAttachKind.LinkLogicType.DEFAULT, RelationAttachKind.LinkLogicCondition.Equal,"prop_CK4","prop_CK5");
        targetRelationAttachKind5.createRelationAttachLinkLogic(relationAttachLinkLogicC);

        Map<String,Object> relationDataMap = new HashMap<>();
        relationDataMap.put("relProp",1000);
        EntitiesOperationResult entitiesOperationResult = targetRelationAttachKind5.newUniversalRelationEntities(relationDataMap);
        Assert.assertEquals(entitiesOperationResult.getOperationStatistics().getSuccessItemsCount(),10l);

        coreRealm.removeRelationAttachKind(targetRelationAttachKind3.getRelationAttachKindUID());
        coreRealm.removeRelationAttachKind(targetRelationAttachKind4.getRelationAttachKindUID());
        coreRealm.removeRelationAttachKind(targetRelationAttachKind5.getRelationAttachKindUID());
        coreRealm.closeGlobalSession();
    }
}
