package com.viewfunction.docg.coreRealm.realmServiceCore.util.cache;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.ResourceCacheRuntimeException;
import org.ehcache.Cache;

import java.util.*;
import java.util.function.Consumer;

public class ResourceCache<K,V> {

    private Cache<K,V> cacheImpl;
    private boolean isClusterMode;

    public ResourceCache(Cache<K,V> cache,boolean isClusterMode){
        this.cacheImpl=cache;
        this.isClusterMode = isClusterMode;
    }

    public void addCacheItem(K keyData, V valueData){
        this.cacheImpl.put(keyData,valueData);
    }

    public V getCacheItem(K keyData){
        return this.cacheImpl.get(keyData);
    }

    public void loadCacheItems(Map<? extends K,? extends V> itemData){
        this.cacheImpl.putAll(itemData);
    }

    public Set<K> getCacheItemKeys() throws ResourceCacheRuntimeException {
        if(this.isClusterMode){
            ResourceCacheRuntimeException re=new ResourceCacheRuntimeException();
            re.setCauseMessage("getCacheItemKeys() can not used under cluster mode");
            throw re;
        }
        Set<K> resultSet=new HashSet<>();
        Iterator<Cache.Entry<K,V>> cacheIterator=this.cacheImpl.iterator();
        while(cacheIterator.hasNext()){
            Cache.Entry<K,V> currentEntry=cacheIterator.next();
            K currentValue=currentEntry.getKey();
            resultSet.add(currentValue);
        }
        return resultSet;
    }

    public Set<V> getCacheItemValues() throws ResourceCacheRuntimeException{
        if(this.isClusterMode){
            ResourceCacheRuntimeException re=new ResourceCacheRuntimeException();
            re.setCauseMessage("getCacheItemValues() can not used under cluster mode");
            throw re;
        }
        Set<V> resultSet=new HashSet<>();
        Iterator<Cache.Entry<K,V>> cacheIterator=this.cacheImpl.iterator();
        while(cacheIterator.hasNext()){
            Cache.Entry<K,V> currentEntry=cacheIterator.next();
            V currentValue=currentEntry.getValue();
            resultSet.add(currentValue);
        }
        return resultSet;
    }

    public List<V> queryCacheItemValues(ResourceCacheQueryLogic<K,V> queryLogic){
        List<V> resultList=new ArrayList<>();
        this.cacheImpl.forEach(new Consumer<Cache.Entry<K, V>>() {
            @Override
            public void accept(Cache.Entry<K, V> kvEntry) {
                boolean filterResult=queryLogic.filterLogic(kvEntry.getKey(),kvEntry.getValue());
                if(filterResult){
                    resultList.add(kvEntry.getValue());
                }
            }
        });
        return resultList;
    }

    public void emptyCacheItems(){
        this.cacheImpl.clear();
    }

    public void removeCacheItem(K kValue){
        this.cacheImpl.remove(kValue);
    }

    public boolean containsCacheItem(K keyValue){
        return this.cacheImpl.containsKey(keyValue);
    }
}