package com.example.myapplication.model;

/**
 * GPS 정보를 저장하는 데이터 클래스
 */
public class GPSData {
    private double latitude;
    private double longitude;
    private float altitude;
    private String locationName;
    private boolean hasGPS;

    public GPSData() {
        this.hasGPS = false;
    }

    public GPSData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.hasGPS = true;
    }

    public GPSData(double latitude, double longitude, float altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.hasGPS = true;
    }

    // Getter and Setter methods
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public boolean hasGPS() {
        return hasGPS;
    }

    public void setHasGPS(boolean hasGPS) {
        this.hasGPS = hasGPS;
    }

    /**
     * GPS 좌표가 유효한지 확인
     */
    public boolean isValid() {
        return hasGPS && latitude != 0.0 && longitude != 0.0;
    }

    /**
     * 구글 맵 URL 생성
     */
    public String getGoogleMapsUrl() {
        if (!isValid()) {
            return null;
        }
        return String.format("https://www.google.com/maps?q=%.6f,%.6f", latitude, longitude);
    }

    @Override
    public String toString() {
        if (!hasGPS) {
            return "GPS 정보 없음";
        }
        return String.format("위도: %.6f, 경도: %.6f", latitude, longitude);
    }
}
