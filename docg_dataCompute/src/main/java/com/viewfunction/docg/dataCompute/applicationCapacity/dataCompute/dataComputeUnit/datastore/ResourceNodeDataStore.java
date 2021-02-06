package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataStorageMetaInfo;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataClassTypeNotMatchedException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteBiPredicate;

import javax.cache.Cache;
import java.util.*;

public class ResourceNodeDataStore<K, V> {

    private Ignite invokerIgnite;

    private IgniteCache<K, V> cache;

    private Class keyClass;

    private Class valueClass;

    private boolean needCheckKeyClassType=true;

    private boolean needCheckValueClassType=true;

    public ResourceNodeDataStore(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;

        CacheConfiguration currentCacheConfig=this.cache.getConfiguration(CacheConfiguration.class);
        this.keyClass=currentCacheConfig.getKeyType();
        this.valueClass=currentCacheConfig.getValueType();
        this.setClassTypeCheckFlag();
    }

    public ResourceNodeDataStore(Ignite invokerIgnite, String cacheName, CacheMode cacheMode){
        this.invokerIgnite=invokerIgnite;
        CacheConfiguration<K, V> cacheCfg = new CacheConfiguration<>(cacheName);
        if(cacheMode.equals(CacheMode.PARTITIONED)){
            //need use backup to relocate data if container connectome node is closed
            int defaultBackupNumber=1;
            String dataStoreBackupNumberStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreBackupsNumber");
            if(dataStoreBackupNumberStr!=null){
                defaultBackupNumber=Integer.parseInt(dataStoreBackupNumberStr);
            }
            cacheCfg.setBackups(defaultBackupNumber);
        }
        cacheCfg.setSqlSchema(cacheName.toUpperCase());
        cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        String dataStoreAtomicityModeStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreAtomicityMode");
        if(dataStoreAtomicityModeStr!=null){
            if(dataStoreAtomicityModeStr.equals(""+CacheAtomicityMode.ATOMIC)){
                cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
            }
            if(dataStoreAtomicityModeStr.equals(""+CacheAtomicityMode.TRANSACTIONAL)){
                cacheCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
            }
        }
        cacheCfg.setCacheMode(cacheMode);
        String dataStoreRegionNameStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreRegionName");
        cacheCfg.setDataRegionName(dataStoreRegionNameStr);
        //If the data store partitions number is to height, it will leading use lot's of resource even on small
        // workload(such as in spark use much tasks for small work load),Ignite use 1024 by default.
        // Use dataStorePartitionsNumber to control this attribute
        //int defaultDataStorePartitionsNumber=1024;
        String dataStorePartitionsNumberStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStorePartitionsNumber");
        if(dataStorePartitionsNumberStr!=null){
            int dataStorePartitionsNumber=Integer.parseInt(dataStorePartitionsNumberStr);
            cacheCfg.setAffinity(new RendezvousAffinityFunction(false, dataStorePartitionsNumber));
        }
        this.cache = this.invokerIgnite.createCache(cacheCfg);

        CacheConfiguration currentCacheConfig=this.cache.getConfiguration(CacheConfiguration.class);
        this.keyClass=currentCacheConfig.getKeyType();
        this.valueClass=currentCacheConfig.getValueType();
        this.setClassTypeCheckFlag();
    }

