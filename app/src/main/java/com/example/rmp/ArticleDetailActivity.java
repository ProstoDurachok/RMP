package com.example.rmp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class ArticleDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView articleImageView;
    private TextView contentTextView;
    private ImageButton closeButton;
    private DatabaseReference databaseReference;
    private String currentUserUid;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        titleTextView = findViewById(R.id.articleDetailTitle);
        descriptionTextView = findViewById(R.id.articleDetailDescription);
        articleImageView = findViewById(R.id.articleDetailImage);
        contentTextView = findViewById(R.id.articleDetailContent);
        closeButton = findViewById(R.id.close);

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

        closeButton.setOnClickListener(v -> {
            finish();
        });

        String articleId = getIntent().getStringExtra("articleId");

        if (articleId != null) {
            loadArticleDetails(articleId);
        }
    }

    private void loadArticleDetails(String articleId) {
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("articles")
                .child(currentUserUid)
                .child(articleId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                if (article != null) {
                    titleTextView.setText(article.getTitle());
                    descriptionTextView.setText(article.getDescription());
                    Glide.with(ArticleDetailActivity.this).load(article.getImageUrl()).into(articleImageView);
                    contentTextView.setText(article.getContent());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
