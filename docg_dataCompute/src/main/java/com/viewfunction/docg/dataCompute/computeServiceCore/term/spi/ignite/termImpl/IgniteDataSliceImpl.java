package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceQueryResult;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.DataSliceQueryBuilder;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.DataSliceDataException;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.DataSlicePropertiesStructureException;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.DataSliceQueryStructureException;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSliceAtomicityMode;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSliceStoreMode;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteDataSlice;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.*;

public class IgniteDataSliceImpl implements IgniteDataSlice {
    private Ignite invokerIgnite;
    private IgniteCache<?, ?> cache;

    public IgniteDataSliceImpl(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;
    }

    @Override
    public boolean addDataRecord(Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException {
        return insertDataRecordOperation("INSERT INTO",dataPropertiesValue);
    }

    @Override
    public DataSliceOperationResult addDataRecords(List<String> propertiesNameList, List<Map<String,Object>> dataPropertiesValueList) throws DataSlicePropertiesStructureException{
        Map<String,String> slicePropertiesMap = getDataSlicePropertiesInfo();
        Set<String> slicePropertiesNameSet = slicePropertiesMap.keySet();

        for(String currentPropertyName:propertiesNameList){
            if(!slicePropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new DataSlicePropertiesStructureException();
            }
        }

        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();

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
                        dataSliceOperationResult.increaseSuccessCount();
                    }
                }
            }catch (javax.cache.CacheException e){
                e.printStackTrace();
                dataSliceOperationResult.increaseFailCount();
            }
        }
        dataSliceOperationResult.setOperationSummary("DataSlice addDataRecords Operation");

        dataSliceOperationResult.finishOperation();
        return dataSliceOperationResult;
    }

    @Override
    public boolean updateDataRecord(Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException {
        Set<String> dataPropertyNameSet = dataPropertiesValue.keySet();
        validateDataProperties(dataPropertyNameSet);

        Set<String> slicePrimaryKeySet = getDataSlicePrimaryKeysInfo();
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
            DataSliceDataException dataSliceDataException = new DataSliceDataException();
            dataSliceDataException.addSuppressed(e);
            throw dataSliceDataException;
        }
        return false;
    }

    @Override
    public boolean addOrUpdateDataRecord(Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException {
        return insertDataRecordOperation("MERGE INTO",dataPropertiesValue);
    }

    @Override
    public Map<String,Object> getDataRecordByPrimaryKeys(Map<String,Object> dataPKPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException {
        Set<String> slicePKPropertiesNameSet = getDataSlicePrimaryKeysInfo();
        Set<String> dataPropertyNameSet = dataPKPropertiesValue.keySet();

        if(dataPKPropertiesValue.size() != slicePKPropertiesNameSet.size()){
            throw new DataSlicePropertiesStructureException();
        }
        List<String> pkPropertiesNameList = new ArrayList<>();
        for(String currentPropertyName:dataPropertyNameSet){
            if(!slicePKPropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new DataSlicePropertiesStructureException();
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
            DataSliceDataException dataSliceDataException = new DataSliceDataException();
            dataSliceDataException.addSuppressed(e);
            throw dataSliceDataException;
        }
    }

    @Override
    public boolean deleteDataRecord(Map<String,Object> dataPKPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException {
        Set<String> slicePKPropertiesNameSet = getDataSlicePrimaryKeysInfo();
        Set<String> dataPropertyNameSet = dataPKPropertiesValue.keySet();

        if(dataPKPropertiesValue.size() != slicePKPropertiesNameSet.size()){
            throw new DataSlicePropertiesStructureException();
        }
        List<String> pkPropertiesNameList = new ArrayList<>();
        for(String currentPropertyName:dataPropertyNameSet){
            if(!slicePKPropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new DataSlicePropertiesStructureException();
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
            DataSliceDataException dataSliceDataException = new DataSliceDataException();
            dataSliceDataException.addSuppressed(e);
            throw dataSliceDataException;
        }
        return false;
    }

    @Override
    public DataSliceOperationResult deleteDataRecords(List<Map<String,Object>> dataPKPropertiesValueList) throws DataSlicePropertiesStructureException, DataSliceDataException{
        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();
        if(dataPKPropertiesValueList != null){
            for(Map<String,Object> currentDataMap:dataPKPropertiesValueList){
                boolean deleteResult = deleteDataRecord(currentDataMap);
                if(deleteResult){
                    dataSliceOperationResult.increaseSuccessCount();
                }else{
                    dataSliceOperationResult.increaseFailCount();
                }
            }
        }
        dataSliceOperationResult.setOperationSummary("DataSlice deleteDataRecords Operation");
        dataSliceOperationResult.finishOperation();
        return dataSliceOperationResult;
    }

    public DataSliceQueryResult queryDataRecords(String queryLogic) throws DataSliceDataException {
        return queryDataRecordsBySQL(queryLogic);
    }

    @Override
    public DataSliceQueryResult queryDataRecords(QueryParameters queryParameters) throws DataSliceDataException, DataSliceQueryStructureException {
        String sliceName = this.cache.getName();
        String dataSliceQuerySQL = DataSliceQueryBuilder.buildSelectQuerySQL(sliceName,queryParameters);
        return queryDataRecordsBySQL(dataSliceQuerySQL);
    }

    @Override
    public void emptyDataSlice(){
        this.cache.removeAll();
    }

    @Override
    public DataSliceMetaInfo getDataSliceMetaInfo(){
        CacheConfiguration currentCacheConfig = this.cache.getConfiguration(CacheConfiguration.class);
        DataSliceMetaInfo dataSliceMetaInfo = new DataSliceMetaInfo();
        dataSliceMetaInfo.setDataSliceName(currentCacheConfig.getName());
        dataSliceMetaInfo.setPrimaryDataCount(this.cache.size(CachePeekMode.PRIMARY));
        dataSliceMetaInfo.setBackupDataCount(this.cache.size(CachePeekMode.BACKUP));
        dataSliceMetaInfo.setTotalDataCount(this.cache.size(CachePeekMode.ALL));
        dataSliceMetaInfo.setStoreBackupNumber(currentCacheConfig.getBackups());
        CacheMode currentStoreCacheMode=currentCacheConfig.getCacheMode();
        switch(currentStoreCacheMode){
            case PARTITIONED -> dataSliceMetaInfo.setDataStoreMode(DataSliceStoreMode.Grid);
            case REPLICATED -> dataSliceMetaInfo.setDataStoreMode(DataSliceStoreMode.PerUnit);
        }
        switch(currentCacheConfig.getAtomicityMode()){
            case ATOMIC -> dataSliceMetaInfo.setAtomicityMode(DataSliceAtomicityMode.ATOMIC);
            case TRANSACTIONAL -> dataSliceMetaInfo.setAtomicityMode(DataSliceAtomicityMode.TRANSACTIONAL);
        }
        dataSliceMetaInfo.setSliceGroupName(""+currentCacheConfig.getSqlSchema());
        dataSliceMetaInfo.setKeyClass(currentCacheConfig.getKeyType());
        dataSliceMetaInfo.setValueClass(currentCacheConfig.getValueType());
        return dataSliceMetaInfo;
    }

    private Set<String> getDataSlicePrimaryKeysInfo(){
        CacheConfiguration cfg = cache.getConfiguration(CacheConfiguration.class);
        Collection<QueryEntity> entities = cfg.getQueryEntities();
        if(entities != null && entities.size()>0){
            QueryEntity mainQueryEntity = entities.iterator().next();
            Set<String> keysField = mainQueryEntity.getKeyFields();
            return keysField;
        }
        return null;
    }

    private Map<String,String> getDataSlicePropertiesInfo(){
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

    private void validateDataProperties(Set<String> dataPropertyNameSet) throws DataSlicePropertiesStructureException{
        Map<String,String> slicePropertiesMap = getDataSlicePropertiesInfo();
        Set<String> slicePropertiesNameSet = slicePropertiesMap.keySet();
        for(String currentPropertyName:dataPropertyNameSet){
            if(!slicePropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new DataSlicePropertiesStructureException();
            }
        }
    }

    private boolean insertDataRecordOperation(String insertType, Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException {
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
            DataSliceDataException dataSliceDataException = new DataSliceDataException();
            dataSliceDataException.addSuppressed(e);
            throw dataSliceDataException;
        }
        return false;
    }

    private DataSliceQueryResult queryDataRecordsBySQL(String querySQL) throws DataSliceDataException {
        DataSliceQueryResult dataSliceQueryResult= new DataSliceQueryResult();
        dataSliceQueryResult.setQueryLogic(querySQL);
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
                dataSliceQueryResult.getResultRecords().add(currentRecordValue);
            }
        }catch (javax.cache.CacheException e){
            DataSliceDataException dataSliceDataException = new DataSliceDataException();
            dataSliceDataException.addSuppressed(e);
            throw dataSliceDataException;
        }
        dataSliceQueryResult.setOperationSummary("DataSlice queryDataRecords Operation");
        dataSliceQueryResult.finishOperation();
        return dataSliceQueryResult;
    }
}