    public ResourceNodeDataStore(Ignite invokerIgnite, String cacheName, CacheMode cacheMode, Class keyType, Class valueType){
        this.invokerIgnite=invokerIgnite;
        CacheConfiguration<K, V> cacheCfg = new CacheConfiguration<>(cacheName);
        if(cacheMode.equals(CacheMode.PARTITIONED)){
            //need use backup to relocate data if container connectome node is closed
            int defaultBackupNumber=1;
            String dataStoreBackupNumberStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreBackupsNumber");
            if(dataStoreBackupNumberStr!=null){
                defaultBackupNumber=Integer.parseInt(dataStoreBackupNumberStr);
            }
            cacheCfg.setBackups(defaultBackupNumber);
        }
        if(keyType!=null&&valueType!=null){
            cacheCfg.setTypes(keyType,valueType);
            cacheCfg.setIndexedTypes(keyType,valueType);
        }
        //cacheCfg.setSqlSchema(cacheName.toUpperCase());
        cacheCfg.setSqlSchema("PUBLIC");
        cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        String dataStoreAtomicityModeStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreAtomicityMode");

        if(dataStoreAtomicityModeStr!=null){
            if(dataStoreAtomicityModeStr.equals(""+CacheAtomicityMode.ATOMIC)){
                cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
            }
            if(dataStoreAtomicityModeStr.equals(""+CacheAtomicityMode.TRANSACTIONAL)){
                cacheCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
            }
        }
        cacheCfg.setCacheMode(cacheMode);
        String dataStoreRegionNameStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreRegionName");
        cacheCfg.setDataRegionName(dataStoreRegionNameStr);
        //If the data store partitions number is to height, it will leading use lot's of resource even on small
        // workload(such as in spark use much tasks for small work load),Ignite use 1024 by default.
        // Use dataStorePartitionsNumber to control this attribute
        //int defaultDataStorePartitionsNumber=1024;
        String dataStorePartitionsNumberStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStorePartitionsNumber");
        if(dataStorePartitionsNumberStr!=null){
            int dataStorePartitionsNumber=Integer.parseInt(dataStorePartitionsNumberStr);
            cacheCfg.setAffinity(new RendezvousAffinityFunction(false, dataStorePartitionsNumber));
        }
        this.cache = this.invokerIgnite.createCache(cacheCfg);

        this.keyClass=keyType;
        this.valueClass=valueType;
        this.setClassTypeCheckFlag();
    }

    private void setClassTypeCheckFlag(){
        if(this.keyClass.equals(Object.class)){
            this.needCheckKeyClassType=false;
        }else{
            this.needCheckKeyClassType=true;
        }
        if(this.valueClass.equals(Object.class)){
            this.needCheckValueClassType=false;
        }else{
            this.needCheckValueClassType=true;
        }
    }

