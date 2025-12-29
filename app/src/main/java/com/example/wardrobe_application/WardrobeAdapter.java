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

        //Uri imageUri = Uri.parse(item.itemImage);


        holder.ivItemImage.setImageDrawable(null);
        Glide.with(holder.itemView.getContext())
                .load(item.getItemImage())
                .centerCrop()
                .into(holder.ivItemImage);

        //sets the click listener for the image view
        holder.ivItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns number of card items in the recycler view
        return wardrobeItemArrayList.size();
    }

    private onItemClickListener listener;
    //interface for  the click listener
    public interface onItemClickListener{
        void onItemClick(WardrobeItem item);
    }
    //sets the click listener for the adapter
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
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

