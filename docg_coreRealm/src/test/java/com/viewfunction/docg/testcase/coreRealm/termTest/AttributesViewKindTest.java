package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

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

        AttributeKind attributeKind02 = coreRealm.createAttributeKind("attributeKind02","attributeKind02Desc", AttributeDataType.DATE);
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
        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeDataType(),AttributeDataType.DATE);
        Assert.assertEquals(containsAttributeKinds.get(0).getAttributeKindUID(),attributeKind02.getAttributeKindUID());

        AttributeKind attributeKind03 = coreRealm.createAttributeKind("attributeKind03","attributeKind03Desc", AttributeDataType.DATE);
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
    }
}
