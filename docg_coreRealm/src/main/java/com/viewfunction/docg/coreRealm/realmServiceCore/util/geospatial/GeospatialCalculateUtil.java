package com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable.SpatialPredicateType;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.PolygonArea;
import net.sf.geographiclib.PolygonResult;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GeospatialCalculateUtil {

    private static GeometryFactory geometryFactory = null;
    private static WKTReader _WKTReader = null;
    static Logger logger = LoggerFactory.getLogger(GeospatialCalculateUtil.class);

    public static boolean spatialPredicateWKTCalculate(String fromGeometryWKT,
                                                       SpatialPredicateType spatialPredicateType, String toGeometryWKT) throws CoreRealmServiceRuntimeException {
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
            Geometry toGeometry = _WKTReader.read(toGeometryWKT);
            return spatialPredicateWKTCalculate(fromGeometry,spatialPredicateType,toGeometry);
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
        }
    }

    public static boolean spatialPredicateWKTCalculate(String fromGeometryWKT,
                                                       SpatialPredicateType spatialPredicateType, Set<String> toGeometryWKTSet) throws CoreRealmServiceRuntimeException {
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
            Geometry toGeometries = null;
            for(String currentWTK:toGeometryWKTSet){
                if(currentWTK != null){
                    Geometry currentToGeometry = _WKTReader.read(currentWTK);
                    if(toGeometries == null){
                        toGeometries = currentToGeometry;
                    }else{
                        toGeometries = toGeometries.union(currentToGeometry);
                    }
                }
            }
            if(toGeometries != null){
                return spatialPredicateWKTCalculate(fromGeometry,spatialPredicateType,toGeometries);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
        }
        return false;
    }

    public static Set<String> spatialBufferPredicateFilterWKTsCalculate(String fromGeometryWKT,double bufferDistanceValue,
                                                                        SpatialPredicateType spatialPredicateType, Map<String,String> entitiesSpatialContentDataMap) throws CoreRealmServiceRuntimeException {
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
            Geometry bufferedFromGeometry = fromGeometry.buffer(bufferDistanceValue);
            return spatialPredicateFilterWKTsCalculate(bufferedFromGeometry.toText(),spatialPredicateType,entitiesSpatialContentDataMap);
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
        }
    }

    public static Set<String> spatialPredicateFilterWKTsCalculate(String fromGeometryWKT,
                                                                  SpatialPredicateType spatialPredicateType, Map<String,String> entitiesSpatialContentDataMap) throws CoreRealmServiceRuntimeException {
        if(entitiesSpatialContentDataMap != null && entitiesSpatialContentDataMap.size()>0){
            if(geometryFactory == null){
                geometryFactory = JTSFactoryFinder.getGeometryFactory();
                _WKTReader = new WKTReader(geometryFactory);
            }
            try {
                Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
                Set<String> entityUIDsSet = new HashSet<>();
                if(entitiesSpatialContentDataMap.size() <= 1000){
                    Geometry currentToGeometry = null;
                    for(Map.Entry<String, String> entry : entitiesSpatialContentDataMap.entrySet()){
                        String entityUID = entry.getKey();
                        String spatialContent = entry.getValue();
                        if(spatialContent != null){
                            try {
                                currentToGeometry = _WKTReader.read(spatialContent);
                                if(spatialPredicateWKTCalculate(fromGeometry,spatialPredicateType,currentToGeometry)){
                                    entityUIDsSet.add(entityUID);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    List<EntitySpatialContentMapper> entitySpatialContentMapperList = new ArrayList<>();
                    for(Map.Entry<String, String> entry : entitiesSpatialContentDataMap.entrySet()){
                        String entityUID = entry.getKey();
                        String spatialContent = entry.getValue();
                        entitySpatialContentMapperList.add(new EntitySpatialContentMapper(entityUID,spatialContent));
                    }
                    int degreeOfParallelism = BatchDataOperationUtil.calculateRuntimeCPUCoresByUsageRate(BatchDataOperationUtil.CPUUsageRate.High);
                    int singlePartitionSize = (entitySpatialContentMapperList.size()/degreeOfParallelism)+1;
                    List<List<EntitySpatialContentMapper>> rsList = Lists.partition(entitySpatialContentMapperList, singlePartitionSize);

                    ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
                    for(List<EntitySpatialContentMapper> currentEntitySpatialContentMapperList:rsList){
                        SpatialPredicateWKTCalculateThread spatialPredicateWKTCalculateThread = new SpatialPredicateWKTCalculateThread(fromGeometry,spatialPredicateType,entityUIDsSet,currentEntitySpatialContentMapperList);
                        executor.execute(spatialPredicateWKTCalculateThread);
                    }
                    executor.shutdown();
                    try {
                        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return entityUIDsSet;
            } catch (ParseException e) {
                e.printStackTrace();
                CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
                runtimeException.setCauseMessage("Geometry WKT Parse error");
                throw runtimeException;
            }
        }else{
            return null;
        }
    }

    private static class EntitySpatialContentMapper{
        private String entityUID;
        private String entitySpatialContentValue;
        public EntitySpatialContentMapper(String entityUID,String entitySpatialContentValue){
            this.entityUID = entityUID;
            this.entitySpatialContentValue = entitySpatialContentValue;
        }

        public String getEntityUID() {
            return entityUID;
        }

        public String getEntitySpatialContentValue() {
            return entitySpatialContentValue;
        }
    }

    private static class SpatialPredicateWKTCalculateThread implements Runnable{

        private Set<String> entityUIDsSet;
        private List<EntitySpatialContentMapper> entitySpatialContentMapperList;
        private Geometry fromGeometry;
        private SpatialPredicateType spatialPredicateType;
        public SpatialPredicateWKTCalculateThread(Geometry fromGeometry,SpatialPredicateType spatialPredicateType,Set<String> entityUIDsSet,List<EntitySpatialContentMapper> entitySpatialContentMapperList){
            this.entityUIDsSet = entityUIDsSet;
            this.entitySpatialContentMapperList = entitySpatialContentMapperList;
            this.fromGeometry = fromGeometry;
            this.spatialPredicateType = spatialPredicateType;
        }

        @Override
        public void run() {
            Geometry currentToGeometry = null;
            for(EntitySpatialContentMapper currentEntitySpatialContentMapper:this.entitySpatialContentMapperList){
                String entityUID = currentEntitySpatialContentMapper.getEntityUID();
                String spatialContent = currentEntitySpatialContentMapper.getEntitySpatialContentValue();
                try {
                    if(spatialContent != null){
                        currentToGeometry = _WKTReader.read(spatialContent);
                        if(spatialPredicateWKTCalculate(this.fromGeometry,this.spatialPredicateType,currentToGeometry)){
                            entityUIDsSet.add(entityUID);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static GeospatialScaleFeatureSupportable.WKTGeometryType getGeometryWKTType(String geometryWKT) throws CoreRealmServiceRuntimeException{
        Geometry targetGeometry = getGeometryFromWKT(geometryWKT);
        String geometryTypeStr = targetGeometry.getGeometryType();
        if(Geometry.TYPENAME_POLYGON.equals(geometryTypeStr)){
            return GeospatialScaleFeatureSupportable.WKTGeometryType.POLYGON;
        }else if(Geometry.TYPENAME_POINT.equals(geometryTypeStr)){
            return GeospatialScaleFeatureSupportable.WKTGeometryType.POINT;
        }else if(Geometry.TYPENAME_MULTIPOINT.equals(geometryTypeStr)){
            return GeospatialScaleFeatureSupportable.WKTGeometryType.MULTIPOINT;
        }else if(Geometry.TYPENAME_LINESTRING.equals(geometryTypeStr)){
            return GeospatialScaleFeatureSupportable.WKTGeometryType.LINESTRING;
        }else if(Geometry.TYPENAME_MULTILINESTRING.equals(geometryTypeStr)){
            return GeospatialScaleFeatureSupportable.WKTGeometryType.MULTILINESTRING;
        }else if(Geometry.TYPENAME_MULTIPOLYGON.equals(geometryTypeStr)){
            return GeospatialScaleFeatureSupportable.WKTGeometryType.MULTIPOLYGON;
        }else if(Geometry.TYPENAME_GEOMETRYCOLLECTION.equals(geometryTypeStr)){
            return GeospatialScaleFeatureSupportable.WKTGeometryType.GEOMETRYCOLLECTION;
        }
        return null;
    }

    public static double getGeometriesDistance(String fromGeometryWKT,String toGeometryWKT) throws CoreRealmServiceRuntimeException{
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
            Geometry toGeometry = _WKTReader.read(toGeometryWKT);
            return fromGeometry.distance(toGeometry);
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
        }
    }

    public static boolean isGeometriesInDistance(String fromGeometryWKT,String toGeometryWKT,double distanceValue) throws CoreRealmServiceRuntimeException{
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
            Geometry toGeometry = _WKTReader.read(toGeometryWKT);
            return fromGeometry.isWithinDistance(toGeometry,distanceValue);
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
        }
    }

    public static boolean isGeometriesInDistance(String fromGeometryWKT,Set<String> toGeometryWKTSet,double distanceValue) throws CoreRealmServiceRuntimeException{
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
            Geometry toGeometries = null;
            for(String currentWTK:toGeometryWKTSet){
                if(currentWTK != null){
                    Geometry currentToGeometry = _WKTReader.read(currentWTK);
                    if(toGeometries == null){
                        toGeometries = currentToGeometry;
                    }else{
                        toGeometries = toGeometries.union(currentToGeometry);
                    }
                }
            }
            if(toGeometries != null){
                return fromGeometry.isWithinDistance(toGeometries,distanceValue);
            }else{
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
        }
    }

    public static String getGeometryBufferWKTContent(String fromGeometryWKT,double distanceValue) throws CoreRealmServiceRuntimeException{
        Geometry bufferedGeometry = getGeometryFromWKT(fromGeometryWKT).buffer(distanceValue);
        return bufferedGeometry.toText();
    }

    public static String getGeometryEnvelopeWKTContent(String fromGeometryWKT) throws CoreRealmServiceRuntimeException{
        Geometry envelopeGeometry = getGeometryFromWKT(fromGeometryWKT).getEnvelope();
        return envelopeGeometry.toText();
    }

    public static String getGeometryCentroidPointWKTContent(String fromGeometryWKT) throws CoreRealmServiceRuntimeException{
        Geometry centroidGeometry = getGeometryFromWKT(fromGeometryWKT).getCentroid();
        return centroidGeometry.toText();
    }

    public static String getGeometryInteriorPointWKTContent(String fromGeometryWKT) throws CoreRealmServiceRuntimeException{
        Geometry interiorGeometry = getGeometryFromWKT(fromGeometryWKT).getInteriorPoint();
        return interiorGeometry.toText();
    }

    public static double getGeometryArea(String geometryWKT, GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        Geometry targetGeometry = getGeometryFromWKT(geometryWKT);
        if(targetGeometry instanceof Polygon || targetGeometry instanceof MultiPolygon){
            if(GeospatialScaleCalculable.SpatialScaleLevel.Local.equals(spatialScaleLevel)){
                return targetGeometry.getArea();
            }else{
                Coordinate[] coordinates = targetGeometry.getCoordinates();
                Double[][] result = new Double[coordinates.length][2];
                for (int i = 0; i < coordinates.length; i++) {
                    result[i][0] = coordinates[i].x;
                    result[i][1] = coordinates[i].y;
                }
                PolygonArea targetPolygonArea = new PolygonArea(Geodesic.WGS84,false);
                for(Double[] currentPoint:result){
                    targetPolygonArea.AddPoint(currentPoint[1],currentPoint[0]);
                }
                PolygonResult res = targetPolygonArea.Compute();
                targetPolygonArea.Clear();
                return Math.abs(res.area);
            }
        }else{
            logger.error("Geometry type {} doesn't supported in getGeometryArea API.",targetGeometry.getGeometryType());
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Geometry type "+targetGeometry.getGeometryType()+" doesn't supported in getGeometryArea API.\"");
            throw exception;
        }
    }

    public static double getGeometryPerimeter(String geometryWKT, GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        Geometry targetGeometry = getGeometryFromWKT(geometryWKT);
        if(targetGeometry instanceof Polygon || targetGeometry instanceof MultiPolygon){
            if(GeospatialScaleCalculable.SpatialScaleLevel.Local.equals(spatialScaleLevel)){
                return targetGeometry.getLength();
            }else{
                Coordinate[] coordinates = targetGeometry.getCoordinates();
                Double[][] result = new Double[coordinates.length][2];
                for (int i = 0; i < coordinates.length; i++) {
                    result[i][0] = coordinates[i].x;
                    result[i][1] = coordinates[i].y;
                }
                PolygonArea targetPolygonArea = new PolygonArea(Geodesic.WGS84,false);
                for(Double[] currentPoint:result){
                    targetPolygonArea.AddPoint(currentPoint[1],currentPoint[0]);
                }
                PolygonResult res = targetPolygonArea.Compute();
                targetPolygonArea.Clear();
                return res.perimeter;
            }
        }else{
            logger.error("Geometry type {} doesn't supported in getGeometryPerimeter API.",targetGeometry.getGeometryType());
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Geometry type "+targetGeometry.getGeometryType()+" doesn't supported in getGeometryPerimeter API.\"");
            throw exception;
        }
    }

    public static double getGeometryLineLength(String geometryWKT, GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        Geometry targetGeometry = getGeometryFromWKT(geometryWKT);
        if(targetGeometry instanceof LineString || targetGeometry instanceof MultiLineString){
            if(GeospatialScaleCalculable.SpatialScaleLevel.Local.equals(spatialScaleLevel)){
                return targetGeometry.getLength();
            }else{
                Coordinate[] coordinates = targetGeometry.getCoordinates();
                Double[][] result = new Double[coordinates.length][2];
                for (int i = 0; i < coordinates.length; i++) {
                    result[i][0] = coordinates[i].x;
                    result[i][1] = coordinates[i].y;
                }
                double totalLength = 0;
                Geodesic geod = Geodesic.WGS84;
                for (int i = 0; i < result.length - 1; i++) {
                    GeodesicData g = geod.Inverse(
                            result[i][1], result[i][0],  // 点1 纬度,经度
                            result[i+1][1], result[i+1][0] // 点2 纬度,经度
                    );
                    totalLength += g.s12; // s12是两点之间的距离(米)
                }
                return totalLength;
            }
        }else{
            logger.error("Geometry type {} doesn't supported in getGeometryLineLength API.",targetGeometry.getGeometryType());
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Geometry type "+targetGeometry.getGeometryType()+" doesn't supported in getGeometryLineLength API.\"");
            throw exception;
        }
    }

    private static Geometry getGeometryFromWKT(String wktValue)throws CoreRealmServiceRuntimeException{
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry geometry = _WKTReader.read(wktValue);
            return geometry;
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
        }
    }

    private static boolean spatialPredicateWKTCalculate(Geometry fromGeometry,
                                                        SpatialPredicateType spatialPredicateType, Geometry toGeometry){
        // https://docs.geotools.org/latest/userguide/library/jts/geometry.html
        boolean calculateResult = false;
        switch (spatialPredicateType){
            case Equals:
                calculateResult = fromGeometry.equals(toGeometry);
                break;
            case Within:
                calculateResult = fromGeometry.within(toGeometry);
                break;
            case Crosses:
                calculateResult = fromGeometry.crosses(toGeometry);
                break;
            case Touches:
                calculateResult = fromGeometry.touches(toGeometry);
                break;
            case Contains:
                calculateResult = fromGeometry.contains(toGeometry);
                break;
            case Overlaps:
                calculateResult = fromGeometry.overlaps(toGeometry);
                break;
            case Intersects:
                calculateResult = fromGeometry.intersects(toGeometry);
                break;
            case Disjoint:
                calculateResult = fromGeometry.disjoint(toGeometry);
                break;
            case Cover:
                calculateResult = fromGeometry.covers(toGeometry);
                break;
            case CoveredBy:
                calculateResult = fromGeometry.coveredBy(toGeometry);
                break;
        }
        return calculateResult;
    }

    public static String getGeoJsonFromWTK(String wktValue){
        String json = null;
        try {
            WKTReader reader = new WKTReader();
            Geometry geometry = reader.read(wktValue);
            StringWriter writer = new StringWriter();
            GeometryJSON g = new GeometryJSON(20);
            g.write(geometry, writer);
            json = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Map<String,String> getEntitiesGeospatialScaleContentMap(GraphOperationExecutor workingGraphOperationExecutor,
                                                                    String targetConceptionKind, AttributesParameters attributesParameters, GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceEntityExploreException {
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

    public static String getGeospatialScaleContentAttributeName(GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel){
        String spatialScalePropertyName = null;
        switch(spatialScaleLevel){
            case Local: spatialScalePropertyName = RealmConstant._GeospatialLLGeometryContent;break;
            case Global: spatialScalePropertyName = RealmConstant._GeospatialGLGeometryContent;break;
            case Country: spatialScalePropertyName = RealmConstant._GeospatialCLGeometryContent;break;
        }
        return spatialScalePropertyName;
    }

    public static Map<String,String> getGeospatialScaleContent(GraphOperationExecutor workingGraphOperationExecutor, GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel, List<String> entityUIDs){
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

    public static Map<String,String> getGeospatialScaleEntityContentMap(GraphOperationExecutor workingGraphOperationExecutor,
                                                                  String targetConceptionKind, AttributesParameters attributesParameters, GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceEntityExploreException{
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
        attributeNames.add(RealmConstant.GeospatialCodeProperty);
        attributeNames.add(RealmConstant.GeospatialChineseNameProperty);
        attributeNames.add(RealmConstant.GeospatialEnglishNameProperty);

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
                    String geospatialCodePropertyValue = null;
                    String geospatialChineseNamePropertyValue = null;
                    String geospatialEnglishNamePropertyValue = null;


                    if(nodeRecord.containsKey("operationResult."+spatialScalePropertyName)){
                        spatialScalePropertyValue = nodeRecord.get("operationResult."+spatialScalePropertyName).asString();
                    }
                    if(spatialScalePropertyValue != null){
                        entitiesSpatialContentDataMap.put(conceptionEntityUID,spatialScalePropertyValue);
                    }
                    if(nodeRecord.containsKey("operationResult."+RealmConstant.GeospatialCodeProperty)){
                        geospatialCodePropertyValue = nodeRecord.get("operationResult."+RealmConstant.GeospatialCodeProperty).asString();
                    }
                    if(geospatialCodePropertyValue != null){
                        entitiesSpatialContentDataMap.put(conceptionEntityUID+"-"+RealmConstant.GeospatialCodeProperty,geospatialCodePropertyValue);
                    }

                    if(nodeRecord.containsKey("operationResult."+RealmConstant.GeospatialChineseNameProperty)){
                        geospatialChineseNamePropertyValue = nodeRecord.get("operationResult."+RealmConstant.GeospatialChineseNameProperty).asString();
                    }
                    if(geospatialChineseNamePropertyValue != null){
                        entitiesSpatialContentDataMap.put(conceptionEntityUID+"-"+RealmConstant.GeospatialChineseNameProperty,geospatialChineseNamePropertyValue);
                    }

                    if(nodeRecord.containsKey("operationResult."+RealmConstant.GeospatialEnglishNameProperty)){
                        geospatialEnglishNamePropertyValue = nodeRecord.get("operationResult."+RealmConstant.GeospatialEnglishNameProperty).asString();
                    }
                    if(geospatialEnglishNamePropertyValue != null){
                        entitiesSpatialContentDataMap.put(conceptionEntityUID+"-"+RealmConstant.GeospatialEnglishNameProperty,geospatialEnglishNamePropertyValue);
                    }
                }
                return null;
            }
        };
        workingGraphOperationExecutor.executeRead(spatialScalePropertyHandelTransformer, queryCql);
        return entitiesSpatialContentDataMap;
    }

    public static List<ConceptionEntity> getConceptionEntitiesByUIDs(GraphOperationExecutor workingGraphOperationExecutor,GraphOperationExecutor globalGraphOperationExecutor, Set<String> matchedEntityUIDSet){
        if(matchedEntityUIDSet!= null){
            String cypherProcedureString = "MATCH (targetNodes) WHERE id(targetNodes) IN " + matchedEntityUIDSet.toString()+"\n"+
                    "RETURN DISTINCT targetNodes as operationResult";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,
                    globalGraphOperationExecutor);
            Object conceptionEntityList = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,cypherProcedureString);
            return conceptionEntityList != null ? (List<ConceptionEntity>)conceptionEntityList : null;
        }
        return null;
    }

    public static List<ConceptionEntityValue> getConceptionEntityAttributesByUIDs(GraphOperationExecutor workingGraphOperationExecutor,Set<String> matchedEntityUIDSet,List<String> attributeNames) throws CoreRealmServiceEntityExploreException {
        if(matchedEntityUIDSet!= null){
            List<String> queryAttributeNames = new ArrayList<>();
            for(String currentAttributeName:attributeNames){
                if(!queryAttributeNames.contains(currentAttributeName)){
                    queryAttributeNames.add(currentAttributeName);
                }
            }
            String cypherProcedureString = CypherBuilder.matchAttributesWithNodeIDs(new ArrayList<>(matchedEntityUIDSet),attributeNames);
            GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer = new GetListConceptionEntityValueTransformer(attributeNames);
            getListConceptionEntityValueTransformer.setUseIDMatchLogic(true);
            Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer,cypherProcedureString);
            if(resEntityRes != null){
                return (List<ConceptionEntityValue>)resEntityRes;
            }
        }
        return null;
    }
}
