package com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable;

import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.exception.MemoryTableDataException;
import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.exception.MemoryTablePropertiesStructureException;
import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.exception.MemoryTableQueryStructureException;
import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.query.QueryParameters;
import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.result.MemoryTableOperationResult;
import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.result.MemoryTableQueryResult;
import com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.util.MemoryTableQueryBuilder;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.*;

public class MemoryTable {

    private Ignite invokerIgnite;
    private IgniteCache<?, ?> cache;

    public MemoryTable(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;
    }

    public boolean addDataRecord(Map<String,Object> dataPropertiesValue) throws MemoryTablePropertiesStructureException, MemoryTableDataException {
        return insertDataRecordOperation("INSERT INTO",dataPropertiesValue);
    }

    public MemoryTableOperationResult addDataRecords(List<String> propertiesNameList, List<Map<String,Object>> dataPropertiesValueList) throws MemoryTablePropertiesStructureException {
        Map<String,String> slicePropertiesMap = getMemoryTablePropertiesInfo();
        Set<String> slicePropertiesNameSet = slicePropertiesMap.keySet();

        for(String currentPropertyName:propertiesNameList){
            if(!slicePropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new MemoryTablePropertiesStructureException();
            }
        }

        MemoryTableOperationResult memoryTableOperationResult = new MemoryTableOperationResult();

        StringBuffer propertiesNameSb = new StringBuffer();
        StringBuffer propertiesValuePlaceHolderSb = new StringBuffer();
        propertiesNameSb.append("(");
        propertiesValuePlaceHolderSb.append("(");

        for(int i = 0; i< propertiesNameList.size(); i++){
            String currentDataPropertyName = propertiesNameList.get(i);
            // get dataType for property value validate
            //String dataType = slicePropertiesMap.get(currentDataPropertyName.toUpperCase());
            propertiesNameSb.append(currentDataPropertyName);
            propertiesValuePlaceHolderSb.append("?");

            if(i < propertiesNameList.size() - 1){
                propertiesNameSb.append(",");
                propertiesValuePlaceHolderSb.append(",");
            }
        }
        propertiesNameSb.append(")");
        propertiesValuePlaceHolderSb.append(")");

        String sliceName = this.cache.getName();

        String sqlFieldsQuerySQL = "INSERT INTO "+sliceName+" "+propertiesNameSb.toString() +" VALUES "+propertiesValuePlaceHolderSb.toString();
        SqlFieldsQuery qry = new SqlFieldsQuery(sqlFieldsQuerySQL);

        for(Map<String,Object> currentDataValueRow :dataPropertiesValueList){
            Object[] propertiesValueArray = new Object[propertiesNameList.size()];
            for(int i =0;i<propertiesNameList.size();i++){
                String currentPropertyName = propertiesNameList.get(i);
                propertiesValueArray[i] = currentDataValueRow.get(currentPropertyName);
            }
            try {
                List<List<?>> cursor = this.cache.query(qry.setArgs(propertiesValueArray)).getAll();
                if(cursor.get(0).get(0) instanceof Long){
                    if((Long)cursor.get(0).get(0) == 1){
                        memoryTableOperationResult.increaseSuccessCount();
                    }
                }
            }catch (javax.cache.CacheException e){
                e.printStackTrace();
                memoryTableOperationResult.increaseFailCount();
            }
        }
        memoryTableOperationResult.setOperationSummary("MemoryTable addDataRecords Operation");

        memoryTableOperationResult.finishOperation();
        return memoryTableOperationResult;
    }

