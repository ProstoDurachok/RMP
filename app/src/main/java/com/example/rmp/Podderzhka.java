package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Podderzhka extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podderzhka);
        findViewById(R.id.button).setOnClickListener(v -> {

            startActivity(new Intent(this, Settings.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
    }
}