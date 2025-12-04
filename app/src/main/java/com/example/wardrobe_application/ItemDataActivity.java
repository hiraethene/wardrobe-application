package com.example.wardrobe_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class ItemDataActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_data_activity);
    }

    public void returnToMainActivity(View view) {
        // Create a new intent to return to first Activity
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
