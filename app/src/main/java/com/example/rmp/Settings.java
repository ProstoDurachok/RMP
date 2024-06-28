package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Handle click on "Поддержка"
        findViewById(R.id.podderzhka).setOnClickListener(v -> {
            startActivity(new Intent(this, Podderzhka.class));
            // Add transition animations if needed
            // overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });

        // Handle click on "Exit"
        findViewById(R.id.exit).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity2.class));
            // Add transition animations if needed
            // overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });

        // Handle click on "Close" button (крестик)
        ImageButton closeButton = findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity
            }
        });
    }
}