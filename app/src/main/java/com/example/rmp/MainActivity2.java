package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {

    private EditText editText, editPassword;
    private DatabaseReference usersRef; // Ссылка на базу данных Firebase
    private String userId; // Глобальная переменная для хранения UUID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editText = findViewById(R.id.customEditText);
        editPassword = findViewById(R.id.editPassword);
        usersRef = FirebaseDatabase.getInstance().getReference().child("User"); // Инициализация ссылки на базу данных

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
        // Запрос к базе данных Firebase для получения id по email и password
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Получаем данные пользователя
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        String id = userSnapshot.child("id").getValue(String.class);

                        // Проверяем пароль и id
                        if (storedPassword != null && id != null && storedPassword.equals(password)) {
                            userId = id; // Получаем id пользователя
                            moveToNextScreen(); // Переходим на следующий экран
                            return;
                        }
                    }
                }
                // Неверные учетные данные или пользователь не найден
                Toast.makeText(MainActivity2.this, "Authentication failed: Invalid credentials", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ошибка при чтении из базы данных
                Toast.makeText(MainActivity2.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveToNextScreen() {
        // Переход на следующий экран с передачей userId
        if (userId != null) {
            Intent intent = new Intent(MainActivity2.this, Finances.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish(); // Закрыть текущую активность
        } else {
            Toast.makeText(MainActivity2.this, "User ID is null", Toast.LENGTH_SHORT).show();
        }
    }

}
