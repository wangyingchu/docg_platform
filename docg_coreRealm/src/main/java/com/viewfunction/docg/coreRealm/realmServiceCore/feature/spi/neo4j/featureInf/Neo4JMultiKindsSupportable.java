package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MultiKindsSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface Neo4JMultiKindsSupportable extends MultiKindsSupportable,Neo4JKeyResourcesRetrievable{

    Logger logger = LoggerFactory.getLogger(Neo4JClassificationAttachable.class);

    default boolean joinKinds(String[] newKindNames) throws CoreRealmServiceRuntimeException{
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


    default boolean retreatFromKind(String kindName) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null){

        }
        return false;
    }
}
