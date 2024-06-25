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

public class Registration extends AppCompatActivity {
    private EditText emailText, passwordText;
    private DatabaseReference Database;
    private String USER_KEY = "User";
    private static final AtomicInteger keyGenerator = new AtomicInteger();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        /*findViewById(R.id.button).setOnClickListener(v -> {

            startActivity(new Intent(this, MainActivity.class));
             overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });*/
    }

    public void init()
    {

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        Database = FirebaseDatabase.getInstance().getReference(USER_KEY);

    }

    public void onClickSave (View view)
    {

        String id = String.valueOf(keyGenerator.incrementAndGet());
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        Users newUser = new Users(id,email,password);
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) )
        {
            Database.push().setValue(newUser);
            Toast.makeText(this, "Успех!", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

}