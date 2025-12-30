package com.example.wardrobe_application;

import static android.graphics.Insets.add;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Class name for Log tag
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    // Unique tag required for the intent extra

    private TextView tvItemNum;

    private ActivityResultLauncher<Intent> addItemLauncher;
    // Launcher to handle image picking from gallery

    private String documentID;

    RecyclerView rvWardrobe;
    ArrayList<WardrobeItem> wardrobeItemArrayList = new ArrayList<>();
    WardrobeAdapter adapter;
    private FirebaseApp FireBaseApp;

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
        FireBaseApp.initializeApp(this);
        // Initialize all the view variables.
        tvItemNum = findViewById(R.id.tvItemNum);
        rvWardrobe = findViewById(R.id.rvWardrobe);


        adapter = new WardrobeAdapter(this, wardrobeItemArrayList);
        adapter.setOnItemClickListener(this::launchItemDataActivity);
        rvWardrobe.setAdapter(adapter);
        rvWardrobe.setLayoutManager(new GridLayoutManager(this, 2));

        loadWardrobeFromFirestore();

        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            WardrobeItem item = data.getParcelableExtra("newItem");
                            Log.d(LOG_TAG, "wardrobe item" + item);

                            if (item != null) {
                                getCurrentItemCount();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("wardrobe")
                                .add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                item.documentID = documentReference.getId();
                                                wardrobeItemArrayList.add(item);
                                                adapter.notifyItemInserted(wardrobeItemArrayList.size() - 1);
                                                getCurrentItemCount();
                                                Log.e(LOG_TAG, "Added a new item to the wardrobe");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(LOG_TAG, "Error adding document", e);
                                                }
                                        });

                            }
                        }
                    }
                }
        );
    }

    /**
     * Handles the onClick for the "Send" button. Gets the value of the main EditText,
     * creates an intent, and launches the second activity with that intent.
     * <p>
     *
     * @param view The view (Button) that was clicked.
     */
    public void launchSecondActivity(View view) {
        Log.d(LOG_TAG, "Button clicked!");
        Intent intent = new Intent(this, SecondActivity.class);
        addItemLauncher.launch(intent);

    }

    public void launchItemDataActivity(WardrobeItem item) {
        Log.d(LOG_TAG, "ImageView clicked!");
        Intent intent = new Intent(this, ItemDataActivity.class);
        intent.putExtra("selectedItem", item);
        startActivity(intent);
    }
//Returns the number of items in array list
// and adds one to make it intuitive for users as lists start from 0
    public void getCurrentItemCount() {
        int itemCount = wardrobeItemArrayList.size();
        String strItemCount;

        if (itemCount == 1) {
            strItemCount = Integer.toString(itemCount) + " item";
        }
        else {
            strItemCount = Integer.toString(itemCount) + " items";
        }
        //Shows the number of items in the wardrobe in the item num textview
        tvItemNum.setText(strItemCount);
    }

   public void loadWardrobeFromFirestore() {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("wardrobe")
              .get()
              .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                  @Override
                  public void onSuccess(QuerySnapshot querySnapshot) {
                      wardrobeItemArrayList.clear();

                      for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                          WardrobeItem item = document.toObject(WardrobeItem.class);

                          if (item != null) {
                              wardrobeItemArrayList.add(item);
                          }
                      }
                      adapter.notifyDataSetChanged();
                      getCurrentItemCount();
                  }
              })
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Log.e(LOG_TAG, "Error loading wardrobe from Firestore",e);
                  }
              });

       }
}