    public boolean updateDataRecord(Map<String,Object> dataPropertiesValue) throws MemoryTablePropertiesStructureException, MemoryTableDataException {
        Set<String> dataPropertyNameSet = dataPropertiesValue.keySet();
        validateDataProperties(dataPropertyNameSet);

        Set<String> slicePrimaryKeySet = getMemoryTablePrimaryKeysInfo();
        String[] dataPropertiesNameArray = dataPropertyNameSet.stream().toArray(n -> new String[n]);

        List<String> normalPropertiesNameList = new ArrayList<>();
        List<String> pkPropertiesNameList = new ArrayList<>();

        for(String currentPropertyName:dataPropertiesNameArray){
           if(slicePrimaryKeySet.contains(currentPropertyName.toUpperCase())){
               // PK property For update operation
               pkPropertiesNameList.add(currentPropertyName);
           }else{
               normalPropertiesNameList.add(currentPropertyName);
           }
        }

        String normalPropertyHolderStr = generatePropertiesValuePlaceHolder(normalPropertiesNameList,",");
        String pkPropertyHolderStr = generatePropertiesValuePlaceHolder(pkPropertiesNameList,"AND");

        String sliceName = this.cache.getName();
        String sqlFieldsQuerySQL = "UPDATE "+sliceName+" SET "+normalPropertyHolderStr.toString() +" WHERE ("+pkPropertyHolderStr + ")";
        SqlFieldsQuery qry = new SqlFieldsQuery(sqlFieldsQuerySQL);

        Object[] propertiesValueArray = new Object[normalPropertiesNameList.size()+pkPropertiesNameList.size()];
        for(int i = 0 ; i < normalPropertiesNameList.size() ; i++){
            String currentPropertyName = normalPropertiesNameList.get(i);
            propertiesValueArray[i] = dataPropertiesValue.get(currentPropertyName);
        }

        for(int i = 0 ; i < pkPropertiesNameList.size() ; i++){
            String currentPropertyName = pkPropertiesNameList.get(i);
            propertiesValueArray[i+normalPropertiesNameList.size()] = dataPropertiesValue.get(currentPropertyName);
        }

        try{
            List<List<?>> cursor = this.cache.query(qry.setArgs(propertiesValueArray)).getAll();
            for (List next : cursor) {
                for (Object item : next) {
                    if(item instanceof Long){
                        Long result = (Long) item;
                        if(result == 1){
                            return true;
                        }
                    }
                }
            }
        }catch (javax.cache.CacheException e){
            MemoryTableDataException memoryTableDataException = new MemoryTableDataException();
            memoryTableDataException.addSuppressed(e);
            throw memoryTableDataException;
        }
        return false;
    }

    public boolean addOrUpdateDataRecord(Map<String,Object> dataPropertiesValue) throws MemoryTablePropertiesStructureException, MemoryTableDataException {
        return insertDataRecordOperation("MERGE INTO",dataPropertiesValue);
    }

    public Map<String,Object> getDataRecordByPrimaryKeys(Map<String,Object> dataPKPropertiesValue) throws MemoryTablePropertiesStructureException, MemoryTableDataException {
        Set<String> slicePKPropertiesNameSet = getMemoryTablePrimaryKeysInfo();
        Set<String> dataPropertyNameSet = dataPKPropertiesValue.keySet();

        if(dataPKPropertiesValue.size() != slicePKPropertiesNameSet.size()){
            throw new MemoryTablePropertiesStructureException();
        }
        List<String> pkPropertiesNameList = new ArrayList<>();
        for(String currentPropertyName:dataPropertyNameSet){
            if(!slicePKPropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new MemoryTablePropertiesStructureException();
            }
            pkPropertiesNameList.add(currentPropertyName);
        }

        String pkPropertyHolderStr = generatePropertiesValuePlaceHolder(pkPropertiesNameList,"AND");
        String sliceName = this.cache.getName();
        String sqlFieldsQuerySQL = "SELECT DISTINCT * FROM "+ sliceName +" WHERE "+pkPropertyHolderStr;
        SqlFieldsQuery qry = new SqlFieldsQuery(sqlFieldsQuerySQL);

        Object[] propertiesValueArray = new Object[pkPropertiesNameList.size()];
        for(int i = 0 ; i < pkPropertiesNameList.size() ; i++){
            String currentPropertyName = pkPropertiesNameList.get(i);
            propertiesValueArray[i] = dataPKPropertiesValue.get(currentPropertyName);
        }

        try{
            Map<String, Object> resultRecord = new HashMap<>();
            FieldsQueryCursor<List<?>> cur = cache.query(qry.setArgs(propertiesValueArray));
            Iterator<List<?>> it = cur.iterator();
            if (!it.hasNext()) {
                return null;
            }

            String[] colNames = new String[cur.getColumnsCount()];
            for (int i = 0; i < colNames.length; ++i) {
                String colName = cur.getFieldName(i);
                colNames[i] = colName;
            }
            while (it.hasNext()) {
                List<?> row = it.next();
                for (int i = 0; i < colNames.length; ++i) {
                    if (colNames[i] != null) {
                        resultRecord.put(colNames[i], row.get(i));
                    }
                }
            }
            return resultRecord;
        }catch (javax.cache.CacheException e){
            MemoryTableDataException memoryTableDataException = new MemoryTableDataException();
            memoryTableDataException.addSuppressed(e);
            throw memoryTableDataException;
        }
    }

