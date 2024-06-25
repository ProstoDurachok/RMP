package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {

    private DatabaseReference Database;
    private EditText editText, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Найти поля по их ID
        editText = findViewById(R.id.customEditText);
        editPassword = findViewById(R.id.editPassword);

        // Инициализация ссылки на базу данных
        Database = FirebaseDatabase.getInstance().getReference("User");

        findViewById(R.id.button).setOnClickListener(v -> {
            String text = editText.getText().toString();
            String password = editPassword.getText().toString();

            // Проверка введенных данных
            isValidCredentials(text, password);
        });
    }

    private void isValidCredentials(String email, String password) {
        Database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Users user = userSnapshot.getValue(Users.class);
                        if (user != null && user.getPassword().equals(password)) {
                            // Успешная авторизация
                            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                            intent.putExtra("text", email);
                            intent.putExtra("password", password);
                            startActivity(intent);
                            return;
                        }
                    }
                }
                // Неверные учетные данные
                Toast.makeText(MainActivity2.this, "Успешная авторизация", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Обработка ошибки
                Toast.makeText(MainActivity2.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
