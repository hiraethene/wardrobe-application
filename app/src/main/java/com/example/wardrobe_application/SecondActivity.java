package com.example.wardrobe_application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class SecondActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private Spinner spCategory;
    private EditText tmTitle;
    private EditText tmDescription;
    private EditText tmBrand;
    private Spinner spSize;
    private Spinner spCondition;
    private Spinner spColour;
    private EditText tmMaterial;
    private EditText tmPrice;

    private Uri selectedImageUri;


    ActivityResultLauncher<Intent> launchGallery;
    private ActivityResultLauncher<String> imagePickerLauncher;

    /**
     * Initializes the activity.
     *
     * @param savedInstanceState The current state data
     */
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //setting up dropdown functionality for category spinner
        spCategory = (Spinner) findViewById(R.id.spCategory);
        //creates an array adapter using the category string array
        ArrayAdapter<CharSequence> adapter_category = ArrayAdapter.createFromResource(
                this,
                R.array.array_category,
                android.R.layout.simple_spinner_item
        );
        //Specify layout
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //applying the adapter to the spinner
        spCategory.setAdapter(adapter_category);

        //setting up dropdown functionality for size spinner
        spSize = (Spinner) findViewById(R.id.spSize);
        //creates an array adapter using the size string array
        ArrayAdapter<CharSequence> adapter_size = ArrayAdapter.createFromResource(
                this,
                R.array.array_size,
                android.R.layout.simple_spinner_item
        );
        //Specify layout
        adapter_size.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //applying the adapter to the spinner
        spSize.setAdapter(adapter_size);

        //setting up dropdown functionality for condition spinner
        spCondition = (Spinner) findViewById(R.id.spCondition);
        //creates an array adapter using the condition string array
        ArrayAdapter<CharSequence> adapter_condition = ArrayAdapter.createFromResource(
                this,
                R.array.array_condition,
                android.R.layout.simple_spinner_item
        );
        //Specify layout
        adapter_condition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //applying the adapter to the spinner
        spCondition.setAdapter(adapter_condition);

        //setting up dropdown functionality for colour spinner
        spColour = (Spinner) findViewById(R.id.spColour);
        //creates an array adapter using the colour string array
        ArrayAdapter<CharSequence> adapter_colour = ArrayAdapter.createFromResource(
                this,
                R.array.array_colour,
                android.R.layout.simple_spinner_item
        );
        //Specify layout
        adapter_colour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //applying the adapter to the spinner
        spColour.setAdapter(adapter_colour);

        // Get the intent that launched this activity, and the message in
        // the intent extra.
        Intent intent = getIntent();

        // Put that message into the text_message TextView
        //TextView textView = findViewById(R.id.tm);
        // textView.setText(message);

        imageButton = findViewById(R.id.btnImageUpload);

        launchGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null
                                && data.getData() != null) {
                            Uri uri = data.getData();
                            selectedImageUri = uri;
                            //saves the image
                            imageButton.setTag(uri);

                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .centerCrop()
                                    .into(imageButton);
                            }
                    }
                }
        );
        imageButton.setOnClickListener(view -> openGallery());

    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        launchGallery.launch(intent);
    }

    /**
     * Handles the onClick for the "Return" button.
     *
     * @param view The view (Button) that was clicked.
     */
    public void returnReply(View view) {
        // Create a new intent to return to first Activity
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
    private boolean isEmpty(EditText editText,String errorMessage) {
        if (editText.getText().toString().trim().isEmpty()){
            editText.setError(errorMessage);
            editText.requestFocus();
            return true;
        }
        return false;
    }

    private boolean isSpinnerDefault(Spinner spinner, String errorMessage) {
        if (spinner.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView) spinner.getSelectedView();
            if (errorText != null) {
                errorText.setError("");
                errorText.setTextColor(getResources().getColor((android.R.color.holo_red_dark)));
                errorText.setText(errorMessage);
            }
            spinner.requestFocus();
            return true;
        }
        return false;
    }
    public void collectItemData(View view) {
        // Collects Spinner and EditText references
        spCategory = (Spinner) findViewById(R.id.spCategory);
        tmTitle = (EditText) findViewById(R.id.tmTitle);
        tmDescription = (EditText) findViewById(R.id.tmDescription);
        tmBrand = (EditText) findViewById(R.id.tmBrand);
        spSize = (Spinner) findViewById(R.id.spSize);
        spCondition = (Spinner) findViewById(R.id.spCondition);
        spColour = (Spinner) findViewById(R.id.spColour);
        tmMaterial = (EditText) findViewById(R.id.tmMaterial);
        tmPrice = (EditText) findViewById(R.id.tmPrice);

        // Validates EditTexts
        if (tmTitle.getText().toString().trim().isEmpty()) {
            tmTitle.setError("Title field is empty. Title required.");
            return;
        }
        if (tmDescription.getText().toString().trim().isEmpty()) {
            tmDescription.setError("Description field is empty. Description required.");
            return;
        }
        if (tmBrand.getText().toString().trim().isEmpty()) {
            tmBrand.setError("Brand field is empty. Brand required.");
            return;
        }
        if (tmMaterial.getText().toString().trim().isEmpty()) {
            tmMaterial.setError("Material field is empty. Material required.");
            return;
        }

        // Price validation

        // Checks if price is empty
        final String priceText = tmPrice.getText().toString().trim();
        if (priceText.isEmpty()) {
            tmPrice.setError("Price field is empty. Price required.");
            return;
        }

        // Checks if the number in price is greater than 0
        try {
            double priceValue = Double.parseDouble(priceText);
            if (priceValue < 0) {
                tmPrice.setError("Price must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            tmPrice.setError("Invalid price.");
            return;
        }

        // Validates spinners
        if (spCategory.getSelectedItemPosition() == 0) {
            ((TextView) spCategory.getSelectedView()).setError("Select a Category");
            return;
        }

        if (spSize.getSelectedItemPosition() == 0) {
            ((TextView) spSize.getSelectedView()).setError("Select a size");
            return;
        }

        if (spCondition.getSelectedItemPosition() == 0) {
            ((TextView) spCondition.getSelectedView()).setError("Select a condition");
            return;
        }

        if (spColour.getSelectedItemPosition() == 0) {
            ((TextView) spColour.getSelectedView()).setError("Select a colour");
            return;
        }

       // Validates the image
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            Log.e("SecondActivity","No image selected");
            return;
        }

        // Collects item values after validation
        String category = spCategory.getSelectedItem().toString();
        String title = tmTitle.getText().toString();
        String description = tmDescription.getText().toString();
        String brand = tmBrand.getText().toString();
        String size = spSize.getSelectedItem().toString();
        String condition = spCondition.getSelectedItem().toString();
        String colour = spColour.getSelectedItem().toString();
        String material = tmMaterial.getText().toString();

        System.out.println(category + "," + title + "," + description + "," + brand + "," + size + "," + condition + "," + colour + "," + material + "," + priceText);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference()
                .child("wardrobe_images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();

                            // create the item object
                            WardrobeItem item = new WardrobeItem(
                                    downloadUrl,
                                    category,
                                    title,
                                    description,
                                    brand,
                                    size,
                                    condition,
                                    colour,
                                    material,
                                    priceText
                            );
                            // Return the item to the main activity
                            Intent returnDataIntent = new Intent();
                            returnDataIntent.putExtra("newItem", item);
                            setResult(RESULT_OK, returnDataIntent);
                            finish();
                        })
                )
                .addOnFailureListener(e ->
                        Log.e("Image upload failed", "Image upload failed"));

    }
}
