package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Registration extends AppCompatActivity {

    private DatabaseReference userRef;
    private EditText emailText, passwordText;
    private static final String USERS_KEY = "User";
    private boolean isOutsideTouchRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init(); // Инициализация полей и базы данных

        // Установка слушателя для скрытия клавиатуры при касании вне поля ввода
        View mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard();
                    if (isOutsideTouchRegistered) {
                        v.performClick(); // Обеспечиваем выполнение обработчика клика, если это касание
                    }
                    isOutsideTouchRegistered = true; // Разрешаем регистрацию только после первого касания
                }
                return false;
            }
        });

        // Добавляем слушатели для EditText полей для восстановления регистрации
        emailText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isOutsideTouchRegistered = false; // Отменяем регистрацию при касании поля ввода
                return false;
            }
        });
        passwordText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isOutsideTouchRegistered = false; // Отменяем регистрацию при касании поля ввода
                return false;
            }
        });
    }

    private void init() {
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        userRef = FirebaseDatabase.getInstance().getReference().child(USERS_KEY);
    }

    public void onClickSave(View view) {
        String id = UUID.randomUUID().toString();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        // Проверка на корректность email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Введите корректный адрес электронной почты", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка на сложность пароля
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов, включая цифры и буквы", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка на совпадение email
        checkForDuplicateEmail(email, () -> {
            Users newUser = new Users(id, email, password);

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && isOutsideTouchRegistered) {
                userRef.child(id).setValue(newUser); // Сохранение пользователя с UUID в Firebase
                Toast.makeText(this, "Успешная регистрация!", Toast.LENGTH_SHORT).show();

                // После успешной регистрации переход на другой экран (например, авторизации)
                startActivity(new Intent(this, MainActivity2.class));
                finish();
            } else {
                Toast.makeText(this, "Заполните все поля или нажмите на поле ввода для восстановления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод для проверки корректности email
    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Метод для проверки сложности пароля
    private boolean isValidPassword(String password) {
        // Пример проверки: пароль должен содержать минимум 6 символов, включая цифры и буквы
        return password.length() >= 6 && containsDigit(password) && containsLetter(password);
    }

    // Вспомогательные методы для проверки наличия цифр и букв в пароле
    private boolean containsDigit(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLetter(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    // Метод для проверки дублирования email
    private void checkForDuplicateEmail(String email, Runnable onSuccess) {
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists
                    Toast.makeText(Registration.this, "Этот email уже зарегистрирован", Toast.LENGTH_SHORT).show();
                } else {
                    // Email does not exist, proceed with registration
                    onSuccess.run();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Registration.this, "Ошибка при проверке email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод для скрытия клавиатуры
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
