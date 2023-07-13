package com.updatedtamizha.vintage.Adapters;

import android.icu.text.Transliterator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.updatedtamizha.vintage.Models.StoryModel;
import com.updatedtamizha.vintage.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class storiesAdapter extends RecyclerView.Adapter<storiesAdapter.StoryViewholder> {

    private List<StoryModel> list;

    public storiesAdapter(List<StoryModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public StoryViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item,parent,false);
        return new StoryViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewholder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(list.get(position).getImages().get(0))
                .placeholder(R.drawable.profile)
                .into(holder.thumbnail);
        holder.name.setText(list.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class StoryViewholder extends  RecyclerView.ViewHolder{

        private CircleImageView thumbnail;
        private TextView name;

        public StoryViewholder(@NonNull View itemView) {
            super(itemView);

            thumbnail= itemView.findViewById(R.id.stories);
            name= itemView.findViewById(R.id.name);
        }
    }

}
