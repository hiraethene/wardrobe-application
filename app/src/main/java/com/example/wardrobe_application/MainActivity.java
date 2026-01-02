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
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;


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
 *      </ul>
 *   </p>
 */
public class MainActivity extends AppCompatActivity {
    //Class name for Log tag
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private TextView tvItemNum;
    private ActivityResultLauncher<Intent> addItemLauncher;
    private ActivityResultLauncher<Intent> itemDataLauncher;
    RecyclerView rvWardrobe;
    ArrayList<WardrobeItem> wardrobeItemArrayList = new ArrayList<>();
    WardrobeAdapter adapter;

    /**
     * <p>
     *     This method is called when the activity is created, where it initialises view variables, firebase,
     *     sets its layout, loads in items from Firestore, sets up WardrobeAdapter for the RecyclerView,
     *     assigns a click listener for launching the ItemDatActivity and handles items added to the wardrobe
     *     with the addItemLauncher result launcher.
     *</p>
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


        adapter = new WardrobeAdapter(wardrobeItemArrayList);
        // Sets a listener for launching the ItemDataActivity when an item is clicked
        adapter.setOnItemClickListener(this::launchItemDataActivity);
        rvWardrobe.setAdapter(adapter);
        rvWardrobe.setLayoutManager(new GridLayoutManager(this, 2));

        // Loads the wardrobe items from Firestore into the RecyclerView
        loadWardrobeFromFirestore();

        // Register for the result from AddItemActivity where an item is removed
        // Removes the item from the local wardrobe item list and updates the RecyclerView.
        // Removes item from list and adapter
        // This also calls tvItemNum TextView to display the updated count of items.
        itemDataLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String deletedID = result.getData().getStringExtra("deletedItemID");

                        // Removes item from list and adapter
                        for (int i = 0; i < wardrobeItemArrayList.size(); i++) {
                            if (wardrobeItemArrayList.get(i).getDocumentID().equals(deletedID)) {
                                adapter.removeItem(wardrobeItemArrayList.get(i));
                            }
                        }
                        getCurrentItemCount();

                    }
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
                                // Updates the number of items displayed in tvItemNum TextView
                                getCurrentItemCount();

                                // Adds the item to the wardrobe array list and Firestore document collection "wardrobe"
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("wardrobe")
                                .add(item).addOnSuccessListener(documentReference -> {
                                    Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    item.setDocumentID(documentReference.getId());
                                    wardrobeItemArrayList.add(item);
                                    adapter.notifyItemInserted(wardrobeItemArrayList.size() - 1);
                                    getCurrentItemCount();
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
     * Updates the tvItemNum TextView to show the current number of wardrobe items.
     * This method changes "item" to "items" when there are multiple items in the wardrobe to ensure correct grammar.
     */
    public void getCurrentItemCount() {
        int itemCount = wardrobeItemArrayList.size();
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
                  // Notifies the RecyclerView that data in the adapter has changed so it refreshes.
                  adapter.notifyDataSetChanged();
                  getCurrentItemCount();
              })
              .addOnFailureListener(e -> Log.e(LOG_TAG, "Error loading wardrobe from Firestore",e));
       }
}