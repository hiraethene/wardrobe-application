package com.example.wardrobe_application;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
public class WardrobeAdapter extends RecyclerView.Adapter<WardrobeAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<WardrobeItem> wardrobeItemArrayList;

    //Constructor
    public WardrobeAdapter(Context context, ArrayList<WardrobeItem> wardrobeItemArrayList) {
        this.context = context;
        this.wardrobeItemArrayList = wardrobeItemArrayList;
    }

    @NonNull
    @Override
    public WardrobeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflates layout of each item of recycler view
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.wardrobe_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WardrobeAdapter.ViewHolder holder, int position) {
        //sets data to imageview of each card layout
        WardrobeItem item = wardrobeItemArrayList.get(position);

        Uri imageUri = Uri.parse(item.itemImage);

        Glide.with(context)
                .load(imageUri)
                .centerCrop()
                .into(holder.ivItemImage);
    }

    @Override
    public int getItemCount() {
        //returns number of card items in the recyler view
        return wardrobeItemArrayList.size();
    }

    //ViewHolder class for initialising views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
        }
    }


}

