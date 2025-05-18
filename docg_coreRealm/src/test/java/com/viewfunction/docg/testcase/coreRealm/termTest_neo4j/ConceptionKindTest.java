package com.viewfunction.docg.testcase.coreRealm.termTest_neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.ClassificationAttachParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.FixConceptionEntityAttachParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.GreaterThanFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.NullValueFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ConceptionKindTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindA";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ConceptionKindTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testConceptionKindFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"TestConceptionKindADesc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),testConceptionKindName);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"TestConceptionKindADesc+中文描述");
        }

        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName+"_TestRelation");
        if(_ConceptionKind02 != null){
            coreRealm.removeConceptionKind(testConceptionKindName+"_TestRelation",true);
        }
        _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName+"_TestRelation");
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind(testConceptionKindName+"_TestRelation","TestConceptionKind_TestRelationADesc+中文描述");
            Assert.assertNotNull(_ConceptionKind02);
            Assert.assertEquals(_ConceptionKind02.getConceptionKindName(),testConceptionKindName+"_TestRelation");
            Assert.assertEquals(_ConceptionKind02.getConceptionKindDesc(),"TestConceptionKind_TestRelationADesc+中文描述");
        }

        EntitiesOperationResult purgeEntitiesOperationResult = _ConceptionKind01.purgeAllEntities();
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getOperationSummary());
        Assert.assertEquals(purgeEntitiesOperationResult.getOperationStatistics().getFailItemsCount(),0);

        Long entitiesCount = _ConceptionKind01.countConceptionEntities();
        Assert.assertEquals(entitiesCount.longValue(),0);

        List<AttributesViewKind> containedAttributesViewKindsList = _ConceptionKind01.getContainsAttributesViewKinds();
        Assert.assertNotNull(containedAttributesViewKindsList);
        Assert.assertEquals(containedAttributesViewKindsList.size(),0);

        AttributesViewKind attributesViewKindToAdd01 = coreRealm.createAttributesViewKind("targetAttributesViewKindToAdd01","targetAttributesViewKindToAdd01Desc",null);

        boolean addAttributesViewKindResult = _ConceptionKind01.attachAttributesViewKind(attributesViewKindToAdd01.getAttributesViewKindUID());
        Assert.assertTrue(addAttributesViewKindResult);
        addAttributesViewKindResult = _ConceptionKind01.attachAttributesViewKind(attributesViewKindToAdd01.getAttributesViewKindUID());
        Assert.assertTrue(addAttributesViewKindResult);
        addAttributesViewKindResult = _ConceptionKind01.attachAttributesViewKind(null);
        Assert.assertFalse(addAttributesViewKindResult);

        boolean exceptionShouldBeCaught = false;
        try{
            _ConceptionKind01.attachAttributesViewKind("123456");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        containedAttributesViewKindsList = _ConceptionKind01.getContainsAttributesViewKinds();
        Assert.assertNotNull(containedAttributesViewKindsList);
        Assert.assertEquals(containedAttributesViewKindsList.size(),1);

        Assert.assertEquals(containedAttributesViewKindsList.get(0).getAttributesViewKindName(),"targetAttributesViewKindToAdd01");
        Assert.assertEquals(containedAttributesViewKindsList.get(0).getAttributesViewKindDesc(),"targetAttributesViewKindToAdd01Desc");
        Assert.assertEquals(containedAttributesViewKindsList.get(0).getAttributesViewKindUID(),attributesViewKindToAdd01.getAttributesViewKindUID());
        Assert.assertEquals(containedAttributesViewKindsList.get(0).getAttributesViewKindDataForm(), AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE);

        AttributesViewKind attributesViewKindToAdd02 = coreRealm.createAttributesViewKind("targetAttributesViewKindToAdd02",
                "targetAttributesViewKindToAdd02Desc",AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE);
        addAttributesViewKindResult = _ConceptionKind01.attachAttributesViewKind(attributesViewKindToAdd02.getAttributesViewKindUID());
        Assert.assertTrue(addAttributesViewKindResult);

        containedAttributesViewKindsList = _ConceptionKind01.getContainsAttributesViewKinds();
        Assert.assertNotNull(containedAttributesViewKindsList);
        Assert.assertEquals(containedAttributesViewKindsList.size(),2);

        List<AttributesViewKind> targetAttributesViewKindList = _ConceptionKind01.getContainsAttributesViewKinds("targetAttributesViewKindToAdd02");
        Assert.assertNotNull(targetAttributesViewKindList);
        Assert.assertNotNull(targetAttributesViewKindList.get(0));
        Assert.assertEquals(targetAttributesViewKindList.get(0).getAttributesViewKindName(),"targetAttributesViewKindToAdd02");
        Assert.assertEquals(targetAttributesViewKindList.get(0).getAttributesViewKindDesc(),"targetAttributesViewKindToAdd02Desc");
        Assert.assertEquals(targetAttributesViewKindList.get(0).getAttributesViewKindUID(),attributesViewKindToAdd02.getAttributesViewKindUID());
        Assert.assertEquals(targetAttributesViewKindList.get(0).getAttributesViewKindDataForm(), AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE);

        List<AttributeKind> attributeKindList = _ConceptionKind01.getContainsSingleValueAttributeKinds();
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),0);

        AttributeKind attributeKind01 = coreRealm.createAttributeKind("attributeKind01","attributeKind01Desc", AttributeDataType.BOOLEAN);
        Assert.assertNotNull(attributeKind01);

        boolean attachAttributeKindRes = attributesViewKindToAdd02.attachAttributeKind(attributeKind01.getAttributeKindUID());
        Assert.assertTrue(attachAttributeKindRes);

        attributeKindList = _ConceptionKind01.getContainsSingleValueAttributeKinds();
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),0);

        AttributeKind attributeKind02 = coreRealm.createAttributeKind("attributeKind02","attributeKind02Desc", AttributeDataType.TIMESTAMP);
        Assert.assertNotNull(attributeKind02);

        attachAttributeKindRes = attributesViewKindToAdd01.attachAttributeKind(attributeKind02.getAttributeKindUID());
        Assert.assertTrue(attachAttributeKindRes);

        attributeKindList = _ConceptionKind01.getContainsSingleValueAttributeKinds();
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),1);
        Assert.assertNotNull(attributeKindList.get(0).getAttributeKindUID());
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindName(),"attributeKind02");
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindDesc(),"attributeKind02Desc");
        Assert.assertEquals(attributeKindList.get(0).getAttributeDataType(),AttributeDataType.TIMESTAMP);

        attributeKindList = _ConceptionKind01.getContainsSingleValueAttributeKinds("attributeKind02");
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),1);
        Assert.assertNotNull(attributeKindList.get(0).getAttributeKindUID());
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindName(),"attributeKind02");
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindDesc(),"attributeKind02Desc");
        Assert.assertEquals(attributeKindList.get(0).getAttributeDataType(),AttributeDataType.TIMESTAMP);

        attributeKindList = _ConceptionKind01.getContainsSingleValueAttributeKinds("attributeKind02+NotExist");
        Assert.assertNotNull(attributeKindList);
        Assert.assertEquals(attributeKindList.size(),0);

        targetAttributesViewKindList = _ConceptionKind01.getContainsAttributesViewKinds("targetAttributesViewKindNotExist");
        Assert.assertEquals(targetAttributesViewKindList.size(),0);

        targetAttributesViewKindList = _ConceptionKind01.getContainsAttributesViewKinds("targetAttributesViewKindToAdd02");
        boolean removeViewKindResult = _ConceptionKind01.detachAttributesViewKind(targetAttributesViewKindList.get(0).getAttributesViewKindUID());
        Assert.assertTrue(removeViewKindResult);
        removeViewKindResult = _ConceptionKind01.detachAttributesViewKind(targetAttributesViewKindList.get(0).getAttributesViewKindUID());
        Assert.assertFalse(removeViewKindResult);

        targetAttributesViewKindList = _ConceptionKind01.getContainsAttributesViewKinds("targetAttributesViewKindToAdd02");
        Assert.assertEquals(targetAttributesViewKindList.size(),0);

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);
        newEntityValue.put("prop2",190.22d);
        newEntityValue.put("prop3",50);
        newEntityValue.put("prop4","thi is s string");
        newEntityValue.put("prop5","我是中文string");
        newEntityValue.put("propTmp1", LocalDate.of(1667,1,1));
        newEntityValue.put("propTmp2", new LocalTime[]{LocalTime.of(13,3,3),
                LocalTime.of(14,4,4)});

        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);

        ConceptionEntity _ConceptionEntity = _ConceptionKind01.newEntity(conceptionEntityValue,false);
        Assert.assertNotNull(_ConceptionEntity);
        Assert.assertEquals(_ConceptionEntity.getConceptionKindName(),testConceptionKindName);
        Assert.assertEquals(_ConceptionEntity.getAllConceptionKindNames().size(),1);
        Assert.assertEquals(_ConceptionEntity.getAllConceptionKindNames().get(0),testConceptionKindName);
        Assert.assertNotNull(_ConceptionEntity.getConceptionEntityUID());

        String queryUIDValue = _ConceptionEntity.getConceptionEntityUID();
        ConceptionEntity _queryResultConceptionEntity = _ConceptionKind01.getEntityByUID(queryUIDValue);

        Assert.assertNotNull(_queryResultConceptionEntity);
        Assert.assertEquals(_queryResultConceptionEntity.getConceptionKindName(),testConceptionKindName);
        Assert.assertEquals(_queryResultConceptionEntity.getAllConceptionKindNames().size(),1);
        Assert.assertEquals(_queryResultConceptionEntity.getAllConceptionKindNames().get(0),testConceptionKindName);
        Assert.assertEquals(_queryResultConceptionEntity.getConceptionEntityUID(),queryUIDValue);

        List<String> attributeNameList = _queryResultConceptionEntity.getAttributeNames();

        Assert.assertNotNull(attributeNameList);
        Assert.assertEquals(attributeNameList.size(),7);
        Assert.assertTrue(attributeNameList.contains("prop1"));
        Assert.assertTrue(attributeNameList.contains("prop2"));
        Assert.assertTrue(attributeNameList.contains("prop3"));
        Assert.assertTrue(attributeNameList.contains("prop4"));
        Assert.assertTrue(attributeNameList.contains("prop5"));
        Assert.assertTrue(attributeNameList.contains("propTmp1"));
        Assert.assertTrue(attributeNameList.contains("propTmp2"));

        List<AttributeValue> attributeValueList = _queryResultConceptionEntity.getAttributes();
        Assert.assertNotNull(attributeValueList);
        Assert.assertEquals(attributeValueList.size(),7);

        entitiesCount = _ConceptionKind01.countConceptionEntities();
        Assert.assertEquals(entitiesCount.longValue(),1);

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
        newEntityValueMap.put("prop22", LocalDate.of(1667,1,1));
        newEntityValueMap.put("prop23", new LocalTime[]{LocalTime.of(13,3,3),
                LocalTime.of(14,4,4)});

        List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();
        ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValueMap);
        ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValueMap);
        ConceptionEntityValue conceptionEntityValue3 = new ConceptionEntityValue(newEntityValueMap);
        conceptionEntityValueList.add(conceptionEntityValue1);
        conceptionEntityValueList.add(conceptionEntityValue2);
        conceptionEntityValueList.add(conceptionEntityValue3);

        EntitiesOperationResult addEntitiesResult = _ConceptionKind01.newEntities(conceptionEntityValueList,false);
        Assert.assertNotNull(addEntitiesResult);
        Assert.assertNotNull(addEntitiesResult.getSuccessEntityUIDs());
        Assert.assertNotNull(addEntitiesResult.getOperationStatistics());
        Assert.assertEquals(addEntitiesResult.getSuccessEntityUIDs().size(),3);
        Assert.assertEquals(addEntitiesResult.getOperationStatistics().getSuccessItemsCount(),3);
        Assert.assertNotNull(addEntitiesResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(addEntitiesResult.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(addEntitiesResult.getOperationStatistics().getOperationSummary());
        Assert.assertEquals(addEntitiesResult.getOperationStatistics().getFailItemsCount(),0);

        entitiesCount = _ConceptionKind01.countConceptionEntities();
        Assert.assertEquals(entitiesCount.longValue(),4);

        for(String currentEntityUID:addEntitiesResult.getSuccessEntityUIDs()){
            ConceptionEntity currentConceptionEntity = _ConceptionKind01.getEntityByUID(currentEntityUID);
            Assert.assertNotNull(currentConceptionEntity);
            Assert.assertEquals(currentConceptionEntity.getAttributes().size(),23);
        }

        ConceptionEntity currentConceptionEntity = _ConceptionKind01.getEntityByUID(addEntitiesResult.getSuccessEntityUIDs().get(0));

        Assert.assertEquals(currentConceptionEntity.getAttribute("prop1").getAttributeValue(),Long.parseLong("12345"));
        Assert.assertEquals(currentConceptionEntity.getAttribute("prop2").getAttributeValue(),Double.parseDouble("12345.789"));
        Assert.assertEquals(currentConceptionEntity.getAttribute("prop22").getAttributeValue(),LocalDate.of(1667,1,1));

        Map<String,Object> updateEntityValueMap= new HashMap<>();
        updateEntityValueMap.put("prop1",Long.parseLong("59000"));
        updateEntityValueMap.put("prop2",Double.parseDouble("10000000.1"));
        updateEntityValueMap.put("prop3",new Date());
        updateEntityValueMap.put("prop3_NotExist",new Date());
        updateEntityValueMap.put("prop21", new Byte[]{Byte.valueOf("88"),Byte.valueOf("77"),Byte.valueOf("66")});
        updateEntityValueMap.put("prop23", new LocalTime[]{LocalTime.of(0,0,0)});

        ConceptionEntityValue conceptionEntityValueForUpdate = new ConceptionEntityValue(updateEntityValueMap);
        conceptionEntityValueForUpdate.setConceptionEntityUID(addEntitiesResult.getSuccessEntityUIDs().get(0));

        ConceptionEntity updatedConceptionEntityValue = _ConceptionKind01.updateEntity(conceptionEntityValueForUpdate);
        Assert.assertNotNull(updatedConceptionEntityValue);
        Assert.assertEquals(updatedConceptionEntityValue.getAttribute("prop1").getAttributeValue(),Long.parseLong("59000"));
        Assert.assertEquals(updatedConceptionEntityValue.getAttribute("prop2").getAttributeValue(),Double.parseDouble("10000000.1"));
        Assert.assertEquals(updatedConceptionEntityValue.getAttribute("prop3").getAttributeValue(),Long.parseLong("1234"));
        Assert.assertEquals(((LocalTime[])updatedConceptionEntityValue.getAttribute("prop23").getAttributeValue())[0],LocalTime.of(0,0,0));
        Assert.assertFalse(updatedConceptionEntityValue.hasAttribute("prop3_NotExist"));

        List<ConceptionEntityValue> conceptionEntityValueForUpdateList = new ArrayList<>();
        ConceptionEntityValue conceptionEntityValueForUpdate1 = new ConceptionEntityValue(updateEntityValueMap);
        conceptionEntityValueForUpdate1.setConceptionEntityUID(addEntitiesResult.getSuccessEntityUIDs().get(0));
        conceptionEntityValueForUpdateList.add(conceptionEntityValueForUpdate1);
        ConceptionEntityValue conceptionEntityValueForUpdate2 = new ConceptionEntityValue(updateEntityValueMap);
        conceptionEntityValueForUpdate2.setConceptionEntityUID(addEntitiesResult.getSuccessEntityUIDs().get(1));
        conceptionEntityValueForUpdateList.add(conceptionEntityValueForUpdate2);
        ConceptionEntityValue conceptionEntityValueForUpdate3 = new ConceptionEntityValue(updateEntityValueMap);
        conceptionEntityValueForUpdate3.setConceptionEntityUID(addEntitiesResult.getSuccessEntityUIDs().get(2));
        conceptionEntityValueForUpdateList.add(conceptionEntityValueForUpdate3);
        ConceptionEntityValue conceptionEntityValueForUpdate4 = new ConceptionEntityValue(updateEntityValueMap);
        conceptionEntityValueForUpdate4.setConceptionEntityUID("123456789");
        conceptionEntityValueForUpdateList.add(conceptionEntityValueForUpdate4);

        EntitiesOperationResult entitiesOperationResult = _ConceptionKind01.updateEntities(conceptionEntityValueForUpdateList);
        Assert.assertNotNull(entitiesOperationResult);
        Assert.assertNotNull(entitiesOperationResult.getSuccessEntityUIDs());
        Assert.assertNotNull(entitiesOperationResult.getOperationStatistics());
        Assert.assertNotNull(entitiesOperationResult.getOperationStatistics().getOperationSummary());
        Assert.assertNotNull(entitiesOperationResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(entitiesOperationResult.getOperationStatistics().getFinishTime());
        Assert.assertEquals(entitiesOperationResult.getSuccessEntityUIDs().size(),3);
        Assert.assertTrue(entitiesOperationResult.getSuccessEntityUIDs().contains(addEntitiesResult.getSuccessEntityUIDs().get(0)));
        Assert.assertTrue(entitiesOperationResult.getSuccessEntityUIDs().contains(addEntitiesResult.getSuccessEntityUIDs().get(1)));
        Assert.assertTrue(entitiesOperationResult.getSuccessEntityUIDs().contains(addEntitiesResult.getSuccessEntityUIDs().get(2)));
        Assert.assertEquals(entitiesOperationResult.getOperationStatistics().getSuccessItemsCount(),3);
        Assert.assertEquals(entitiesOperationResult.getOperationStatistics().getFailItemsCount(),1);

        ConceptionEntity conceptionEntityForDelete = _ConceptionKind01.getEntityByUID(addEntitiesResult.getSuccessEntityUIDs().get(0));
        Assert.assertNotNull(conceptionEntityForDelete);
        boolean deleteEntityResult = _ConceptionKind01.deleteEntity(addEntitiesResult.getSuccessEntityUIDs().get(0));
        Assert.assertTrue(deleteEntityResult);
        conceptionEntityForDelete = _ConceptionKind01.getEntityByUID(addEntitiesResult.getSuccessEntityUIDs().get(0));
        Assert.assertNull(conceptionEntityForDelete);

        exceptionShouldBeCaught = false;
        try{
            _ConceptionKind01.deleteEntity("123456");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _ConceptionKind01.deleteEntity(addEntitiesResult.getSuccessEntityUIDs().get(0));
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        List<String> entityUIDsForDelete = new ArrayList<>();
        entityUIDsForDelete.add(addEntitiesResult.getSuccessEntityUIDs().get(0));
        entityUIDsForDelete.add(addEntitiesResult.getSuccessEntityUIDs().get(1));
        entityUIDsForDelete.add(addEntitiesResult.getSuccessEntityUIDs().get(2));
        entityUIDsForDelete.add("123456");

        entitiesOperationResult = _ConceptionKind01.deleteEntities(entityUIDsForDelete);
        Assert.assertNotNull(entitiesOperationResult);
        Assert.assertNotNull(entitiesOperationResult.getSuccessEntityUIDs());
        Assert.assertNotNull(entitiesOperationResult.getOperationStatistics());
        Assert.assertNotNull(entitiesOperationResult.getOperationStatistics().getOperationSummary());
        Assert.assertNotNull(entitiesOperationResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(entitiesOperationResult.getOperationStatistics().getFinishTime());
        Assert.assertEquals(entitiesOperationResult.getSuccessEntityUIDs().size(),2);
        Assert.assertTrue(entitiesOperationResult.getSuccessEntityUIDs().contains(addEntitiesResult.getSuccessEntityUIDs().get(1)));
        Assert.assertTrue(entitiesOperationResult.getSuccessEntityUIDs().contains(addEntitiesResult.getSuccessEntityUIDs().get(2)));
        Assert.assertEquals(entitiesOperationResult.getOperationStatistics().getSuccessItemsCount(),2);
        Assert.assertEquals(entitiesOperationResult.getOperationStatistics().getFailItemsCount(),2);

        ConceptionEntity relationQueryTest01 = _ConceptionKind01.newEntity(conceptionEntityValueForUpdate,false);
        ConceptionEntity relationQueryTest02 = _ConceptionKind01.newEntity(conceptionEntityValueForUpdate,false);
        for(int i=0;i<100;i++){
            Map<String,Object> newEntityForRelationTestValueMap= new HashMap<>();
            newEntityForRelationTestValueMap.put("prop1",1000);
            ConceptionEntityValue newRelationTestEntityValue = new ConceptionEntityValue(newEntityForRelationTestValueMap);
            ConceptionEntity relationEntity = _ConceptionKind02.newEntity(newRelationTestEntityValue,false);
            relationQueryTest01.attachFromRelation(relationEntity.getConceptionEntityUID(),"queryTestRelation01",null,false);
        }
        for(int i=0;i<5;i++){
            Map<String,Object> newEntityForRelationTestValueMap= new HashMap<>();
            newEntityForRelationTestValueMap.put("prop1",5000);
            ConceptionEntityValue newRelationTestEntityValue = new ConceptionEntityValue(newEntityForRelationTestValueMap);
            ConceptionEntity relationEntity = _ConceptionKind02.newEntity(newRelationTestEntityValue,false);
            relationQueryTest02.attachFromRelation(relationEntity.getConceptionEntityUID(),"queryTestRelation01",null,false);
        }
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setStartPage(1);
        queryParameters.setEndPage(100);
        queryParameters.setPageSize(10);
        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult =_ConceptionKind01.getKindDirectRelatedEntities(null,"queryTestRelation01",RelationDirection.FROM,null,queryParameters);
        Assert.assertEquals(conceptionEntitiesRetrieveResult.getConceptionEntities().size(),105);

        List<String> attributesList = new ArrayList<>();
        attributesList.add("prop1");
        attributesList.add("propNotExist");
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult =_ConceptionKind01.getAttributesOfKindDirectRelatedEntities(null,attributesList,"queryTestRelation01",RelationDirection.FROM,null,queryParameters);
        Assert.assertNotNull(conceptionEntitiesAttributesRetrieveResult);
        List<ConceptionEntityValue> conceptionEntityValuesList = conceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues();

        for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValuesList){
            Assert.assertNotNull(currentConceptionEntityValue.getEntityAttributesValue());
            Assert.assertNotNull(currentConceptionEntityValue.getConceptionEntityUID());
            Assert.assertNotNull(currentConceptionEntityValue.getEntityAttributesValue().get("prop1"));
            Assert.assertNull(currentConceptionEntityValue.getEntityAttributesValue().get("propNotExist"));
        }

        conceptionEntitiesRetrieveResult =_ConceptionKind01.getKindDirectRelatedEntities(null,"queryTestRelation01",RelationDirection.TO,null,queryParameters);
        Assert.assertEquals(conceptionEntitiesRetrieveResult.getConceptionEntities().size(),0);

        conceptionEntitiesRetrieveResult =_ConceptionKind01.getKindDirectRelatedEntities(null,"queryTestRelation01",RelationDirection.TWO_WAY,null,queryParameters);
        Assert.assertEquals(conceptionEntitiesRetrieveResult.getConceptionEntities().size(),105);

        queryParameters.setDefaultFilteringItem(new GreaterThanFilteringItem("prop1",1000));
        conceptionEntitiesRetrieveResult =_ConceptionKind01.getKindDirectRelatedEntities(null,"queryTestRelation01",RelationDirection.FROM,null,queryParameters);
        Assert.assertEquals(conceptionEntitiesRetrieveResult.getConceptionEntities().size(),5);

        List<String> startEntityUIDS = new ArrayList<>();
        startEntityUIDS.add(relationQueryTest02.getConceptionEntityUID());
        conceptionEntitiesRetrieveResult =_ConceptionKind01.getKindDirectRelatedEntities(startEntityUIDS,"queryTestRelation01",RelationDirection.FROM,null,queryParameters);
        Assert.assertEquals(conceptionEntitiesRetrieveResult.getConceptionEntities().size(),5);

        startEntityUIDS.clear();
        startEntityUIDS.add(relationQueryTest01.getConceptionEntityUID());
        conceptionEntitiesRetrieveResult =_ConceptionKind01.getKindDirectRelatedEntities(startEntityUIDS,"queryTestRelation01",RelationDirection.FROM,null,queryParameters);
        Assert.assertEquals(conceptionEntitiesRetrieveResult.getConceptionEntities().size(),0);

        Set<KindDataDistributionInfo> dsset = _ConceptionKind01.getKindDataDistributionStatistics(0.9);
        Assert.assertTrue(dsset != null);
        Assert.assertTrue(dsset.size()>0);

        Set<KindAttributeDistributionInfo> deset2 = _ConceptionKind01.getKindAttributesDistributionStatistics(0.9);
        Assert.assertTrue(deset2 != null);
        Assert.assertTrue(deset2.size()>0);

        Set<ConceptionEntity> randomEntityList = _ConceptionKind01.getRandomEntities(2);
        Assert.assertNotNull(randomEntityList);
        Assert.assertEquals(randomEntityList.size(),2);
        for(ConceptionEntity currentEntity:randomEntityList){
            Assert.assertEquals(currentEntity.getConceptionKindName(),testConceptionKindName);
            Assert.assertNotNull(currentEntity.getConceptionEntityUID());
        }

        AttributesParameters attributesParameters = new AttributesParameters();
        attributesParameters.setDefaultFilteringItem(new NullValueFilteringItem("mustNotExistAttr"));
        randomEntityList = _ConceptionKind01.getRandomEntities(attributesParameters,true,2);
        Assert.assertNotNull(randomEntityList);
        Assert.assertEquals(randomEntityList.size(),2);
        for(ConceptionEntity currentEntity:randomEntityList){
            Assert.assertEquals(currentEntity.getConceptionKindName(),testConceptionKindName);
            Assert.assertNotNull(currentEntity.getConceptionEntityUID());
        }

        Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"TestConceptionKindADesc+中文描述");
        boolean updateDescResult = _ConceptionKind01.updateConceptionKindDesc("TestConceptionKindADesc+中文描述UPD");
        Assert.assertTrue(updateDescResult);
        Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"TestConceptionKindADesc+中文描述UPD");
        Assert.assertEquals(coreRealm.getConceptionKind(testConceptionKindName).getConceptionKindDesc(),"TestConceptionKindADesc+中文描述UPD");

        Assert.assertEquals(attributeKind02.getAttributeKindDesc(),"attributeKind02Desc");
        updateDescResult = attributeKind02.updateAttributeKindDesc("attributeKind02DescUPD");
        Assert.assertTrue(updateDescResult);
        Assert.assertEquals(attributeKind02.getAttributeKindDesc(),"attributeKind02DescUPD");
        Assert.assertEquals(coreRealm.getAttributeKind(attributeKind02.getAttributeKindUID()).getAttributeKindDesc(),"attributeKind02DescUPD");

        Map<String,Object> kindScopeAttributeMap = new HashMap<>();
        kindScopeAttributeMap.put("dateTypeAttr",new Date());
        kindScopeAttributeMap.put("intTypeAttr",1000);
        EntitiesOperationStatistics addAttrResult = _ConceptionKind01.setKindScopeAttributes(kindScopeAttributeMap);
        Assert.assertEquals(addAttrResult.getSuccessItemsCount(),_ConceptionKind01.countConceptionEntities().longValue());
        String randomEntityId = _ConceptionKind01.getRandomEntities(1).iterator().next().getConceptionEntityUID();
        ConceptionEntity randomEntity = _ConceptionKind01.getEntityByUID(randomEntityId);
        Assert.assertNotNull(randomEntity.getAttribute("dateTypeAttr"));
        Assert.assertEquals(((Long)randomEntity.getAttribute("intTypeAttr").getAttributeValue()).longValue(),1000l);

        String classificationName01 = "classificationForTest01";
        String classificationName02 = "classificationForTest02";
        Classification _Classification01 = coreRealm.getClassification(classificationName01);
        Classification _Classification02 = coreRealm.getClassification(classificationName02);
        if(_Classification01 != null){
            boolean removeClassificationResult = coreRealm.removeClassification(classificationName01);
            Assert.assertTrue(removeClassificationResult);
        }
        if(_Classification02 != null){
            boolean removeClassificationResult = coreRealm.removeClassification(classificationName02);
            Assert.assertTrue(removeClassificationResult);
        }
        coreRealm.createClassification(classificationName02,"Test01");
        coreRealm.createClassification(classificationName01,"Test02",classificationName02);

        List<String> entitiesUIDList = new ArrayList<>();
        RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
        relationAttachInfo.setRelationKind("relationKind01AA");
        relationAttachInfo.setRelationDirection(RelationDirection.FROM);

        for(int i =0;i<20;i++){
            Map<String,Object> newEntityValueMap2= new HashMap<>();
            newEntityValueMap2.put("prop1",10000l);
            newEntityValueMap2.put("prop2",190.22d);
            newEntityValueMap2.put("prop3",50);
            newEntityValueMap2.put("prop4","thi is s string");
            newEntityValueMap2.put("prop5","我是中文string");
            newEntityValueMap2.put("propTmp1", LocalDate.of(1667,1,1));
            newEntityValueMap2.put("propTmp2", new LocalTime[]{LocalTime.of(13,3,3),
                    LocalTime.of(14,4,4)});
            ConceptionEntityValue currentConceptionEntityValue = new ConceptionEntityValue(newEntityValueMap2);
            ConceptionEntity _CurrentConceptionEntity = _ConceptionKind01.newEntity(currentConceptionEntityValue,false);
            entitiesUIDList.add(_CurrentConceptionEntity.getConceptionEntityUID());
            _CurrentConceptionEntity.attachClassification(relationAttachInfo,classificationName01);
        }

        ClassificationAttachParameters classificationAttachParameters = new ClassificationAttachParameters();
        classificationAttachParameters.setRelationKind("relationKind01AA");
        classificationAttachParameters.setOffspringAttach(false);
        classificationAttachParameters.setAttachedClassification(classificationName01);
        classificationAttachParameters.setRelationDirection(RelationDirection.FROM);
        Set<ClassificationAttachParameters> classificationAttachParametersSet = new HashSet();
        classificationAttachParametersSet.add(classificationAttachParameters);

        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult01 = _ConceptionKind01.getEntitiesWithClassificationsAttached(null,classificationAttachParametersSet);
        Assert.assertEquals(conceptionEntitiesRetrieveResult01.getOperationStatistics().getResultEntitiesCount(),20);
        Long countResult =_ConceptionKind01.countEntitiesWithClassificationsAttached(null,true,classificationAttachParametersSet);
        Assert.assertEquals(countResult,20);

        List<String> attributesList2 = new ArrayList<>();
        attributesList2.add("prop1");
        attributesList2.add("prop2");
        attributesList2.add("prop5");
        attributesList2.add("propTmp1");
        attributesList2.add("propTmpNOTEXIST");

        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributesRetrieveResult0 = _ConceptionKind01.getSingleValueEntityAttributesByAttributeNamesWithClassificationsAttached(attributesList2,null,classificationAttachParametersSet);
        Assert.assertEquals(conceptionEntitiesAttributesRetrieveResult0.getOperationStatistics().getResultEntitiesCount(),20);
        Assert.assertEquals(conceptionEntitiesAttributesRetrieveResult0.getConceptionEntityValues().size(),20);
        for(ConceptionEntityValue cConceptionEntityValue:conceptionEntitiesAttributesRetrieveResult0.getConceptionEntityValues()){
            String entityID = cConceptionEntityValue.getConceptionEntityUID();
            Assert.assertTrue(entitiesUIDList.contains(entityID));
            Map<String,Object> attrMap =  cConceptionEntityValue.getEntityAttributesValue();
            Assert.assertTrue(attrMap.get("prop1") != null);
            Assert.assertTrue(attrMap.get("prop2") != null);
            Assert.assertTrue(attrMap.get("prop5") != null);
            Assert.assertTrue(attrMap.get("propTmp1") != null);
            Assert.assertNull(attrMap.get("propTmpNOTEXIST"));
        }

        Map<String,Object> newEntityValueMap3= new HashMap<>();
        newEntityValueMap3.put("propHHH",10000l);
        ConceptionEntityValue fixConceptionEntityValue = new ConceptionEntityValue(newEntityValueMap3);
        ConceptionEntity fixConceptionEntity = _ConceptionKind02.newEntity(fixConceptionEntityValue,false);

        ConceptionEntity sourceConceptionEntity = _ConceptionKind01.getEntityByUID(entitiesUIDList.get(0));
        sourceConceptionEntity.attachFromRelation(fixConceptionEntity.getConceptionEntityUID(),"relationKind01BB",null,false);

        FixConceptionEntityAttachParameters fixConceptionEntityAttachParameters = new FixConceptionEntityAttachParameters();
        fixConceptionEntityAttachParameters.setConceptionEntityUID(fixConceptionEntity.getConceptionEntityUID());
        fixConceptionEntityAttachParameters.setRelationDirection(RelationDirection.FROM);
        fixConceptionEntityAttachParameters.setRelationKind("relationKind01BB");

        conceptionEntitiesAttributesRetrieveResult0 = _ConceptionKind01.getSingleValueEntityAttributesByAttributeNamesWithClassificationsAttached(attributesList2,null,classificationAttachParametersSet,fixConceptionEntityAttachParameters);
        Assert.assertEquals(conceptionEntitiesAttributesRetrieveResult0.getConceptionEntityValues().size(),1);
        ConceptionEntityValue resultValue = conceptionEntitiesAttributesRetrieveResult0.getConceptionEntityValues().get(0);
        Assert.assertEquals(resultValue.getConceptionEntityUID(),sourceConceptionEntity.getConceptionEntityUID());

        Map<String,Object> attrMap =  resultValue.getEntityAttributesValue();
        Assert.assertEquals(attrMap.get("prop1"),sourceConceptionEntity.getAttribute("prop1").getAttributeValue());
        Assert.assertEquals(attrMap.get("prop2"),sourceConceptionEntity.getAttribute("prop2").getAttributeValue());
        Assert.assertEquals(attrMap.get("prop5"),sourceConceptionEntity.getAttribute("prop5").getAttributeValue());
        Assert.assertEquals(attrMap.get("propTmp1"),sourceConceptionEntity.getAttribute("propTmp1").getAttributeValue());

        classificationAttachParameters.setOffspringAttach(true);
        classificationAttachParameters.setAttachedClassification(classificationName02);
        conceptionEntitiesRetrieveResult01 = _ConceptionKind01.getEntitiesWithClassificationsAttached(null,classificationAttachParametersSet);
        Assert.assertEquals(conceptionEntitiesRetrieveResult01.getOperationStatistics().getResultEntitiesCount(),20);

        EntitiesOperationResult removeResult = _ConceptionKind01.deleteEntities(entitiesUIDList);
        Assert.assertEquals(removeResult.getOperationStatistics().getSuccessItemsCount(),20);
        _ConceptionKind02.deleteEntity(fixConceptionEntity.getConceptionEntityUID());
    }
}
