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

import java.io.IOException;


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


    ActivityResultLauncher<Intent> launchGallery;

    /**
     * Initializes the activity.
     *
     * @param savedInstanceState The current state data
     */;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //setting up dropdown functionality for category spinner
        spCategory = (Spinner) findViewById (R.id.spCategory);
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
        spSize = (Spinner) findViewById (R.id.spSize);
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
        spCondition = (Spinner) findViewById (R.id.spCondition);
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
        spColour = (Spinner) findViewById (R.id.spColour);
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
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

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
                            Uri selectedImageUri = data.getData();
                            //saves the image
                            imageButton.setTag(selectedImageUri);
                            try {
                                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                imageButton.setImageBitmap(selectedImageBitmap);

                            } catch (IOException e) {
                                e.printStackTrace();

                            }
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

    public void collectItemData(View view) {
        //COLLECT SPINNER AND EDIT TEXT DATA
        spCategory = (Spinner) findViewById(R.id.spCategory);
        String category = spCategory.getSelectedItem().toString();

        tmTitle = (EditText)findViewById(R.id.tmTitle);
        String title = tmTitle.getText().toString();

        tmDescription = (EditText)findViewById(R.id.tmDescription);
        String description = tmDescription.getText().toString();

        tmBrand = (EditText)findViewById(R.id.tmBrand);
        String brand = tmBrand.getText().toString();

        spSize = (Spinner)findViewById(R.id.spSize);
        String size = spSize.getSelectedItem().toString();

        spCondition = (Spinner) findViewById(R.id.spCondition);
        String condition = spCondition.getSelectedItem().toString();

        spColour = (Spinner) findViewById(R.id.spColour);
        String colour = spColour.getSelectedItem().toString();

        tmMaterial = (EditText)findViewById(R.id.tmMaterial);
        String material = tmMaterial.getText().toString();

        tmPrice = (EditText)findViewById(R.id.tmPrice);
        String price = tmPrice.getText().toString().trim();
        if (price.isEmpty()) price = "0";

        System.out.println(category + "," + title + "," + description + "," + brand + "," + size + "," + condition + "," + colour + "," + material + "," + price);

        //getting the image uri
        Uri selectedImageUri = (Uri) imageButton.getTag();
        String imageUriString = selectedImageUri == null ? "" : selectedImageUri.toString();

        //wardrobe object
        WardrobeItem item = new WardrobeItem(
                imageUriString,
                category,
                title,
                description,
                brand,
                size,
                condition,
                colour,
                material,
                price
        );

        Intent returnDataIntent = new Intent();
        returnDataIntent.putExtra("newItem", item);
        setResult(RESULT_OK, returnDataIntent);
        finish();

    }



}
