package com.example.wardrobe_application;

import android.content.Intent;
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
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * SecondActivity is the activity that allows the user to create a new wardrobe item.
 *
 * <p>
 *     This activity features an ImageButton, Spinners and EditText objects where users can input
 *     item details including the item's image, category, title, description, brand, size,
 *     condition, colour, material and price. Users select an image from their device's gallery,
 *     and all user input is validated when they press the upload item button. This activity
 *     also features a back button in case the user changes their mind and decides to go back
 *     to the main activity.
 * </p>
 *
 * <p>
 *     If the inputs pass validation checks, the item is returned to the main activity,
 *     where the details are stored in a document collection called "wardrobe" in Firestore, and
 *     the download url of  the image is stored in Firebase storage.
 * </p>
 *
 */
public class SecondActivity extends AppCompatActivity {
    // UI components used to collect user input for creating a wardrobe item.
    private ImageButton imageButton;
    private Spinner spCategory;
    private EditText etTitle;
    private EditText etDescription;
    private EditText etBrand;
    private Spinner spSize;
    private Spinner spCondition;
    private Spinner spColour;
    private EditText etMaterial;
    private EditText etPrice;
    private Uri selectedImageUri;
    ActivityResultLauncher<Intent> launchGallery;

    /**
     * Initializes the activity UI with components used to input item details,
     * sets up spinner adapters that make spinners functional, registers the activity result
     * launcher for an image to be selected from the user's gallery,
     *
     * @param savedInstanceState The current state data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Initialize UI components
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etBrand = findViewById(R.id.etBrand);
        etMaterial = findViewById(R.id.etMaterial);
        etPrice = findViewById(R.id.etPrice);
        imageButton = findViewById(R.id.btnImageUpload);
        spCategory = findViewById(R.id.spCategory);
        spSize = findViewById(R.id.spSize);
        spCondition = findViewById(R.id.spCondition);
        spColour = findViewById(R.id.spColour);

        // SPINNER FUNCTIONALITY

        //setting up dropdown functionality for category spinner
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


        // Registers the activity result launcher for an image to be selected from
        // the user's gallery.

        launchGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Retrieves the image's URI, stores it and displays it in imageButton using Glide
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
        // Opens the gallery when the image button is clicked
        imageButton.setOnClickListener(view -> openGallery());
    }

    /**
     * Launches the device's image gallery so that the user can select an image for the item.
     * The selected image's URI is retrieved through the activity result launcher and
     * displayed in the image button.
     */
    public void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        launchGallery.launch(intent);
    }

    /**
     * Handles the onClick for the back button which returns to the main activity.
     *
     * @param view The view (Button) that was clicked.
     */
    public void returnToMainActivity(View view) {
        // Create a new intent to return to the main activity
        Intent returnIntent = new Intent();
        // Sets the result as cancelled as no data is being returned
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    /**
     * This method validates, collects and processes the user's input
     * to create a new wardrobe item when the upload item button is pressed.
     *
     * <p>
     *     This method does the following:
     *     <ul>
     *         <li>Retrieves references to all Spinner and EditText components.</li>
     *         <li>Validates each component to check that a choice is made in spinners,
     *             EditTexts aren't empty, price is a positive number, and price isn't a letter.</li>
     *         <li>Checks that the user has selected an image.</li>
     *         <li>If all of the component pass validation, the item input details are collected
     *             from the ImageButton, Spinners and EditTexts. </li>
     *         <li>The WardrobeItem item object is then created with the input details and image URL.
     *             It gets returned to the MainActivity through intent where the item is added.</li>
     *     </ul>
     * </p>
     * @param view The upload item button that triggers this method when it's clicked.
     */
    public void collectItemData(View view) {
        spCategory = findViewById(R.id.spCategory);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etBrand = findViewById(R.id.etBrand);
        spSize = findViewById(R.id.spSize);
        spCondition = findViewById(R.id.spCondition);
        spColour = findViewById(R.id.spColour);
        etMaterial = findViewById(R.id.etMaterial);
        etPrice = findViewById(R.id.etPrice);

        // Validates EditTexts and displays error messages if they don't pass validation
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Title field is empty. Title required.");
            Toast.makeText(this, "Title field is empty. Title required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            etDescription.setError("Description field is empty. Description required.");
            Toast.makeText(this, "Description field is empty. Description required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etBrand.getText().toString().trim().isEmpty()) {
            etBrand.setError("Brand field is empty. Brand required.");
            Toast.makeText(this, "Brand field is empty. Brand required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etMaterial.getText().toString().trim().isEmpty()) {
            etMaterial.setError("Material field is empty. Material required.");
            Toast.makeText(this, "Material field is empty. Material required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Price validation

        // Checks if price is empty
        final String priceText = etPrice.getText().toString().trim();
        if (priceText.isEmpty()) {
            etPrice.setError("Price field is empty. Price required.");
            Toast.makeText(this, "Price field is empty. Price required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Checks if the number in price is greater than 0 or a letter
        try {
            double priceValue = Double.parseDouble(priceText);
            if (priceValue < 0) {
                etPrice.setError("Price must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price.");
            Toast.makeText(this, "Invalid price. Enter a number above 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validates spinners by checking if they're still at default value
        // and displays error messages if they don't pass validation
        if (spCategory.getSelectedItemPosition() == 0) {
            ((TextView) spCategory.getSelectedView()).setError("Select a Category");
            Toast.makeText(this, "Select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spSize.getSelectedItemPosition() == 0) {
            ((TextView) spSize.getSelectedView()).setError("Select a size");
            Toast.makeText(this, "Select a size", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spCondition.getSelectedItemPosition() == 0) {
            ((TextView) spCondition.getSelectedView()).setError("Select a condition");
            Toast.makeText(this, "Select a condition", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spColour.getSelectedItemPosition() == 0) {
            ((TextView) spColour.getSelectedView()).setError("Select a colour");
            Toast.makeText(this, "Select a colour", Toast.LENGTH_SHORT).show();
            return;
        }

       // Validates the image and displays an error message if it doesn't pass it
        // Checks if the URI is null
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            Log.e("SecondActivity","No image selected");
            return;
        }

        // Collects item details after passing validation
        String category = spCategory.getSelectedItem().toString();
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String brand = etBrand.getText().toString();
        String size = spSize.getSelectedItem().toString();
        String condition = spCondition.getSelectedItem().toString();
        String colour = spColour.getSelectedItem().toString();
        String material = etMaterial.getText().toString();

        // Used for debugging
        System.out.println(category + "," + title + "," + description + "," + brand + "," + size + "," + condition + "," + colour + "," + material + "," + priceText);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Creates a reference to a new file in the wardrobe_images folder and
        // generates a file name using the current time stamp to ensure it's unique and not
        // redundant.
        StorageReference storageRef = storage.getReference()
                .child("wardrobe_images/" + System.currentTimeMillis() + ".jpg");

        // Uploads the image uri to Firebase storage
        storageRef.putFile(selectedImageUri)
                // Gets the download url and converts it to string
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();

                            // creates the item object
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
                            // Return the item to the main activity using Intent
                            Intent returnDataIntent = new Intent();
                            returnDataIntent.putExtra("newItem", item);
                            setResult(RESULT_OK, returnDataIntent);
                            // Closes SecondActivity and goes back to MainActivity
                            finish();
                        })
                )
                .addOnFailureListener(e ->
                        Log.e("Image upload", "Image upload failed"));
    }
}
