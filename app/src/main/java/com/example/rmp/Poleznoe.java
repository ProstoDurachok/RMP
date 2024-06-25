package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Poleznoe extends AppCompatActivity {
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poleznoe);
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
        ImageButton favoriteButton = findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                favoriteButton.setSelected(isFavorite);
            }
        });
        ImageButton favoriteButton1 = findViewById(R.id.favoriteButton1);
        favoriteButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                favoriteButton1.setSelected(isFavorite);
            }
        });
        ImageButton favoriteButtonAll = findViewById(R.id.favoriteButtonAll);
        favoriteButtonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                favoriteButtonAll.setSelected(isFavorite);
            }
        });
        findViewById(R.id.bitcoin).setOnClickListener(v -> {

            startActivity(new Intent(this, BitCoin.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
        findViewById(R.id.devops).setOnClickListener(v -> {

            startActivity(new Intent(this, DevOps.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
    }
}