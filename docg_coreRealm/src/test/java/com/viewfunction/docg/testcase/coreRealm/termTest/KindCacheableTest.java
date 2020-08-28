package com.viewfunction.docg.testcase.coreRealm.termTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.KindCacheable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject.AttributeKindVO;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject.AttributesViewKindVO;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject.ConceptionKindVO;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
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

        AttributesViewKind attributesViewKind01 = coreRealm.createAttributesViewKind("attributesViewKind_01","targetAttributesViewKindADesc",null);
        Assert.assertNotNull(attributesViewKind01);

        ResourceCache<String, AttributesViewKindVO> cache2 = resourceCacheHolder.getCache(KindCacheable.ATTRIBUTES_VIEW_KIND_CACHE,String.class,AttributesViewKindVO.class);
        Assert.assertTrue(cache2.containsCacheItem(attributesViewKind01.getAttributesViewKindUID()));

        AttributesViewKindVO targetAttributesViewKind = cache2.getCacheItem(attributesViewKind01.getAttributesViewKindUID());
        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindUID(),attributesViewKind01.getAttributesViewKindUID());
        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindName(),"attributesViewKind_01");
        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindDesc(),"targetAttributesViewKindADesc");
        Assert.assertEquals(targetAttributesViewKind.getAttributesViewKindDataForm(), AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE);

        coreRealm.removeAttributesViewKind(attributesViewKind01.getAttributesViewKindUID());
        Assert.assertFalse(cache2.containsCacheItem(targetAttributesViewKind.getAttributesViewKindUID()));

        ConceptionKind _ConceptionKind01 = coreRealm.getConceptionKind("conceptionKind_01");
        if(_ConceptionKind01 != null){
            coreRealm.removeConceptionKind("conceptionKind_01",true);
        }
        _ConceptionKind01 = coreRealm.getConceptionKind("conceptionKind_01");
        if(_ConceptionKind01 == null){
            _ConceptionKind01 = coreRealm.createConceptionKind("conceptionKind_01","TestConceptionKindADesc+中文描述");
            Assert.assertNotNull(_ConceptionKind01);
        }

        ResourceCache<String, ConceptionKindVO> cache3 = resourceCacheHolder.getCache(KindCacheable.CONCEPTION_KIND_CACHE,String.class,ConceptionKindVO.class);
        Assert.assertTrue(cache3.containsCacheItem(_ConceptionKind01.getConceptionKindName()));
        ConceptionKindVO targetConceptionKind = cache3.getCacheItem(_ConceptionKind01.getConceptionKindName());
        Assert.assertEquals(targetConceptionKind.getConceptionKindDesc(),_ConceptionKind01.getConceptionKindDesc());

        coreRealm.removeConceptionKind("conceptionKind_01",true);
        Assert.assertFalse(cache3.containsCacheItem(_ConceptionKind01.getConceptionKindName()));



        resourceCacheHolder.shutdownCacheHolder();
    }

}
