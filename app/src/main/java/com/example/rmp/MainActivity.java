package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(v -> {

            startActivity(new Intent(this, MainActivity2.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
        findViewById(R.id.newacc).setOnClickListener(v -> {

            startActivity(new Intent(this, Registration.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
    }
}