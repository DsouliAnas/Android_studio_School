package com.example.school_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private String[] imageUrls;

    // Constructor to initialize image URLs
    public ImageAdapter(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    // Creates the ViewHolder to hold image view
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    // Binds the image to the ImageView using Glide
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(imageUrls[position])  // Load image from URL
                .into(holder.imageView);  // Set image in ImageView
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return imageUrls.length;
    }

    // ViewHolder class to hold the image view
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);  // Initialize ImageView
        }
    }
}
