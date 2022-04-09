package com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable.SpatialPredicateType;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

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
            // https://docs.geotools.org/latest/userguide/library/jts/geometry.html
            Geometry fromGeometry = _WKTReader.read(fromGeometryWKT);
            Geometry toGeometry = _WKTReader.read(toGeometryWKT);
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
        } catch (ParseException e) {
            e.printStackTrace();
            CoreRealmServiceRuntimeException runtimeException = new CoreRealmServiceRuntimeException();
            runtimeException.setCauseMessage("Geometry WKT Parse error");
            throw runtimeException;
        }
    }
}
