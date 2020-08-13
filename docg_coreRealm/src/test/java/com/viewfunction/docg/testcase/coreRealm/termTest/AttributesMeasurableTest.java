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

        AttributeValue attributeValueNotExist = _queryResultConceptionEntity.getAttribute("valueNotExost");
        Assert.assertNull(attributeValueNotExist);

        AttributeValue attributeValue_prop18 = _queryResultConceptionEntity.getAttribute("prop18");
        Assert.assertNotNull(attributeValue_prop18);
        Assert.assertEquals(attributeValue_prop18.getAttributeDataType(),AttributeDataType.DATE_ARRAY);
        Assert.assertTrue(attributeValue_prop18.getAttributeValue() instanceof Date[]);
        Assert.assertEquals(((Date[])attributeValue_prop18.getAttributeValue()).length,4);
        Assert.assertTrue(((Date[])attributeValue_prop18.getAttributeValue())[0] instanceof Date);
        Assert.assertNotNull(((Date[])attributeValue_prop18.getAttributeValue())[0]);
        Assert.assertNotNull(((Date[])attributeValue_prop18.getAttributeValue())[1]);
        Assert.assertNotNull(((Date[])attributeValue_prop18.getAttributeValue())[2]);
        Assert.assertNotNull(((Date[])attributeValue_prop18.getAttributeValue())[3]);

        AttributeValue attributeValue_prop16 = _queryResultConceptionEntity.getAttribute("prop16");
        Assert.assertNotNull(attributeValue_prop16);
        Assert.assertEquals(attributeValue_prop16.getAttributeDataType(),AttributeDataType.DOUBLE_ARRAY);
        Assert.assertTrue(attributeValue_prop16.getAttributeValue() instanceof Double[]);
        Assert.assertEquals(((Double[])attributeValue_prop16.getAttributeValue()).length,3);
        Assert.assertTrue(((Double[])attributeValue_prop16.getAttributeValue())[0] instanceof Double);
        Assert.assertEquals(((Double[])attributeValue_prop16.getAttributeValue())[0],new Double(1000.1d));
        Assert.assertEquals(((Double[])attributeValue_prop16.getAttributeValue())[1],new Double(2000.2));
        Assert.assertEquals(((Double[])attributeValue_prop16.getAttributeValue())[2],new Double(3000.3));

        AttributeValue attributeValue_prop4 = _queryResultConceptionEntity.getAttribute("prop4");
        Assert.assertNotNull(attributeValue_prop4);
        Assert.assertEquals(attributeValue_prop4.getAttributeDataType(),AttributeDataType.STRING);
        Assert.assertTrue(attributeValue_prop4.getAttributeValue() instanceof String);
        Assert.assertEquals(attributeValue_prop4.getAttributeValue(),"thi is s string");

        AttributeValue attributeValue_prop5 = _queryResultConceptionEntity.getAttribute("prop5");
        Assert.assertNotNull(attributeValue_prop5);
        Assert.assertEquals(attributeValue_prop5.getAttributeDataType(),AttributeDataType.BOOLEAN);
        Assert.assertTrue(attributeValue_prop5.getAttributeValue() instanceof Boolean);
        Assert.assertEquals(attributeValue_prop5.getAttributeValue(),true);
        Assert.assertEquals(attributeValue_prop5.getAttributeValue(),new Boolean(true));

        AttributeValue attributeValue_prop6 = _queryResultConceptionEntity.getAttribute("prop6");
        Assert.assertNotNull(attributeValue_prop6);
        Assert.assertEquals(attributeValue_prop6.getAttributeDataType(),AttributeDataType.DOUBLE);
        Assert.assertTrue(attributeValue_prop6.getAttributeValue() instanceof Double);
        Assert.assertEquals(attributeValue_prop6.getAttributeValue(),5566778890.223344d);
        Assert.assertEquals(attributeValue_prop6.getAttributeValue(),new Double(5566778890.223344));

        AttributeValue attributeValue_prop7 = _queryResultConceptionEntity.getAttribute("prop7");
        Assert.assertNotNull(attributeValue_prop7);
        Assert.assertEquals(attributeValue_prop7.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertTrue(attributeValue_prop7.getAttributeValue() instanceof Long);
        Assert.assertEquals(attributeValue_prop7.getAttributeValue(),24l);
        Assert.assertEquals(attributeValue_prop7.getAttributeValue(),new Long(24));

        Assert.assertTrue( _queryResultConceptionEntity.hasAttribute("prop1"));
        Assert.assertTrue( _queryResultConceptionEntity.hasAttribute("prop7"));
        Assert.assertTrue( _queryResultConceptionEntity.hasAttribute("prop9"));
        Assert.assertTrue( _queryResultConceptionEntity.hasAttribute("prop20"));
        Assert.assertFalse( _queryResultConceptionEntity.hasAttribute("propNotExist"));

        AttributeValue newAddedAttributeValue1 = _queryResultConceptionEntity.addAttribute("newBooleanAttribute1",false);
        Assert.assertNotNull(newAddedAttributeValue1);
        Assert.assertEquals(newAddedAttributeValue1.getAttributeDataType(),AttributeDataType.BOOLEAN);
        Assert.assertTrue(newAddedAttributeValue1.getAttributeValue() instanceof Boolean);
        Assert.assertEquals(newAddedAttributeValue1.getAttributeValue(),false);
        Assert.assertEquals(newAddedAttributeValue1.getAttributeValue(),new Boolean(false));

        AttributeValue newAddedAttributeValue2 = _queryResultConceptionEntity.addAttribute("newBooleanAttribute2",true);
        Assert.assertNotNull(newAddedAttributeValue2);
        Assert.assertEquals(newAddedAttributeValue2.getAttributeDataType(),AttributeDataType.BOOLEAN);
        Assert.assertTrue(newAddedAttributeValue2.getAttributeValue() instanceof Boolean);
        Assert.assertEquals(newAddedAttributeValue2.getAttributeValue(),true);
        Assert.assertEquals(newAddedAttributeValue2.getAttributeValue(),new Boolean(true));

        AttributeValue newAddedAttributeValue3 = _queryResultConceptionEntity.addAttribute("newIntAttribute",5000);
        Assert.assertNotNull(newAddedAttributeValue3);
        Assert.assertEquals(newAddedAttributeValue3.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertTrue(newAddedAttributeValue3.getAttributeValue() instanceof Long);
        Assert.assertEquals(newAddedAttributeValue3.getAttributeValue(),new Long(5000));

        AttributeValue newAddedAttributeValue4 = _queryResultConceptionEntity.addAttribute("newShortAttribute",Short.valueOf("25"));
        Assert.assertNotNull(newAddedAttributeValue4);
        Assert.assertEquals(newAddedAttributeValue4.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertTrue(newAddedAttributeValue4.getAttributeValue() instanceof Long);
        Assert.assertEquals(newAddedAttributeValue4.getAttributeValue(),new Long(25));

        AttributeValue newAddedAttributeValue5 = _queryResultConceptionEntity.addAttribute("newLongAttribute",4566777l);
        Assert.assertNotNull(newAddedAttributeValue5);
        Assert.assertEquals(newAddedAttributeValue5.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertTrue(newAddedAttributeValue5.getAttributeValue() instanceof Long);
        Assert.assertEquals(newAddedAttributeValue5.getAttributeValue(),new Long(4566777l));

        AttributeValue newAddedAttributeValue6 = _queryResultConceptionEntity.addAttribute("newFloatAttribute",3456.9f);
        Assert.assertNotNull(newAddedAttributeValue6);
        Assert.assertEquals(newAddedAttributeValue6.getAttributeDataType(),AttributeDataType.DOUBLE);
        Assert.assertTrue(newAddedAttributeValue6.getAttributeValue() instanceof Double);
        Assert.assertEquals(newAddedAttributeValue6.getAttributeValue(),new Double(3456.9));

        AttributeValue newAddedAttributeValue7 = _queryResultConceptionEntity.addAttribute("newDoubleAttribute",5673.999521d);
        Assert.assertNotNull(newAddedAttributeValue7);
        Assert.assertEquals(newAddedAttributeValue7.getAttributeDataType(),AttributeDataType.DOUBLE);
        Assert.assertTrue(newAddedAttributeValue7.getAttributeValue() instanceof Double);
        Assert.assertEquals(newAddedAttributeValue7.getAttributeValue(),new Double(5673.999521d));

        Date targetDate = new Date();
        AttributeValue newAddedAttributeValue8 = _queryResultConceptionEntity.addAttribute("newDateAttribute",targetDate);
        Assert.assertNotNull(newAddedAttributeValue8);
        Assert.assertEquals(newAddedAttributeValue8.getAttributeDataType(),AttributeDataType.DATE);
        Assert.assertTrue(newAddedAttributeValue8.getAttributeValue() instanceof Date);
        Assert.assertEquals(newAddedAttributeValue8.getAttributeValue(),targetDate);

        AttributeValue newAddedAttributeValue9 = _queryResultConceptionEntity.addAttribute("newStringAttribute","string值");
        Assert.assertNotNull(newAddedAttributeValue9);
        Assert.assertEquals(newAddedAttributeValue9.getAttributeDataType(),AttributeDataType.STRING);
        Assert.assertTrue(newAddedAttributeValue9.getAttributeValue() instanceof String);
        Assert.assertEquals(newAddedAttributeValue9.getAttributeValue(),"string值");

        byte[] byteArray = "this is string for byte array".getBytes();
        AttributeValue newAddedAttributeValue10 = _queryResultConceptionEntity.addAttribute("newByteArrayAttribute",byteArray);
        Assert.assertNotNull(newAddedAttributeValue10);
        Assert.assertEquals(newAddedAttributeValue10.getAttributeDataType(),AttributeDataType.LONG_ARRAY);
        Assert.assertTrue(newAddedAttributeValue10.getAttributeValue() instanceof  Long[]);
        //Assert.assertEquals(newAddedAttributeValue10.getAttributeValue(),byteArray);

        AttributeValue newAddedAttributeValue11 = _queryResultConceptionEntity.addAttribute("newByteAttribute",Byte.valueOf("1"));
        Assert.assertNotNull(newAddedAttributeValue11);
        Assert.assertEquals(newAddedAttributeValue11.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertTrue(newAddedAttributeValue11.getAttributeValue() instanceof Long);
        Assert.assertEquals(newAddedAttributeValue11.getAttributeValue(),Long.valueOf("1"));

        AttributeValue newAddedAttributeValue12 = _queryResultConceptionEntity.addAttribute("newDecimalAttribute",new BigDecimal(45679.23455));
        Assert.assertNotNull(newAddedAttributeValue12);
        Assert.assertEquals(newAddedAttributeValue12.getAttributeDataType(),AttributeDataType.DOUBLE);
        Assert.assertTrue(newAddedAttributeValue12.getAttributeValue() instanceof Double);
        Assert.assertEquals(newAddedAttributeValue12.getAttributeValue(),Double.valueOf(45679.23455));

        AttributeValue newAddedAttributeValue13 = _queryResultConceptionEntity.addAttribute("newBooleanArrayAttribute",new Boolean[]{false,true});
        Assert.assertNotNull(newAddedAttributeValue13);
        Assert.assertEquals(newAddedAttributeValue13.getAttributeDataType(),AttributeDataType.BOOLEAN_ARRAY);
        Assert.assertTrue(newAddedAttributeValue13.getAttributeValue() instanceof Boolean[]);
        Assert.assertEquals(((Boolean[]) newAddedAttributeValue13.getAttributeValue()).length,2);
        Assert.assertEquals(((Boolean[]) newAddedAttributeValue13.getAttributeValue())[0],Boolean.valueOf(false));

        AttributeValue newAddedAttributeValue14 = _queryResultConceptionEntity.addAttribute("newIntArrayAttribute",new Integer[]{100,200});
        Assert.assertNotNull(newAddedAttributeValue14);
        Assert.assertEquals(newAddedAttributeValue14.getAttributeDataType(),AttributeDataType.LONG_ARRAY);
        Assert.assertTrue(newAddedAttributeValue14.getAttributeValue() instanceof Long[]);
        Assert.assertEquals(((Long[]) newAddedAttributeValue14.getAttributeValue()).length,2);
        Assert.assertEquals(((Long[]) newAddedAttributeValue14.getAttributeValue())[1],Long.valueOf(200));

        AttributeValue newAddedAttributeValue15 = _queryResultConceptionEntity.addAttribute("newShortArrayAttribute",new Short[]{12,34});
        Assert.assertNotNull(newAddedAttributeValue15);
        Assert.assertEquals(newAddedAttributeValue15.getAttributeDataType(),AttributeDataType.LONG_ARRAY);
        Assert.assertTrue(newAddedAttributeValue15.getAttributeValue() instanceof Long[]);
        Assert.assertEquals(((Long[]) newAddedAttributeValue15.getAttributeValue()).length,2);
        Assert.assertEquals(((Long[]) newAddedAttributeValue15.getAttributeValue())[0],Long.valueOf(12));

        AttributeValue newAddedAttributeValue16 = _queryResultConceptionEntity.addAttribute("newLongArrayAttribute",new Long[]{444l,555l});
        Assert.assertNotNull(newAddedAttributeValue16);
        Assert.assertEquals(newAddedAttributeValue16.getAttributeDataType(),AttributeDataType.LONG_ARRAY);
        Assert.assertTrue(newAddedAttributeValue16.getAttributeValue() instanceof Long[]);
        Assert.assertEquals(((Long[]) newAddedAttributeValue16.getAttributeValue()).length,2);
        Assert.assertEquals(((Long[]) newAddedAttributeValue16.getAttributeValue())[1],Long.valueOf(555l));

        AttributeValue newAddedAttributeValue17 = _queryResultConceptionEntity.addAttribute("newFloatArrayAttribute",new Float[]{123.1f,456.7f});
        Assert.assertNotNull(newAddedAttributeValue17);
        Assert.assertEquals(newAddedAttributeValue17.getAttributeDataType(),AttributeDataType.DOUBLE_ARRAY);
        Assert.assertTrue(newAddedAttributeValue17.getAttributeValue() instanceof Double[]);
        Assert.assertEquals(((Double[]) newAddedAttributeValue17.getAttributeValue()).length,2);
        Assert.assertEquals(((Double[]) newAddedAttributeValue17.getAttributeValue())[0],Double.valueOf(123.1d));

        AttributeValue newAddedAttributeValue18 = _queryResultConceptionEntity.addAttribute("newDoubleArrayAttribute",new Double[]{77.88d,88.99d});
        Assert.assertNotNull(newAddedAttributeValue18);
        Assert.assertEquals(newAddedAttributeValue18.getAttributeDataType(),AttributeDataType.DOUBLE_ARRAY);
        Assert.assertTrue(newAddedAttributeValue18.getAttributeValue() instanceof Double[]);
        Assert.assertEquals(((Double[]) newAddedAttributeValue18.getAttributeValue()).length,2);
        Assert.assertEquals(((Double[]) newAddedAttributeValue18.getAttributeValue())[1],Double.valueOf(88.99d));

        Date arrayValue1 = new Date();
        Date arrayValue2 = new Date();
        AttributeValue newAddedAttributeValue19 = _queryResultConceptionEntity.addAttribute("newDateArrayAttribute",new Date[]{arrayValue1,arrayValue2});
        Assert.assertNotNull(newAddedAttributeValue19);
        Assert.assertEquals(newAddedAttributeValue19.getAttributeDataType(),AttributeDataType.DATE_ARRAY);
        Assert.assertTrue(newAddedAttributeValue19.getAttributeValue() instanceof Date[]);
        Assert.assertEquals(((Date[]) newAddedAttributeValue19.getAttributeValue()).length,2);
        Assert.assertEquals(((Date[]) newAddedAttributeValue19.getAttributeValue())[0],arrayValue1);

        AttributeValue newAddedAttributeValue20 = _queryResultConceptionEntity.addAttribute("newStringArrayAttribute",new String[]{"stringvalue1","stringvalue2"});
        Assert.assertNotNull(newAddedAttributeValue20);
        Assert.assertEquals(newAddedAttributeValue20.getAttributeDataType(),AttributeDataType.STRING_ARRAY);
        Assert.assertTrue(newAddedAttributeValue20.getAttributeValue() instanceof String[]);
        Assert.assertEquals(((String[]) newAddedAttributeValue20.getAttributeValue()).length,2);
        Assert.assertEquals(((String[]) newAddedAttributeValue20.getAttributeValue())[1],"stringvalue2");

        AttributeValue newAddedAttributeValue21 = _queryResultConceptionEntity.addAttribute("newDecimalArrayAttribute",new BigDecimal[]{new BigDecimal(11223.23455),new BigDecimal(22334.889972)});
        Assert.assertNotNull(newAddedAttributeValue21);
        Assert.assertEquals(newAddedAttributeValue21.getAttributeDataType(),AttributeDataType.DOUBLE_ARRAY);
        Assert.assertTrue(newAddedAttributeValue21.getAttributeValue() instanceof Double[]);
        Assert.assertEquals(((Double[]) newAddedAttributeValue21.getAttributeValue()).length,2);
        Assert.assertEquals(((Double[]) newAddedAttributeValue21.getAttributeValue())[0],Double.valueOf(11223.23455));

        boolean exceptionShouldBeCaught = false;
        try {
            _queryResultConceptionEntity.addAttribute("newDecimalArrayAttribute", Long.parseLong("12345678"));
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try {
            _queryResultConceptionEntity.updateAttribute("prop1_notExist", Long.parseLong("12345678"));
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try {
            _queryResultConceptionEntity.updateAttribute("prop1", Double.parseDouble("12345678"));
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        AttributeValue updatedAttributeValue_prop1 = _queryResultConceptionEntity.updateAttribute("prop1", Long.parseLong("11111"));
        Assert.assertNotNull(updatedAttributeValue_prop1);
        Assert.assertEquals(updatedAttributeValue_prop1.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertEquals(updatedAttributeValue_prop1.getAttributeName(),"prop1");
        Assert.assertEquals(updatedAttributeValue_prop1.getAttributeValue(),Long.parseLong("11111"));

        AttributeValue updatedAttributeValueConfirm_prop1 = _queryResultConceptionEntity.getAttribute("prop1");
        Assert.assertNotNull(updatedAttributeValueConfirm_prop1);
        Assert.assertEquals(updatedAttributeValueConfirm_prop1.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertEquals(updatedAttributeValueConfirm_prop1.getAttributeValue(),Long.parseLong("11111"));

        AttributeValue updatedAttributeValue_prop2 = _queryResultConceptionEntity.updateAttribute("prop2", Double.parseDouble("12345.111"));
        Assert.assertNotNull(updatedAttributeValue_prop2);
        Assert.assertEquals(updatedAttributeValue_prop2.getAttributeDataType(),AttributeDataType.DOUBLE);
        Assert.assertEquals(updatedAttributeValue_prop2.getAttributeName(),"prop2");
        Assert.assertEquals(updatedAttributeValue_prop2.getAttributeValue(), Double.parseDouble("12345.111"));

        AttributeValue updatedAttributeValue_prop3 = _queryResultConceptionEntity.updateAttribute("prop3", Integer.parseInt("445566"));
        Assert.assertNotNull(updatedAttributeValue_prop3);
        Assert.assertEquals(updatedAttributeValue_prop3.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertEquals(updatedAttributeValue_prop3.getAttributeName(),"prop3");
        Assert.assertEquals(updatedAttributeValue_prop3.getAttributeValue(), Long.parseLong("445566"));

        AttributeValue updatedAttributeValue_prop4 = _queryResultConceptionEntity.updateAttribute("prop4","thi is s string");
        Assert.assertNotNull(updatedAttributeValue_prop4);
        Assert.assertEquals(updatedAttributeValue_prop4.getAttributeDataType(),AttributeDataType.STRING);
        Assert.assertEquals(updatedAttributeValue_prop4.getAttributeName(),"prop4");
        Assert.assertEquals(updatedAttributeValue_prop4.getAttributeValue(), "thi is s string");

        AttributeValue updatedAttributeValue_prop5 = _queryResultConceptionEntity.updateAttribute("prop5",Boolean.valueOf("true"));
        Assert.assertNotNull(updatedAttributeValue_prop5);
        Assert.assertEquals(updatedAttributeValue_prop5.getAttributeDataType(),AttributeDataType.BOOLEAN);
        Assert.assertEquals(updatedAttributeValue_prop5.getAttributeName(),"prop5");
        Assert.assertEquals(updatedAttributeValue_prop5.getAttributeValue(), Boolean.valueOf("true"));

        AttributeValue updatedAttributeValue_prop6 = _queryResultConceptionEntity.updateAttribute("prop6",new BigDecimal("556674450.224"));
        Assert.assertNotNull(updatedAttributeValue_prop6);
        Assert.assertEquals(updatedAttributeValue_prop6.getAttributeDataType(),AttributeDataType.DOUBLE);
        Assert.assertEquals(updatedAttributeValue_prop6.getAttributeName(),"prop6");
        Assert.assertEquals(updatedAttributeValue_prop6.getAttributeValue(), Double.parseDouble("556674450.224"));

        AttributeValue updatedAttributeValue_prop7 = _queryResultConceptionEntity.updateAttribute("prop7",Short.valueOf("4"));
        Assert.assertNotNull(updatedAttributeValue_prop7);
        Assert.assertEquals(updatedAttributeValue_prop7.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertEquals(updatedAttributeValue_prop7.getAttributeName(),"prop7");
        Assert.assertEquals(updatedAttributeValue_prop7.getAttributeValue(), Long.parseLong("4"));

        AttributeValue updatedAttributeValue_pro8 = _queryResultConceptionEntity.updateAttribute("prop8",Float.valueOf("123434.66"));
        Assert.assertNotNull(updatedAttributeValue_pro8);
        Assert.assertEquals(updatedAttributeValue_pro8.getAttributeDataType(),AttributeDataType.DOUBLE);
        Assert.assertEquals(updatedAttributeValue_pro8.getAttributeName(),"prop8");
        Assert.assertEquals(updatedAttributeValue_pro8.getAttributeValue(), Double.parseDouble("123434.66"));

        AttributeValue updatedAttributeValue_pro9 = _queryResultConceptionEntity.updateAttribute("prop9",new Long[]{5000l,8000l});
        Assert.assertNotNull(updatedAttributeValue_pro9);
        Assert.assertEquals(updatedAttributeValue_pro9.getAttributeDataType(),AttributeDataType.LONG_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro9.getAttributeName(),"prop9");
        Assert.assertEquals(((Long[])updatedAttributeValue_pro9.getAttributeValue()).length, 2);
        Assert.assertEquals(((Long[])updatedAttributeValue_pro9.getAttributeValue())[0], new Long(5000));

        AttributeValue updatedAttributeValue_pro10 = _queryResultConceptionEntity.updateAttribute("prop10",new Double[]{5000.1d,8000.2d});
        Assert.assertNotNull(updatedAttributeValue_pro10);
        Assert.assertEquals(updatedAttributeValue_pro10.getAttributeDataType(),AttributeDataType.DOUBLE_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro10.getAttributeName(),"prop10");
        Assert.assertEquals(((Double[])updatedAttributeValue_pro10.getAttributeValue()).length, 2);
        Assert.assertEquals(((Double[])updatedAttributeValue_pro10.getAttributeValue())[1], new Double(8000.2));

        AttributeValue updatedAttributeValue_pro11 = _queryResultConceptionEntity.updateAttribute("prop11",new Integer[]{100,2,44,55});
        Assert.assertNotNull(updatedAttributeValue_pro11);
        Assert.assertEquals(updatedAttributeValue_pro11.getAttributeDataType(),AttributeDataType.LONG_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro11.getAttributeName(),"prop11");
        Assert.assertEquals(((Long[])updatedAttributeValue_pro11.getAttributeValue()).length, 4);
        Assert.assertEquals(((Long[])updatedAttributeValue_pro11.getAttributeValue())[3], new Long(55));

        AttributeValue updatedAttributeValue_pro12 = _queryResultConceptionEntity.updateAttribute("prop12",new String[]{"this is str1AA","这是字符串2AA"});
        Assert.assertNotNull(updatedAttributeValue_pro12);
        Assert.assertEquals(updatedAttributeValue_pro12.getAttributeDataType(),AttributeDataType.STRING_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro12.getAttributeName(),"prop12");
        Assert.assertEquals(((String[])updatedAttributeValue_pro12.getAttributeValue()).length, 2);
        Assert.assertEquals(((String[])updatedAttributeValue_pro12.getAttributeValue())[0],"this is str1AA");

        AttributeValue updatedAttributeValue_pro13 = _queryResultConceptionEntity.updateAttribute("prop13",new Boolean[]{true,true});
        Assert.assertNotNull(updatedAttributeValue_pro13);
        Assert.assertEquals(updatedAttributeValue_pro13.getAttributeDataType(),AttributeDataType.BOOLEAN_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro13.getAttributeName(),"prop13");
        Assert.assertEquals(((Boolean[])updatedAttributeValue_pro13.getAttributeValue()).length, 2);
        Assert.assertEquals(((Boolean[])updatedAttributeValue_pro13.getAttributeValue())[1],new Boolean(true));

        AttributeValue updatedAttributeValue_pro14 = _queryResultConceptionEntity.updateAttribute("prop14",new BigDecimal[]{new BigDecimal("1234567.890"),new BigDecimal("987654321.12345")});
        Assert.assertNotNull(updatedAttributeValue_pro14);
        Assert.assertEquals(updatedAttributeValue_pro14.getAttributeDataType(),AttributeDataType.DOUBLE_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro14.getAttributeName(),"prop14");
        Assert.assertEquals(((Double[])updatedAttributeValue_pro14.getAttributeValue()).length, 2);
        Assert.assertEquals(((Double[])updatedAttributeValue_pro14.getAttributeValue())[1],new Double(987654321.12345));

        AttributeValue updatedAttributeValue_pro15 = _queryResultConceptionEntity.updateAttribute("prop15",new Short[]{66,97});
        Assert.assertNotNull(updatedAttributeValue_pro15);
        Assert.assertEquals(updatedAttributeValue_pro15.getAttributeDataType(),AttributeDataType.LONG_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro15.getAttributeName(),"prop15");
        Assert.assertEquals(((Long[])updatedAttributeValue_pro15.getAttributeValue()).length, 2);
        Assert.assertEquals(((Long[])updatedAttributeValue_pro15.getAttributeValue())[0],new Long(66));

        AttributeValue updatedAttributeValue_pro16 = _queryResultConceptionEntity.updateAttribute("prop16",new Float[]{5000.1f,7000.8f});
        Assert.assertNotNull(updatedAttributeValue_pro16);
        Assert.assertEquals(updatedAttributeValue_pro16.getAttributeDataType(),AttributeDataType.DOUBLE_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro16.getAttributeName(),"prop16");
        Assert.assertEquals(((Double[])updatedAttributeValue_pro16.getAttributeValue()).length, 2);
        Assert.assertEquals(((Double[])updatedAttributeValue_pro16.getAttributeValue())[1],new Double(7000.8));

        AttributeValue updatedAttributeValue_pro17 = _queryResultConceptionEntity.updateAttribute("prop17",new Date());
        Assert.assertNotNull(updatedAttributeValue_pro17);
        Assert.assertEquals(updatedAttributeValue_pro17.getAttributeDataType(),AttributeDataType.DATE);
        Assert.assertEquals(updatedAttributeValue_pro17.getAttributeName(),"prop17");
        Assert.assertNotNull(updatedAttributeValue_pro17.getAttributeValue());

        AttributeValue updatedAttributeValue_pro18 = _queryResultConceptionEntity.updateAttribute("prop18", new Date[]{new Date(),new Date()});
        Assert.assertNotNull(updatedAttributeValue_pro18);
        Assert.assertEquals(updatedAttributeValue_pro18.getAttributeDataType(),AttributeDataType.DATE_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro18.getAttributeName(),"prop18");
        Assert.assertEquals(((Date[])updatedAttributeValue_pro18.getAttributeValue()).length, 2);
        Assert.assertNotNull(((Date[])updatedAttributeValue_pro18.getAttributeValue())[1]);
        Assert.assertNotNull(((Date[])updatedAttributeValue_pro18.getAttributeValue())[0]);

        AttributeValue updatedAttributeValue_pro19 = _queryResultConceptionEntity.updateAttribute("prop19", Byte.valueOf("9"));
        Assert.assertNotNull(updatedAttributeValue_pro19);
        Assert.assertEquals(updatedAttributeValue_pro19.getAttributeDataType(),AttributeDataType.LONG);
        Assert.assertEquals(updatedAttributeValue_pro19.getAttributeName(),"prop19");
        Assert.assertEquals(updatedAttributeValue_pro19.getAttributeValue(),new Long(9));

        AttributeValue updatedAttributeValue_pro20 = _queryResultConceptionEntity.updateAttribute("prop20","this is a new byte array value".getBytes());
        Assert.assertNotNull(updatedAttributeValue_pro20);
        Assert.assertEquals(updatedAttributeValue_pro20.getAttributeDataType(),AttributeDataType.LONG_ARRAY);
        Assert.assertEquals(updatedAttributeValue_pro20.getAttributeName(),"prop20");
        Assert.assertNotNull(updatedAttributeValue_pro20.getAttributeValue());

        exceptionShouldBeCaught = false;
        try {
            _queryResultConceptionEntity.removeAttribute("prop1NotExist");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        boolean removeAttributeResult = _queryResultConceptionEntity.removeAttribute("prop1");
        Assert.assertTrue(removeAttributeResult);
        Assert.assertFalse(_queryResultConceptionEntity.hasAttribute("prop1"));
        Assert.assertNull(_queryResultConceptionEntity.getAttribute("prop1"));

        Map<String, Object> newPropertiesMap = new HashMap<>();
        newPropertiesMap.put("newAtt1",Long.valueOf(1000001));
        newPropertiesMap.put("newAtt2",new Date());
        newPropertiesMap.put("prop17","Dup value");

        List<String> addAttributesResult = _queryResultConceptionEntity.addAttributes(newPropertiesMap);

    }
}
