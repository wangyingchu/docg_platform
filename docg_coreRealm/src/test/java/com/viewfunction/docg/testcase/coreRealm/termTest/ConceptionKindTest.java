package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

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
    public void testConceptionKindFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"TestConceptionKindADesc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),testConceptionKindName);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"TestConceptionKindADesc+中文描述");
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
        AttributesViewKind attributesViewKindToAdd01 = coreRealm.createAttributesViewKind("targetAttributesViewKindToAdd01","targetAttributesViewKindToAdd01Desc",null);

        boolean addAttributesViewKindResult = _ConceptionKind01.addAttributesViewKind(attributesViewKindToAdd01.getAttributesViewKindUID());


/*
        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);
        newEntityValue.put("prop2",190.22d);
        newEntityValue.put("prop3",50);
        newEntityValue.put("prop4","thi is s string");
        newEntityValue.put("prop5","我是中文string");

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
        Assert.assertEquals(attributeNameList.size(),5);
        Assert.assertTrue(attributeNameList.contains("prop1"));
        Assert.assertTrue(attributeNameList.contains("prop2"));
        Assert.assertTrue(attributeNameList.contains("prop3"));
        Assert.assertTrue(attributeNameList.contains("prop4"));
        Assert.assertTrue(attributeNameList.contains("prop5"));

        List<AttributeValue> attributeValueList = _queryResultConceptionEntity.getAttributes();
        Assert.assertNotNull(attributeValueList);
        Assert.assertEquals(attributeValueList.size(),5);

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
            Assert.assertEquals(currentConceptionEntity.getAttributes().size(),20);
        }

        ConceptionEntity currentConceptionEntity = _ConceptionKind01.getEntityByUID(addEntitiesResult.getSuccessEntityUIDs().get(0));

        Assert.assertEquals(currentConceptionEntity.getAttribute("prop1").getAttributeValue(),Long.parseLong("12345"));
        Assert.assertEquals(currentConceptionEntity.getAttribute("prop2").getAttributeValue(),Double.parseDouble("12345.789"));

        Map<String,Object> updateEntityValueMap= new HashMap<>();
        updateEntityValueMap.put("prop1",Long.parseLong("59000"));
        updateEntityValueMap.put("prop2",Double.parseDouble("10000000.1"));
        updateEntityValueMap.put("prop3",new Date());
        updateEntityValueMap.put("prop3_NotExist",new Date());

        ConceptionEntityValue conceptionEntityValueForUpdate = new ConceptionEntityValue(updateEntityValueMap);
        conceptionEntityValueForUpdate.setConceptionEntityUID(addEntitiesResult.getSuccessEntityUIDs().get(0));

        ConceptionEntity updatedConceptionEntityValue = _ConceptionKind01.updateEntity(conceptionEntityValueForUpdate);
        Assert.assertNotNull(updatedConceptionEntityValue);
        Assert.assertEquals(updatedConceptionEntityValue.getAttribute("prop1").getAttributeValue(),Long.parseLong("59000"));
        Assert.assertEquals(updatedConceptionEntityValue.getAttribute("prop2").getAttributeValue(),Double.parseDouble("10000000.1"));
        Assert.assertEquals(updatedConceptionEntityValue.getAttribute("prop3").getAttributeValue(),Long.parseLong("1234"));
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

        boolean exceptionShouldBeCaught = false;
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

        */
    }
}
