package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.GreaterThanEqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class RelationKindTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindForRelationA";
    private static String testConceptionKindName2 = "TestConceptionKindForRelationB";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for RelationKindTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testRelationKindFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        coreRealm.openGlobalSession();
        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName2);
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        if(_ConceptionKind02 != null){
            coreRealm.removeConceptionKind(testConceptionKindName2,true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        _ConceptionKind02 = coreRealm.getConceptionKind(testConceptionKindName2);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,null);
        }
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind(testConceptionKindName2,null);
        }

        RelationKind _RelationKind01 = coreRealm.getRelationKind("RelationKind0001ForTest");
        if(_RelationKind01 != null){
            coreRealm.removeRelationKind("RelationKind0001ForTest",true);
        }
        _RelationKind01 = coreRealm.getRelationKind("RelationKind0001ForTest");
        if(_RelationKind01 == null){
            _RelationKind01 = coreRealm.createRelationKind("RelationKind0001ForTest",null);
        }

        for(int i =0;i<10;i++){
            Map<String,Object> newEntityValue= new HashMap<>();
            newEntityValue.put("prop1",Math.random());
            newEntityValue.put("prop2",Math.random()+100000);
            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);

            ConceptionEntity _ConceptionEntity_1 = _ConceptionKind01.newEntity(conceptionEntityValue,false);
            ConceptionEntity _ConceptionEntity_2 = _ConceptionKind02.newEntity(conceptionEntityValue,false);

            Map<String,Object> relationPropertiesValue= new HashMap<>();
            relationPropertiesValue.put("relProp1",Math.random()*1000);

            RelationEntity resultRelationEntity = _ConceptionEntity_1.attachFromRelation(_ConceptionEntity_2.getConceptionEntityUID(),"RelationKind0001ForTest",relationPropertiesValue,false);
            Assert.assertNotNull(resultRelationEntity);
        }

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDistinctMode(true);
        RelationEntitiesRetrieveResult _RelationEntitiesRetrieveResult = _RelationKind01.getRelationEntities(queryParameters);

        Assert.assertNotNull(_RelationEntitiesRetrieveResult);
        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getOperationStatistics());

        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getOperationStatistics().getQueryParameters());
        Assert.assertEquals(_RelationEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount(),10l);

        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getOperationStatistics().getFinishTime());

        Assert.assertNotNull(_RelationEntitiesRetrieveResult.getRelationEntities());
        Assert.assertEquals(_RelationEntitiesRetrieveResult.getRelationEntities().size(),10);

        for(RelationEntity currentRelationEntity : _RelationEntitiesRetrieveResult.getRelationEntities()){
            Assert.assertEquals(currentRelationEntity.getRelationKindName(),"RelationKind0001ForTest");
            Assert.assertNotNull(currentRelationEntity.getFromConceptionEntityUID());
            Assert.assertNotNull(currentRelationEntity.getToConceptionEntityUID());
        }

        queryParameters.setResultNumber(7);
        _RelationEntitiesRetrieveResult = _RelationKind01.getRelationEntities(queryParameters);
        Assert.assertNotNull(_RelationEntitiesRetrieveResult);
        Assert.assertEquals(_RelationEntitiesRetrieveResult.getRelationEntities().size(),7);

        queryParameters.setResultNumber(500);
        queryParameters.setDefaultFilteringItem(new GreaterThanEqualFilteringItem("relProp1",500l));
        _RelationEntitiesRetrieveResult = _RelationKind01.getRelationEntities(queryParameters);
        Assert.assertNotNull(_RelationEntitiesRetrieveResult);
        Assert.assertTrue(_RelationEntitiesRetrieveResult.getRelationEntities().size()<10);

        AttributesParameters attributesParameters = new AttributesParameters();
        attributesParameters.setDistinctMode(true);
        attributesParameters.setDefaultFilteringItem(new GreaterThanEqualFilteringItem("relProp1",500l));
        Long entityCount = _RelationKind01.countRelationEntities(attributesParameters);
        long res1 = (_RelationEntitiesRetrieveResult.getOperationStatistics().getResultEntitiesCount());
        Assert.assertEquals(res1,entityCount.longValue());

        Assert.assertEquals(_RelationKind01.countRelationEntities(),new Long(10));

        EntitiesOperationResult purgeAllOperationResult = _RelationKind01.purgeAllRelationEntities();
        Assert.assertNotNull(purgeAllOperationResult);
        Assert.assertNotNull(purgeAllOperationResult.getOperationStatistics());

        Assert.assertEquals(purgeAllOperationResult.getOperationStatistics().getSuccessItemsCount(),10);
        Assert.assertEquals(purgeAllOperationResult.getOperationStatistics().getFailItemsCount(),0);

        Assert.assertNotNull(purgeAllOperationResult.getOperationStatistics().getStartTime());
        Assert.assertNotNull(purgeAllOperationResult.getOperationStatistics().getFinishTime());
        Assert.assertNotNull(purgeAllOperationResult.getOperationStatistics().getOperationSummary());

        Assert.assertEquals(_RelationKind01.countRelationEntities(),new Long(0));

        coreRealm.closeGlobalSession();
    }
}
