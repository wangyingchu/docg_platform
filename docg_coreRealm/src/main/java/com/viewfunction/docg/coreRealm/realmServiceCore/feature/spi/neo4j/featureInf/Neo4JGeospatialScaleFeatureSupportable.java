package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;
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
        return null;
    }

    public default GeospatialScaleEvent attachGeospatialScaleEvent(String geospatialRegionName,String geospatialCode,
                                                                   String eventComment, Map<String, Object> eventData) throws CoreRealmServiceRuntimeException{
        return null;
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
