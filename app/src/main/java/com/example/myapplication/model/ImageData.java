package com.example.myapplication.model;

import java.io.File;

/**
 * 이미지 정보를 저장하는 데이터 클래스
 */
public class ImageData {
    private String imagePath;
    private String imageName;
    private String description;
    private GPSData gpsData;
    private long dateTaken;
    private long fileSize;
    private int width;
    private int height;
    private String mimeType;

    public ImageData() {
        this.gpsData = new GPSData();
    }

    public ImageData(String imagePath) {
        this.imagePath = imagePath;
        this.imageName = new File(imagePath).getName();
        this.gpsData = new GPSData();
    }

    // Getter and Setter methods
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.imageName = new File(imagePath).getName();
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GPSData getGpsData() {
        return gpsData;
    }

    public void setGpsData(GPSData gpsData) {
        this.gpsData = gpsData;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * 이미지가 GPS 정보를 가지고 있는지 확인
     */
    public boolean hasGPS() {
        return gpsData != null && gpsData.hasGPS();
    }

    /**
     * 설명 텍스트 파일 경로 생성
     */
    public String getDescriptionFilePath() {
        if (imagePath == null) {
            return null;
        }
        String basePath = imagePath.substring(0, imagePath.lastIndexOf('.'));
        return basePath + ".txt";
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형태로 변환
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    /**
     * 이미지 해상도 문자열 반환
     */
    public String getResolution() {
        return width + " x " + height;
    }

    @Override
    public String toString() {
        return "ImageData{" +
                "imageName='" + imageName + '\'' +
                ", hasGPS=" + hasGPS() +
                ", fileSize=" + getFormattedFileSize() +
                '}';
    }
}
