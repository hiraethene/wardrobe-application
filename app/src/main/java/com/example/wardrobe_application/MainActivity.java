package com.example.wardrobe_application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * MainActivity is the main activity of the wardrobe application.
 * It displays wardrobe items in a card grid layout using a RecyclerView and features a button that allows users to add items.
 *
 * <p>
 *     Features include:
 *       <ul>
 *           <li>Displays wardrobe items loaded from Firestore in a card grid layout</li>
 *           <li>A button that launches SecondActivity, where users can upload items.</li>
 *           <li>Items in the card layout that launch ItemDataActivity when clicked,
 *               where users can oversee the item's details or delete the item.</li>
 *           <li>Loading the items from Firestore when the app starts so the wardrobe is up to date.</li>
 *           <li>Keeping track of the total number of items and displaying it in a TextView.</li>
 *           <li>Searching and filtering through the wardrobe using Spinners and SearchView</li>
 *      </ul>
 *   </p>
 */
public class MainActivity extends AppCompatActivity {
    //Class name for Log tag
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SearchView svWardrobe;
    private Spinner spFilter;
    private Spinner spCategoryFilter;
    private Spinner spSizeFilter;
    private Spinner spColourFilter;
    private Spinner spConditionFilter;
    private Spinner spPriceFilter;
    private TextView tvItemNum;
    private boolean isUserFiltering = false;
    private ActivityResultLauncher<Intent> addItemLauncher;
    private ActivityResultLauncher<Intent> itemDataLauncher;
    RecyclerView rvWardrobe;
    ArrayList<WardrobeItem> wardrobeItemArrayList = new ArrayList<>();
    ArrayList<WardrobeItem> filteredList = new ArrayList<>();
    WardrobeAdapter adapter;

