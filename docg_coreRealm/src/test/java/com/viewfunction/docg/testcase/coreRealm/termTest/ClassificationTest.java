package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JClassificationImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ClassificationTest {

    private static String testRealmName = "UNIT_TEST_Realm";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ClassificationTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testClassificationFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

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
        InheritanceTree<Classification> tree02 = _Classification04.getOffspringClassifications();

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
    }
}
