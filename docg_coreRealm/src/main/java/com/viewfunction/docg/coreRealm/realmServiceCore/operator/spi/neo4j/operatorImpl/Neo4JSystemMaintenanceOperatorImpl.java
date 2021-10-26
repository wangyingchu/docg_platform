package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListAttributeSystemInfoTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Neo4JSystemMaintenanceOperatorImpl implements SystemMaintenanceOperator {

    private String coreRealmName;
    private GraphOperationExecutorHelper graphOperationExecutorHelper;
    private static Logger logger = LoggerFactory.getLogger(Neo4JSystemMaintenanceOperatorImpl.class);

    public Neo4JSystemMaintenanceOperatorImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public DataStatusSnapshotInfo getDataStatusSnapshot() {
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.meta/apoc.meta.stats/
        */
        String cypherProcedureString = "CALL apoc.meta.stats();";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            DataTransformer<DataStatusSnapshotInfo> staticHandleDataTransformer = new DataTransformer() {

                @Override
                public DataStatusSnapshotInfo transformResult(Result result) {
                    if(result.hasNext()){
                        Record staticRecord = result.next();
                        Map<String,Object> staticResultMap = staticRecord.asMap();
                        long wholeConceptionEntityCount = ((Number) staticResultMap.get("nodeCount")).longValue();
                        long wholeRelationEntityCount = ((Number) staticResultMap.get("relCount")).longValue();
                        int wholeConceptionKindCount = ((Number) staticResultMap.get("labelCount")).intValue();
                        int wholeRelationKindCount = ((Number) staticResultMap.get("relTypeCount")).intValue();
                        int wholePhysicAttributeNameCount = ((Number) staticResultMap.get("propertyKeyCount")).intValue();
                        Map<String,Long> conceptionKindsDataCount = new HashMap<>();
                        Map<String,Long> relationKindsDataCount = new HashMap<>();
                        List<RuntimeRelationAndConceptionKindAttachInfo> relationAndConceptionKindAttachInfo = new ArrayList<>();

                        Map<String,Object> conceptionKindsDataCountData = (Map<String,Object>)staticResultMap.get("labels");
                        Set<String> conceptionKindName = conceptionKindsDataCountData.keySet();
                        for(String currentKind:conceptionKindName){
                            long currentDataCount = ((Number) conceptionKindsDataCountData.get(currentKind)).longValue();
                            conceptionKindsDataCount.put(currentKind,currentDataCount);
                        }

                        Map<String,Object> relationKindsDataCountData = (Map<String,Object>)staticResultMap.get("relTypesCount");
                        Set<String> relationKindName = relationKindsDataCountData.keySet();
                        for(String currentKind:relationKindName){
                            long currentDataCount = ((Number) relationKindsDataCountData.get(currentKind)).longValue();
                            relationKindsDataCount.put(currentKind,currentDataCount);
                        }

                        Map<String,Object> relationAndConceptionAttachData = (Map<String,Object>)staticResultMap.get("relTypes");
                        Set<String> attachInfo = relationAndConceptionAttachData.keySet();
                        for(String currentAttachInfo : attachInfo){
                            long currentDataCount = ((Number) relationAndConceptionAttachData.get(currentAttachInfo)).longValue();
                            RuntimeRelationAndConceptionKindAttachInfo currentRuntimeRelationAndConceptionKindAttachInfo =
                                    getRuntimeRelationAndConceptionKindAttachInfo(currentAttachInfo,currentDataCount);
                            if(currentRuntimeRelationAndConceptionKindAttachInfo != null){
                                relationAndConceptionKindAttachInfo.add(currentRuntimeRelationAndConceptionKindAttachInfo);
                            }
                        }

                        DataStatusSnapshotInfo dataStatusSnapshotInfo = new DataStatusSnapshotInfo(wholeConceptionEntityCount,
                                wholeRelationEntityCount,wholeConceptionKindCount,wholeRelationKindCount,wholePhysicAttributeNameCount,
                                conceptionKindsDataCount,relationKindsDataCount,relationAndConceptionKindAttachInfo);
                        return dataStatusSnapshotInfo;
                    }
                    return null;
                }
            };
            Object responseObj = workingGraphOperationExecutor.executeRead(staticHandleDataTransformer,cypherProcedureString);
            return responseObj != null ? (DataStatusSnapshotInfo)responseObj : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public SystemStatusSnapshotInfo getSystemStatusSnapshot() {
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.meta/apoc.meta.schema/
        https://neo4j.com/labs/apoc/4.1/overview/apoc.monitor/apoc.monitor.tx/
        https://neo4j.com/labs/apoc/4.1/overview/apoc.monitor/apoc.monitor.store/
        */
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            SystemStatusSnapshotInfo systemStatusSnapshotInfo = new SystemStatusSnapshotInfo();

            String cypherProcedureString = "CALL apoc.monitor.kernel()";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
            DataTransformer<Object> dataTransformer1 = new DataTransformer() {

                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()) {
                        Record staticRecord = result.next();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
                        try {
                            Date systemStartupTime = simpleDateFormat.parse(staticRecord.get("kernelStartTime").toString().replaceAll("\"",""));
                            systemStatusSnapshotInfo.setSystemStartupTime(systemStartupTime);
                            Date systemCreateTime = simpleDateFormat.parse(staticRecord.get("storeCreationDate").toString().replaceAll("\"",""));
                            systemStatusSnapshotInfo.setSystemCreateTime(systemCreateTime);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer1,cypherProcedureString);

            cypherProcedureString = "CALL apoc.monitor.tx()";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
            DataTransformer<Object> dataTransformer2 = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()) {
                        Record staticRecord = result.next();
                        long peakRequestCount = staticRecord.get("peakTx").asLong();
                        systemStatusSnapshotInfo.setPeakRequestCount(peakRequestCount);
                        long totalAcceptedRequestCount = staticRecord.get("totalOpenedTx").asLong();
                        systemStatusSnapshotInfo.setTotalAcceptedRequestCount(totalAcceptedRequestCount);
                        long currentAcceptedRequestCount = staticRecord.get("currentOpenedTx").asLong();
                        systemStatusSnapshotInfo.setCurrentAcceptedRequestCount(currentAcceptedRequestCount);
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer2,cypherProcedureString);

            cypherProcedureString = "CALL apoc.metrics.storage('dbms.directories.data')";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
            DataTransformer<Object> dataTransformer3 = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()) {
                        Record staticRecord = result.next();
                        long totalDiskSpaceSize = staticRecord.get("totalSpaceBytes").asLong();
                        systemStatusSnapshotInfo.setTotalDiskSpaceSize(totalDiskSpaceSize);
                        long freeDiskSpaceSize = staticRecord.get("freeSpaceBytes").asLong();
                        systemStatusSnapshotInfo.setFreeDiskSpaceSize(freeDiskSpaceSize);
                        long usableDiskSpaceSize = staticRecord.get("usableSpaceBytes").asLong();
                        systemStatusSnapshotInfo.setUsableDiskSpaceSize(usableDiskSpaceSize);
                        double freeDiskPercent = staticRecord.get("percentFree").asDouble();
                        systemStatusSnapshotInfo.setFreeDiskPercent(freeDiskPercent);
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer3,cypherProcedureString);

            return systemStatusSnapshotInfo;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<AttributeSystemInfo> getConceptionKindAttributesSystemInfo(String conceptionKindName) {
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.meta/apoc.meta.schema/
        https://neo4j.com/developer/kb/viewing-schema-data-with-apoc/
        */
        String cypherProcedureString = "CALL apoc.meta.schema() yield value\n" +
                "UNWIND apoc.map.sortedProperties(value) as labelData\n" +
                "WITH labelData[0] as label, labelData[1] as data\n" +
                "WHERE data.type = \"node\" AND label = \""+conceptionKindName+"\"\n" +
                "UNWIND apoc.map.sortedProperties(data.properties) as property\n" +
                "WITH label, property[0] as property, property[1] as propData\n" +
                "RETURN label,\n" +
                "property,\n" +
                "propData.type as type,\n" +
                "propData.indexed as isIndexed,\n" +
                "propData.unique as uniqueConstraint,\n" +
                "propData.existence as existenceConstraint";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListAttributeSystemInfoTransformer getListAttributeSystemInfoTransformer =
                    new GetListAttributeSystemInfoTransformer(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object responseObj = workingGraphOperationExecutor.executeRead(getListAttributeSystemInfoTransformer,cypherProcedureString);
            return responseObj != null ? (List<AttributeSystemInfo>)responseObj : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<AttributeSystemInfo> getRelationKindAttributesSystemInfo(String relationKindName) {
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.meta/apoc.meta.schema/
        https://neo4j.com/developer/kb/viewing-schema-data-with-apoc/
        */
        String cypherProcedureString =
                "CALL apoc.meta.schema() yield value\n" +
                "UNWIND apoc.map.sortedProperties(value) as labelData\n" +
                "WITH labelData[0] as label, labelData[1] as data\n" +
                "WHERE data.type = \"relationship\" AND label = \""+relationKindName+"\"\n" +
                "UNWIND apoc.map.sortedProperties(data.properties) as property\n" +
                "WITH label, property[0] as property, property[1] as propData\n" +
                "RETURN label,\n" +
                "property,\n" +
                "propData.type as type,\n" +
                "propData.indexed as isIndexed,\n" +
                "propData.unique as uniqueConstraint,\n" +
                "propData.existence as existenceConstraint";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListAttributeSystemInfoTransformer getListAttributeSystemInfoTransformer =
                    new GetListAttributeSystemInfoTransformer(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object responseObj = workingGraphOperationExecutor.executeRead(getListAttributeSystemInfoTransformer,cypherProcedureString);
            return responseObj != null ? (List<AttributeSystemInfo>)responseObj : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean createConceptionKindSearchIndex(String indexName, SearchIndexType indexType, String conceptionKindName, Set<String> indexAttributeNames) throws CoreRealmServiceRuntimeException {
        /*
        https://neo4j.com/docs/cypher-manual/4.3/indexes-for-search-performance/
        */
        if(indexName == null){
            logger.error("Index Name is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("Index Name is required");
            throw e;
        }
        if(conceptionKindName == null){
            logger.error("Conception Kind Name is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("Conception Kind Name is required");
            throw e;
        }
        if(indexAttributeNames == null || indexAttributeNames.size() ==0){
            logger.error("At least one attributeName is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("At least one attributeName is required");
            throw e;
        }
        String searchIndexType = "";
        if(indexType != null){
            searchIndexType = "" + indexType;
        }
        Iterator<String> nameIterator = indexAttributeNames.iterator();
        String attributeDefineString = "";
        while(nameIterator.hasNext()){
            attributeDefineString = attributeDefineString+"n."+nameIterator.next();
            if(nameIterator.hasNext()){
                attributeDefineString = attributeDefineString+",";
            }
        }

        String cypherProcedureString = "CREATE "+searchIndexType+" INDEX "+indexName+" IF NOT EXISTS FOR (n:"+conceptionKindName+") ON ("+attributeDefineString+")";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                if(result.hasNext()){
                    Record currentRecord = result.next();
                    System.out.println(currentRecord);
                    System.out.println(currentRecord);
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object queryResponse = workingGraphOperationExecutor.executeWrite(dataTransformer,cypherProcedureString);
            if(queryResponse != null){

            }

        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }


        return false;
    }

    @Override
    public boolean createRelationKindSearchIndex(String indexName, SearchIndexType indexType, String relationKindName, Set<String> indexAttributeNames) throws CoreRealmServiceRuntimeException{
        /*
        https://neo4j.com/docs/cypher-manual/4.3/indexes-for-search-performance/
        */
        return false;
    }

    @Override
    public Set<SearchIndexInfo> listConceptionKindSearchIndex(){
        /*
        https://neo4j.com/docs/cypher-manual/4.3/indexes-for-search-performance/
        */
        String cypherProcedureString ="SHOW INDEXES YIELD name,populationPercent,type,entityType,labelsOrTypes,properties WHERE entityType ='NODE' AND labelsOrTypes IS NOT NULL";

        Set<SearchIndexInfo> searchIndexInfoSet = new HashSet<>();

        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record currentRecord = result.next();
                    String name = currentRecord.get("name").asString();
                    float populationPercent = currentRecord.get("populationPercent").asNumber().floatValue();
                    String type = currentRecord.get("type").asString();
                    String entityType = currentRecord.get("entityType").asString();
                    List labelsOrTypes = currentRecord.get("labelsOrTypes").asList();
                    List properties = currentRecord.get("properties").asList();
                    SearchIndexInfo currentSearchIndexInfo = new SearchIndexInfo(name,populationPercent,type,labelsOrTypes,properties);
                    searchIndexInfoSet.add(currentSearchIndexInfo);
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeWrite(dataTransformer,cypherProcedureString);
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return searchIndexInfoSet;
    }

    @Override
    public Set<SearchIndexInfo> listRelationKindSearchIndex() {
        /*
        https://neo4j.com/docs/cypher-manual/4.3/indexes-for-search-performance/
        */
        String cypherProcedureString ="SHOW INDEXES YIELD name,populationPercent,type,entityType,labelsOrTypes,properties WHERE entityType ='RELATIONSHIP' AND labelsOrTypes IS NOT NULL";
        return null;
    }

    @Override
    public boolean removeConceptionKindSearchIndex(String indexName) throws CoreRealmServiceRuntimeException{
        return false;
    }

    @Override
    public boolean removeRelationKindSearchIndex(String indexName) throws CoreRealmServiceRuntimeException{
        return false;
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    private RuntimeRelationAndConceptionKindAttachInfo getRuntimeRelationAndConceptionKindAttachInfo(String attachInfoString,long relationEntityCount){
        if(attachInfoString.startsWith("()-[:") & attachInfoString.endsWith("]->()")){
            //Only contains relationKind info, skip it.
        }else{
            if(attachInfoString.startsWith("()-[:")){
                int relationTypeEndIndex = attachInfoString.indexOf("]->(:");
                String relationType = attachInfoString.substring(5,relationTypeEndIndex);
                String conceptionType = attachInfoString.substring(relationTypeEndIndex+5,attachInfoString.length()-1);
                RuntimeRelationAndConceptionKindAttachInfo runtimeRelationAndConceptionKindAttachInfo =
                        new RuntimeRelationAndConceptionKindAttachInfo(relationType,conceptionType, RelationDirection.TO,relationEntityCount);
                return runtimeRelationAndConceptionKindAttachInfo;
            }
            if(attachInfoString.endsWith("]->()")){
                int relationTypeStartIndex = attachInfoString.indexOf(")-[:");
                int relationTypeEndIndex = attachInfoString.indexOf("]->()");
                String relationType = attachInfoString.substring(relationTypeStartIndex+4,relationTypeEndIndex);
                String conceptionType = attachInfoString.substring(2,relationTypeStartIndex);
                RuntimeRelationAndConceptionKindAttachInfo runtimeRelationAndConceptionKindAttachInfo =
                        new RuntimeRelationAndConceptionKindAttachInfo(relationType,conceptionType, RelationDirection.FROM,relationEntityCount);
                return runtimeRelationAndConceptionKindAttachInfo;
            }
        }
        return null;
    }
}
