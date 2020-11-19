package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MultiKindsSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Neo4JMultiKindsSupportable extends MultiKindsSupportable,Neo4JKeyResourcesRetrievable{

    Logger logger = LoggerFactory.getLogger(Neo4JClassificationAttachable.class);

    default boolean joinKinds(String[] newKindNames) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{




            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }


    default boolean retreatFromKind(String kindName){
        if(this.getEntityUID() != null){

        }
        return false;
    }
}
