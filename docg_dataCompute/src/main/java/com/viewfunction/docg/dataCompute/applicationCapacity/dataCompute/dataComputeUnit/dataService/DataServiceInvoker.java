package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataCubeExistException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DataServiceInvoker implements AutoCloseable{

    private Ignite invokerIgnite;
    private final String TEMPLATE_OPERATION_CACHE = "TEMPLATE_OPERATION_CACHE";

    public void openServiceSession() throws ComputeGridNotActiveException {
        Ignition.setClientMode(true);
        this.invokerIgnite =Ignition.start(DataComputeConfigurationHandler.getDataComputeIgniteConfigurationFilePath());
        UnitIgniteOperationUtil.checkGridActiveStatus(this.invokerIgnite);
    }

    public void closeServiceSession(){
        if(this.invokerIgnite !=null){
            this.invokerIgnite.close();
        }
    }

    //https://ignite.apache.org/docs/latest/sql-reference/ddl#create-table
    //https://ignite.apache.org/docs/latest/SQL/schemas#cache-and-schema-names
    //https://www.ignite-service.cn/doc/java/


    public DataSlice createGridDataSlice(String dataSliceName, String dataSliceGroup, Map<String, Object> filedMap,List<String> primaryKeysList) throws DataCubeExistException{
        confirmDataSliceNotExist(dataSliceName);
        CacheConfiguration<?, ?> cacheCfg = new CacheConfiguration<>(TEMPLATE_OPERATION_CACHE).setSqlSchema(dataSliceGroup);
        IgniteCache<?, ?> cache = this.invokerIgnite.getOrCreateCache(cacheCfg);
        cache.query(new SqlFieldsQuery(
                "CREATE TABLE "+dataSliceName+" (id LONG PRIMARY KEY, name VARCHAR) " +
                        "WITH \"CACHE_NAME="+dataSliceName+
                        ",DATA_REGION=Default_DataStore_Region," +
                        "TEMPLATE=replicated\"")).getAll();
        this.invokerIgnite.destroyCache(TEMPLATE_OPERATION_CACHE);
        IgniteCache igniteCache = this.invokerIgnite.cache(dataSliceName);
        DataSlice targetDataSlice = new DataSlice(this.invokerIgnite,igniteCache);
        return targetDataSlice;
    }

    public DataCube createGridDataCube(String dataCubeName) throws DataCubeExistException {
        confirmDataSliceNotExist(dataCubeName);
        IgniteCache igniteCache = createIgniteCache(this.invokerIgnite,dataCubeName, CacheMode.PARTITIONED);
        return new DataCube(this.invokerIgnite,igniteCache);
    }

    public DataCube createPerUnitDataCube(String dataCubeName) throws DataCubeExistException {
        confirmDataSliceNotExist(dataCubeName);
        IgniteCache igniteCache = createIgniteCache(this.invokerIgnite,dataCubeName, CacheMode.REPLICATED);
        return new DataCube(this.invokerIgnite,igniteCache);
    }

    public DataCube createUnitLocalDataCube(String dataCubeName) throws DataCubeExistException {
        confirmDataSliceNotExist(dataCubeName);
        IgniteCache igniteCache = createIgniteCache(this.invokerIgnite,dataCubeName, CacheMode.LOCAL);
        return new DataCube(this.invokerIgnite,igniteCache);
    }

    public void eraseDataSlice(String dataSliceName){
        if(listDataSlices().contains(dataSliceName)){
            this.invokerIgnite.destroyCache(dataSliceName);
        }
    }

    public DataSlice getDataSlice(String dataSliceName){
        IgniteCache targetCache=this.invokerIgnite.cache(dataSliceName);
        if(targetCache==null){
            return null;
        }else{
            return new DataSlice(this.invokerIgnite,targetCache);
        }
    }

    public List<String> listDataSlices(){
        Collection<String> igniteCacheNames = this.invokerIgnite.cacheNames();
        List<String> dataSliceNameList = new ArrayList<>();
        for(String currentString:igniteCacheNames){
            if(!currentString.startsWith("SQL_")){
                dataSliceNameList.add(currentString);
            }
        }
        return dataSliceNameList;
    }

    public static DataServiceInvoker getInvokerInstance() throws ComputeGridNotActiveException {
        DataServiceInvoker invokerInstance=new DataServiceInvoker();
        try{
            invokerInstance.openServiceSession();
        }catch (ComputeGridNotActiveException e){
            invokerInstance.closeServiceSession();
            throw e;
        }
        return invokerInstance;
    }

    private IgniteCache createIgniteCache(Ignite invokerIgnite, String cacheName, CacheMode cacheMode){
        CacheConfiguration<?, ?> cacheCfg = new CacheConfiguration<>(cacheName);
        if(cacheMode.equals(CacheMode.PARTITIONED)){
            //need use backup to relocate data if host compute unit is closed
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
            if(dataStoreAtomicityModeStr.equals(""+CacheAtomicityMode.TRANSACTIONAL_SNAPSHOT)){
                cacheCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL_SNAPSHOT);
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

        cacheCfg.setSqlSchema(cacheName);
        IgniteCache resultIgniteCache = this.invokerIgnite.createCache(cacheCfg);

        return resultIgniteCache;
    }

    private void confirmDataSliceNotExist(String dataCubeName) throws DataCubeExistException {
        IgniteCache targetCache=this.invokerIgnite.cache(dataCubeName);
        if(targetCache!=null){
            throw new DataCubeExistException();
        }
    }

    @Override
    public void close() throws Exception {
        closeServiceSession();
    }
}
