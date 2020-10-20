package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.GreaterThanFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.StatisticalAndEvaluable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GroupNumericalAttributesStatisticResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticalAndEvaluableTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKind01";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for StatisticalAndEvaluableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testStatisticalAndEvaluableFunction() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        /*
        try {
            coreRealm.removeConceptionKind(testConceptionKindName+"ForSTAAndEva",true);
        } catch (CoreRealmServiceRuntimeException e) {
            e.printStackTrace();
        }
        */
        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName+"ForSTAAndEva");

        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName+"ForSTAAndEva","testKind01Desc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),testConceptionKindName+"ForSTAAndEva");
            Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"testKind01Desc+中文描述");

            for(int i =0;i<100;i++){
                Map<String,Object> newEntityValue= new HashMap<>();
                newEntityValue.put("prop01",(i+1)*100);
                newEntityValue.put("prop02",(i+1)*100+50);

                if(i<20){
                    newEntityValue.put("groupPropA","Group1");
                    newEntityValue.put("groupPropB","Group1-B");
                }else if(i<40){
                    newEntityValue.put("groupPropA","Group2");
                    newEntityValue.put("groupPropB","Group2-B");
                }else if(i<60){
                    newEntityValue.put("groupPropA","Group3");
                    newEntityValue.put("groupPropB","Group3-B");
                }else if(i<80){
                    newEntityValue.put("groupPropA","Group4");
                    newEntityValue.put("groupPropB","Group4-B");
                }else{
                    newEntityValue.put("groupPropA","Group5");
                    newEntityValue.put("groupPropB","Group5-B");
                }

                ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
                _ConceptionKind01.newEntity(conceptionEntityValue,false);
            }
        }

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDefaultFilteringItem(new GreaterThanFilteringItem("prop01",1000));

        Map<String, StatisticalAndEvaluable.StatisticFunction> statisticConditions = new HashMap<>();
        //statisticConditions.put("prop01",StatisticalAndEvaluable.StatisticFunction.AVG);
        //statisticConditions.put("prop01",StatisticalAndEvaluable.StatisticFunction.MAX);
        statisticConditions.put("prop01",StatisticalAndEvaluable.StatisticFunction.MIN);
        //statisticConditions.put("prop02",StatisticalAndEvaluable.StatisticFunction.COUNT);
        statisticConditions.put("prop02",StatisticalAndEvaluable.StatisticFunction.SUM);
        //statisticConditions.put("prop02",StatisticalAndEvaluable.StatisticFunction.STDEV);
        Map<String,Number> statisticResult = _ConceptionKind01.statisticNumericalAttributes(queryParameters,statisticConditions);
        System.out.println(statisticResult);

        List<GroupNumericalAttributesStatisticResult> groupStatisticResult = _ConceptionKind01.statisticNumericalAttributesByGroup("groupPropA",queryParameters,statisticConditions);
        System.out.println(groupStatisticResult);
        Assert.assertEquals(groupStatisticResult.size(),5);

        coreRealm.closeGlobalSession();
    }
}
