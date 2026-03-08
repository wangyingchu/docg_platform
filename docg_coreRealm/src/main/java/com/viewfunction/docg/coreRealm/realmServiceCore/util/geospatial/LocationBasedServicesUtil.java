package com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial.lbs.CoordinateConvert;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial.lbs.GaodeGeocoder;

public class LocationBasedServicesUtil {

    public enum GIS_CoordinateSystem {WGS84,GCJ02}
    private static GaodeGeocoder geocoder;

    public static String getWKTContentByLocationAddress(String locationAddress, GeospatialScaleFeatureSupportable.WKTGeometryType resultWKTGeometryType,GIS_CoordinateSystem resultGIS_CoordinateSystem){
        if(resultGIS_CoordinateSystem == null){
            return null;
        }else{
            if(GeospatialScaleFeatureSupportable.WKTGeometryType.POINT.equals(resultWKTGeometryType)){
                String apiKey = "f7589f9bcf34789ab32d7263855d1866";
                if(geocoder == null){
                    geocoder = new GaodeGeocoder(apiKey);
                }
                double[] resPoint = geocoder.geocode(locationAddress);
                if(resPoint != null){
                    if(resultGIS_CoordinateSystem.equals(GIS_CoordinateSystem.WGS84)){
                        double[] wgs84 = CoordinateConvert.gcj02ToWgs84(resPoint[0],resPoint[1]);
                        return CoordinateConvert.toPointWkt(wgs84[0],wgs84[1]);
                    }
                    if(resultGIS_CoordinateSystem.equals(GIS_CoordinateSystem.GCJ02)){
                        return CoordinateConvert.toPointWkt(resPoint[0],resPoint[1]);
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args){
        //String wkt = getWKTContentByLocationAddress("北京市海淀区中关村大街27号1101-08室",GeospatialScaleFeatureSupportable.WKTGeometryType.POINT,GIS_CoordinateSystem.WGS84);
        System.out.println(getWKTContentByLocationAddress("昌平区南大街东巷（机关幼儿园西侧）",GeospatialScaleFeatureSupportable.WKTGeometryType.POINT,GIS_CoordinateSystem.WGS84));
        System.out.println(getWKTContentByLocationAddress("北京市海淀区中关村大街27号1101-08室",GeospatialScaleFeatureSupportable.WKTGeometryType.POINT,GIS_CoordinateSystem.GCJ02));
    }
}
