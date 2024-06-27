package com.example.rmp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView articleImageView;
    private TextView contentTextView;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        titleTextView = findViewById(R.id.articleDetailTitle);
        descriptionTextView = findViewById(R.id.articleDetailDescription);
        articleImageView = findViewById(R.id.articleDetailImage);
        contentTextView = findViewById(R.id.articleDetailContent);

        String articleId = getIntent().getStringExtra("articleId");

        if (articleId != null) {
            loadArticleDetails(articleId);
        }
    }

    private void loadArticleDetails(String articleId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("articles").child(articleId);

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
