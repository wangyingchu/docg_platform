package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.*;

public class EntityQueryTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindForEntityQueryTest";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for EntityQueryTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testEntityQueryFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"TestConceptionKindADesc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
        }

        List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();
        for(int i=0 ; i<100 ; i++){
            conceptionEntityValueList.add(generateRandomConceptionEntityValue());
        }
        _ConceptionKind01.newEntities(conceptionEntityValueList,false);

        Map<String,Object> newEntityValueMap= new HashMap<>();
        newEntityValueMap.put("prop1",Long.parseLong("12345"));
        newEntityValueMap.put("prop2",Double.parseDouble("12345.789"));
        newEntityValueMap.put("prop3",Integer.parseInt("1234"));
        newEntityValueMap.put("prop4","thi is s string");
        newEntityValueMap.put("prop5",Boolean.valueOf("true"));
        newEntityValueMap.put("prop6", new BigDecimal("5566778890.223344"));
        newEntityValueMap.put("prop7",Short.valueOf("24"));
        newEntityValueMap.put("prop8", Float.valueOf("1234.66"));
        newEntityValueMap.put("prop_date",new Date());
        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
        _ConceptionKind01.newEntity(conceptionEntityValue,false);

        newEntityValueMap.put("prop3",Integer.parseInt("1234"));
        newEntityValueMap.put("prop5",Boolean.valueOf("false"));
        conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
        _ConceptionKind01.newEntity(conceptionEntityValue,false);

        newEntityValueMap.put("prop2",Double.parseDouble("9983.3211"));
        newEntityValueMap.put("prop3",Integer.parseInt("4433"));
        newEntityValueMap.put("prop4","a122 thi is s string 234");
        conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
        _ConceptionKind01.newEntity(conceptionEntityValue,false);

        QueryParameters queryParameters = new QueryParameters();
        //queryParameters.setDistinctMode(true);
        //queryParameters.setDistinctMode(false);
        queryParameters.addSortingAttribute("prop2", QueryParameters.SortingLogic.DESC);
        queryParameters.addSortingAttribute("prop16", QueryParameters.SortingLogic.ASC);

        queryParameters.setResultNumber(1000);
        //queryParameters.setPageSize(5);
        //queryParameters.setStartPage(3);
        //queryParameters.setEndPage(8);

        //FilteringItem defaultFilteringItem = new EqualFilteringItem("attribute1",new Date());
        FilteringItem defaultFilteringItem = new EqualFilteringItem("prop7",Short.valueOf("24"));
        queryParameters.setDefaultFilteringItem(defaultFilteringItem);

        queryParameters.addFilteringItem(new EqualFilteringItem("prop8",Float.valueOf("1234.66")), QueryParameters.FilteringLogic.AND);
        queryParameters.addFilteringItem(new EqualFilteringItem("prop3",new Integer(1234)), QueryParameters.FilteringLogic.AND);

        //queryParameters.addFilteringItem(new SimilarFilteringItem("prop12","this ",SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
        //queryParameters.addFilteringItem(new SimilarFilteringItem("attribute3","oss", SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
        queryParameters.addFilteringItem(new EqualFilteringItem("prop5",new Boolean(true)), QueryParameters.FilteringLogic.OR);
        queryParameters.addFilteringItem(new EqualFilteringItem("prop1",Long.parseLong("12345")), QueryParameters.FilteringLogic.OR);

        ConceptionEntitiesRetrieveResult conceptionEntitiesRetrieveResult = _ConceptionKind01.getEntities(queryParameters);
        Assert.assertNotNull(conceptionEntitiesRetrieveResult.getOperationStatistics());
        Assert.assertNotNull(conceptionEntitiesRetrieveResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(conceptionEntitiesRetrieveResult.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(conceptionEntitiesRetrieveResult.getOperationStatistics().getQueryParameters());
        Assert.assertTrue(conceptionEntitiesRetrieveResult.getOperationStatistics().getFinishTime().getTime() >
                        conceptionEntitiesRetrieveResult.getOperationStatistics().getStartTime().getTime());
        Assert.assertTrue(conceptionEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount() >0);
        Assert.assertNotNull(conceptionEntitiesRetrieveResult.getConceptionEntities());
        Assert.assertTrue(conceptionEntitiesRetrieveResult.getConceptionEntities().size() >0);

        AttributesParameters attributesParameters = new AttributesParameters();
        attributesParameters.setDefaultFilteringItem(defaultFilteringItem);
        attributesParameters.addFilteringItem(new EqualFilteringItem("prop8",Float.valueOf("1234.66")), QueryParameters.FilteringLogic.AND);
        attributesParameters.addFilteringItem(new EqualFilteringItem("prop3",new Integer(1234)), QueryParameters.FilteringLogic.AND);
        //attributesParameters.addFilteringItem(new SimilarFilteringItem("prop12","this ",SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
        //attributesParameters.addFilteringItem(new SimilarFilteringItem("attribute3","oss", SimilarFilteringItem.MatchingType.BeginWith), QueryParameters.FilteringLogic.AND);
        attributesParameters.addFilteringItem(new EqualFilteringItem("prop5",new Boolean(true)), QueryParameters.FilteringLogic.OR);
        attributesParameters.addFilteringItem(new EqualFilteringItem("prop1",Long.parseLong("12345")), QueryParameters.FilteringLogic.OR);
        Long entityCount = _ConceptionKind01.countEntities(attributesParameters,false);
        long res1 = (conceptionEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount());
        Assert.assertEquals(res1,entityCount.longValue());

        QueryParameters nullValueQueryParameters = new QueryParameters();
        NullValueFilteringItem nullValueFilteringItem = new NullValueFilteringItem("propertyNotExist");
        nullValueQueryParameters.setDefaultFilteringItem(nullValueFilteringItem);
        conceptionEntitiesRetrieveResult = _ConceptionKind01.getEntities(nullValueQueryParameters);
        Assert.assertTrue(conceptionEntitiesRetrieveResult.getConceptionEntities().size() >0);
        nullValueFilteringItem.reverseCondition();
        conceptionEntitiesRetrieveResult = _ConceptionKind01.getEntities(nullValueQueryParameters);
        Assert.assertTrue(conceptionEntitiesRetrieveResult.getConceptionEntities().size() ==0);

        List<String> attributesNameList = new ArrayList<>();
        attributesNameList.add("prop3");
        attributesNameList.add("prop5");
        attributesNameList.add("prop6");
        attributesNameList.add("propNotExist");
        attributesNameList.add("prop_date");

        ConceptionEntitiesAttributesRetrieveResult entitiesAttributesRetrieveResult1 = _ConceptionKind01.getSingleValueEntityAttributesByAttributeNames(attributesNameList,queryParameters);
        Assert.assertNotNull(entitiesAttributesRetrieveResult1.getOperationStatistics());
        Assert.assertNotNull(entitiesAttributesRetrieveResult1.getOperationStatistics().getStartTime());
        Assert.assertNotNull(entitiesAttributesRetrieveResult1.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(entitiesAttributesRetrieveResult1.getOperationStatistics().getQueryParameters());
        Assert.assertTrue(entitiesAttributesRetrieveResult1.getOperationStatistics().getFinishTime().getTime() >
                entitiesAttributesRetrieveResult1.getOperationStatistics().getStartTime().getTime());
        Assert.assertTrue(entitiesAttributesRetrieveResult1.getOperationStatistics().getResultEntitiesCount()>0);
        List<ConceptionEntityValue> resEntityValueList01 = entitiesAttributesRetrieveResult1.getConceptionEntityValues();
        Assert.assertEquals(resEntityValueList01.size(),entitiesAttributesRetrieveResult1.getOperationStatistics().getResultEntitiesCount());

        for(ConceptionEntityValue currentConceptionEntityValue:resEntityValueList01){
            String entityUID = currentConceptionEntityValue.getConceptionEntityUID();
            Map<String,Object> entityValueMap = currentConceptionEntityValue.getEntityAttributesValue();
            Assert.assertNotNull(entityUID);
            Assert.assertNotNull(entityValueMap);
            Assert.assertEquals(entityValueMap.size(),4);
            Assert.assertNotNull(entityValueMap.get("prop3"));
            Assert.assertNotNull(entityValueMap.get("prop5"));
            Assert.assertNotNull(entityValueMap.get("prop6"));
            Assert.assertNotNull(entityValueMap.get("prop_date"));
            Assert.assertTrue((Boolean) entityValueMap.get("prop5"));
            Assert.assertTrue(entityValueMap.get("prop3") instanceof Long);
            Assert.assertTrue(entityValueMap.get("prop5") instanceof Boolean);
            Assert.assertTrue(entityValueMap.get("prop6") instanceof Double);
            Assert.assertTrue(entityValueMap.get("prop_date") instanceof Date);
        }

        String currentViewKindName = "attributesViewKind"+new Date().getTime();
        AttributesViewKind targetAttributesViewKind = coreRealm.createAttributesViewKind(currentViewKindName,"targetAttributesViewKindADesc",null);
        AttributeKind attributeKind03 = coreRealm.createAttributeKind("prop3","prop3Desc", AttributeDataType.INT);
        AttributeKind attributeKind06 = coreRealm.createAttributeKind("prop6","prop6Desc", AttributeDataType.DECIMAL);
        targetAttributesViewKind.attachAttributeKind(attributeKind03.getAttributeKindUID());
        targetAttributesViewKind.attachAttributeKind(attributeKind06.getAttributeKindUID());
        _ConceptionKind01.attachAttributesViewKind(targetAttributesViewKind.getAttributesViewKindUID());

        entitiesAttributesRetrieveResult1 = _ConceptionKind01.getSingleValueEntityAttributesByAttributeNames(attributesNameList,queryParameters);
        for(ConceptionEntityValue currentConceptionEntityValue:entitiesAttributesRetrieveResult1.getConceptionEntityValues()){
            String entityUID = currentConceptionEntityValue.getConceptionEntityUID();
            Map<String,Object> entityValueMap = currentConceptionEntityValue.getEntityAttributesValue();
            Assert.assertTrue(entityValueMap.get("prop3") instanceof Integer);
            Assert.assertTrue(entityValueMap.get("prop6") instanceof BigDecimal);
        }

        List<String> targetAttributesViewKindList = new ArrayList<>();
        targetAttributesViewKindList.add(currentViewKindName);
        ConceptionEntitiesAttributesRetrieveResult entitiesAttributesRetrieveResult2 = _ConceptionKind01.getSingleValueEntityAttributesByViewKinds(targetAttributesViewKindList,queryParameters);

        Assert.assertNotNull(entitiesAttributesRetrieveResult2.getOperationStatistics());
        Assert.assertNotNull(entitiesAttributesRetrieveResult2.getOperationStatistics().getStartTime());
        Assert.assertNotNull(entitiesAttributesRetrieveResult2.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(entitiesAttributesRetrieveResult2.getOperationStatistics().getQueryParameters());
        Assert.assertTrue(entitiesAttributesRetrieveResult2.getOperationStatistics().getFinishTime().getTime() >
                entitiesAttributesRetrieveResult2.getOperationStatistics().getStartTime().getTime());
        Assert.assertTrue(entitiesAttributesRetrieveResult2.getOperationStatistics().getResultEntitiesCount()>0);
        List<ConceptionEntityValue> resEntityValueList02 = entitiesAttributesRetrieveResult2.getConceptionEntityValues();
        Assert.assertEquals(resEntityValueList01.size(),entitiesAttributesRetrieveResult2.getOperationStatistics().getResultEntitiesCount());

        for(ConceptionEntityValue currentConceptionEntityValue:resEntityValueList02){
            String entityUID = currentConceptionEntityValue.getConceptionEntityUID();
            Map<String,Object> entityValueMap = currentConceptionEntityValue.getEntityAttributesValue();
            Assert.assertNotNull(entityUID);
            Assert.assertNotNull(entityValueMap);
            Assert.assertEquals(entityValueMap.size(),2);
            Assert.assertNotNull(entityValueMap.get("prop3"));
            Assert.assertNull(entityValueMap.get("prop5"));
            Assert.assertNotNull(entityValueMap.get("prop6"));
            Assert.assertNull(entityValueMap.get("prop_date"));
            Assert.assertTrue(entityValueMap.get("prop3") instanceof Integer);
            Assert.assertTrue(entityValueMap.get("prop6") instanceof BigDecimal);
        }

        //testcase for attribute type convert
        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName+"2");
        if(_ConceptionKind02 != null){
            coreRealm.removeConceptionKind(testConceptionKindName+"2",true);
        }
        _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName+"2");
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind(testConceptionKindName+"2","-");
            Assert.assertNotNull(_ConceptionKind02);
        }

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
        newEntityValue.put("prop21", new Byte[]{Byte.valueOf("1"),Byte.valueOf("3"),Byte.valueOf("5")});

        ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValue);
        ConceptionEntity _ConceptionEntity = _ConceptionKind02.newEntity(conceptionEntityValue2,false);
        Assert.assertNotNull(_ConceptionEntity);

        List<String> attributesNameList2 = new ArrayList<>();
        attributesNameList2.add("prop1");
        attributesNameList2.add("prop2");
        attributesNameList2.add("prop3");
        attributesNameList2.add("prop4");
        attributesNameList2.add("prop5");
        attributesNameList2.add("prop6");
        attributesNameList2.add("prop7");
        attributesNameList2.add("prop8");
        attributesNameList2.add("prop9");
        attributesNameList2.add("prop10");
        attributesNameList2.add("prop11");
        attributesNameList2.add("prop12");
        attributesNameList2.add("prop13");
        attributesNameList2.add("prop14");
        attributesNameList2.add("prop15");
        attributesNameList2.add("prop16");
        attributesNameList2.add("prop17");
        attributesNameList2.add("prop18");
        attributesNameList2.add("prop19");
        attributesNameList2.add("prop20");
        attributesNameList2.add("prop21");

        QueryParameters queryParameters2 = new QueryParameters();
        UIDMatchFilteringItem _UIDMatchFilteringItem = new UIDMatchFilteringItem("12345678");
        _UIDMatchFilteringItem.reverseCondition();
        queryParameters2.setDefaultFilteringItem(_UIDMatchFilteringItem);

        ConceptionEntitiesAttributesRetrieveResult entitiesAttributesRetrieveResult3 = _ConceptionKind02.getSingleValueEntityAttributesByAttributeNames(attributesNameList2,queryParameters2);
        List<ConceptionEntityValue> resultConceptionEntityValueList = entitiesAttributesRetrieveResult3.getConceptionEntityValues();
        Assert.assertNotNull(resultConceptionEntityValueList);
        Assert.assertEquals(resultConceptionEntityValueList.size(),1);

        ConceptionEntityValue targetConceptionEntityValue = resultConceptionEntityValueList.get(0);
        Map<String,Object> attributesValueMap = targetConceptionEntityValue.getEntityAttributesValue();
        Assert.assertNotNull(targetConceptionEntityValue.getConceptionEntityUID());
        Assert.assertNotNull(attributesValueMap);
        Assert.assertEquals(attributesValueMap.size(),21);
        Assert.assertTrue(attributesValueMap.get("prop1") instanceof Long);
        Assert.assertTrue(attributesValueMap.get("prop2") instanceof Double);
        Assert.assertTrue(attributesValueMap.get("prop3") instanceof Long);
        Assert.assertTrue(attributesValueMap.get("prop4") instanceof String);
        Assert.assertTrue(attributesValueMap.get("prop5") instanceof Boolean);
        Assert.assertTrue(attributesValueMap.get("prop6") instanceof Double);
        Assert.assertTrue(attributesValueMap.get("prop7") instanceof Long);
        Assert.assertTrue(attributesValueMap.get("prop8") instanceof Double);
        Assert.assertTrue(attributesValueMap.get("prop9") instanceof Long[]);
        Assert.assertTrue(attributesValueMap.get("prop10") instanceof Double[]);
        Assert.assertTrue(attributesValueMap.get("prop11") instanceof Long[]);
        Assert.assertTrue(attributesValueMap.get("prop12") instanceof String[]);
        Assert.assertTrue(attributesValueMap.get("prop13") instanceof Boolean[]);
        Assert.assertTrue(attributesValueMap.get("prop14") instanceof Double[]);
        Assert.assertTrue(attributesValueMap.get("prop15") instanceof Long[]);
        Assert.assertTrue(attributesValueMap.get("prop16") instanceof Double[]);
        Assert.assertTrue(attributesValueMap.get("prop17") instanceof Date);
        Assert.assertTrue(attributesValueMap.get("prop18") instanceof Date[]);
        Assert.assertTrue(attributesValueMap.get("prop19") instanceof Long);
        Assert.assertTrue(attributesValueMap.get("prop20") instanceof Long[]);
        Assert.assertTrue(attributesValueMap.get("prop21") instanceof Long[]);

        String currentViewKindName2 = "attributesViewKind2"+new Date().getTime();
        AttributesViewKind targetAttributesViewKind2 = coreRealm.createAttributesViewKind(currentViewKindName2,"-",null);
        _ConceptionKind02.attachAttributesViewKind(targetAttributesViewKind2.getAttributesViewKindUID());

        List<String> targetAttributesViewKindList2 = new ArrayList<>();
        targetAttributesViewKindList2.add(currentViewKindName2);
        ConceptionEntitiesAttributesRetrieveResult entitiesAttributesRetrieveResult4 = _ConceptionKind02.getSingleValueEntityAttributesByViewKinds(targetAttributesViewKindList2,queryParameters2);
        Assert.assertNull(entitiesAttributesRetrieveResult4);

        AttributeKind attributeKindB01 = coreRealm.createAttributeKind("prop1","-", AttributeDataType.LONG);
        AttributeKind attributeKindB02 = coreRealm.createAttributeKind("prop2","-", AttributeDataType.DOUBLE);
        AttributeKind attributeKindB03 = coreRealm.createAttributeKind("prop3","-", AttributeDataType.INT);
        AttributeKind attributeKindB04 = coreRealm.createAttributeKind("prop4","-", AttributeDataType.STRING);
        AttributeKind attributeKindB05 = coreRealm.createAttributeKind("prop5","-", AttributeDataType.BOOLEAN);
        AttributeKind attributeKindB06 = coreRealm.createAttributeKind("prop6","-", AttributeDataType.DECIMAL);
        AttributeKind attributeKindB07 = coreRealm.createAttributeKind("prop7","-", AttributeDataType.SHORT);
        AttributeKind attributeKindB08 = coreRealm.createAttributeKind("prop8","-", AttributeDataType.FLOAT);
        AttributeKind attributeKindB09 = coreRealm.createAttributeKind("prop9","-", AttributeDataType.LONG_ARRAY);
        AttributeKind attributeKindB010 = coreRealm.createAttributeKind("prop10","-", AttributeDataType.DOUBLE_ARRAY);
        AttributeKind attributeKindB011 = coreRealm.createAttributeKind("prop11","-", AttributeDataType.INT_ARRAY);
        AttributeKind attributeKindB012 = coreRealm.createAttributeKind("prop12","-", AttributeDataType.STRING_ARRAY);
        AttributeKind attributeKindB013 = coreRealm.createAttributeKind("prop13","-", AttributeDataType.BOOLEAN_ARRAY);
        AttributeKind attributeKindB014 = coreRealm.createAttributeKind("prop14","-", AttributeDataType.DECIMAL_ARRAY);
        AttributeKind attributeKindB015 = coreRealm.createAttributeKind("prop15","-", AttributeDataType.SHORT_ARRAY);
        AttributeKind attributeKindB016 = coreRealm.createAttributeKind("prop16","-", AttributeDataType.FLOAT_ARRAY);
        AttributeKind attributeKindB017 = coreRealm.createAttributeKind("prop17","-", AttributeDataType.TIMESTAMP);
        AttributeKind attributeKindB018 = coreRealm.createAttributeKind("prop18","-", AttributeDataType.TIMESTAMP_ARRAY);
        AttributeKind attributeKindB019 = coreRealm.createAttributeKind("prop19","-", AttributeDataType.BYTE);
        AttributeKind attributeKindB020 = coreRealm.createAttributeKind("prop20","-", AttributeDataType.BINARY);
        AttributeKind attributeKindB021 = coreRealm.createAttributeKind("prop21","-", AttributeDataType.BYTE_ARRAY);
        targetAttributesViewKind2.attachAttributeKind(attributeKindB01.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB02.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB03.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB04.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB05.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB06.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB07.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB08.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB09.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB010.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB011.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB012.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB013.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB014.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB015.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB016.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB017.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB018.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB019.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB020.getAttributeKindUID());
        targetAttributesViewKind2.attachAttributeKind(attributeKindB021.getAttributeKindUID());

        entitiesAttributesRetrieveResult4 = _ConceptionKind02.getSingleValueEntityAttributesByViewKinds(targetAttributesViewKindList2,queryParameters2);
        List<ConceptionEntityValue> resultConceptionEntityValueList4 = entitiesAttributesRetrieveResult4.getConceptionEntityValues();
        Assert.assertNotNull(resultConceptionEntityValueList4);
        Assert.assertEquals(resultConceptionEntityValueList4.size(),1);

        ConceptionEntityValue targetConceptionEntityValue2 = resultConceptionEntityValueList4.get(0);
        Map<String,Object> attributesValueMap2 = targetConceptionEntityValue2.getEntityAttributesValue();
        Assert.assertNotNull(targetConceptionEntityValue2.getConceptionEntityUID());
        Assert.assertNotNull(attributesValueMap2);

        Assert.assertEquals(attributesValueMap2.size(),21);
        Assert.assertTrue(attributesValueMap2.get("prop1") instanceof Long);
        Assert.assertTrue(attributesValueMap2.get("prop2") instanceof Double);
        Assert.assertTrue(attributesValueMap2.get("prop3") instanceof Integer);
        Assert.assertTrue(attributesValueMap2.get("prop4") instanceof String);
        Assert.assertTrue(attributesValueMap2.get("prop5") instanceof Boolean);
        Assert.assertTrue(attributesValueMap2.get("prop6") instanceof BigDecimal);
        Assert.assertTrue(attributesValueMap2.get("prop7") instanceof Short);
        Assert.assertTrue(attributesValueMap2.get("prop8") instanceof Float);
        Assert.assertTrue(attributesValueMap2.get("prop9") instanceof Long[]);
        Assert.assertTrue(attributesValueMap2.get("prop10") instanceof Double[]);
        Assert.assertTrue(attributesValueMap2.get("prop11") instanceof Integer[]);
        Assert.assertTrue(attributesValueMap2.get("prop12") instanceof String[]);
        Assert.assertTrue(attributesValueMap2.get("prop13") instanceof Boolean[]);
        Assert.assertTrue(attributesValueMap2.get("prop14") instanceof BigDecimal[]);
        Assert.assertTrue(attributesValueMap2.get("prop15") instanceof Short[]);
        Assert.assertTrue(attributesValueMap2.get("prop16") instanceof Float[]);
        Assert.assertTrue(attributesValueMap2.get("prop17") instanceof Date);
        Assert.assertTrue(attributesValueMap2.get("prop18") instanceof Date[]);
        Assert.assertTrue(attributesValueMap2.get("prop19") instanceof Byte);
        Assert.assertTrue(attributesValueMap2.get("prop20") instanceof byte[]);
        Assert.assertTrue(attributesValueMap2.get("prop21") instanceof Byte[]);
    }

    private ConceptionEntityValue generateRandomConceptionEntityValue(){
        Map<String,Object> newEntityValueMap= new HashMap<>();

        long l = (long)(Math.random()*50000);
        newEntityValueMap.put("prop1",Long.valueOf(l));
        double d = Math.random();
        newEntityValueMap.put("prop2",Double.valueOf(d));
        int i = (int)(Math.random()*1000);
        newEntityValueMap.put("prop3",Integer.valueOf(i));
        String str = generateRandomString();
        newEntityValueMap.put("prop4",str);
        Random random=new Random();
        newEntityValueMap.put("prop5",random.nextBoolean());
        newEntityValueMap.put("prop6",new BigDecimal(Math.random()));
        newEntityValueMap.put("prop_date",new Date());
        /*
        int num = (int)(Math.random()*100+1);
        int m =39;
        int n= 10;
        int num2 = (int)(Math.random()*(m-n+1)+m);
        */
        return new ConceptionEntityValue(newEntityValueMap);
    }

    private String generateRandomString(){
        String str="abcdef ghijklmnopqrs tuvwxyzABCDEFGH IJKLMNOPQRSTUVWXY Z0123456789";
        Random random1=new Random();
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < 40; i++) {
            int number=random1.nextInt(str.length());
            char charAt = str.charAt(number);
            sb.append(charAt);
        }
        return sb.toString();
    }
}
