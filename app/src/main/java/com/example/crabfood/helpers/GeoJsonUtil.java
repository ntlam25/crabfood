package com.example.crabfood.helpers;

import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

public class GeoJsonUtil {

    public static List<Point> decodePolyline(String encoded, int precision) {
        List<Point> poly = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;

            // Decode latitude
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            // Decode longitude
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latD = lat / Math.pow(10, precision);
            double lngD = lng / Math.pow(10, precision);

            Point point = Point.fromLngLat(lngD, latD);
            poly.add(point);
        }

        return poly;
    }

    public static String encodePolyline(List<Point> points, int precision) {
        StringBuilder result = new StringBuilder();
        int prevLat = 0;
        int prevLng = 0;

        for (Point point : points) {
            int lat = (int) Math.round(point.latitude() * Math.pow(10, precision));
            int lng = (int) Math.round(point.longitude() * Math.pow(10, precision));

            // Encode latitude
            encodeValue(result, lat - prevLat);
            prevLat = lat;

            // Encode longitude
            encodeValue(result, lng - prevLng);
            prevLng = lng;
        }

        return result.toString();
    }

    private static void encodeValue(StringBuilder result, int value) {
        value = value < 0 ? ~(value << 1) : (value << 1);

        while (value >= 0x20) {
            result.append((char) ((0x20 | (value & 0x1f)) + 63));
            value >>= 5;
        }

        result.append((char) (value + 63));
    }
}