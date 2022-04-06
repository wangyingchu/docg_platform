package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetBooleanFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface Neo4JGeospatialScaleCalculable extends GeospatialScaleCalculable,Neo4JKeyResourcesRetrievable {

    static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialScaleCalculable.class);

    default public List<ConceptionEntity> getSpatialPredicateMatchedConceptionEntities(
            List<String> targetConceptionKinds, SpatialPredicateType spatialPredicateType,
            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);



            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<ConceptionEntity> getSpatialBufferMatchedConceptionEntities(
            List<String> targetConceptionKinds, double bufferDistanceValue,
            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);



            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public boolean isSpatialPredicateMatchedWith(
            String targetConceptionEntityUID, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);



            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    private void validateSpatialScaleLevel(GraphOperationExecutor workingGraphOperationExecutor,SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException {
        if(!checkGeospatialScaleContentExist(workingGraphOperationExecutor,spatialScaleLevel)){
            logger.error("ConceptionEntity with UID {} doesn't have {} level SpatialScale.", this.getEntityUID(),spatialScaleLevel);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ConceptionEntity with UID "+this.getEntityUID()+" doesn't have "+spatialScaleLevel+" level SpatialScale.");
            throw exception;
        }
    }

    private boolean checkGeospatialScaleContentExist(GraphOperationExecutor workingGraphOperationExecutor,SpatialScaleLevel spatialScaleLevel){
        String spatialScalePropertyName = null;
        switch(spatialScaleLevel){
            case Local: spatialScalePropertyName = RealmConstant._GeospatialLLGeometryContent;break;
            case Global: spatialScalePropertyName = RealmConstant._GeospatialGLGeometryContent;break;
            case Country: spatialScalePropertyName = RealmConstant._GeospatialCLGeometryContent;break;
        }
        if(spatialScalePropertyName != null){
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,
                    Long.parseLong(this.getEntityUID()), CypherBuilder.CypherFunctionType.EXISTS, spatialScalePropertyName);
            GetBooleanFormatAggregatedReturnValueTransformer getBooleanFormatAggregatedReturnValueTransformer =
                    new GetBooleanFormatAggregatedReturnValueTransformer("exists",spatialScalePropertyName);

            Object resultRes = workingGraphOperationExecutor.executeRead(getBooleanFormatAggregatedReturnValueTransformer,queryCql);
            return resultRes != null ? (Boolean)resultRes : false;
        }
        return false;
    }
}
