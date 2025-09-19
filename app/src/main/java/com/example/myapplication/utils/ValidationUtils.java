package com.example.myapplication.utils;

import android.util.Log;

import java.io.File;
import java.util.regex.Pattern;

/**
 * 유효성 검사 유틸리티 클래스
 */
public class ValidationUtils {
    private static final String TAG = "ValidationUtils";
    
    // 이미지 파일 확장자 패턴
    private static final Pattern IMAGE_EXTENSION_PATTERN = 
        Pattern.compile("(?i)\\.(jpg|jpeg|png|gif|bmp|webp)$");
    
    // 텍스트 파일 확장자 패턴
    private static final Pattern TEXT_EXTENSION_PATTERN = 
        Pattern.compile("(?i)\\.txt$");
    
    /**
     * 이미지 파일인지 확인
     */
    public static boolean isValidImageFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        
        return IMAGE_EXTENSION_PATTERN.matcher(filePath).find();
    }
    
    /**
     * 텍스트 파일인지 확인
     */
    public static boolean isValidTextFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        
        return TEXT_EXTENSION_PATTERN.matcher(filePath).find();
    }
    
    /**
     * 파일 경로가 유효한지 확인
     */
    public static boolean isValidFilePath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try {
            File file = new File(filePath);
            return file.getAbsolutePath().equals(filePath);
        } catch (Exception e) {
            Log.e(TAG, "파일 경로 유효성 검사 오류: " + filePath, e);
            return false;
        }
    }
    
    /**
     * 파일이 읽기 가능한지 확인
     */
    public static boolean isFileReadable(String filePath) {
        if (!isValidFilePath(filePath)) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }
    
    /**
     * 파일이 쓰기 가능한지 확인
     */
    public static boolean isFileWritable(String filePath) {
        if (!isValidFilePath(filePath)) {
            return false;
        }
        
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        
        if (parentDir != null && !parentDir.exists()) {
            return parentDir.mkdirs() && parentDir.canWrite();
        }
        
        return file.canWrite() || (parentDir != null && parentDir.canWrite());
    }
    
    /**
     * 설명 텍스트가 유효한지 확인
     */
    public static boolean isValidDescription(String description) {
        if (description == null) {
            return true; // null은 유효 (빈 설명)
        }
        
        // 3줄 이내인지 확인
        String[] lines = description.split("\n");
        if (lines.length > 3) {
            return false;
        }
        
        // 각 줄의 길이 확인 (예: 100자 이내)
        for (String line : lines) {
            if (line.length() > 100) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * GPS 좌표가 유효한지 확인
     */
    public static boolean isValidGPSCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    /**
     * 파일 크기가 유효한지 확인
     */
    public static boolean isValidFileSize(long fileSize) {
        // 최대 100MB
        final long MAX_FILE_SIZE = 100 * 1024 * 1024;
        return fileSize > 0 && fileSize <= MAX_FILE_SIZE;
    }
    
    /**
     * 이미지 해상도가 유효한지 확인
     */
    public static boolean isValidImageResolution(int width, int height) {
        // 최소 1x1, 최대 10000x10000
        return width > 0 && height > 0 && 
               width <= 10000 && height <= 10000;
    }
    
    /**
     * 파일명이 유효한지 확인
     */
    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        // 파일명에 사용할 수 없는 문자들
        String invalidChars = "\\/:*?\"<>|";
        for (char c : invalidChars.toCharArray()) {
            if (fileName.indexOf(c) != -1) {
                return false;
            }
        }
        
        // 파일명 길이 확인 (255자 이내)
        return fileName.length() <= 255;
    }
    
    /**
     * 설명 텍스트를 정리 (앞뒤 공백 제거, 연속 공백 정리)
     */
    public static String sanitizeDescription(String description) {
        if (description == null) {
            return "";
        }
        
        // 앞뒤 공백 제거
        String sanitized = description.trim();
        
        // 연속된 공백을 하나로 정리
        sanitized = sanitized.replaceAll("\\s+", " ");
        
        // 연속된 줄바꿈을 하나로 정리
        sanitized = sanitized.replaceAll("\n+", "\n");
        
        return sanitized;
    }
}
