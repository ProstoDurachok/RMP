package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Podderzhka extends AppCompatActivity {
    private EditText supportMessageEditText;
    private DatabaseReference supportDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podderzhka);

        supportDatabaseReference = FirebaseDatabase.getInstance().getReference("support_messages");

        supportMessageEditText = findViewById(R.id.supportMessage);
        ImageButton closeButton = findViewById(R.id.close);
        closeButton.setOnClickListener(v -> {
            onBackPressed();
        });

        findViewById(R.id.button).setOnClickListener(v -> {
            String message = supportMessageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendSupportMessage(message);
            } else {
                Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            }
        });

        supportMessageEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String message = supportMessageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendSupportMessage(message);
                } else {
                    Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        findViewById(R.id.mainLayout).setOnClickListener(v -> {
            hideKeyboard();
            supportMessageEditText.clearFocus();
        });
    }

    private void sendSupportMessage(String message) {
        String messageId = supportDatabaseReference.push().getKey();
        if (messageId != null) {
            SupportMessage supportMessage = new SupportMessage(message);
            supportDatabaseReference.child(messageId).setValue(supportMessage)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Podderzhka.this, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
                        supportMessageEditText.setText(""); // Clear the input field
                        startActivity(new Intent(this, Settings.class));
                    })
                    .addOnFailureListener(e -> Toast.makeText(Podderzhka.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show());
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(supportMessageEditText.getWindowToken(), 0);
        }
    }

    public static class SupportMessage {
        public String message;
        public SupportMessage() {}
        public SupportMessage(String message) {
            this.message = message;
        }
    }
}
