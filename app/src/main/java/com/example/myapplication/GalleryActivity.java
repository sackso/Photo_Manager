package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ImageAdapter;
import com.example.myapplication.model.ImageData;
import com.example.myapplication.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ImageAdapter.OnImageClickListener {
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textEmpty;
    private ImageAdapter imageAdapter;
    private List<ImageData> imageList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        
        initViews();
        setupToolbar();
        setupRecyclerView();
        loadImages();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_images);
        progressBar = findViewById(R.id.progress_bar);
        textEmpty = findViewById(R.id.text_empty);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.gallery_title);
        }
    }
    
    private void setupRecyclerView() {
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList, this);
        
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imageAdapter);
    }
    
    private void loadImages() {
        showLoading(true);
        
        new Thread(() -> {
            try {
                List<ImageData> images = ImageUtils.getAllImages(this);
                
                runOnUiThread(() -> {
                    imageList.clear();
                    imageList.addAll(images);
                    imageAdapter.notifyDataSetChanged();
                    
                    showLoading(false);
                    showEmptyState(images.isEmpty());
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
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
        recyclerView.setVisibility(show ? android.view.View.GONE : android.view.View.VISIBLE);
    }
    
    private void showEmptyState(boolean show) {
        textEmpty.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
    }
    
    @Override
    public void onImageClick(ImageData imageData) {
        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra("image_path", imageData.getImagePath());
        startActivity(intent);
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
        // 갤러리로 돌아올 때 이미지 목록 새로고침
        loadImages();
    }
}
