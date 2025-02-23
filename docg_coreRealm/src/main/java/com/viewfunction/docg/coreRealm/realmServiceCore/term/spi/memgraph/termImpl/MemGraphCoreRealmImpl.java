package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributesViewKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termInf.MemGraphCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemGraphCoreRealmImpl extends Neo4JCoreRealmImpl implements MemGraphCoreRealm {

    public MemGraphCoreRealmImpl(String coreRealmName){
        super(coreRealmName);
    }

    public MemGraphCoreRealmImpl(){}
    private static Logger logger = LoggerFactory.getLogger(MemGraphCoreRealmImpl.class);

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.MEMGRAPH;
    }

    @Override
    public boolean removeConceptionKind(String conceptionKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException{
        if(conceptionKindName == null){
            return false;
        }
        ConceptionKind targetConceptionKind =this.getConceptionKind(conceptionKindName);
        if(targetConceptionKind == null){
            logger.error("CoreRealm does not contains ConceptionKind with name {}.", conceptionKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm does not contains ConceptionKind with name " + conceptionKindName + ".");
            throw exception;
        }else{
            if(deleteExistEntities){
                targetConceptionKind.purgeAllEntities();
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String conceptionKindUID = ((Neo4JConceptionKindImpl)targetConceptionKind).getConceptionKindUID();
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(conceptionKindUID),null,null);
                GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                        new GetSingleConceptionKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,deleteCql);
                ConceptionKind resultConceptionKind = deletedConceptionKindRes != null ? (ConceptionKind)deletedConceptionKindRes : null;
                if(resultConceptionKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String conceptionKindId = ((Neo4JConceptionKindImpl)resultConceptionKind).getConceptionKindUID();
                    Neo4JConceptionKindImpl resultNeo4JConceptionKindImplForCacheOperation = new Neo4JConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,conceptionKindId);
                    executeConceptionKindCacheOperation(resultNeo4JConceptionKindImplForCacheOperation,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        if(attributesViewKindUID == null){
            return false;
        }
        AttributesViewKind targetAttributesViewKind = this.getAttributesViewKind(attributesViewKindUID);
        if(targetAttributesViewKind != null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(attributesViewKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                        new GetSingleAttributesViewKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributesViewKindTransformer,deleteCql);
                AttributesViewKind resultKind = deletedAttributesViewKindRes != null ? (AttributesViewKind)deletedAttributesViewKindRes : null;
                if(resultKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeAttributesViewKindCacheOperation(resultKind,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }else{
            logger.error("AttributesViewKind does not contains entity with UID {}.", attributesViewKindUID);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("AttributesViewKind does not contains entity with UID " + attributesViewKindUID + ".");
            throw exception;
        }
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
