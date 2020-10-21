package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.GreaterThanFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.StatisticalAndEvaluable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GroupNumericalAttributesStatisticResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.NumericalAttributeStatisticCondition;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
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

        try {
            coreRealm.removeConceptionKind(testConceptionKindName+"ForSTAAndEva",true);
        } catch (CoreRealmServiceRuntimeException e) {
            e.printStackTrace();
        }

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

        List<NumericalAttributeStatisticCondition> statisticConditionList = new ArrayList<>();

        statisticConditionList.add(
          new NumericalAttributeStatisticCondition("prop01",StatisticalAndEvaluable.StatisticFunction.MIN)
        );
        statisticConditionList.add(
                new NumericalAttributeStatisticCondition("prop01",StatisticalAndEvaluable.StatisticFunction.AVG)
        );
        statisticConditionList.add(
                new NumericalAttributeStatisticCondition("prop01",StatisticalAndEvaluable.StatisticFunction.COUNT)
        );
        statisticConditionList.add(
                new NumericalAttributeStatisticCondition("prop02",StatisticalAndEvaluable.StatisticFunction.SUM)
        );
        statisticConditionList.add(
                new NumericalAttributeStatisticCondition("prop02",StatisticalAndEvaluable.StatisticFunction.STDEV)
        );
        statisticConditionList.add(
                new NumericalAttributeStatisticCondition("prop02",StatisticalAndEvaluable.StatisticFunction.MAX)
        );

        Map<String,Number> statisticResult = _ConceptionKind01.statisticNumericalAttributes(queryParameters,statisticConditionList);

        Assert.assertNotNull(statisticResult);
        Assert.assertNotNull(statisticResult.get("stDev(prop02)"));
        Assert.assertNotNull(statisticResult.get("avg(prop01)"));
        Assert.assertNotNull(statisticResult.get("count(prop01)"));
        Assert.assertNotNull(statisticResult.get("sum(prop02)"));
        Assert.assertNotNull(statisticResult.get("min(prop01)"));
        Assert.assertNotNull(statisticResult.get("max(prop02)"));

        Assert.assertEquals(statisticResult.get("count(prop01)"),Long.valueOf(90));
        Assert.assertEquals(statisticResult.get("sum(prop02)"),Long.valueOf(504000));
        Assert.assertEquals(statisticResult.get("min(prop01)"),Long.valueOf(1100));
        Assert.assertEquals(statisticResult.get("max(prop02)"),Long.valueOf(10050));

        List<GroupNumericalAttributesStatisticResult> groupStatisticResult = _ConceptionKind01.statisticNumericalAttributesByGroup("groupPropA",queryParameters,statisticConditionList);
        Assert.assertEquals(groupStatisticResult.size(),5);

        List<String> groupList = new ArrayList<>();
        groupList.add("Group1");groupList.add("Group2");groupList.add("Group3");groupList.add("Group4");groupList.add("Group5");
        for(GroupNumericalAttributesStatisticResult currentGroupNumericalAttributesStatisticResult:groupStatisticResult){
            String groupValue = currentGroupNumericalAttributesStatisticResult.getGroupAttributeValue().toString();
            Assert.assertTrue(groupList.contains(groupValue));
            Map<String, Number> groupResult = currentGroupNumericalAttributesStatisticResult.getNumericalAttributesStatisticValue();
            Assert.assertEquals(groupResult.size(),6);
            Assert.assertNotNull(groupResult.get("stDev(prop02)"));
            Assert.assertNotNull(groupResult.get("avg(prop01)"));
            Assert.assertNotNull(groupResult.get("count(prop01)"));
            Assert.assertNotNull(groupResult.get("sum(prop02)"));
            Assert.assertNotNull(groupResult.get("min(prop01)"));
            Assert.assertNotNull(groupResult.get("max(prop02)"));
        }

        coreRealm.closeGlobalSession();
    }
}
