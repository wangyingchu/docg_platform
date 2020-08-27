package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.cache.ResourceCache;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.cache.ResourceCacheHolder;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataValueObject.*;

public interface KindCacheable<K,V>{

    String ATTRIBUTE_KIND_CACHE = "ATTRIBUTE_KIND_CACHE";
    String ATTRIBUTES_VIEW_CACHE = "ATTRIBUTES_VIEW_CACHE";
    String CLASSIFICATION_KIND_CACHE = "CLASSIFICATION_KIND_CACHE";
    String CONCEPTION_KIND_CACHE = "CONCEPTION_KIND_CACHE";
    String RELATION_ATTACH_KIND_CACHE = "RELATION_ATTACH_KIND_CACHE";
    String RELATION_KIND_CACHE = "RELATION_KIND_CACHE";
    enum CacheOperationType {INSERT, UPDATE, DELETE}

    public default void executeAttributeKindCacheOperation(AttributeKind attributeKind, CacheOperationType cacheOperationType) {
        ResourceCacheHolder resourceCacheHolder = com.viewfunction.docg.coreRealm.realmServiceCore.util.cache.ResourceCacheHolder.getInstance();
        ResourceCache<String, AttributeKindVO> cache = resourceCacheHolder.getOrCreateCache(ATTRIBUTE_KIND_CACHE, String.class, AttributeKindVO.class);
        if(attributeKind != null){
            String cacheItemKey = attributeKind.getAttributeKindUID();
            AttributeKindVO attributeKindVO = new AttributeKindVO(attributeKind.getAttributeKindName(),attributeKind.getAttributeKindDesc(),
                    attributeKind.getAttributeDataType(),attributeKind.getAttributeKindUID());
            accessCacheData(cache,cacheOperationType,cacheItemKey,attributeKindVO);
        }
    }

    public default void executeAttributesViewKindCacheOperation(AttributesViewKind attributesViewKind, CacheOperationType cacheOperationType) {
        ResourceCacheHolder resourceCacheHolder = com.viewfunction.docg.coreRealm.realmServiceCore.util.cache.ResourceCacheHolder.getInstance();
        ResourceCache<String, AttributesViewKindVO> cache = resourceCacheHolder.getOrCreateCache(ATTRIBUTES_VIEW_CACHE, String.class, AttributesViewKindVO.class);
        if(attributesViewKind != null){
            String cacheItemKey = attributesViewKind.getAttributesViewKindUID();
            AttributesViewKindVO attributesViewKindVO = new AttributesViewKindVO(attributesViewKind.getAttributesViewKindName(),attributesViewKind.getAttributesViewKindDesc(),
                    attributesViewKind.getAttributesViewKindDataForm(),attributesViewKind.getAttributesViewKindUID());
            accessCacheData(cache,cacheOperationType,cacheItemKey,attributesViewKindVO);
        }
    }

    public default void executeClassificationKindCacheOperation(ClassificationKind classificationKind, CacheOperationType cacheOperationType) {

    }

    public default void executeConceptionKindCacheOperation(ConceptionKind conceptionKind, CacheOperationType cacheOperationType) {

    }

    public default void executeRelationAttachKindCacheOperation(RelationAttachKind relationAttachKind, CacheOperationType cacheOperationType) {

    }

    public default void executeRelationKindCacheOperation(RelationKind relationKind, CacheOperationType cacheOperationType) {

    }

    private void accessCacheData(ResourceCache cache,CacheOperationType cacheOperationType,String cacheItemKey,Object cacheData){
        switch(cacheOperationType){
            case INSERT:
                if(!cache.containsCacheItem(cacheItemKey)){
                    cache.addCacheItem(cacheItemKey,cacheData);
                }
                break;
            case DELETE:
                if(cache.containsCacheItem(cacheItemKey)){
                    cache.removeCacheItem(cacheItemKey);
                }
                break;
            case UPDATE:
                if(cache.containsCacheItem(cacheItemKey)){
                    cache.updateCacheItem(cacheItemKey,cacheData);
                }else{
                    cache.addCacheItem(cacheItemKey,cacheData);
                }
                break;
        }
    }
}
