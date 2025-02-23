package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.memgraph.dataTransformer.GetSingleConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributesViewKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termInf.MemGraphCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemGraphCoreRealmImpl extends Neo4JCoreRealmImpl implements MemGraphCoreRealm {

    private static Logger logger = LoggerFactory.getLogger(MemGraphCoreRealmImpl.class);

    public MemGraphCoreRealmImpl(String coreRealmName){
        super(coreRealmName);
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public MemGraphCoreRealmImpl(){
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.MEMGRAPH;
    }

    @Override
    public ConceptionKind getConceptionKind(String conceptionKindName) {
        if(conceptionKindName == null){
            return null;
        }

        if(conceptionKindName.startsWith("DOCG_")){
            MemGraphConceptionKindImpl memGraphConceptionKindImpl =
                    new MemGraphConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,"0");
            memGraphConceptionKindImpl.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            return memGraphConceptionKindImpl;
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ConceptionKindClass,RealmConstant._NameProperty,conceptionKindName,1);
            GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                    new GetSingleConceptionKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,queryCql);
            return createConceptionKindRes != null?(ConceptionKind)createConceptionKindRes:null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
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
                String conceptionKindUID = ((MemGraphConceptionKindImpl)targetConceptionKind).getConceptionKindUID();
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(conceptionKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                        new GetSingleConceptionKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,deleteCql);
                ConceptionKind resultConceptionKind = deletedConceptionKindRes != null ? (ConceptionKind)deletedConceptionKindRes : null;
                if(resultConceptionKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String conceptionKindId = ((MemGraphConceptionKindImpl)resultConceptionKind).getConceptionKindUID();
                    MemGraphConceptionKindImpl resultMemGraphConceptionKindImplForCacheOperation = new MemGraphConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,conceptionKindId);
                    executeConceptionKindCacheOperation(resultMemGraphConceptionKindImplForCacheOperation,CacheOperationType.DELETE);
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
