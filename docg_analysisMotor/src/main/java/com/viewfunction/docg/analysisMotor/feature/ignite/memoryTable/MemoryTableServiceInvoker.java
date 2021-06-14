package com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable;

import com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.exception.MemoryTablesGridNotActiveException;
import com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.exception.MemoryTableExistException;
import com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.exception.MemoryTablePropertiesStructureException;
import com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.util.MemoryTableConfigurationHandler;
import com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.util.MemoryTableUtil;
import com.viewfunction.docg.analysisMotor.feature.ignite.memoryTable.util.IgniteOperationUtil;
import com.viewfunction.docg.analysisMotor.util.PropertyHandler;
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

public class MemoryTableServiceInvoker implements AutoCloseable{

    private Ignite invokerIgnite;
    private final String TEMPLATE_OPERATION_CACHE = "TEMPLATE_OPERATION_CACHE";

    public void openServiceSession() throws MemoryTablesGridNotActiveException {
        Ignition.setClientMode(true);
        this.invokerIgnite = Ignition.start(MemoryTableConfigurationHandler.getIgniteConfigurationFilePath());
        IgniteOperationUtil.checkGridActiveStatus(this.invokerIgnite);
    }

    public void closeServiceSession(){
        if(this.invokerIgnite !=null){
            this.invokerIgnite.close();
        }
    }

    public MemoryTable createGlobalMemoryTable(String memoryTableName, String memoryTableGroup, Map<String, MemoryTablePropertyType> propertiesDefinitionMap, List<String> primaryKeysList) throws MemoryTableExistException, MemoryTablePropertiesStructureException {
        return createMemoryTable(memoryTableName,memoryTableGroup,propertiesDefinitionMap,primaryKeysList,"PARTITIONED");
    }

    public MemoryTable createPerEngineMemoryTable(String memoryTableName, String memoryTableGroup, Map<String, MemoryTablePropertyType> propertiesDefinitionMap, List<String> primaryKeysList) throws MemoryTableExistException, MemoryTablePropertiesStructureException {
        return createMemoryTable(memoryTableName,memoryTableGroup,propertiesDefinitionMap,primaryKeysList,"REPLICATED");
    }

    private MemoryTable createMemoryTable(String memoryTableName, String memoryTableGroup, Map<String, MemoryTablePropertyType> propertiesDefinitionMap, List<String> primaryKeysList, String templateType) throws MemoryTableExistException, MemoryTablePropertiesStructureException {
        confirmMemoryTableNotExist(memoryTableName);
        validateFieldsDefinition(propertiesDefinitionMap,primaryKeysList);
        CacheConfiguration<?, ?> cacheCfg = new CacheConfiguration<>(TEMPLATE_OPERATION_CACHE).setSqlSchema(memoryTableGroup);
        IgniteCache<?, ?> cache = this.invokerIgnite.getOrCreateCache(cacheCfg);
        String sliceCreateSentence = generateMemoryTableCreateSentence(memoryTableName,propertiesDefinitionMap,primaryKeysList,templateType);
        cache.query(new SqlFieldsQuery(sliceCreateSentence)).getAll();
        this.invokerIgnite.destroyCache(TEMPLATE_OPERATION_CACHE);
        IgniteCache igniteCache = this.invokerIgnite.cache(memoryTableName);
        MemoryTable targetMemoryTable = new MemoryTable(this.invokerIgnite,igniteCache);
        return targetMemoryTable;
    }

    private String generateMemoryTableCreateSentence(String memoryTableName, Map<String, MemoryTablePropertyType> propertiesDefinitionMap, List<String> primaryKeysList, String templateType){
        String dataStoreBackupNumberStr= PropertyHandler.getConfigPropertyValue("igniteDataBackupsNumber");
        String dataStoreAtomicityModeStr= PropertyHandler.getConfigPropertyValue("igniteAtomicityMode");
        String dataStoreRegionNameStr= PropertyHandler.getConfigPropertyValue("igniteRegionName");
        String propertiesStructureSQL = MemoryTableUtil.buildMemoryTableStructureSQL(propertiesDefinitionMap,primaryKeysList);
        String createSentence =  "CREATE TABLE "+memoryTableName+" ("+propertiesStructureSQL+") " +
        "WITH \"CACHE_NAME="+memoryTableName+
        ",DATA_REGION="+dataStoreRegionNameStr+
        ",BACKUPS="+dataStoreBackupNumberStr+
        ",ATOMICITY="+dataStoreAtomicityModeStr+
        ",TEMPLATE="+templateType+"\"";
        return createSentence;
    }

