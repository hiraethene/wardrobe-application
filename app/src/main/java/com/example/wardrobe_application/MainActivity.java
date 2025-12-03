package com.example.wardrobe_application;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Class name for Log tag
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    // Unique tag required for the intent extra
    public static final String EXTRA_MESSAGE
            = "com.example.android.twoactivitiesmessagepass.extra.MESSAGE";

    // EditText view for the message
    private EditText mMessageEditText;
    // TextView for the reply header

    private TextView tvItemNum;

    private ActivityResultLauncher<Intent> launcher;

    RecyclerView rvWardrobe;
    ArrayList<WardrobeItem> wardrobeItemArrayList = new ArrayList<>();
    WardrobeAdapter adapter;
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
        // Initialize all the view variables.
        mMessageEditText = findViewById(R.id.editText_main);
        tvItemNum = findViewById(R.id.tvItemNum);

        rvWardrobe = findViewById(R.id.rvWardrobe);
        adapter = new WardrobeAdapter(this, wardrobeItemArrayList);
        rvWardrobe.setAdapter(adapter);
        rvWardrobe.setLayoutManager(new GridLayoutManager(this, 2));

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            WardrobeItem item = data.getParcelableExtra("newItem");
                            Log.d(LOG_TAG, "wardrobe item" + item);

                            if (item != null) {
                                getCurrentItemCount();
                                wardrobeItemArrayList.add(item);
                                adapter.notifyItemInserted(wardrobeItemArrayList.size() - 1);


                                Log.e(LOG_TAG, "Added a new item to the wardrobe");
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
        launcher.launch(intent);

    }
//Returns the number of items in array list
// and adds one to make it intuitive for users as lists start from 0
    public void getCurrentItemCount() {
        int itemCount = wardrobeItemArrayList.size() + 1;
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

}