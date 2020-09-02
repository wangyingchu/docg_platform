package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
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

        QueryParameters queryParameters = new QueryParameters();
        //queryParameters.setDistinctMode(true);
        //queryParameters.setDistinctMode(false);
        queryParameters.addSortingAttribute("prop18", QueryParameters.SortingLogic.DESC);
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
        System.out.println( conceptionEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount());
        System.out.println( conceptionEntitiesRetrieveResult.getConceptionEntities());




    }
}
