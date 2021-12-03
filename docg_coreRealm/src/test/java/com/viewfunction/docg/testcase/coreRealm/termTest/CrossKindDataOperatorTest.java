package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossKindDataOperatorTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName1 = "CrossKindTestKind01";
    private static String testConceptionKindName2 = "CrossKindTestKind02";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for CrossKindDataOperatorTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testCrossKindDataOperatorFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName1);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName1, testConceptionKindName1 +"中文描述");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(), testConceptionKindName1);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(), testConceptionKindName1 +"中文描述");
        }

        EntitiesOperationResult purgeEntitiesOperationResult1 = _ConceptionKind01.purgeAllEntities();

        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName2);
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind(testConceptionKindName2, testConceptionKindName2 +"中文描述");
            Assert.assertNotNull(_ConceptionKind02);
            Assert.assertEquals(_ConceptionKind02.getConceptionKindName(), testConceptionKindName2);
            Assert.assertEquals(_ConceptionKind02.getConceptionKindDesc(), testConceptionKindName2 +"中文描述");
        }

        EntitiesOperationResult purgeEntitiesOperationResult2 = _ConceptionKind02.purgeAllEntities();

        List<String> uidList = new ArrayList<>();
        for(int i=0;i<10;i++){
            Map<String,Object> newEntityValue= new HashMap<>();
            newEntityValue.put("prop1",i);
            newEntityValue.put("prop2","StringValueOf "+i);
            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
            ConceptionEntity _ConceptionEntity = _ConceptionKind01.newEntity(conceptionEntityValue,false);
            uidList.add(_ConceptionEntity.getConceptionEntityUID());
        }
        uidList.add("10000000000000");// means a not exist entity's UID

        CrossKindDataOperator targetCrossKindDataOperator = coreRealm.getCrossKindDataOperator();

        List<ConceptionEntity> targetConceptionEntityList = targetCrossKindDataOperator.getConceptionEntitiesByUIDs(uidList);

        Assert.assertEquals(targetConceptionEntityList.size(),10);
        for(ConceptionEntity currentConceptionEntity:targetConceptionEntityList){
            Assert.assertEquals(currentConceptionEntity.getConceptionKindName(), testConceptionKindName1);
            Assert.assertTrue(uidList.contains(currentConceptionEntity.getConceptionEntityUID()));
        }

        String relationAttachTargetEntityUID = uidList.get(3);

        List<String> conceptionEntityPairUIDList = new ArrayList<>();
        conceptionEntityPairUIDList.add(relationAttachTargetEntityUID);

        List<String> relationUidList = new ArrayList<>();
        for(int i=0;i<50;i++){
            Map<String,Object> newEntityValue= new HashMap<>();
            newEntityValue.put("propA",i);
            newEntityValue.put("propB","String-"+i);
            Map<String,Object> newRelationEntityValue= new HashMap<>();
            newRelationEntityValue.put("propR1",i);
            newRelationEntityValue.put("propR2","String-"+i);
            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
            ConceptionEntity _ConceptionEntity = _ConceptionKind02.newEntity(conceptionEntityValue,false);
            conceptionEntityPairUIDList.add(_ConceptionEntity.getConceptionEntityUID());
            RelationEntity resultRelationEntity = _ConceptionEntity.attachFromRelation(relationAttachTargetEntityUID,"crossKindTestRelationKind",newRelationEntityValue,true);
            relationUidList.add(resultRelationEntity.getRelationEntityUID());
        }
        relationUidList.add("20000000000000");// means a not exist entity's UID

        List<RelationEntity> relationEntityList = targetCrossKindDataOperator.getRelationEntitiesByUIDs(relationUidList);

        Assert.assertEquals(relationEntityList.size(),50);
        for(RelationEntity currentRelationEntity:relationEntityList){
            Assert.assertEquals(currentRelationEntity.getRelationKindName(), "crossKindTestRelationKind");
            Assert.assertTrue(relationUidList.contains(currentRelationEntity.getRelationEntityUID()));
        }

        List<RelationEntity> relationEntityList2 = targetCrossKindDataOperator.getRelationsOfConceptionEntityPair(conceptionEntityPairUIDList);
        Assert.assertEquals(relationEntityList2.size(),50);
        for(RelationEntity currentRelationEntity:relationEntityList2){
            Assert.assertEquals(currentRelationEntity.getRelationKindName(),"crossKindTestRelationKind");
            Assert.assertEquals(relationAttachTargetEntityUID,currentRelationEntity.getToConceptionEntityUID());
            Assert.assertTrue(relationUidList.contains(currentRelationEntity.getRelationEntityUID()));
            Assert.assertTrue(conceptionEntityPairUIDList.contains(currentRelationEntity.getFromConceptionEntityUID()));
        }

        List<String> attributesNameList = new ArrayList<>();
        attributesNameList.add("prop1");
        attributesNameList.add("prop2");
        List<ConceptionEntityValue> conceptionEntityValueList = targetCrossKindDataOperator.getSingleValueConceptionEntityAttributesByUIDs(uidList,attributesNameList);

        Assert.assertEquals(conceptionEntityValueList.size(),10);
        for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
            Assert.assertTrue(uidList.contains(currentConceptionEntityValue.getConceptionEntityUID()));
            Assert.assertTrue(currentConceptionEntityValue.getEntityAttributesValue().containsKey(("prop1")));
            Assert.assertTrue(currentConceptionEntityValue.getEntityAttributesValue().containsKey(("prop2")));
        }

        attributesNameList.add("propR1");
        attributesNameList.add("propR2");
        List<RelationEntityValue> relationEntityValueList = targetCrossKindDataOperator.getSingleValueRelationEntityAttributesByUIDs(relationUidList,attributesNameList);
        Assert.assertEquals(relationEntityValueList.size(),50);
        for(RelationEntityValue currentRelationEntityValue:relationEntityValueList){
            Assert.assertTrue(!currentRelationEntityValue.getEntityAttributesValue().containsKey("prop1"));
            Assert.assertTrue(!currentRelationEntityValue.getEntityAttributesValue().containsKey("prop2"));
            Assert.assertTrue(currentRelationEntityValue.getEntityAttributesValue().containsKey(("propR1")));
            Assert.assertTrue(currentRelationEntityValue.getEntityAttributesValue().containsKey(("propR2")));
            Assert.assertTrue(relationUidList.contains(currentRelationEntityValue.getRelationEntityUID()));
            Assert.assertNotNull(currentRelationEntityValue.getFromConceptionEntityUID());
            Assert.assertNotNull(currentRelationEntityValue.getToConceptionEntityUID());
        }

        coreRealm.closeGlobalSession();
    }
}
