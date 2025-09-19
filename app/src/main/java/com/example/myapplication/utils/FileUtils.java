package com.example.myapplication.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * 파일 관련 유틸리티 클래스
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 텍스트 파일 읽기
     */
    public static String readTextFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            return "";
        }

        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }

            // 마지막 개행 문자 제거
            if (content.length() > 0) {
                content.setLength(content.length() - 1);
            }

        } catch (IOException e) {
            Log.e(TAG, "파일 읽기 오류: " + filePath, e);
            return "";
        }

        return content.toString();
    }

    /**
     * 텍스트 파일 저장
     */
    public static boolean saveTextFile(String filePath, String content) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        try {
            File file = new File(filePath);
            
            // 디렉토리가 존재하지 않으면 생성
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

                osw.write(content);
                osw.flush();
                return true;

            }
        } catch (IOException e) {
            Log.e(TAG, "파일 저장 오류: " + filePath, e);
            return false;
        }
    }

    /**
     * 파일이 존재하는지 확인
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        return new File(filePath).exists();
    }

    /**
     * 파일 삭제
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * 파일 크기 반환
     */
    public static long getFileSize(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        
        File file = new File(filePath);
        return file.exists() ? file.length() : 0;
    }

    /**
     * 외부 저장소 사용 가능 여부 확인
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 외부 저장소 읽기 가능 여부 확인
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
               Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * 파일 확장자 추출
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 파일명에서 확장자 제거
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        
        String fileName = new File(filePath).getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }
}
