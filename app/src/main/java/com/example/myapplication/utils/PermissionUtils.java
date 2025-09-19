package com.example.myapplication.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 권한 처리 유틸리티 클래스
 */
public class PermissionUtils {
    private static final String TAG = "PermissionUtils";
    
    // 권한 요청 코드
    public static final int PERMISSION_REQUEST_CODE = 1001;
    
    // 필요한 권한들
    public static final String[] REQUIRED_PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    };
    
    // Android 13+ (API 33) 이상에서 필요한 권한
    public static final String[] REQUIRED_PERMISSIONS_API_33 = {
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    };
    
    /**
     * 현재 API 레벨에 맞는 필요한 권한 목록 반환
     */
    public static String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return REQUIRED_PERMISSIONS_API_33;
        } else {
            return REQUIRED_PERMISSIONS;
        }
    }
    
    /**
     * 모든 필요한 권한이 허용되었는지 확인
     */
    public static boolean hasAllPermissions(Context context) {
        String[] permissions = getRequiredPermissions();
        
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 권한이 거부된 권한 목록 반환
     */
    public static List<String> getDeniedPermissions(Context context) {
        List<String> deniedPermissions = new ArrayList<>();
        String[] permissions = getRequiredPermissions();
        
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        
        return deniedPermissions;
    }
    
    /**
     * 권한 요청
     */
    public static void requestPermissions(Activity activity) {
        List<String> deniedPermissions = getDeniedPermissions(activity);
        
        if (!deniedPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                deniedPermissions.toArray(new String[0]),
                PERMISSION_REQUEST_CODE
            );
        }
    }
    
    /**
     * 특정 권한이 허용되었는지 확인
     */
    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) 
            == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * 저장소 권한이 허용되었는지 확인
     */
    public static boolean hasStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return hasPermission(context, Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            return hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    
    /**
     * 위치 권한이 허용되었는지 확인
     */
    public static boolean hasLocationPermission(Context context) {
        return hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
               hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }
    
    /**
     * 권한 요청 결과 처리
     */
    public static boolean handlePermissionResult(int requestCode, String[] permissions, 
                                               int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            
            Log.d(TAG, "권한 요청 결과: " + (allPermissionsGranted ? "모두 허용" : "일부 거부"));
            return allPermissionsGranted;
        }
        
        return false;
    }
    
    /**
     * 권한 설명 메시지 반환
     */
    public static String getPermissionMessage(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.READ_MEDIA_IMAGES:
                return "이미지를 불러오기 위해 저장소 접근 권한이 필요합니다.";
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "설명을 저장하기 위해 저장소 쓰기 권한이 필요합니다.";
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return "GPS 정보를 표시하기 위해 위치 권한이 필요합니다.";
            default:
                return "이 기능을 사용하기 위해 권한이 필요합니다.";
        }
    }
    
    /**
     * 권한이 영구적으로 거부되었는지 확인
     */
    public static boolean isPermissionPermanentlyDenied(Activity activity, String permission) {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) &&
               !hasPermission(activity, permission);
    }
    
    /**
     * 권한 설정 화면으로 이동할 권한 목록 반환
     */
    public static List<String> getPermanentlyDeniedPermissions(Activity activity) {
        List<String> permanentlyDeniedPermissions = new ArrayList<>();
        String[] permissions = getRequiredPermissions();
        
        for (String permission : permissions) {
            if (isPermissionPermanentlyDenied(activity, permission)) {
                permanentlyDeniedPermissions.add(permission);
            }
        }
        
        return permanentlyDeniedPermissions;
    }
}
