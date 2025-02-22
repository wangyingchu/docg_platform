package com.viewfunction.docg.testcase.coreRealm.termTest_neo4j;

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

        Map<String, Map<String, Object>> conceptionEntityUIDAndAttributesMap = new HashMap<>();

        Map<String,Object> conceptionEntity1ValueMap = new HashMap<>();
        conceptionEntity1ValueMap.put("conceptionEntity1Prop1",1000);
        conceptionEntity1ValueMap.put("conceptionEntity1Prop2","STRING1");
        conceptionEntityUIDAndAttributesMap.put(conceptionEntityValueList.get(0).getConceptionEntityUID(),conceptionEntity1ValueMap);

        Map<String,Object> conceptionEntity2ValueMap = new HashMap<>();
        conceptionEntity2ValueMap.put("conceptionEntity2Prop1",2000);
        conceptionEntity2ValueMap.put("conceptionEntity2Prop2","STRING2");
        conceptionEntityUIDAndAttributesMap.put(conceptionEntityValueList.get(1).getConceptionEntityUID(),conceptionEntity2ValueMap);

        EntitiesOperationResult entitiesOperationResult1 = targetCrossKindDataOperator.updateSingleValueConceptionEntityAttributesByUIDs(conceptionEntityUIDAndAttributesMap);
        Assert.assertEquals(entitiesOperationResult1.getOperationStatistics().getSuccessItemsCount(),2);
        Assert.assertEquals(entitiesOperationResult1.getOperationStatistics().getFailItemsCount(),0);
        Assert.assertNotNull(entitiesOperationResult1.getOperationStatistics().getStartTime());
        Assert.assertNotNull(entitiesOperationResult1.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(entitiesOperationResult1.getOperationStatistics().getOperationSummary());
        Assert.assertTrue(entitiesOperationResult1.getSuccessEntityUIDs().contains(conceptionEntityValueList.get(0).getConceptionEntityUID()));
        Assert.assertTrue(entitiesOperationResult1.getSuccessEntityUIDs().contains(conceptionEntityValueList.get(1).getConceptionEntityUID()));

        List<String> targetEntityUIDList = new ArrayList<>();
        targetEntityUIDList.add(conceptionEntityValueList.get(0).getConceptionEntityUID());
        targetEntityUIDList.add(conceptionEntityValueList.get(1).getConceptionEntityUID());
        List<ConceptionEntity> resultConceptionEntities = targetCrossKindDataOperator.getConceptionEntitiesByUIDs(targetEntityUIDList);
        Assert.assertEquals(resultConceptionEntities.size(),2);

        for(ConceptionEntity currentConceptionEntity:resultConceptionEntities){
            if(currentConceptionEntity.getConceptionEntityUID().equals(conceptionEntityValueList.get(0).getConceptionEntityUID())){
                Assert.assertEquals((Long) (currentConceptionEntity.getAttribute("conceptionEntity1Prop1").getAttributeValue()),1000);
                Assert.assertEquals(currentConceptionEntity.getAttribute("conceptionEntity1Prop2").getAttributeValue().toString(),"STRING1");
            }
            if(currentConceptionEntity.getConceptionEntityUID().equals(conceptionEntityValueList.get(1).getConceptionEntityUID())){
                Assert.assertEquals((Long) (currentConceptionEntity.getAttribute("conceptionEntity2Prop1").getAttributeValue()),2000);
                Assert.assertEquals(currentConceptionEntity.getAttribute("conceptionEntity2Prop2").getAttributeValue().toString(),"STRING2");
            }
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

        Map<String, Map<String, Object>> relationEntityUIDAndAttributesMap = new HashMap<>();
        Map<String,Object> relationEntity1ValueMap = new HashMap<>();
        relationEntity1ValueMap.put("relationEntity1Prop1",1000);
        relationEntity1ValueMap.put("relationEntity1Prop2","STRING1");
        relationEntityUIDAndAttributesMap.put(relationEntityValueList.get(0).getRelationEntityUID(),relationEntity1ValueMap);

        Map<String,Object> relationEntity2ValueMap = new HashMap<>();
        relationEntity2ValueMap.put("relationEntity2Prop1",2000);
        relationEntity2ValueMap.put("relationEntity2Prop2","STRING2");
        relationEntityUIDAndAttributesMap.put(relationEntityValueList.get(1).getRelationEntityUID(),relationEntity2ValueMap);

        EntitiesOperationResult entitiesOperationResult2 = targetCrossKindDataOperator.updateSingleValueRelationEntityAttributesByUIDs(relationEntityUIDAndAttributesMap);
        Assert.assertEquals(entitiesOperationResult2.getOperationStatistics().getSuccessItemsCount(),2);
        Assert.assertEquals(entitiesOperationResult2.getOperationStatistics().getFailItemsCount(),0);
        Assert.assertNotNull(entitiesOperationResult2.getOperationStatistics().getStartTime());
        Assert.assertNotNull(entitiesOperationResult2.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(entitiesOperationResult2.getOperationStatistics().getOperationSummary());
        Assert.assertTrue(entitiesOperationResult2.getSuccessEntityUIDs().contains(relationEntityValueList.get(0).getRelationEntityUID()));
        Assert.assertTrue(entitiesOperationResult2.getSuccessEntityUIDs().contains(relationEntityValueList.get(1).getRelationEntityUID()));

        targetEntityUIDList = new ArrayList<>();
        targetEntityUIDList.add(relationEntityValueList.get(0).getRelationEntityUID());
        targetEntityUIDList.add(relationEntityValueList.get(1).getRelationEntityUID());
        List<RelationEntity> resultRelationEntities = targetCrossKindDataOperator.getRelationEntitiesByUIDs(targetEntityUIDList);
        Assert.assertEquals(resultRelationEntities.size(),2);

        for(RelationEntity currentRelationEntity:resultRelationEntities){
            if(currentRelationEntity.getRelationEntityUID().equals(conceptionEntityValueList.get(0).getConceptionEntityUID())){
                Assert.assertEquals((Long) (currentRelationEntity.getAttribute("relationEntity1Prop1").getAttributeValue()),1000);
                Assert.assertEquals(currentRelationEntity.getAttribute("relationEntity1Prop2").getAttributeValue().toString(),"STRING1");
            }
            if(currentRelationEntity.getRelationEntityUID().equals(conceptionEntityValueList.get(1).getConceptionEntityUID())){
                Assert.assertEquals((Long) (currentRelationEntity.getAttribute("relationEntity2Prop1").getAttributeValue()),2000);
                Assert.assertEquals(currentRelationEntity.getAttribute("relationEntity2Prop2").getAttributeValue().toString(),"STRING2");
            }
        }

        coreRealm.closeGlobalSession();
    }
}
