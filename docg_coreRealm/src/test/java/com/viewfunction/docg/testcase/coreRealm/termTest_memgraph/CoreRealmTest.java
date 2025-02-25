package com.viewfunction.docg.testcase.coreRealm.termTest_memgraph;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeFlowImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

public class CoreRealmTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for CoreRealmTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testCoreRealmFunction() throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.MEMGRAPH);
        Assert.assertEquals(coreRealm.getCoreRealmName(), PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME));

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
        Assert.assertEquals(((Neo4JConceptionKindImpl)_ConceptionKind01).getCoreRealmName(), PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME));

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
        Assert.assertEquals(((Neo4JConceptionKindImpl)_ConceptionKind01).getCoreRealmName(), PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME));

        List<ConceptionKind> conceptionKindsList = coreRealm.getConceptionKindsByMetaConfigItemMatch("configItemA",100);
        Assert.assertNotNull(conceptionKindsList);
        Assert.assertEquals(conceptionKindsList.size(),0);
        _ConceptionKind01.addOrUpdateMetaConfigItem("configItemA",100);
        conceptionKindsList = coreRealm.getConceptionKindsByMetaConfigItemMatch("configItemA",100);
        Assert.assertEquals(conceptionKindsList.size(),1);
        Assert.assertEquals(conceptionKindsList.get(0).getConceptionKindName(),"kind01");
        _ConceptionKind02.addOrUpdateMetaConfigItem("configItemA",100);
        conceptionKindsList = coreRealm.getConceptionKindsByMetaConfigItemMatch("configItemA",100);
        Assert.assertEquals(conceptionKindsList.size(),2);
        conceptionKindsList = coreRealm.getConceptionKindsByMetaConfigItemMatch("configItemA",100000);
        Assert.assertEquals(conceptionKindsList.size(),0);
        _ConceptionKind01.deleteMetaConfigItem("configItemA");
        _ConceptionKind02.deleteMetaConfigItem("configItemA");
        conceptionKindsList = coreRealm.getConceptionKindsByMetaConfigItemMatch("configItemA",100);
        Assert.assertEquals(conceptionKindsList.size(),0);

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

        List<RelationKind> relationKindsList = coreRealm.getRelationKindsByMetaConfigItemMatch("configItemA",200);
        Assert.assertNotNull(relationKindsList);
        Assert.assertEquals(relationKindsList.size(),0);
        targetRelationKind01.addOrUpdateMetaConfigItem("configItemA",200);
        relationKindsList = coreRealm.getRelationKindsByMetaConfigItemMatch("configItemA",200);
        Assert.assertEquals(relationKindsList.size(),1);
        Assert.assertEquals(relationKindsList.get(0).getRelationKindName(),"relationKind01");
        relationKindsList = coreRealm.getRelationKindsByMetaConfigItemMatch("configItemA",100000);
        Assert.assertEquals(relationKindsList.size(),0);
        targetRelationKind01.deleteMetaConfigItem("configItemA");
        relationKindsList = coreRealm.getRelationKindsByMetaConfigItemMatch("configItemA",200);
        Assert.assertEquals(relationKindsList.size(),0);

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

        List<AttributeKind>  attributeKindList = coreRealm.getAttributeKinds(null,null,null);
        Assert.assertTrue(attributeKindList.size()>0);
        attributeKindList = coreRealm.getAttributeKinds("attributeKind01",null,null);
        Assert.assertTrue(attributeKindList.size()>0);
        attributeKindList = coreRealm.getAttributeKinds(null,"attributeKind01Desc",null);
        Assert.assertTrue(attributeKindList.size()>0);
        attributeKindList = coreRealm.getAttributeKinds("attributeKind01","attributeKind01Desc",null);
        Assert.assertTrue(attributeKindList.size()>0);
        attributeKindList = coreRealm.getAttributeKinds("attributeKind01","attributeKind01DescNOTEXIST",null);
        Assert.assertTrue(attributeKindList.size()==0);
        attributeKindList = coreRealm.getAttributeKinds("attributeKind01","attributeKind01Desc",AttributeDataType.BINARY);
        Assert.assertTrue(attributeKindList.size()==0);
        attributeKindList = coreRealm.getAttributeKinds("attributeKind01","attributeKind01Desc",AttributeDataType.BOOLEAN);
        Assert.assertTrue(attributeKindList.size()>0);

        Assert.assertEquals(attributeKindList.get(0).getAttributeKindName(),"attributeKind01");
        Assert.assertEquals(attributeKindList.get(0).getAttributeKindDesc(),"attributeKind01Desc");
        Assert.assertEquals(attributeKindList.get(0).getAttributeDataType(),AttributeDataType.BOOLEAN);

        List<AttributeKindMetaInfo> attributeKindMetaInfoList = coreRealm.getAttributeKindsMetaInfo();
        Assert.assertNotNull(attributeKindMetaInfoList);
        Assert.assertTrue(attributeKindMetaInfoList.size()>0);

        boolean removeAttributeKindRes01 = coreRealm.removeAttributeKind(null);
        Assert.assertFalse(removeAttributeKindRes01);
        removeAttributeKindRes01 = coreRealm.removeAttributeKind(targetAttributeKindUID);
        Assert.assertTrue(removeAttributeKindRes01);
        attributeKind02 = coreRealm.getAttributeKind(targetAttributeKindUID);
        Assert.assertNull(attributeKind02);

        attributeKindList = coreRealm.getAttributeKinds(null,null,null);
        Assert.assertTrue(attributeKindList.size() == 0);

        AttributesViewKind attributesViewKind03 = coreRealm.createAttributesViewKind("attributesViewKind03","attributesViewKind03Desc",AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE);

        List<AttributesViewKind> attributesViewKindList = coreRealm.getAttributesViewKinds(null,null,null);
        Assert.assertTrue(attributesViewKindList.size()>0);
        attributesViewKindList = coreRealm.getAttributesViewKinds("attributesViewKind03",null,null);
        Assert.assertTrue(attributesViewKindList.size()>0);
        attributesViewKindList = coreRealm.getAttributesViewKinds(null,"attributesViewKind03Desc",null);
        Assert.assertTrue(attributesViewKindList.size()>0);
        attributesViewKindList = coreRealm.getAttributesViewKinds("attributesViewKind03","attributesViewKind03Desc",null);
        Assert.assertTrue(attributesViewKindList.size()>0);
        attributesViewKindList = coreRealm.getAttributesViewKinds("attributesViewKind03","attributesViewKind03DescNOTEXIST",null);
        Assert.assertTrue(attributesViewKindList.size()==0);
        attributesViewKindList = coreRealm.getAttributesViewKinds("attributesViewKind03","attributesViewKind03Desc",AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE);
        Assert.assertTrue(attributesViewKindList.size()==0);
        attributesViewKindList = coreRealm.getAttributesViewKinds("attributesViewKind03","attributesViewKind03Desc",AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE);
        Assert.assertTrue(attributesViewKindList.size()>0);

        boolean deleteConfigItemResult = attributesViewKind03.deleteMetaConfigItem("TESTFilterConfig");
        Assert.assertTrue(deleteConfigItemResult);
        Assert.assertNull(attributesViewKind03.getMetaConfigItem("TESTFilterConfig"));
        List<AttributesViewKind> matchedAttributesViewKindList = coreRealm.getAttributesViewKindsByMetaConfigItemMatch("TESTFilterConfig","selected");
        Assert.assertEquals(matchedAttributesViewKindList.size(),0);
        attributesViewKind03.addOrUpdateMetaConfigItem("TESTFilterConfig","selected");
        matchedAttributesViewKindList = coreRealm.getAttributesViewKindsByMetaConfigItemMatch("TESTFilterConfig","selected");
        Assert.assertEquals(matchedAttributesViewKindList.size(),1);
        Assert.assertEquals(matchedAttributesViewKindList.get(0).getAttributesViewKindName(),"attributesViewKind03");
        attributesViewKind03.addOrUpdateMetaConfigItem("TESTFilterConfig","notSelected");
        matchedAttributesViewKindList = coreRealm.getAttributesViewKindsByMetaConfigItemMatch("TESTFilterConfig","selected");
        Assert.assertEquals(matchedAttributesViewKindList.size(),0);
        coreRealm.removeAttributesViewKind(attributesViewKind03.getAttributesViewKindUID());

        RelationAttachKind targetRelationAttachKind = coreRealm.createRelationAttachKind("RelationAttachKind_Name","RelationAttachKind_Desc",
                "RelationAttachKind_SourceKind","RelationAttachKind_TargetKind","RelationAttachKind_RelationKind",true);
        Assert.assertNotNull(targetRelationAttachKind);
        Assert.assertNotNull(targetRelationAttachKind.getRelationAttachKindUID());
        Assert.assertEquals(targetRelationAttachKind.getRelationAttachKindName(),"RelationAttachKind_Name");
        Assert.assertEquals(targetRelationAttachKind.getRelationAttachKindDesc(),"RelationAttachKind_Desc");
        Assert.assertEquals(targetRelationAttachKind.getSourceConceptionKindName(),"RelationAttachKind_SourceKind");
        Assert.assertEquals(targetRelationAttachKind.getTargetConceptionKindName(),"RelationAttachKind_TargetKind");
        Assert.assertEquals(targetRelationAttachKind.getRelationKindName(),"RelationAttachKind_RelationKind");
        Assert.assertEquals(targetRelationAttachKind.isRepeatableRelationKindAllow(),true);

        RelationAttachKind targetRelationAttachKind2 = coreRealm.getRelationAttachKind(targetRelationAttachKind.getRelationAttachKindUID());
        Assert.assertNotNull(targetRelationAttachKind2);
        Assert.assertNotNull(targetRelationAttachKind2.getRelationAttachKindUID());
        Assert.assertEquals(targetRelationAttachKind2.getRelationAttachKindName(),"RelationAttachKind_Name");
        Assert.assertEquals(targetRelationAttachKind2.getRelationAttachKindDesc(),"RelationAttachKind_Desc");
        Assert.assertEquals(targetRelationAttachKind2.getSourceConceptionKindName(),"RelationAttachKind_SourceKind");
        Assert.assertEquals(targetRelationAttachKind2.getTargetConceptionKindName(),"RelationAttachKind_TargetKind");
        Assert.assertEquals(targetRelationAttachKind2.getRelationKindName(),"RelationAttachKind_RelationKind");
        Assert.assertEquals(targetRelationAttachKind2.isRepeatableRelationKindAllow(),true);

        RelationAttachKind  targetRelationAttachKind3 = coreRealm.getRelationAttachKind(targetRelationAttachKind.getRelationAttachKindUID()+"1000");
        Assert.assertNull(targetRelationAttachKind3);

        targetRelationAttachKind = coreRealm.createRelationAttachKind("RelationAttachKind_Name","RelationAttachKind_Desc",
                "RelationAttachKind_SourceKind","RelationAttachKind_TargetKind","RelationAttachKind_RelationKind",true);
        Assert.assertNull(targetRelationAttachKind);

        targetRelationAttachKind = coreRealm.createRelationAttachKind("RelationAttachKind_Name","RelationAttachKind_Desc",
                null,"RelationAttachKind_TargetKind","RelationAttachKind_RelationKind",true);
        Assert.assertNull(targetRelationAttachKind);

        targetRelationAttachKind = coreRealm.createRelationAttachKind(null,"RelationAttachKind_Desc",
                null,"RelationAttachKind_TargetKind","RelationAttachKind_RelationKind",true);
        Assert.assertNull(targetRelationAttachKind);

        targetRelationAttachKind = coreRealm.createRelationAttachKind("RelationAttachKind_Name2","RelationAttachKind_Desc",
                "RelationAttachKind_SourceKind","RelationAttachKind_TargetKind","RelationAttachKind_RelationKind",false);
        Assert.assertNotNull(targetRelationAttachKind);
        Assert.assertNotNull(targetRelationAttachKind.getRelationAttachKindUID());
        Assert.assertEquals(targetRelationAttachKind.getRelationAttachKindName(),"RelationAttachKind_Name2");
        Assert.assertEquals(targetRelationAttachKind.getRelationAttachKindDesc(),"RelationAttachKind_Desc");
        Assert.assertEquals(targetRelationAttachKind.getSourceConceptionKindName(),"RelationAttachKind_SourceKind");
        Assert.assertEquals(targetRelationAttachKind.getTargetConceptionKindName(),"RelationAttachKind_TargetKind");
        Assert.assertEquals(targetRelationAttachKind.getRelationKindName(),"RelationAttachKind_RelationKind");
        Assert.assertEquals(targetRelationAttachKind.isRepeatableRelationKindAllow(),false);

        List<RelationAttachKind> relationAttachKindList = coreRealm.getRelationAttachKinds(null,null,null,null,null,Boolean.valueOf(true));
        Assert.assertNotNull(relationAttachKindList);
        Assert.assertEquals(relationAttachKindList.size(),1);
        relationAttachKindList = coreRealm.getRelationAttachKinds(null,null,null,null,null,Boolean.valueOf(false));
        Assert.assertNotNull(relationAttachKindList);
        Assert.assertEquals(relationAttachKindList.size(),1);

        relationAttachKindList = coreRealm.getRelationAttachKinds(null,null,null,null,null,null);
        Assert.assertNotNull(relationAttachKindList);
        Assert.assertEquals(relationAttachKindList.size(),2);

        relationAttachKindList = coreRealm.getRelationAttachKinds("NOTMatchedValue",null,null,null,null,null);
        Assert.assertNotNull(relationAttachKindList);
        Assert.assertEquals(relationAttachKindList.size(),0);

        boolean removeResult = coreRealm.removeRelationAttachKind(targetRelationAttachKind.getRelationAttachKindUID());
        Assert.assertTrue(removeResult);
        removeResult = coreRealm.removeRelationAttachKind(targetRelationAttachKind2.getRelationAttachKindUID());
        Assert.assertTrue(removeResult);

        exceptionShouldBeCaught = false;
        try{
            coreRealm.removeRelationAttachKind(targetRelationAttachKind2.getRelationAttachKindUID()+"12345");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        TimeFlow defaultTimeFlow = coreRealm.getOrCreateTimeFlow();
        Assert.assertNotNull(defaultTimeFlow);
        Assert.assertNotNull(((Neo4JTimeFlowImpl)defaultTimeFlow).getTimeFlowUID());
        Assert.assertEquals(defaultTimeFlow.getTimeFlowName(), RealmConstant._defaultTimeFlowName);

        TimeFlow defaultTimeFlow2 = coreRealm.getOrCreateTimeFlow("自定义时间流");
        Assert.assertNotNull(defaultTimeFlow2);
        Assert.assertNotNull(((Neo4JTimeFlowImpl)defaultTimeFlow2).getTimeFlowUID());
        Assert.assertEquals(defaultTimeFlow2.getTimeFlowName(), "自定义时间流");

        List<TimeFlow> timeFlowsList = coreRealm.getTimeFlows();
        Assert.assertTrue(timeFlowsList.size()>=2);

        boolean hasDefaultTimeFlow = false;
        boolean hasCustomTimeFlow = false;
        for(TimeFlow currentTimeFlow:timeFlowsList){
            if(currentTimeFlow.getTimeFlowName().equals(RealmConstant._defaultTimeFlowName)){
                hasDefaultTimeFlow = true;
            }
            if(currentTimeFlow.getTimeFlowName().equals("自定义时间流")){
                hasCustomTimeFlow = true;
            }
        }
        Assert.assertTrue(hasDefaultTimeFlow);
        Assert.assertTrue(hasCustomTimeFlow);

        List<EntityStatisticsInfo> statisticsInfosList = coreRealm.getConceptionEntitiesStatistics();
        Assert.assertNotNull(statisticsInfosList);
        Assert.assertTrue(statisticsInfosList.size()>1);
        for(EntityStatisticsInfo currentEntityStatisticsInfo:statisticsInfosList){
            if(!currentEntityStatisticsInfo.isSystemKind()){
                Assert.assertNotNull(currentEntityStatisticsInfo.getEntityKindName());
                Assert.assertNotNull(currentEntityStatisticsInfo.getEntityKindType());
                Assert.assertNotNull(currentEntityStatisticsInfo.getEntityKindUID());
                Assert.assertNotNull(currentEntityStatisticsInfo.getEntityKindDesc());
            }
        }

        statisticsInfosList = coreRealm.getRelationEntitiesStatistics();
        Assert.assertNotNull(statisticsInfosList);
        Assert.assertTrue(statisticsInfosList.size()>0);
        for(EntityStatisticsInfo currentEntityStatisticsInfo:statisticsInfosList){
            if(!currentEntityStatisticsInfo.isSystemKind()){
                Assert.assertNotNull(currentEntityStatisticsInfo.getEntityKindName());
                Assert.assertNotNull(currentEntityStatisticsInfo.getEntityKindType());
                Assert.assertNotNull(currentEntityStatisticsInfo.getEntityKindUID());
                Assert.assertNotNull(currentEntityStatisticsInfo.getEntityKindDesc());
            }
        }
//NEED ENABLE//
//NEED ENABLE//
       //List<ConceptionKindCorrelationInfo> correlationInfo = coreRealm.getConceptionKindsCorrelation();
       //Assert.assertNotNull(correlationInfo);

        List<KindMetaInfo> kindMetaInfoList = coreRealm.getConceptionKindsMetaInfo();
        Assert.assertNotNull(kindMetaInfoList);
        Assert.assertTrue(kindMetaInfoList.size()>0);

        coreRealm.createRelationKind("relationKindForRemove01","relationKindForRemove01Desc");
        kindMetaInfoList = coreRealm.getRelationKindsMetaInfo();
        Assert.assertNotNull(kindMetaInfoList);
        Assert.assertTrue(kindMetaInfoList.size()>0);

        int currentRelationKindsCount = kindMetaInfoList.size();
        coreRealm.removeRelationKind("relationKindForRemove01",true);
        kindMetaInfoList = coreRealm.getRelationKindsMetaInfo();
        Assert.assertNotNull(kindMetaInfoList);
        Assert.assertEquals(kindMetaInfoList.size(),currentRelationKindsCount-1);

        List<AttributesViewKindMetaInfo> attributesViewKindMetaInfoList = coreRealm.getAttributesViewKindsMetaInfo();
        Assert.assertNotNull(attributesViewKindMetaInfoList);
        Assert.assertTrue(attributesViewKindMetaInfoList.size()>0);

        coreRealm.createConceptionKind("ConceptionKindForRename","ConceptionKindForRenameDesc");
        ConceptionKind _ConceptionKindForRename =coreRealm.getConceptionKind("ConceptionKindForRename");

        Map<String,Object> newEntityValueMap= new HashMap<>();
        newEntityValueMap.put("prop1",Long.parseLong("12345"));
        List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();
        for(int i =0 ;i<1000;i++){
            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
            conceptionEntityValueList.add(conceptionEntityValue);
        }

        _ConceptionKindForRename.newEntities(conceptionEntityValueList,false);

        _ConceptionKindForRename = coreRealm.getConceptionKind("ConceptionKindForRename");
        Assert.assertNotNull(_ConceptionKindForRename);
        Assert.assertEquals(_ConceptionKindForRename.countConceptionEntities().longValue(),1000);

        for(int i=0;i<100;i++){
            Set<ConceptionEntity> conceptionEntities =  _ConceptionKindForRename.getRandomEntities(2);
            Iterator<ConceptionEntity> entitiesItor = conceptionEntities.iterator();
            ConceptionEntity firstEntity = entitiesItor.next();
            ConceptionEntity secondEntity = entitiesItor.next();
            firstEntity.attachFromRelation(secondEntity.getConceptionEntityUID(),"RelationKindForRenameA",null,true);
        }

        boolean renameResult = coreRealm.renameConceptionKind("ConceptionKindForRename","ConceptionKindForRenameAfterOpe","ConceptionKindForRenameAfterOpeDesc");
        Assert.assertTrue(renameResult);

        RelationKind _RelationKindForRenameBefore = coreRealm.createRelationKind("RelationKindForRenameA","RelationKindForRenameDescA");
        Assert.assertNotNull(_RelationKindForRenameBefore);
        Assert.assertEquals(_RelationKindForRenameBefore.getRelationKindName(),"RelationKindForRenameA");
        Assert.assertEquals(_RelationKindForRenameBefore.getRelationKindDesc(),"RelationKindForRenameDescA");

        long relationEntitiesCount = _RelationKindForRenameBefore.countRelationEntities();
        Assert.assertEquals(relationEntitiesCount,100);

        ConceptionKind _ConceptionKindForRenameAfter = coreRealm.getConceptionKind("ConceptionKindForRenameAfterOpe");
        Assert.assertNotNull(_ConceptionKindForRenameAfter);
        Assert.assertEquals(_ConceptionKindForRenameAfter.getConceptionKindName(),"ConceptionKindForRenameAfterOpe");
        Assert.assertEquals(_ConceptionKindForRenameAfter.getConceptionKindDesc(),"ConceptionKindForRenameAfterOpeDesc");
        Assert.assertEquals(_ConceptionKindForRenameAfter.countConceptionEntities().longValue(),1000);

//NEED ENABLE//
//NEED ENABLE//
        //boolean renameRelationKindResult = coreRealm.renameRelationKind("RelationKindForRenameA","RelationKindForRename-B","RelationKindForRenamebDesc");
        //Assert.assertTrue(renameRelationKindResult);
        //Assert.assertNull(coreRealm.getRelationKind("RelationKindForRenameA"));

        RelationKind _RelationKindForRenameBAfter = coreRealm.getRelationKind("RelationKindForRename-B");
        //Assert.assertNotNull(_RelationKindForRenameBAfter);
        //Assert.assertEquals(_RelationKindForRenameBAfter.getRelationKindName(),"RelationKindForRename-B");
        //Assert.assertEquals(_RelationKindForRenameBAfter.getRelationKindDesc(),"RelationKindForRenamebDesc");

        //relationEntitiesCount = _RelationKindForRenameBAfter.countRelationEntities();
        //Assert.assertEquals(relationEntitiesCount,100);

        //Assert.assertNull(coreRealm.getConceptionKind("ConceptionKindForRename"));
        //coreRealm.removeConceptionKind("ConceptionKindForRenameAfterOpe",true);
//NEED ENABLE//
//NEED ENABLE//
        //  coreRealm.removeRelationKind("RelationKindForRename-B",true);
    }
}