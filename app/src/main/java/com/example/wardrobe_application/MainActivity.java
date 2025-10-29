package com.example.wardrobe_application;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {

    //Class name for Log tag
    //private static final String LOG_TAG = MyActivity.class.getSimpleName();

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
    }

    public void onStart() {
        super.onStart();
        tToast("onStart");
    }

    public void onRestart() {
        super.onRestart();
        tToast("onRestart");
    }

    public void onResume() {
        super.onResume();
        tToast("onResume");
    }

    public void onPause() {
        super.onPause();
        tToast("onPause");
    }

    public void onStop() {
        super.onStop();
        tToast("onStop");
    }
    public void onDestroy() {
        super.onDestroy();
        tToast("onDestroy");
    }

    private void tToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }
}