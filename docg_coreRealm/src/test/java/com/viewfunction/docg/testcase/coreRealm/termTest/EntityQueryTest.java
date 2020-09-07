package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
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

        //need test case
        System.out.println( conceptionEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount());
        System.out.println( conceptionEntitiesRetrieveResult.getConceptionEntities());

        List<String> attributesNameList = new ArrayList<>();
        attributesNameList.add("prop3");
        attributesNameList.add("prop5");
        attributesNameList.add("prop6");
        attributesNameList.add("propNotExist");
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

            System.out.println(entityUID);
            System.out.println(entityValueMap);

        }


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
