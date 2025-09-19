package com.example.myapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 날짜 관련 유틸리티 클래스
 */
public class DateUtils {
    
    /**
     * 타임스탬프를 읽기 쉬운 날짜 형식으로 변환
     */
    public static String formatTimestamp(long timestamp) {
        if (timestamp <= 0) {
            return "알 수 없음";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 타임스탬프를 날짜만 표시하는 형식으로 변환
     */
    public static String formatDate(long timestamp) {
        if (timestamp <= 0) {
            return "알 수 없음";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 타임스탬프를 시간만 표시하는 형식으로 변환
     */
    public static String formatTime(long timestamp) {
        if (timestamp <= 0) {
            return "알 수 없음";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 상대적 시간 표시 (예: 2시간 전, 3일 전)
     */
    public static String getRelativeTime(long timestamp) {
        if (timestamp <= 0) {
            return "알 수 없음";
        }
        
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        if (diff < 0) {
            return "미래";
        }
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;
        
        if (seconds < 60) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 7) {
            return days + "일 전";
        } else if (weeks < 4) {
            return weeks + "주 전";
        } else if (months < 12) {
            return months + "개월 전";
        } else {
            return years + "년 전";
        }
    }
    
    /**
     * 파일명에 사용할 수 있는 날짜 형식
     */
    public static String formatForFilename(long timestamp) {
        if (timestamp <= 0) {
            return "unknown";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
