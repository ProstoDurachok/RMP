package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {

    private EditText editText, editPassword;
    private DatabaseReference usersRef;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editText = findViewById(R.id.customEditText);
        editPassword = findViewById(R.id.editPassword);
        usersRef = FirebaseDatabase.getInstance().getReference().child("User");

        preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        findViewById(R.id.button).setOnClickListener(v -> {
            String email = editText.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                signIn(email, password);
            } else {
                Toast.makeText(MainActivity2.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.redirect).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity2.this, Registration.class));
        });
    }

    private void signIn(String email, String password) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        String id = userSnapshot.child("id").getValue(String.class);

                        if (storedPassword != null && id != null && storedPassword.equals(password)) {
                            saveUserId(id);
                            Log.d("Finances", "Current User UUID: " + id);

                            moveToNextScreen();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(MainActivity2.this, "Authentication failed: User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity2.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserId(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        Log.d("Finances", "Current User UUID: " + userId);

        editor.apply();
    }


    private void moveToNextScreen() {
        startActivity(new Intent(MainActivity2.this, Zadachi.class));
        finish();
    }
}
