package com.example.rmp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.ArrayList;

public class Poleznoe extends AppCompatActivity {

    private LinearLayout articleContainer;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private ImageButton favoriteButtonAll;

    private List<Article> allArticles = new ArrayList<>();
    private List<Article> filteredArticles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poleznoe);

        articleContainer = findViewById(R.id.articleContainer);
        databaseReference = FirebaseDatabase.getInstance().getReference("articles");
        searchView = findViewById(R.id.search);
        favoriteButtonAll = findViewById(R.id.favoriteButtonAll);

        setupSearchView();
        setupFavoriteButton();

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

        // Добавить начальные данные в Firebase
        addInitialArticles();

        loadArticlesFromFirebase();

        // Setup click listener to close search view and keyboard when clicking outside
        findViewById(R.id.mainLayout).setOnClickListener(v -> {
            closeKeyboard();
            searchView.clearFocus();
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterArticles(newText);
                return true;
            }
        });

        // Close keyboard when the search view loses focus
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                closeKeyboard();
            }
        });
    }

    private void setupFavoriteButton() {
        favoriteButtonAll.setOnClickListener(this::onFavoriteButtonClick);
    }

    public void onFavoriteButtonClick(View view) {
        ImageButton favoriteButton = (ImageButton) view;
        boolean isFavoriteSelected = !favoriteButton.isSelected();
        favoriteButton.setSelected(isFavoriteSelected);

        if (isFavoriteSelected) {
            // Show only favorite articles
            showFavoriteArticles();
        } else {
            // Show all articles
            showAllArticles();
        }
    }

    private void addInitialArticles() {
        // Add initial data if it doesn't exist
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Add initial articles
                    addArticleToFirebase("Что такое Биткоин?", "Статья от известного инвестора", "https://get.wallhere.com/photo/2560x1600-px-clear-sky-forest-landscape-pine-trees-road-sky-summer-1413157.jpg", "Основной текст статьи о Биткоине.", false);
                    addArticleToFirebase("Что такое ДевОпс?", "Статья от известного работника", "https://get.wallhere.com/photo/2560x1600-px-clear-sky-forest-landscape-pine-trees-road-sky-summer-1413157.jpg", "Основной текст статьи о ДевОпсе.", false);
                    addArticleToFirebase("Тренировка", "Интенсивная тренировка для здоровья", "https://get.wallhere.com/photo/2560x1600-px-clear-sky-forest-landscape-pine-trees-road-sky-summer-1413157.jpg", "Основной текст статьи о тренировке.", false);
                    addArticleToFirebase("Медитация", "Расслабляющая медитация", "https://get.wallhere.com/photo/2560x1600-px-clear-sky-forest-landscape-pine-trees-road-sky-summer-1413157.jpg", "Основной текст статьи о медитации.", false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Poleznoe.this, "Failed to check initial articles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addArticleToFirebase(String title, String description, String imageUrl, String content, boolean favorite) {
        String articleId = databaseReference.push().getKey();
        Article newArticle = new Article(articleId, title, description, imageUrl, content, favorite);
        if (articleId != null) {
            databaseReference.child(articleId).setValue(newArticle);
        }
    }

    private void loadArticlesFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allArticles.clear();
                articleContainer.removeAllViews();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Article article = postSnapshot.getValue(Article.class);
                    if (article != null) {
                        allArticles.add(article);
                        // Add article to UI based on current filter state
                        if (favoriteButtonAll.isSelected()) {
                            // Show only favorite articles
                            if (article.isFavorite()) {
                                addArticleToUI(article, postSnapshot.getKey());
                            }
                        } else {
                            // Show all articles
                            addArticleToUI(article, postSnapshot.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Poleznoe.this, "Failed to load articles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterArticles(String query) {
        filteredArticles.clear();
        for (Article article : allArticles) {
            if (article.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredArticles.add(article);
            }
        }
        refreshArticleUI();
    }

    private void refreshArticleUI() {
        articleContainer.removeAllViews();
        for (Article article : filteredArticles) {
            addArticleToUI(article, article.getId());
        }
    }

    private void showFavoriteArticles() {
        filteredArticles.clear();
        for (Article article : allArticles) {
            if (article.isFavorite()) {
                filteredArticles.add(article);
            }
        }
        refreshArticleUI();
    }

    private void showAllArticles() {
        filteredArticles.clear();
        filteredArticles.addAll(allArticles);
        refreshArticleUI();
    }

    private void addArticleToUI(Article article, String articleId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View articleView = inflater.inflate(R.layout.article_item, articleContainer, false);

        CardView cardView = articleView.findViewById(R.id.articleCardView);
        TextView titleTextView = articleView.findViewById(R.id.articleTitle);
        TextView descriptionTextView = articleView.findViewById(R.id.articleDescription);
        ImageView articleImageView = articleView.findViewById(R.id.articleImage);
        CheckBox checkBoxFavorite = articleView.findViewById(R.id.checkBoxFavorite);

        titleTextView.setText(article.getTitle());
        descriptionTextView.setText(article.getDescription());
        Glide.with(this).load(article.getImageUrl()).into(articleImageView);
        checkBoxFavorite.setChecked(article.isFavorite()); // set checkbox state

        checkBoxFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update favorite state in Firebase when checkbox state changes
            databaseReference.child(articleId).child("favorite").setValue(isChecked);
        });

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(Poleznoe.this, ArticleDetailActivity.class);
            intent.putExtra("articleId", articleId);
            startActivity(intent);
        });

        articleContainer.addView(articleView);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            searchView.clearFocus();
        }
    }
}
