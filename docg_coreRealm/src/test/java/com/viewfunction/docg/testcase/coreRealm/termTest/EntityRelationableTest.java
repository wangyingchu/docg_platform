package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.ResultEntitiesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.GreaterThanFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.LessThanFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRelationableTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindForRelationableTest";
    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for EntityRelationableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testEntityRelationableFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"TestConceptionKindADesc+中文描述");
        }

        Map<String,Object> newEntityValue1= new HashMap<>();
        newEntityValue1.put("prop1","fromEntity");
        ConceptionEntityValue conceptionEntityValue1 = new ConceptionEntityValue(newEntityValue1);
        ConceptionEntity _ConceptionEntity1 = _ConceptionKind01.newEntity(conceptionEntityValue1,false);

        Map<String,Object> newEntityValue2= new HashMap<>();
        newEntityValue2.put("prop1","toEntity");
        ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValue2);
        ConceptionEntity _ConceptionEntity2 = _ConceptionKind01.newEntity(conceptionEntityValue2,false);

        Map<String,Object> newRelationValue= new HashMap<>();
        newRelationValue.put("prop1",10000l);
        newRelationValue.put("prop2",190.22d);
        newRelationValue.put("prop3",50);
        newRelationValue.put("prop4","thi is s string");
        newRelationValue.put("prop5","我是中文string");

        Assert.assertEquals(_ConceptionEntity1.countAllRelations(),new Long(0));
        Assert.assertEquals(_ConceptionEntity2.countAllRelations(),new Long(0));
        Assert.assertEquals(_ConceptionEntity1.getAllRelations().size(),0);

        RelationEntity relationEntity01 = _ConceptionEntity1.attachFromRelation(_ConceptionEntity2.getConceptionEntityUID(),"testRelationType01",newRelationValue,true);

        Assert.assertEquals(_ConceptionEntity1.countAllRelations(),new Long(1));
        Assert.assertEquals(_ConceptionEntity2.countAllRelations(),new Long(1));
        Assert.assertEquals(_ConceptionEntity1.getAllRelations().size(),1);

        Assert.assertEquals(_ConceptionEntity1.getAllRelations().get(0).getRelationEntityUID(),relationEntity01.getRelationEntityUID());
        Assert.assertEquals(_ConceptionEntity1.getAllRelations().get(0).getFromConceptionEntityUID(),_ConceptionEntity1.getConceptionEntityUID());
        Assert.assertEquals(_ConceptionEntity1.getAllRelations().get(0).getToConceptionEntityUID(),_ConceptionEntity2.getConceptionEntityUID());

        RelationEntity relationEntity02 = _ConceptionEntity1.attachToRelation(_ConceptionEntity2.getConceptionEntityUID(),"testRelationType02",newRelationValue,true);

        Assert.assertEquals(_ConceptionEntity1.countAllRelations(),new Long(2));
        Assert.assertEquals(_ConceptionEntity2.countAllRelations(),new Long(2));
        Assert.assertEquals(_ConceptionEntity1.getAllRelations().size(),2);

        List<RelationEntity> relationEntityList1 = _ConceptionEntity1.getAllSpecifiedRelations(null, RelationDirection.TWO_WAY);
        Assert.assertEquals(relationEntityList1.size(),2);
        relationEntityList1 = _ConceptionEntity1.getAllSpecifiedRelations(null, RelationDirection.FROM);
        Assert.assertEquals(relationEntityList1.size(),1);
        Assert.assertEquals(relationEntityList1.get(0).getRelationEntityUID(),relationEntity01.getRelationEntityUID());
        relationEntityList1 = _ConceptionEntity1.getAllSpecifiedRelations(null, RelationDirection.TO);
        Assert.assertEquals(relationEntityList1.size(),1);
        Assert.assertEquals(relationEntityList1.get(0).getRelationEntityUID(),relationEntity02.getRelationEntityUID());

        relationEntityList1 = _ConceptionEntity1.getAllSpecifiedRelations("RelationKindNotExist", RelationDirection.TWO_WAY);
        Assert.assertEquals(relationEntityList1.size(),0);
        Assert.assertEquals(_ConceptionEntity1.countAllSpecifiedRelations("RelationKindNotExist", RelationDirection.TWO_WAY),new Long(0));

        relationEntityList1 = _ConceptionEntity1.getAllSpecifiedRelations("testRelationType01", RelationDirection.TWO_WAY);
        Assert.assertEquals(relationEntityList1.size(),1);
        Assert.assertEquals(_ConceptionEntity1.countAllSpecifiedRelations("testRelationType01", RelationDirection.TWO_WAY),new Long(1));

        relationEntityList1 = _ConceptionEntity1.getAllSpecifiedRelations("testRelationType01", RelationDirection.FROM);
        Assert.assertEquals(relationEntityList1.size(),1);
        Assert.assertEquals(_ConceptionEntity1.countAllSpecifiedRelations("testRelationType01", RelationDirection.FROM),new Long(1));

        relationEntityList1 = _ConceptionEntity1.getAllSpecifiedRelations("testRelationType01", RelationDirection.TO);
        Assert.assertEquals(relationEntityList1.size(),0);
        Assert.assertEquals(_ConceptionEntity1.countAllSpecifiedRelations("testRelationType01", RelationDirection.TO),new Long(0));

        QueryParameters queryParameters1 = new QueryParameters();

        FilteringItem defaultFilteringItem = new EqualFilteringItem("prop1",10000l);
        queryParameters1.setDefaultFilteringItem(defaultFilteringItem);
        FilteringItem orFilteringItem = new EqualFilteringItem("prop3",50);
        queryParameters1.addFilteringItem(orFilteringItem, QueryParameters.FilteringLogic.OR);
        queryParameters1.addSortingAttribute("prop4", QueryParameters.SortingLogic.ASC);

        List<RelationEntity> relationEntityList2 = _ConceptionEntity1.getSpecifiedRelations(queryParameters1,RelationDirection.TWO_WAY);
        Assert.assertEquals(relationEntityList2.size(),4);
        Long countSpecifiedRelations = _ConceptionEntity1.countSpecifiedRelations(queryParameters1,RelationDirection.TWO_WAY);
        Assert.assertEquals(countSpecifiedRelations,new Long(4));

        queryParameters1.setDistinctMode(true);
        relationEntityList2 = _ConceptionEntity1.getSpecifiedRelations(queryParameters1,RelationDirection.TWO_WAY);
        Assert.assertEquals(relationEntityList2.size(),2);
        // need check count number defect
        countSpecifiedRelations = _ConceptionEntity1.countSpecifiedRelations(queryParameters1,RelationDirection.TWO_WAY);
        Assert.assertEquals(countSpecifiedRelations,new Long(2));

        //use batch operation mode way 1
        GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
        CoreRealm coreRealm2 = RealmTermFactory.getDefaultCoreRealm();
        ((Neo4JCoreRealmImpl)coreRealm2).setGlobalGraphOperationExecutor(graphOperationExecutor);
        _ConceptionKind01 = coreRealm2.getConceptionKind(testConceptionKindName);
        try{
            Map<String,Object> newEntityValue3= new HashMap<>();
            newEntityValue3.put("prop1ForRelTest","ConceptionEntity3");
            ConceptionEntityValue conceptionEntityValue3 = new ConceptionEntityValue(newEntityValue3);
            ConceptionEntity _ConceptionEntity3 = _ConceptionKind01.newEntity(conceptionEntityValue3,false);

            Map<String,Object> newEntityValue4= new HashMap<>();
            newEntityValue4.put("prop1ForRelTest","ConceptionEntity4");
            ConceptionEntityValue conceptionEntityValue4 = new ConceptionEntityValue(newEntityValue4);
            ConceptionEntity _ConceptionEntity4 = _ConceptionKind01.newEntity(conceptionEntityValue4,false);

            for(int i = 0; i<100;i++){
                Map<String,Object> relationValue= new HashMap<>();
                relationValue.put("prop1",10000l+i);
                relationValue.put("prop2",190.22d+i);
                relationValue.put("prop3",50+i);
                relationValue.put("prop4","thi is s stringA"+i);
                _ConceptionEntity3.attachFromRelation(_ConceptionEntity4.getConceptionEntityUID(),"testRelationTypeA",relationValue,true);
            }

            for(int i = 0; i<100;i++){
                Map<String,Object> relationValue= new HashMap<>();
                relationValue.put("prop2",1000.22d+i);
                relationValue.put("prop3",600+i);
                relationValue.put("prop4","thi is s stringB"+i);
                _ConceptionEntity3.attachToRelation(_ConceptionEntity4.getConceptionEntityUID(),"testRelationTypeA",relationValue,true);
            }

            for(int i = 0; i<50;i++){
                Map<String,Object> relationValue= new HashMap<>();
                relationValue.put("prop1",10000l+i);
                relationValue.put("prop2",190.22d+i);
                relationValue.put("prop3",100000+i);
                relationValue.put("prop4","thi is s stringA"+i);
                _ConceptionEntity3.attachFromRelation(_ConceptionEntity4.getConceptionEntityUID(),"testRelationTypeB",relationValue,true);
            }

            for(int i = 0; i<50;i++){
                Map<String,Object> relationValue= new HashMap<>();
                relationValue.put("prop2",1000.22d+i);
                relationValue.put("prop3",50000+i);
                relationValue.put("prop4","thi is s stringB"+i);
                _ConceptionEntity3.attachFromRelation(_ConceptionEntity4.getConceptionEntityUID(),"testRelationTypeB",relationValue,true);
            }

            QueryParameters queryParameters2 = new QueryParameters();

            FilteringItem defaultFilteringItem2 = new EqualFilteringItem("prop1",10000l);
            queryParameters2.setDefaultFilteringItem(defaultFilteringItem2);
            List<RelationEntity> relationEntityList3 = _ConceptionEntity3.getSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY);
            Assert.assertEquals(relationEntityList3.size(),2);
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY),new Long("2"));
            Assert.assertEquals(_ConceptionEntity4.countSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY),new Long("2"));

            FilteringItem defaultFilteringItem3 = new GreaterThanFilteringItem("prop3",50019);
            queryParameters2.setDefaultFilteringItem(defaultFilteringItem3);
            relationEntityList3 = _ConceptionEntity3.getSpecifiedRelations(queryParameters2,RelationDirection.FROM);
            Assert.assertEquals(relationEntityList3.size(),80);
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.FROM),new Long("80"));
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TO),new Long("0"));
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY),new Long("80"));

            FilteringItem andFilteringItem1 = new LessThanFilteringItem("prop2",1037);
            queryParameters2.addFilteringItem(andFilteringItem1, QueryParameters.FilteringLogic.AND);
            relationEntityList3 = _ConceptionEntity3.getSpecifiedRelations(queryParameters2,RelationDirection.FROM);
            Assert.assertEquals(relationEntityList3.size(),67);
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.FROM),new Long("67"));
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TO),new Long("0"));
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY),new Long("67"));

        }finally {
            graphOperationExecutor.close();
        }

        //use batch operation mode way 2
        CoreRealm coreRealm3 = RealmTermFactory.getDefaultCoreRealm();
        coreRealm3.openGlobalSession();

            _ConceptionKind01 = coreRealm3.getConceptionKind(testConceptionKindName);
            Map<String,Object> newEntityValue3= new HashMap<>();
            newEntityValue3.put("prop1ForRelTest","ConceptionEntity3");
            ConceptionEntityValue conceptionEntityValue3 = new ConceptionEntityValue(newEntityValue3);
            ConceptionEntity _ConceptionEntity3 = _ConceptionKind01.newEntity(conceptionEntityValue3,false);

            Map<String,Object> newEntityValue4= new HashMap<>();
            newEntityValue4.put("prop1ForRelTest","ConceptionEntity4");
            ConceptionEntityValue conceptionEntityValue4 = new ConceptionEntityValue(newEntityValue4);
            ConceptionEntity _ConceptionEntity4 = _ConceptionKind01.newEntity(conceptionEntityValue4,false);

            for(int i = 0; i<100;i++){
                Map<String,Object> relationValue= new HashMap<>();
                relationValue.put("prop1",10000l+i);
                relationValue.put("prop2",190.22d+i);
                relationValue.put("prop3",50+i);
                relationValue.put("prop4","thi is s stringA"+i);
                _ConceptionEntity3.attachFromRelation(_ConceptionEntity4.getConceptionEntityUID(),"testRelationTypeA",relationValue,true);
            }

            for(int i = 0; i<100;i++){
                Map<String,Object> relationValue= new HashMap<>();
                relationValue.put("prop2",1000.22d+i);
                relationValue.put("prop3",600+i);
                relationValue.put("prop4","thi is s stringB"+i);
                _ConceptionEntity3.attachToRelation(_ConceptionEntity4.getConceptionEntityUID(),"testRelationTypeA",relationValue,true);
            }

            for(int i = 0; i<50;i++){
                Map<String,Object> relationValue= new HashMap<>();
                relationValue.put("prop1",10000l+i);
                relationValue.put("prop2",190.22d+i);
                relationValue.put("prop3",100000+i);
                relationValue.put("prop4","thi is s stringA"+i);
                _ConceptionEntity3.attachFromRelation(_ConceptionEntity4.getConceptionEntityUID(),"testRelationTypeB",relationValue,true);
            }

            for(int i = 0; i<50;i++){
                Map<String,Object> relationValue= new HashMap<>();
                relationValue.put("prop2",1000.22d+i);
                relationValue.put("prop3",50000+i);
                relationValue.put("prop4","thi is s stringB"+i);
                _ConceptionEntity3.attachFromRelation(_ConceptionEntity4.getConceptionEntityUID(),"testRelationTypeB",relationValue,true);
            }

            QueryParameters queryParameters2 = new QueryParameters();

            FilteringItem defaultFilteringItem2 = new EqualFilteringItem("prop1",10000l);
            queryParameters2.setDefaultFilteringItem(defaultFilteringItem2);
            List<RelationEntity> relationEntityList3 = _ConceptionEntity3.getSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY);
            Assert.assertEquals(relationEntityList3.size(),2);
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY),new Long("2"));
            Assert.assertEquals(_ConceptionEntity4.countSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY),new Long("2"));

            FilteringItem defaultFilteringItem3 = new GreaterThanFilteringItem("prop3",50019);
            queryParameters2.setDefaultFilteringItem(defaultFilteringItem3);
            relationEntityList3 = _ConceptionEntity3.getSpecifiedRelations(queryParameters2,RelationDirection.FROM);
            Assert.assertEquals(relationEntityList3.size(),80);
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.FROM),new Long("80"));
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TO),new Long("0"));
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY),new Long("80"));

            FilteringItem andFilteringItem1 = new LessThanFilteringItem("prop2",1037);
            queryParameters2.addFilteringItem(andFilteringItem1, QueryParameters.FilteringLogic.AND);
            relationEntityList3 = _ConceptionEntity3.getSpecifiedRelations(queryParameters2,RelationDirection.FROM);
            Assert.assertEquals(relationEntityList3.size(),67);
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.FROM),new Long("67"));
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TO),new Long("0"));
            Assert.assertEquals(_ConceptionEntity3.countSpecifiedRelations(queryParameters2,RelationDirection.TWO_WAY),new Long("67"));

        coreRealm3.closeGlobalSession();

        ConceptionKind _ConceptionKind0A = coreRealm.getConceptionKind(testConceptionKindName+"A");
        if(_ConceptionKind0A != null){
            coreRealm.removeConceptionKind(testConceptionKindName+"A",true);
        }
        _ConceptionKind0A = coreRealm.getConceptionKind(testConceptionKindName+"A");
        if(_ConceptionKind0A == null){
            _ConceptionKind0A = coreRealm.createConceptionKind(testConceptionKindName+"A","TestConceptionKindADesc+中文描述");
        }

        ConceptionKind _ConceptionKind0B = coreRealm.getConceptionKind(testConceptionKindName+"B");
        if(_ConceptionKind0B != null){
            coreRealm.removeConceptionKind(testConceptionKindName+"B",true);
        }
        _ConceptionKind0B = coreRealm.getConceptionKind(testConceptionKindName+"B");
        if(_ConceptionKind0B == null){
            _ConceptionKind0B = coreRealm.createConceptionKind(testConceptionKindName+"B","TestConceptionKindADesc+中文描述");
        }

        ConceptionKind _ConceptionKind0C = coreRealm.getConceptionKind(testConceptionKindName+"C");
        if(_ConceptionKind0C != null){
            coreRealm.removeConceptionKind(testConceptionKindName+"C",true);
        }
        _ConceptionKind0C = coreRealm.getConceptionKind(testConceptionKindName+"C");
        if(_ConceptionKind0C == null){
            _ConceptionKind0C = coreRealm.createConceptionKind(testConceptionKindName+"C","TestConceptionKindADesc+中文描述");
        }

        Map<String,Object> newEntityValueCommon= new HashMap<>();
        newEntityValueCommon.put("prop1",10000);
        newEntityValueCommon.put("prop2",20000);
        newEntityValueCommon.put("prop3",30000);
        ConceptionEntityValue conceptionEntityValueCommon1 = new ConceptionEntityValue(newEntityValueCommon);
        ConceptionEntity _ConceptionEntityA = _ConceptionKind0A.newEntity(conceptionEntityValueCommon1,false);

        Map<String,Object> newEntityValueCommon2= new HashMap<>();
        ConceptionEntityValue conceptionEntityValueCommon2 = new ConceptionEntityValue(newEntityValueCommon2);
        ConceptionEntity _ConceptionEntityB1 = _ConceptionKind0B.newEntity(conceptionEntityValueCommon2,false);
        _ConceptionEntityB1.addAttribute("kindName","ConceptionKind0B");
        ConceptionEntity _ConceptionEntityB2 = _ConceptionKind0B.newEntity(conceptionEntityValueCommon2,false);
        _ConceptionEntityB2.addAttribute("kindName","ConceptionKind0B");

        Map<String,Object> newEntityValueCommon3= new HashMap<>();
        ConceptionEntityValue conceptionEntityValueCommon3 = new ConceptionEntityValue(newEntityValueCommon3);
        ConceptionEntity _ConceptionEntityC1 = _ConceptionKind0C.newEntity(conceptionEntityValueCommon3,false);
        _ConceptionEntityC1.addAttribute("kindName","ConceptionKind0C");
        ConceptionEntity _ConceptionEntityC2 = _ConceptionKind0C.newEntity(conceptionEntityValueCommon3,false);
        _ConceptionEntityC2.addAttribute("kindName","ConceptionKind0C");
        ConceptionEntity _ConceptionEntityC3 = _ConceptionKind0C.newEntity(conceptionEntityValueCommon3,false);
        _ConceptionEntityC3.addAttribute("kindName","ConceptionKind0C");

        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB2.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC3.getConceptionEntityUID(),"testRelationTypeType2",null,true);
        _ConceptionEntityB1.attachFromRelation(_ConceptionEntityC2.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityB2.attachFromRelation(_ConceptionEntityB1.getConceptionEntityUID(),"testRelationTypeType1",null,true);

        List<ConceptionEntity> resultListConceptionEntityList = _ConceptionEntityA.getRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2);

        Assert.assertEquals(resultListConceptionEntityList.size(),4);
        for(ConceptionEntity currentConceptionEntity:resultListConceptionEntityList){
            Assert.assertNotNull(currentConceptionEntity.getConceptionEntityUID());
            Assert.assertNotNull(currentConceptionEntity.getConceptionKindName());
            if(currentConceptionEntity.getConceptionKindName().equals("TestConceptionKindForRelationableTestB")){
                Assert.assertTrue(
                currentConceptionEntity.getConceptionEntityUID().equals(_ConceptionEntityB1.getConceptionEntityUID()) |
                        currentConceptionEntity.getConceptionEntityUID().equals(_ConceptionEntityB2.getConceptionEntityUID())
                );
            }
            if(currentConceptionEntity.getConceptionKindName().equals("TestConceptionKindForRelationableTestC")){
                Assert.assertTrue(
                        currentConceptionEntity.getConceptionEntityUID().equals(_ConceptionEntityC1.getConceptionEntityUID()) |
                                currentConceptionEntity.getConceptionEntityUID().equals(_ConceptionEntityC2.getConceptionEntityUID())
                );
            }
        }

        Long countRelatedNodesNumber = _ConceptionEntityA.countRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2);
        Assert.assertEquals(countRelatedNodesNumber,new Long("4"));

        List<String> attributesList = new ArrayList<>();
        attributesList.add("kindName");
        attributesList.add("dataOrigin");
        attributesList.add("createDate");
        ConceptionEntitiesAttributesRetrieveResult resultConceptionEntitiesAttributesRetrieveResult = _ConceptionEntityA.getAttributesOfRelatedConceptionEntities(null,attributesList,"testRelationTypeType1",RelationDirection.TWO_WAY,2);
        List<ConceptionEntityValue> conceptionEntityValueList = resultConceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues();
        for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
            Assert.assertNotNull(currentConceptionEntityValue.getConceptionEntityUID());
            Assert.assertEquals(currentConceptionEntityValue.getEntityAttributesValue().size(),3);
        }
        Assert.assertEquals(resultConceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues().size(),4);

        AttributesParameters conceptionAttributesParameters =new AttributesParameters();
        conceptionAttributesParameters.setDefaultFilteringItem(new EqualFilteringItem("kindName","ConceptionKind0C"));
        Long countRelatedConceptionEntitiesRes = _ConceptionEntityA.countRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2,null,conceptionAttributesParameters,true);
        Assert.assertEquals(countRelatedConceptionEntitiesRes,new Long("2"));

        AttributesParameters relationAttributesParameters =new AttributesParameters();
        relationAttributesParameters.setDefaultFilteringItem(new EqualFilteringItem("dataOrigin","dataOrigin001"+"bad"));
        countRelatedConceptionEntitiesRes = _ConceptionEntityA.countRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2,relationAttributesParameters,conceptionAttributesParameters,true);
        Assert.assertEquals(countRelatedConceptionEntitiesRes,new Long("0"));
        relationAttributesParameters.setDefaultFilteringItem(new EqualFilteringItem("dataOrigin","dataOrigin001"));
        countRelatedConceptionEntitiesRes = _ConceptionEntityA.countRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2,relationAttributesParameters,conceptionAttributesParameters,true);
        Assert.assertEquals(countRelatedConceptionEntitiesRes,new Long("2"));

        ResultEntitiesParameters resultEntitiesParameters= new ResultEntitiesParameters();
        resultEntitiesParameters.setDistinctMode(true);
        resultEntitiesParameters.addSortingAttribute("kindName", QueryParameters.SortingLogic.DESC);
        resultEntitiesParameters.setResultNumber(10000);
        List<ConceptionEntity> resultListConceptionEntityList2 = _ConceptionEntityA.getRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2,null,conceptionAttributesParameters,resultEntitiesParameters);

        Assert.assertEquals(resultListConceptionEntityList2.size(),2);
        for(ConceptionEntity currentConceptionEntity:resultListConceptionEntityList){
            Assert.assertNotNull(currentConceptionEntity.getConceptionEntityUID());
            Assert.assertNotNull(currentConceptionEntity.getConceptionKindName());
            if(currentConceptionEntity.getConceptionKindName().equals("TestConceptionKindForRelationableTestC")) {
                Assert.assertTrue(
                        currentConceptionEntity.getConceptionEntityUID().equals(_ConceptionEntityC1.getConceptionEntityUID()) |
                                currentConceptionEntity.getConceptionEntityUID().equals(_ConceptionEntityC2.getConceptionEntityUID())
                );
            }
        }

        resultListConceptionEntityList2 = _ConceptionEntityA.getRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2,relationAttributesParameters,conceptionAttributesParameters,resultEntitiesParameters);
        Assert.assertEquals(resultListConceptionEntityList2.size(),2);

        List<String> attributesList2 = new ArrayList<>();
        attributesList2.add("dataOrigin");
        attributesList2.add("createDate");
        ConceptionEntitiesAttributesRetrieveResult resultConceptionEntitiesAttributesRetrieveResult2 = _ConceptionEntityA.getAttributesOfRelatedConceptionEntities(null,attributesList2,"testRelationTypeType1",RelationDirection.TWO_WAY,2,relationAttributesParameters,conceptionAttributesParameters,resultEntitiesParameters);
        List<ConceptionEntityValue> conceptionEntityValueList2 = resultConceptionEntitiesAttributesRetrieveResult2.getConceptionEntityValues();
        for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList2){
            Assert.assertNotNull(currentConceptionEntityValue.getConceptionEntityUID());
            Assert.assertEquals(currentConceptionEntityValue.getEntityAttributesValue().size(),2);
        }
        Assert.assertEquals(resultConceptionEntitiesAttributesRetrieveResult2.getConceptionEntityValues().size(),2);

        relationAttributesParameters.setDefaultFilteringItem(new EqualFilteringItem("dataOrigin","dataOrigin001"+"bad"));
        resultListConceptionEntityList2 = _ConceptionEntityA.getRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2,relationAttributesParameters,conceptionAttributesParameters,resultEntitiesParameters);
        Assert.assertEquals(resultListConceptionEntityList2.size(),0);

        resultEntitiesParameters.setStartPage(2);
        resultEntitiesParameters.setEndPage(4);
        resultEntitiesParameters.setPageSize(10);
        resultListConceptionEntityList2 = _ConceptionEntityA.getRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2,null,conceptionAttributesParameters,resultEntitiesParameters);
        Assert.assertEquals(resultListConceptionEntityList2.size(),0);

        RelationEntity resultRelationEntity = _ConceptionEntityA.attachFromRelation(_ConceptionEntityB2.getConceptionEntityUID(),"detachRelTestRelation",null,false);
        RelationEntity resultRelationEntity2 = _ConceptionEntityB1.attachFromRelation(_ConceptionEntityB2.getConceptionEntityUID(),"detachRelTestRelation",null,false);
        boolean detachResult = _ConceptionEntityA.detachRelation(resultRelationEntity.getRelationEntityUID());
        Assert.assertTrue(detachResult);

        detachResult = _ConceptionEntityA.detachRelation(resultRelationEntity2.getRelationEntityUID());
        Assert.assertFalse(detachResult);
        boolean exceptionShouldBeCaught = false;
        try{
            _ConceptionEntityA.detachRelation(resultRelationEntity.getRelationEntityUID());
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        List<String> detachAllRelationsResult = _ConceptionEntityA.detachAllRelations();
        Assert.assertNotNull(detachAllRelationsResult);
        Assert.assertEquals(detachAllRelationsResult.size(),4);
        Assert.assertEquals(_ConceptionEntityA.getAllRelations().size(),0);

        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB2.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC3.getConceptionEntityUID(),"testRelationTypeType2",null,true);

        detachAllRelationsResult = _ConceptionEntityA.detachAllSpecifiedRelations("testRelationTypeType1",RelationDirection.TO);
        Assert.assertNotNull(detachAllRelationsResult);
        Assert.assertEquals(detachAllRelationsResult.size(),0);

        detachAllRelationsResult = _ConceptionEntityA.detachAllSpecifiedRelations("testRelationTypeType1",RelationDirection.FROM);
        Assert.assertNotNull(detachAllRelationsResult);
        Assert.assertEquals(detachAllRelationsResult.size(),3);

        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB2.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC3.getConceptionEntityUID(),"testRelationTypeType2",null,true);

        detachAllRelationsResult = _ConceptionEntityA.detachAllSpecifiedRelations("testRelationTypeType1",RelationDirection.TWO_WAY);
        Assert.assertNotNull(detachAllRelationsResult);
        Assert.assertEquals(detachAllRelationsResult.size(),3);

        detachAllRelationsResult = _ConceptionEntityC3.detachAllSpecifiedRelations("testRelationTypeType2",RelationDirection.TO);
        Assert.assertNotNull(detachAllRelationsResult);
        Assert.assertEquals(detachAllRelationsResult.size(),2);

        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB2.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC3.getConceptionEntityUID(),"testRelationTypeType2",null,true);

        QueryParameters exploreParameters = new QueryParameters();
        exploreParameters.setEntityKind("testRelationTypeType1");
        exploreParameters.setResultNumber(2);
        detachAllRelationsResult = _ConceptionEntityA.detachSpecifiedRelations(exploreParameters,RelationDirection.TWO_WAY);
        Assert.assertNotNull(detachAllRelationsResult);
        Assert.assertEquals(detachAllRelationsResult.size(),2);
    }
}
