package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class DevOps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_ops);
        findViewById(R.id.close).setOnClickListener(v -> {

            startActivity(new Intent(this, Poleznoe.class));
            /* overridePendingTransition(R.anim.fade_out, R.anim.fade_in);*/
        });
    }
}