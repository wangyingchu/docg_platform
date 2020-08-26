package com.viewfunction.docg.coreRealm.realmServiceCore.util.cache;

import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.clustered.client.config.builders.ClusteredStoreConfigurationBuilder;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

import static org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder.clusteredShared;
import static org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder.cluster;
import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;
import static org.ehcache.config.units.MemoryUnit.MB;

import java.net.URI;
import static java.net.URI.create;
import java.time.Duration;

public class ResourceCacheHolder<K, V> {

    private static ResourceCacheHolder singletonResourceCacheHolder;
    private CacheManager _CacheManager;
    private boolean clusterResourceCacheEnabled;
    private String clusterResourceCacheServiceLocationStr;
    private String clusterResourceCacheServiceDefaultResourceIdStr;
    private String clusterResourceCacheServiceShareResourceIdStr;
    private final String CLUSTER_RESOURCE_POOL = "CIM_BUILDIN_CLUSTER_RESOURCE_POOL";

    private ResourceCacheHolder() {
        //init encache
        String clusterResourceCacheEnabledStr = PropertiesHandler.getPropertyValue(PropertiesHandler.CLUSTER_RESOURCE_CACHE_ENABLE);
        this.clusterResourceCacheServiceLocationStr = PropertiesHandler.getPropertyValue(PropertiesHandler.CLUSTER_RESOURCE_CACHE_SERVICE_LOCATION);
        this.clusterResourceCacheServiceDefaultResourceIdStr = PropertiesHandler.getPropertyValue(PropertiesHandler.CLUSTER_RESOURCE_CACHE_SERVICE_DEFAULT_RESOURCE_ID);
        this.clusterResourceCacheServiceShareResourceIdStr = PropertiesHandler.getPropertyValue(PropertiesHandler.CLUSTER_RESOURCE_CACHE_SERVICE_SHARE_RESOURCE_ID);
        this.clusterResourceCacheEnabled = Boolean.valueOf(clusterResourceCacheEnabledStr);
        if (isClusterMode()) {
            URI uri = create(this.clusterResourceCacheServiceLocationStr);
            _CacheManager = newCacheManagerBuilder()
                    .with(
                            cluster(uri)
                                    .autoCreate()
                                    .defaultServerResource(this.clusterResourceCacheServiceDefaultResourceIdStr)
                                    .resourcePool(CLUSTER_RESOURCE_POOL, 256, MemoryUnit.MB, this.clusterResourceCacheServiceShareResourceIdStr))
                    .build(true);
        } else {
            _CacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        }
    }

    public static ResourceCacheHolder getInstance() {
        return createSingletonCacheResourceHolder();
    }

    private static ResourceCacheHolder createSingletonCacheResourceHolder() {
        if (singletonResourceCacheHolder == null) {
            singletonResourceCacheHolder = new ResourceCacheHolder();
        }
        return singletonResourceCacheHolder;
    }

    public ResourceCache<K, V> getOrCreateCache(String cacheName, Class<K> _KClass, Class<V> _VClass) {
        Cache<K, V> targetCache = _CacheManager.getCache(cacheName, _KClass, _VClass);
        if (targetCache == null) {
            if (isClusterMode()) {
                targetCache = _CacheManager.createCache(cacheName, newCacheConfigurationBuilder(_KClass, _VClass,
                        //heap(10000).offheap(30, MB).with(clusteredDedicated(this.clusterResourceCacheServiceResourceIdStr, 40, MemoryUnit.MB)))
                        //heap(10000).offheap(30, MB).with(clusteredShared(CLUSTER_RESOURCE_POOL)))
                        //heap(10000).with(clusteredShared(CLUSTER_RESOURCE_POOL)))
                        ResourcePoolsBuilder.newResourcePoolsBuilder().with(clusteredShared(CLUSTER_RESOURCE_POOL)))
                        .add(ClusteredStoreConfigurationBuilder.withConsistency(Consistency.STRONG))
                        .withSizeOfMaxObjectSize(5, MemoryUnit.KB)
                        .withSizeOfMaxObjectGraph(100000)
                        .withExpiry(ExpiryPolicyBuilder.noExpiration()));
            } else {
                CacheConfiguration<K, V> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(_KClass, _VClass,
                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10000, EntryUnit.ENTRIES).offheap(10, MemoryUnit.MB))
                        .withSizeOfMaxObjectSize(5, MemoryUnit.KB)
                        .withSizeOfMaxObjectGraph(100000)
                        .withExpiry(ExpiryPolicyBuilder.noExpiration())
                        .build();
                targetCache = _CacheManager.createCache(cacheName, cacheConfiguration);
            }
        }
        return new ResourceCache<K, V>(targetCache, isClusterMode());
    }

    public ResourceCache<K, V> getOrCreateCache(String cacheName, Class<K> _KClass, Class<V> _VClass, int expirationInSeconds) {
        Cache<K, V> targetCache = _CacheManager.getCache(cacheName, _KClass, _VClass);
        if (targetCache == null) {
            if (isClusterMode()) {
                targetCache = _CacheManager.createCache(cacheName, newCacheConfigurationBuilder(_KClass, _VClass,
                        //heap(10000).offheap(30, MB).with(clusteredDedicated(this.clusterResourceCacheServiceResourceIdStr, 40, MemoryUnit.MB)))
                        heap(10000).offheap(30, MB).with(clusteredShared(CLUSTER_RESOURCE_POOL)))
                        .add(ClusteredStoreConfigurationBuilder.withConsistency(Consistency.STRONG))
                        .withSizeOfMaxObjectSize(5, MemoryUnit.KB)
                        .withSizeOfMaxObjectGraph(100000)
                        .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(expirationInSeconds))));
            } else {
                CacheConfiguration<K, V> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(_KClass, _VClass,
                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10000, EntryUnit.ENTRIES).offheap(100, MemoryUnit.MB))
                        .withSizeOfMaxObjectSize(5, MemoryUnit.KB)
                        .withSizeOfMaxObjectGraph(100000)
                        .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(expirationInSeconds)))
                        .build();
                targetCache = _CacheManager.createCache(cacheName, cacheConfiguration);
            }
        }
        return new ResourceCache<K, V>(targetCache, isClusterMode());
    }

    public ResourceCache<K, V> getCache(String cacheName, Class<K> _KClass, Class<V> _VClass) {
        Cache<K, V> targetCache = _CacheManager.getCache(cacheName, _KClass, _VClass);
        if (targetCache != null) {
            return new ResourceCache<K, V>(targetCache, isClusterMode());
        } else {
            return null;
        }
    }

    public void removeCache(String cacheName) {
        _CacheManager.removeCache(cacheName);
    }

    public void shutdownCacheHolder() {
        if (_CacheManager != null) {
            _CacheManager.close();
        }
    }

    public final boolean isClusterMode() {
        if (clusterResourceCacheEnabled && clusterResourceCacheServiceLocationStr != null && clusterResourceCacheServiceDefaultResourceIdStr != null && clusterResourceCacheServiceShareResourceIdStr != null) {
            return true;
        } else {
            return false;
        }
    }
}