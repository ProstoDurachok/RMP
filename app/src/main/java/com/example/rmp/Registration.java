package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.atomic.AtomicInteger;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Registration extends AppCompatActivity {
    // Добавьте переменную для хранения базы данных пользователей
    private DatabaseReference userRef;
    private EditText emailText, passwordText;
    private static final String USERS_KEY = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
    }

    public void init() {
        // Инициализация полей ввода и ссылки на базу данных
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        userRef = FirebaseDatabase.getInstance().getReference().child(USERS_KEY);
    }

    public void onClickSave(View view) {
        String id = UUID.randomUUID().toString(); // Генерация UUID
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        Users newUser = new Users(id, email, password);

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            userRef.child(id).setValue(newUser); // Сохранение пользователя с UUID в Firebase
            Toast.makeText(this, "Успех!", Toast.LENGTH_SHORT).show();

            // После успешной регистрации переходите на экран авторизации
            startActivity(new Intent(this, MainActivity2.class));
            finish(); // Закрыть текущую активность, чтобы пользователь не мог вернуться назад
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }
}
