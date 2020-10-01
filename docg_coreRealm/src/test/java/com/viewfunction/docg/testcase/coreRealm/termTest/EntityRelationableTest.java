package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
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
        countSpecifiedRelations = _ConceptionEntity1.countSpecifiedRelations(queryParameters1,RelationDirection.TWO_WAY);
        Assert.assertEquals(countSpecifiedRelations,new Long(2));


        //queryParameters1.setResultNumber(10000000); //?? not work??
        //queryParameters1.setEntityKind("NOTEXIST");
        //queryParameters1.setStartPage(1);
        //queryParameters1.setEndPage(10);

    }
}
