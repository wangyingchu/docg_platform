package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListAttributeSystemInfoTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetLongFormatReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetMapAttributeSystemInfoTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
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
                    for(Object currentRelationshipObj:relationshipsList){
                        Relationship currentRelationship = (Relationship)currentRelationshipObj;
                        String relationshipType = currentRelationship.type();
                        String startConceptionKindId = ""+currentRelationship.startNodeId();
                        String endConceptionKindId = ""+currentRelationship.endNodeId();
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
                    for(Object currentRelationshipObj:relationshipsList){
                        Relationship currentRelationship = (Relationship)currentRelationshipObj;
                        String relationshipType = currentRelationship.type();
                        String startConceptionKindId = ""+currentRelationship.startNodeId();
                        String endConceptionKindId = ""+currentRelationship.endNodeId();
                        boolean relationExist = checkRelationEntitiesExist(workingGraphOperationExecutor,conceptionKindId_nameMapping.get(startConceptionKindId),conceptionKindId_nameMapping.get(endConceptionKindId),relationshipType);
                        if(relationExist){
                            ConceptionKindCorrelationInfo currentConceptionKindCorrelationInfo =
                                    new ConceptionKindCorrelationInfo(
                                            conceptionKindId_nameMapping.get(startConceptionKindId),
                                            conceptionKindId_nameMapping.get(endConceptionKindId),
                                            relationshipType,1);
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
        https://www.amcharts.com/demos/rectangular-voronoi-tree-map/
        */
        String cql ="MATCH (n) WHERE (n.`"+attributeName+"`) IS NOT NULL\n" +
                    "RETURN DISTINCT LABELS(n),count(n)";
        logger.debug("Generated Cypher Statement: {}", cql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<Set<String>, Long> valueDistributionStatisticMap = new HashMap<>();
            DataTransformer statisticsDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {




                    return null;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(statisticsDataTransformer,cql);
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

        https://www.amcharts.com/demos/rectangular-voronoi-tree-map/
     */
        return null;
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
        String cql = "MATCH p=(source:"+sourceConceptionKindName+")-[r:"+relationKindName+"]->(target:"+targetConceptionKindName+") RETURN count(r) AS operationResult";
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
        String cql = "MATCH p=(source:"+sourceConceptionKindName+")-[r:"+relationKindName+"]->(target:"+targetConceptionKindName+") RETURN r AS operationResult LIMIT 1";
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
}
