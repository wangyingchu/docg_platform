package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Map;

public class MetaConfigItemFeatureSupportableTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testAttributesViewKindName = "TestAttributesViewKindForMetaConfigItemFeature";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for MetaConfigItemFeatureSupportableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testMetaConfigItemFeatureSupportableFunction() {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        Assert.assertEquals(coreRealm.getStorageImplTech(), CoreRealmStorageImplTech.NEO4J);
        AttributesViewKind targetAttributesViewKind = coreRealm.createAttributesViewKind(testAttributesViewKindName,"desc",null);

        boolean addConfigItemResult = targetAttributesViewKind.addOrUpdateMetaConfigItem("configItem1",new Date());
        Assert.assertTrue(addConfigItemResult);

        Map<String,Object> metaConfigItemsMap = targetAttributesViewKind.getMetaConfigItems();
        Assert.assertNotNull(metaConfigItemsMap);

        Assert.assertEquals(metaConfigItemsMap.size(),1);
        Assert.assertNotNull(metaConfigItemsMap.get("configItem1"));
        Assert.assertTrue(metaConfigItemsMap.get("configItem1") instanceof Date);

        addConfigItemResult = targetAttributesViewKind.addOrUpdateMetaConfigItem("configItem2",Long.valueOf(10000));
        Assert.assertTrue(addConfigItemResult);

        metaConfigItemsMap = targetAttributesViewKind.getMetaConfigItems();
        Assert.assertEquals(metaConfigItemsMap.size(),2);
        Assert.assertNotNull(metaConfigItemsMap.get("configItem2"));
        Assert.assertEquals(metaConfigItemsMap.get("configItem2"),Long.valueOf(10000));

        Object item2Result = targetAttributesViewKind.getMetaConfigItem("configItem2");
        Assert.assertTrue(item2Result instanceof Long);
        Assert.assertEquals(item2Result,Long.valueOf(10000));

        addConfigItemResult = targetAttributesViewKind.addOrUpdateMetaConfigItem("configItem2",Long.valueOf(50000));
        Assert.assertTrue(addConfigItemResult);

        item2Result = targetAttributesViewKind.getMetaConfigItem("configItem2");
        Assert.assertTrue(item2Result instanceof Long);
        Assert.assertEquals(item2Result,Long.valueOf(50000));

        Object itemNotExistResult = targetAttributesViewKind.getMetaConfigItem("configItemNotExist");
        Assert.assertNull(itemNotExistResult);

        boolean deleteItemResult = targetAttributesViewKind.deleteMetaConfigItem("configItem2");
        Assert.assertTrue(deleteItemResult);
        itemNotExistResult = targetAttributesViewKind.getMetaConfigItem("configItem2");
        Assert.assertNull(itemNotExistResult);

        metaConfigItemsMap = targetAttributesViewKind.getMetaConfigItems();
        Assert.assertNotNull(metaConfigItemsMap);
        Assert.assertEquals(metaConfigItemsMap.size(),1);
    }
}
