package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Date;

public class MetaAttributeFeatureSupportableTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKindForMetaAttributeFeature";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for MetaAttributeFeatureSupportableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testMetaAttributeFeatureSupportableFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind(testConceptionKindName,true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind(testConceptionKindName);
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind(testConceptionKindName,"TestMetaAttributeFeatureDesc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindName(),testConceptionKindName);
            Assert.assertEquals(_ConceptionKind01.getConceptionKindDesc(),"TestMetaAttributeFeatureDesc+中文描述");
        }

        Assert.assertNotNull(_ConceptionKind01.getDataOrigin());
        Assert.assertNull(_ConceptionKind01.getCreatorId());

        Date createdDateTime = _ConceptionKind01.getCreateDateTime();
        Assert.assertNotNull(createdDateTime);

        Date lastModifyDateTime = _ConceptionKind01.getLastModifyDateTime();
        Assert.assertNotNull(lastModifyDateTime);

        boolean updateResult = _ConceptionKind01.updateLastModifyDateTime();
        Assert.assertTrue(updateResult);
        Date lastModifyDateTime2 = _ConceptionKind01.getLastModifyDateTime();
        Assert.assertNotNull(lastModifyDateTime2);
        Assert.assertTrue(lastModifyDateTime2.getTime() > lastModifyDateTime.getTime());

        updateResult = _ConceptionKind01.updateCreatorId("creatorID001");
        Assert.assertTrue(updateResult);
        String newCreatorID = _ConceptionKind01.getCreatorId();
        Assert.assertEquals(newCreatorID,"creatorID001");

        String dataOrigin = _ConceptionKind01.getDataOrigin();
        updateResult = _ConceptionKind01.updateDataOrigin(dataOrigin+"NewValue");
        Assert.assertTrue(updateResult);

        String newDataOrigin = _ConceptionKind01.getDataOrigin();
        Assert.assertEquals(newDataOrigin,dataOrigin+"NewValue");

        Date newCreatDate = _ConceptionKind01.getCreateDateTime();
        Assert.assertEquals(createdDateTime,newCreatDate);

        RelationKind targetRelationKind01 = coreRealm.getRelationKind("relationKind01");
        //Assert.assertNotNull(targetRelationKind01.getCreateDateTime());
        //Assert.assertNotNull(targetRelationKind01.getLastModifyDateTime());
        //Assert.assertNotNull(targetRelationKind01.getDataOrigin());
    }
}
