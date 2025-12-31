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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


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

    public void onDeleteButtonClick(View view) {
        WardrobeItem item = getIntent().getParcelableExtra("selectedItem");
        deleteItem(item);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("deletedItemID", item.documentID);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    public void deleteItem(WardrobeItem item) {
        //remove from wardrobe list and fire store?
        if (item.documentID != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("wardrobe").document(item.documentID)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Delete","document snapshot successfully deleted"))
                    .addOnFailureListener(e -> Log.w("Delete,","Error deleting document",e));
        }
        // Deletes the item from firebase storage
        if (item.itemImage != null && !item.itemImage.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReferenceFromUrl(item.itemImage);
            imageRef.delete()
                    .addOnSuccessListener(aVoid -> Log.d("Delete","Image successfully deleted"))
                    .addOnFailureListener(e -> Log.w("Delete,","Error deleting image",e));
        }
    }
}