    public boolean deleteDataRecord(Map<String,Object> dataPKPropertiesValue) throws MemoryTablePropertiesStructureException, MemoryTableDataException {
        Set<String> slicePKPropertiesNameSet = getMemoryTablePrimaryKeysInfo();
        Set<String> dataPropertyNameSet = dataPKPropertiesValue.keySet();

        if(dataPKPropertiesValue.size() != slicePKPropertiesNameSet.size()){
            throw new MemoryTablePropertiesStructureException();
        }
        List<String> pkPropertiesNameList = new ArrayList<>();
        for(String currentPropertyName:dataPropertyNameSet){
            if(!slicePKPropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new MemoryTablePropertiesStructureException();
            }
            pkPropertiesNameList.add(currentPropertyName);
        }

        String pkPropertyHolderStr = generatePropertiesValuePlaceHolder(pkPropertiesNameList,"AND");
        String sliceName = this.cache.getName();
        String sqlFieldsQuerySQL = "DELETE FROM "+ sliceName +" WHERE "+pkPropertyHolderStr;
        SqlFieldsQuery qry = new SqlFieldsQuery(sqlFieldsQuerySQL);

        Object[] propertiesValueArray = new Object[pkPropertiesNameList.size()];
        for(int i = 0 ; i < pkPropertiesNameList.size() ; i++){
            String currentPropertyName = pkPropertiesNameList.get(i);
            propertiesValueArray[i] = dataPKPropertiesValue.get(currentPropertyName);
        }

        try {
            List<List<?>> cursor = this.cache.query(qry.setArgs(propertiesValueArray)).getAll();
            for (List next : cursor) {
                for (Object item : next) {
                    if(item instanceof Long){
                        Long result = (Long) item;
                        if(result == 1){
                            return true;
                        }
                    }
                }
            }
        }catch (javax.cache.CacheException e){
            MemoryTableDataException memoryTableDataException = new MemoryTableDataException();
            memoryTableDataException.addSuppressed(e);
            throw memoryTableDataException;
        }
        return false;
    }

    public MemoryTableOperationResult deleteDataRecords(List<Map<String,Object>> dataPKPropertiesValueList) throws MemoryTablePropertiesStructureException, MemoryTableDataException {
        MemoryTableOperationResult memoryTableOperationResult = new MemoryTableOperationResult();
        if(dataPKPropertiesValueList != null){
            for(Map<String,Object> currentDataMap:dataPKPropertiesValueList){
                boolean deleteResult = deleteDataRecord(currentDataMap);
                if(deleteResult){
                    memoryTableOperationResult.increaseSuccessCount();
                }else{
                    memoryTableOperationResult.increaseFailCount();
                }
            }
        }
        memoryTableOperationResult.setOperationSummary("MemoryTable deleteDataRecords Operation");
        memoryTableOperationResult.finishOperation();
        return memoryTableOperationResult;
    }

