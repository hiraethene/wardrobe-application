package com.example.wardrobe_application;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * ItemDataActivity provides the functionality for displaying a single WardrobeItem's attributes in the ItemDataActivity activity.
 *
 * <p>
 *     This activity receives a WardrobeItem object through Intent, populates the views in the UI with its data, and allows the user
 *     to delete the item from Firestore and Firebase storage.
 * </p>
 *
 * <p>
 *       Features include:
 *       <ul>
 *           <li>Displays item attributes including image, category, title, description, brand, size, condition
 *               colour, material and price. This is done by populating the views using {@link #populateViews(WardrobeItem)}.</li>
 *           <li>Deletes items from Firestore and Firebase storage using  {@link #deleteItem(WardrobeItem)} triggered by
 *               {@link #onDeleteButtonClick(View)}.</li>
 *           <li>Handles returning to the main activity when the user presses the back button through {@link #returnToMainActivity(View)}.</li>
 *      </ul>
 *   </p>
 */
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

        // Retrieves the item that was passed
        WardrobeItem item = getIntent().getParcelableExtra("selectedItem");
        if (item != null) {
            populateViews(item);
        } else {
            Log.e("parcelablePassing","No WardrobeItem passed to ItemDataActivity");
            finish();
        }

    }

    /**
     * Handles the onClick for the "Back" button to return to main activity.
     *
     * @param view The button view that was clicked.
     */
    public void returnToMainActivity(View view) {finish();}

    /**
     *  Populates the TextViews and ImageView with the WardrobeItem item's data.
     * @param item the WardrobeItem whose data is displayed.
     */
    public void populateViews(WardrobeItem item) {
        tvCategory.setText(item.getItemCategory());
        tvTitle.setText(item.getItemTitle());
        tvDescription.setText(item.getItemDescription());
        tvBrand.setText(item.getItemBrand());
        tvSize.setText(item.getItemSize());
        tvCondition.setText(item.getItemCondition());
        tvColour.setText(item.getItemColour());
        tvMaterial.setText(item.getItemMaterial());
        tvPrice.setText(item.getItemPrice());

        // Load image with firebase url
        Glide.with(getApplicationContext())
                .load(item.getItemImage())
                .centerCrop()
                .into(ivDisplayItemImage);
    }

    /**
     * Handles the click of the delete item button by calling the deleteItem method.
     *
     * @param view the button view that was clicked
     */
    public void onDeleteButtonClick(View view) {
        // Retrieves the item that was passed.
        WardrobeItem item = getIntent().getParcelableExtra("selectedItem");
        if (item !=null) {
            //Calls the deleteItem method that removes the item from Firebase and Firestore storage.
            deleteItem(item);
            Intent resultIntent = new Intent();
            // Puts the ID of the item's document into the intent so the activity knows
            // which item was deleted.
            resultIntent.putExtra("deletedItemID", item.getDocumentID());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    /**
     * Removes the item's document from the Firestore wardrobe collection and
     * deletes its image from Firebase storage.
     *
     * @param item the WardrobeItem to be deleted.
     */
    public void deleteItem(WardrobeItem item) {
        //removes the item's document in Firestore
        if (item.getDocumentID() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("wardrobe").document(item.getDocumentID())
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Delete","document snapshot successfully deleted"))
                    .addOnFailureListener(e -> Log.w("Delete,","Error deleting document",e));
        }
        // Deletes the item's image from firebase storage
        if (item.getItemImage() != null && !item.getItemImage().isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReferenceFromUrl(item.getItemImage());
            imageRef.delete()
                    .addOnSuccessListener(aVoid -> Log.d("Delete","Image successfully deleted"))
                    .addOnFailureListener(e -> Log.w("Delete,","Error deleting image",e));
        }
    }
}
