package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.model.ImageData;

import java.util.List;

public class MapImageAdapter extends RecyclerView.Adapter<MapImageAdapter.MapImageViewHolder> {
    
    private Context context;
    private List<ImageData> imageList;
    private OnMapImageClickListener clickListener;
    private int selectedPosition = -1;
    
    public interface OnMapImageClickListener {
        void onMapImageClick(ImageData imageData);
    }
    
    public MapImageAdapter(Context context, List<ImageData> imageList, OnMapImageClickListener clickListener) {
        this.context = context;
        this.imageList = imageList;
        this.clickListener = clickListener;
    }
    
    @NonNull
    @Override
    public MapImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_map_image, parent, false);
        return new MapImageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MapImageViewHolder holder, int position) {
        ImageData imageData = imageList.get(position);
        holder.bind(imageData, position == selectedPosition);
    }
    
    @Override
    public int getItemCount() {
        return imageList.size();
    }
    
    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }
    
    class MapImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewThumbnail;
        private View viewSelected;
        private ProgressBar progressBar;
        
        public MapImageViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageViewThumbnail = itemView.findViewById(R.id.image_view_thumbnail);
            viewSelected = itemView.findViewById(R.id.view_selected);
            progressBar = itemView.findViewById(R.id.progress_bar);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    setSelectedPosition(position);
                    clickListener.onMapImageClick(imageList.get(position));
                }
            });
        }
        
        public void bind(ImageData imageData, boolean isSelected) {
            // 선택 상태 표시
            viewSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            
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