    public MemoryTableQueryResult queryDataRecords(String queryLogic) throws MemoryTableDataException {
        return queryDataRecordsBySQL(queryLogic);
    }

    public MemoryTableQueryResult queryDataRecords(QueryParameters queryParameters) throws MemoryTableDataException, MemoryTableQueryStructureException {
        String sliceName = this.cache.getName();
        String memoryTableQuerySQL = MemoryTableQueryBuilder.buildSelectQuerySQL(sliceName,queryParameters);
        return queryDataRecordsBySQL(memoryTableQuerySQL);
    }

    public void emptyMemoryTable(){
        this.cache.removeAll();
    }

    public MemoryTableMetaInfo getMemoryTableMetaInfo(){
        CacheConfiguration currentCacheConfig = this.cache.getConfiguration(CacheConfiguration.class);
        MemoryTableMetaInfo memoryTableMetaInfo = new MemoryTableMetaInfo();
        memoryTableMetaInfo.setMemoryTableName(currentCacheConfig.getName());
        memoryTableMetaInfo.setPrimaryDataCount(this.cache.size(CachePeekMode.PRIMARY));
        memoryTableMetaInfo.setBackupDataCount(this.cache.size(CachePeekMode.BACKUP));
        memoryTableMetaInfo.setTotalDataCount(this.cache.size(CachePeekMode.ALL));
        memoryTableMetaInfo.setStoreBackupNumber(currentCacheConfig.getBackups());
        CacheMode currentStoreCacheMode=currentCacheConfig.getCacheMode();
        String dataStoreMode="UNKNOWN";
        switch(currentStoreCacheMode){
            case PARTITIONED:dataStoreMode="Global";break;
            case LOCAL:dataStoreMode="Engine Local";break;
            case REPLICATED:dataStoreMode="Per Engine";break;
        }
        memoryTableMetaInfo.setMemoryTableMode(dataStoreMode);
        memoryTableMetaInfo.setAtomicityMode(""+currentCacheConfig.getAtomicityMode());
        memoryTableMetaInfo.setMemoryTableGroupName(""+currentCacheConfig.getSqlSchema());
        memoryTableMetaInfo.setKeyClass(currentCacheConfig.getKeyType());
        memoryTableMetaInfo.setValueClass(currentCacheConfig.getValueType());
        return memoryTableMetaInfo;
    }

    private Set<String> getMemoryTablePrimaryKeysInfo(){
        CacheConfiguration cfg = cache.getConfiguration(CacheConfiguration.class);
        Collection<QueryEntity> entities = cfg.getQueryEntities();
        if(entities != null && entities.size()>0){
            QueryEntity mainQueryEntity = entities.iterator().next();
            Set<String> keysField = mainQueryEntity.getKeyFields();
            return keysField;
        }
        return null;
    }

    private Map<String,String> getMemoryTablePropertiesInfo(){
        CacheConfiguration cfg = cache.getConfiguration(CacheConfiguration.class);
        Collection<QueryEntity> entities = cfg.getQueryEntities();
        if(entities != null && entities.size()>0){
            QueryEntity mainQueryEntity = entities.iterator().next();
            LinkedHashMap<String,String> propertiesMap = mainQueryEntity.getFields();
            return propertiesMap;
        }
        return null;
    }

    private String generatePropertiesValuePlaceHolder(List<String> propertiesNameList,String divStr){
        StringBuffer propertiesUpdatePartSb = new StringBuffer();
        for(int i = 0; i< propertiesNameList.size(); i++){
            String currentPropertyName = propertiesNameList.get(i);
            propertiesUpdatePartSb.append(currentPropertyName);
            propertiesUpdatePartSb.append(" = ? ");
            if(i < propertiesNameList.size()-1){
                propertiesUpdatePartSb.append(divStr+" ");
            }
        }
        return propertiesUpdatePartSb.toString();
    }

