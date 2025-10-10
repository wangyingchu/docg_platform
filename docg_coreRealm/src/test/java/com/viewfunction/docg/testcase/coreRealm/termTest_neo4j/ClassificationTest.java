package com.viewfunction.docg.testcase.coreRealm.termTest_neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JClassificationImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

public class ClassificationTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ClassificationTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testClassificationFunction() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        coreRealm.openGlobalSession();

        String classificationName01 = "classification1";
        Classification _Classification01 = coreRealm.getClassification(classificationName01);

        Assert.assertFalse(coreRealm.removeClassification(null));
        if(_Classification01 != null){
            boolean removeClassificationResult = coreRealm.removeClassification(classificationName01);
            Assert.assertTrue(removeClassificationResult);

        }

        _Classification01 = coreRealm.getClassification(classificationName01);
        Assert.assertNull(_Classification01);
        _Classification01 = coreRealm.createClassification(classificationName01,classificationName01+"Desc");
        Assert.assertNotNull(_Classification01);
        _Classification01 = coreRealm.getClassification(classificationName01);
        Assert.assertNotNull(_Classification01);

        String classificationName02 = "classification2";
        Classification _Classification02 = coreRealm.getClassification(classificationName02);
        if(_Classification02 != null){
            coreRealm.removeClassification(classificationName02);
        }

        _Classification02 = coreRealm.createClassification(classificationName02,classificationName02+"Desc",classificationName01);
        Assert.assertNotNull(_Classification02);

        Classification parentClassificationOf02 = _Classification02.getParentClassification();
        Assert.assertNotNull(parentClassificationOf02);
        Assert.assertTrue(_Classification01.isRootClassification());
        Assert.assertTrue(parentClassificationOf02.isRootClassification());
        Assert.assertFalse(_Classification02.isRootClassification());

        Assert.assertEquals(_Classification01.getClassificationName(),parentClassificationOf02.getClassificationName());
        Assert.assertEquals(_Classification01.getClassificationDesc(),parentClassificationOf02.getClassificationDesc());

        if(_Classification01 instanceof Neo4JClassificationImpl && parentClassificationOf02 instanceof Neo4JClassificationImpl){
            Assert.assertEquals(
                    ((Neo4JClassificationImpl)_Classification01).getClassificationUID(),
                    ((Neo4JClassificationImpl)parentClassificationOf02).getClassificationUID()
            );
        }

        String classificationName03 = "classification3";
        Classification _Classification03 = coreRealm.getClassification(classificationName03);
        if(_Classification03 != null){
            coreRealm.removeClassification(classificationName03);
        }

        String classificationName04 = "classification4";
        Classification _Classification04 = coreRealm.getClassification(classificationName04);
        if(_Classification04 != null){
            coreRealm.removeClassification(classificationName04);
        }

        String classificationName05 = "classification5";
        Classification _Classification05 = coreRealm.getClassification(classificationName05);
        if(_Classification05 != null){
            coreRealm.removeClassification(classificationName05);
        }

        coreRealm.createClassification(classificationName03,classificationName03+"Desc",classificationName01);
        coreRealm.createClassification(classificationName04,classificationName04+"Desc",classificationName01);
        _Classification05 = coreRealm.createClassification(classificationName05,classificationName05+"Desc",classificationName01);

        List<Classification> _Classification01ChildrenList = _Classification01.getChildClassifications();
        Assert.assertNotNull(_Classification01ChildrenList);
        Assert.assertEquals(_Classification01ChildrenList.size(),4);

        AttributesParameters attributesParameters = new AttributesParameters();
        attributesParameters.setDefaultFilteringItem(new EqualFilteringItem("description","classification1DescNotExist"));
        _Classification01ChildrenList = _Classification01.getChildClassifications(attributesParameters);
        Assert.assertNotNull(_Classification01ChildrenList);
        Assert.assertEquals(_Classification01ChildrenList.size(),0);

        attributesParameters.setDefaultFilteringItem(new EqualFilteringItem("description",classificationName02+"Desc"));
        _Classification01ChildrenList = _Classification01.getChildClassifications(attributesParameters);
        Assert.assertNotNull(_Classification01ChildrenList);
        Assert.assertEquals(_Classification01ChildrenList.size(),1);
        Assert.assertEquals(_Classification01ChildrenList.get(0).getClassificationName(),classificationName02);
        attributesParameters.addFilteringItem(new EqualFilteringItem("notEXISTFilter","NotExist"), QueryParameters.FilteringLogic.OR);
        _Classification01ChildrenList = _Classification01.getChildClassifications(attributesParameters);
        Assert.assertNotNull(_Classification01ChildrenList);
        Assert.assertEquals(_Classification01ChildrenList.size(),1);

        String classificationName05_1 = "classification5_1";
        Classification _Classification05_1 = coreRealm.getClassification(classificationName05_1);
        if(_Classification05_1 != null){
            coreRealm.removeClassification(classificationName05_1);
        }

        String classificationName05_2 = "classification5_2";
        Classification _Classification05_2 = coreRealm.getClassification(classificationName05_2);
        if(_Classification05_2 != null){
            coreRealm.removeClassification(classificationName05_2);
        }

        _Classification05_1 = coreRealm.createClassification(classificationName05_1,classificationName05_1+"Desc",classificationName05);
        _Classification05_2 = coreRealm.createClassification(classificationName05_2,classificationName05_2+"Desc",classificationName05);

        String classificationName05_1_1 = "classification5_1_1";
        Classification _Classification05_2_1 = coreRealm.getClassification(classificationName05_1_1);
        if(_Classification05_2_1 != null){
            coreRealm.removeClassification(classificationName05_1_1);
        }
        _Classification05_2_1 = coreRealm.createClassification(classificationName05_1_1,classificationName05_1_1+"Desc",classificationName05_1);

        _Classification01ChildrenList = _Classification01.getChildClassifications();
        Assert.assertNotNull(_Classification01ChildrenList);
        Assert.assertEquals(_Classification01ChildrenList.size(),4);

        List<Classification> _Classification05ChildrenList = _Classification05.getChildClassifications();
        Assert.assertNotNull(_Classification05ChildrenList);
        Assert.assertEquals(_Classification05ChildrenList.size(),2);

        InheritanceTree<Classification> tree01 = _Classification01.getOffspringClassifications();
        InheritanceTree<Classification> tree02 = _Classification05.getOffspringClassifications();

        Assert.assertNotNull(tree01);
        Assert.assertNotNull(tree02);
        Assert.assertEquals(tree01.size(),8);

        Assert.assertEquals(tree01.numOfChildren(classificationName01),4);
        Assert.assertEquals(tree01.numOfChildren(classificationName05),2);

        Assert.assertEquals(tree01.depth(classificationName01),0);
        Assert.assertEquals(tree01.depth(classificationName05),1);
        Assert.assertEquals(tree01.depth(classificationName05_1),2);
        Assert.assertEquals(tree01.depth(classificationName05_1_1),3);

        Assert.assertTrue(tree01.isLeafNode(classificationName02));
        Assert.assertTrue(tree01.isLeafNode(classificationName03));
        Assert.assertTrue(tree01.isLeafNode(classificationName05_2));
        Assert.assertFalse(tree01.isLeafNode(classificationName01));
        Assert.assertFalse(tree01.isLeafNode(classificationName05));

        Assert.assertTrue(tree01.isRoot(classificationName01));
        Assert.assertFalse(tree01.isRoot(classificationName03));
        Assert.assertFalse(tree01.isRoot(classificationName05));
        Assert.assertFalse(tree01.isRoot(classificationName05_2));

        Assert.assertEquals(tree01.getRootID(),classificationName01);

        Classification rootOfTree = tree01.getRoot();
        Assert.assertNotNull(rootOfTree);
        Assert.assertEquals(rootOfTree.getClassificationName(),classificationName01);
        Assert.assertEquals(rootOfTree.getClassificationDesc(),classificationName01+"Desc");
        if(_Classification01 instanceof Neo4JClassificationImpl && rootOfTree instanceof Neo4JClassificationImpl){
            Assert.assertEquals(
                    ((Neo4JClassificationImpl)_Classification01).getClassificationUID(),
                    ((Neo4JClassificationImpl)rootOfTree).getClassificationUID()
            );
        }

        Classification leafOfTree = tree01.getNode(classificationName05_1);
        Assert.assertNotNull(leafOfTree);
        Assert.assertEquals(leafOfTree.getClassificationName(),classificationName05_1);
        Assert.assertEquals(leafOfTree.getClassificationDesc(),classificationName05_1+"Desc");
        if(_Classification05_1 instanceof Neo4JClassificationImpl && leafOfTree instanceof Neo4JClassificationImpl){
            Assert.assertEquals(
                    ((Neo4JClassificationImpl)_Classification05_1).getClassificationUID(),
                    ((Neo4JClassificationImpl)leafOfTree).getClassificationUID()
            );
        }

        Classification parentOfClassification05_1 = tree01.getParent(classificationName05_1);
        Assert.assertNotNull(parentOfClassification05_1);
        Assert.assertEquals(parentOfClassification05_1.getClassificationName(),classificationName05);
        String parentNodeUID = tree01.getParentID(classificationName05_1);
        Assert.assertNotNull(parentNodeUID);
        Assert.assertEquals(parentNodeUID,classificationName05);

        Collection<String> childIDOfRoot = tree01.getChildrenID(classificationName01);
        Assert.assertNotNull(childIDOfRoot);
        Assert.assertEquals(childIDOfRoot.size(),4);
        Assert.assertTrue(childIDOfRoot.contains(classificationName02));
        Assert.assertTrue(childIDOfRoot.contains(classificationName03));
        Assert.assertTrue(childIDOfRoot.contains(classificationName04));
        Assert.assertTrue(childIDOfRoot.contains(classificationName05));
        Assert.assertFalse(childIDOfRoot.contains(classificationName05+"NOTEXIST"));

        Collection<String> childIDOfNode5 = tree01.getChildrenID(classificationName05);
        Assert.assertNotNull(childIDOfNode5);
        Assert.assertEquals(childIDOfNode5.size(),2);
        Assert.assertTrue(childIDOfNode5.contains(classificationName05_1));
        Assert.assertTrue(childIDOfNode5.contains(classificationName05_2));

        Collection<String> childIDOfNode5_2 = tree01.getChildrenID(classificationName05_2);
        Assert.assertNotNull(childIDOfNode5_2);
        Assert.assertEquals(childIDOfNode5_2.size(),0);

        Collection<Classification> childrenOfNode5 = tree01.getChildren(classificationName05);
        Assert.assertNotNull(childrenOfNode5);
        Assert.assertEquals(childrenOfNode5.size(),2);

        Collection<Classification> siblingsOfNode5 = tree01.getSiblings(classificationName05_2);
        Assert.assertNotNull(siblingsOfNode5);
        Assert.assertEquals(siblingsOfNode5.size(),1);
        Assert.assertEquals(siblingsOfNode5.iterator().next().getClassificationName(), classificationName05_1);

        childIDOfNode5 = tree01.getChildrenID(classificationName05);
        Assert.assertNotNull(childIDOfNode5);
        Assert.assertEquals(childIDOfNode5.size(),2);

        Collection<String> siblingsIDOfNode5 = tree01.getSiblingsID(classificationName05_2);

        Assert.assertNotNull(siblingsIDOfNode5);
        Assert.assertEquals(siblingsIDOfNode5.size(),1);
        Assert.assertEquals(siblingsIDOfNode5.iterator().next(), classificationName05_1);

        Iterable<Classification> pathOfClassification = tree01.path(classificationName05,classificationName05_1_1);
        Assert.assertNotNull(pathOfClassification);

        Iterator<Classification> pathIterator = pathOfClassification.iterator();
        Assert.assertTrue(pathIterator.hasNext());
        int currentPathStep = 0;
        while(pathIterator.hasNext()){
            currentPathStep++;
            Classification currentClassification = pathIterator.next();
            if(currentPathStep ==1){
                Assert.assertEquals(currentClassification.getClassificationName(),classificationName05_1_1);
            }
            if(currentPathStep ==2){
                Assert.assertEquals(currentClassification.getClassificationName(),classificationName05_1);
            }
            if(currentPathStep ==3){
                Assert.assertEquals(currentClassification.getClassificationName(),classificationName05);
            }
        }

        pathOfClassification = tree01.path(classificationName02,classificationName05_1_1);
        Assert.assertNull(pathOfClassification);

        pathOfClassification = tree01.path(classificationName01,classificationName05_1);
        Assert.assertNotNull(pathOfClassification);

        pathIterator = pathOfClassification.iterator();
        Assert.assertTrue(pathIterator.hasNext());
        currentPathStep = 0;
        while(pathIterator.hasNext()){
            currentPathStep++;
            Classification currentClassification = pathIterator.next();
            if(currentPathStep ==1){
                Assert.assertEquals(currentClassification.getClassificationName(),classificationName05_1);
            }
            if(currentPathStep ==2){
                Assert.assertEquals(currentClassification.getClassificationName(),classificationName05);
            }
            if(currentPathStep ==3){
                Assert.assertEquals(currentClassification.getClassificationName(),classificationName01);
            }
        }

        Iterable<Classification> traverTreeIterator = tree01.traversalTree(classificationName01);
        Assert.assertNotNull(traverTreeIterator);
        Iterator<Classification> traverIterator = traverTreeIterator.iterator();
        Assert.assertNotNull(traverIterator);
        Assert.assertTrue(traverIterator.hasNext());

        traverTreeIterator = tree01.traversalTree(classificationName01, InheritanceTree.TraversalStrategy.PreOrder);
        Assert.assertNotNull(traverTreeIterator);
        traverIterator = traverTreeIterator.iterator();
        Assert.assertNotNull(traverIterator);
        Assert.assertTrue(traverIterator.hasNext());

        traverTreeIterator = tree01.traversalTree(classificationName01, InheritanceTree.TraversalStrategy.PostOrder);
        Assert.assertNotNull(traverTreeIterator);
        traverIterator = traverTreeIterator.iterator();
        Assert.assertNotNull(traverIterator);
        Assert.assertTrue(traverIterator.hasNext());

        Iterable<String> traverTreeIterator2 = tree01.traversalTreeByID(classificationName01);
        Assert.assertNotNull(traverTreeIterator2);

        Assert.assertNotNull(traverTreeIterator2.iterator());
        Assert.assertTrue(traverTreeIterator2.iterator().hasNext());

        AttributesParameters attributesParameters2 = new AttributesParameters();
        attributesParameters2.setDefaultFilteringItem(new EqualFilteringItem("description","classification1DescNotExist"));
        InheritanceTree<Classification> tree03 = _Classification01.getOffspringClassifications(attributesParameters2);
        Assert.assertNotNull(tree03);
        Assert.assertEquals(tree03.size(),1);
        attributesParameters2.setDefaultFilteringItem(new EqualFilteringItem("description",classificationName02+"Desc"));
        tree03 = _Classification01.getOffspringClassifications(attributesParameters2);
        Assert.assertNotNull(tree03);
        Assert.assertEquals(tree03.size(),2);
        attributesParameters2.addFilteringItem(new EqualFilteringItem("notExistProp","NotExistValue"), QueryParameters.FilteringLogic.AND);
        tree03 = _Classification01.getOffspringClassifications(attributesParameters2);
        Assert.assertNotNull(tree03);
        Assert.assertEquals(tree03.size(),1);

        String classificationName0A = "classificationName0A";
        Classification _Classification0A = coreRealm.getClassification(classificationName0A);
        if(_Classification0A != null){
            coreRealm.removeClassification(classificationName0A);
        }
        _Classification0A = coreRealm.createClassification(classificationName0A,classificationName0A+"Desc");

        String classificationName0B = "classificationName0B";
        Classification _Classification0B = coreRealm.getClassification(classificationName0B);
        if(_Classification0B != null){
            coreRealm.removeClassification(classificationName0B);
        }
        _Classification0B = coreRealm.createClassification(classificationName0B,classificationName0B+"Desc");

        List<Classification> childClassificationList = _Classification0A.getChildClassifications();
        Assert.assertNotNull(childClassificationList);
        Assert.assertEquals(childClassificationList.size(),0);

        Classification parentClassification = _Classification0B.getParentClassification();
        Assert.assertNull(parentClassification);

        boolean attachChildClassificationResult = _Classification0A.attachChildClassification(classificationName0B);
        Assert.assertTrue(attachChildClassificationResult);
        attachChildClassificationResult = _Classification0A.attachChildClassification(classificationName0B);
        Assert.assertTrue(attachChildClassificationResult);
        attachChildClassificationResult = _Classification0A.attachChildClassification(classificationName0B);
        Assert.assertTrue(attachChildClassificationResult);

        childClassificationList = _Classification0A.getChildClassifications();
        Assert.assertNotNull(childClassificationList);
        Assert.assertEquals(childClassificationList.size(),1);

        parentClassification = _Classification0B.getParentClassification();
        Assert.assertNotNull(parentClassification);

        Assert.assertEquals(childClassificationList.get(0).getClassificationName(),classificationName0B);
        Assert.assertEquals(parentClassification.getClassificationName(),classificationName0A);

        boolean detachChildClassificationResult = _Classification0A.detachChildClassification(classificationName0B);
        Assert.assertTrue(detachChildClassificationResult);

        childClassificationList = _Classification0A.getChildClassifications();
        Assert.assertNotNull(childClassificationList);
        Assert.assertEquals(childClassificationList.size(),0);

        parentClassification = _Classification0B.getParentClassification();
        Assert.assertNull(parentClassification);

        String classificationName0C = "classificationName0C";
        Classification _Classification0C = coreRealm.getClassification(classificationName0C);
        if(_Classification0C != null){
            coreRealm.removeClassification(classificationName0C);
        }

        _Classification0C = _Classification0A.createChildClassification(classificationName0C,classificationName0C+"DESC");
        Assert.assertNotNull(_Classification0C);

        childClassificationList = _Classification0A.getChildClassifications();
        Assert.assertNotNull(childClassificationList);
        Assert.assertEquals(childClassificationList.size(),1);

        parentClassification = _Classification0C.getParentClassification();
        Assert.assertNotNull(parentClassification);

        Assert.assertEquals(childClassificationList.get(0).getClassificationName(),classificationName0C);
        Assert.assertEquals(parentClassification.getClassificationName(),classificationName0A);

        Assert.assertNotNull(coreRealm.getClassification(classificationName0C));

        boolean removeChildClassificationResult = _Classification0A.removeChildClassification(classificationName0C);
        Assert.assertTrue(removeChildClassificationResult);
        Assert.assertNull(coreRealm.getClassification(classificationName0C));

        boolean exceptionShouldBeCaught = false;
        try{
            _Classification0A.createChildClassification(classificationName05,classificationName0C+"DESC");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _Classification0A.removeChildClassification(classificationName0C);
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _Classification0A.removeChildClassification(classificationName0C+"notExist");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _Classification0A.attachChildClassification(classificationName0C+"notExist");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _Classification0A.detachChildClassification(classificationName0C+"notExist");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        exceptionShouldBeCaught = false;
        try{
            _Classification0A.detachChildClassification(classificationName05);
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind("testConceptionKindForClassification");
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind("testConceptionKindForClassification",true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind("testConceptionKindForClassification");
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind("testConceptionKindForClassification","TestConceptionKindADesc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
        }

        Map<String,Object> relationDataMap = new HashMap<>();

        RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
        relationAttachInfo.setRelationKind("relationTypeForClassificationTest01");
        relationAttachInfo.setRelationDirection(RelationDirection.FROM);
        relationAttachInfo.setRelationData(relationDataMap);

        RelationEntity resultRelationEntity01 = _ConceptionKind01.attachClassification(relationAttachInfo,classificationName05_1);
        Assert.assertNotNull(resultRelationEntity01);
        resultRelationEntity01 = _ConceptionKind01.attachClassification(relationAttachInfo,classificationName05_1);
        Assert.assertNull(resultRelationEntity01);

        relationAttachInfo.setRelationKind("relationTypeForClassificationTest02");
        resultRelationEntity01 = _ConceptionKind01.attachClassification(relationAttachInfo,classificationName05_2);
        Assert.assertNotNull(resultRelationEntity01);

        relationAttachInfo.setRelationDirection(RelationDirection.TO);
        resultRelationEntity01 = _ConceptionKind01.attachClassification(relationAttachInfo,classificationName05_1_1);
        Assert.assertNotNull(resultRelationEntity01);

        exceptionShouldBeCaught = false;
        try{
            _ConceptionKind01.attachClassification(relationAttachInfo,classificationName05_1+"NotExist");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        List<Classification> attachedClassificationList = _ConceptionKind01.getAttachedClassifications("relationTypeForClassificationTest01",RelationDirection.TWO_WAY);
        Assert.assertNotNull(attachedClassificationList);
        Assert.assertEquals(attachedClassificationList.size(),1);
        Assert.assertEquals(attachedClassificationList.get(0).getClassificationName(),classificationName05_1);
        attachedClassificationList = _ConceptionKind01.getAttachedClassifications("relationTypeForClassificationTest01",RelationDirection.FROM);
        Assert.assertNotNull(attachedClassificationList);
        Assert.assertEquals(attachedClassificationList.size(),1);
        Assert.assertEquals(attachedClassificationList.get(0).getClassificationName(),classificationName05_1);
        attachedClassificationList = _ConceptionKind01.getAttachedClassifications("relationTypeForClassificationTest01",RelationDirection.TO);
        Assert.assertNotNull(attachedClassificationList);
        Assert.assertEquals(attachedClassificationList.size(),0);

        attachedClassificationList = _ConceptionKind01.getAttachedClassifications("relationTypeForClassificationTest02",RelationDirection.TWO_WAY);
        Assert.assertNotNull(attachedClassificationList);
        Assert.assertEquals(attachedClassificationList.size(),2);

        attachedClassificationList = _ConceptionKind01.getAttachedClassifications("relationTypeForClassificationTest02",RelationDirection.FROM);
        Assert.assertNotNull(attachedClassificationList);
        Assert.assertEquals(attachedClassificationList.size(),1);
        Assert.assertEquals(attachedClassificationList.get(0).getClassificationName(),classificationName05_2);

        attachedClassificationList = _ConceptionKind01.getAttachedClassifications("relationTypeForClassificationTest02",RelationDirection.TO);
        Assert.assertNotNull(attachedClassificationList);
        Assert.assertEquals(attachedClassificationList.size(),1);
        Assert.assertEquals(attachedClassificationList.get(0).getClassificationName(),classificationName05_1_1);

        boolean detachClassificationResult = _ConceptionKind01.detachClassification(classificationName05_1,"relationTypeForClassificationTest01",RelationDirection.TO);
        Assert.assertFalse(detachClassificationResult);
        detachClassificationResult = _ConceptionKind01.detachClassification(classificationName05_1,"relationTypeForClassificationTest01",RelationDirection.FROM);
        Assert.assertTrue(detachClassificationResult);
        attachedClassificationList = _ConceptionKind01.getAttachedClassifications("relationTypeForClassificationTest01",RelationDirection.FROM);
        Assert.assertEquals(attachedClassificationList.size(),0);
        detachClassificationResult = _ConceptionKind01.detachClassification(classificationName05_1,"relationTypeForClassificationTest01",RelationDirection.FROM);
        Assert.assertFalse(detachClassificationResult);

        exceptionShouldBeCaught = false;
        try{
            _ConceptionKind01.detachClassification(classificationName05_1_1,"relationTypeForClassificationTest02",RelationDirection.TWO_WAY);
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught);

        detachClassificationResult = _ConceptionKind01.detachClassification(classificationName05_2,"relationTypeForClassificationTest02",RelationDirection.FROM);
        Assert.assertTrue(detachClassificationResult);

        attachedClassificationList = _ConceptionKind01.getAttachedClassifications("relationTypeForClassificationTest02",RelationDirection.TWO_WAY);
        Assert.assertNotNull(attachedClassificationList);
        Assert.assertEquals(attachedClassificationList.size(),1);

        Map<String,Object> newEntityValue= new HashMap<>();
        newEntityValue.put("prop1",10000l);
        newEntityValue.put("prop2",190.22d);
        newEntityValue.put("prop3",50);
        newEntityValue.put("prop4","thi is s string");
        newEntityValue.put("prop5","我是中文string");

        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValue);
        ConceptionEntity _ConceptionEntity01 = _ConceptionKind01.newEntity(conceptionEntityValue,false);

        RelationEntity _RelationEntity2 = _ConceptionEntity01.attachClassification(relationAttachInfo,classificationName03);
        Assert.assertNotNull(_RelationEntity2);
        attachedClassificationList = _ConceptionEntity01.getAttachedClassifications("relationTypeForClassificationTest02",RelationDirection.TO);
        Assert.assertNotNull(attachedClassificationList);
        Assert.assertEquals(attachedClassificationList.size(),1);
        Assert.assertEquals(attachedClassificationList.get(0).getClassificationName(),classificationName03);

        Classification rootClassification =coreRealm.getClassification("classification1");
        List<ConceptionKind> relatedConceptionKindList = rootClassification.getRelatedConceptionKinds("relationTypeForClassificationTest02",RelationDirection.TO,true,3);
        Assert.assertNotNull(relatedConceptionKindList);
        Assert.assertEquals(relatedConceptionKindList.size(),0);
        //Assert.assertEquals(relatedConceptionKindList.get(0).getConceptionKindName(),"testConceptionKindForClassification");
        relatedConceptionKindList = rootClassification.getRelatedConceptionKinds("relationTypeForClassificationTest02",RelationDirection.TO,true,2);
        Assert.assertNotNull(relatedConceptionKindList);
        Assert.assertEquals(relatedConceptionKindList.size(),0);
        relatedConceptionKindList = rootClassification.getRelatedConceptionKinds("relationTypeForClassificationTest02",RelationDirection.FROM,true,3);
        Assert.assertNotNull(relatedConceptionKindList);
        Assert.assertEquals(relatedConceptionKindList.size(),1);

        AttributesViewKind targetAttributesViewKind = coreRealm.createAttributesViewKind("testAttributesViewKindForClassification","testAttributesViewKindForClassificationDesc",null);
        relationAttachInfo.setRelationDirection(RelationDirection.FROM);
        resultRelationEntity01 = targetAttributesViewKind.attachClassification(relationAttachInfo,classificationName05_1_1);
        Assert.assertNotNull(resultRelationEntity01);
        List<AttributesViewKind> relatedAttributesViewKindList = rootClassification.getRelatedAttributesViewKinds("relationTypeForClassificationTest02",RelationDirection.FROM,true,3);
        Assert.assertNotNull(relatedAttributesViewKindList);
        Assert.assertEquals(relatedAttributesViewKindList.size(),0);
        //Assert.assertEquals(relatedAttributesViewKindList.get(0).getAttributesViewKindName(),"testAttributesViewKindForClassification");
        relatedAttributesViewKindList = rootClassification.getRelatedAttributesViewKinds("relationTypeForClassificationTest02",RelationDirection.FROM,true,2);
        Assert.assertNotNull(relatedAttributesViewKindList);
        Assert.assertEquals(relatedAttributesViewKindList.size(),0);
        relatedAttributesViewKindList = rootClassification.getRelatedAttributesViewKinds("relationTypeForClassificationTest02",RelationDirection.TO,true,3);
        Assert.assertNotNull(relatedAttributesViewKindList);
        Assert.assertEquals(relatedAttributesViewKindList.size(),1);

        RelationKind _RelationKind01 = coreRealm.getRelationKind("testRelationKindForClassification");
        if(_RelationKind01 != null){
            coreRealm.removeRelationKind("testRelationKindForClassification",true);
        }
        _RelationKind01 = coreRealm.getRelationKind("testRelationKindForClassification");
        if(_RelationKind01 == null){
            _RelationKind01 = coreRealm.createRelationKind("testRelationKindForClassification","testRelationKindForClassification+中文描述");
            Assert.assertNotNull(_RelationKind01);
        }
        resultRelationEntity01 = _RelationKind01.attachClassification(relationAttachInfo,classificationName05_1_1);
        Assert.assertNotNull(resultRelationEntity01);
        List<RelationKind> relatedRelationKindList = rootClassification.getRelatedRelationKinds("relationTypeForClassificationTest02",RelationDirection.FROM,true,3);
        Assert.assertNotNull(relatedRelationKindList);
        Assert.assertEquals(relatedRelationKindList.size(),0);
        //Assert.assertEquals(relatedRelationKindList.get(0).getRelationKindName(),"testRelationKindForClassification");
        relatedRelationKindList = rootClassification.getRelatedRelationKinds("relationTypeForClassificationTest02",RelationDirection.FROM,true,2);
        Assert.assertNotNull(relatedRelationKindList);
        Assert.assertEquals(relatedRelationKindList.size(),0);
        relatedRelationKindList = rootClassification.getRelatedRelationKinds("relationTypeForClassificationTest02",RelationDirection.TO,true,3);
        Assert.assertNotNull(relatedRelationKindList);
        Assert.assertEquals(relatedRelationKindList.size(),1);

        AttributeKind targetAttributeKind = coreRealm.createAttributeKind("testAttributeKindForClassification","testAttributeKindForClassificationDesc",AttributeDataType.BOOLEAN);
        relationAttachInfo.setRelationDirection(RelationDirection.FROM);
        resultRelationEntity01 = targetAttributeKind.attachClassification(relationAttachInfo,classificationName05_1_1);
        Assert.assertNotNull(resultRelationEntity01);
        List<AttributeKind> relatedAttributeKindList = rootClassification.getRelatedAttributeKinds("relationTypeForClassificationTest02",RelationDirection.FROM,true,3);
        Assert.assertNotNull(relatedAttributeKindList);
        Assert.assertEquals(relatedAttributeKindList.size(),0);
        //Assert.assertEquals(relatedAttributeKindList.get(0).getAttributeKindName(),"testAttributeKindForClassification");
        relatedAttributeKindList = rootClassification.getRelatedAttributeKinds("relationTypeForClassificationTest02",RelationDirection.FROM,true,2);
        Assert.assertNotNull(relatedAttributeKindList);
        Assert.assertEquals(relatedAttributeKindList.size(),0);
        relatedAttributeKindList = rootClassification.getRelatedAttributeKinds("relationTypeForClassificationTest02",RelationDirection.TO,true,3);
        Assert.assertNotNull(relatedAttributeKindList);
        Assert.assertEquals(relatedAttributeKindList.size(),1);

        Map<String,Object> newEntityValue2= new HashMap<>();
        newEntityValue2.put("propA",50000l);
        for(int i=0;i<10;i++){
            ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValue2);
            ConceptionEntity _ConceptionEntity01_2 = _ConceptionKind01.newEntity(conceptionEntityValue2,false);
            _ConceptionEntity01_2.attachClassification(relationAttachInfo,"classification1");
        }

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setEntityKind("testConceptionKindForClassification");

        List<ConceptionEntity> relatedEntitiesList =  rootClassification.getRelatedConceptionEntities("relationTypeForClassificationTest02",RelationDirection.FROM,queryParameters,true,4);
        Assert.assertNotNull(relatedEntitiesList);
        Assert.assertEquals(relatedEntitiesList.size(),1);
        for(ConceptionEntity currentConceptionEntity:relatedEntitiesList){
            Assert.assertEquals(currentConceptionEntity.getConceptionKindName(),"testConceptionKindForClassification");
        }

        queryParameters.setResultNumber(5);
        relatedEntitiesList = rootClassification.getRelatedConceptionEntities("relationTypeForClassificationTest02",RelationDirection.FROM,queryParameters,true,4);
        Assert.assertNotNull(relatedEntitiesList);
        Assert.assertEquals(relatedEntitiesList.size(),1);

        queryParameters.setDefaultFilteringItem(new EqualFilteringItem("propA", 60000l));
        relatedEntitiesList = rootClassification.getRelatedConceptionEntities("relationTypeForClassificationTest02",RelationDirection.FROM,queryParameters,true,4);
        Assert.assertNotNull(relatedEntitiesList);
        Assert.assertEquals(relatedEntitiesList.size(),0);

        for(int i=0;i<10;i++){
            ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValue2);
            ConceptionEntity _ConceptionEntity01_2 = _ConceptionKind01.newEntity(conceptionEntityValue2,false);
            _ConceptionEntity01_2.attachClassification(relationAttachInfo,"classification5_1_1");
        }

        queryParameters = new QueryParameters();
        queryParameters.setEntityKind("testConceptionKindForClassification");

        relatedEntitiesList =  rootClassification.getRelatedConceptionEntities("relationTypeForClassificationTest02",RelationDirection.FROM,queryParameters,true,4);
        Assert.assertNotNull(relatedEntitiesList);
        Assert.assertEquals(relatedEntitiesList.size(),1);

        relatedEntitiesList =  rootClassification.getRelatedConceptionEntities("relationTypeForClassificationTest02",RelationDirection.FROM,queryParameters,true,2);
        Assert.assertNotNull(relatedEntitiesList);
        Assert.assertEquals(relatedEntitiesList.size(),1);

        ConceptionKind _ConceptionKind02 = coreRealm.getConceptionKind("testConceptionKindForClassification2");
        if(_ConceptionKind02 != null){
            coreRealm.removeConceptionKind("testConceptionKindForClassification2",true);
        }
        _ConceptionKind02 = coreRealm.getConceptionKind("testConceptionKindForClassification2");
        if(_ConceptionKind02 == null){
            _ConceptionKind02 = coreRealm.createConceptionKind("testConceptionKindForClassification2","TestConceptionKindADesc+中文描述");
            Assert.assertNotNull(_ConceptionKind02);
        }
        for(int i=0;i<5;i++){
            ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValue2);
            ConceptionEntity _ConceptionEntity01_2 = _ConceptionKind02.newEntity(conceptionEntityValue2,false);
            _ConceptionEntity01_2.attachClassification(relationAttachInfo,"classification1");
        }
        relatedEntitiesList = rootClassification.getRelatedConceptionEntities("relationTypeForClassificationTest02",RelationDirection.FROM,null,true,2);
        Assert.assertNotNull(relatedEntitiesList);
        Assert.assertEquals(relatedEntitiesList.size(),1);

        for(int i=0;i<5;i++){
            relationAttachInfo.setRelationKind("relationTypeForClassificationTest03");
            ConceptionEntityValue conceptionEntityValue2 = new ConceptionEntityValue(newEntityValue2);
            ConceptionEntity _ConceptionEntity01_2 = _ConceptionKind02.newEntity(conceptionEntityValue2,false);
            _ConceptionEntity01_2.attachClassification(relationAttachInfo,"classification1");
        }

        relatedEntitiesList = rootClassification.getRelatedConceptionEntities("relationTypeForClassificationTest02",RelationDirection.FROM,null,true,2);
        Assert.assertNotNull(relatedEntitiesList);
        Assert.assertEquals(relatedEntitiesList.size(),1);

        relatedEntitiesList = rootClassification.getRelatedConceptionEntities(null,RelationDirection.FROM,null,true,2);
        Assert.assertNotNull(relatedEntitiesList);
        Assert.assertEquals(relatedEntitiesList.size(),1);

        Classification relatedClassification = attachedClassificationList.get(0);
        relatedClassification.addAttribute("FireProtectionZoneDisplayColor","#CE0000");
        Assert.assertEquals(relatedClassification.getAttribute("FireProtectionZoneDisplayColor").getAttributeValue(),"#CE0000");

        Assert.assertEquals(relatedClassification.getClassificationDesc(),"classification3Desc");
        boolean updateDescResult = relatedClassification.updateClassificationDesc("classification3DescUPD");
        Assert.assertTrue(updateDescResult);
        Assert.assertEquals(relatedClassification.getClassificationDesc(),"classification3DescUPD");
        Assert.assertEquals(coreRealm.getClassification(relatedClassification.getClassificationName()).getClassificationDesc(),"classification3DescUPD");

        Classification childClassificationForRename = relatedClassification.createChildClassification("childClassificationForRename","childClassificationForRenameDesc");
        boolean renameResult = childClassificationForRename.updateClassificationName("NOT_EXIST_NAME");
        Assert.assertTrue(renameResult);
        Assert.assertEquals(childClassificationForRename.getClassificationName(),"NOT_EXIST_NAME");

        boolean exceptionShouldBeCaught2 = false;
        try{
            childClassificationForRename.updateClassificationName("NOT_EXIST_NAME");
        }catch(CoreRealmServiceRuntimeException e){
            exceptionShouldBeCaught2 = true;
        }
        Assert.assertTrue(exceptionShouldBeCaught2);

        boolean removeFlag = relatedClassification.removeChildClassification("NOT_EXIST_NAME");
        Assert.assertTrue(removeFlag);

        Classification levelTestClassification = coreRealm.createClassification("firstLevelCF_2",classificationName01+"Desc");
        Classification childClassification2 = levelTestClassification.createChildClassification("secondLevelCF_2","secondLevelCFDesc");
        Classification childClassification3 = childClassification2.createChildClassification("thirdLevelCF_2","thirdLevelCFDesc");
        List<String> pathClassificationDescriptions0 = new ArrayList<>();

        pathClassificationDescriptions0.add("secondLevelCFDesc");
        pathClassificationDescriptions0.add("thirdLevelCFDesc");
        List<Classification> resultClassifications0 = levelTestClassification.getOffspringClassificationsByPathDescription(pathClassificationDescriptions0);
        Assert.assertEquals(resultClassifications0.size(),1);
        Assert.assertEquals(resultClassifications0.get(0).getClassificationName(),childClassification3.getClassificationName());

        List<String> pathClassificationDescriptions = new ArrayList<>();
        pathClassificationDescriptions.add("classification1Desc1");
        pathClassificationDescriptions.add("classification1Desc2");
        pathClassificationDescriptions.add("classification1Desc3");
        pathClassificationDescriptions.add("classification1Desc4");
        pathClassificationDescriptions.add("classification1Desc5");
        List<Classification> resultClassifications = levelTestClassification.getOffspringClassificationsByPathDescription(pathClassificationDescriptions);
        Assert.assertEquals(resultClassifications.size(),0);

        coreRealm.removeClassification(levelTestClassification.getClassificationName());
        coreRealm.removeClassification(childClassification2.getClassificationName());
        coreRealm.removeClassification(childClassification3.getClassificationName());

        coreRealm.closeGlobalSession();
    }
}
