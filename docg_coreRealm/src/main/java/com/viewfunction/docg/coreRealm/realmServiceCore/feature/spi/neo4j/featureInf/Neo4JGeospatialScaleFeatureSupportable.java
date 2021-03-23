package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JGeospatialScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JGeospatialScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(geospatialScaleEventUID), null, null);
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialScaleEventClass, getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getSingleConceptionEntityTransformer, queryCql);
                if(resEntityRes == null){
                    logger.error("GeospatialScaleEvent does not contains entity with UID {}.", geospatialScaleEventUID);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("GeospatialScaleEvent does not contains entity with UID " + geospatialScaleEventUID + ".");
                    throw exception;
                }else{
                    Neo4JGeospatialScaleEventImpl neo4JGeospatialScaleEventImpl = new Neo4JGeospatialScaleEventImpl(null,null,null,null,geospatialScaleEventUID);
                    neo4JGeospatialScaleEventImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    if(neo4JGeospatialScaleEventImpl.getAttachConceptionEntity().getConceptionEntityUID().equals(this.getEntityUID())){
                        String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(geospatialScaleEventUID),null,null);
                        Object deletedEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, deleteCql);
                        if(deletedEntityRes == null){
                            throw new CoreRealmServiceRuntimeException();
                        }else{
                            return true;
                        }
                    }else{
                        logger.error("GeospatialScaleEvent with entity UID {} doesn't attached to current ConceptionEntity with UID {}.", geospatialScaleEventUID,this.getEntityUID());
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("GeospatialScaleEvent with entity UID " + geospatialScaleEventUID + " doesn't attached to current ConceptionEntity with UID "+ this.getEntityUID()+ ".");
                        throw exception;
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    public default List<GeospatialScaleEvent> getAttachedGeospatialScaleEvents(){
        if(this.getEntityUID() != null) {
            String queryCql = "MATCH(currentEntity)-[:`" + RealmConstant.GeospatialScale_AttachToRelationClass + "`]->(geospatialScaleEvents:DOCG_GeospatialScaleEvent) WHERE id(currentEntity) = " + this.getEntityUID() + " \n" +
                    "RETURN geospatialScaleEvents as operationResult";
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                GetListGeospatialScaleEventTransformer getListGeospatialScaleEventTransformer = new GetListGeospatialScaleEventTransformer(null,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEventTransformer,queryCql);
                if(queryRes != null){
                    List<GeospatialScaleEvent> res = (List<GeospatialScaleEvent>)queryRes;
                    return res;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return new ArrayList<>();
    }

    public default List<GeospatialScaleEntity> getAttachedGeospatialScaleEntities(){
        if(this.getEntityUID() != null) {
            String queryCql = "MATCH(currentEntity)-[:`" + RealmConstant.GeospatialScale_AttachToRelationClass + "`]->(geospatialScaleEvents:DOCG_GeospatialScaleEvent)<-[:`"+RealmConstant.GeospatialScale_GeospatialReferToRelationClass+"`]-(geospatialScaleEntities:`DOCG_GeospatialScaleEntity`) WHERE id(currentEntity) = " + this.getEntityUID() + " \n" +
                    "RETURN geospatialScaleEntities as operationResult";
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                        new GetListGeospatialScaleEntityTransformer(null,null,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
                if(queryRes != null){
                    return (List<GeospatialScaleEntity>)queryRes;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return new ArrayList<>();
    }

    public default List<GeospatialScaleDataPair> getAttachedGeospatialScaleDataPairs(){
        List<GeospatialScaleDataPair> geospatialScaleDataPairList = new ArrayList<>();
        if(this.getEntityUID() != null) {
            String queryCql = "MATCH(currentEntity)-[:`" + RealmConstant.GeospatialScale_AttachToRelationClass + "`]->(geospatialScaleEvents:DOCG_GeospatialScaleEvent)<-[:`"+RealmConstant.GeospatialScale_GeospatialReferToRelationClass+"`]-(geospatialScaleEntities:`DOCG_GeospatialScaleEntity`) WHERE id(currentEntity) = " + this.getEntityUID() + " \n" +
                    "RETURN geospatialScaleEntities ,geospatialScaleEvents";
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                DataTransformer<Object> _DataTransformer = new DataTransformer<Object>() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()) {
                            Record record = result.next();

                            Neo4JGeospatialScaleEntityImpl neo4JGeospatialScaleEntityImpl = null;
                            Neo4JGeospatialScaleEventImpl neo4JGeospatialScaleEventImpl = null;

                            Node geospatialScaleEntityNode = record.get("geospatialScaleEntities").asNode();
                            List<String> allLabelNames = Lists.newArrayList(geospatialScaleEntityNode.labels());
                            boolean isMatchedEntity = true;
                            if (allLabelNames.size() > 0) {
                                isMatchedEntity = allLabelNames.contains(RealmConstant.GeospatialScaleEntityClass);
                            }
                            if (isMatchedEntity) {
                                long nodeUID = geospatialScaleEntityNode.id();
                                String conceptionEntityUID = ""+nodeUID;
                                String targetGeospatialCode = geospatialScaleEntityNode.get(RealmConstant.GeospatialCodeProperty).asString();
                                String targetGeospatialScaleGradeString = geospatialScaleEntityNode.get(RealmConstant.GeospatialScaleGradeProperty).asString();
                                String currentGeospatialRegionName = geospatialScaleEntityNode.get(RealmConstant.GeospatialRegionProperty).asString();
                                String _ChineseName = null;
                                String _EnglishName = null;
                                if(geospatialScaleEntityNode.containsKey(RealmConstant.GeospatialChineseNameProperty)){
                                    _ChineseName = geospatialScaleEntityNode.get(RealmConstant.GeospatialChineseNameProperty).asString();
                                }
                                if(geospatialScaleEntityNode.containsKey(RealmConstant.GeospatialEnglishNameProperty)){
                                    _EnglishName = geospatialScaleEntityNode.get(RealmConstant.GeospatialEnglishNameProperty).asString();
                                }
                                GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade = getGeospatialScaleGrade(targetGeospatialScaleGradeString);
                                neo4JGeospatialScaleEntityImpl =
                                        new Neo4JGeospatialScaleEntityImpl(null,currentGeospatialRegionName,conceptionEntityUID,geospatialScaleGrade,targetGeospatialCode,_ChineseName,_EnglishName);
                                neo4JGeospatialScaleEntityImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                            }

                            Node geospatialScaleEventNode = record.get("geospatialScaleEvents").asNode();
                            List<String> allConceptionKindNames = Lists.newArrayList(geospatialScaleEventNode.labels());
                            boolean isMatchedConceptionKind = false;
                            if(allConceptionKindNames.size()>0){
                                isMatchedConceptionKind = allConceptionKindNames.contains(RealmConstant.GeospatialScaleEventClass);
                            }
                            if(isMatchedConceptionKind) {
                                long nodeUID = geospatialScaleEventNode.id();
                                String geospatialScaleEventUID = "" + nodeUID;
                                String eventComment = geospatialScaleEventNode.get(RealmConstant._GeospatialScaleEventComment).asString();
                                String geospatialScaleGrade = geospatialScaleEventNode.get(RealmConstant._GeospatialScaleEventScaleGrade).asString();
                                String referLocation = geospatialScaleEventNode.get(RealmConstant._GeospatialScaleEventReferLocation).asString();
                                String geospatialRegion = geospatialScaleEventNode.get(RealmConstant._GeospatialScaleEventGeospatialRegion).asString();

                                neo4JGeospatialScaleEventImpl = new Neo4JGeospatialScaleEventImpl(geospatialRegion, eventComment, referLocation, getGeospatialScaleGrade(geospatialScaleGrade.trim()), geospatialScaleEventUID);
                                neo4JGeospatialScaleEventImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                            }
                            if(neo4JGeospatialScaleEntityImpl != null && neo4JGeospatialScaleEventImpl != null){
                                geospatialScaleDataPairList.add(
                                        new GeospatialScaleDataPair(neo4JGeospatialScaleEventImpl,neo4JGeospatialScaleEntityImpl)
                                );
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(_DataTransformer,queryCql);
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return geospatialScaleDataPairList;
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
