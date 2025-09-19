package com.example.myapplication.utils;

import android.media.ExifInterface;
import android.util.Log;

import com.example.myapplication.model.GPSData;

import java.io.IOException;

/**
 * GPS 관련 유틸리티 클래스
 */
public class GPSUtils {
    private static final String TAG = "GPSUtils";

    /**
     * EXIF 데이터에서 GPS 정보 추출
     */
    public static GPSData extractGPSFromExif(String imagePath) {
        GPSData gpsData = new GPSData();
        
        if (imagePath == null || imagePath.isEmpty()) {
            return gpsData;
        }

        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            
            // GPS 태그 존재 여부 확인
            if (!exifInterface.hasAttribute(ExifInterface.TAG_GPS_LATITUDE) ||
                !exifInterface.hasAttribute(ExifInterface.TAG_GPS_LONGITUDE)) {
                return gpsData;
            }

            // 위도 추출
            String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            double latitude = convertDMSToDD(latValue, latRef);

            // 경도 추출
            String lonRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String lonValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            double longitude = convertDMSToDD(lonValue, lonRef);

            // 고도 추출 (선택사항)
            String altValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
            String altRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
            float altitude = 0;
            if (altValue != null && altRef != null) {
                altitude = Float.parseFloat(altValue);
                if ("1".equals(altRef)) {
                    altitude = -altitude; // 해수면 아래
                }
            }

            gpsData = new GPSData(latitude, longitude, altitude);
            gpsData.setHasGPS(true);

            Log.d(TAG, "GPS 정보 추출 성공: " + latitude + ", " + longitude);

        } catch (IOException e) {
            Log.e(TAG, "EXIF 데이터 읽기 오류: " + imagePath, e);
        } catch (Exception e) {
            Log.e(TAG, "GPS 데이터 파싱 오류: " + imagePath, e);
        }

        return gpsData;
    }

    /**
     * DMS (도분초) 형식을 DD (십진도) 형식으로 변환
     */
    private static double convertDMSToDD(String dms, String ref) {
        if (dms == null || ref == null) {
            return 0.0;
        }

        try {
            String[] parts = dms.split(",");
            if (parts.length != 3) {
                return 0.0;
            }

            // 도, 분, 초 추출
            double degrees = Double.parseDouble(parts[0].split("/")[0]) / Double.parseDouble(parts[0].split("/")[1]);
            double minutes = Double.parseDouble(parts[1].split("/")[0]) / Double.parseDouble(parts[1].split("/")[1]);
            double seconds = Double.parseDouble(parts[2].split("/")[0]) / Double.parseDouble(parts[2].split("/")[1]);

            double dd = degrees + (minutes / 60.0) + (seconds / 3600.0);

            // 남위 또는 서경인 경우 음수로 변환
            if ("S".equals(ref) || "W".equals(ref)) {
                dd = -dd;
            }

            return dd;

        } catch (Exception e) {
            Log.e(TAG, "DMS to DD 변환 오류: " + dms, e);
            return 0.0;
        }
    }

    /**
     * 두 GPS 좌표 간의 거리 계산 (미터 단위)
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // 미터로 변환

        return distance;
    }

    /**
     * GPS 좌표가 유효한 범위 내에 있는지 확인
     */
    public static boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    /**
     * 구글 맵 URL 생성
     */
    public static String createGoogleMapsUrl(double latitude, double longitude) {
        if (!isValidCoordinate(latitude, longitude)) {
            return null;
        }
        return String.format("https://www.google.com/maps?q=%.6f,%.6f", latitude, longitude);
    }

    /**
     * 구글 맵 URL 생성 (줌 레벨 포함)
     */
    public static String createGoogleMapsUrl(double latitude, double longitude, int zoom) {
        if (!isValidCoordinate(latitude, longitude)) {
            return null;
        }
        return String.format("https://www.google.com/maps?q=%.6f,%.6f&z=%d", latitude, longitude, zoom);
    }
}
