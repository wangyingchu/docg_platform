package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JGeospatialScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Neo4JGeospatialScaleFeatureSupportable extends GeospatialScaleFeatureSupportable,Neo4JKeyResourcesRetrievable{

    static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialScaleFeatureSupportable.class);

    public default WKTGeometryType getGeometryType() {
        String returnDataValue = getAttributeValue(RealmConstant._GeospatialGeometryType);
        if(returnDataValue != null){
            if(returnDataValue.equals(""+ WKTGeometryType.POINT)){
                return WKTGeometryType.POINT;
            }else if(returnDataValue.equals(""+ WKTGeometryType.LINESTRING)){
                return WKTGeometryType.LINESTRING;
            }else if(returnDataValue.equals(""+ WKTGeometryType.POLYGON)){
                return WKTGeometryType.POLYGON;
            }else if(returnDataValue.equals(""+ WKTGeometryType.MULTIPOINT)){
                return WKTGeometryType.MULTIPOINT;
            }else if(returnDataValue.equals(""+ WKTGeometryType.MULTILINESTRING)){
                return WKTGeometryType.MULTILINESTRING;
            }else if(returnDataValue.equals(""+ WKTGeometryType.MULTIPOLYGON)){
                return WKTGeometryType.MULTIPOLYGON;
            }else if(returnDataValue.equals(""+ WKTGeometryType.GEOMETRYCOLLECTION)){
                return WKTGeometryType.GEOMETRYCOLLECTION;
            }
        }
        return null;
    }

    public default boolean addOrUpdateGeometryType(WKTGeometryType wKTGeometryType){
        return addOrUpdateAttributeValue(RealmConstant._GeospatialGeometryType,""+wKTGeometryType);
    }

    public default String getGlobalCRSAID(){
        return getAttributeValue(RealmConstant._GeospatialGlobalCRSAID);
    }

    public default boolean addOrUpdateGlobalCRSAID(String crsAID){
        return addOrUpdateAttributeValue(RealmConstant._GeospatialGlobalCRSAID,crsAID);
    }

    public default String getCountryCRSAID(){
        return getAttributeValue(RealmConstant._GeospatialCountryCRSAID);
    }

    public default boolean addOrUpdateCountryCRSAID(String crsAID){
        return addOrUpdateAttributeValue(RealmConstant._GeospatialCountryCRSAID,crsAID);
    }

    public default String getLocalCRSAID(){
        return getAttributeValue(RealmConstant._GeospatialLocalCRSAID);
    }

    public default boolean addOrUpdateLocalCRSAID(String crsAID){
        return addOrUpdateAttributeValue(RealmConstant._GeospatialLocalCRSAID,crsAID);
    }

    public default String getGLGeometryContent(){
        return getAttributeValue(RealmConstant._GeospatialGLGeometryContent);
    }

    public default boolean addOrUpdateGLGeometryContent(String wKTContent){
        return addOrUpdateAttributeValue(RealmConstant._GeospatialGLGeometryContent,wKTContent);
    }

    public default String getCLGeometryContent(){
        return getAttributeValue(RealmConstant._GeospatialCLGeometryContent);
    }

    public default boolean addOrUpdateCLGeometryContent(String wKTContent){
        return addOrUpdateAttributeValue(RealmConstant._GeospatialCLGeometryContent,wKTContent);
    }

    public default String getLLGeometryContent(){
        return getAttributeValue(RealmConstant._GeospatialLLGeometryContent);
    }

    public default boolean addOrUpdateLLGeometryContent(String wKTContent){
        return addOrUpdateAttributeValue(RealmConstant._GeospatialLLGeometryContent,wKTContent);
    }

    public default GeospatialScaleEvent attachGeospatialScaleEvent(String geospatialCode, String eventComment,
                                                                   Map<String, Object> eventData) throws CoreRealmServiceRuntimeException{
        return attachGeospatialScaleEventInnerLogic(RealmConstant._defaultGeospatialRegionName,geospatialCode,eventComment,eventData);
    }

    public default GeospatialScaleEvent attachGeospatialScaleEvent(String geospatialRegionName,String geospatialCode,
                                                                   String eventComment, Map<String, Object> eventData) throws CoreRealmServiceRuntimeException{
        return attachGeospatialScaleEventInnerLogic(geospatialRegionName,geospatialCode,eventComment,eventData);
    }

    public default boolean detachGeospatialScaleEvent(String geospatialScaleEventUID) throws CoreRealmServiceRuntimeException{
        return false;
    }

    public default List<GeospatialScaleEvent> getAttachedGeospatialScaleEvents(){
        return null;
    }

    public default List<GeospatialScaleEntity> getAttachedGeospatialScaleEntities(){
        return null;
    }

    public default List<GeospatialScaleDataPair> getAttachedGeospatialScaleDataPairs(){
        return null;
    }

    private GeospatialScaleEvent attachGeospatialScaleEventInnerLogic(String geospatialRegionName,String geospatialCode,
                                                                      String eventComment, Map<String, Object> eventData) throws CoreRealmServiceRuntimeException {
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(1);
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName));
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialCodeProperty,geospatialCode), QueryParameters.FilteringLogic.AND);
                queryParameters.setDistinctMode(true);
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleEntityClass,queryParameters,null);

                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialScaleEntityClass, workingGraphOperationExecutor);
                Object targetGeospatialScaleEntityRes = workingGraphOperationExecutor.executeRead(getSingleConceptionEntityTransformer, queryCql);
                if(targetGeospatialScaleEntityRes == null){
                    logger.error("GeospatialScaleEntity with geospatialCode {} doesn't exist.", geospatialCode);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("GeospatialScaleEntity with geospatialCode "+geospatialCode+" doesn't exist.");
                    throw exception;
                }
                ConceptionEntity targetGeospatialScaleEntity = (ConceptionEntity)targetGeospatialScaleEntityRes;
                String eventGeospatialScaleGrade = targetGeospatialScaleEntity.getAttribute(RealmConstant.GeospatialScaleGradeProperty).
                        getAttributeValue().toString();

                Map<String, Object> propertiesMap = eventData != null ? eventData : new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                propertiesMap.put(RealmConstant._GeospatialScaleEventReferLocation,geospatialCode);
                propertiesMap.put(RealmConstant._GeospatialScaleEventComment,eventComment);
                propertiesMap.put(RealmConstant._GeospatialScaleEventScaleGrade,eventGeospatialScaleGrade);
                propertiesMap.put(RealmConstant._GeospatialScaleEventGeospatialRegion,geospatialRegionName);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.GeospatialScaleEventClass}, propertiesMap);
                logger.debug("Generated Cypher Statement: {}", createCql);
                getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialScaleEventClass, workingGraphOperationExecutor);
                Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createCql);
                if(newEntityRes != null) {
                    ConceptionEntity geospatialScaleEventEntity = (ConceptionEntity) newEntityRes;
                    geospatialScaleEventEntity.attachToRelation(this.getEntityUID(), RealmConstant.GeospatialScale_AttachToRelationClass, null, true);
                    RelationEntity linkToGeospatialScaleEntityRelation = targetGeospatialScaleEntity.attachFromRelation(geospatialScaleEventEntity.getConceptionEntityUID(), RealmConstant.GeospatialScale_GeospatialReferToRelationClass, null, true);
                    if(linkToGeospatialScaleEntityRelation != null){
                        Neo4JGeospatialScaleEventImpl neo4JGeospatialScaleEventImpl = new Neo4JGeospatialScaleEventImpl(geospatialRegionName,eventComment,geospatialCode,getGeospatialScaleGrade(eventGeospatialScaleGrade.trim()),geospatialScaleEventEntity.getConceptionEntityUID());
                        neo4JGeospatialScaleEventImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                        return neo4JGeospatialScaleEventImpl;
                    }
                }
            }catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    private GeospatialRegion.GeospatialScaleGrade getGeospatialScaleGrade(String geospatialScaleGradeValue){
        if(geospatialScaleGradeValue.equals("CONTINENT")){
            return GeospatialRegion.GeospatialScaleGrade.CONTINENT;
        }else if(geospatialScaleGradeValue.equals("COUNTRY_REGION")){
            return GeospatialRegion.GeospatialScaleGrade.COUNTRY_REGION;
        }else if(geospatialScaleGradeValue.equals("PROVINCE")){
            return GeospatialRegion.GeospatialScaleGrade.PROVINCE;
        }else if(geospatialScaleGradeValue.equals("PREFECTURE")){
            return GeospatialRegion.GeospatialScaleGrade.PREFECTURE;
        }else if(geospatialScaleGradeValue.equals("COUNTY")){
            return GeospatialRegion.GeospatialScaleGrade.COUNTY;
        }else if(geospatialScaleGradeValue.equals("TOWNSHIP")){
            return GeospatialRegion.GeospatialScaleGrade.TOWNSHIP;
        }else if(geospatialScaleGradeValue.equals("VILLAGE")){
            return GeospatialRegion.GeospatialScaleGrade.VILLAGE;
        }
        return null;
    }

    private String getAttributeValue(String attributeName){
        if (getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(getEntityUID()),new String[]{attributeName});
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record returnRecord = result.next();
                            Map<String,Object> returnValueMap = returnRecord.asMap();
                            String attributeNameFullName= CypherBuilder.operationResultName+"."+ attributeName;
                            Object attributeValueObject = returnValueMap.get(attributeNameFullName);
                            if(attributeValueObject!= null){
                                return attributeValueObject;
                            }
                        }
                        return null;
                    }
                };
                Object resultRes = workingGraphOperationExecutor.executeRead(dataTransformer,queryCql);
                return resultRes !=null ? resultRes.toString() : null;
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    private boolean addOrUpdateAttributeValue(String attributeName,Object attributeValue) {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                Map<String,Object> attributeDataMap = new HashMap<>();
                attributeDataMap.put(attributeName,attributeValue);
                String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(getEntityUID()),attributeDataMap);
                DataTransformer updateItemDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record returnRecord = result.next();
                            Map<String,Object> returnValueMap = returnRecord.asMap();
                            String attributeNameFullName= CypherBuilder.operationResultName+"."+ attributeName;
                            Object attributeValueObject = returnValueMap.get(attributeNameFullName);
                            if(attributeValueObject!= null){
                                return attributeValueObject;
                            }
                        }
                        return null;
                    }
                };
                Object resultRes = workingGraphOperationExecutor.executeWrite(updateItemDataTransformer,updateCql);
                return resultRes != null ? true: false;
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }
}
