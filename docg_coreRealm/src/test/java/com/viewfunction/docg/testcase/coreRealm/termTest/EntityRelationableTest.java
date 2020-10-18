package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.GreaterThanFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.LessThanFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

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
    public void testClassificationFunction() throws CoreRealmServiceRuntimeException {
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
        //Assert.assertEquals(countSpecifiedRelations,new Long(2));

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
        newEntityValueCommon.put("prop1","fromEntity");
        ConceptionEntityValue conceptionEntityValueCommon1 = new ConceptionEntityValue(newEntityValueCommon);
        ConceptionEntity _ConceptionEntityA = _ConceptionKind0A.newEntity(conceptionEntityValueCommon1,false);

        ConceptionEntity _ConceptionEntityB1 = _ConceptionKind0B.newEntity(conceptionEntityValueCommon1,false);
        ConceptionEntity _ConceptionEntityB2 = _ConceptionKind0B.newEntity(conceptionEntityValueCommon1,false);

        ConceptionEntity _ConceptionEntityC1 = _ConceptionKind0C.newEntity(conceptionEntityValueCommon1,false);
        ConceptionEntity _ConceptionEntityC2 = _ConceptionKind0C.newEntity(conceptionEntityValueCommon1,false);
        ConceptionEntity _ConceptionEntityC3 = _ConceptionKind0C.newEntity(conceptionEntityValueCommon1,false);

        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityB2.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC1.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityA.attachFromRelation(_ConceptionEntityC3.getConceptionEntityUID(),"testRelationTypeType2",null,true);
        _ConceptionEntityB1.attachFromRelation(_ConceptionEntityC2.getConceptionEntityUID(),"testRelationTypeType1",null,true);
        _ConceptionEntityB2.attachFromRelation(_ConceptionEntityB1.getConceptionEntityUID(),"testRelationTypeType1",null,true);

        List<ConceptionEntity> resultListConceptionEntityList = _ConceptionEntityA.getRelatedConceptionEntities(null,"testRelationTypeType1",RelationDirection.TWO_WAY,2);
        System.out.println(resultListConceptionEntityList);

        Assert.assertEquals(resultListConceptionEntityList.size(),4);
        for(ConceptionEntity currentConceptionEntity:resultListConceptionEntityList){
            System.out.println(currentConceptionEntity.getConceptionKindName());
            System.out.println(currentConceptionEntity.getConceptionEntityUID());



        }

    }
}