    private void validateDataProperties(Set<String> dataPropertyNameSet) throws MemoryTablePropertiesStructureException {
        Map<String,String> slicePropertiesMap = getMemoryTablePropertiesInfo();
        Set<String> slicePropertiesNameSet = slicePropertiesMap.keySet();
        for(String currentPropertyName:dataPropertyNameSet){
            if(!slicePropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new MemoryTablePropertiesStructureException();
            }
        }
    }

    private boolean insertDataRecordOperation(String insertType, Map<String,Object> dataPropertiesValue) throws MemoryTablePropertiesStructureException, MemoryTableDataException {
        Set<String> dataPropertyNameSet = dataPropertiesValue.keySet();
        validateDataProperties(dataPropertyNameSet);

        String[] dataPropertiesNameArray = dataPropertyNameSet.stream().toArray(n -> new String[n]);
        StringBuffer propertiesNameSb = new StringBuffer();
        StringBuffer propertiesValuePlaceHolderSb = new StringBuffer();
        propertiesNameSb.append("(");
        propertiesValuePlaceHolderSb.append("(");

        Object[] propertiesValueArray = new Object[dataPropertyNameSet.size()];

        for(int i = 0; i< dataPropertiesNameArray.length; i++){
            String currentDataPropertyName = dataPropertiesNameArray[i];
            // get dataType for property value validate
            //String dataType = slicePropertiesMap.get(currentDataPropertyName.toUpperCase());
            propertiesNameSb.append(currentDataPropertyName);
            propertiesValuePlaceHolderSb.append("?");

            if(i < dataPropertiesNameArray.length-1){
                propertiesNameSb.append(",");
                propertiesValuePlaceHolderSb.append(",");
            }
            propertiesValueArray[i] = dataPropertiesValue.get(currentDataPropertyName);
        }
        propertiesNameSb.append(")");
        propertiesValuePlaceHolderSb.append(")");

        String sliceName = this.cache.getName();

        String sqlFieldsQuerySQL = insertType+" "+sliceName+" "+propertiesNameSb.toString() +" VALUES "+propertiesValuePlaceHolderSb.toString();
        SqlFieldsQuery qry = new SqlFieldsQuery(sqlFieldsQuerySQL);

        try {
            List<List<?>> cursor = this.cache.query(qry.setArgs(propertiesValueArray)).getAll();
            for (List next : cursor) {
                for (Object item : next) {
                    if(item instanceof Long){
                        Long result = (Long) item;
                        if(result == 1){
                            return true;
                        }
                    }
                }
            }
        }catch (javax.cache.CacheException e){
            MemoryTableDataException memoryTableDataException = new MemoryTableDataException();
            memoryTableDataException.addSuppressed(e);
            throw memoryTableDataException;
        }
        return false;
    }

    private MemoryTableQueryResult queryDataRecordsBySQL(String querySQL) throws MemoryTableDataException {
        MemoryTableQueryResult memoryTableQueryResult = new MemoryTableQueryResult();
        memoryTableQueryResult.setQueryLogic(querySQL);
        SqlFieldsQuery qry = new SqlFieldsQuery(querySQL);
        try{
            FieldsQueryCursor<List<?>> cur = cache.query(qry);
            int columnsCount = cur.getColumnsCount();
            for (List<?> row : cur) {
                Map<String,Object> currentRecordValue = new HashMap<>();
                for(int i =0;i<columnsCount;i++){
                    currentRecordValue.put(
                            cur.getFieldName(i),
                            row.get(i)
                    );
                }
                memoryTableQueryResult.getResultRecords().add(currentRecordValue);
            }
        }catch (javax.cache.CacheException e){
            MemoryTableDataException memoryTableDataException = new MemoryTableDataException();
            memoryTableDataException.addSuppressed(e);
            throw memoryTableDataException;
        }
        memoryTableQueryResult.setOperationSummary("MemoryTable queryDataRecords Operation");
        memoryTableQueryResult.finishOperation();
        return memoryTableQueryResult;
    }
}
