package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JExternalAttributesValueAccessible;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

            cypherProcedureString = "call dbms.components() yield name, versions, edition unwind versions as version return name, version, edition";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
            DataTransformer<Object> dataTransformer3 = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()) {
                        Record staticRecord = result.next();
                        String systemImplementationTech = staticRecord.get("name").asString();
                        systemStatusSnapshotInfo.setSystemImplementationTech(systemImplementationTech);
                        String systemImplementationVersion = staticRecord.get("version").asString();
                        systemStatusSnapshotInfo.setSystemImplementationVersion(systemImplementationVersion);
                        String systemImplementationEdition = staticRecord.get("edition").asString();
                        systemStatusSnapshotInfo.setSystemImplementationEdition(systemImplementationEdition);
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer3,cypherProcedureString);

            if(systemStatusSnapshotInfo.getSystemImplementationVersion().startsWith("4")){
                //for neo4j v4
                cypherProcedureString = "CALL apoc.metrics.storage('dbms.directories.data')";
            }else{
                //for neo4j v5 and above
                cypherProcedureString = "CALL apoc.metrics.storage('server.directories.data')";
            }
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
            DataTransformer<Object> dataTransformer4 = new DataTransformer() {
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
            workingGraphOperationExecutor.executeRead(dataTransformer4,cypherProcedureString);

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
    public Map<String, List<AttributeSystemInfo>> getAllConceptionKindsAttributesSystemInfo() {
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.meta/apoc.meta.schema/
        https://neo4j.com/developer/kb/viewing-schema-data-with-apoc/
        */
        String cypherProcedureString = "CALL apoc.meta.schema() yield value\n" +
                "UNWIND apoc.map.sortedProperties(value) as labelData\n" +
                "WITH labelData[0] as label, labelData[1] as data\n" +
                "WHERE data.type = \"node\" \n" +
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
            GetMapAttributeSystemInfoTransformer getMapAttributeSystemInfoTransformer =
                    new GetMapAttributeSystemInfoTransformer(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object responseObj = workingGraphOperationExecutor.executeRead(getMapAttributeSystemInfoTransformer,cypherProcedureString);
            return responseObj != null ? (Map<String, List<AttributeSystemInfo>>)responseObj : null;
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
    public Map<String, List<AttributeSystemInfo>> getAllRelationKindsAttributesSystemInfo() {
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.meta/apoc.meta.schema/
        https://neo4j.com/developer/kb/viewing-schema-data-with-apoc/
        */
        String cypherProcedureString =
                "CALL apoc.meta.schema() yield value\n" +
                        "UNWIND apoc.map.sortedProperties(value) as labelData\n" +
                        "WITH labelData[0] as label, labelData[1] as data\n" +
                        "WHERE data.type = \"relationship\" \n" +
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
            GetMapAttributeSystemInfoTransformer getMapAttributeSystemInfoTransformer =
                    new GetMapAttributeSystemInfoTransformer(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object responseObj = workingGraphOperationExecutor.executeRead(getMapAttributeSystemInfoTransformer,cypherProcedureString);
            return responseObj != null ? (Map<String, List<AttributeSystemInfo>>)responseObj : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean createConceptionKindSearchIndex(String indexName, String conceptionKindName, Set<String> indexAttributeNames) throws CoreRealmServiceRuntimeException {
        /*
        https://neo4j.com/docs/cypher-manual/current/indexes-for-search-performance/
        */
        if(indexName == null){
            logger.error("Index Name is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("Index Name is required");
            throw e;
        }
        Set<SearchIndexInfo> existIndexSet = listConceptionKindSearchIndex();
        for(SearchIndexInfo currentSearchIndexInfo:existIndexSet){
            if(currentSearchIndexInfo.getIndexName().equals(indexName)){
                logger.error("ConceptionKind Search Index with name {} already exist." , indexName);
                CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
                e.setCauseMessage("ConceptionKind Search Index with name "+ indexName +" already exist.");
                throw e;
            }
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

        Iterator<String> nameIterator = indexAttributeNames.iterator();
        String attributeDefineString = "";
        while(nameIterator.hasNext()){
            attributeDefineString = attributeDefineString+"n."+"`"+nameIterator.next()+"`";
            if(nameIterator.hasNext()){
                attributeDefineString = attributeDefineString+",";
            }
        }

        String cypherProcedureString = "CREATE "+searchIndexType+" INDEX "+indexName+" IF NOT EXISTS FOR (n:"+conceptionKindName+") ON ("+attributeDefineString+")";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        existIndexSet = listConceptionKindSearchIndex();
        for(SearchIndexInfo currentSearchIndexInfo:existIndexSet){
            if(currentSearchIndexInfo.getIndexName().equals(indexName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean createRelationKindSearchIndex(String indexName, String relationKindName, Set<String> indexAttributeNames) throws CoreRealmServiceRuntimeException{
        /*
        https://neo4j.com/docs/cypher-manual/current/indexes-for-search-performance/
        */
        if(indexName == null){
            logger.error("Index Name is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("Index Name is required");
            throw e;
        }
        Set<SearchIndexInfo> existIndexSet = listRelationKindSearchIndex();
        for(SearchIndexInfo currentSearchIndexInfo:existIndexSet){
            if(currentSearchIndexInfo.getIndexName().equals(indexName)){
                logger.error("RelationKind Search Index with name {} already exist." , indexName);
                CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
                e.setCauseMessage("RelationKind Search Index with name "+ indexName +" already exist.");
                throw e;
            }
        }
        if(relationKindName == null){
            logger.error("Relation Kind Name is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("Relation Kind Name is required");
            throw e;
        }
        if(indexAttributeNames == null || indexAttributeNames.size() ==0){
            logger.error("At least one attributeName is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("At least one attributeName is required");
            throw e;
        }
        String searchIndexType = "";

        Iterator<String> nameIterator = indexAttributeNames.iterator();
        String attributeDefineString = "";
        while(nameIterator.hasNext()){
            attributeDefineString = attributeDefineString+"r."+ "`"+nameIterator.next()+"`";
            if(nameIterator.hasNext()){
                attributeDefineString = attributeDefineString+",";
            }
        }

        String cypherProcedureString = "CREATE "+searchIndexType+" INDEX "+indexName+" IF NOT EXISTS FOR ()-[r:"+relationKindName+"]-() ON ("+attributeDefineString+")";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        existIndexSet = listRelationKindSearchIndex();
        for(SearchIndexInfo currentSearchIndexInfo:existIndexSet){
            if(currentSearchIndexInfo.getIndexName().equals(indexName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<SearchIndexInfo> listConceptionKindSearchIndex(){
        return listKindSearchIndex("NODE");
    }

    @Override
    public Set<SearchIndexInfo> listRelationKindSearchIndex() {
        return listKindSearchIndex("RELATIONSHIP");
    }

    @Override
    public boolean removeConceptionKindSearchIndex(String indexName) throws CoreRealmServiceRuntimeException{
        /*
        https://neo4j.com/docs/cypher-manual/4.3/indexes-for-search-performance/
        */
        if(indexName == null){
            logger.error("Index Name is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("Index Name is required");
            throw e;
        }
        boolean indexExist = false;
        Set<SearchIndexInfo> existIndexSet = listConceptionKindSearchIndex();
        for(SearchIndexInfo currentSearchIndexInfo:existIndexSet){
            if(currentSearchIndexInfo.getIndexName().equals(indexName)){
                indexExist = true;
                break;
            }
        }
        if(!indexExist){
            logger.error("ConceptionKind Search Index with name {} does not exist." , indexName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("ConceptionKind Search Index with name "+ indexName +" does not exist.");
            throw e;
        }

        String cypherProcedureString = "DROP INDEX " + indexName + " IF EXISTS";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        existIndexSet = listConceptionKindSearchIndex();
        for(SearchIndexInfo currentSearchIndexInfo:existIndexSet){
            if(currentSearchIndexInfo.getIndexName().equals(indexName)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeRelationKindSearchIndex(String indexName) throws CoreRealmServiceRuntimeException{
        /*
        https://neo4j.com/docs/cypher-manual/4.3/indexes-for-search-performance/
        */
        if(indexName == null){
            logger.error("Index Name is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("Index Name is required");
            throw e;
        }
        boolean indexExist = false;
        Set<SearchIndexInfo> existIndexSet = listRelationKindSearchIndex();
        for(SearchIndexInfo currentSearchIndexInfo:existIndexSet){
            if(currentSearchIndexInfo.getIndexName().equals(indexName)){
                indexExist = true;
                break;
            }
        }
        if(!indexExist){
            logger.error("RelationKind Search Index with name {} does not exist." , indexName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("RelationKind Search Index with name "+ indexName +" does not exist.");
            throw e;
        }

        String cypherProcedureString = "DROP INDEX " + indexName + " IF EXISTS";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        existIndexSet = listRelationKindSearchIndex();
        for(SearchIndexInfo currentSearchIndexInfo:existIndexSet){
            if(currentSearchIndexInfo.getIndexName().equals(indexName)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Set<ConceptionKindCorrelationInfo> getSystemConceptionKindsRelationDistributionStatistics() {
        String cql ="CALL db.schema.visualization()";
        logger.debug("Generated Cypher Statement: {}", cql);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Set<ConceptionKindCorrelationInfo>> statisticsDataTransformer = new DataTransformer(){
                @Override
                public Set<ConceptionKindCorrelationInfo> transformResult(Result result) {
                    Record currentRecord = result.next();
                    List<Object> nodesList = currentRecord.get("nodes").asList();
                    List<Object> relationshipsList = currentRecord.get("relationships").asList();

                    Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet = new HashSet<>();
                    Map<String,String> conceptionKindId_nameMapping = new HashMap<>();
                    for(Object currentNodeObj:nodesList){
                        Node currentNode = (Node)currentNodeObj;
                        long currentNodeId = currentNode.id();
                        String currentConceptionKindName = currentNode.labels().iterator().next();
                        conceptionKindId_nameMapping.put(""+currentNodeId,currentConceptionKindName);
                    }

                    int degreeOfParallelism = 6;
                    int singlePartitionSize = (relationshipsList.size()/degreeOfParallelism)+1;

                    if(relationshipsList.size()>0){
                        List<List<Object>> rsList = Lists.partition(relationshipsList, singlePartitionSize);
                        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
                        for(List<Object> currentRelationEntityValueList:rsList){
                            CheckConceptionKindsRelationEntitiesExistThread checkConceptionKindsRelationEntitiesExistThread = new CheckConceptionKindsRelationEntitiesExistThread(currentRelationEntityValueList,conceptionKindId_nameMapping,conceptionKindCorrelationInfoSet);
                            executor.execute(checkConceptionKindsRelationEntitiesExistThread);
                        }
                        executor.shutdown();
                        try {
                            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    /*
                    for(Object currentRelationshipObj:relationshipsList){
                        Relationship currentRelationship = (Relationship)currentRelationshipObj;
                        String relationshipType = currentRelationship.type();
                        String startConceptionKindId = ""+currentRelationship.startNodeId();
                        String endConceptionKindId = ""+currentRelationship.endNodeId();
                        boolean relationExist = checkRelationEntitiesExist(workingGraphOperationExecutor,conceptionKindId_nameMapping.get(startConceptionKindId),conceptionKindId_nameMapping.get(endConceptionKindId),relationshipType);
                        if(relationExist){
                            if(conceptionKindId_nameMapping.get(startConceptionKindId).startsWith(RealmConstant.RealmInnerTypePerFix) &
                                    conceptionKindId_nameMapping.get(endConceptionKindId).startsWith(RealmConstant.RealmInnerTypePerFix)){
                            }else{
                                ConceptionKindCorrelationInfo currentConceptionKindCorrelationInfo =
                                        new ConceptionKindCorrelationInfo(
                                                conceptionKindId_nameMapping.get(startConceptionKindId),
                                                conceptionKindId_nameMapping.get(endConceptionKindId),
                                                relationshipType,1);
                                conceptionKindCorrelationInfoSet.add(currentConceptionKindCorrelationInfo);
                            }
                        }
                    }
                    */
                    return conceptionKindCorrelationInfoSet;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(statisticsDataTransformer,cql);
            if(queryRes != null){
                return (Set<ConceptionKindCorrelationInfo>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    private class CheckConceptionKindsRelationEntitiesExistThread implements Runnable{
        private List<Object> relationEntityValueList;
        private Map<String,String> conceptionKindId_nameMapping;
        private Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet;
        public CheckConceptionKindsRelationEntitiesExistThread(List<Object> relationEntityValueList,Map<String,String> conceptionKindId_nameMapping,Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet){
            this.relationEntityValueList = relationEntityValueList;
            this.conceptionKindId_nameMapping = conceptionKindId_nameMapping;
            this.conceptionKindCorrelationInfoSet = conceptionKindCorrelationInfoSet;
        }

        @Override
        public void run() {
            for(Object currentRelationshipObj:relationEntityValueList){
                Relationship currentRelationship = (Relationship)currentRelationshipObj;
                String relationshipType = currentRelationship.type();
                String startConceptionKindId = ""+currentRelationship.startNodeId();
                String endConceptionKindId = ""+currentRelationship.endNodeId();
                GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
                boolean relationExist = checkRelationEntitiesExist(graphOperationExecutor,conceptionKindId_nameMapping.get(startConceptionKindId),conceptionKindId_nameMapping.get(endConceptionKindId),relationshipType);
                if(relationExist){
                    if(!conceptionKindId_nameMapping.get(startConceptionKindId).startsWith(RealmConstant.RealmInnerTypePerFix) &
                            !conceptionKindId_nameMapping.get(endConceptionKindId).startsWith(RealmConstant.RealmInnerTypePerFix)){
                        ConceptionKindCorrelationInfo currentConceptionKindCorrelationInfo =
                                new ConceptionKindCorrelationInfo(
                                        conceptionKindId_nameMapping.get(startConceptionKindId),
                                        conceptionKindId_nameMapping.get(endConceptionKindId),
                                        relationshipType,1);
                        conceptionKindCorrelationInfoSet.add(currentConceptionKindCorrelationInfo);
                    }
                }
            }
        }
    }

    @Override
    public Set<ConceptionKindCorrelationInfo> getAllDataRelationDistributionStatistics() {
        String cql ="CALL db.schema.visualization()";
        logger.debug("Generated Cypher Statement: {}", cql);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Set<ConceptionKindCorrelationInfo>> statisticsDataTransformer = new DataTransformer(){
                @Override
                public Set<ConceptionKindCorrelationInfo> transformResult(Result result) {
                    Record currentRecord = result.next();
                    List<Object> nodesList = currentRecord.get("nodes").asList();
                    List<Object> relationshipsList = currentRecord.get("relationships").asList();

                    Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet = new HashSet<>();
                    Map<String,String> conceptionKindId_nameMapping = new HashMap<>();
                    for(Object currentNodeObj:nodesList){
                        Node currentNode = (Node)currentNodeObj;
                        long currentNodeId = currentNode.id();
                        String currentConceptionKindName = currentNode.labels().iterator().next();
                        conceptionKindId_nameMapping.put(""+currentNodeId,currentConceptionKindName);
                    }

                    int degreeOfParallelism = 6;
                    int singlePartitionSize = (relationshipsList.size()/degreeOfParallelism)+1;

                    List<List<Object>> rsList = Lists.partition(relationshipsList, singlePartitionSize);

                    if(rsList.size() > 0){
                        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
                        for(List<Object> currentRelationEntityValueList:rsList){
                            CheckRelationEntitiesExistThread checkRelationEntitiesExistThread = new CheckRelationEntitiesExistThread(currentRelationEntityValueList,conceptionKindId_nameMapping,conceptionKindCorrelationInfoSet);
                            executor.execute(checkRelationEntitiesExistThread);
                        }
                        executor.shutdown();
                        try {
                            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return conceptionKindCorrelationInfoSet;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(statisticsDataTransformer,cql);
            if(queryRes != null){
                return (Set<ConceptionKindCorrelationInfo>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    private class CheckRelationEntitiesExistThread implements Runnable{
        private List<Object> relationEntityValueList;
        private Map<String,String> conceptionKindId_nameMapping;
        private Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet;
        public CheckRelationEntitiesExistThread(List<Object> relationEntityValueList,Map<String,String> conceptionKindId_nameMapping,Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet){
            this.relationEntityValueList = relationEntityValueList;
            this.conceptionKindId_nameMapping = conceptionKindId_nameMapping;
            this.conceptionKindCorrelationInfoSet = conceptionKindCorrelationInfoSet;
        }

        @Override
        public void run() {
            GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
            for(Object currentRelationshipObj:relationEntityValueList){
                Relationship currentRelationship = (Relationship)currentRelationshipObj;
                String relationshipType = currentRelationship.type();
                String startConceptionKindId = ""+currentRelationship.startNodeId();
                String endConceptionKindId = ""+currentRelationship.endNodeId();

                boolean relationExist = checkRelationEntitiesExist(graphOperationExecutor,conceptionKindId_nameMapping.get(startConceptionKindId),conceptionKindId_nameMapping.get(endConceptionKindId),relationshipType);
                if(relationExist){
                    ConceptionKindCorrelationInfo currentConceptionKindCorrelationInfo =
                            new ConceptionKindCorrelationInfo(
                                    conceptionKindId_nameMapping.get(startConceptionKindId),
                                    conceptionKindId_nameMapping.get(endConceptionKindId),
                                    relationshipType,1);
                    conceptionKindCorrelationInfoSet.add(currentConceptionKindCorrelationInfo);
                }
            }
            graphOperationExecutor.close();
        }
    }

    @Override
    public Set<ConceptionKindCorrelationInfo> getAllDataRelationDistributionDetailStatistics() {
        String cql ="CALL db.schema.visualization()";
        logger.debug("Generated Cypher Statement: {}", cql);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Set<ConceptionKindCorrelationInfo>> statisticsDataTransformer = new DataTransformer(){
                @Override
                public Set<ConceptionKindCorrelationInfo> transformResult(Result result) {
                    Record currentRecord = result.next();
                    List<Object> nodesList = currentRecord.get("nodes").asList();
                    List<Object> relationshipsList = currentRecord.get("relationships").asList();

                    Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet = new HashSet<>();
                    Map<String,String> conceptionKindId_nameMapping = new HashMap<>();
                    for(Object currentNodeObj:nodesList){
                        Node currentNode = (Node)currentNodeObj;
                        long currentNodeId = currentNode.id();
                        String currentConceptionKindName = currentNode.labels().iterator().next();
                        conceptionKindId_nameMapping.put(""+currentNodeId,currentConceptionKindName);
                    }
                    for(Object currentRelationshipObj:relationshipsList){
                        Relationship currentRelationship = (Relationship)currentRelationshipObj;
                        String relationshipType = currentRelationship.type();
                        String startConceptionKindId = ""+currentRelationship.startNodeId();
                        String endConceptionKindId = ""+currentRelationship.endNodeId();
                        long relationEntitiesCount = checkRelationEntitiesCount(workingGraphOperationExecutor,conceptionKindId_nameMapping.get(startConceptionKindId),conceptionKindId_nameMapping.get(endConceptionKindId),relationshipType);
                        if(relationEntitiesCount > 0){
                            ConceptionKindCorrelationInfo currentConceptionKindCorrelationInfo =
                                    new ConceptionKindCorrelationInfo(
                                            conceptionKindId_nameMapping.get(startConceptionKindId),
                                            conceptionKindId_nameMapping.get(endConceptionKindId),
                                            relationshipType,relationEntitiesCount);
                            conceptionKindCorrelationInfoSet.add(currentConceptionKindCorrelationInfo);
                        }
                    }
                    return conceptionKindCorrelationInfoSet;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(statisticsDataTransformer,cql);
            if(queryRes != null){
                return (Set<ConceptionKindCorrelationInfo>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public Set<String> getRealtimeAttributesStatistics() {
        String cql ="CALL db.propertyKeys()";
        logger.debug("Generated Cypher Statement: {}", cql);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Set<String>> statisticsDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    Set<String> attributeNamesSet = new HashSet<>();
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        if(nodeRecord.containsKey("propertyKey")){
                            attributeNamesSet.add(nodeRecord.get("propertyKey").asString());
                        }
                    }
                    return attributeNamesSet;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(statisticsDataTransformer,cql);
            if(queryRes != null){
                return (Set<String>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public Map<Set<String>, Long> getConceptionAttributeValueDistributionStatistic(String attributeName) {
        /*
        属性实时分布查询
        MATCH (n) WHERE (n.COUNTY) IS NOT NULL
        RETURN DISTINCT LABELS(n),count(n)
        */
        String cql ="MATCH (n) WHERE (n.`"+attributeName+"`) IS NOT NULL\n" +
                "RETURN DISTINCT LABELS(n),COUNT(n)";
        logger.debug("Generated Cypher Statement: {}", cql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<Set<String>, Long> valueDistributionStatisticMap = new HashMap<>();
            DataTransformer statisticsDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record dataRecord = result.next();
                        if(dataRecord.containsKey("LABELS(n)")){
                            List<Object> labelsName = dataRecord.get("LABELS(n)").asList();
                            Long attributeCount = dataRecord.get("COUNT(n)").asLong();
                            if(labelsName != null && labelsName.size()>0){
                                Set<String> conceptionNamesSet = new HashSet<>();
                                for(Object currentName:labelsName){
                                    conceptionNamesSet.add(currentName.toString());
                                }
                                valueDistributionStatisticMap.put(conceptionNamesSet,attributeCount);
                            }
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(statisticsDataTransformer,cql);
            return valueDistributionStatisticMap;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Map<String, Long> getRelationAttributeValueDistributionStatistic(String attributeName) {
        /*
        属性实时分布查询
        MATCH ()-[r]-() WHERE (r.COMMITTEE_TYPE) IS NOT NULL
        RETURN DISTINCT TYPE(r),count(r)
        */
        String cql ="MATCH ()-[r]-() WHERE (r.`"+attributeName+"`) IS NOT NULL\n" +
                "        RETURN DISTINCT TYPE(r),COUNT(r)";
        logger.debug("Generated Cypher Statement: {}", cql);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String, Long> valueDistributionStatisticMap = new HashMap<>();
            DataTransformer statisticsDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record dataRecord = result.next();
                        if(dataRecord.containsKey("TYPE(r)")){
                            String typeName = dataRecord.get("TYPE(r)").asString();
                            Long attributeCount = dataRecord.get("COUNT(r)").asLong();
                            valueDistributionStatisticMap.put(typeName,attributeCount);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(statisticsDataTransformer,cql);
            return valueDistributionStatisticMap;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public GlobalClassificationsRuntimeStatistics getGlobalClassificationsRuntimeStatistics() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            GlobalClassificationsRuntimeStatistics globalClassificationsRuntimeStatistics = new GlobalClassificationsRuntimeStatistics();
            String cql1 ="MATCH (n:DOCG_Classification) \n" +
                    "\n" +
                    "OPTIONAL MATCH (n) -[]-(conceptionKinds:DOCG_ConceptionKind)\n" +
                    "OPTIONAL MATCH (n) -[]-(relationKinds:DOCG_RelationKind)\n" +
                    "OPTIONAL MATCH (n) -[]-(attributesViewKinds:DOCG_AttributesViewKind)\n" +
                    "OPTIONAL MATCH (n) -[]-(attributeKinds:DOCG_AttributeKind)\n" +
                    "\n" +
                    "WITH conceptionKinds,relationKinds,attributesViewKinds,attributeKinds\n" +
                    "\n" +
                    "RETURN count(DISTINCT conceptionKinds),count(DISTINCT relationKinds),count(DISTINCT attributesViewKinds),count(DISTINCT attributeKinds)";
            logger.debug("Generated Cypher Statement: {}", cql1);
            DataTransformer dataTransformer1 = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        if(nodeRecord != null){
                            int relatedConceptionKinds = nodeRecord.get("count(DISTINCT conceptionKinds)").asInt();
                            int relatedRelationKinds = nodeRecord.get("count(DISTINCT relationKinds)").asInt();
                            int relatedAttributesViewKinds = nodeRecord.get("count(DISTINCT attributesViewKinds)").asInt();
                            int relatedAttributeKinds = nodeRecord.get("count(DISTINCT attributeKinds)").asInt();
                            globalClassificationsRuntimeStatistics.setRelatedConceptionKindCount(relatedConceptionKinds);
                            globalClassificationsRuntimeStatistics.setRelatedRelationKindCount(relatedRelationKinds);
                            globalClassificationsRuntimeStatistics.setRelatedAttributesViewKindCount(relatedAttributesViewKinds);
                            globalClassificationsRuntimeStatistics.setRelatedAttributeKindCount(relatedAttributeKinds);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer1,cql1);

            String cql2 ="MATCH (n:DOCG_Classification) \n" +
                    "\n" +
                    "OPTIONAL MATCH (n) -[]-(conceptionEntities) WHERE \n" +
                    "NOT 'DOCG_AttributeKind' IN labels(conceptionEntities) \n" +
                    "AND NOT 'DOCG_ConceptionKind' IN labels(conceptionEntities)\n" +
                    "AND NOT 'DOCG_RelationKind' IN labels(conceptionEntities)\n" +
                    "AND NOT 'DOCG_AttributesViewKind' IN labels(conceptionEntities)\n" +
                    "AND NOT 'DOCG_Classification' IN labels(conceptionEntities)\n" +
                    "AND NOT 'DOCG_MetaConfigItemsStorage' IN labels(conceptionEntities)\n" +
                    "\n" +
                    "WITH conceptionEntities\n" +
                    "\n" +
                    "RETURN count(DISTINCT conceptionEntities)";
            logger.debug("Generated Cypher Statement: {}", cql2);
            DataTransformer dataTransformer2 = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        if(nodeRecord != null){
                            int relatedConceptionEntities = nodeRecord.get("count(DISTINCT conceptionEntities)").asInt();
                            globalClassificationsRuntimeStatistics.setRelatedConceptionEntityCount(relatedConceptionEntities);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer2,cql2);

            return globalClassificationsRuntimeStatistics;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Set<String> generateTimeFlowSearchIndexes() throws CoreRealmServiceRuntimeException {
        Set<String> generatedIndexSet = new HashSet<>();
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String cypherProcedureString = "CREATE INDEX DOCG_TIMEFLOW_TimeScaleEntity_FilterIndex IF NOT EXISTS FOR (n:DOCG_TimeScaleEntity) ON (n.timeFlow)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_TIMEFLOW_TimeScaleEntity_FilterIndex");

            cypherProcedureString = "CREATE INDEX DOCG_TIMEFLOW_Minute_FilterIndex IF NOT EXISTS FOR (n:DOCG_TS_Minute) ON (n.timeFlow)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_TIMEFLOW_Minute_FilterIndex");

            cypherProcedureString = "CREATE INDEX DOCG_TIMEFLOW_Hour_FilterIndex IF NOT EXISTS FOR (n:DOCG_TS_Hour) ON (n.timeFlow)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_TIMEFLOW_Hour_FilterIndex");

            cypherProcedureString = "CREATE INDEX DOCG_TIMEFLOW_TimeScaleEvent_FilterIndex0 IF NOT EXISTS FOR (n:DOCG_TimeScaleEvent) ON (n.DOCG_TimeScaleEventTimeFlow)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_TIMEFLOW_TimeScaleEvent_FilterIndex0");

            cypherProcedureString = "CREATE INDEX DOCG_TIMEFLOW_TimeScaleEvent_FilterIndex1 IF NOT EXISTS FOR (n:DOCG_TimeScaleEvent) ON (n.DOCG_TimeScaleEventScaleGrade)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_TIMEFLOW_TimeScaleEvent_FilterIndex1");

            cypherProcedureString = "CREATE INDEX DOCG_TIMEFLOW_TimeScaleEvent_FilterIndex2 IF NOT EXISTS FOR (n:DOCG_TimeScaleEvent) ON (n.DOCG_TimeScaleEventTimeFlow,n.DOCG_TimeScaleEventScaleGrade)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_TIMEFLOW_TimeScaleEvent_FilterIndex2");

            cypherProcedureString = "CREATE INDEX DOCG_TIMEFLOW_Hour_ValueFinderIndex IF NOT EXISTS FOR (n:DOCG_TS_Hour) ON (n.year,n.month,n.day,n.hour)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_TIMEFLOW_Hour_ValueFinderIndex");

            cypherProcedureString = "CREATE INDEX DOCG_TIMEFLOW_Minute_ValueFinderIndex IF NOT EXISTS FOR (n:DOCG_TS_Minute) ON (n.year,n.month,n.day,n.hour,n.minute)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_TIMEFLOW_Minute_ValueFinderIndex");
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return generatedIndexSet;
    }

    @Override
    public Set<String> generateGeospatialRegionSearchIndexes() throws CoreRealmServiceRuntimeException {
        Set<String> generatedIndexSet = new HashSet<>();
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String cypherProcedureString = "CREATE INDEX DOCG_GEOSPATIALREGION_GeospatialScaleEvent_FilterIndex0 IF NOT EXISTS FOR (n:DOCG_GeospatialScaleEvent) ON (n.DOCG_GeospatialScaleEventGeospatialRegion)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_GEOSPATIALREGION_GeospatialScaleEvent_FilterIndex0");

            cypherProcedureString = "CREATE INDEX DOCG_GEOSPATIALREGION_GeospatialScaleEvent_FilterIndex1 IF NOT EXISTS FOR (n:DOCG_GeospatialScaleEvent) ON (n.DOCG_GeospatialScaleEventScaleGrade)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_GEOSPATIALREGION_GeospatialScaleEvent_FilterIndex1");

            cypherProcedureString = "CREATE INDEX DOCG_GEOSPATIALREGION_GeospatialScaleEvent_FilterIndex2 IF NOT EXISTS FOR (n:DOCG_GeospatialScaleEvent) ON (n.DOCG_GeospatialScaleEventGeospatialRegion,n.DOCG_GeospatialScaleEventScaleGrade)";
            workingGraphOperationExecutor.executeWrite(result -> null, cypherProcedureString);
            generatedIndexSet.add("DOCG_GEOSPATIALREGION_GeospatialScaleEvent_FilterIndex2");
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return generatedIndexSet;
    }

    @Override
    public Map<String,ConceptionKindDataCapabilityInfo> getConceptionKindsDataCapabilityStatistics() {
        Map<String,ConceptionKindDataCapabilityInfo> conceptionKindsDataCapabilityStatisticsMap = new HashMap<>();

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(1000000);

        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Boolean transformResult(Result result) {
                if(result.hasNext()){
                    return true;
                }else{
                    return false;
                }
            }
        };

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.ConceptionKindClass,queryParameters,null);
            GetListConceptionKindTransformer getListConceptionKindTransformer = new GetListConceptionKindTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object conceptionListRes = workingGraphOperationExecutor.executeRead(getListConceptionKindTransformer,queryCql);
            if(conceptionListRes != null){
                List<ConceptionKind> currentList = (List<ConceptionKind>) conceptionListRes;
                for(ConceptionKind currentConceptionKind:currentList){
                    String conceptionKindName = currentConceptionKind.getConceptionKindName();
                    ConceptionKindDataCapabilityInfo conceptionKindDataCapabilityInfo =
                            getConceptionKindDataCapabilityInfo(workingGraphOperationExecutor,dataTransformer,conceptionKindName);
                    conceptionKindsDataCapabilityStatisticsMap.put(conceptionKindName,conceptionKindDataCapabilityInfo);
                }
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            throw new RuntimeException(e);
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return conceptionKindsDataCapabilityStatisticsMap;
    }

    @Override
    public Map<String,ConceptionKindDataCapabilityInfo> getConceptionKindsDataCapabilityStatistics(List<String> targetConceptionKindNameList){
        if(targetConceptionKindNameList != null && !targetConceptionKindNameList.isEmpty()){
            Map<String,ConceptionKindDataCapabilityInfo> conceptionKindsDataCapabilityStatisticsMap = new HashMap<>();
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Boolean transformResult(Result result) {
                    if(result.hasNext()){
                        return true;
                    }else{
                        return false;
                    }
                }
            };

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                for(String conceptionKindName:targetConceptionKindNameList){
                    ConceptionKindDataCapabilityInfo conceptionKindDataCapabilityInfo =
                            getConceptionKindDataCapabilityInfo(workingGraphOperationExecutor,dataTransformer,conceptionKindName);
                    conceptionKindsDataCapabilityStatisticsMap.put(conceptionKindName,conceptionKindDataCapabilityInfo);
                }
            } finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            return conceptionKindsDataCapabilityStatisticsMap;
        }else{
            return null;
        }
    }

    @Override
    public boolean registerExternalAttributesValueAccessProcessor(AttributesViewKind attributesViewKind, String accessProcessorID) throws CoreRealmServiceRuntimeException{
        if(!AttributesViewKind.AttributesViewKindDataForm.EXTERNAL_VALUE.equals(attributesViewKind.getAttributesViewKindDataForm())){
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("AttributesViewKindDataForm must be EXTERNAL_VALUE");
            throw exception;
        }else{
            return attributesViewKind.addOrUpdateMetaConfigItem(Neo4JExternalAttributesValueAccessible.ExternalAttributesValueAccessProcessorID,accessProcessorID);
        }
    }

    @Override
    public Map<LocalDate, Long> getKindEntitiesPeriodicOperationStatic(String kindName, kindType kindType, OperationType operationType, OperationPeriod operationPeriod) throws CoreRealmServiceRuntimeException {
        Map<LocalDate, Long> periodicOperationStaticMap = new HashMap<>();
        if(kindType == null || operationType == null || operationPeriod == null){
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("kindType, operationType and operationPeriod is required");
            throw exception;
        }

        String filterProperty = null;
        switch (operationType){
            case Create -> filterProperty="createDate";
            case Update -> filterProperty="lastModifyDate";
        }

        String kindNamePart = kindName != null ? ":`"+kindName+"`": "";
        String matchPart = null;
        String dataPrefix = null;
        switch(kindType){
            case ConceptionKind :
                matchPart="MATCH (n"+kindNamePart+")\n"+"WHERE n."+filterProperty+" IS NOT NULL\n";
                dataPrefix = "n";
                break;
            case RelationKind : matchPart="MATCH ()-[r"+kindNamePart+"]-()\n"+"WHERE r."+filterProperty+" IS NOT NULL\n";
                dataPrefix = "r";
        }

        String returnPart = null;
        switch(operationPeriod){
            case Year -> returnPart="RETURN date.truncate('year', datetime("+dataPrefix+"."+filterProperty+")) AS year, count(*) AS count\n"+"ORDER BY year";
            case Month -> returnPart="RETURN date.truncate('month', datetime("+dataPrefix+"."+filterProperty+")) AS month, count(*) AS count\n"+"ORDER BY month";
            case Day -> returnPart = "RETURN date(datetime("+dataPrefix+"."+filterProperty+")) AS day, count(*) AS count\n" +"ORDER BY day";
        }

        String cql = matchPart + returnPart;
        logger.debug("Generated Cypher Statement: {}", cql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        if(nodeRecord != null){
                            LocalDate dateKey = null;
                            switch (operationPeriod){
                                case Year :
                                    dateKey = nodeRecord.get("year").asLocalDate();
                                    break;
                                case Month:
                                    dateKey = nodeRecord.get("month").asLocalDate();
                                    break;
                                case Day:
                                    dateKey = nodeRecord.get("day").asLocalDate();
                            }
                            Long entitiesCount = nodeRecord.get("count").asLong();
                            periodicOperationStaticMap.put(dateKey,entitiesCount);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer,cql);
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        return periodicOperationStaticMap;
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

    private Set<SearchIndexInfo> listKindSearchIndex(String entityType){
        /*
        https://neo4j.com/docs/cypher-manual/4.3/indexes-for-search-performance/
        */
        String cypherProcedureString ="SHOW INDEXES YIELD name,populationPercent,type,entityType,labelsOrTypes,properties WHERE entityType ='"+entityType+"' AND labelsOrTypes IS NOT NULL";

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

    private long checkRelationEntitiesCount(GraphOperationExecutor workingGraphOperationExecutor,String sourceConceptionKindName,
                                            String targetConceptionKindName,String relationKindName){
        String cql = "MATCH p=(source:`"+sourceConceptionKindName+"`)-[r:`"+relationKindName+"`]->(target:`"+targetConceptionKindName+"`) RETURN count(r) AS operationResult";
        logger.debug("Generated Cypher Statement: {}", cql);
        GetLongFormatReturnValueTransformer GetLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer();
        Object queryRes = workingGraphOperationExecutor.executeRead(GetLongFormatReturnValueTransformer,cql);
        if(queryRes != null){
            return (Long)queryRes;
        }
        return 0;
    }

    private boolean checkRelationEntitiesExist(GraphOperationExecutor workingGraphOperationExecutor,String sourceConceptionKindName,
                                               String targetConceptionKindName,String relationKindName){
        String cql = "MATCH p=(source:`"+sourceConceptionKindName+"`)-[r:`"+relationKindName+"`]->(target:`"+targetConceptionKindName+"`) RETURN r AS operationResult LIMIT 1";
        logger.debug("Generated Cypher Statement: {}", cql);
        DataTransformer<Boolean> dataTransformer = new DataTransformer<>() {
            @Override
            public Boolean transformResult(Result result) {
                boolean relationEntitiesExist = false;
                int resultNumCount = result.list().size();
                if(resultNumCount == 0){
                    relationEntitiesExist = false;
                }else{
                    relationEntitiesExist = true;
                }
                return relationEntitiesExist;
            }
        };
        Object queryRes = workingGraphOperationExecutor.executeRead(dataTransformer,cql);
        return queryRes != null ? ((Boolean)queryRes).booleanValue():false;
    }

    private ConceptionKindDataCapabilityInfo getConceptionKindDataCapabilityInfo(GraphOperationExecutor workingGraphOperationExecutor,DataTransformer dataTransformer,String conceptionKindName){
        ConceptionKindDataCapabilityInfo conceptionKindDataCapabilityInfo = new ConceptionKindDataCapabilityInfo();
        //For ContainsGeospatialAttribute
        /*
            MATCH (n:`ConceptionKind`)
            WHERE n.DOCG_GS_CLGeometryContent IS NOT NULL OR n.DOCG_GS_GLGeometryContent IS NOT NULL OR n.DOCG_GS_LLGeometryContent IS NOT NULL
            RETURN n LIMIT 1
        */
        String containsGeospatialAttributeCheckCql = "MATCH (n:`"+conceptionKindName+"`)\n" +
                "        WHERE n.DOCG_GS_CLGeometryContent IS NOT NULL OR n.DOCG_GS_GLGeometryContent IS NOT NULL OR n.DOCG_GS_LLGeometryContent IS NOT NULL\n" +
                "        RETURN n LIMIT 1";
        Object checkRes1 = workingGraphOperationExecutor.executeRead(dataTransformer,containsGeospatialAttributeCheckCql);
        if(checkRes1 != null){
            conceptionKindDataCapabilityInfo.setContainsGeospatialAttribute((Boolean)checkRes1);
        }

        //For AttachedToGeospatialScaleEvent
        String attachedToGeospatialScaleEventCheckCql = "MATCH (n:`"+conceptionKindName+"`)-[:DOCG_AttachToGeospatialScale]->(DOCG_GeospatialScaleEvent) RETURN n LIMIT 1";
        Object checkRes2 = workingGraphOperationExecutor.executeRead(dataTransformer,attachedToGeospatialScaleEventCheckCql);
        if(checkRes2 != null){
            conceptionKindDataCapabilityInfo.setAttachedToGeospatialScaleEvent((Boolean)checkRes2);
        }

        //For AttachedToTimeScaleEvent
        String attachedToTimeScaleEventCheckCql = "MATCH (n:`"+conceptionKindName+"`)-[:DOCG_AttachToTimeScale]->(DOCG_TimeScaleEvent) RETURN n LIMIT 1";
        Object checkRes3 = workingGraphOperationExecutor.executeRead(dataTransformer,attachedToTimeScaleEventCheckCql);
        if(checkRes3 != null){
            conceptionKindDataCapabilityInfo.setAttachedToTimeScaleEvent((Boolean)checkRes3);
        }

        return conceptionKindDataCapabilityInfo;
    }
}