    /**
     * <p>
     *     This method is called when the activity is created, where it initialises view variables, firebase,
     *     sets its layout, loads in items from Firestore, sets up WardrobeAdapter for the RecyclerView,
     *     assigns a click listener for launching the ItemDatActivity and handles items added to the wardrobe
     *     with the addItemLauncher result launcher.
     *</p>
     *
     * <p>
     *     Allows the user to search and filter through wardrobe items by attaching listeners to the SearchView
     *     and filter Spinners.
     * </p>
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        // Initialize all the view variables.
        tvItemNum = findViewById(R.id.tvItemNum);
        rvWardrobe = findViewById(R.id.rvWardrobe);
        svWardrobe = findViewById(R.id.svWardrobe);
        spFilter = findViewById(R.id.spFilter);
        spCategoryFilter = findViewById(R.id.spCategoryFilter);
        spSizeFilter = findViewById(R.id.spSizeFilter);
        spConditionFilter = findViewById(R.id.spConditionFilter);
        spColourFilter = findViewById(R.id.spColourFilter);
        spPriceFilter = findViewById(R.id.spPriceFilter);

        // Hides sub spinners
        spCategoryFilter.setVisibility(View.GONE);
        spSizeFilter.setVisibility(View.GONE);
        spConditionFilter.setVisibility(View.GONE);
        spColourFilter.setVisibility(View.GONE);
        spPriceFilter.setVisibility(View.GONE);

        //setting up dropdown functionality for filter spinner
        //creates an array adapter using the filter string array
        ArrayAdapter<CharSequence> adapter_filter = ArrayAdapter.createFromResource(
                this,
                R.array.filter_menu,
                android.R.layout.simple_spinner_item
        );
        //Specify layout
        adapter_filter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //applying the adapter to the spinner
        spFilter.setAdapter(adapter_filter);

        // Attaches a listener to the spFilter  spinner
        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Hides sub spinners
                spCategoryFilter.setVisibility(View.GONE);
                spSizeFilter.setVisibility(View.GONE);
                spConditionFilter.setVisibility(View.GONE);
                spColourFilter.setVisibility(View.GONE);
                spPriceFilter.setVisibility(View.GONE);

                // Resets filters to all
                spCategoryFilter.setSelection(0);
                spSizeFilter.setSelection(0);
                spConditionFilter.setSelection(0);
                spColourFilter.setSelection(0);
                spPriceFilter.setSelection(0);


                // Displays spinners depending on the filter the user chose
                switch (position) {
                    // Category
                    case 1:
                        spCategoryFilter.setVisibility(View.VISIBLE);
                        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                                MainActivity.this,
                                R.array.filter_category,
                                android.R.layout.simple_spinner_item
                        );
                        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCategoryFilter.setAdapter(categoryAdapter);
                        break;
                    // Size
                    case 2:
                        spSizeFilter.setVisibility(View.VISIBLE);
                        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(
                                MainActivity.this,
                                R.array.filter_size,
                                android.R.layout.simple_spinner_item
                        );
                        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSizeFilter.setAdapter(sizeAdapter);
                        break;
                    // Condition
                    case 3:
                        spConditionFilter.setVisibility(View.VISIBLE);
                        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                                MainActivity.this,
                                R.array.filter_condition,
                                android.R.layout.simple_spinner_item
                        );
                        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spConditionFilter.setAdapter(conditionAdapter);
                        break;
                    // Colour
                    case 4:
                        spColourFilter.setVisibility(View.VISIBLE);
                        ArrayAdapter<CharSequence> colourAdapter = ArrayAdapter.createFromResource(
                                MainActivity.this,
                                R.array.filter_colour,
                                android.R.layout.simple_spinner_item
                        );
                        colourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spColourFilter.setAdapter(colourAdapter);
                        break;
                    // Price
                    case 5:
                        spPriceFilter.setVisibility(View.VISIBLE);
                        ArrayAdapter<CharSequence> priceAdapter = ArrayAdapter.createFromResource(
                                MainActivity.this,
                                R.array.filter_price,
                                android.R.layout.simple_spinner_item
                        );
                        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spPriceFilter.setAdapter(priceAdapter);
                        break;
                    default:
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Prevents automatic filtering
                if (!isUserFiltering) return;
                // Filters the items
                filterWardrobe(svWardrobe.getQuery().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSizeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUserFiltering) return;
                filterWardrobe(svWardrobe.getQuery().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spConditionFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUserFiltering) return;
                filterWardrobe(svWardrobe.getQuery().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spColourFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUserFiltering) return;
                filterWardrobe(svWardrobe.getQuery().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spPriceFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUserFiltering) return;
                filterWardrobe(svWardrobe.getQuery().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        adapter = new WardrobeAdapter(wardrobeItemArrayList);
        // Sets a listener for launching the ItemDataActivity when an item is clicked
        adapter.setOnItemClickListener(this::launchItemDataActivity);
        rvWardrobe.setAdapter(adapter);
        rvWardrobe.setLayoutManager(new GridLayoutManager(this, 2));

        // Implementing search for title,description, brand and material
        // Allows for real time searching
        svWardrobe.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // Calls filtering method when the search text changes
                filterWardrobe(newText);
                return true;
            }
        });
        // Loads the wardrobe items from Firestore into the RecyclerView
        loadWardrobeFromFirestore();
        isUserFiltering = true;
        // Register for the result from AddItemActivity where an item is removed
        // Removes the item from the local wardrobe item list and updates the RecyclerView.
        // Removes item from list and adapter
        // This also calls tvItemNum TextView to display the updated count of items.
        itemDataLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String deletedID = result.getData().getStringExtra("deletedItemID");

                        // Removes item from wardrobe list and adapter
                       wardrobeItemArrayList.removeIf(
                               item -> item.getDocumentID().equals(deletedID)
                       );
                        }
                    filterWardrobe(svWardrobe.getQuery().toString());
                    getCurrentItemCount();
                });



        // Registers for the result from SecondActivity where an item is added
        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Passes wardrobe item that is to be added in through parcelable
                            WardrobeItem item = data.getParcelableExtra("newItem");
                            Log.d(LOG_TAG, "wardrobe item" + item);

                            if (item != null) {
                                // Adds the item to the wardrobe array list and Firestore document collection "wardrobe"
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("wardrobe")
                                .add(item).addOnSuccessListener(documentReference -> {
                                    Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    item.setDocumentID(documentReference.getId());
                                    wardrobeItemArrayList.add(item);

                                    // Refreshes displayed list
                                    filterWardrobe(svWardrobe.getQuery().toString());
                                    Log.e(LOG_TAG, "Added a new item to the wardrobe");
                                })
                                        .addOnFailureListener(e -> Log.w(LOG_TAG, "Error adding document", e));
                            }
                        }
                    }
                }
        );
    }
    /**
     * Launches the SecondActivity where the user can add a new wardrobe item.
     * This method is triggered by the click of the add item button in MainActivity.
     *
     * @param view The view (Button) that was clicked.
     */
    public void launchSecondActivity(View view) {
        Log.d(LOG_TAG, "Button clicked!");
        Intent intent = new Intent(this, SecondActivity.class);
        addItemLauncher.launch(intent);

    }

    /**
     * Launches the ItemDataActivity where the user can see the item details and delete the item.
     * This method is triggered by the click of an item in the RecyclerView. It passes the specific item
     * to the ItemDataActivity using an Intent.
     * @param item the item that was clicked and will have its details displayed in ItemDataActivity.
     */
    public void launchItemDataActivity(WardrobeItem item) {
        Log.d(LOG_TAG, "ImageView clicked!");
        Intent intent = new Intent(this, ItemDataActivity.class);
        intent.putExtra("selectedItem", item);
        itemDataLauncher.launch(intent);
    }

