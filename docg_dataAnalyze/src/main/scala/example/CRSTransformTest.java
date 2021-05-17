package example;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTWriter2;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class CRSTransformTest {

    public static void main(String[] args) throws FactoryException, TransformException, ParseException {
        String wktStr = "POINT(29.563022 106.713015)";
        String result = crsTransform(wktStr,"epsg:4326","epsg:4545");
        System.out.println(result);
    }

    public static String crsTransform(String wktStr, String sourceCrs, String targetCrs) throws ParseException, FactoryException, TransformException {
        Geometry sourceGeometry = createGeometryFromWktStr(wktStr);
        MathTransform mathTransform = createCrsTransform(sourceCrs, targetCrs);
        Geometry targetGeometry = JTS.transform(sourceGeometry, mathTransform);
        WKTWriter2 wktWriter2 = new WKTWriter2();
        return wktWriter2.write(targetGeometry);
    }

    public static Geometry createGeometryFromWktStr(String wktStr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        Geometry geometry = reader.read(wktStr);
        return geometry;
    }

    public static MathTransform createCrsTransform(String sourceCrs, String targetCrs) throws ParseException, FactoryException {
        CoordinateReferenceSystem sourceCRS = CRS.decode(sourceCrs.trim().toUpperCase());
        CoordinateReferenceSystem targetCRS = CRS.decode(targetCrs.trim().toUpperCase());

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

        return transform;
    }
}
