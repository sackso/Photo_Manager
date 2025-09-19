package com.example.myapplication.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DateUtils 테스트 클래스
 */
public class DateUtilsTest {
    
    @Test
    public void testFormatTimestamp() {
        // 유효한 타임스탬프
        long timestamp = 1705312200000L; // 2024-01-15 14:30:00
        String formatted = DateUtils.formatTimestamp(timestamp);
        assertTrue(formatted.contains("2024"));
        assertTrue(formatted.contains("01"));
        assertTrue(formatted.contains("15"));
        
        // 무효한 타임스탬프
        assertEquals("알 수 없음", DateUtils.formatTimestamp(0));
        assertEquals("알 수 없음", DateUtils.formatTimestamp(-1));
    }
    
    @Test
    public void testFormatDate() {
        // 유효한 타임스탬프
        long timestamp = 1705312200000L; // 2024-01-15 14:30:00
        String formatted = DateUtils.formatDate(timestamp);
        assertTrue(formatted.contains("2024-01-15"));
        
        // 무효한 타임스탬프
        assertEquals("알 수 없음", DateUtils.formatDate(0));
    }
    
    @Test
    public void testFormatTime() {
        // 유효한 타임스탬프
        long timestamp = 1705312200000L; // 2024-01-15 14:30:00
        String formatted = DateUtils.formatTime(timestamp);
        assertTrue(formatted.contains("14:30"));
        
        // 무효한 타임스탬프
        assertEquals("알 수 없음", DateUtils.formatTime(0));
    }
    
    @Test
    public void testGetRelativeTime() {
        long now = System.currentTimeMillis();
        
        // 방금 전
        assertEquals("방금 전", DateUtils.getRelativeTime(now - 1000));
        
        // 몇 분 전
        assertEquals("1분 전", DateUtils.getRelativeTime(now - 60 * 1000));
        assertEquals("5분 전", DateUtils.getRelativeTime(now - 5 * 60 * 1000));
        
        // 몇 시간 전
        assertEquals("1시간 전", DateUtils.getRelativeTime(now - 60 * 60 * 1000));
        assertEquals("2시간 전", DateUtils.getRelativeTime(now - 2 * 60 * 60 * 1000));
        
        // 몇 일 전
        assertEquals("1일 전", DateUtils.getRelativeTime(now - 24 * 60 * 60 * 1000));
        assertEquals("3일 전", DateUtils.getRelativeTime(now - 3 * 24 * 60 * 60 * 1000));
        
        // 무효한 타임스탬프
        assertEquals("알 수 없음", DateUtils.getRelativeTime(0));
        assertEquals("미래", DateUtils.getRelativeTime(now + 1000));
    }
    
    @Test
    public void testFormatForFilename() {
        // 유효한 타임스탬프
        long timestamp = 1705312200000L; // 2024-01-15 14:30:00
        String formatted = DateUtils.formatForFilename(timestamp);
        assertTrue(formatted.contains("20240115"));
        assertTrue(formatted.contains("143000"));
        
        // 무효한 타임스탬프
        assertEquals("unknown", DateUtils.formatForFilename(0));
    }
}
