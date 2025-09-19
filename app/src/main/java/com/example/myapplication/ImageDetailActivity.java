package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.model.GPSData;
import com.example.myapplication.model.ImageData;
import com.example.myapplication.utils.FileUtils;
import com.example.myapplication.utils.GPSUtils;
import com.example.myapplication.utils.ImageUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {
    
    private ImageView imageViewDetail;
    private ImageView iconGPS;
    private TextView textFileSize;
    private TextView textResolution;
    private TextView textDateTaken;
    private TextView textGPSCoordinates;
    private LinearLayout layoutGPSInfo;
    private EditText editTextDescription;
    private Button buttonSaveDescription;
    
    private ImageData imageData;
    private String imagePath;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        
        initViews();
        setupToolbar();
        loadImageData();
        setupClickListeners();
    }
    
    private void initViews() {
        imageViewDetail = findViewById(R.id.image_view_detail);
        iconGPS = findViewById(R.id.icon_gps);
        textFileSize = findViewById(R.id.text_file_size);
        textResolution = findViewById(R.id.text_resolution);
        textDateTaken = findViewById(R.id.text_date_taken);
        textGPSCoordinates = findViewById(R.id.text_gps_coordinates);
        layoutGPSInfo = findViewById(R.id.layout_gps_info);
        editTextDescription = findViewById(R.id.edit_text_description);
        buttonSaveDescription = findViewById(R.id.button_save_description);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.image_detail_title);
        }
    }
    
    private void loadImageData() {
        Intent intent = getIntent();
        imagePath = intent.getStringExtra("image_path");
        
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_loading_image), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 이미지 데이터 생성
        imageData = new ImageData(imagePath);
        
        // 이미지 로드
        loadImage();
        
        // 이미지 정보 표시
        displayImageInfo();
        
        // GPS 정보 표시
        displayGPSInfo();
        
        // 설명 로드
        loadDescription();
    }
    
    private void loadImage() {
        new Thread(() -> {
            try {
                // 화면 크기에 맞는 이미지 로드
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                
                Bitmap bitmap = ImageUtils.loadImageForDisplay(imagePath, screenWidth, screenHeight);
                
                if (bitmap != null) {
                    runOnUiThread(() -> {
                        imageViewDetail.setImageBitmap(bitmap);
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.error_loading_image), 
                        Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private void displayImageInfo() {
        File file = new File(imagePath);
        if (file.exists()) {
            imageData.setFileSize(file.length());
        }
        
        textFileSize.setText(imageData.getFormattedFileSize());
        textResolution.setText(imageData.getResolution());
        
        // 촬영 날짜 포맷팅
        if (imageData.getDateTaken() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateString = sdf.format(new Date(imageData.getDateTaken()));
            textDateTaken.setText(dateString);
        } else {
            textDateTaken.setText("알 수 없음");
        }
    }
    
    private void displayGPSInfo() {
        // GPS 정보 추출
        GPSData gpsData = GPSUtils.extractGPSFromExif(imagePath);
        imageData.setGpsData(gpsData);
        
        if (gpsData.hasGPS() && gpsData.isValid()) {
            iconGPS.setVisibility(android.view.View.VISIBLE);
            layoutGPSInfo.setVisibility(android.view.View.VISIBLE);
            textGPSCoordinates.setText(gpsData.toString());
        } else {
            iconGPS.setVisibility(android.view.View.GONE);
            layoutGPSInfo.setVisibility(android.view.View.GONE);
        }
    }
    
    private void loadDescription() {
        new Thread(() -> {
            try {
                String description = FileUtils.readTextFile(imageData.getDescriptionFilePath());
                
                runOnUiThread(() -> {
                    editTextDescription.setText(description);
                });
            } catch (Exception e) {
                // 설명 파일이 없거나 읽기 실패한 경우 무시
            }
        }).start();
    }
    
    private void setupClickListeners() {
        buttonSaveDescription.setOnClickListener(v -> saveDescription());
        
        // GPS 좌표 클릭 시 구글 맵 열기
        textGPSCoordinates.setOnClickListener(v -> {
            if (imageData.hasGPS()) {
                String mapsUrl = imageData.getGpsData().getGoogleMapsUrl();
                if (mapsUrl != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
                    startActivity(intent);
                }
            }
        });
    }
    
    private void saveDescription() {
        String description = editTextDescription.getText().toString().trim();
        
        new Thread(() -> {
            try {
                boolean success = FileUtils.saveTextFile(imageData.getDescriptionFilePath(), description);
                
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(this, getString(R.string.description_saved), 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.description_save_failed), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.error_saving_file), 
                        Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
