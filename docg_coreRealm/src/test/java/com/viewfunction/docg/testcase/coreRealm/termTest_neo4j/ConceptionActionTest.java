package com.viewfunction.docg.testcase.coreRealm.termTest_neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionAction;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ConceptionActionTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindForActionTest";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ActionTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testActionFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        ConceptionKind testConceptionKind = coreRealm.getConceptionKind(testConceptionKindName);
        if(testConceptionKind != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        testConceptionKind = coreRealm.createConceptionKind(testConceptionKindName,"testKindForActionTestDesc+中文描述");
        Assert.assertNotNull(testConceptionKind);
        Assert.assertEquals(testConceptionKind.getConceptionKindName(),testConceptionKindName);
        Assert.assertEquals(testConceptionKind.getConceptionKindDesc(),"testKindForActionTestDesc+中文描述");

        EntitiesOperationResult purgeEntitiesOperationResult = testConceptionKind.purgeAllEntities();
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(purgeEntitiesOperationResult.getOperationStatistics().getOperationSummary());
        Assert.assertEquals(purgeEntitiesOperationResult.getOperationStatistics().getFailItemsCount(),0);

        Set<ConceptionAction> conceptionActionSet =  testConceptionKind.getActions();
        Assert.assertNotNull(conceptionActionSet);
        Assert.assertEquals(conceptionActionSet.size(),0);

        boolean registerResult = testConceptionKind.registerAction("testActionName1","testActionDesc1","com.viewfunction.docg.externalCustomizedAction.TestExternalConceptionActionLogicExecutor_WRONG");
        Assert.assertTrue(registerResult);

        boolean exceptionShouldThrow1 = false;
        try {
            testConceptionKind.registerAction("testActionName1", "testActionDesc1", "com.viewfunction.docg.externalCustomizedAction.TestExternalConceptionActionLogicExecutor_WRONG");
        }catch (CoreRealmServiceRuntimeException coreRealmServiceRuntimeException){
            coreRealmServiceRuntimeException.printStackTrace();
            exceptionShouldThrow1 = true;
        }
        Assert.assertTrue(exceptionShouldThrow1);

        conceptionActionSet = testConceptionKind.getActions();
        Assert.assertNotNull(conceptionActionSet);
        Assert.assertEquals(conceptionActionSet.size(),1);

        registerResult = testConceptionKind.registerAction("testActionName2","testActionDesc2","com.viewfunction.docg.externalCustomizedAction.TestExternalConceptionActionLogicExecutor");
        Assert.assertTrue(registerResult);

        conceptionActionSet = testConceptionKind.getActions();
        Assert.assertNotNull(conceptionActionSet);
        Assert.assertEquals(conceptionActionSet.size(),2);

        ConceptionAction conceptionAction1 = testConceptionKind.getAction("testActionName1");
        Assert.assertNotNull(conceptionAction1);

        ConceptionAction conceptionAction2 = testConceptionKind.getAction("testActionName2");
        Assert.assertNotNull(conceptionAction2);

        Assert.assertNotNull(conceptionAction1.getActionUID());
        Assert.assertEquals(conceptionAction1.getActionName(),"testActionName1");
        Assert.assertEquals(conceptionAction1.getActionDesc(),"testActionDesc1");
        Assert.assertEquals(conceptionAction1.getActionImplementationClass(),"com.viewfunction.docg.externalCustomizedAction.TestExternalConceptionActionLogicExecutor_WRONG");

        Assert.assertTrue(conceptionAction1.updateActionDesc("testActionDesc1UPD"));
        Assert.assertTrue(conceptionAction1.updateActionImplementationClass("com.viewfunction.docg.externalCustomizedAction.TestExternalConceptionActionLogicExecutor"));

        conceptionAction1 = testConceptionKind.getAction("testActionName1");
        Assert.assertNotNull(conceptionAction1);
        Assert.assertEquals(conceptionAction1.getActionDesc(),"testActionDesc1UPD");
        Assert.assertEquals(conceptionAction1.getActionImplementationClass(),"com.viewfunction.docg.externalCustomizedAction.TestExternalConceptionActionLogicExecutor");

        ConceptionKind containerConceptionKind = conceptionAction1.getContainerConceptionKind();
        Assert.assertNotNull(containerConceptionKind);
        Assert.assertEquals(containerConceptionKind.getConceptionKindName(),testConceptionKindName);

        Map<String,Object> params = new HashMap<>();
        params.put("param01","param01Value");
        params.put("param02",1200);

        Object actionExecuteResult = conceptionAction1.executeActionSync(params);
        Assert.assertNotNull(actionExecuteResult);

        Map<String,Object> resultMap = (Map<String,Object>)actionExecuteResult;
        Assert.assertEquals(resultMap.get("conceptionKindName"),testConceptionKindName);
        Assert.assertEquals(resultMap.get("param01"),"param01Value");
        Assert.assertEquals(resultMap.get("param02"),1200);

        CompletableFuture<Object> resultFuture =  conceptionAction1.executeActionAsync(params);
        Assert.assertNotNull(resultFuture);
        try {
            Object resMap = resultFuture.get();
            resultMap = (Map<String,Object>)resMap;
            Assert.assertEquals(resultMap.get("conceptionKindName"),testConceptionKindName);
            Assert.assertEquals(resultMap.get("param01"),"param01Value");
            Assert.assertEquals(resultMap.get("param02"),1200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);
        newEntityValue.put("prop2",190.22d);
        newEntityValue.put("prop3",50);
        newEntityValue.put("prop4","thi is s string");
        newEntityValue.put("prop5","我是中文string");
        newEntityValue.put("propTmp1", LocalDate.of(1667,1,1));
        newEntityValue.put("propTmp2", new LocalTime[]{LocalTime.of(13,3,3),
                LocalTime.of(14,4,4)});

        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
        ConceptionEntity newEntity = testConceptionKind.newEntity(conceptionEntityValue,false);

        actionExecuteResult = conceptionAction1.executeActionSync(params,newEntity);
        Assert.assertNotNull(actionExecuteResult);

        resultMap = (Map<String,Object>)actionExecuteResult;
        Assert.assertEquals(resultMap.get("conceptionKindName"),testConceptionKindName);
        Assert.assertEquals(resultMap.get("param01"),"param01Value");
        Assert.assertEquals(resultMap.get("param02"),1200);
        Assert.assertEquals(resultMap.get("conceptionEntityUID"),newEntity.getConceptionEntityUID());

        resultFuture =  conceptionAction1.executeActionAsync(params,newEntity);
        Assert.assertNotNull(resultFuture);
        try {
            Object resMap = resultFuture.get();
            resultMap = (Map<String,Object>)resMap;
            Assert.assertEquals(resultMap.get("conceptionKindName"),testConceptionKindName);
            Assert.assertEquals(resultMap.get("param01"),"param01Value");
            Assert.assertEquals(resultMap.get("param02"),1200);
            Assert.assertEquals(resultMap.get("conceptionEntityUID"),newEntity.getConceptionEntityUID());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        boolean unregisterResult = testConceptionKind.unregisterAction("testActionName1");
        Assert.assertTrue(unregisterResult);
        conceptionAction1 = testConceptionKind.getAction("testActionName1");
        Assert.assertNull(conceptionAction1);

        conceptionActionSet = testConceptionKind.getActions();
        Assert.assertNotNull(conceptionActionSet);
        Assert.assertEquals(conceptionActionSet.size(),1);

        unregisterResult = testConceptionKind.unregisterAction("testActionName2");
        Assert.assertTrue(unregisterResult);
        conceptionAction2 = testConceptionKind.getAction("testActionName2");
        Assert.assertNull(conceptionAction2);

        conceptionActionSet = testConceptionKind.getActions();
        Assert.assertNotNull(conceptionActionSet);
        Assert.assertEquals(conceptionActionSet.size(),0);

        boolean exceptionShouldThrow2 = false;
        try {
            testConceptionKind.unregisterAction("testActionName1");
        }catch (CoreRealmServiceRuntimeException coreRealmServiceRuntimeException){
            coreRealmServiceRuntimeException.printStackTrace();
            exceptionShouldThrow2 = true;
        }
        Assert.assertTrue(exceptionShouldThrow2);

        coreRealm.removeConceptionKind(testConceptionKindName,true);
    }

}
