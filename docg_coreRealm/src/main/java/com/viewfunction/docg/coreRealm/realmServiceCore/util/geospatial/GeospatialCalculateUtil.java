package com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable.SpatialPredicateType;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.Set;

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

    public static GeospatialScaleFeatureSupportable.WKTGeometryType getGeometryWKTType(String geometryWKT) throws CoreRealmServiceRuntimeException{
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry targetGeometry = _WKTReader.read(geometryWKT);
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
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
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

    public static String getGeometryBufferWKTContent(String fromGeometryWKT,double distanceValue) throws CoreRealmServiceRuntimeException{
        if(geometryFactory == null){
            geometryFactory = JTSFactoryFinder.getGeometryFactory();
            _WKTReader = new WKTReader(geometryFactory);
        }
        try {
            Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
            Geometry bufferedGeometry = fromGeometry.buffer(distanceValue);
            return bufferedGeometry.toText();
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
}
