package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.GreaterThanEqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.NullValueFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class RelationKindTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindForRelationA";
    private static String testConceptionKindName2 = "TestConceptionKindForRelationB";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for RelationKindTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testRelationKindFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        coreRealm.openGlobalSession();
        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName2);
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        if(_ConceptionKind02 != null){
            coreRealm.removeConceptionKind(testConceptionKindName2,true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName2);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,null);
        }
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind(testConceptionKindName2,null);
        }

        RelationKind _RelationKind01 = coreRealm.getRelationKind("RelationKind0001ForTest");
        if(_RelationKind01 != null){
            coreRealm.removeRelationKind("RelationKind0001ForTest",true);
        }
        _RelationKind01 = coreRealm.getRelationKind("RelationKind0001ForTest");
        if(_RelationKind01 == null){
            _RelationKind01 = coreRealm.createRelationKind("RelationKind0001ForTest",null);
        }

        for(int i =0;i<10;i++){
            Map<String,Object> newEntityValue= new HashMap<>();
            newEntityValue.put("prop1",Math.random());
            newEntityValue.put("prop2",Math.random()+100000);
            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);

            ConceptionEntity _ConceptionEntity_1 = _ConceptionKind01.newEntity(conceptionEntityValue,false);
            ConceptionEntity _ConceptionEntity_2 = _ConceptionKind02.newEntity(conceptionEntityValue,false);

            Map<String,Object> relationPropertiesValue= new HashMap<>();
            relationPropertiesValue.put("relProp1",Math.random()*1000);
            relationPropertiesValue.put("temProp1", LocalTime.now());
            relationPropertiesValue.put("temProp2", new LocalDate[]{LocalDate.now()});

            RelationEntity resultRelationEntity = _ConceptionEntity_1.attachFromRelation(_ConceptionEntity_2.getConceptionEntityUID(),"RelationKind0001ForTest",relationPropertiesValue,false);
            Assert.assertNotNull(resultRelationEntity);
            Assert.assertNotNull(resultRelationEntity.getAttribute("temProp1").getAttributeValue());
            Assert.assertTrue(resultRelationEntity.getAttribute("temProp1").getAttributeValue() instanceof LocalTime);
            Assert.assertEquals(resultRelationEntity.getAttribute("temProp1").getAttributeDataType(),AttributeDataType.TIME);
            Assert.assertNotNull(resultRelationEntity.getAttribute("temProp2").getAttributeValue());
            Assert.assertTrue(resultRelationEntity.getAttribute("temProp2").getAttributeValue() instanceof LocalDate[]);
            Assert.assertEquals(resultRelationEntity.getAttribute("temProp2").getAttributeDataType(),AttributeDataType.DATE_ARRAY);
        }

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDistinctMode(true);
        RelationEntitiesRetrieveResult _RelationEntitiesRetrieveResult = _RelationKind01.getRelationEntities(queryParameters);

        Assert.assertNotNull(_RelationEntitiesRetrieveResult);
        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getOperationStatistics());

        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getOperationStatistics().getQueryParameters());
        Assert.assertEquals(_RelationEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount(),10l);

        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getOperationStatistics().getFinishTime());

        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getRelationEntities());
        Assert.assertEquals(_RelationEntitiesRetrieveResult.getRelationEntities().size(),10);

        for(RelationEntity currentRelationEntity : _RelationEntitiesRetrieveResult.getRelationEntities()){
            Assert.assertEquals(currentRelationEntity.getRelationKindName(),"RelationKind0001ForTest");
            Assert.assertNotNull(currentRelationEntity.getFromConceptionEntityUID());
            Assert.assertNotNull(currentRelationEntity.getToConceptionEntityUID());
        }

        queryParameters.setResultNumber(7);
        _RelationEntitiesRetrieveResult = _RelationKind01.getRelationEntities(queryParameters);
        Assert.assertNotNull(_RelationEntitiesRetrieveResult);
        Assert.assertEquals(_RelationEntitiesRetrieveResult.getRelationEntities().size(),7);

        queryParameters.setResultNumber(20);
        _RelationEntitiesRetrieveResult = _RelationKind01.getRelationEntities(queryParameters);
        Assert.assertNotNull(_RelationEntitiesRetrieveResult);
        Assert.assertEquals(_RelationEntitiesRetrieveResult.getRelationEntities().size(),10);

        queryParameters.setResultNumber(500);
        queryParameters.setDefaultFilteringItem(new GreaterThanEqualFilteringItem("relProp1",500l));
        _RelationEntitiesRetrieveResult = _RelationKind01.getRelationEntities(queryParameters);
        Assert.assertNotNull(_RelationEntitiesRetrieveResult);
        Assert.assertTrue(_RelationEntitiesRetrieveResult.getRelationEntities().size()<10);

        AttributesParameters attributesParameters = new AttributesParameters();
        attributesParameters.setDefaultFilteringItem(new GreaterThanEqualFilteringItem("relProp1",500l));
        Long entityCount = _RelationKind01.countRelationEntities(attributesParameters,true);
        long res1 = (_RelationEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount());
        Assert.assertEquals(res1,entityCount.longValue());

        Assert.assertEquals(_RelationKind01.countRelationEntities(),new Long(10));

        Set<RelationEntity> relationEntitySet = _RelationKind01.getRandomEntities(5);
        Assert.assertNotNull(relationEntitySet);
        Assert.assertEquals(relationEntitySet.size(),5);
        for(RelationEntity currentEntity:relationEntitySet){
            Assert.assertEquals(currentEntity.getRelationKindName(),"RelationKind0001ForTest");
            Assert.assertNotNull(currentEntity.getRelationEntityUID());
        }

        AttributesParameters attributesParameters2 = new AttributesParameters();
        attributesParameters2.setDefaultFilteringItem(new NullValueFilteringItem("mustNotExistAttr"));
        relationEntitySet = _RelationKind01.getRandomEntities(attributesParameters2,true,5);
        Assert.assertNotNull(relationEntitySet);
        Assert.assertEquals(relationEntitySet.size(),5);
        for(RelationEntity currentEntity:relationEntitySet){
            Assert.assertEquals(currentEntity.getRelationKindName(),"RelationKind0001ForTest");
            Assert.assertNotNull(currentEntity.getRelationEntityUID());
        }

        attributesParameters2 = new AttributesParameters();
        attributesParameters2.setDefaultFilteringItem(new EqualFilteringItem("mustNotExistAttr",1000));
        relationEntitySet = _RelationKind01.getRandomEntities(attributesParameters2,false,5);
        Assert.assertNotNull(relationEntitySet);
        Assert.assertEquals(relationEntitySet.size(),0);

        EntitiesOperationResult purgeAllOperationResult = _RelationKind01.purgeAllRelationEntities();
        Assert.assertNotNull(purgeAllOperationResult);
        Assert.assertNotNull(purgeAllOperationResult.getOperationStatistics());

        Assert.assertEquals(purgeAllOperationResult.getOperationStatistics().getSuccessItemsCount(),10);
        Assert.assertEquals(purgeAllOperationResult.getOperationStatistics().getFailItemsCount(),0);

        Assert.assertNotNull(purgeAllOperationResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(purgeAllOperationResult.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(purgeAllOperationResult.getOperationStatistics().getOperationSummary());

        Assert.assertEquals(_RelationKind01.countRelationEntities(),new Long(0));

        long selfAttachedRemoveResult = _RelationKind01.purgeRelationsOfSelfAttachedConceptionEntities();
        Assert.assertEquals(selfAttachedRemoveResult,0);

        Map<String,Object> newEntityValue= new HashMap<>();
        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
        ConceptionEntity _ConceptionEntity_3 = _ConceptionKind01.newEntity(conceptionEntityValue,false);

        for(int i=0;i<10;i++) {
            _ConceptionEntity_3.attachFromRelation(_ConceptionEntity_3.getConceptionEntityUID(), "RelationKind0001ForTest", null, false);
        }
        selfAttachedRemoveResult = _RelationKind01.purgeRelationsOfSelfAttachedConceptionEntities();
        Assert.assertEquals(selfAttachedRemoveResult,1);

        List<String> relationEntityUIDList = new ArrayList<>();
        for(int i=0;i<10;i++) {
            RelationEntity currentRelationEntity = _ConceptionEntity_3.attachFromRelation(_ConceptionEntity_3.getConceptionEntityUID(), "RelationKind0001ForTest", null, true);
            relationEntityUIDList.add(currentRelationEntity.getRelationEntityUID());
        }

        boolean exceptionShouldThrown = false;
        try {
            _RelationKind01.deleteEntity("12345");
        }catch(CoreRealmServiceException e){
            exceptionShouldThrown = true;
        }
        Assert.assertTrue(exceptionShouldThrown);

        RelationEntity targetRelationEntity = _RelationKind01.getEntityByUID(relationEntityUIDList.get(0));
        Assert.assertNotNull(targetRelationEntity);
        Assert.assertNotNull(targetRelationEntity.getFromConceptionEntityKinds());
        Assert.assertNotNull(targetRelationEntity.getToConceptionEntityKinds());
        Assert.assertEquals(targetRelationEntity.getFromConceptionEntityKinds().get(0),testConceptionKindName);
        Assert.assertEquals(targetRelationEntity.getToConceptionEntityKinds().get(0),testConceptionKindName);

        Map<String,Object> kindScopeAttributeMap = new HashMap<>();
        kindScopeAttributeMap.put("dateTypeAttrA",new Date());
        kindScopeAttributeMap.put("intTypeAttr",1000);
        EntitiesOperationStatistics addAttrResult = _RelationKind01.setKindScopeAttributes(kindScopeAttributeMap);
        Assert.assertEquals(addAttrResult.getSuccessItemsCount(),_RelationKind01.countRelationEntities().longValue());
        String randomEntityId = _RelationKind01.getRandomEntities(1).iterator().next().getRelationEntityUID();
        RelationEntity randomEntity = _RelationKind01.getEntityByUID(randomEntityId);
        Assert.assertNotNull(randomEntity.getAttribute("dateTypeAttrA"));
        Assert.assertEquals(((Long)randomEntity.getAttribute("intTypeAttr").getAttributeValue()).longValue(),1000l);

        boolean  deleteSingleEntityResult = _RelationKind01.deleteEntity(relationEntityUIDList.get(0));
        Assert.assertTrue(deleteSingleEntityResult);

        List<String> uidsForMultiDelete = new ArrayList<>();
        uidsForMultiDelete.add(relationEntityUIDList.get(1));
        uidsForMultiDelete.add(relationEntityUIDList.get(2));
        uidsForMultiDelete.add("1234567890");
        EntitiesOperationResult entitiesOperationResult = _RelationKind01.deleteEntities(uidsForMultiDelete);
        Assert.assertEquals(entitiesOperationResult.getSuccessEntityUIDs().size(),2);
        Assert.assertEquals(entitiesOperationResult.getOperationStatistics().getFailItemsCount(),1);

        selfAttachedRemoveResult = _RelationKind01.purgeRelationsOfSelfAttachedConceptionEntities();
        Assert.assertEquals(selfAttachedRemoveResult,10-3);

        boolean updateDescResult = _RelationKind01.updateRelationKindDesc("TestRelationKindADesc+中文描述UPD");
        Assert.assertTrue(updateDescResult);
        Assert.assertEquals(_RelationKind01.getRelationKindDesc(),"TestRelationKindADesc+中文描述UPD");
        Assert.assertEquals(coreRealm.getRelationKind("RelationKind0001ForTest").getRelationKindDesc(),"TestRelationKindADesc+中文描述UPD");

        List<AttributesViewKind> containedAttributesViewKindsList = _RelationKind01.getContainsAttributesViewKinds();
        Assert.assertNotNull(containedAttributesViewKindsList);
        Assert.assertEquals(containedAttributesViewKindsList.size(),0);

        AttributesViewKind attributesViewKindToAdd01 = coreRealm.createAttributesViewKind("targetAttributesViewKindToAdd01_forRel","targetAttributesViewKindToAdd01Desc_forRel",null);

        boolean addAttributesViewKindResult = _RelationKind01.attachAttributesViewKind(attributesViewKindToAdd01.getAttributesViewKindUID());
        Assert.assertTrue(addAttributesViewKindResult);
        addAttributesViewKindResult = _RelationKind01.attachAttributesViewKind(attributesViewKindToAdd01.getAttributesViewKindUID());
        Assert.assertTrue(addAttributesViewKindResult);
        addAttributesViewKindResult = _RelationKind01.attachAttributesViewKind(null);
        Assert.assertFalse(addAttributesViewKindResult);

        boolean exceptionShouldBeCaught = false;
        try{
            _RelationKind01.attachAttributesViewKind("445566778");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        containedAttributesViewKindsList = _RelationKind01.getContainsAttributesViewKinds();
        Assert.assertNotNull(containedAttributesViewKindsList);
        Assert.assertEquals(containedAttributesViewKindsList.size(),1);

        Assert.assertEquals(containedAttributesViewKindsList.get(0).getAttributesViewKindName(),"targetAttributesViewKindToAdd01_forRel");
        Assert.assertEquals(containedAttributesViewKindsList.get(0).getAttributesViewKindDesc(),"targetAttributesViewKindToAdd01Desc_forRel");
        Assert.assertEquals(containedAttributesViewKindsList.get(0).getAttributesViewKindUID(),attributesViewKindToAdd01.getAttributesViewKindUID());
        Assert.assertEquals(containedAttributesViewKindsList.get(0).getAttributesViewKindDataForm(), AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE);

        AttributesViewKind attributesViewKindToAdd02 = coreRealm.createAttributesViewKind("targetAttributesViewKindToAdd02_forRel",
                "targetAttributesViewKindToAdd02Desc",AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE);
        addAttributesViewKindResult = _RelationKind01.attachAttributesViewKind(attributesViewKindToAdd02.getAttributesViewKindUID());
        Assert.assertTrue(addAttributesViewKindResult);

        containedAttributesViewKindsList = _RelationKind01.getContainsAttributesViewKinds();
        Assert.assertNotNull(containedAttributesViewKindsList);
        Assert.assertEquals(containedAttributesViewKindsList.size(),2);

        List<AttributesViewKind> targetAttributesViewKindList = _RelationKind01.getContainsAttributesViewKinds("targetAttributesViewKindToAdd02_forRel");
        Assert.assertNotNull(targetAttributesViewKindList);
        Assert.assertNotNull(targetAttributesViewKindList.get(0));
        Assert.assertEquals(targetAttributesViewKindList.get(0).getAttributesViewKindName(),"targetAttributesViewKindToAdd02_forRel");
        Assert.assertEquals(targetAttributesViewKindList.get(0).getAttributesViewKindDesc(),"targetAttributesViewKindToAdd02Desc");
        Assert.assertEquals(targetAttributesViewKindList.get(0).getAttributesViewKindUID(),attributesViewKindToAdd02.getAttributesViewKindUID());
        Assert.assertEquals(targetAttributesViewKindList.get(0).getAttributesViewKindDataForm(), AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE);

        List<AttributeKind> attributeKindList = _RelationKind01.getContainsSingleValueAttributeKinds();
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),0);

        AttributeKind attributeKind01 = coreRealm.createAttributeKind("attributeKind01","attributeKind01Desc", AttributeDataType.BOOLEAN);
        Assert.assertNotNull(attributeKind01);

        boolean attachAttributeKindRes = attributesViewKindToAdd02.attachAttributeKind(attributeKind01.getAttributeKindUID());
        Assert.assertTrue(attachAttributeKindRes);

        attributeKindList = _RelationKind01.getContainsSingleValueAttributeKinds();
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),0);

        AttributeKind attributeKind02 = coreRealm.createAttributeKind("attributeKind02","attributeKind02Desc", AttributeDataType.TIMESTAMP);
        Assert.assertNotNull(attributeKind02);

        attachAttributeKindRes = attributesViewKindToAdd01.attachAttributeKind(attributeKind02.getAttributeKindUID());
        Assert.assertTrue(attachAttributeKindRes);

        attributeKindList = _RelationKind01.getContainsSingleValueAttributeKinds();
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),1);
        Assert.assertNotNull(attributeKindList.get(0).getAttributeKindUID());
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindName(),"attributeKind02");
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindDesc(),"attributeKind02Desc");
        Assert.assertEquals(attributeKindList.get(0).getAttributeDataType(),AttributeDataType.TIMESTAMP);

        attributeKindList = _RelationKind01.getContainsSingleValueAttributeKinds("attributeKind02");
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),1);
        Assert.assertNotNull(attributeKindList.get(0).getAttributeKindUID());
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindName(),"attributeKind02");
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindDesc(),"attributeKind02Desc");
        Assert.assertEquals(attributeKindList.get(0).getAttributeDataType(),AttributeDataType.TIMESTAMP);

        attributeKindList = _RelationKind01.getContainsSingleValueAttributeKinds("attributeKind02+NotExist");
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),0);

        targetAttributesViewKindList = _RelationKind01.getContainsAttributesViewKinds("targetAttributesViewKindNotExist");
        Assert.assertEquals(targetAttributesViewKindList.size(),0);

        targetAttributesViewKindList = _RelationKind01.getContainsAttributesViewKinds("targetAttributesViewKindToAdd02_forRel");
        boolean removeViewKindResult = _RelationKind01.detachAttributesViewKind(targetAttributesViewKindList.get(0).getAttributesViewKindUID());
        Assert.assertTrue(removeViewKindResult);
        removeViewKindResult = _RelationKind01.detachAttributesViewKind(targetAttributesViewKindList.get(0).getAttributesViewKindUID());
        Assert.assertFalse(removeViewKindResult);

        targetAttributesViewKindList = _RelationKind01.getContainsAttributesViewKinds("targetAttributesViewKindToAdd02_forRel");
        Assert.assertEquals(targetAttributesViewKindList.size(),0);

        coreRealm.closeGlobalSession();
    }
}
