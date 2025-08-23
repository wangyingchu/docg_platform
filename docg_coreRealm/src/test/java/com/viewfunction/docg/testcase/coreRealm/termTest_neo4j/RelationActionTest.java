package com.viewfunction.docg.testcase.coreRealm.termTest_neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RelationActionTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindForActionTest2";
    private static String testRelationKindName = "TestRelationKindForActionTest";
    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ConceptionActionTest");
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

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);
        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
        ConceptionEntity newEntity1 = testConceptionKind.newEntity(conceptionEntityValue,false);

        newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);
        conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
        ConceptionEntity newEntity2 = testConceptionKind.newEntity(conceptionEntityValue,false);

        RelationKind testRelationKind = coreRealm.getRelationKind(testRelationKindName);
        if(testRelationKind != null){
            coreRealm.removeRelationKind(testRelationKindName,true);
        }
        testRelationKind = coreRealm.createRelationKind(testRelationKindName,"testRelationKindForActionTestDesc+中文描述");

        Assert.assertNotNull(testRelationKind);
        Assert.assertEquals(testRelationKind.getRelationKindName(),testRelationKindName);
        Assert.assertEquals(testRelationKind.getRelationKindDesc(),"testRelationKindForActionTestDesc+中文描述");

        Set<RelationAction> relationActionSet = testRelationKind.getActions();
        Assert.assertNotNull(relationActionSet);
        Assert.assertEquals(relationActionSet.size(),0);

        boolean registerResult = testRelationKind.registerAction("testActionName1","testActionDesc1","com.viewfunction.docg.externalCustomizedAction.TestExternalRelationActionLogicExecutor_WRONG");
        Assert.assertTrue(registerResult);

        boolean exceptionShouldThrow1 = false;
        try {
            testRelationKind.registerAction("testActionName1", "testActionDesc1", "com.viewfunction.docg.externalCustomizedAction.TestExternalRelationActionLogicExecutor_WRONG");
        }catch (CoreRealmServiceRuntimeException coreRealmServiceRuntimeException){
            coreRealmServiceRuntimeException.printStackTrace();
            exceptionShouldThrow1 = true;
        }
        Assert.assertTrue(exceptionShouldThrow1);

        relationActionSet = testRelationKind.getActions();
        Assert.assertNotNull(relationActionSet);
        Assert.assertEquals(relationActionSet.size(),1);

        registerResult = testRelationKind.registerAction("testActionName2","testActionDesc2","com.viewfunction.docg.externalCustomizedAction.TestExternalRelationActionLogicExecutor");
        Assert.assertTrue(registerResult);

        relationActionSet = testRelationKind.getActions();
        Assert.assertNotNull(relationActionSet);
        Assert.assertEquals(relationActionSet.size(),2);

        RelationAction relationAction1 = testRelationKind.getAction("testActionName1");
        Assert.assertNotNull(relationAction1);

        RelationAction relationAction2 = testRelationKind.getAction("testActionName2");
        Assert.assertNotNull(relationAction2);

        Assert.assertNotNull(relationAction1.getActionUID());
        Assert.assertEquals(relationAction1.getActionName(),"testActionName1");
        Assert.assertEquals(relationAction1.getActionDesc(),"testActionDesc1");
        Assert.assertEquals(relationAction1.getActionImplementationClass(),"com.viewfunction.docg.externalCustomizedAction.TestExternalRelationActionLogicExecutor_WRONG");

        Assert.assertTrue(relationAction1.updateActionDesc("testActionDesc1UPD"));
        Assert.assertTrue(relationAction1.updateActionImplementationClass("com.viewfunction.docg.externalCustomizedAction.TestExternalRelationActionLogicExecutor"));

        relationAction1 = testRelationKind.getAction("testActionName1");
        Assert.assertNotNull(relationAction1);
        Assert.assertEquals(relationAction1.getActionDesc(),"testActionDesc1UPD");
        Assert.assertEquals(relationAction1.getActionImplementationClass(),"com.viewfunction.docg.externalCustomizedAction.TestExternalRelationActionLogicExecutor");

        RelationKind containerRelationKind = relationAction1.getContainerRelationKind();
        Assert.assertNotNull(containerRelationKind);
        Assert.assertEquals(containerRelationKind.getRelationKindName(),testRelationKindName);

        Map<String,Object> params = new HashMap<>();
        params.put("param01","param01Value");
        params.put("param02",1200);

        Object actionExecuteResult = relationAction1.executeActionSync(params);
        Assert.assertNotNull(actionExecuteResult);

        Map<String,Object> resultMap = (Map<String,Object>)actionExecuteResult;
        Assert.assertEquals(resultMap.get("relationKindName"),testRelationKindName);
        Assert.assertEquals(resultMap.get("param01"),"param01Value");
        Assert.assertEquals(resultMap.get("param02"),1200);

        CompletableFuture<Object> resultFuture =  relationAction1.executeActionAsync(params);
        Assert.assertNotNull(resultFuture);
        try {
            Object resMap = resultFuture.get();
            resultMap = (Map<String,Object>)resMap;
            Assert.assertEquals(resultMap.get("relationKindName"),testRelationKindName);
            Assert.assertEquals(resultMap.get("param01"),"param01Value");
            Assert.assertEquals(resultMap.get("param02"),1200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);
        newEntityValue.put("prop2",190.22d);
        newEntityValue.put("prop3",50);
        newEntityValue.put("prop4","thi is s string");
        newEntityValue.put("prop5","我是中文string");

        RelationEntity targetRelationEntity = newEntity1.attachFromRelation(newEntity2.getConceptionEntityUID(),testRelationKindName,newEntityValue,false);
        Assert.assertNotNull(targetRelationEntity);

        actionExecuteResult = relationAction1.executeActionSync(params,targetRelationEntity);
        Assert.assertNotNull(actionExecuteResult);

        resultMap = (Map<String,Object>)actionExecuteResult;
        Assert.assertEquals(resultMap.get("relationKindName"),testRelationKindName);
        Assert.assertEquals(resultMap.get("param01"),"param01Value");
        Assert.assertEquals(resultMap.get("param02"),1200);
        Assert.assertEquals(resultMap.get("relationEntityUID"),targetRelationEntity.getRelationEntityUID());

        resultFuture =  relationAction1.executeActionAsync(params,targetRelationEntity);
        Assert.assertNotNull(resultFuture);
        try {
            Object resMap = resultFuture.get();
            resultMap = (Map<String,Object>)resMap;
            Assert.assertEquals(resultMap.get("relationKindName"),testRelationKindName);
            Assert.assertEquals(resultMap.get("param01"),"param01Value");
            Assert.assertEquals(resultMap.get("param02"),1200);
            Assert.assertEquals(resultMap.get("relationEntityUID"),targetRelationEntity.getRelationEntityUID());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        boolean unregisterResult = testRelationKind.unregisterAction("testActionName1");
        Assert.assertTrue(unregisterResult);
        relationAction1 = testRelationKind.getAction("testActionName1");
        Assert.assertNull(relationAction1);

        relationActionSet = testRelationKind.getActions();
        Assert.assertNotNull(relationActionSet);
        Assert.assertEquals(relationActionSet.size(),1);

        unregisterResult = testRelationKind.unregisterAction("testActionName2");
        Assert.assertTrue(unregisterResult);
        relationAction2 = testRelationKind.getAction("testActionName2");
        Assert.assertNull(relationAction2);

        relationActionSet = testRelationKind.getActions();
        Assert.assertNotNull(relationActionSet);
        Assert.assertEquals(relationActionSet.size(),0);

        boolean exceptionShouldThrow2 = false;
        try {
            testRelationKind.unregisterAction("testActionName1");
        }catch (CoreRealmServiceRuntimeException coreRealmServiceRuntimeException){
            coreRealmServiceRuntimeException.printStackTrace();
            exceptionShouldThrow2 = true;
        }
        Assert.assertTrue(exceptionShouldThrow2);

        coreRealm.removeConceptionKind(testConceptionKindName,true);
        coreRealm.removeRelationKind(testRelationKindName,true);
    }
}
