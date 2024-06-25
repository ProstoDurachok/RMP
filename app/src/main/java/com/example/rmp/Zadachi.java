package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Zadachi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zadachi);
        findViewById(R.id.zadachi).setOnClickListener(v -> {

            startActivity(new Intent(this, Zadachi.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
        findViewById(R.id.finance).setOnClickListener(v -> {

            startActivity(new Intent(this, Finances.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
        findViewById(R.id.poleznoe).setOnClickListener(v -> {

            startActivity(new Intent(this, Poleznoe.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
        findViewById(R.id.settings).setOnClickListener(v -> {

            startActivity(new Intent(this, Settings.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
    }
}