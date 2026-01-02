package com.example.wardrobe_application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

/**
 * WardrobeAdapter is a RecyclerView adapter that displays a list of WardrobeItem items in a card layout.
 * The adapter acts as a bridge between the WardrobeItem list data and the RecyclerView.
 *
 * <p>
 *     The adapter does the following:
 * <ul>
 *     <li>Inflates a card layout for each item.</li>
 *     <li>Binds the item's image reference to its ImageView.</li>
 *     <li>Sets a click listener for the ImageView representing the item.</li>
 *     <li>Includes a method that removes items from the list of wardrobe items.</li>
 *     <li>Includes a method that returns the size of the list of wardrobe items.</li>
 * </ul>
 * </p>
 */
public class WardrobeAdapter extends RecyclerView.Adapter<WardrobeAdapter.ViewHolder> {
    private final ArrayList<WardrobeItem> wardrobeItemArrayList;
    //The listener for the click of the ImageView
    private onItemClickListener listener;


    /**
     * This constructor creates a WardrobeAdapter.
     *
     * @param wardrobeItemArrayList the list of WardrobeItem items to display.
     */
    public WardrobeAdapter(ArrayList<WardrobeItem> wardrobeItemArrayList) {
        this.wardrobeItemArrayList = wardrobeItemArrayList;
    }

    /**
     * Returns the total amount of items in the wardrobe list.
     * @return the size of the ArrayList.
     */
    @Override
    public int getItemCount() {return wardrobeItemArrayList.size();}

    /**
     * RecyclerView calls this method whenever it needs to create a new ViewHolder. This inflates
     * the card layout for a single wardrobe item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return a new ViewHolder instance.
     */
    @NonNull
    @Override
    public WardrobeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflates layout of each item of recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wardrobe_card_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * RecyclerView calls this method to associate a ViewHolder with the WardrobeItem data(binding).
     * This loads the item's image into ImageView and sets a click listener for the ImageView.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull WardrobeAdapter.ViewHolder holder, int position) {
        // Gets the item at the current position
        WardrobeItem item = wardrobeItemArrayList.get(position);

        // Loads the item's image using Glide
        holder.ivItemImage.setImageDrawable(null);
        Glide.with(holder.itemView.getContext())
                .load(item.getItemImage())
                .centerCrop()
                .into(holder.ivItemImage);

        //sets the click listener for the image view
        holder.ivItemImage.setOnClickListener(view -> listener.onItemClick(item));

    }

    /**
     * Removes the item from the wardrobe array.
     * @param item the item to be removed.
     */
    public void removeItem(WardrobeItem item) {
        int pos = wardrobeItemArrayList.indexOf(item);
        if (pos != -1) {
            wardrobeItemArrayList.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    /**
     * Listener interface that's used to notify when a wardrobe item is clicked.
     */
    public interface onItemClickListener { void onItemClick(WardrobeItem item);}

    /**
     * Sets the click listener for the adapter
     * @param listener the listener used to handle item click events.
     */
    public void setOnItemClickListener(onItemClickListener listener) {this.listener = listener;}


    /**
     * This is the ViewHolder class that holds the references to the ImageView used to display the item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivItemImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivItemImage = itemView.findViewById(R.id.ivItemImage);
            }
        }
    }


