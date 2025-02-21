package com.example.myapplication;

public class VideoModel {
    private final String title;
    private final String thumbnailUrl;
    private final String videoId;

    public VideoModel(String title, String thumbnailUrl, String videoId) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getVideoId() {
        return videoId;
    }
}
