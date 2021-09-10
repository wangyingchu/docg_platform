package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DataStatusSnapshotInfo;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RuntimeRelationAndConceptionKindAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.SystemStatusSnapshotInfo;
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
