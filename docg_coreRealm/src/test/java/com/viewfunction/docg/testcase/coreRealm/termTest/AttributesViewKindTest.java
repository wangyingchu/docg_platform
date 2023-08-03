package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributesViewKindTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindB";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for AttributesViewKindTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testAttributesViewKindFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        AttributesViewKind targetAttributesViewKind = coreRealm.createAttributesViewKind("targetAttributesViewKindA","targetAttributesViewKindADesc",null);
        Assert.assertNotNull(targetAttributesViewKind);
        Assert.assertNotNull(targetAttributesViewKind.getAttributesViewKindUID());
        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindName(),"targetAttributesViewKindA");
        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindDesc(),"targetAttributesViewKindADesc");
        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindDataForm(),AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE);
        Assert.assertFalse(targetAttributesViewKind.isCollectionAttributesViewKind());

        List<AttributeKind> containsAttributeKinds = targetAttributesViewKind.getContainsAttributeKinds();
        Assert.assertNotNull(containsAttributeKinds);
        Assert.assertEquals(containsAttributeKinds.size(),0);

        AttributeKind attributeKind01 = coreRealm.createAttributeKind("attributeKind01","attributeKind01Desc", AttributeDataType.BOOLEAN);
        Assert.assertNotNull(attributeKind01);
        Assert.assertNotNull(attributeKind01.getAttributeKindUID());
        Assert.assertEquals(attributeKind01.getAttributeKindName(),"attributeKind01");
        Assert.assertEquals(attributeKind01.getAttributeKindDesc(),"attributeKind01Desc");
        Assert.assertEquals(attributeKind01.getAttributeDataType(),AttributeDataType.BOOLEAN);

        List<AttributesViewKind> attributesViewKindsList = attributeKind01.getContainerAttributesViewKinds();
        Assert.assertNotNull(attributesViewKindsList);
        Assert.assertEquals(attributesViewKindsList.size(),0);

        boolean addAttributeKineRes = targetAttributesViewKind.attachAttributeKind(attributeKind01.getAttributeKindUID());
        Assert.assertTrue(addAttributeKineRes);
        addAttributeKineRes = targetAttributesViewKind.attachAttributeKind(attributeKind01.getAttributeKindUID());
        Assert.assertTrue(addAttributeKineRes);

        attributesViewKindsList = attributeKind01.getContainerAttributesViewKinds();
        Assert.assertNotNull(attributesViewKindsList);
        Assert.assertEquals(attributesViewKindsList.size(),1);
        Assert.assertEquals(attributesViewKindsList.get(0).getAttributesViewKindName(),"targetAttributesViewKindA");

        containsAttributeKinds = targetAttributesViewKind.getContainsAttributeKinds();
        Assert.assertNotNull(containsAttributeKinds);
        Assert.assertEquals(containsAttributeKinds.size(),1);

        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeKindName(),"attributeKind01");
        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeKindDesc(),"attributeKind01Desc");
        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeDataType(),AttributeDataType.BOOLEAN);
        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeKindUID(),attributeKind01.getAttributeKindUID());

        boolean exceptionShouldBeCaught = false;
        try{
            targetAttributesViewKind.attachAttributeKind("123456");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        AttributeKind attributeKind02 = coreRealm.createAttributeKind("attributeKind02","attributeKind02Desc", AttributeDataType.TIMESTAMP);
        Assert.assertNotNull(attributeKind02);
        addAttributeKineRes = targetAttributesViewKind.attachAttributeKind(attributeKind02.getAttributeKindUID());
        Assert.assertTrue(addAttributeKineRes);

        containsAttributeKinds = targetAttributesViewKind.getContainsAttributeKinds();
        Assert.assertNotNull(containsAttributeKinds);
        Assert.assertEquals(containsAttributeKinds.size(),2);

        boolean removeAttributeTypeRes = coreRealm.removeAttributeKind(attributeKind01.getAttributeKindUID());
        Assert.assertTrue(removeAttributeTypeRes);

        exceptionShouldBeCaught = false;
        try{
            coreRealm.removeAttributeKind(attributeKind01.getAttributeKindUID());
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        containsAttributeKinds = targetAttributesViewKind.getContainsAttributeKinds();
        Assert.assertNotNull(containsAttributeKinds);
        Assert.assertEquals(containsAttributeKinds.size(),1);

        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeKindName(),"attributeKind02");
        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeKindDesc(),"attributeKind02Desc");
        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeDataType(),AttributeDataType.TIMESTAMP);
        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeKindUID(),attributeKind02.getAttributeKindUID());

        AttributeKind attributeKind03 = coreRealm.createAttributeKind("attributeKind03","attributeKind03Desc", AttributeDataType.TIMESTAMP);
        Assert.assertNotNull(attributeKind03);
        removeAttributeTypeRes = targetAttributesViewKind.detachAttributeKind(attributeKind03.getAttributeKindUID());
        Assert.assertFalse(removeAttributeTypeRes);

        List<ConceptionKind> containerConceptionKindList = targetAttributesViewKind.getContainerConceptionKinds();
        Assert.assertNotNull(containerConceptionKindList);
        Assert.assertEquals(containerConceptionKindList.size(),0);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"TestConceptionKindBDesc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),testConceptionKindName);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"TestConceptionKindBDesc+中文描述");
        }

        boolean attachAttributesViewKindRes = _ConceptionKind01.attachAttributesViewKind(targetAttributesViewKind.getAttributesViewKindUID());
        Assert.assertTrue(attachAttributesViewKindRes);

        containerConceptionKindList = targetAttributesViewKind.getContainerConceptionKinds();
        Assert.assertNotNull(containerConceptionKindList);
        Assert.assertEquals(containerConceptionKindList.size(),1);
        Assert.assertEquals(containerConceptionKindList.get(0).getConceptionKindName(),testConceptionKindName);
        Assert.assertEquals(containerConceptionKindList.get(0).getConceptionKindDesc(),"TestConceptionKindBDesc+中文描述");

        boolean detachAttributesViewKindRes = _ConceptionKind01.detachAttributesViewKind(targetAttributesViewKind.getAttributesViewKindUID());
        Assert.assertTrue(detachAttributesViewKindRes);

        containerConceptionKindList = targetAttributesViewKind.getContainerConceptionKinds();
        Assert.assertNotNull(containerConceptionKindList);
        Assert.assertEquals(containerConceptionKindList.size(),0);

        targetAttributesViewKind.detachAttributeKind(attributeKind02.getAttributeKindUID());
        targetAttributesViewKind.detachAttributeKind(attributeKind03.getAttributeKindUID());

        Map<String,Object> attachMetaInfoMap = new HashMap<>();
        attachMetaInfoMap.put("attr01",1);
        attachMetaInfoMap.put("attr02",new Date());
        attachMetaInfoMap.put("attr03","this is string");
        boolean attachResult = targetAttributesViewKind.attachAttributeKind(attributeKind02.getAttributeKindUID(),attachMetaInfoMap);
        Assert.assertTrue(attachResult);

        Assert.assertEquals(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind02.getAttributeKindUID(),"attr01"),1l);
        Assert.assertEquals(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind02.getAttributeKindUID(),"attr03"),"this is string");
        Assert.assertNotNull(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind02.getAttributeKindUID(),"attr02"));

        attachMetaInfoMap.put("attr01",2);
        attachMetaInfoMap.put("attr02",new Date());
        attachMetaInfoMap.put("attr03","this is string2");
        attachMetaInfoMap.put("attr04",true);
        attachResult = targetAttributesViewKind.attachAttributeKind(attributeKind03.getAttributeKindUID(),attachMetaInfoMap);
        Assert.assertTrue(attachResult);

        Assert.assertNotNull(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),"attr04"));
        Assert.assertTrue(targetAttributesViewKind.removeAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),"attr04"));
        Assert.assertNull(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),"attr04"));

        exceptionShouldBeCaught = false;
        try{
            targetAttributesViewKind.removeAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),"attr04");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        Assert.assertNull(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),"attr05"));
        Assert.assertNull(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),"attr06"));

        Map<String,Object> attachMetaInfoMap2 = new HashMap<>();
        attachMetaInfoMap2.put("attr05",1000);
        attachMetaInfoMap2.put("attr06","attr06value");

        List<String> addResult = targetAttributesViewKind.setAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),attachMetaInfoMap2);
        Assert.assertNotNull(addResult);
        Assert.assertEquals(addResult.size(),2);
        Assert.assertTrue(addResult.contains("attr05"));
        Assert.assertTrue(addResult.contains("attr06"));
        Assert.assertEquals(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),"attr05"),1000l);
        Assert.assertEquals(targetAttributesViewKind.getAttributeKindAttachMetaInfo(attributeKind03.getAttributeKindUID(),"attr06"),"attr06value");

        Map<String,Object> metaInfoMap = targetAttributesViewKind.getAttributeKindsAttachMetaInfo("attr01");
        Assert.assertNotNull(metaInfoMap);
        Assert.assertEquals(metaInfoMap.size(),2);
        Assert.assertTrue(metaInfoMap.containsKey(attributeKind02.getAttributeKindUID()));
        Assert.assertTrue(metaInfoMap.containsKey(attributeKind03.getAttributeKindUID()));
        Assert.assertEquals(metaInfoMap.get(attributeKind02.getAttributeKindUID()),1l);
        Assert.assertEquals(metaInfoMap.get(attributeKind03.getAttributeKindUID()),2l);

        Map<String,Object> metaInfoMap2 = targetAttributesViewKind.getAttributeKindAllAttachMetaInfo(attributeKind02.getAttributeKindUID());
        Assert.assertNotNull(metaInfoMap2);
        Assert.assertEquals(metaInfoMap2.size(),3);
        Assert.assertNotNull(metaInfoMap2.get("attr03"));
        Assert.assertNotNull(metaInfoMap2.get("attr02"));
        Assert.assertNotNull(metaInfoMap2.get("attr01"));

        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindDesc(),"targetAttributesViewKindADesc");
        boolean updateDescResult = targetAttributesViewKind.updateAttributesViewKindDesc("targetAttributesViewKindADescUPD");
        Assert.assertTrue(updateDescResult);
        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindDesc(),"targetAttributesViewKindADescUPD");
        Assert.assertEquals(coreRealm.getAttributesViewKind(targetAttributesViewKind.getAttributesViewKindUID()).getAttributesViewKindDesc(),"targetAttributesViewKindADescUPD");
    }
}
