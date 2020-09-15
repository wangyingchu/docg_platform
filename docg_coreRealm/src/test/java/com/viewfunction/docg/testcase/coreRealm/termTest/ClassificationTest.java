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

        coreRealm.createClassification(classificationName05_1,classificationName05_1+"Desc",classificationName05);
        coreRealm.createClassification(classificationName05_2,classificationName05_2+"Desc",classificationName05);

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

        Assert.assertEquals(tree01.size(),6);

        Assert.assertEquals(tree01.numOfChildren(((Neo4JClassificationImpl)_Classification01).getClassificationUID()),4);

    }
}
