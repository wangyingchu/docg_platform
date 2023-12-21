package com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite;

import com.viewfunction.docg.dataCompute.computeServiceCore.payload.*;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSliceAtomicityMode;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSliceStoreMode;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.config.DataComputeConfigurationHandler;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientCacheConfiguration;
import org.apache.ignite.client.ClientClusterGroup;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.ClientConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class ComputeGridObserver implements AutoCloseable{

    private String observedUnitIPsStr= DataComputeConfigurationHandler.getConfigPropertyValue("observedUnitIPs");
    private IgniteClient igniteClient;

    private ComputeGridObserver(){}

    public void openObserveSession(){
        String[] unitIPArray = observedUnitIPsStr.split(",");
        ClientConfiguration cfg = new ClientConfiguration().setAddresses(unitIPArray).setPartitionAwarenessEnabled(true);
        this.igniteClient = Ignition.startClient(cfg);
    }

    public void closeObserveSession(){
        if(this.igniteClient != null){
            this.igniteClient.close();
        }
    }

    public static ComputeGridObserver getObserverInstance(){
        ComputeGridObserver computeGridObserver = new ComputeGridObserver();
        computeGridObserver.openObserveSession();
        return computeGridObserver;
    }

    @Override
    public void close() throws Exception {
        closeObserveSession();
    }

    public Set<DataComputeUnitMetaInfo> listDataComputeUnit(){
        Set<DataComputeUnitMetaInfo> dataComputeUnitMetaInfoSet = new HashSet<>();
        Collection<ClusterNode> nodesCollection = this.igniteClient.cluster().forServers().nodes();
        if(nodesCollection != null){
            for(ClusterNode currentClusterNode:nodesCollection){
                String _UnitId = currentClusterNode.id().toString();
                String _UnitType = currentClusterNode.attribute(DataComputeConfigurationHandler.getConfigPropertyValue("unitRoleAttributeName"));
                Collection<String> _HostName = currentClusterNode.hostNames();
                Collection<String> _IPAddress = currentClusterNode.addresses();
                boolean isClientUnit = currentClusterNode.isClient();
                DataComputeUnitMetaInfo currentDataComputeUnitMetaInfo = new DataComputeUnitMetaInfo(_UnitId,_UnitType,_HostName,_IPAddress,isClientUnit);
                dataComputeUnitMetaInfoSet.add(currentDataComputeUnitMetaInfo);
            }
        }
        return dataComputeUnitMetaInfoSet;
    }

    public Set<DataSliceMetaInfo> listDataSlice(){
        Set<DataSliceMetaInfo> dataSliceMetaInfoSet = new HashSet<>();
        Collection<String> cacheNameCollection = this.igniteClient.cacheNames();
        if(cacheNameCollection != null){
            for(String currentCacheName : cacheNameCollection){
                ClientCache currentCache = this.igniteClient.cache(currentCacheName);
                ClientCacheConfiguration clientCacheConfiguration = currentCache.getConfiguration();
                DataSliceMetaInfo dataSliceMetaInfo = new DataSliceMetaInfo();
                dataSliceMetaInfo.setDataSliceName(clientCacheConfiguration.getName());
                dataSliceMetaInfo.setPrimaryDataCount(currentCache.size(CachePeekMode.PRIMARY));
                dataSliceMetaInfo.setBackupDataCount(currentCache.size(CachePeekMode.BACKUP));
                dataSliceMetaInfo.setTotalDataCount(currentCache.size(CachePeekMode.ALL));
                dataSliceMetaInfo.setStoreBackupNumber(clientCacheConfiguration.getBackups());
                CacheMode currentStoreCacheMode=clientCacheConfiguration.getCacheMode();
                switch(currentStoreCacheMode){
                    case PARTITIONED -> dataSliceMetaInfo.setDataStoreMode(DataSliceStoreMode.Grid);
                    case REPLICATED -> dataSliceMetaInfo.setDataStoreMode(DataSliceStoreMode.PerUnit);
                }
                switch(clientCacheConfiguration.getAtomicityMode()){
                    case ATOMIC -> dataSliceMetaInfo.setAtomicityMode(DataSliceAtomicityMode.ATOMIC);
                    case TRANSACTIONAL -> dataSliceMetaInfo.setAtomicityMode(DataSliceAtomicityMode.TRANSACTIONAL);
                }
                dataSliceMetaInfoSet.add(dataSliceMetaInfo);

                //dataSliceMetaInfo.setSliceGroupName();
            }
        }
        return dataSliceMetaInfoSet;
    }

    public ComputeGridRealtimeStatisticsInfo getGridRealtimeStatisticsInfo(){
        Map<String,Object> currentIgniteMetricsValueMap = getCurrentIgniteMetricsValueMap();
        long maxAvailableMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.MaxSize").toString());
        long usedMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.TotalUsedSize").toString());
        long assignedMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.OffHeapSize").toString());
        int upTime = Integer.parseInt(currentIgniteMetricsValueMap.get("ignite.uptime").toString());
        long startTime = Long.parseLong(currentIgniteMetricsValueMap.get("ignite.startTimestamp").toString());

        //int executorService = Integer.parseInt(currentIgniteMetricsValueMap.get("ignite.executorServiceFormatted").toString());

        ClientClusterGroup computeUnitCluster = this.igniteClient.cluster().forServers();
        int serverUnitCount = computeUnitCluster.nodes().size();

        ComputeGridRealtimeStatisticsInfo computeGridRealtimeStatisticsInfo = new ComputeGridRealtimeStatisticsInfo();
        computeGridRealtimeStatisticsInfo.setAssignedMemoryInMB(serverUnitCount*(assignedMem/1024/1024));
        computeGridRealtimeStatisticsInfo.setMaxAvailableMemoryInMB(serverUnitCount*(maxAvailableMem/1024/1024));
        computeGridRealtimeStatisticsInfo.setUsedMemoryInMB(serverUnitCount*(usedMem/1024/1024));
        computeGridRealtimeStatisticsInfo.setYoungestUnitId(computeUnitCluster.forYoungest().node().id().toString());
        computeGridRealtimeStatisticsInfo.setOldestUnitId(computeUnitCluster.forOldest().node().id().toString());
        computeGridRealtimeStatisticsInfo.setGridUpTimeInMinute(upTime/1000/60);
        Instant instant = Instant.ofEpochMilli(startTime);
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        computeGridRealtimeStatisticsInfo.setGridStartTime(localDateTime);
        computeGridRealtimeStatisticsInfo.setDataComputeUnitsAmount(serverUnitCount);

        Map<String,Object> ipAddressInfoMap = getCurrentUnitAttributeValueMap("org.apache.ignite.ips");
        List<String> ipAddressList = new ArrayList<>();
        Set<String> keySet = ipAddressInfoMap.keySet();
        List<String> needRemoveKey = new ArrayList<>();
        for(String currentKey:keySet){
            String unitIP = ipAddressInfoMap.get(currentKey).toString();
            if(ipAddressList.contains(unitIP)){
                needRemoveKey.add(currentKey);
            }else{
                ipAddressList.add(unitIP);
            }
        }
        for(String key:needRemoveKey){
            ipAddressInfoMap.remove(key);
        }

        List<Map<String,Object>> unitsMetricsList = getCurrentUnitsMetricsValuesList();

        long totalIdleTimeValue = 0;
        long totalBusyTimeValue = 0;
        int totalCPUValue = 0;
        for(Map<String,Object> currentMetricsList:unitsMetricsList){
            totalIdleTimeValue = totalIdleTimeValue + (Long)currentMetricsList.get("TOTAL_IDLE_TIME");
            totalBusyTimeValue = totalBusyTimeValue + (Long)currentMetricsList.get("TOTAL_BUSY_TIME");
            String unitId = currentMetricsList.get("UNIT_ID").toString();
            if(ipAddressInfoMap.containsKey(unitId)){
                int currentTotalCPU = (Integer)currentMetricsList.get("TOTAL_CPU");
                totalCPUValue = totalCPUValue+currentTotalCPU;
            }
        }
        computeGridRealtimeStatisticsInfo.setTotalIdleTimeInSecond(totalIdleTimeValue/1000);
        computeGridRealtimeStatisticsInfo.setTotalBusyTimeInSecond(totalBusyTimeValue/1000);
        computeGridRealtimeStatisticsInfo.setTotalAvailableCPUCores(totalCPUValue);
        return computeGridRealtimeStatisticsInfo;
    }

    public Set<ComputeUnitRealtimeStatisticsInfo> getComputeUnitsRealtimeStatisticsInfo(){
        Set<ComputeUnitRealtimeStatisticsInfo> computeUnitRealtimeStatisticsInfoSet = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        ZoneId zoneId = ZoneId.systemDefault();
        List<Map<String,Object>> currentUnitsMetricsValuesList = getCurrentUnitsMetricsValuesList();
        for(Map<String,Object> currentMetricsMap : currentUnitsMetricsValuesList){
            ComputeUnitRealtimeStatisticsInfo computeUnitRealtimeStatisticsInfo = new ComputeUnitRealtimeStatisticsInfo();
            computeUnitRealtimeStatisticsInfo.setUnitID(currentMetricsMap.get("UNIT_ID").toString());

            computeUnitRealtimeStatisticsInfo.setAvailableCPUCores((Integer)currentMetricsMap.get("TOTAL_CPU"));
            computeUnitRealtimeStatisticsInfo.setUnitUpTimeInMinute((Long)currentMetricsMap.get("UPTIME")/1000/60);
            computeUnitRealtimeStatisticsInfo.setTotalIdleTimeInSecond((Long)currentMetricsMap.get("TOTAL_IDLE_TIME")/1000);
            computeUnitRealtimeStatisticsInfo.setTotalBusyTimeInSecond((Long)currentMetricsMap.get("TOTAL_BUSY_TIME")/1000);
            computeUnitRealtimeStatisticsInfo.setAverageCPULoadPercentage((Double)currentMetricsMap.get("AVG_CPU_LOAD"));
            computeUnitRealtimeStatisticsInfo.setCurrentCPULoadPercentage((Double)currentMetricsMap.get("CUR_CPU_LOAD"));
            computeUnitRealtimeStatisticsInfo.setIdleTimePercentage((Double)currentMetricsMap.get("IDLE_TIME_PERCENTAGE"));
            computeUnitRealtimeStatisticsInfo.setBusyTimePercentage((Double)currentMetricsMap.get("BUSY_TIME_PERCENTAGE"));
            computeUnitRealtimeStatisticsInfo.setCurrentIdleTimeInSecond((Long)currentMetricsMap.get("CUR_IDLE_TIME")/1000);

            String unitStartDatetime = currentMetricsMap.get("NODE_START_TIME").toString();
            try {
                //输出格式为："2023-12-13 09:16:38.845"
                Date nodeStartDate = sdf.parse(unitStartDatetime.substring(0,unitStartDatetime.length()-4));
                Instant instant = nodeStartDate.toInstant();
                LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                computeUnitRealtimeStatisticsInfo.setUnitStartTime(localDateTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Map<String,Object> currentIgniteMetricsValueMap = getCurrentIgniteMetricsValueMap();
            long maxAvailableMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.MaxSize").toString());
            long usedMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.TotalUsedSize").toString());
            long assignedMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.OffHeapSize").toString());
            computeUnitRealtimeStatisticsInfo.setAssignedMemoryInMB(assignedMem/1024/1024);
            computeUnitRealtimeStatisticsInfo.setMaxAvailableMemoryInMB(maxAvailableMem/1024/1024);
            computeUnitRealtimeStatisticsInfo.setUsedMemoryInMB(usedMem/1024/1024);

            computeUnitRealtimeStatisticsInfoSet.add(computeUnitRealtimeStatisticsInfo);
        }
        return computeUnitRealtimeStatisticsInfoSet;
    }

    public DataSliceDetailInfo getDataSliceDetail(String dataSliceName){
        ClientCache targetClientCache = this.igniteClient.cache(dataSliceName);
        if(targetClientCache!= null){
            DataSliceDetailInfo dataSliceDetailInfo = new DataSliceDetailInfo();
            dataSliceDetailInfo.setDataSliceName(targetClientCache.getName());

            ClientCacheConfiguration clientCacheConfiguration = targetClientCache.getConfiguration();
            dataSliceDetailInfo.setStoreBackupNumber(clientCacheConfiguration.getBackups());
            dataSliceDetailInfo.setSliceGroupName(clientCacheConfiguration.getGroupName());

            QueryEntity[] entities = clientCacheConfiguration.getQueryEntities();
            LinkedHashMap<String,String> propertiesMap = null;
            if(entities != null && entities.length > 0){
                QueryEntity currentQueryEntity = entities[0];
                propertiesMap = currentQueryEntity.getFields();
            }
            Map<String, DataSlicePropertyType> propertiesDefinition = new HashMap<>();
            if(propertiesMap != null){
                Set<String> propertyNameSet = propertiesMap.keySet();
                for(String currentPropertyName : propertyNameSet){
                    String propertyType = propertiesMap.get(currentPropertyName);
                    DataSlicePropertyType currentPropertyType = getSlicePropertyType(propertyType);
                    propertiesDefinition.put(currentPropertyName,currentPropertyType);
                }
            }
            dataSliceDetailInfo.setPropertiesDefinition(propertiesDefinition);

            dataSliceDetailInfo.setPrimaryDataCount(targetClientCache.size(CachePeekMode.PRIMARY));
            dataSliceDetailInfo.setBackupDataCount(targetClientCache.size(CachePeekMode.BACKUP));
            dataSliceDetailInfo.setTotalDataCount(targetClientCache.size(CachePeekMode.ALL));

            CacheMode cacheMode = clientCacheConfiguration.getCacheMode();
            switch(cacheMode){
                case REPLICATED -> dataSliceDetailInfo.setDataStoreMode(DataSliceStoreMode.PerUnit);
                case PARTITIONED -> dataSliceDetailInfo.setDataStoreMode(DataSliceStoreMode.Grid);
            }

            CacheAtomicityMode cacheAtomicityMode = clientCacheConfiguration.getAtomicityMode();
            switch(cacheAtomicityMode){
                case ATOMIC -> dataSliceDetailInfo.setAtomicityMode(DataSliceAtomicityMode.ATOMIC);
                case TRANSACTIONAL -> dataSliceDetailInfo.setAtomicityMode(DataSliceAtomicityMode.TRANSACTIONAL);
            }
            return dataSliceDetailInfo;
        }

        return null;
    }

    private Map<String,Object> getCurrentIgniteMetricsValueMap(){
        Map<String,Object> currentIgniteMetricsValueMap = new HashMap<>();
        List<List<?>> listValue = this.igniteClient.query(new SqlFieldsQuery("select name, value from SYS.METRICS").setSchema("SYS")).getAll();
        for(List<?> currentList :listValue){
            String metricName = currentList.get(0).toString();
            Object metricNameValue = currentList.get(1);
            currentIgniteMetricsValueMap.put(metricName,metricNameValue);
        }
        return currentIgniteMetricsValueMap;
    }

    private List<Map<String,Object>> getCurrentUnitsMetricsValuesList(){
        List<Map<String,Object>> unitsMetricsValuesList = new ArrayList<>();
        String sql = "SELECT NODE_ID, LAST_UPDATE_TIME, MAX_ACTIVE_JOBS, CUR_ACTIVE_JOBS, AVG_ACTIVE_JOBS, MAX_WAITING_JOBS, CUR_WAITING_JOBS, AVG_WAITING_JOBS, MAX_REJECTED_JOBS, CUR_REJECTED_JOBS, AVG_REJECTED_JOBS, TOTAL_REJECTED_JOBS, MAX_CANCELED_JOBS, CUR_CANCELED_JOBS, AVG_CANCELED_JOBS, TOTAL_CANCELED_JOBS, MAX_JOBS_WAIT_TIME, CUR_JOBS_WAIT_TIME, AVG_JOBS_WAIT_TIME, MAX_JOBS_EXECUTE_TIME, CUR_JOBS_EXECUTE_TIME, AVG_JOBS_EXECUTE_TIME, TOTAL_JOBS_EXECUTE_TIME, TOTAL_EXECUTED_JOBS, TOTAL_EXECUTED_TASKS, TOTAL_BUSY_TIME, TOTAL_IDLE_TIME, CUR_IDLE_TIME, BUSY_TIME_PERCENTAGE, IDLE_TIME_PERCENTAGE, TOTAL_CPU, CUR_CPU_LOAD, AVG_CPU_LOAD, CUR_GC_CPU_LOAD, HEAP_MEMORY_INIT, HEAP_MEMORY_USED, HEAP_MEMORY_COMMITED, HEAP_MEMORY_MAX, HEAP_MEMORY_TOTAL, NONHEAP_MEMORY_INIT, NONHEAP_MEMORY_USED, NONHEAP_MEMORY_COMMITED, NONHEAP_MEMORY_MAX, NONHEAP_MEMORY_TOTAL, UPTIME, JVM_START_TIME, NODE_START_TIME, LAST_DATA_VERSION, CUR_THREAD_COUNT, MAX_THREAD_COUNT, TOTAL_THREAD_COUNT, CUR_DAEMON_THREAD_COUNT, SENT_MESSAGES_COUNT, SENT_BYTES_COUNT, RECEIVED_MESSAGES_COUNT, RECEIVED_BYTES_COUNT, OUTBOUND_MESSAGES_QUEUE\n" +
        "FROM SYS.NODE_METRICS;";

        List<List<?>> listValue = this.igniteClient.query(new SqlFieldsQuery(sql).setSchema("SYS")).getAll();
        for(List<?> currentList :listValue){
            String unitId = currentList.get(0).toString();

            Map<String,Object> metricsValueMap = new HashMap<>();
            metricsValueMap.put("UNIT_ID",unitId);

            Long _TOTAL_BUSY_TIME = Long.parseLong(currentList.get(25).toString());
            Long _TOTAL_IDLE_TIME = Long.parseLong(currentList.get(26).toString());
            Long _CUR_IDLE_TIME = Long.parseLong(currentList.get(27).toString());
            Double _BUSY_TIME_PERCENTAGE = Double.parseDouble(currentList.get(28).toString());
            Double _IDLE_TIME_PERCENTAGE = Double.parseDouble(currentList.get(29).toString());
            Integer _TOTAL_CPU = Integer.parseInt(currentList.get(30).toString());
            Double _CUR_CPU_LOAD = Double.parseDouble(currentList.get(31).toString());
            Double _AVG_CPU_LOAD = Double.parseDouble(currentList.get(32).toString());
            Long _UPTIME = Long.parseLong(currentList.get(44).toString());
            String _NODE_START_TIME = currentList.get(46).toString().toString();

            metricsValueMap.put("TOTAL_BUSY_TIME",_TOTAL_BUSY_TIME);
            metricsValueMap.put("TOTAL_IDLE_TIME",_TOTAL_IDLE_TIME);
            metricsValueMap.put("CUR_IDLE_TIME",_CUR_IDLE_TIME);
            metricsValueMap.put("BUSY_TIME_PERCENTAGE",_BUSY_TIME_PERCENTAGE);
            metricsValueMap.put("IDLE_TIME_PERCENTAGE",_IDLE_TIME_PERCENTAGE);
            metricsValueMap.put("TOTAL_CPU",_TOTAL_CPU);
            metricsValueMap.put("CUR_CPU_LOAD",_CUR_CPU_LOAD);
            metricsValueMap.put("AVG_CPU_LOAD",_AVG_CPU_LOAD);
            metricsValueMap.put("UPTIME",_UPTIME);
            metricsValueMap.put("NODE_START_TIME",_NODE_START_TIME);

            unitsMetricsValuesList.add(metricsValueMap);
        }

        return unitsMetricsValuesList;
    }

    private Map<String,Object> getCurrentUnitAttributeValueMap(String attributeName){
        Map<String,Object> unitAttributeValueMap = new HashMap<>();
        List<List<?>> listValue = this.igniteClient
                .query(new SqlFieldsQuery("SELECT NODE_ID,VALUE FROM SYS.NODE_ATTRIBUTES where NAME = ? ")
                        .setSchema("SYS").setArgs(attributeName)).getAll();
        for(List<?> currentList :listValue){
            String unitId = currentList.get(0).toString();
            Object attributeValue = currentList.get(1);
            unitAttributeValueMap.put(unitId,attributeValue);
        }
        return unitAttributeValueMap;
    }

    private DataSlicePropertyType getSlicePropertyType(String propertyKindDesc){
        if(propertyKindDesc != null){
            if("java.sql.Timestamp".equals(propertyKindDesc)){
                return DataSlicePropertyType.TIMESTAMP;
            }else if("java.lang.Double".equals(propertyKindDesc)){
                return DataSlicePropertyType.DOUBLE;
            }else if("java.lang.Float".equals(propertyKindDesc)){
                return DataSlicePropertyType.FLOAT;
            }else if("java.sql.Time".equals(propertyKindDesc)){
                return DataSlicePropertyType.TIME;
            }else if("java.sql.Date".equals(propertyKindDesc)){
                return DataSlicePropertyType.DATE;
            }else if("java.lang.Integer".equals(propertyKindDesc)){
                return DataSlicePropertyType.INT;
            }else if("java.lang.Boolean".equals(propertyKindDesc)){
                return DataSlicePropertyType.BOOLEAN;
            }else if("java.lang.Long".equals(propertyKindDesc)){
                return DataSlicePropertyType.LONG;
            }else if("java.lang.Short".equals(propertyKindDesc)){
                return DataSlicePropertyType.SHORT;
            }else if("java.util.UUID".equals(propertyKindDesc)){
                return DataSlicePropertyType.UUID;
            }else if("org.locationtech.jts.geom.Geometry".equals(propertyKindDesc)){
                return DataSlicePropertyType.GEOMETRY;
            }else if("java.lang.Byte".equals(propertyKindDesc)){
                return DataSlicePropertyType.BYTE;
            }else if("java.lang.String".equals(propertyKindDesc)){
                return DataSlicePropertyType.STRING;
            }else if("[B".equals(propertyKindDesc)){
                return DataSlicePropertyType.BINARY;
            }else if("java.math.BigDecimal".equals(propertyKindDesc)){
                return DataSlicePropertyType.DECIMAL;
            }
        }
        return null;
    }
}