    public void eraseMemoryTable(String memoryTableName){
        if(listMemoryTables().contains(memoryTableName)){
            this.invokerIgnite.destroyCache(memoryTableName);
        }
    }

    public MemoryTable getMemoryTable(String memoryTableName){
        IgniteCache targetCache=this.invokerIgnite.cache(memoryTableName);
        if(targetCache==null){
            return null;
        }else{
            return new MemoryTable(this.invokerIgnite,targetCache);
        }
    }

    public List<String> listMemoryTables(){
        Collection<String> igniteCacheNames = this.invokerIgnite.cacheNames();
        List<String> memoryTableNameList = new ArrayList<>();
        for(String currentString:igniteCacheNames){
            if(!currentString.equals(TEMPLATE_OPERATION_CACHE)){
                memoryTableNameList.add(currentString);
            }
        }
        return memoryTableNameList;
    }

    public static MemoryTableServiceInvoker getInvokerInstance() throws MemoryTablesGridNotActiveException {
        MemoryTableServiceInvoker invokerInstance=new MemoryTableServiceInvoker();
        try{
            invokerInstance.openServiceSession();
        }catch (MemoryTablesGridNotActiveException e){
            invokerInstance.closeServiceSession();
            throw e;
        }
        return invokerInstance;
    }

    public void setServiceSession(Ignite invokerIgnite){
        this.invokerIgnite=invokerIgnite;
    }

    public static MemoryTableServiceInvoker getInvokerInstance(Ignite invokerIgnite) throws MemoryTablesGridNotActiveException {
        MemoryTableServiceInvoker invokerInstance=new MemoryTableServiceInvoker();
        invokerInstance.setServiceSession(invokerIgnite);
        return invokerInstance;
    }

    private IgniteCache createIgniteCache(Ignite invokerIgnite, String cacheName, CacheMode cacheMode){
        CacheConfiguration<?, ?> cacheCfg = new CacheConfiguration<>(cacheName);
        if(cacheMode.equals(CacheMode.PARTITIONED)){
            //need use backup to relocate data if host node is closed
            int defaultBackupNumber=1;
            String dataStoreBackupNumberStr= PropertyHandler.getConfigPropertyValue("igniteDataBackupsNumber");
            if(dataStoreBackupNumberStr!=null){
                defaultBackupNumber=Integer.parseInt(dataStoreBackupNumberStr);
            }
            cacheCfg.setBackups(defaultBackupNumber);
        }
        cacheCfg.setSqlSchema(cacheName.toUpperCase());
        cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        String dataStoreAtomicityModeStr= PropertyHandler.getConfigPropertyValue("igniteAtomicityMode");
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
        String dataStoreRegionNameStr= PropertyHandler.getConfigPropertyValue("igniteRegionName");
        cacheCfg.setDataRegionName(dataStoreRegionNameStr);
        //If the data store partitions number is to height, it will leading use lot's of resource even on small
        // workload(such as in spark use much tasks for small work load),Ignite use 1024 by default.
        // Use dataStorePartitionsNumber to control this attribute
        //int defaultDataStorePartitionsNumber=1024;
        String dataStorePartitionsNumberStr= PropertyHandler.getConfigPropertyValue("ignitePartitionsNumber");
        if(dataStorePartitionsNumberStr!=null){
            int dataStorePartitionsNumber=Integer.parseInt(dataStorePartitionsNumberStr);
            cacheCfg.setAffinity(new RendezvousAffinityFunction(false, dataStorePartitionsNumber));
        }

        cacheCfg.setSqlSchema(cacheName);
        IgniteCache resultIgniteCache = this.invokerIgnite.createCache(cacheCfg);

        return resultIgniteCache;
    }

    private void confirmMemoryTableNotExist(String dataCubeName) throws MemoryTableExistException {
        IgniteCache targetCache=this.invokerIgnite.cache(dataCubeName);
        if(targetCache!=null){
            throw new MemoryTableExistException();
        }
    }

    private void validateFieldsDefinition(Map<String, MemoryTablePropertyType> fieldsDefinitionMap, List<String> primaryKeysList) throws MemoryTablePropertiesStructureException {
        if(fieldsDefinitionMap == null || fieldsDefinitionMap.size() ==0){
            throw new MemoryTablePropertiesStructureException();
        }
    }

    @Override
    public void close() throws Exception {
        closeServiceSession();
    }
}
