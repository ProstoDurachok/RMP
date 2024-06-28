package com.example.rmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.SpannableString;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Zadachi extends AppCompatActivity {
    private EditText editText;
    private DatabaseReference databaseReference;
    private LinearLayout taskListLayout;
    private RelativeLayout rootLayout;
    private String currentUserUid;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zadachi);

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userId")) {
            currentUserUid = intent.getStringExtra("userId");
        } else {
            currentUserUid = sharedPreferences.getString("userId", null);

            if (currentUserUid == null) {
                currentUserUid = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", currentUserUid);
                editor.apply();
            }
        }

        databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("tasks")
            .child(currentUserUid);
        taskListLayout = findViewById(R.id.task_list_layout);
        rootLayout = findViewById(R.id.root_layout);

        editText = findViewById(R.id.edit_text);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String taskName = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(taskName)) {
                    addTaskToFirebase(taskName);
                    editText.setText("");
                    editText.setVisibility(View.GONE);
                    hideKeyboard(editText);
                }
                return true;
            }
            return false;
        });

        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(editText);
                editText.setVisibility(View.GONE);
                return false;
            }
        });

        findViewById(R.id.Plus).setOnClickListener(v -> {
            editText.setVisibility(View.VISIBLE);
            editText.requestFocus();
            showKeyboard(editText);
        });

        findViewById(R.id.zadachi).setOnClickListener(v -> {
            startActivity(new Intent(this, Zadachi.class));
        });
        findViewById(R.id.finance).setOnClickListener(v -> {
            startActivity(new Intent(this, Finances.class));
        });
        findViewById(R.id.poleznoe).setOnClickListener(v -> {
            startActivity(new Intent(this, Poleznoe.class));
        });
        findViewById(R.id.settings).setOnClickListener(v -> {
            startActivity(new Intent(this, Settings.class));
        });

        findViewById(R.id.delete_completed_button).setOnClickListener(v -> deleteCompletedTasksFromFirebase());

        loadTasksFromFirebase();
    }

    private void addTaskToFirebase(String taskName) {
        String taskId = databaseReference.push().getKey();
        Task newTask = new Task(taskId, taskName, false);
        if (taskId != null) {
            databaseReference.child(taskId).setValue(newTask);
        }
        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
    }

    private void loadTasksFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskListLayout.removeAllViews();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Task task = postSnapshot.getValue(Task.class);
                    if (task != null) {
                        addTaskToUI(task);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Zadachi.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addTaskToUI(Task task) {
        View taskView = getLayoutInflater().inflate(R.layout.task_item, taskListLayout, false);

        CheckBox checkBox = taskView.findViewById(R.id.checkb);
        TextView textView = taskView.findViewById(R.id.task_text);

        checkBox.setChecked(task.isChecked);
        updateTextView(task, textView);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.isChecked = isChecked;
            databaseReference.child(task.taskId).setValue(task);
            updateTextView(task, textView);
        });

        taskListLayout.addView(taskView);
    }

    private void updateTextView(Task task, TextView textView) {
        SpannableString spannableString = new SpannableString(task.taskName);
        if (task.isChecked) {
            spannableString.setSpan(new BlackStrikethroughSpan(), 0, spannableString.length(), 0);
        } else {
            textView.setText(task.taskName);
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Remove strikethrough flag
        }
        textView.setTextColor(Color.WHITE);
        textView.setText(spannableString);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void deleteCompletedTasksFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null && task.isChecked) {
                        databaseReference.child(task.taskId).removeValue();
                    }
                }
                Toast.makeText(Zadachi.this, "Completed tasks deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Zadachi.this, "Failed to delete completed tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
