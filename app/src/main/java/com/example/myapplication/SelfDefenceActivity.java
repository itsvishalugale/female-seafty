package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelfDefenceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private EditText searchBar;
    private ImageView clearSearch;
    private List<VideoModel> videoList, filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_defence);

        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.searchBar);
        clearSearch = findViewById(R.id.clearSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load video list
        videoList = getSelfDefenceVideos();
        filteredList = new ArrayList<>(videoList);
        videoAdapter = new VideoAdapter(filteredList, this::openYouTubeVideo);
        recyclerView.setAdapter(videoAdapter);

        // Search bar text change listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterVideos(charSequence.toString());
                clearSearch.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Clear search input when 'X' button is clicked
        clearSearch.setOnClickListener(v -> {
            searchBar.setText("");
            filterVideos("");
        });
    }

    private List<VideoModel> getSelfDefenceVideos() {
        List<VideoModel> videos = new ArrayList<>();

        // Adding self-defense videos
        videos.add(new VideoModel("5 Self-Defense Moves Every Woman Should Know", "https://img.youtube.com/vi/KVpxP3ZZtAc/0.jpg", "KVpxP3ZZtAc"));
        videos.add(new VideoModel("How to Escape Wrist Grab", "https://img.youtube.com/vi/9m-x64bKfR4/0.jpg", "9m-x64bKfR4"));
        videos.add(new VideoModel("What To Do If Someone Grabs You From Behind", "https://img.youtube.com/vi/IPBhxe_3uds/0.jpg", "IPBhxe_3uds"));
        videos.add(new VideoModel("How to Escape a Bear Hug Attack", "https://img.youtube.com/vi/q1pBBRi3XF8/0.jpg", "q1pBBRi3XF8"));
        videos.add(new VideoModel("Fast Self Defense Moves", "https://img.youtube.com/vi/MybIOuS30lk/0.jpg", "MybIOuS30lk"));
        videos.add(new VideoModel("Simple Defense Against Attack", "https://img.youtube.com/vi/0UqK3tfuu8Q/0.jpg", "0UqK3tfuu8Q"));
        videos.add(new VideoModel("Short: Women's Self Defense Tips", "https://img.youtube.com/vi/s5C5xgZ3gBM/0.jpg", "s5C5xgZ3gBM"));
        videos.add(new VideoModel("Short: Quick Self Defense Trick", "https://img.youtube.com/vi/Va46jxWS-D8/0.jpg", "Va46jxWS-D8"));

        // Adding extra requested videos
        videos.add(new VideoModel("How to Defend Yourself", "https://img.youtube.com/vi/vcpZxOQOWTY/0.jpg", "vcpZxOQOWTY"));
        videos.add(new VideoModel("Basic Self Defense Moves", "https://img.youtube.com/vi/XSScUodSezQ/0.jpg", "XSScUodSezQ"));
        videos.add(new VideoModel("Escape Dangerous Situations", "https://img.youtube.com/vi/Srg_zlYueOo/0.jpg", "Srg_zlYueOo"));
        videos.add(new VideoModel("Self-Defense Against Attackers", "https://img.youtube.com/vi/21jFKYXVGGA/0.jpg", "21jFKYXVGGA"));
        videos.add(new VideoModel("Women's Street Safety Tips", "https://img.youtube.com/vi/eFq_7hUB330/0.jpg", "eFq_7hUB330"));
        videos.add(new VideoModel("How to Protect Yourself", "https://img.youtube.com/vi/21Nier1cHjQ/0.jpg", "21Nier1cHjQ"));

        return videos;
    }

    private void filterVideos(String query) {
        filteredList.clear();
        for (VideoModel video : videoList) {
            if (video.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(video);
            }
        }
        videoAdapter.notifyDataSetChanged();
    }

    private void openYouTubeVideo(String videoId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
        startActivity(intent);
    }
}
