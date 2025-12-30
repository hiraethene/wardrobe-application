package com.example.wardrobe_application;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class ItemDataActivity extends Activity {
TextView tvCategory, tvTitle, tvDescription, tvBrand, tvSize, tvCondition, tvColour, tvMaterial, tvPrice;
ImageView ivDisplayItemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_data_activity);

        // Initialize all the view variables.
        ivDisplayItemImage = findViewById(R.id.ivDisplayItemImage);
        tvCategory = findViewById(R.id.tvCategory);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvBrand = findViewById(R.id.tvBrand);
        tvSize = findViewById(R.id.tvSize);
        tvCondition = findViewById(R.id.tvCondition);
        tvColour = findViewById(R.id.tvColour);
        tvMaterial = findViewById(R.id.tvMaterial);
        tvPrice = findViewById(R.id.tvPrice);


        WardrobeItem item = getIntent().getParcelableExtra("selectedItem");
        populateViews(item);

        Intent intent = getIntent();
    }

    /**
     * Handles the onClick for the "Back" button.
     *
     * @param view The view (Button) that was clicked.
     */
    public void returnToMainActivity(View view) {
        // Create a new intent to return to first Activity
        finish();
    }

    public void populateViews(WardrobeItem item) {
        tvCategory.setText(item.itemCategory);
        tvTitle.setText(item.itemTitle);
        tvDescription.setText(item.itemDescription);
        tvBrand.setText(item.itemBrand);
        tvSize.setText(item.itemSize);
        tvCondition.setText(item.itemCondition);
        tvColour.setText(item.itemColour);
        tvMaterial.setText(item.itemMaterial);
        tvPrice.setText(item.itemPrice);

        // Load image with firebase url
        Glide.with(this)
                .load(item.itemImage)
                .centerCrop()
                .into(ivDisplayItemImage);
    }

    public void deleteItem(WardrobeItem item) {
        //load the item
        //remove from wardrobe list and fire store?
        if (item.documentID != null) {
     //       FirebaseFirestore db = FirebaseFirestore.getInstance();
     //        bvgfe\
        }
    }
}
