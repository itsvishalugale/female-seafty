package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final List<VideoModel> videoList;
    private final OnVideoClickListener listener;

    public VideoAdapter(List<VideoModel> videoList, OnVideoClickListener listener) {
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoModel video = videoList.get(position);
        holder.title.setText(video.getTitle());

        // Load thumbnail with error handling
        Picasso.get()
                .load(video.getThumbnailUrl())
                .placeholder(R.drawable.placeholder) // Placeholder image
                .error(R.drawable.error_image) // Error image in case loading fails
                .into(holder.thumbnail);

        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video.getVideoId()));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.videoTitle);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
        }
    }

    public interface OnVideoClickListener {
        void onVideoClick(String videoId);
    }
}
