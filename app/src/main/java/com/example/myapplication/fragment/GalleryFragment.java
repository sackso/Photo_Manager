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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.GalleryActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ImageAdapter;
import com.example.myapplication.model.ImageData;
import com.example.myapplication.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements ImageAdapter.OnImageClickListener {
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textEmpty;
    private ImageAdapter imageAdapter;
    private List<ImageData> imageList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadImages();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_images);
        progressBar = view.findViewById(R.id.progress_bar);
        textEmpty = view.findViewById(R.id.text_empty);
    }
    
    private void setupRecyclerView() {
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(getContext(), imageList, this);
        
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imageAdapter);
    }
    
    private void loadImages() {
        showLoading(true);
        
        new Thread(() -> {
            try {
                List<ImageData> images = ImageUtils.getAllImages(getContext());
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        imageList.clear();
                        imageList.addAll(images);
                        imageAdapter.notifyDataSetChanged();
                        
                        showLoading(false);
                        showEmptyState(images.isEmpty());
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
    public void onImageClick(ImageData imageData) {
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        intent.putExtra("image_path", imageData.getImagePath());
        startActivity(intent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 갤러리로 돌아올 때 이미지 목록 새로고침
        loadImages();
    }
}
