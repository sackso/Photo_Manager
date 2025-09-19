package com.example.myapplication.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.myapplication.model.GPSData;
import com.example.myapplication.model.ImageData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 이미지 관련 유틸리티 클래스
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";

    /**
     * 갤러리에서 모든 이미지 가져오기
     */
    public static List<ImageData> getAllImages(Context context) {
        List<ImageData> imageList = new ArrayList<>();
        
        String[] projection = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE
        };

        String selection = MediaStore.Images.Media.DATA + " IS NOT NULL";
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder)) {

            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
                int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);

                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(dataColumn);
                    if (imagePath != null && new java.io.File(imagePath).exists()) {
                        ImageData imageData = new ImageData(imagePath);
                        imageData.setImageName(cursor.getString(nameColumn));
                        imageData.setFileSize(cursor.getLong(sizeColumn));
                        imageData.setDateTaken(cursor.getLong(dateColumn));
                        imageData.setWidth(cursor.getInt(widthColumn));
                        imageData.setHeight(cursor.getInt(heightColumn));
                        imageData.setMimeType(cursor.getString(mimeColumn));

                        // GPS 정보 추출
                        GPSData gpsData = GPSUtils.extractGPSFromExif(imagePath);
                        imageData.setGpsData(gpsData);

                        // 설명 파일 로드
                        String description = FileUtils.readTextFile(imageData.getDescriptionFilePath());
                        imageData.setDescription(description);

                        imageList.add(imageData);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "이미지 목록 가져오기 오류", e);
        }

        return imageList;
    }

    /**
     * GPS가 있는 이미지만 필터링
     */
    public static List<ImageData> getImagesWithGPS(List<ImageData> allImages) {
        List<ImageData> gpsImages = new ArrayList<>();
        for (ImageData imageData : allImages) {
            if (imageData.hasGPS()) {
                gpsImages.add(imageData);
            }
        }
        return gpsImages;
    }

    /**
     * 이미지 썸네일 생성
     */
    public static Bitmap createThumbnail(String imagePath, int maxSize) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            // 이미지 크기 계산
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;

            // 샘플 크기 계산
            options.inSampleSize = calculateInSampleSize(imageWidth, imageHeight, maxSize, maxSize);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            if (bitmap != null) {
                // 회전 보정
                bitmap = rotateImageIfRequired(bitmap, imagePath);
            }

            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "썸네일 생성 오류: " + imagePath, e);
            return null;
        }
    }

    /**
     * 이미지 회전 보정
     */
    private static Bitmap rotateImageIfRequired(Bitmap bitmap, String imagePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            );

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            Log.e(TAG, "이미지 회전 보정 오류: " + imagePath, e);
            return bitmap;
        }
    }

    /**
     * 비트맵 회전
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 샘플 크기 계산
     */
    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 이미지 확대/축소를 위한 비트맵 로드
     */
    public static Bitmap loadImageForDisplay(String imagePath, int maxWidth, int maxHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;

            options.inSampleSize = calculateInSampleSize(imageWidth, imageHeight, maxWidth, maxHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            if (bitmap != null) {
                bitmap = rotateImageIfRequired(bitmap, imagePath);
            }

            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "이미지 로드 오류: " + imagePath, e);
            return null;
        }
    }

    /**
     * 이미지 파일이 유효한지 확인
     */
    public static boolean isValidImageFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        String extension = FileUtils.getFileExtension(filePath).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif") || 
               extension.equals("bmp") || extension.equals("webp");
    }
}
