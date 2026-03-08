package com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial.lbs;

/**
 * 坐标转换：高德/腾讯等使用的 GCJ-02 与 WGS84 互转。
 */
public final class CoordinateConvert {

    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;

    private CoordinateConvert() {}

    /** GCJ-02 转 WGS84，入参为 GCJ-02 的 [经度, 纬度]，返回 WGS84 [经度, 纬度] */
    public static double[] gcj02ToWgs84(double gcjLng, double gcjLat) {
        double[] fromWgs = wgs84ToGcj02(gcjLng, gcjLat);
        return new double[]{
                2 * gcjLng - fromWgs[0],
                2 * gcjLat - fromWgs[1]
        };
    }

    /** WGS84 转 GCJ-02，返回 [经度, 纬度] */
    public static double[] wgs84ToGcj02(double lng, double lat) {
        if (outOfChina(lng, lat)) return new double[]{ lng, lat };
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * Math.PI);
        dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(radLat) * Math.PI);
        return new double[]{ lng + dLng, lat + dLat };
    }

    private static boolean outOfChina(double lng, double lat) {
        return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLng(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 生成 OGC WKT 的 POINT：POINT(经度 纬度)
     */
    public static String toPointWkt(double longitude, double latitude) {
        return "POINT(" + longitude + " " + latitude + ")";
    }
}
