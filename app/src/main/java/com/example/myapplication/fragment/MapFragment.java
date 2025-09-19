package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MapActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MapImageAdapter;
import com.example.myapplication.model.ImageData;
import com.example.myapplication.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements MapImageAdapter.OnMapImageClickListener {
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textEmpty;
    private MapImageAdapter mapImageAdapter;
    private List<ImageData> gpsImageList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadGPSImages();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_map_images);
        progressBar = view.findViewById(R.id.progress_bar);
        textEmpty = view.findViewById(R.id.text_empty);
    }
    
    private void setupRecyclerView() {
        gpsImageList = new ArrayList<>();
        mapImageAdapter = new MapImageAdapter(getContext(), gpsImageList, this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), 
            LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mapImageAdapter);
    }
    
    private void loadGPSImages() {
        showLoading(true);
        
        new Thread(() -> {
            try {
                List<ImageData> allImages = ImageUtils.getAllImages(getContext());
                List<ImageData> gpsImages = ImageUtils.getImagesWithGPS(allImages);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        gpsImageList.clear();
                        gpsImageList.addAll(gpsImages);
                        mapImageAdapter.notifyDataSetChanged();
                        
                        showLoading(false);
                        showEmptyState(gpsImages.isEmpty());
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(getContext(), 
                            getString(R.string.error_loading_image), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showEmptyState(boolean show) {
        textEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public void onMapImageClick(ImageData imageData) {
        Intent intent = new Intent(getContext(), MapActivity.class);
        intent.putExtra("image_path", imageData.getImagePath());
        startActivity(intent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 지도로 돌아올 때 GPS 이미지 목록 새로고침
        loadGPSImages();
    }
}
