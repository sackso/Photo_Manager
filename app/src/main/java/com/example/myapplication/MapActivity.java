package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.MapImageAdapter;
import com.example.myapplication.model.ImageData;
import com.example.myapplication.utils.ImageUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, 
    MapImageAdapter.OnMapImageClickListener {
    
    private GoogleMap googleMap;
    private RecyclerView recyclerViewMapImages;
    private ProgressBar progressBar;
    private TextView textEmpty;
    private MapImageAdapter mapImageAdapter;
    private List<ImageData> gpsImageList;
    private List<Marker> markers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        initViews();
        setupToolbar();
        setupMapFragment();
        setupRecyclerView();
        loadGPSImages();
    }
    
    private void initViews() {
        recyclerViewMapImages = findViewById(R.id.recycler_view_map_images);
        progressBar = findViewById(R.id.progress_bar);
        textEmpty = findViewById(R.id.text_empty);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.map_title);
        }
    }
    
    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    
    private void setupRecyclerView() {
        gpsImageList = new ArrayList<>();
        markers = new ArrayList<>();
        mapImageAdapter = new MapImageAdapter(this, gpsImageList, this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, 
            LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMapImages.setLayoutManager(layoutManager);
        recyclerViewMapImages.setAdapter(mapImageAdapter);
    }
    
    private void loadGPSImages() {
        showLoading(true);
        
        new Thread(() -> {
            try {
                List<ImageData> allImages = ImageUtils.getAllImages(this);
                List<ImageData> gpsImages = ImageUtils.getImagesWithGPS(allImages);
                
                runOnUiThread(() -> {
                    gpsImageList.clear();
                    gpsImageList.addAll(gpsImages);
                    mapImageAdapter.notifyDataSetChanged();
                    
                    showLoading(false);
                    showEmptyState(gpsImages.isEmpty());
                    
                    // 지도에 마커 추가
                    if (googleMap != null) {
                        addMarkersToMap(gpsImages);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, getString(R.string.error_loading_image), 
                        Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        
        // 지도 설정
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        // 마커 클릭 리스너
        googleMap.setOnMarkerClickListener(marker -> {
            // 마커 클릭 시 해당 이미지 선택
            String imagePath = (String) marker.getTag();
            if (imagePath != null) {
                selectImageInList(imagePath);
            }
            return true;
        });
        
        // GPS 이미지가 로드되었으면 마커 추가
        if (!gpsImageList.isEmpty()) {
            addMarkersToMap(gpsImageList);
        }
    }
    
    private void addMarkersToMap(List<ImageData> images) {
        if (googleMap == null) return;
        
        // 기존 마커 제거
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        
        LatLng firstLocation = null;
        
        for (ImageData imageData : images) {
            if (imageData.hasGPS()) {
                LatLng location = new LatLng(
                    imageData.getGpsData().getLatitude(),
                    imageData.getGpsData().getLongitude()
                );
                
                if (firstLocation == null) {
                    firstLocation = location;
                }
                
                // 썸네일 생성
                Bitmap thumbnail = ImageUtils.createThumbnail(imageData.getImagePath(), 100);
                
                MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title(imageData.getImageName());
                
                if (thumbnail != null) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(thumbnail));
                }
                
                Marker marker = googleMap.addMarker(markerOptions);
                if (marker != null) {
                    marker.setTag(imageData.getImagePath());
                    markers.add(marker);
                }
            }
        }
        
        // 첫 번째 위치로 카메라 이동
        if (firstLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10));
        }
    }
    
    private void selectImageInList(String imagePath) {
        for (int i = 0; i < gpsImageList.size(); i++) {
            if (gpsImageList.get(i).getImagePath().equals(imagePath)) {
                mapImageAdapter.setSelectedPosition(i);
                recyclerViewMapImages.smoothScrollToPosition(i);
                break;
            }
        }
    }
    
    @Override
    public void onMapImageClick(ImageData imageData) {
        if (googleMap != null && imageData.hasGPS()) {
            LatLng location = new LatLng(
                imageData.getGpsData().getLatitude(),
                imageData.getGpsData().getLongitude()
            );
            
            // 해당 위치로 카메라 이동
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            
            // 해당 마커 찾아서 강조
            for (Marker marker : markers) {
                if (imageData.getImagePath().equals(marker.getTag())) {
                    marker.showInfoWindow();
                    break;
                }
            }
        }
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        recyclerViewMapImages.setVisibility(show ? android.view.View.GONE : android.view.View.VISIBLE);
    }
    
    private void showEmptyState(boolean show) {
        textEmpty.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 지도로 돌아올 때 GPS 이미지 목록 새로고침
        loadGPSImages();
    }
}
