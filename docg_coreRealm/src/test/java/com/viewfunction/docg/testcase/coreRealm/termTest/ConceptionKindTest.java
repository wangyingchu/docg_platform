package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
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
import java.util.List;
import java.util.Map;

public class ConceptionKindTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ConceptionKindTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testCoreRealmFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        /*
        ConceptionKind _ConceptionKind01 = coreRealm.createConceptionKind("kind01","kind01Desc+中文描述");
        Assert.assertNotNull(_ConceptionKind01);
        Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),"kind01");
        Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"kind01Desc+中文描述");
        Assert.assertNotNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getConceptionKindUID());
        Assert.assertNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getCoreRealmName());
        */

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind("kind01");
        Long entitiesCount = _ConceptionKind01.countConceptionEntities();

     //   Assert.assertEquals(entitiesCount.longValue(),0);


        /*
        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);
        newEntityValue.put("prop2",190.22d);
        newEntityValue.put("prop3",50);
        newEntityValue.put("prop4","thi is s string");
        newEntityValue.put("prop5","我是中文string");

        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);


        ConceptionEntity _ConceptionEntity = _ConceptionKind01.newEntity(conceptionEntityValue,false);

        Assert.assertNotNull(_ConceptionEntity);
        Assert.assertEquals(_ConceptionEntity.getConceptionKindName(),"kind01");
        Assert.assertEquals(_ConceptionEntity.getAllConceptionKindNames().size(),1);
        Assert.assertEquals(_ConceptionEntity.getAllConceptionKindNames().get(0),"kind01");
        Assert.assertNotNull(_ConceptionEntity.getConceptionEntityUID());
        */

        String queryUIDValue = "1213";
        ConceptionEntity _queryResultConceptionEntity = _ConceptionKind01.getEntityByUID(queryUIDValue);

        Assert.assertNotNull(_queryResultConceptionEntity);
        Assert.assertEquals(_queryResultConceptionEntity.getConceptionKindName(),"kind01");
        Assert.assertEquals(_queryResultConceptionEntity.getAllConceptionKindNames().size(),1);
        Assert.assertEquals(_queryResultConceptionEntity.getAllConceptionKindNames().get(0),"kind01");
        Assert.assertEquals(_queryResultConceptionEntity.getConceptionEntityUID(),queryUIDValue);

        List<String> attributeNameList = _queryResultConceptionEntity.getAttributeNames();

        Assert.assertNotNull(attributeNameList);
        Assert.assertEquals(attributeNameList.size(),5);
        Assert.assertTrue(attributeNameList.contains("prop1"));
        Assert.assertTrue(attributeNameList.contains("prop2"));
        Assert.assertTrue(attributeNameList.contains("prop3"));
        Assert.assertTrue(attributeNameList.contains("prop4"));
        Assert.assertTrue(attributeNameList.contains("prop5"));

        List<AttributeValue> attributeValueList = _queryResultConceptionEntity.getAttributes();
        Assert.assertNotNull(attributeValueList);
        Assert.assertEquals(attributeValueList.size(),5);

    }
}
