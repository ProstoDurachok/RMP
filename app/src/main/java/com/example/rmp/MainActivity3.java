package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        findViewById(R.id.button).setOnClickListener(v -> {

            startActivity(new Intent(this, Zadachi.class));
            /*overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
    }
}