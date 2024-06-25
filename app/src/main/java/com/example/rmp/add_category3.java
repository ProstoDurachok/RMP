package com.example.rmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class add_category3 extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;

    private ImageView imageView;
    private EditText customEditText;
    private Uri imageUri;
    private Spinner spinnerCategory;
    private EditText amountEditText;


    private String uuid; // Переменная для хранения UUID текущего пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category3);

        imageView = findViewById(R.id.imageView);
        customEditText = findViewById(R.id.customEditText);
        spinnerCategory = findViewById(R.id.spinner_category); // Инициализируем Spinner
        amountEditText = findViewById(R.id.amountEditText);

        // Получаем UUID текущего пользователя из Intent
        Intent intent = getIntent();
        if (intent != null) {
            uuid = intent.getStringExtra("userId");
        } else {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        imageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                requestStoragePermission();
            }
        });

        findViewById(R.id.button).setOnClickListener(v -> saveCategory());

        // Загружаем категории из Firebase
        loadCategoriesCohFromDatabase();
    }

    private void loadCategoriesCohFromDatabase() {
        DatabaseReference categoriesCohRef = FirebaseDatabase.getInstance().getReference()
                .child("categoriesCoh")
                .child(uuid); // Используем uuid текущего пользователя

        categoriesCohRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> categoryNames = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryItemCoh categoryItemCoh = dataSnapshot.getValue(CategoryItemCoh.class);
                    if (categoryItemCoh != null) {
                        categoryNames.add(categoryItemCoh.getName());
                    }
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(add_category3.this,
                        android.R.layout.simple_spinner_item, categoryNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(add_category3.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCategory() {
        String name = customEditText.getText().toString();
        String amount = amountEditText.getText().toString();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        if (imageUri != null && !name.isEmpty() && !amount.isEmpty() && !selectedCategory.isEmpty()) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storageRef.child("images/" + name + ".jpg");
                UploadTask uploadTask = imageRef.putBytes(imageBytes);

                uploadTask.addOnFailureListener(e -> {
                    Toast.makeText(add_category3.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        CustomCategoryItem customCategoryItem = new CustomCategoryItem(name, imageUrl, amount, uuid);
                        saveCustomCategoryItemToDatabase(customCategoryItem);

                        // После сохранения, вызываем метод для обновления amount категории
                        updateCategoryAmount(selectedCategory, amount);
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a name, amount, select a category, and select an image", Toast.LENGTH_SHORT).show();
        }
    }



    private void saveCustomCategoryItemToDatabase(CustomCategoryItem customCategoryItem) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference()
                .child("categoriesRas")
                .child(customCategoryItem.getUuid()); // Используем uuid из объекта CustomCategoryItem

        categoryRef.push().setValue(customCategoryItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(add_category3.this, "Category saved successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(add_category3.this, "Failed to save category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCategoryAmount(String categoryName, String amountToSubtract) {
        DatabaseReference categoriesCohRef = FirebaseDatabase.getInstance().getReference()
                .child("categoriesCoh")
                .child(uuid);

        categoriesCohRef.orderByChild("name")
                .equalTo(categoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String categoryId = dataSnapshot.getKey();
                                CategoryItemCoh categoryItemCoh = dataSnapshot.getValue(CategoryItemCoh.class);
                                if (categoryItemCoh != null) {
                                    int currentAmount = Integer.parseInt(categoryItemCoh.getAmount());
                                    int amount = Integer.parseInt(amountToSubtract);
                                    int newAmount = currentAmount - amount;

                                    categoriesCohRef.child(categoryId).child("amount").setValue(String.valueOf(newAmount))
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(add_category3.this, "Amount updated successfully", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(add_category3.this, "Failed to update amount: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(add_category3.this, "Category not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(add_category3.this, "Failed to update amount: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Требуется разрешение на доступ к хранилищу для выбора изображения", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
