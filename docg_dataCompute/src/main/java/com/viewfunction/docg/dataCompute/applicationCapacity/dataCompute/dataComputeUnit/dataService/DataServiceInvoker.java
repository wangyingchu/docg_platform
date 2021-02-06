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
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataServiceInvoker implements AutoCloseable{

    private Ignite invokerIgnite;

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

    public DataCube createGridDataCube(String dataCubeName) throws DataCubeExistException {
        confirmDataCubeNotExist(dataCubeName);
        IgniteCache igniteCache = createIgniteCache(this.invokerIgnite,dataCubeName, CacheMode.PARTITIONED);
        return new DataCube(this.invokerIgnite,igniteCache);
    }

    public DataCube createPerUnitDataCube(String dataCubeName) throws DataCubeExistException {
        confirmDataCubeNotExist(dataCubeName);
        IgniteCache igniteCache = createIgniteCache(this.invokerIgnite,dataCubeName, CacheMode.REPLICATED);
        return new DataCube(this.invokerIgnite,igniteCache);
    }

    public DataCube createUnitLocalDataCube(String dataCubeName) throws DataCubeExistException {
        confirmDataCubeNotExist(dataCubeName);
        IgniteCache igniteCache = createIgniteCache(this.invokerIgnite,dataCubeName, CacheMode.LOCAL);
        return new DataCube(this.invokerIgnite,igniteCache);
    }

    public void eraseDataCube(String dataCubeName){
        if(listDataCubes().contains(dataCubeName)){
            this.invokerIgnite.destroyCache(dataCubeName);
        }
    }

    public DataCube getDataCube(String dataCubeName){
        IgniteCache targetCache=this.invokerIgnite.cache(dataCubeName);
        if(targetCache==null){
            return null;
        }else{
            return new DataCube(this.invokerIgnite,targetCache);
        }
    }

    public List<String> listDataCubes(){
        Collection<String> igniteCacheNames = this.invokerIgnite.cacheNames();
        List<String> dataCubeNameList = new ArrayList<>();
        for(String currentString:igniteCacheNames){
            if(!currentString.startsWith("SQL_")){
                dataCubeNameList.add(currentString);
            }
        }
        return dataCubeNameList;
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

    private void confirmDataCubeNotExist(String dataCubeName) throws DataCubeExistException {
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
