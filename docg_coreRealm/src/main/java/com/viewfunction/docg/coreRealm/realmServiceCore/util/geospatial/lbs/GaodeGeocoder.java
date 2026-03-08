package com.viewfunction.docg.coreRealm.realmServiceCore.util.geospatial.lbs;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

/**
 * 使用高德 Web 服务 API 地理编码，返回的为 GCJ-02，在此转换为 WGS84 输出。
 * 需设置环境变量 GAODE_API_KEY 为高德 Key。
 * 文档：https://lbs.amap.com/api/webservice/guide/api/georegeo
 */
public class GaodeGeocoder {

    private static final String GEOCODE_URL = "https://restapi.amap.com/v3/geocode/geo";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final String apiKey;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    public GaodeGeocoder(String apiKey) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
    }

    /**
     * @param address 地址字符串
     * @return [经度 longitude, 纬度 latitude]，解析失败返回 null
     */
    public double[] geocode(String address) {
        if (address == null || address.isBlank() || apiKey.isEmpty()) return null;
        try {
            String encoded = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
            String cityEncoded = URLEncoder.encode("北京", StandardCharsets.UTF_8);
            String url = GEOCODE_URL + "?address=" + encoded + "&city=" + cityEncoded + "&output=json&key=" + apiKey;
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(REQUEST_TIMEOUT)
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = resp.body();
            if (resp.statusCode() != 200) return null;
            if (body == null || body.contains("\"status\":\"0\"") || !body.contains("\"geocodes\"")) return null;
            double[] gcj = parseLocation(body);
            if (gcj == null) return null;
            return CoordinateConvert.gcj02ToWgs84(gcj[0], gcj[1]);
        } catch (Exception e) {
            System.err.println("高德地理编码失败: " + address + " - " + e.getMessage());
            return null;
        }
    }

    private static final Pattern LOCATION_PATTERN = Pattern.compile("\"location\":\"([^\"]+)\"");

    private double[] parseLocation(String json) {
        if (json == null || json.isBlank()) return null;
        var m = LOCATION_PATTERN.matcher(json);
        if (!m.find()) return null;
        String loc = m.group(1);
        String[] parts = loc.split(",");
        if (parts.length != 2) return null;
        try {
            double lng = Double.parseDouble(parts[0].trim());
            double lat = Double.parseDouble(parts[1].trim());
            return new double[]{ lng, lat };
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
