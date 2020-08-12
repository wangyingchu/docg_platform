package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
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

public class AttributesMeasurableTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKind01";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for AttributesMeasurableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testAttributesMeasurableFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"testKind01Desc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),testConceptionKindName);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"testKind01Desc+中文描述");
        }

        EntitiesOperationResult purgeEntitiesOperationResult = _ConceptionKind01.purgeAllEntities();

        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getOperationSummary());
        Assert.assertEquals(purgeEntitiesOperationResult.getOperationStatistics().getFailItemsCount(),0);

        Assert.assertNotNull(purgeEntitiesOperationResult.getSuccessEntityUIDs());
        Assert.assertEquals(purgeEntitiesOperationResult.getSuccessEntityUIDs().size(),0);

        Long entitiesCount = _ConceptionKind01.countConceptionEntities();
        Assert.assertEquals(entitiesCount,new Long(0));

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",Long.parseLong("12345"));
        newEntityValue.put("prop2",Double.parseDouble("12345.789"));
        newEntityValue.put("prop3",Integer.parseInt("1234"));
        newEntityValue.put("prop4","thi is s string");
        newEntityValue.put("prop5",Boolean.valueOf("true"));
        newEntityValue.put("prop6", new BigDecimal("5566778890.223344"));
        newEntityValue.put("prop7", Short.valueOf("24"));
        newEntityValue.put("prop8", Float.valueOf("1234.66"));
        newEntityValue.put("prop9", new Long[]{1000l,2000l,3000l});
        newEntityValue.put("prop10", new Double[]{1000.1d,2000.2d,3000.3d});
        newEntityValue.put("prop11", new Integer[]{100,200,300});
        newEntityValue.put("prop12", new String[]{"this is str1","这是字符串2"});
        newEntityValue.put("prop13", new Boolean[]{true,true,false,false,true});
        newEntityValue.put("prop14", new BigDecimal[]{new BigDecimal("1234567.890"),new BigDecimal("987654321.12345")});
        newEntityValue.put("prop15", new Short[]{1,2,3,4,5});
        newEntityValue.put("prop16", new Float[]{1000.1f,2000.2f,3000.3f});
        newEntityValue.put("prop17", new Date());
        newEntityValue.put("prop18", new Date[]{new Date(),new Date(),new Date(),new Date()});
        newEntityValue.put("prop19", Byte.valueOf("2"));
        newEntityValue.put("prop20", "this is a byte array value".getBytes());

        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);

        ConceptionEntity _ConceptionEntity = _ConceptionKind01.newEntity(conceptionEntityValue,false);
        Assert.assertNotNull(_ConceptionEntity);
        Assert.assertEquals(_ConceptionEntity.getConceptionKindName(),testConceptionKindName);
        Assert.assertEquals(_ConceptionEntity.getAllConceptionKindNames().size(),1);
        Assert.assertEquals(_ConceptionEntity.getAllConceptionKindNames().get(0),testConceptionKindName);
        Assert.assertNotNull(_ConceptionEntity.getConceptionEntityUID());

        entitiesCount = _ConceptionKind01.countConceptionEntities();
        Assert.assertEquals(entitiesCount,new Long(1));

        String queryUIDValue = _ConceptionEntity.getConceptionEntityUID();
        ConceptionEntity _queryResultConceptionEntity = _ConceptionKind01.getEntityByUID(queryUIDValue);

        List<String> attributeNameList = _queryResultConceptionEntity.getAttributeNames();
        Assert.assertNotNull(attributeNameList);
        Assert.assertEquals(attributeNameList.size(),20);
        List<String> targetAttributeNameList = new ArrayList();
        targetAttributeNameList.add("prop1");targetAttributeNameList.add("prop2");targetAttributeNameList.add("prop3");
        targetAttributeNameList.add("prop4");targetAttributeNameList.add("prop5");targetAttributeNameList.add("prop6");
        targetAttributeNameList.add("prop7");targetAttributeNameList.add("prop8");targetAttributeNameList.add("prop9");
        targetAttributeNameList.add("prop10");targetAttributeNameList.add("prop11");targetAttributeNameList.add("prop12");
        targetAttributeNameList.add("prop13");targetAttributeNameList.add("prop14");targetAttributeNameList.add("prop15");
        targetAttributeNameList.add("prop16");targetAttributeNameList.add("prop17");targetAttributeNameList.add("prop18");
        targetAttributeNameList.add("prop19");targetAttributeNameList.add("prop20");
        for(String attributeName:attributeNameList){
            Assert.assertTrue(targetAttributeNameList.contains(attributeName));
        }
        List<AttributeValue> attributeValueList = _queryResultConceptionEntity.getAttributes();
        Assert.assertNotNull(attributeValueList);
        Assert.assertEquals(attributeValueList.size(),20);
        for(AttributeValue currentAttributeValue:attributeValueList){
            String attributeName = currentAttributeValue.getAttributeName();
            AttributeDataType currentAttributeDataType = currentAttributeValue.getAttributeDataType();
            Object currentAttributeValueObj = currentAttributeValue.getAttributeValue();
            Assert.assertTrue(targetAttributeNameList.contains(attributeName));
            Assert.assertNotNull(attributeName);
            Assert.assertNotNull(currentAttributeDataType);
            Assert.assertNotNull(currentAttributeValueObj);
            if(attributeName.equals("prop1")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.LONG);
                Assert.assertTrue(currentAttributeValueObj instanceof Long);
                Assert.assertEquals(currentAttributeValueObj,12345l);
                Assert.assertEquals(currentAttributeValueObj,new Long(12345));
            }
            if(attributeName.equals("prop2")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.DOUBLE);
                Assert.assertTrue(currentAttributeValueObj instanceof Double);
                Assert.assertEquals(currentAttributeValueObj,12345.789d);
                Assert.assertEquals(currentAttributeValueObj,new Double(12345.789));
            }
            //neo4j java driver 内部使用Long 来存储 int类型数据
            if(attributeName.equals("prop3")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.LONG);
                Assert.assertTrue(currentAttributeValueObj instanceof Long);
                Assert.assertEquals(currentAttributeValueObj,1234l);
                Assert.assertEquals(currentAttributeValueObj,new Long(1234));
            }
            if(attributeName.equals("prop4")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.STRING);
                Assert.assertTrue(currentAttributeValueObj instanceof String);
                Assert.assertEquals(currentAttributeValueObj,"thi is s string");
            }
            if(attributeName.equals("prop5")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.BOOLEAN);
                Assert.assertTrue(currentAttributeValueObj instanceof Boolean);
                Assert.assertEquals(currentAttributeValueObj,true);
                Assert.assertEquals(currentAttributeValueObj,new Boolean(true));
            }
            //neo4j java driver 内部使用Double 来存储BigDecimal类型数据
            if(attributeName.equals("prop6")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.DOUBLE);
                Assert.assertTrue(currentAttributeValueObj instanceof Double);
                Assert.assertEquals(currentAttributeValueObj,5566778890.223344d);
                Assert.assertEquals(currentAttributeValueObj,new Double(5566778890.223344));
            }
            //neo4j java driver 内部使用Long 来存储Short类型数据
            if(attributeName.equals("prop7")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.LONG);
                Assert.assertTrue(currentAttributeValueObj instanceof Long);
                Assert.assertEquals(currentAttributeValueObj,24l);
                Assert.assertEquals(currentAttributeValueObj,new Long(24));
            }
            //neo4j java driver 内部使用Double 来存储Float类型数据
            if(attributeName.equals("prop8")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.DOUBLE);
                Assert.assertTrue(currentAttributeValueObj instanceof Double);
                Assert.assertEquals(currentAttributeValueObj,1234.66d);
                Assert.assertEquals(currentAttributeValueObj,new Double(1234.66));
            }
            //neo4j java driver 内部使用Long 来存储Byte类型数据
            if(attributeName.equals("prop19")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.LONG);
                Assert.assertTrue(currentAttributeValueObj instanceof Long);
                Assert.assertEquals(currentAttributeValueObj,2l);
                Assert.assertEquals(currentAttributeValueObj,new Long(2));
            }
            if(attributeName.equals("prop17")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.DATE);
                Assert.assertTrue(currentAttributeValueObj instanceof Date);
            }
            if(attributeName.equals("prop9")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.LONG_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof Long[]);
                Assert.assertEquals(((Long[])currentAttributeValueObj).length,3);
                Assert.assertTrue(((Long[])currentAttributeValueObj)[0] instanceof Long);
                Assert.assertEquals(((Long[])currentAttributeValueObj)[0],new Long(1000l));
                Assert.assertEquals(((Long[])currentAttributeValueObj)[1],new Long(2000l));
                Assert.assertEquals(((Long[])currentAttributeValueObj)[2],new Long(3000l));
            }
            if(attributeName.equals("prop10")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.DOUBLE_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof Double[]);
                Assert.assertEquals(((Double[])currentAttributeValueObj).length,3);
                Assert.assertTrue(((Double[])currentAttributeValueObj)[0] instanceof Double);
                Assert.assertEquals(((Double[])currentAttributeValueObj)[0],new Double(1000.1d));
                Assert.assertEquals(((Double[])currentAttributeValueObj)[1],new Double(2000.2d));
                Assert.assertEquals(((Double[])currentAttributeValueObj)[2],new Double(3000.3d));
            }
            //neo4j java driver 内部使用Long 来存储 int类型数据
            if(attributeName.equals("prop11")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.LONG_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof Long[]);
                Assert.assertEquals(((Long[])currentAttributeValueObj).length,3);
                Assert.assertTrue(((Long[])currentAttributeValueObj)[0] instanceof Long);
                Assert.assertEquals(((Long[])currentAttributeValueObj)[0],new Long(100));
                Assert.assertEquals(((Long[])currentAttributeValueObj)[1],new Long(200l));
                Assert.assertEquals(((Long[])currentAttributeValueObj)[2],new Long(300));
            }
            if(attributeName.equals("prop12")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.STRING_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof String[]);
                Assert.assertEquals(((String[])currentAttributeValueObj).length,2);
                Assert.assertTrue(((String[])currentAttributeValueObj)[0] instanceof String);
                Assert.assertEquals(((String[])currentAttributeValueObj)[0],"this is str1");
                Assert.assertEquals(((String[])currentAttributeValueObj)[1],"这是字符串2");
            }
            if(attributeName.equals("prop13")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.BOOLEAN_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof Boolean[]);
                Assert.assertEquals(((Boolean[])currentAttributeValueObj).length,5);
                Assert.assertTrue(((Boolean[])currentAttributeValueObj)[0] instanceof Boolean);
                Assert.assertEquals(((Boolean[])currentAttributeValueObj)[0],new Boolean(true));
                Assert.assertEquals(((Boolean[])currentAttributeValueObj)[1],new Boolean(true));
                Assert.assertEquals(((Boolean[])currentAttributeValueObj)[2],new Boolean(false));
                Assert.assertEquals(((Boolean[])currentAttributeValueObj)[3],new Boolean(false));
                Assert.assertEquals(((Boolean[])currentAttributeValueObj)[4],new Boolean(true));
            }
            //neo4j java driver 内部使用Double 来存储BigDecimal类型数据
            if(attributeName.equals("prop14")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.DOUBLE_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof Double[]);
                Assert.assertEquals(((Double[])currentAttributeValueObj).length,2);
                Assert.assertTrue(((Double[])currentAttributeValueObj)[0] instanceof Double);
                Assert.assertEquals(((Double[])currentAttributeValueObj)[0],new Double(1234567.890));
                Assert.assertEquals(((Double[])currentAttributeValueObj)[1],new Double(987654321.12345));
            }
            //neo4j java driver 内部使用Long 来存储Short类型数据
            if(attributeName.equals("prop15")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.LONG_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof Long[]);
                Assert.assertEquals(((Long[])currentAttributeValueObj).length,5);
                Assert.assertTrue(((Long[])currentAttributeValueObj)[0] instanceof Long);
                Assert.assertEquals(((Long[])currentAttributeValueObj)[0],new Long(1));
                Assert.assertEquals(((Long[])currentAttributeValueObj)[1],new Long(2));
                Assert.assertEquals(((Long[])currentAttributeValueObj)[2],new Long(3));
                Assert.assertEquals(((Long[])currentAttributeValueObj)[3],new Long(4));
                Assert.assertEquals(((Long[])currentAttributeValueObj)[4],new Long(5));
            }
            //neo4j java driver 内部使用Double 来存储Float类型数据
            if(attributeName.equals("prop16")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.DOUBLE_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof Double[]);
                Assert.assertEquals(((Double[])currentAttributeValueObj).length,3);
                Assert.assertTrue(((Double[])currentAttributeValueObj)[0] instanceof Double);
                Assert.assertEquals(((Double[])currentAttributeValueObj)[0],new Double(1000.1d));
                Assert.assertEquals(((Double[])currentAttributeValueObj)[1],new Double(2000.2));
                Assert.assertEquals(((Double[])currentAttributeValueObj)[2],new Double(3000.3));
            }
            if(attributeName.equals("prop18")){
                Assert.assertEquals(currentAttributeDataType,AttributeDataType.DATE_ARRAY);
                Assert.assertTrue(currentAttributeValueObj instanceof Date[]);
                Assert.assertEquals(((Date[])currentAttributeValueObj).length,4);
                Assert.assertTrue(((Date[])currentAttributeValueObj)[0] instanceof Date);
                Assert.assertNotNull(((Date[])currentAttributeValueObj)[0]);
                Assert.assertNotNull(((Date[])currentAttributeValueObj)[1]);
                Assert.assertNotNull(((Date[])currentAttributeValueObj)[2]);
                Assert.assertNotNull(((Date[])currentAttributeValueObj)[3]);
            }
        }
    }
}
