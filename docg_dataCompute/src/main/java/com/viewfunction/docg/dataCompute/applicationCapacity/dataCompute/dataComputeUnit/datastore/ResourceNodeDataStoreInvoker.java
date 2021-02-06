package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataCubeExistException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;

import java.util.Collection;

public class ResourceNodeDataStoreInvoker implements AutoCloseable{

    private Ignite invokerIgnite;

    public void openDataAccessSession() throws ComputeGridNotActiveException {
        Ignition.setClientMode(true);
        this.invokerIgnite =Ignition.start(DataComputeConfigurationHandler.getDataComputeIgniteConfigurationFilePath());
        UnitIgniteOperationUtil.checkGridActiveStatus(this.invokerIgnite);
    }

    public void closeDataAccessSession(){
        if(this.invokerIgnite !=null){
            this.invokerIgnite.close();
        }
    }

    public void setDataAccessSession(Ignite invokerIgnite){
        this.invokerIgnite=invokerIgnite;
    }

    public Ignite getDataAccessSession() {
        return this.invokerIgnite;
    }

    public ResourceNodeDataStore createGridSingletonDataStore(String dataStoreName) throws DataCubeExistException {
        confirmDataStoreNotExist(dataStoreName);
        ResourceNodeDataStore connectomeDataStore=new ResourceNodeDataStore(this.invokerIgnite,dataStoreName,CacheMode.PARTITIONED);
        return connectomeDataStore;
    }

    public ResourceNodeDataStore createGridSingletonDataStore(String dataStoreName, Class keyType, Class valueType) throws DataCubeExistException {
        confirmDataStoreNotExist(dataStoreName);
        ResourceNodeDataStore connectomeDataStore=new ResourceNodeDataStore(this.invokerIgnite,dataStoreName,CacheMode.PARTITIONED,keyType,valueType);
        return connectomeDataStore;
    }

    public ResourceNodeDataStore createGridPerNodeDataStore(String dataStoreName) throws DataCubeExistException {
        confirmDataStoreNotExist(dataStoreName);
        ResourceNodeDataStore connectomeDataStore=new ResourceNodeDataStore(this.invokerIgnite,dataStoreName,CacheMode.REPLICATED);
        return connectomeDataStore;
    }

    public ResourceNodeDataStore createGridPerNodeDataStore(String dataStoreName, Class keyType, Class valueType) throws DataCubeExistException {
        confirmDataStoreNotExist(dataStoreName);
        ResourceNodeDataStore connectomeDataStore=new ResourceNodeDataStore(this.invokerIgnite,dataStoreName,CacheMode.REPLICATED,keyType,valueType);
        return connectomeDataStore;
    }

    public ResourceNodeDataStore createConnectomeLocalDataStore(String dataStoreName) throws DataCubeExistException {
        confirmDataStoreNotExist(dataStoreName);
        ResourceNodeDataStore connectomeDataStore=new ResourceNodeDataStore(this.invokerIgnite,dataStoreName,CacheMode.LOCAL);
        return connectomeDataStore;
    }

    public ResourceNodeDataStore createConnectomeLocalDataStore(String dataStoreName, Class keyType, Class valueType) throws DataCubeExistException {
        confirmDataStoreNotExist(dataStoreName);
        ResourceNodeDataStore connectomeDataStore=new ResourceNodeDataStore(this.invokerIgnite,dataStoreName,CacheMode.LOCAL,keyType,valueType);
        return connectomeDataStore;
    }

    public void eraseDataStore(String dataStoreName){
        this.invokerIgnite.destroyCache(dataStoreName);
    }

    private void confirmDataStoreNotExist(String dataStoreName) throws DataCubeExistException {
        IgniteCache targetCache=this.invokerIgnite.cache(dataStoreName);
        if(targetCache!=null){
            throw new DataCubeExistException();
        }
    }

    public ResourceNodeDataStore getGridDataStore(String storeName){
        IgniteCache targetCache=this.invokerIgnite.cache(storeName);
        if(targetCache==null){
            return null;
        }else{
            ResourceNodeDataStore connectomeDataStore=new ResourceNodeDataStore(this.invokerIgnite,targetCache);
            return connectomeDataStore;
        }
    }

    public Collection<String> listGridDataStores(){
        return this.invokerIgnite.cacheNames();
    }

    public static ResourceNodeDataStoreInvoker getInvokerInstance() throws ComputeGridNotActiveException {
        ResourceNodeDataStoreInvoker invokerInstance=new ResourceNodeDataStoreInvoker();
        try{
            invokerInstance.openDataAccessSession();
        }catch (ComputeGridNotActiveException e){
            invokerInstance.closeDataAccessSession();
            throw e;
        }
        return invokerInstance;
    }

    public static ResourceNodeDataStoreInvoker getInvokerInstance(Ignite dataAccessSession){
        ResourceNodeDataStoreInvoker invokerInstance=new ResourceNodeDataStoreInvoker();
        invokerInstance.setDataAccessSession(dataAccessSession);
        return invokerInstance;
    }

    @Override
    public void close(){
        closeDataAccessSession();
    }
}
