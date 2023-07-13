package com.updatedtamizha.vintage.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.updatedtamizha.vintage.Adapters.storiesAdapter;
import com.updatedtamizha.vintage.Models.StoryModel;
import com.updatedtamizha.vintage.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public HomeFragment(){
        
    }
    
    private storiesAdapter storiesAdapter;
    private RecyclerView stories,feeds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        init(view);

        LinearLayoutManager feedsLayoutManager =  new LinearLayoutManager(getContext());
        feedsLayoutManager.setOrientation(RecyclerView.VERTICAL);
        
        feeds.setLayoutManager(feedsLayoutManager);


        List<String> images = new ArrayList<>();
        images.add("");

        List<StoryModel> list = new ArrayList<>();
        list.add(new StoryModel(images,"John Doe"));
        list.add(new StoryModel(images,"John Doe"));
        list.add(new StoryModel(images,"John Doe"));
        list.add(new StoryModel(images,"John Doe"));
        list.add(new StoryModel(images,"John Doe"));
        list.add(new StoryModel(images,"John Doe"));
        list.add(new StoryModel(images,"John Doe"));


        storiesAdapter = new storiesAdapter(list);
        stories.setAdapter(storiesAdapter);
        
    }
    
    private void init(View view){
        
        stories = view.findViewById(R.id.stories);
        feeds = view.findViewById(R.id.feeds);
        
    }
}