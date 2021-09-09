package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DataStatusSnapshotInfo;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
                        DataStatusSnapshotInfo dataStatusSnapshotInfo = new DataStatusSnapshotInfo(wholeConceptionEntityCount,
                                wholeRelationEntityCount,wholeConceptionKindCount,wholeRelationKindCount,wholePhysicAttributeNameCount,
                                conceptionKindsDataCount,relationKindsDataCount);
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

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
