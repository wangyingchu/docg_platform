package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termInf.MemGraphConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionKindImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemGraphConceptionKindImpl extends Neo4JConceptionKindImpl implements MemGraphConceptionKind {

    private static Logger logger = LoggerFactory.getLogger(MemGraphConceptionKindImpl.class);
    private String coreRealmName;
    private String conceptionKindName;
    private String conceptionKindDesc;
    private String conceptionKindUID;

    public MemGraphConceptionKindImpl(String coreRealmName, String conceptionKindName, String conceptionKindDesc, String conceptionKindUID) {
        super(coreRealmName, conceptionKindName, conceptionKindDesc, conceptionKindUID);
        this.coreRealmName = coreRealmName;
        this.conceptionKindName = conceptionKindName;
        this.conceptionKindDesc = conceptionKindDesc;
        this.conceptionKindUID = conceptionKindUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException {
        try{
            CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

            String cql = "MATCH (n:`"+this.conceptionKindName+"`) DETACH DELETE n" ;
            logger.debug("Generated Cypher Statement: {}", cql);
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record resultRecord = result.next();
                        if(resultRecord.containsKey("total")){
                            long deletedRecordCount = resultRecord.get("total").asLong();
                            commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(deletedRecordCount);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
            commonEntitiesOperationResultImpl.getOperationStatistics().
                    setOperationSummary("purgeAllEntities operation for conceptionKind "+this.conceptionKindName+" success.");

            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
