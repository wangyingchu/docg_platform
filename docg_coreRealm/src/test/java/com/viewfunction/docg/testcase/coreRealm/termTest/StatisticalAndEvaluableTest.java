package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.GreaterThanFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.StatisticalAndEvaluable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GroupNumericalAttributesStatisticResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.NumericalAttributeStatisticCondition;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
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

        Classification classification0 = coreRealm.getClassification("Classification0");
        if(classification0 == null){
            coreRealm.createClassification("Classification0","Classification0Desc");
        }
        Classification classification1 = coreRealm.getClassification("Classification1");
        if(classification1 == null){
            coreRealm.createClassification("Classification1","Classification1Desc");
        }
        Classification classification2 = coreRealm.getClassification("Classification2");
        if(classification2 == null){
            coreRealm.createClassification("Classification2","Classification2Desc");
        }
        RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
        relationAttachInfo.setRelationKind("RelationKind0001");
        relationAttachInfo.setRelationDirection(RelationDirection.FROM);

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
                ConceptionEntity currentEntity = _ConceptionKind01.newEntity(conceptionEntityValue,false);
                try {
                    if(i<20){
                        currentEntity.attachClassification(relationAttachInfo,"Classification1");
                    }else if(i<40){
                        currentEntity.attachClassification(relationAttachInfo,"Classification0");
                    }else if(i<60){
                        currentEntity.attachClassification(relationAttachInfo,"Classification2");
                    }else if(i<80){
                        currentEntity.attachClassification(relationAttachInfo,"Classification1");
                    }else{
                        currentEntity.attachClassification(relationAttachInfo,"Classification0");
                    }
                } catch (CoreRealmServiceRuntimeException e) {
                    e.printStackTrace();
                }
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

        CoreRealm coreRealm2 = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind _ConceptionKind2 = coreRealm2.getConceptionKind(testConceptionKindName+"ForSTAAndEva");
        Map<String,List<ConceptionEntity>> staticClassificationResult = _ConceptionKind2.statisticRelatedClassifications(null,"RelationKind0001", RelationDirection.TO);

        Assert.assertEquals(staticClassificationResult.keySet().size(),3);
        Assert.assertTrue(staticClassificationResult.containsKey("Classification0"));
        Assert.assertTrue(staticClassificationResult.containsKey("Classification1"));
        Assert.assertTrue(staticClassificationResult.containsKey("Classification2"));

        int entityNumber = staticClassificationResult.get("Classification0").size()+
                staticClassificationResult.get("Classification1").size()+
                staticClassificationResult.get("Classification2").size();
        Assert.assertEquals(entityNumber,100);

        ConceptionEntity _ConceptionEntity01 = staticClassificationResult.get("Classification0").get(0);
        Assert.assertEquals(_ConceptionEntity01.getConceptionKindName(),testConceptionKindName+"ForSTAAndEva");
        List<Classification> resultClassificationList01 = _ConceptionEntity01.getAttachedClassifications("RelationKind0001", RelationDirection.FROM);
        Assert.assertEquals(resultClassificationList01.get(0).getClassificationName(),"Classification0");

        ConceptionEntity _ConceptionEntity02 = staticClassificationResult.get("Classification1").get(0);
        Assert.assertEquals(_ConceptionEntity02.getConceptionKindName(),testConceptionKindName+"ForSTAAndEva");
        List<Classification> resultClassificationList02 = _ConceptionEntity02.getAttachedClassifications("RelationKind0001", RelationDirection.FROM);
        Assert.assertEquals(resultClassificationList02.get(0).getClassificationName(),"Classification1");

        ConceptionEntity _ConceptionEntity03 = staticClassificationResult.get("Classification2").get(0);
        Assert.assertEquals(_ConceptionEntity03.getConceptionKindName(),testConceptionKindName+"ForSTAAndEva");
        List<Classification> resultClassificationList03 = _ConceptionEntity03.getAttachedClassifications("RelationKind0001", RelationDirection.FROM);
        Assert.assertEquals(resultClassificationList03.get(0).getClassificationName(),"Classification2");

        staticClassificationResult = _ConceptionKind2.statisticRelatedClassifications(null,"RelationKind0001", RelationDirection.FROM);
        Assert.assertEquals(staticClassificationResult.keySet().size(),0);

        staticClassificationResult = _ConceptionKind2.statisticRelatedClassifications(null,"RelationKind0001", RelationDirection.TWO_WAY);
        Assert.assertEquals(staticClassificationResult.keySet().size(),3);

        RelationKind _RelationKind0001 = coreRealm2.getRelationKind("RelationKind0001");

        if(_RelationKind0001 != null){
            try {
                coreRealm.removeRelationKind("RelationKind0001",true);
            } catch (CoreRealmServiceRuntimeException e) {
                e.printStackTrace();
            }
        }
        _RelationKind0001 = coreRealm2.createRelationKind("RelationKind0001","DESC");

        List<GroupNumericalAttributesStatisticResult> groupNumericalAttributesStatisticResult2 = _RelationKind0001.statisticNumericalAttributesByGroup("groupPropA",queryParameters,statisticConditionList);
        Assert.assertEquals(groupNumericalAttributesStatisticResult2.size(),0);


        Map<String,Number> numberMap2 = _RelationKind0001.statisticNumericalAttributes(queryParameters,statisticConditionList);
        Assert.assertEquals(numberMap2.size(),6);

        boolean exceptionShouldThrown = false;
        try {
            _RelationKind0001.statisticRelatedClassifications(null, "RelationKind0001", RelationDirection.TO);
        }catch (CoreRealmServiceEntityExploreException e){
            exceptionShouldThrown = true;
        }
        Assert.assertTrue(exceptionShouldThrown);
    }
}
