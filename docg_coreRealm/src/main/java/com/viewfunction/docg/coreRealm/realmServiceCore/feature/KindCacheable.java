package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.cache.ResourceCacheHolder;

public interface KindCacheable{

    public enum CacheOperationType {INSERT, UPDATE, DELETE}

    public default void op(){
        ResourceCacheHolder resourceCacheHolder = com.viewfunction.docg.coreRealm.realmServiceCore.util.cache.ResourceCacheHolder.getInstance();
        //resourceCacheHolder.getOrCreateCache()
    }

    public default boolean executeAttributeKindCacheOperation(AttributeKind attributeKind, CacheOperationType cacheOperationType) {
        return true;
    }

    public default boolean executeAttributesViewKindCacheOperation(AttributesViewKind attributesViewKind, CacheOperationType cacheOperationType) {
        return true;
    }

    public default boolean executeClassificationKindCacheOperation(ClassificationKind classificationKind, CacheOperationType cacheOperationType) {
        return true;
    }

    public default boolean executeConceptionKindCacheOperation(ConceptionKind conceptionKind, CacheOperationType cacheOperationType) {
        return true;
    }

    public default boolean executeRelationAttachKindCacheOperation(RelationAttachKind relationAttachKind, CacheOperationType cacheOperationType) {
        return true;
    }

    public default boolean executeRelationKindCacheOperation(RelationKind relationKind, CacheOperationType cacheOperationType) {
        return true;
    }

}
