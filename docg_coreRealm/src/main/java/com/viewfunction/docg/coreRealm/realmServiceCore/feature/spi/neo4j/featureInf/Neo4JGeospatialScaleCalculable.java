package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetBooleanFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial.GeospatialCalculateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            List<String> targetConceptionKinds, double bufferDistanceValue,SpatialPredicateType spatialPredicateType,
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

    default public boolean isSpatialPredicateMatchedWith(SpatialPredicateType spatialPredicateType,
            String targetConceptionEntityUID, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                boolean isTargetEntityContentValidate=checkGeospatialScaleContentExist(workingGraphOperationExecutor,spatialScaleLevel,targetConceptionEntityUID);
                if(!isTargetEntityContentValidate){
                    return false;
                }else{
                    List<String> entityUIDList = new ArrayList<>();
                    entityUIDList.add(this.getEntityUID());
                    entityUIDList.add(targetConceptionEntityUID);

                    Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                    if(getGeospatialScaleContentMap.size() == 2){
                        return GeospatialCalculateUtil.spatialPredicateWKTCalculate(getGeospatialScaleContentMap.get(this.getEntityUID()),
                                spatialPredicateType,
                                getGeospatialScaleContentMap.get(targetConceptionEntityUID));
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    default public GeospatialScaleFeatureSupportable.WKTGeometryType getEntityGeometryType(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                List<String> entityUIDList = new ArrayList<>();
                entityUIDList.add(this.getEntityUID());
                Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                if(getGeospatialScaleContentMap.size() == 1){
                    return GeospatialCalculateUtil.getGeometryWKTType(getGeospatialScaleContentMap.get(this.getEntityUID()));
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public double getEntitiesSpatialDistance(String targetConceptionEntityUID, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                boolean isTargetEntityContentValidate=checkGeospatialScaleContentExist(workingGraphOperationExecutor,spatialScaleLevel,targetConceptionEntityUID);
                if(!isTargetEntityContentValidate){
                    return Double.NaN;
                }else{
                    List<String> entityUIDList = new ArrayList<>();
                    entityUIDList.add(this.getEntityUID());
                    entityUIDList.add(targetConceptionEntityUID);
                    Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                    if(getGeospatialScaleContentMap.size() == 2){
                        return GeospatialCalculateUtil.getGeometriesDistance(getGeospatialScaleContentMap.get(this.getEntityUID()),getGeospatialScaleContentMap.get(targetConceptionEntityUID));
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return Double.NaN;
    }

    default public boolean isEntitiesWithinSpatialDistance(String targetConceptionEntityUID, double distanceValue,SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                validateSpatialScaleLevel(workingGraphOperationExecutor,targetConceptionEntityUID,spatialScaleLevel);
                List<String> entityUIDList = new ArrayList<>();
                entityUIDList.add(this.getEntityUID());
                entityUIDList.add(targetConceptionEntityUID);
                Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                if(getGeospatialScaleContentMap.size() == 2){
                    return GeospatialCalculateUtil.isGeometriesInDistance(getGeospatialScaleContentMap.get(this.getEntityUID()),getGeospatialScaleContentMap.get(targetConceptionEntityUID),distanceValue);
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    private void validateSpatialScaleLevel(GraphOperationExecutor workingGraphOperationExecutor,SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException {
        if(!checkGeospatialScaleContentExist(workingGraphOperationExecutor,spatialScaleLevel,this.getEntityUID())){
            logger.error("ConceptionEntity with UID {} doesn't have {} level SpatialScale.", this.getEntityUID(),spatialScaleLevel);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ConceptionEntity with UID "+this.getEntityUID()+" doesn't have "+spatialScaleLevel+" level SpatialScale.");
            throw exception;
        }
    }

    private void validateSpatialScaleLevel(GraphOperationExecutor workingGraphOperationExecutor,String entityUID,SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException {
        if(!checkGeospatialScaleContentExist(workingGraphOperationExecutor,spatialScaleLevel,entityUID)){
            logger.error("ConceptionEntity with UID {} doesn't have {} level SpatialScale.", entityUID,spatialScaleLevel);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ConceptionEntity with UID "+entityUID+" doesn't have "+spatialScaleLevel+" level SpatialScale.");
            throw exception;
        }
    }

    private boolean checkGeospatialScaleContentExist(GraphOperationExecutor workingGraphOperationExecutor,SpatialScaleLevel spatialScaleLevel,String entityUID){
        String spatialScalePropertyName = null;
        switch(spatialScaleLevel){
            case Local: spatialScalePropertyName = RealmConstant._GeospatialLLGeometryContent;break;
            case Global: spatialScalePropertyName = RealmConstant._GeospatialGLGeometryContent;break;
            case Country: spatialScalePropertyName = RealmConstant._GeospatialCLGeometryContent;break;
        }
        if(spatialScalePropertyName != null){
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,
                    Long.parseLong(entityUID), CypherBuilder.CypherFunctionType.EXISTS, spatialScalePropertyName);
            GetBooleanFormatAggregatedReturnValueTransformer getBooleanFormatAggregatedReturnValueTransformer =
                    new GetBooleanFormatAggregatedReturnValueTransformer("exists",spatialScalePropertyName);

            Object resultRes = workingGraphOperationExecutor.executeRead(getBooleanFormatAggregatedReturnValueTransformer,queryCql);
            return resultRes != null ? (Boolean)resultRes : false;
        }
        return false;
    }

    private Map<String,String> getGeospatialScaleContent(GraphOperationExecutor workingGraphOperationExecutor, SpatialScaleLevel spatialScaleLevel, List<String> entityUIDs){
        String spatialScalePropertyName = null;
        switch(spatialScaleLevel){
            case Local: spatialScalePropertyName = RealmConstant._GeospatialLLGeometryContent;break;
            case Global: spatialScalePropertyName = RealmConstant._GeospatialGLGeometryContent;break;
            case Country: spatialScalePropertyName = RealmConstant._GeospatialCLGeometryContent;break;
        }
        List<String> attributeNames = new ArrayList<>();
        attributeNames.add(spatialScalePropertyName);

        try {
            String cypherProcedureString = CypherBuilder.matchAttributesWithNodeIDs(entityUIDs,attributeNames);

            GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer = new GetListConceptionEntityValueTransformer(attributeNames);
            getListConceptionEntityValueTransformer.setUseIDMatchLogic(true);
            Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer,cypherProcedureString);
            if(resEntityRes != null){
                Map<String,String> geospatialScaleContentMap = new HashMap<>();
                List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;

                for(ConceptionEntityValue currentConceptionEntityValue:resultEntitiesValues){
                    String entityUID = currentConceptionEntityValue.getConceptionEntityUID();
                    String geospatialScaleContent = currentConceptionEntityValue.getEntityAttributesValue().get(spatialScalePropertyName).toString();
                    geospatialScaleContentMap.put(entityUID,geospatialScaleContent);
                }
                return geospatialScaleContentMap;
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }
}
