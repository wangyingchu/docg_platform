package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
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
    public void testCoreRealmFunction() throws CoreRealmServiceRuntimeException {
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
    }
}
