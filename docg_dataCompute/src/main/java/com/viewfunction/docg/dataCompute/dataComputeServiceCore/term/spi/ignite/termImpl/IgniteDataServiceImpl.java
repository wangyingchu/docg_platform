package com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.util.DataSliceUtil;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSliceExistException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSlicePropertiesStructureException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlice;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.spi.ignite.termInf.IgniteDataService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.config.DataComputeConfigurationHandler;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.util.*;

public class IgniteDataServiceImpl implements IgniteDataService {

    private Ignite invokerIgnite;
    private final String TEMPLATE_OPERATION_CACHE = "TEMPLATE_OPERATION_CACHE";

    private IgniteDataServiceImpl(){}

    public void openServiceSession() throws ComputeGridNotActiveException {
        Ignition.setClientMode(true);

        // use this logic to avoid create already exist ignite nodes has same name
        IgniteConfiguration igniteConfiguration= new IgniteConfiguration();
        igniteConfiguration.setClientMode(true);
        igniteConfiguration.setIgniteInstanceName("DOCG_DATA_COMPUTE_CLIENT_"+UUID.randomUUID());
        this.invokerIgnite = Ignition.start(igniteConfiguration);

        //IgniteConfiguration IgniteConfiguration = DataComputeConfigurationHandler.getDataComputeIgniteConfigurationFilePath();
        //this.invokerIgnite = Ignition.start(DataComputeConfigurationHandler.getDataComputeIgniteConfigurationFilePath());
        UnitIgniteOperationUtil.checkGridActiveStatus(this.invokerIgnite);
    }

    public void closeServiceSession(){
        if(this.invokerIgnite !=null){
            this.invokerIgnite.close();
        }
    }

    public DataSlice createGridDataSlice(String dataSliceName, String dataSliceGroup, Map<String, DataSlicePropertyType> propertiesDefinitionMap, List<String> primaryKeysList) throws DataSliceExistException, DataSlicePropertiesStructureException {
        return createDataSlice(dataSliceName,dataSliceGroup,propertiesDefinitionMap,primaryKeysList,"PARTITIONED");
    }

    public DataSlice createPerUnitDataSlice(String dataSliceName, String dataSliceGroup, Map<String, DataSlicePropertyType> propertiesDefinitionMap,List<String> primaryKeysList) throws DataSliceExistException,DataSlicePropertiesStructureException {
        return createDataSlice(dataSliceName,dataSliceGroup,propertiesDefinitionMap,primaryKeysList,"REPLICATED");
    }

    private DataSlice createDataSlice(String dataSliceName, String dataSliceGroup, Map<String, DataSlicePropertyType> propertiesDefinitionMap,List<String> primaryKeysList,String templateType) throws DataSliceExistException,DataSlicePropertiesStructureException {
        confirmDataSliceNotExist(dataSliceName);
        validateFieldsDefinition(propertiesDefinitionMap,primaryKeysList);
        CacheConfiguration<?, ?> cacheCfg = new CacheConfiguration<>(TEMPLATE_OPERATION_CACHE).setSqlSchema(dataSliceGroup);
        IgniteCache<?, ?> cache = this.invokerIgnite.getOrCreateCache(cacheCfg);
        String sliceCreateSentence = generateDataSliceCreateSentence(dataSliceName,propertiesDefinitionMap,primaryKeysList,templateType);
        cache.query(new SqlFieldsQuery(sliceCreateSentence)).getAll();
        this.invokerIgnite.destroyCache(TEMPLATE_OPERATION_CACHE);
        IgniteCache igniteCache = this.invokerIgnite.cache(dataSliceName);
        IgniteDataSliceImpl targetDataSlice = new IgniteDataSliceImpl(this.invokerIgnite,igniteCache);
        return targetDataSlice;
    }

    private String generateDataSliceCreateSentence(String dataSliceName,Map<String, DataSlicePropertyType> propertiesDefinitionMap,List<String> primaryKeysList,String templateType){
        String dataStoreBackupNumberStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreBackupsNumber");
        String dataStoreAtomicityModeStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreAtomicityMode");
        String dataStoreRegionNameStr= DataComputeConfigurationHandler.getConfigPropertyValue("dataStoreRegionName");
        String propertiesStructureSQL = DataSliceUtil.buildSliceStructureSQL(propertiesDefinitionMap,primaryKeysList);
        String createSentence =  "CREATE TABLE "+dataSliceName+" ("+propertiesStructureSQL+") " +
                "WITH \"CACHE_NAME="+dataSliceName+
                ",DATA_REGION="+dataStoreRegionNameStr+
                ",BACKUPS="+dataStoreBackupNumberStr+
                ",ATOMICITY="+dataStoreAtomicityModeStr+
                ",TEMPLATE="+templateType+"\"";
        return createSentence;
    }

    public void eraseDataSlice(String dataSliceName){
        if(listDataSliceNames().contains(dataSliceName)){
            this.invokerIgnite.destroyCache(dataSliceName);
        }
    }

    public DataSlice getDataSlice(String dataSliceName){
        IgniteCache targetCache=this.invokerIgnite.cache(dataSliceName);
        if(targetCache==null){
            return null;
        }else{
            return new IgniteDataSliceImpl(this.invokerIgnite,targetCache);
        }
    }

    public List<String> listDataSliceNames(){
        Collection<String> igniteCacheNames = this.invokerIgnite.cacheNames();
        List<String> dataSliceNameList = new ArrayList<>();
        for(String currentString:igniteCacheNames){
            if(!currentString.equals(TEMPLATE_OPERATION_CACHE)){
                dataSliceNameList.add(currentString);
            }
        }
        return dataSliceNameList;
    }

    public static IgniteDataServiceImpl getServiceInstance() throws ComputeGridNotActiveException {
        IgniteDataServiceImpl igniteDataServiceImpl= new IgniteDataServiceImpl();
        try{
            igniteDataServiceImpl.openServiceSession();
        }catch (ComputeGridNotActiveException e){
            igniteDataServiceImpl.closeServiceSession();
            throw e;
        }
        return igniteDataServiceImpl;
    }

    public void setServiceSession(Ignite invokerIgnite){
        this.invokerIgnite=invokerIgnite;
    }

    public static IgniteDataServiceImpl getServiceInstance(Ignite invokerIgnite) throws ComputeGridNotActiveException {
        IgniteDataServiceImpl igniteDataServiceImpl=new IgniteDataServiceImpl();
        igniteDataServiceImpl.setServiceSession(invokerIgnite);
        return igniteDataServiceImpl;
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

    private void confirmDataSliceNotExist(String dataCubeName) throws DataSliceExistException {
        IgniteCache targetCache=this.invokerIgnite.cache(dataCubeName);
        if(targetCache!=null){
            throw new DataSliceExistException();
        }
    }

    private void validateFieldsDefinition(Map<String, DataSlicePropertyType> fieldsDefinitionMap,List<String> primaryKeysList) throws DataSlicePropertiesStructureException {
        if(fieldsDefinitionMap == null || fieldsDefinitionMap.size() ==0){
            throw new DataSlicePropertiesStructureException();
        }
    }

    @Override
    public void close() throws Exception {
        closeServiceSession();
    }
}