    /**
     * Updates the tvItemNum TextView to show the current number of wardrobe items displayed.
     * This method changes "item" to "items" when there are multiple items in the wardrobe displayed to ensure correct grammar.
     */
    public void getCurrentItemCount() {
        int itemCount = adapter.getDisplayedItemCount();
        String strItemCount;

        if (itemCount == 1) {
            strItemCount = itemCount + " item";
        }
        else {
            strItemCount = itemCount + " items";
        }
        //Shows the number of items in the wardrobe in the tvItemNum textview
        tvItemNum.setText(strItemCount);
    }

    /**
     * Filters and sorts the wardrobe items based on user input.
     * This method applies text-based searching with SearchView and also
     * allows the user to filter with Spinners. The RecyclerView is updated to
     * display only matching items and refreshes the item count.
     * @param text the text the user is entering to search through items
     */
    public void filterWardrobe(String text) {
        // Clears previous results
        filteredList.clear();
        String category = "All";
        if (spCategoryFilter.getSelectedItem() != null) {
            category = spCategoryFilter.getSelectedItem().toString();
        }

        String size = "All";
        if (spSizeFilter.getSelectedItem() != null) {
            size = spSizeFilter.getSelectedItem().toString();
        }

        String condition = "All";
        if (spConditionFilter.getSelectedItem() != null) {
            condition = spConditionFilter.getSelectedItem().toString();
        }

        String colour = "All";
        if (spColourFilter.getSelectedItem() != null) {
            colour = spColourFilter.getSelectedItem().toString();
        }

        String price = "All";
        if (spPriceFilter.getSelectedItem() != null) {
            price = spPriceFilter.getSelectedItem().toString();
        }


        // Goes through all wardrobe items to check whether the search matches any
        // attribute details the items have in title, description, brand and material.
        for (WardrobeItem item : wardrobeItemArrayList) {
            boolean matchesText =  (item.getItemTitle().toLowerCase().contains(text.toLowerCase()) ||
                item.getItemDescription().toLowerCase().contains(text.toLowerCase())  ||
                    item.getItemBrand().toLowerCase().contains(text.toLowerCase())  ||
                    item.getItemMaterial().toLowerCase().contains(text.toLowerCase()));

            boolean matchesCategory = category.equals("All")  || item.getItemCategory().equals(category);
            boolean matchesSize = size.equals("All")  || item.getItemSize().equals(size);
            boolean matchesCondition = condition.equals("All")  || item.getItemCondition().equals(condition);
            boolean matchesColour = colour.equals("All")  || item.getItemColour().trim().equalsIgnoreCase(colour.trim());

            // Show item if it matches the filters the user chose
            if (matchesText && matchesCategory && matchesSize && matchesCondition && matchesColour) {
                filteredList.add(item);
            }
        }

        // Sorts by price lowest to highest and highest to lowest
        if (price.equals("Lowest to highest")) {
            filteredList.sort(Comparator.comparingDouble(WardrobeItem::getItemPriceAsDouble));
        } else if (price.equals("Highest to lowest")) {
            filteredList.sort((a, b) -> Double.compare(b.getItemPriceAsDouble(), a.getItemPriceAsDouble()));
        }
        // Makes the recyclerview only display the filtered items
        adapter.filterList(filteredList);
        getCurrentItemCount();
    }
    /**
     * Loads all wardrobe items from Firestore into the RecyclerView.
     *
     * <p>
     *     This method retrieves every item stored in the "wardrobe" document collection.
     *     It clears the current list, before converting each document into a WardrobeItem object,
     *     sets its documentID and refreshes the RecyclerView. The getCurrentItemCount method is called to
     *     ensure the item count on the tvItemNum TextView is up to date.
     * </p>
     */
   @SuppressLint("NotifyDataSetChanged")
   public void loadWardrobeFromFirestore() {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("wardrobe")
              .get()
              .addOnSuccessListener(querySnapshot -> {
                  wardrobeItemArrayList.clear();

                  for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                      WardrobeItem item = document.toObject(WardrobeItem.class);

                      // Checks if an item is null before setting its documentID and adding it to the wardrobe list
                      if (item != null) {
                          item.setDocumentID(document.getId());
                          wardrobeItemArrayList.add(item);
                      }
                  }
                  // Show all items when main activity is created
                  filteredList.clear();
                  filteredList.addAll(wardrobeItemArrayList);
                  adapter.filterList(filteredList);
                  getCurrentItemCount();
              })
              .addOnFailureListener(e -> Log.e(LOG_TAG, "Error loading wardrobe from Firestore",e));
       }
}