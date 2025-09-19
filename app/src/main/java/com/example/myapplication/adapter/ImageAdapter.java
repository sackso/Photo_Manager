package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.model.ImageData;
import com.example.myapplication.utils.ImageUtils;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    
    private Context context;
    private List<ImageData> imageList;
    private OnImageClickListener clickListener;
    
    public interface OnImageClickListener {
        void onImageClick(ImageData imageData);
    }
    
    public ImageAdapter(Context context, List<ImageData> imageList, OnImageClickListener clickListener) {
        this.context = context;
        this.imageList = imageList;
        this.clickListener = clickListener;
    }
    
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageData imageData = imageList.get(position);
        holder.bind(imageData);
    }
    
    @Override
    public int getItemCount() {
        return imageList.size();
    }
    
    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewThumbnail;
        private ImageView iconGPS;
        private TextView textImageName;
        private TextView textFileSize;
        private ProgressBar progressBar;
        
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageViewThumbnail = itemView.findViewById(R.id.image_view_thumbnail);
            iconGPS = itemView.findViewById(R.id.icon_gps);
            textImageName = itemView.findViewById(R.id.text_image_name);
            textFileSize = itemView.findViewById(R.id.text_file_size);
            progressBar = itemView.findViewById(R.id.progress_bar);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onImageClick(imageList.get(position));
                }
            });
        }
        
        public void bind(ImageData imageData) {
            // 이미지 이름 설정
            textImageName.setText(imageData.getImageName());
            
            // 파일 크기 설정
            textFileSize.setText(imageData.getFormattedFileSize());
            
            // GPS 아이콘 표시
            if (imageData.hasGPS()) {
                iconGPS.setVisibility(View.VISIBLE);
            } else {
                iconGPS.setVisibility(View.GONE);
            }
            
            // 이미지 로드
            loadImage(imageData.getImagePath());
        }
        
        private void loadImage(String imagePath) {
            progressBar.setVisibility(View.VISIBLE);
            
            // Glide를 사용한 이미지 로딩
            RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.color.image_placeholder)
                .error(R.color.image_placeholder);
            
            Glide.with(context)
                .load(imagePath)
                .apply(options)
                .into(imageViewThumbnail);
            
            // 로딩 완료 후 프로그레스바 숨기기
            progressBar.setVisibility(View.GONE);
        }
    }
}
