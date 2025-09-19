package com.example.myapplication.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ValidationUtils 테스트 클래스
 */
public class ValidationUtilsTest {
    
    @Test
    public void testIsValidImageFile() {
        // 유효한 이미지 파일
        assertTrue(ValidationUtils.isValidImageFile("test.jpg"));
        assertTrue(ValidationUtils.isValidImageFile("test.jpeg"));
        assertTrue(ValidationUtils.isValidImageFile("test.png"));
        assertTrue(ValidationUtils.isValidImageFile("test.gif"));
        assertTrue(ValidationUtils.isValidImageFile("test.bmp"));
        assertTrue(ValidationUtils.isValidImageFile("test.webp"));
        
        // 대소문자 구분 없음
        assertTrue(ValidationUtils.isValidImageFile("test.JPG"));
        assertTrue(ValidationUtils.isValidImageFile("test.PNG"));
        
        // 유효하지 않은 파일
        assertFalse(ValidationUtils.isValidImageFile("test.txt"));
        assertFalse(ValidationUtils.isValidImageFile("test.doc"));
        assertFalse(ValidationUtils.isValidImageFile(null));
        assertFalse(ValidationUtils.isValidImageFile(""));
    }
    
    @Test
    public void testIsValidDescription() {
        // 유효한 설명
        assertTrue(ValidationUtils.isValidDescription("간단한 설명"));
        assertTrue(ValidationUtils.isValidDescription("첫 번째 줄\n두 번째 줄"));
        assertTrue(ValidationUtils.isValidDescription("첫 번째\n두 번째\n세 번째"));
        assertTrue(ValidationUtils.isValidDescription(null));
        assertTrue(ValidationUtils.isValidDescription(""));
        
        // 유효하지 않은 설명 (4줄)
        assertFalse(ValidationUtils.isValidDescription("첫 번째\n두 번째\n세 번째\n네 번째"));
        
        // 유효하지 않은 설명 (긴 줄)
        String longLine = "a".repeat(101);
        assertFalse(ValidationUtils.isValidDescription(longLine));
    }
    
    @Test
    public void testIsValidGPSCoordinate() {
        // 유효한 GPS 좌표
        assertTrue(ValidationUtils.isValidGPSCoordinate(37.5665, 126.9780));
        assertTrue(ValidationUtils.isValidGPSCoordinate(-90, -180));
        assertTrue(ValidationUtils.isValidGPSCoordinate(90, 180));
        assertTrue(ValidationUtils.isValidGPSCoordinate(0, 0));
        
        // 유효하지 않은 GPS 좌표
        assertFalse(ValidationUtils.isValidGPSCoordinate(91, 0));
        assertFalse(ValidationUtils.isValidGPSCoordinate(-91, 0));
        assertFalse(ValidationUtils.isValidGPSCoordinate(0, 181));
        assertFalse(ValidationUtils.isValidGPSCoordinate(0, -181));
    }
    
    @Test
    public void testIsValidFileSize() {
        // 유효한 파일 크기
        assertTrue(ValidationUtils.isValidFileSize(1024));
        assertTrue(ValidationUtils.isValidFileSize(100 * 1024 * 1024)); // 100MB
        
        // 유효하지 않은 파일 크기
        assertFalse(ValidationUtils.isValidFileSize(0));
        assertFalse(ValidationUtils.isValidFileSize(-1));
        assertFalse(ValidationUtils.isValidFileSize(101 * 1024 * 1024)); // 101MB
    }
    
    @Test
    public void testIsValidImageResolution() {
        // 유효한 해상도
        assertTrue(ValidationUtils.isValidImageResolution(1920, 1080));
        assertTrue(ValidationUtils.isValidImageResolution(1, 1));
        assertTrue(ValidationUtils.isValidImageResolution(10000, 10000));
        
        // 유효하지 않은 해상도
        assertFalse(ValidationUtils.isValidImageResolution(0, 1080));
        assertFalse(ValidationUtils.isValidImageResolution(1920, 0));
        assertFalse(ValidationUtils.isValidImageResolution(10001, 1080));
        assertFalse(ValidationUtils.isValidImageResolution(1920, 10001));
    }
    
    @Test
    public void testIsValidFileName() {
        // 유효한 파일명
        assertTrue(ValidationUtils.isValidFileName("test.jpg"));
        assertTrue(ValidationUtils.isValidFileName("my_image_2024.png"));
        assertTrue(ValidationUtils.isValidFileName("file-name.txt"));
        
        // 유효하지 않은 파일명
        assertFalse(ValidationUtils.isValidFileName("test/file.jpg"));
        assertFalse(ValidationUtils.isValidFileName("test:file.jpg"));
        assertFalse(ValidationUtils.isValidFileName("test*file.jpg"));
        assertFalse(ValidationUtils.isValidFileName("test?file.jpg"));
        assertFalse(ValidationUtils.isValidFileName("test<file.jpg"));
        assertFalse(ValidationUtils.isValidFileName("test>file.jpg"));
        assertFalse(ValidationUtils.isValidFileName("test|file.jpg"));
        assertFalse(ValidationUtils.isValidFileName(null));
        assertFalse(ValidationUtils.isValidFileName(""));
    }
    
    @Test
    public void testSanitizeDescription() {
        // 앞뒤 공백 제거
        assertEquals("test", ValidationUtils.sanitizeDescription("  test  "));
        assertEquals("test", ValidationUtils.sanitizeDescription("\ttest\t"));
        
        // 연속 공백 정리
        assertEquals("test message", ValidationUtils.sanitizeDescription("test   message"));
        assertEquals("test\nmessage", ValidationUtils.sanitizeDescription("test\n\nmessage"));
        
        // null 처리
        assertEquals("", ValidationUtils.sanitizeDescription(null));
        
        // 빈 문자열
        assertEquals("", ValidationUtils.sanitizeDescription(""));
        assertEquals("", ValidationUtils.sanitizeDescription("   "));
    }
}
