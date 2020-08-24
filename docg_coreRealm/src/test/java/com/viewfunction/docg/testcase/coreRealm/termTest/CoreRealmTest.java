package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CoreRealmTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for CoreRealmTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testCoreRealmFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind("kind01");
        if(_ConceptionKind01 != null){
            boolean removeResult = coreRealm.removeConceptionKind("kind01",true);
            Assert.assertTrue(removeResult);
            _ConceptionKind01 = coreRealm.getConceptionKind("kind01");
        }
        Assert.assertNull(_ConceptionKind01);
        _ConceptionKind01 = coreRealm.createConceptionKind("kind01","kind01Desc+中文描述");
        Assert.assertNotNull(_ConceptionKind01);
        Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),"kind01");
        Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"kind01Desc+中文描述");
        Assert.assertNotNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getConceptionKindUID());
        Assert.assertNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getCoreRealmName());

        _ConceptionKind01 = coreRealm.createConceptionKind("kind01","kind01Desc+中文描述");
        Assert.assertNull(_ConceptionKind01);

        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind("kind02");
        if(_ConceptionKind02 != null){
            coreRealm.removeConceptionKind("kind02",true);
        }
        _ConceptionKind02 = coreRealm.createConceptionKind("kind02","kind02Desc+中文描述");
        Assert.assertNotNull(_ConceptionKind02);

        _ConceptionKind01 = coreRealm.getConceptionKind("kind01");
        Assert.assertNotNull(_ConceptionKind01);
        Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),"kind01");
        Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"kind01Desc+中文描述");
        Assert.assertNotNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getConceptionKindUID());
        Assert.assertNull(((Neo4JConceptionKindImpl)_ConceptionKind01).getCoreRealmName());

        AttributesViewKind attributesViewKind01 = coreRealm.createAttributesViewKind("attributesViewKind01","attributesViewKind01Desc",null);
        Assert.assertNotNull(attributesViewKind01);
        Assert.assertNotNull(attributesViewKind01.getAttributesViewKindUID());
        Assert.assertEquals(attributesViewKind01.getAttributesViewKindName(),"attributesViewKind01");
        Assert.assertEquals(attributesViewKind01.getAttributesViewKindDesc(),"attributesViewKind01Desc");
        Assert.assertEquals(attributesViewKind01.getAttributesViewKindDataForm(),AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE);
        Assert.assertFalse(attributesViewKind01.isCollectionAttributesViewKind());

        String targetAttributesViewKindUID = attributesViewKind01.getAttributesViewKindUID();

        attributesViewKind01 = coreRealm.createAttributesViewKind(null,"attributesViewKind01Desc",null);
        Assert.assertNull(attributesViewKind01);

        attributesViewKind01 = coreRealm.createAttributesViewKind("attributesViewKind02",null,AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE);
        Assert.assertNotNull(attributesViewKind01);
        Assert.assertNotNull(attributesViewKind01.getAttributesViewKindUID());
        Assert.assertEquals(attributesViewKind01.getAttributesViewKindName(),"attributesViewKind02");
        Assert.assertEquals(attributesViewKind01.getAttributesViewKindDataForm(),AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE);
        Assert.assertTrue(attributesViewKind01.isCollectionAttributesViewKind());

        AttributesViewKind attributesViewKind02 = coreRealm.getAttributesViewKind(targetAttributesViewKindUID);
        Assert.assertNotNull(attributesViewKind02);
        Assert.assertNotNull(attributesViewKind02.getAttributesViewKindUID());
        Assert.assertEquals(attributesViewKind02.getAttributesViewKindName(),"attributesViewKind01");
        Assert.assertEquals(attributesViewKind02.getAttributesViewKindDesc(),"attributesViewKind01Desc");
        Assert.assertEquals(attributesViewKind02.getAttributesViewKindDataForm(),AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE);
        Assert.assertFalse(attributesViewKind02.isCollectionAttributesViewKind());

        attributesViewKind02 = coreRealm.getAttributesViewKind("123456");
        Assert.assertNull(attributesViewKind02);

        boolean removeAttributesViewKindRes = coreRealm.removeAttributesViewKind(targetAttributesViewKindUID);
        Assert.assertTrue(removeAttributesViewKindRes);

        attributesViewKind02 = coreRealm.getAttributesViewKind(targetAttributesViewKindUID);
        Assert.assertNull(attributesViewKind02);

        removeAttributesViewKindRes = coreRealm.removeAttributesViewKind(null);
        Assert.assertFalse(removeAttributesViewKindRes);

        boolean exceptionShouldBeCaught = false;
        try{
            coreRealm.removeAttributesViewKind("123456");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        AttributeKind attributeKind01 = coreRealm.createAttributeKind("attributeKind01","attributeKind01Desc", AttributeDataType.BOOLEAN);
        Assert.assertNotNull(attributeKind01);
        Assert.assertNotNull(attributeKind01.getAttributeKindUID());
        Assert.assertEquals(attributeKind01.getAttributeKindName(),"attributeKind01");
        Assert.assertEquals(attributeKind01.getAttributeKindDesc(),"attributeKind01Desc");
        Assert.assertEquals(attributeKind01.getAttributeDataType(),AttributeDataType.BOOLEAN);

        String targetAttributeKindUID = attributeKind01.getAttributeKindUID();
        AttributeKind attributeKind02 = coreRealm.getAttributeKind(targetAttributeKindUID);
        Assert.assertNotNull(attributeKind02);
        Assert.assertNotNull(attributeKind02.getAttributeKindUID());
        Assert.assertEquals(attributeKind02.getAttributeKindName(),"attributeKind01");
        Assert.assertEquals(attributeKind02.getAttributeKindDesc(),"attributeKind01Desc");
        Assert.assertEquals(attributeKind02.getAttributeDataType(),AttributeDataType.BOOLEAN);

        attributeKind02 = coreRealm.getAttributeKind(null);
        Assert.assertNull(attributeKind02);
        attributeKind02 = coreRealm.getAttributeKind("123456");
        Assert.assertNull(attributeKind02);

        boolean removeAttributeKindRes01 = coreRealm.removeAttributeKind(null);
        Assert.assertFalse(removeAttributeKindRes01);
        removeAttributeKindRes01 = coreRealm.removeAttributeKind(targetAttributeKindUID);
        Assert.assertTrue(removeAttributeKindRes01);
        attributeKind02 = coreRealm.getAttributeKind(targetAttributeKindUID);
        Assert.assertNull(attributeKind02);
    }
}
