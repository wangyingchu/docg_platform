package com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable.SpatialPredicateType;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GeospatialCalculateUtil {

    private static GeometryFactory geometryFactory = null;
    private static WKTReader _WKTReader = null;

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

    public static double getGeometryArea(String fromGeometryWKT) throws CoreRealmServiceRuntimeException{
        Geometry targetGeometry = getGeometryFromWKT(fromGeometryWKT);
        return targetGeometry.getArea();
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
}
