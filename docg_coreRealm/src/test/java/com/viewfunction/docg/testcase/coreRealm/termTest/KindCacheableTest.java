package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.KindCacheable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject.AttributeKindVO;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.cache.ResourceCache;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.cache.ResourceCacheHolder;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class KindCacheableTest {

    private static String testRealmName = "UNIT_TEST_Realm";
    private static String testConceptionKindName = "TestConceptionKind_03";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for KindCacheableTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testKindCacheableTestFunction() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ResourceCacheHolder resourceCacheHolder = ResourceCacheHolder.getInstance();

        AttributeKind attributeKind01 = coreRealm.createAttributeKind("attributeKind_01","attributeKind01Desc", AttributeDataType.BOOLEAN);
        Assert.assertNotNull(attributeKind01);

        ResourceCache<String, AttributeKindVO> cache = resourceCacheHolder.getCache(KindCacheable.ATTRIBUTE_KIND_CACHE,String.class,AttributeKindVO.class);
        Assert.assertTrue(cache.containsCacheItem(attributeKind01.getAttributeKindUID()));
        AttributeKindVO targetAttributeKind = cache.getCacheItem(attributeKind01.getAttributeKindUID());

        Assert.assertEquals(targetAttributeKind.getAttributeKindUID(),attributeKind01.getAttributeKindUID());
        Assert.assertEquals(targetAttributeKind.getAttributeKindName(),"attributeKind_01");
        Assert.assertEquals(targetAttributeKind.getAttributeKindDesc(),"attributeKind01Desc");
        Assert.assertEquals(targetAttributeKind.getAttributeDataType(),AttributeDataType.BOOLEAN);

        coreRealm.removeAttributeKind(targetAttributeKind.getAttributeKindUID());
        Assert.assertFalse(cache.containsCacheItem(targetAttributeKind.getAttributeKindUID()));



        resourceCacheHolder.shutdownCacheHolder();
    }

}
