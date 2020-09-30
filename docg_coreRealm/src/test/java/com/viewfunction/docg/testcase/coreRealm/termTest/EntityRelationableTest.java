package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
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

        _ConceptionEntity1.attachFromRelation(_ConceptionEntity2.getConceptionEntityUID(),"testRelationType01",newRelationValue,true);
        _ConceptionEntity1.attachToRelation(_ConceptionEntity2.getConceptionEntityUID(),"testRelationType02",newRelationValue,true);

        //Assert.assertEquals(_ConceptionEntity1.countRelations(),2l);

        _ConceptionEntity1.getAllRelations();

        System.out.println(_ConceptionEntity1.countRelations());








    }
}
