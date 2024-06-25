package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.podderzhka).setOnClickListener(v -> {

            startActivity(new Intent(this, Podderzhka.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
        findViewById(R.id.exit).setOnClickListener(v -> {

            startActivity(new Intent(this, MainActivity2.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
    }
}