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

import java.math.BigDecimal;
import java.util.*;

public class AttributesMeasurableTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKind01";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for AttributesMeasurableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testAttributesMeasurableFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"testKind01Desc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),testConceptionKindName);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"testKind01Desc+中文描述");
        }

        //Long entitiesCount = _ConceptionKind01.countConceptionEntities();

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",Long.parseLong("12345"));
        newEntityValue.put("prop2",Double.parseDouble("12345.789"));
        newEntityValue.put("prop3",Integer.parseInt("1234"));
        newEntityValue.put("prop4","thi is s string");
        newEntityValue.put("prop5",Boolean.valueOf("true"));
        newEntityValue.put("prop6", new BigDecimal("5566778890.223344"));
        newEntityValue.put("prop7", Short.valueOf("24"));
        newEntityValue.put("prop8", Float.valueOf("1234.66"));
        newEntityValue.put("prop9", new Long[]{1000l,2000l,3000l});
        newEntityValue.put("prop10", new Double[]{1000.1d,2000.2d,3000.3d});
        newEntityValue.put("prop11", new Integer[]{100,200,300});
        newEntityValue.put("prop12", new String[]{"this is str1","这是字符串2"});
        newEntityValue.put("prop13", new Boolean[]{true,true,false,false,true});
        newEntityValue.put("prop14", new BigDecimal[]{new BigDecimal("1234567.890"),new BigDecimal("987654321.12345")});
        newEntityValue.put("prop15", new Short[]{1,2,3,4,5});
        newEntityValue.put("prop16", new Float[]{1000.1f,2000.2f,3000.3f});
        newEntityValue.put("prop17", new Date());
        newEntityValue.put("prop18", new Date[]{new Date(),new Date(),new Date(),new Date()});
        newEntityValue.put("prop19", Byte.valueOf("2"));
        newEntityValue.put("prop20", "this is a byte array value".getBytes());

        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
/*
        ConceptionEntity _ConceptionEntity = _ConceptionKind01.newEntity(conceptionEntityValue,false);
        Assert.assertNotNull(_ConceptionEntity);
        Assert.assertEquals(_ConceptionEntity.getConceptionKindName(),testConceptionKindName);
        Assert.assertEquals(_ConceptionEntity.getAllConceptionKindNames().size(),1);
        Assert.assertEquals(_ConceptionEntity.getAllConceptionKindNames().get(0),testConceptionKindName);
        Assert.assertNotNull(_ConceptionEntity.getConceptionEntityUID());
*/
        String queryUIDValue = "4";
                //_ConceptionEntity.getConceptionEntityUID();
        ConceptionEntity _queryResultConceptionEntity = _ConceptionKind01.getEntityByUID(queryUIDValue);

        List<String> attributeNameList = _queryResultConceptionEntity.getAttributeNames();
        List<AttributeValue> attributeValueList = _queryResultConceptionEntity.getAttributes();

    }
}
