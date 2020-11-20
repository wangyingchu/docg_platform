package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MultiConceptionKindsSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface Neo4JMultiConceptionKindsSupportable extends MultiConceptionKindsSupportable,Neo4JKeyResourcesRetrievable{

    Logger logger = LoggerFactory.getLogger(Neo4JMultiConceptionKindsSupportable.class);

    default boolean joinConceptionKinds(String[] newKindNames) throws CoreRealmServiceRuntimeException {
        if(newKindNames == null || newKindNames.length == 0){
            logger.error("At least one Conception Kind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("At least one Conception Kind Name is required.");
            throw exception;
        }
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                String updateKindsCql = CypherBuilder.modifyNodeLabelsWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,
                        Long.parseLong(this.getEntityUID()), CypherBuilder.LabelOperationType.ADD,newKindNames);
                DataTransformer<Boolean> updateKindDataTransformer = new DataTransformer(){
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord != null){
                                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                                List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());

                                int matchedCount = 0;
                                for(String currentNewKindName:newKindNames){
                                    if(allConceptionKindNames.contains(currentNewKindName)){
                                        matchedCount++;
                                    }
                                }
                                if(matchedCount == newKindNames.length){
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                };
                Object operationResult = workingGraphOperationExecutor.executeWrite(updateKindDataTransformer,updateKindsCql);
                if(operationResult != null){
                    Boolean res = (Boolean)operationResult;
                    return res;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    default boolean retreatFromConceptionKind(String kindName) throws CoreRealmServiceRuntimeException{
        if(kindName == null){
            logger.error("Conception Kind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Conception Kind Name is required.");
            throw exception;
        }

        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.getEntityUID()), null, null);
                DataTransformer<Boolean> checkKindDataTransformer = new DataTransformer(){
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord != null){
                                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                                List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                                if(allConceptionKindNames.contains(kindName)){
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                        }
                        return false;
                    }
                };
                Object checkResult = workingGraphOperationExecutor.executeRead(checkKindDataTransformer,queryCql);
                if(checkResult != null){
                    Boolean checkRes = (Boolean)checkResult;
                    if(!checkRes){
                        logger.error("Entity with UID {} doesn't belongs to Conception Kind "+kindName+".", this.getEntityUID());
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("Entity with UID "+this.getEntityUID()+" doesn't belongs to Conception Kind "+kindName+".");
                        throw exception;
                    }
                }

                String[] kindNamesArray = new String[]{kindName};
                String updateKindsCql = CypherBuilder.modifyNodeLabelsWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,
                        Long.parseLong(this.getEntityUID()), CypherBuilder.LabelOperationType.REMOVE,kindNamesArray);
                DataTransformer<Boolean> updateKindDataTransformer = new DataTransformer(){
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord != null){
                                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                                List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                                if(allConceptionKindNames.contains(kindName)){
                                    return false;
                                }else{
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                };
                Object operationResult = workingGraphOperationExecutor.executeWrite(updateKindDataTransformer,updateKindsCql);
                if(operationResult != null){
                    Boolean res = (Boolean)operationResult;
                    return res;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }
}
