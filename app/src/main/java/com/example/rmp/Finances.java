package com.example.rmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class Finances extends AppCompatActivity {
    private static final int ADD_CATEGORY_REQUEST = 1;
    private String currentUserUid;
    private ArrayList<FinanceItem> financeItems;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finances);

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

        Log.d("FinanceActivity", "Current User UUID: " + currentUserUid);

        findViewById(R.id.add1).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, add_category.class).putExtra("userId", currentUserUid), ADD_CATEGORY_REQUEST);
        });

        findViewById(R.id.add2).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, add_category2.class).putExtra("userId", currentUserUid), ADD_CATEGORY_REQUEST);
        });

        findViewById(R.id.add3).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, add_category3.class).putExtra("userId", currentUserUid), ADD_CATEGORY_REQUEST);
        });

        loadCategoriesFromDatabase();
        loadCategoriesFromDatabaseCoh();
        loadCategoriesFromDatabaseRas();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CATEGORY_REQUEST && resultCode == RESULT_OK) {
            loadCategoriesFromDatabase();
            loadCategoriesFromDatabaseCoh();
            loadCategoriesFromDatabaseRas();
        }
    }

    private void loadCategoriesFromDatabase() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference()
                .child("categories")
                .child(currentUserUid);

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                financeItems = new ArrayList<>();
                Log.d("Finances", "Snapshot children count: " + snapshot.getChildrenCount());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                    Log.d("Finances", "CategoryItem: " + categoryItem);

                    if (categoryItem != null) {
                        Log.d("Finances", "CategoryItem Name: " + categoryItem.getName());
                        Log.d("Finances", "CategoryItem Image URL: " + categoryItem.getImageUrl());
                        Log.d("Finances", "CategoryItem ID: " + categoryItem.getId());

                        if (currentUserUid.equals(categoryItem.getId()) && categoryItem.getImageUrl() != null && !categoryItem.getImageUrl().isEmpty()) {
                            financeItems.add(new FinanceItem(categoryItem.getName(), categoryItem.getAmount(), categoryItem.getImageUrl()));
                        }
                    }
                }
                Log.d("Finances", "Categories loaded: " + financeItems.size());
                populateLinearLayout(financeItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Finances.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategoriesFromDatabaseRas() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference()
                .child("categoriesRas")
                .child(currentUserUid);

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                financeItems = new ArrayList<>();
                Log.d("Finances", "Snapshot children count: " + snapshot.getChildrenCount());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CustomCategoryItem customCategoryItem = dataSnapshot.getValue(CustomCategoryItem.class);
                    Log.d("Finances", "CategoryItem: " + customCategoryItem);

                    if (customCategoryItem != null) {
                        Log.d("Finances", "CategoryItem Name: " + customCategoryItem.getName());
                        Log.d("Finances", "CategoryItem Image URL: " + customCategoryItem.getImageUrl());
                        Log.d("Finances", "CategoryItem ID: " + customCategoryItem.getUuid());

                        if (currentUserUid.equals(customCategoryItem.getUuid()) && customCategoryItem.getImageUrl() != null && !customCategoryItem.getImageUrl().isEmpty()) {
                            financeItems.add(new FinanceItem(customCategoryItem.getName(), customCategoryItem.getAmount(), customCategoryItem.getImageUrl()));
                        }
                    }
                }
                Log.d("Finances", "Categories loaded: " + financeItems.size());
                populateLinearLayoutRas(financeItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Finances.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAmountToDatabase(String categoryName, String amount) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference()
                .child("categories")
                .child(currentUserUid);

        categoryRef.orderByChild("name")
                .equalTo(categoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String categoryId = dataSnapshot.getKey();
                                try {
                                    if (!TextUtils.isEmpty(amount) && TextUtils.isDigitsOnly(amount)) {
                                        int amountValue = Integer.parseInt(amount);

                                        categoryRef.child(categoryId).child("amount").setValue(String.valueOf(amountValue))
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(Finances.this, "Amount saved successfully", Toast.LENGTH_SHORT).show();
                                                    updateFinanceItem(categoryName, String.valueOf(amountValue));
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(Finances.this, "Failed to save amount: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(Finances.this, "Invalid amount format", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NumberFormatException e) {
                                    Toast.makeText(Finances.this, "Invalid amount format", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(Finances.this, "Category not found", Toast.LENGTH_SHORT).show();
                            Log.e("Finances", "Category not found for name: " + categoryName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Finances.this, "Failed to save amount: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Finances", "Database error: " + error.getMessage());
                    }
                });
    }

    private void saveAmountToDatabaseCoh(String categoryName, String amount) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference()
                .child("categoriesCoh")
                .child(currentUserUid);

        categoryRef.orderByChild("name")
                .equalTo(categoryName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String categoryId = dataSnapshot.getKey();
                                try {
                                    if (!TextUtils.isEmpty(amount) && TextUtils.isDigitsOnly(amount)) {
                                        int amountValue = Integer.parseInt(amount);

                                        categoryRef.child(categoryId).child("amount").setValue(String.valueOf(amountValue))
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(Finances.this, "Amount saved successfully", Toast.LENGTH_SHORT).show();
                                                    updateFinanceItemCoh(categoryName, String.valueOf(amountValue));
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(Finances.this, "Failed to save amount: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(Finances.this, "Invalid amount format", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NumberFormatException e) {
                                    Toast.makeText(Finances.this, "Invalid amount format", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(Finances.this, "Category not found", Toast.LENGTH_SHORT).show();
                            Log.e("Finances", "Category not found for name: " + categoryName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Finances.this, "Failed to save amount: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Finances", "Database error: " + error.getMessage());
                    }
                });
    }

    private void updateFinanceItem(String categoryName, String newAmount) {
        LinearLayout linearLayout = findViewById(R.id.finance_list);

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View itemView = linearLayout.getChildAt(i);
            TextView nameView = itemView.findViewById(R.id.name);
            TextView amountView = itemView.findViewById(R.id.amount);

            String name = nameView.getText().toString();
            if (name.equals(categoryName)) {
                amountView.setText(newAmount);

                if (financeItems != null) {
                    for (FinanceItem item : financeItems) {
                        if (item.getName().equals(categoryName)) {
                            item.setAmount(newAmount);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    private void updateFinanceItemCoh(String categoryName, String newAmount) {
        LinearLayout linearLayout = findViewById(R.id.finance_list_coh);

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View itemView = linearLayout.getChildAt(i);
            TextView nameView = itemView.findViewById(R.id.name);
            TextView amountView = itemView.findViewById(R.id.amount);

            String name = nameView.getText().toString();
            if (name.equals(categoryName)) {
                amountView.setText(newAmount); // Обновляем отображаемое значение

                // Обновляем значение в объекте FinanceItem, если необходимо
                if (financeItems != null) {
                    for (FinanceItem item : financeItems) {
                        if (item.getName().equals(categoryName)) {
                            // Устанавливаем значение amount в FinanceItem как строку
                            item.setAmount(newAmount); // Предполагается, что у FinanceItem есть метод setAmount(String)
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    private void populateLinearLayout(ArrayList<FinanceItem> financeItems) {
        LinearLayout linearLayout = findViewById(R.id.finance_list);
        linearLayout.removeAllViews();
        Log.d("Finances", "Populating LinearLayout with " + financeItems.size() + " items");

        for (FinanceItem item : financeItems) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.your_layout_item, linearLayout, false);

            TextView nameView = itemView.findViewById(R.id.name);
            nameView.setText(item.getName());

            ImageView imageView = itemView.findViewById(R.id.image);
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.money1)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.money1); // Default image
            }

            TextView amountView = itemView.findViewById(R.id.amount);
            EditText amountEditText = itemView.findViewById(R.id.amountEditText);
            amountEditText.setVisibility(View.GONE); // Hide EditText by default

            amountView.setText(String.valueOf(item.getAmount()));
            amountView.setOnClickListener(v -> {
                amountView.setVisibility(View.GONE);
                amountEditText.setVisibility(View.VISIBLE);
                amountEditText.setText(String.valueOf(item.getAmount()));
            });

            amountEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String newAmount = amountEditText.getText().toString();
                    saveAmountToDatabase(item.getName(), newAmount);
                    amountEditText.setVisibility(View.GONE);
                    amountView.setVisibility(View.VISIBLE);
                    amountView.setText(newAmount);
                    return true;
                }
                return false;
            });

            linearLayout.addView(itemView);
        }
    }

    private void loadCategoriesFromDatabaseCoh() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference()
                .child("categoriesCoh")
                .child(currentUserUid);

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                financeItems = new ArrayList<>();
                Log.d("Finances", "Snapshot children count: " + snapshot.getChildrenCount());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryItemCoh categoryItemCoh = dataSnapshot.getValue(CategoryItemCoh.class);
                    Log.d("Finances", "CategoryItem: " + categoryItemCoh);

                    // Check if the category belongs to the current user
                    if (categoryItemCoh != null) {
                        Log.d("Finances", "CategoryItem Name: " + categoryItemCoh.getName());
                        Log.d("Finances", "CategoryItem Image URL: " + categoryItemCoh.getImageUrl());
                        Log.d("Finances", "CategoryItem ID: " + categoryItemCoh.getId());

                        if (currentUserUid.equals(categoryItemCoh.getId()) && categoryItemCoh.getImageUrl() != null && !categoryItemCoh.getImageUrl().isEmpty()) {
                            // Create FinanceItem considering the loaded amount
                            financeItems.add(new FinanceItem(categoryItemCoh.getName(), categoryItemCoh.getAmount(), categoryItemCoh.getImageUrl()));
                        }
                    }
                }
                Log.d("Finances", "Categories loaded: " + financeItems.size());
                populateLinearLayoutCoh(financeItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Finances.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateLinearLayoutCoh(ArrayList<FinanceItem> financeItems) {
        LinearLayout linearLayout = findViewById(R.id.finance_list_coh);
        linearLayout.removeAllViews();
        Log.d("Finances", "Populating LinearLayoutCoh with " + financeItems.size() + " items");

        for (FinanceItem item : financeItems) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.your_layout_item, linearLayout, false);

            TextView nameView = itemView.findViewById(R.id.name);
            nameView.setText(item.getName());

            ImageView imageView = itemView.findViewById(R.id.image);
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.money1)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.money1); // Default image
            }

            TextView amountView = itemView.findViewById(R.id.amount);
            EditText amountEditText = itemView.findViewById(R.id.amountEditText);
            amountEditText.setVisibility(View.GONE); // Hide EditText by default

            amountView.setText(String.valueOf(item.getAmount()));
            amountView.setOnClickListener(v -> {
                amountView.setVisibility(View.GONE);
                amountEditText.setVisibility(View.VISIBLE);
                amountEditText.setText(String.valueOf(item.getAmount()));
            });

            amountEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String newAmount = amountEditText.getText().toString();
                    saveAmountToDatabaseCoh(item.getName(), newAmount);
                    amountEditText.setVisibility(View.GONE);
                    amountView.setVisibility(View.VISIBLE);
                    amountView.setText(newAmount);
                    return true;
                }
                return false;
            });

            linearLayout.addView(itemView);
        }
    }


    private void populateLinearLayoutRas(ArrayList<FinanceItem> financeItems) {
        LinearLayout linearLayout = findViewById(R.id.finance_list_ras);
        linearLayout.removeAllViews();
        Log.d("Finances", "Populating LinearLayoutCoh with " + financeItems.size() + " items");

        for (FinanceItem item : financeItems) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.your_layout_item, linearLayout, false);

            TextView nameView = itemView.findViewById(R.id.name);
            nameView.setText(item.getName());

            ImageView imageView = itemView.findViewById(R.id.image);
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.money1)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.money1); // Default image
            }

            TextView amountView = itemView.findViewById(R.id.amount);
            EditText amountEditText = itemView.findViewById(R.id.amountEditText);
            amountEditText.setVisibility(View.GONE); // Hide EditText by default

            amountView.setText(String.valueOf(item.getAmount()));
            amountView.setOnClickListener(v -> {
                amountView.setVisibility(View.GONE);
                amountEditText.setVisibility(View.VISIBLE);
                amountEditText.setText(String.valueOf(item.getAmount()));
            });

            amountEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String newAmount = amountEditText.getText().toString();
                    saveAmountToDatabaseCoh(item.getName(), newAmount);
                    amountEditText.setVisibility(View.GONE);
                    amountView.setVisibility(View.VISIBLE);
                    amountView.setText(newAmount);
                    return true;
                }
                return false;
            });

            linearLayout.addView(itemView);
        }
    }
}
