package com.example.wardrobe_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class SecondActivity extends AppCompatActivity {




    /**
     * Initializes the activity.
     *
     * @param savedInstanceState The current state data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);



        // Get the intent that launched this activity, and the message in
        // the intent extra.
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Put that message into the text_message TextView
        TextView textView = findViewById(R.id.text_message);
        textView.setText(message);
    }

    /**
     * Handles the onClick for the "Return" button.
     *
     * @param view The view (Button) that was clicked.
     */
    public void returnReply(View view) {


        // Create a new intent to return to first Activity
        Intent returnIntent = new Intent();
        finish();
    }
}
