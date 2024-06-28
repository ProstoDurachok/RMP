package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Registration extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.emailText);
        passwordEditText = findViewById(R.id.passwordText);

        // Set touch listener for the main layout
        findViewById(R.id.mainLayout).setOnTouchListener((v, event) -> {
            hideKeyboard();
            clearFocus();
            return false;
        });

        findViewById(R.id.button).setOnClickListener(v -> {
            // Handle button click
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()) {
                // Perform registration logic
                Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
        }
    }

    private void clearFocus() {
        emailEditText.clearFocus();
        passwordEditText.clearFocus();
    }

    public void onClickSave(View view) {
        // Handle onClickSave if necessary
    }
}