    private boolean validateKeyType(Object key){
        if(this.needCheckKeyClassType){
            if(this.keyClass.isAssignableFrom(key.getClass())){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

    private boolean validateValueType(Object value){
        if(this.needCheckValueClassType){
            if(this.valueClass.isAssignableFrom(value.getClass())){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

    public DataStorageMetaInfo getDataStoreMetaInfo(){
        CacheConfiguration currentCacheConfig=this.cache.getConfiguration(CacheConfiguration.class);
        DataStorageMetaInfo dataStorageMetaInfo =new DataStorageMetaInfo();
        dataStorageMetaInfo.setPrimaryDataCount(this.cache.size(CachePeekMode.PRIMARY));
        dataStorageMetaInfo.setBackupDataCount(this.cache.size(CachePeekMode.BACKUP));
        dataStorageMetaInfo.setTotalDataCount(this.cache.size(CachePeekMode.ALL));
        dataStorageMetaInfo.setStoreBackupNumber(currentCacheConfig.getBackups());
        CacheMode currentStoreCacheMode=currentCacheConfig.getCacheMode();
        String dataStoreMode="UNKNOWN";
        switch(currentStoreCacheMode){
            case PARTITIONED:dataStoreMode="Grid Singleton";break;
            case LOCAL:dataStoreMode="Connectome Local";break;
            case REPLICATED:dataStoreMode="Grid PerNode";break;
        }
        dataStorageMetaInfo.setDataStoreMode(dataStoreMode);
        dataStorageMetaInfo.setAtomicityMode(""+currentCacheConfig.getAtomicityMode());
        dataStorageMetaInfo.setSqlSchema(""+currentCacheConfig.getSqlSchema());
        dataStorageMetaInfo.setKeyClass(currentCacheConfig.getKeyType());
        dataStorageMetaInfo.setValueClass(currentCacheConfig.getValueType());
        return dataStorageMetaInfo;
    }

    public void addData(K key, V value) throws DataClassTypeNotMatchedException {
        if(validateKeyType(key)&&validateValueType(value)) {
            this.cache.put(key,value);
        }else{
            throw new DataClassTypeNotMatchedException();
        }
    }

    public void addData(Map<? extends K,? extends V> map)throws DataClassTypeNotMatchedException {
        if(!map.isEmpty()){
            if(this.needCheckKeyClassType){
                Iterator keyIterator=map.keySet().iterator();
                while(keyIterator.hasNext()){
                    if(!validateKeyType(keyIterator.next())){
                        throw new DataClassTypeNotMatchedException();
                    }
                }
            }
            if(this.needCheckValueClassType){
                Iterator valueIterator=map.values().iterator();
                while(valueIterator.hasNext()){
                    if(!validateValueType(valueIterator.next())){
                        throw new DataClassTypeNotMatchedException();
                    }
                }
            }
            this.cache.putAll(map);
        }
    }

    public boolean addDataIfNotExist(K key, V value) throws DataClassTypeNotMatchedException {
        if(validateKeyType(key)&&validateValueType(value)){
            return this.cache.putIfAbsent(key,value);
        }else{
            throw new DataClassTypeNotMatchedException();
        }
    }

    public boolean updateData(K key, V value)throws DataClassTypeNotMatchedException {
        if(validateKeyType(key)&&validateValueType(value)){
            return this.cache.replace(key, value);
        }else{
            throw new DataClassTypeNotMatchedException();
        }
    }

    public boolean updateDataIfOldValueMatched(K key, V oldValue,V newValue)throws DataClassTypeNotMatchedException {
        if(validateKeyType(key)&&validateValueType(oldValue)&&validateValueType(newValue)){
            return this.cache.replace(key,oldValue,newValue);
        }else{
            throw new DataClassTypeNotMatchedException();
        }
    }

    public boolean deleteData(K key)throws DataClassTypeNotMatchedException {
        if(validateKeyType(key)){
            return this.cache.remove(key);
        }else{
            throw new DataClassTypeNotMatchedException();
        }
    }

    public void deleteData(Set<? extends K> keys)throws DataClassTypeNotMatchedException {
        if(!keys.isEmpty()){
            if(this.needCheckKeyClassType) {
                Iterator keyIterator = keys.iterator();
                while (keyIterator.hasNext()) {
                    if (!validateKeyType(keyIterator.next())) {
                        throw new DataClassTypeNotMatchedException();
                    }
                }
            }
            this.cache.removeAll(keys);
        }
    }

    public boolean deleteDataIfValueMatched(K key,V value)throws DataClassTypeNotMatchedException {
        if(validateKeyType(key)&&validateValueType(value)){
            return this.cache.remove(key, value);
        }else{
            throw new DataClassTypeNotMatchedException();
        }
    }

    /* lock function only support in TRANSACTIONAL AtomicityMode,not support yet */
    /*
    public Lock getDataLock(K key){
        return this.cache.lock(key);
    }

    public Lock getDataLock(Collection<? extends K> keys){
        return this.cache.lockAll(keys);
    }
    */

    public V getData(K key){
        return this.cache.get(key);
    }

    public void emptyDataStore(){
        this.cache.removeAll();
    }

    public List<DataObject> listAllData(){
        List<DataObject> queryResultDataObjectList=new ArrayList<>();
        Iterator<Cache.Entry<K,V>> iterator= this.cache.iterator();
        while(iterator.hasNext()){
            Cache.Entry<K,V> currentEntry=iterator.next();
            K keyObject=currentEntry.getKey();
            V valueObject=currentEntry.getValue();
            queryResultDataObjectList.add(new DataObject(keyObject,valueObject));
        }
        return queryResultDataObjectList;
    }

    public List<DataObject> queryData(final BinaryDataMatchCondition dataMatchCondition){
        List<DataObject> queryResultDataObjectList=new ArrayList<>();

        ScanQuery<BinaryObject, BinaryObject> scan = new ScanQuery<>(
                new IgniteBiPredicate<BinaryObject, BinaryObject>() {
                    @Override
                    public boolean apply(BinaryObject keyBinaryObject, BinaryObject valueBinaryObject) {
                        boolean matchResult=dataMatchCondition.match(keyBinaryObject,valueBinaryObject);
                        return matchResult;
                    }
                }
        );
        List<Cache.Entry<BinaryObject,BinaryObject>> resultList=this.cache.withKeepBinary().query(scan).getAll();
        if(resultList!=null){
            for(Cache.Entry<BinaryObject,BinaryObject> resultDatMap:resultList){
                BinaryObject keyObject=resultDatMap.getKey();
                BinaryObject valueObject=resultDatMap.getValue();
                queryResultDataObjectList.add(new DataObject(keyObject.deserialize(),valueObject.deserialize()));
            }
        }
        return queryResultDataObjectList;
    }

    public List<DataObject> queryData(final GeneralDataMatchCondition generalDataMatchCondition){
        List<DataObject> queryResultDataObjectList=new ArrayList<>();

        ScanQuery<K, V> scan = new ScanQuery<>(
                new IgniteBiPredicate<K, V>() {
                    @Override
                    public boolean apply(K keyBinaryObject, V valueBinaryObject) {
                        boolean matchResult=generalDataMatchCondition.match(keyBinaryObject,valueBinaryObject);
                        return matchResult;
                    }
                }
        );
        List<Cache.Entry<K,V>> resultList=this.cache.query(scan).getAll();
        if(resultList!=null){
            for(Cache.Entry<K,V> resultDatMap:resultList){
                K keyObject=resultDatMap.getKey();
                V valueObject=resultDatMap.getValue();
                queryResultDataObjectList.add(new DataObject(keyObject,valueObject));
            }
        }
        return queryResultDataObjectList;
    }

    public List<DataObject> queryData(SqlQueryCondition<K, V> sqlQueryCondition){
        List<DataObject> queryResultDataObjectList=new ArrayList<>();
        List<Cache.Entry<K,V>> resultList=this.cache.query(sqlQueryCondition.getIgniteSqlQuery()).getAll();
        if(resultList!=null){
            for(Cache.Entry<K,V> resultDatMap:resultList){
                K keyObject=resultDatMap.getKey();
                V valueObject=resultDatMap.getValue();
                queryResultDataObjectList.add(new DataObject(keyObject,valueObject));
            }
        }
        return queryResultDataObjectList;
    }

    public List<DataObject> queryData(TextContainsQueryCondition<K, V> textContainsQueryCondition){
        List<DataObject> queryResultDataObjectList=new ArrayList<>();
        List<Cache.Entry<K,V>> resultList=this.cache.query(textContainsQueryCondition.getIgniteTextQuery()).getAll();
        if(resultList!=null){
            for(Cache.Entry<K,V> resultDatMap:resultList){
                K keyObject=resultDatMap.getKey();
                V valueObject=resultDatMap.getValue();
                queryResultDataObjectList.add(new DataObject(keyObject,valueObject));
            }
        }
        return queryResultDataObjectList;
    }

    public List<List<?>> queryAggregationFields(SqlAggregationCondition sqlAggregationCondition){
        QueryCursor<List<?>> cursor = this.cache.query(sqlAggregationCondition.getIgniteSqlFieldsQuery());
        List<List<?>> result = cursor.getAll();
        return result;
    }

    public List<DataObject> queryDataAndMonitorFollowingChange(DataChangeMonitor<K, V> dataChangeMonitor){
        List<DataObject> queryResultDataObjectList=new ArrayList<>();
        List<Cache.Entry<K,V>> resultList=this.cache.query(dataChangeMonitor.getIgniteContinuousQuery()).getAll();
        if(resultList!=null){
            for(Cache.Entry<K,V> resultDatMap:resultList){
                K keyObject=resultDatMap.getKey();
                V valueObject=resultDatMap.getValue();
                queryResultDataObjectList.add(new DataObject(keyObject,valueObject));
            }
        }
        return queryResultDataObjectList;
    }
}
