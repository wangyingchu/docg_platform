package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
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

        RelationKind relationKind01 = coreRealm.createRelationKind("relationKind01","relationKind01Desc");
        Assert.assertNotNull(relationKind01);
        Assert.assertEquals(relationKind01.getRelationKindName(),"relationKind01");
        Assert.assertEquals(relationKind01.getRelationKindDesc(),"relationKind01Desc");
        relationKind01 = coreRealm.createRelationKind("relationKind01","relationKind01Desc");
        Assert.assertNull(relationKind01);

        RelationKind targetRelationKind01 = coreRealm.getRelationKind("relationKind01");
        Assert.assertNotNull(targetRelationKind01);
        Assert.assertEquals(targetRelationKind01.getRelationKindName(),"relationKind01");
        Assert.assertEquals(targetRelationKind01.getRelationKindDesc(),"relationKind01Desc");

        targetRelationKind01 = coreRealm.getRelationKind("relationKind01+NotExist");
        Assert.assertNull(targetRelationKind01);

        boolean removeRelationTypeRes = coreRealm.removeRelationKind("relationKind01",true);
        Assert.assertTrue(removeRelationTypeRes);

        exceptionShouldBeCaught = false;
        try{
            coreRealm.removeRelationKind("relationKind01",true);
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            coreRealm.createRelationKind("relationKind02","relationKind02Desc","parentRelationType");
        }catch(CoreRealmFunctionNotSupportedException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        String classificationName01 = "classification001";
        Classification _Classification01 = coreRealm.getClassification(classificationName01);

        Assert.assertFalse(coreRealm.removeClassification(null));
        if(_Classification01 != null){
            boolean removeClassificationResult = coreRealm.removeClassification(classificationName01);
            Assert.assertTrue(removeClassificationResult);
            exceptionShouldBeCaught = false;
            try {
                coreRealm.removeClassification(classificationName01);
            }catch (CoreRealmServiceRuntimeException e){
                exceptionShouldBeCaught = true;
            }
            Assert.assertTrue(exceptionShouldBeCaught);
        }

        _Classification01 = coreRealm.getClassification(classificationName01);
        Assert.assertNull(_Classification01);
        _Classification01 = coreRealm.createClassification(classificationName01,classificationName01+"Desc");
        Assert.assertNotNull(_Classification01);
        _Classification01 = coreRealm.getClassification(classificationName01);
        Assert.assertNotNull(_Classification01);

        String classificationName02 = "classification002";
        Classification _Classification02 = coreRealm.getClassification(classificationName02);
        if(_Classification02 != null){
            coreRealm.removeClassification(classificationName02);
        }

        _Classification02 = coreRealm.createClassification(classificationName02,classificationName02+"Desc",classificationName01);
        Assert.assertNotNull(_Classification02);

        _Classification02.addAttribute("attribute01","this is a string value");
        Assert.assertEquals(_Classification02.getAttribute("attribute01").getAttributeValue(),"this is a string value");

        String classificationName03 = "classification003";
        Classification _Classification03 = coreRealm.getClassification(classificationName03);
        if(_Classification03 != null){
            coreRealm.removeClassification(classificationName03);
        }

        String classificationName03_1 = "classification003_1";
        Classification _Classification03_1 = coreRealm.getClassification(classificationName03_1);
        if(_Classification03_1 != null){
            coreRealm.removeClassification(classificationName03_1);
        }

        String classificationName03_1_1 = "classification003_1_1";
        Classification _Classification03_1_1 = coreRealm.getClassification(classificationName03_1_1);
        if(_Classification03_1_1 != null){
            coreRealm.removeClassification(classificationName03_1_1);
        }

        coreRealm.createClassification(classificationName03,classificationName03+"Desc");
        coreRealm.createClassification(classificationName03_1,classificationName03_1+"Desc",classificationName03);
        coreRealm.createClassification(classificationName03_1_1,classificationName03_1_1+"Desc",classificationName03_1);

        Classification targetClassification = coreRealm.getClassification(classificationName03);
        Assert.assertNotNull(targetClassification);
        targetClassification = coreRealm.getClassification(classificationName03_1);
        Assert.assertNotNull(targetClassification);
        targetClassification = coreRealm.getClassification(classificationName03_1_1);
        Assert.assertNotNull(targetClassification);

        coreRealm.removeClassificationWithOffspring(classificationName03);

        targetClassification = coreRealm.getClassification(classificationName03);
        Assert.assertNull(targetClassification);
        targetClassification = coreRealm.getClassification(classificationName03_1);
        Assert.assertNull(targetClassification);
        targetClassification = coreRealm.getClassification(classificationName03_1_1);
        Assert.assertNull(targetClassification);
    }
}
