package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetBooleanFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial.GeospatialCalculateUtil;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public interface Neo4JGeospatialScaleCalculable extends GeospatialScaleCalculable,Neo4JKeyResourcesRetrievable {

    static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialScaleCalculable.class);

    default public List<ConceptionEntity> getSpatialPredicateMatchedConceptionEntities(String targetConceptionKind,
            AttributesParameters attributesParameters, SpatialPredicateType spatialPredicateType,
            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                Map<String,String> entitiesSpatialContentDataMap = getEntitiesGeospatialScaleContentMap(workingGraphOperationExecutor,targetConceptionKind,attributesParameters,spatialScaleLevel);
                if(entitiesSpatialContentDataMap != null){
                    List<String> entityUIDList = new ArrayList<>();
                    entityUIDList.add(this.getEntityUID());
                    Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                    Set<String> matchedEntityUIDSet = GeospatialCalculateUtil.spatialPredicateFilterWKTsCalculate(
                            getGeospatialScaleContentMap.get(this.getEntityUID()),spatialPredicateType,entitiesSpatialContentDataMap);

                    return getConceptionEntitiesByUIDs(workingGraphOperationExecutor,matchedEntityUIDSet);
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public List<ConceptionEntity> getSpatialBufferMatchedConceptionEntities(String targetConceptionKind,
            AttributesParameters attributesParameters, double bufferDistanceValue,SpatialPredicateType spatialPredicateType,
            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                Map<String,String> entitiesSpatialContentDataMap = getEntitiesGeospatialScaleContentMap(workingGraphOperationExecutor,targetConceptionKind,attributesParameters,spatialScaleLevel);


            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public boolean isSpatialPredicateMatchedWith(SpatialPredicateType spatialPredicateType,
            String targetConceptionEntityUID, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null && targetConceptionEntityUID != null) {
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

    default public boolean isSpatialPredicateMatchedWith(SpatialPredicateType spatialPredicateType,
                                                         Set<String> targetConceptionEntityUIDsSet, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null && targetConceptionEntityUIDsSet != null && targetConceptionEntityUIDsSet.size()>0) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                    List<String> entityUIDList = new ArrayList<>();
                    entityUIDList.add(this.getEntityUID());
                    entityUIDList.addAll(targetConceptionEntityUIDsSet);
                    Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);

                    String fromGeometryWKT = getGeospatialScaleContentMap.get(this.getEntityUID());
                    Set<String> targetGeometryWKTs = new HashSet<>();
                    for(String currentEntityUID:getGeospatialScaleContentMap.keySet()){
                        if(!currentEntityUID.equals(this.getEntityUID())){
                            targetGeometryWKTs.add(getGeospatialScaleContentMap.get(currentEntityUID));
                        }
                    }
                    return GeospatialCalculateUtil.spatialPredicateWKTCalculate(fromGeometryWKT,spatialPredicateType,
                                targetGeometryWKTs);
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
        if(this.getEntityUID() != null && targetConceptionEntityUID != null) {
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

    default public boolean isSpatialDistanceWithinEntity(String targetConceptionEntityUID, double distanceValue, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null && targetConceptionEntityUID != null) {
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

    default public boolean isSpatialDistanceWithinEntities(Set<String> targetConceptionEntityUIDsSet, double distanceValue, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null && targetConceptionEntityUIDsSet != null && targetConceptionEntityUIDsSet.size()>0) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                List<String> entityUIDList = new ArrayList<>();
                entityUIDList.add(this.getEntityUID());
                entityUIDList.addAll(targetConceptionEntityUIDsSet);
                Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);

                String fromGeometryWKT = getGeospatialScaleContentMap.get(this.getEntityUID());
                Set<String> targetGeometryWKTs = new HashSet<>();
                for(String currentEntityUID:getGeospatialScaleContentMap.keySet()){
                    if(!currentEntityUID.equals(this.getEntityUID())){
                        targetGeometryWKTs.add(getGeospatialScaleContentMap.get(currentEntityUID));
                    }
                }
                return GeospatialCalculateUtil.isGeometriesInDistance(fromGeometryWKT,targetGeometryWKTs,distanceValue);
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    default public String getEntitySpatialBufferWKTGeometryContent(double bufferDistanceValue,SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                List<String> entityUIDList = new ArrayList<>();
                entityUIDList.add(this.getEntityUID());
                Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                if(getGeospatialScaleContentMap.size() == 1){
                    return GeospatialCalculateUtil.getGeometryBufferWKTContent(getGeospatialScaleContentMap.get(this.getEntityUID()),bufferDistanceValue);
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public String getEntitySpatialEnvelopeWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                List<String> entityUIDList = new ArrayList<>();
                entityUIDList.add(this.getEntityUID());
                Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                if(getGeospatialScaleContentMap.size() == 1){
                    return GeospatialCalculateUtil.getGeometryEnvelopeWKTContent(getGeospatialScaleContentMap.get(this.getEntityUID()));
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public String getEntitySpatialCentroidPointWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                List<String> entityUIDList = new ArrayList<>();
                entityUIDList.add(this.getEntityUID());
                Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                if(getGeospatialScaleContentMap.size() == 1){
                    return GeospatialCalculateUtil.getGeometryCentroidPointWKTContent(getGeospatialScaleContentMap.get(this.getEntityUID()));
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default public String getEntitySpatialInteriorPointWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                validateSpatialScaleLevel(workingGraphOperationExecutor,spatialScaleLevel);
                List<String> entityUIDList = new ArrayList<>();
                entityUIDList.add(this.getEntityUID());
                Map<String,String> getGeospatialScaleContentMap = getGeospatialScaleContent(workingGraphOperationExecutor,spatialScaleLevel,entityUIDList);
                if(getGeospatialScaleContentMap.size() == 1){
                    return GeospatialCalculateUtil.getGeometryInteriorPointWKTContent(getGeospatialScaleContentMap.get(this.getEntityUID()));
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
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
        String spatialScalePropertyName = getGeospatialScaleContentAttributeName(spatialScaleLevel);
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

    private String getGeospatialScaleContentAttributeName(SpatialScaleLevel spatialScaleLevel){
        String spatialScalePropertyName = null;
        switch(spatialScaleLevel){
            case Local: spatialScalePropertyName = RealmConstant._GeospatialLLGeometryContent;break;
            case Global: spatialScalePropertyName = RealmConstant._GeospatialGLGeometryContent;break;
            case Country: spatialScalePropertyName = RealmConstant._GeospatialCLGeometryContent;break;
        }
        return spatialScalePropertyName;
    }

    private Map<String,String> getEntitiesGeospatialScaleContentMap(GraphOperationExecutor workingGraphOperationExecutor,
                 String targetConceptionKind,AttributesParameters attributesParameters,SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceEntityExploreException {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setDistinctMode(true);
        queryParameters.setResultNumber(100000000);
        if (attributesParameters != null) {
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
        }

        List<String> attributeNames = new ArrayList<>();
        String spatialScalePropertyName = getGeospatialScaleContentAttributeName(spatialScaleLevel);
        attributeNames.add(spatialScalePropertyName);

        String queryCql = CypherBuilder.matchAttributesWithQueryParameters(targetConceptionKind,queryParameters,attributeNames);

        Map<String,String> entitiesSpatialContentDataMap = new HashMap<>();
        DataTransformer spatialScalePropertyHandelTransformer = new DataTransformer(){
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long nodeUID = nodeRecord.get("id("+CypherBuilder.operationResultName+")").asInt();
                    String conceptionEntityUID = ""+nodeUID;
                    String spatialScalePropertyValue = null;
                    if(nodeRecord.containsKey("operationResult."+spatialScalePropertyName)){
                        spatialScalePropertyValue = nodeRecord.get("operationResult."+spatialScalePropertyName).asString();
                    }
                    if(spatialScalePropertyValue != null){
                        entitiesSpatialContentDataMap.put(conceptionEntityUID,spatialScalePropertyValue);
                    }
                }
                return null;
            }
        };
        workingGraphOperationExecutor.executeRead(spatialScalePropertyHandelTransformer, queryCql);

        return entitiesSpatialContentDataMap;
    }

    private List<ConceptionEntity> getConceptionEntitiesByUIDs(GraphOperationExecutor workingGraphOperationExecutor,Set<String> matchedEntityUIDSet){

        if(matchedEntityUIDSet!= null){
            String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + matchedEntityUIDSet.toString()+"\n"+
                    "RETURN DISTINCT targetNodes as operationResult";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,
                    getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
            Object conceptionEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString);
            return conceptionEntityList != null ? (List<ConceptionEntity>)conceptionEntityList : null;
        }
        return null;
    }
}
