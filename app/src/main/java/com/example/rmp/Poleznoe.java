package com.example.rmp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.UUID;

public class Poleznoe extends AppCompatActivity {
    private LinearLayout articleContainer;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private ImageButton favoriteButtonAll;

    private List<Article> allArticles = new ArrayList<>();
    private List<Article> filteredArticles = new ArrayList<>();
    private String currentUserUid;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poleznoe);

        articleContainer = findViewById(R.id.articleContainer);
        searchView = findViewById(R.id.search);
        favoriteButtonAll = findViewById(R.id.favoriteButtonAll);

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
                .child("articles")
                .child(currentUserUid);

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
        addInitialArticles();
        loadArticlesFromFirebase();
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
            showFavoriteArticles();
        } else {
            showAllArticles();
        }
    }

    private void addInitialArticles() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    addArticleToFirebase("Что такое Биткоин?", "Статья от известного инвестора", "https://get.wallhere.com/photo/2560x1600-px-clear-sky-forest-landscape-pine-trees-road-sky-summer-1413157.jpg", "Битко́йн, или битко́ин[⇨] (от англ. Bitcoin, от bit — бит и coin — монета) — пиринговая платёжная система, использующая одноимённую единицу для учёта операций. Для обеспечения функционирования и защиты системы используются криптографические методы, но при этом вся информация о транзакциях между адресами системы доступна в открытом виде. Минимальная передаваемая величина (наименьшая величина дробления) — 10−8 биткойна — получила название «сато́ши» — в честь создателя Сатоси Накамото, хотя сам он использовал в таких случаях слово «цент»[1]. Электронный платёж между двумя сторонами происходит без посредников и необратим — нет механизма отмены подтверждённой операции (включая случаи, когда платёж был отправлен на ошибочный или несуществующий адрес, или когда транзакция была подписана закрытым ключом, который стал известен другим лицам). Средства никто не может заблокировать (арестовать), даже временно, за исключением владельца закрытого ключа (или лица, которому он стал известен). Но предусмотренная технология мультиподписи позволяет привлечь третью сторону (арбитра) и реализовать «обратимые транзакции». При помощи специального языка сценариев есть возможность реализовать и другие варианты умных контрактов[2][3][4], однако он не доступен из графического интерфейса и не полон по Тьюрингу, в отличие от поздних блокчейновых систем (см. Ethereum[5]). Разные авторы по-разному классифицируют биткойны. Чаще всего встречаются варианты: криптовалюта[6], виртуальная валюта[6][7][8], цифровая валюта[9][10], электронная наличность[11]. Биткойны могут использоваться для обмена на товары или услуги у продавцов, которые согласны их принимать. Обмен на обычные валюты происходит через онлайн-сервисы обмена цифровых валют, другие платёжные системы, обменные пункты или непосредственно между заинтересованными сторонами. Котировка биткойна зависит исключительно от баланса спроса и предложения, она никем не регулируется и не сдерживается. При этом никто не обязан принимать биткойны, то есть не существует механизма получить за них хоть что-нибудь, если по какой-то причине их откажутся покупать или принимать в оплату. Комиссия за проведение операций назначается отправителем добровольно. Размер комиссии влияет на приоритет при обработке транзакции. Обычно программа-клиент подсказывает рекомендуемый размер комиссии. Транзакции без комиссии возможны и также обрабатываются, однако не рекомендуются, поскольку время их обработки неизвестно и может быть довольно велико. Одна из главных особенностей системы — полная децентрализация: нет центрального администратора или какого-либо его аналога. Необходимым и достаточным элементом этой платёжной системы является базовая программа-клиент (имеет открытый исходный код). Запущенные на множестве компьютеров программы-клиенты соединяются между собой в одноранговую сеть, каждый узел которой равноправен и самодостаточен. Невозможно государственное или частное управление системой, в том числе изменение суммарного количества биткойнов. Заранее известны объём и время выпуска новых биткойнов, но распределяются они относительно случайно среди тех, кто использует своё оборудование для вычислений[12], результаты которых являются механизмом регулирования и подтверждения правомочности операций в системе «Биткойн» (см. метод доказательства выполнения работы[13]). Одним из следствий децентрализации является потенциальная возможность «двойного расходования», то есть передача одних и тех же биткойнов разным получателям. В обычных условиях от этого защищает включение транзакции в блокчейн. Но если контролировать более 50 % суммарной вычислительной мощности биткойн-сети, то существует теоретическая возможность «заменить» одну цепочку транзакций на другую[⇨].", false);
                    addArticleToFirebase("Что такое ДевОпс?", "Статья от известного работника", "https://get.wallhere.com/photo/2560x1600-px-clear-sky-forest-landscape-pine-trees-road-sky-summer-1413157.jpg", "DevOps (акроним от англ. development and operations) — методология автоматизации технологических процессов сборки, настройки и развёртывания программного обеспечения. Методология предполагает активное взаимодействие специалистов по разработке со специалистами по информационно-технологическому обслуживанию и взаимную интеграцию их технологических процессов друг в друга для обеспечения высокого качества программного продукта. Предназначена для эффективной организации создания и обновления программных продуктов и услуг. Основана на идее тесной взаимозависимости создания продукта и эксплуатации программного обеспечения, которая прививается команде как культура создания продукта. Организациям, которым необходимы частые выпуски программного обеспечения, может понадобиться DevOps, т.е. автоматизация технологических процессов сборки, настройки и развёртывания программного обеспечения. Дневной цикл выпусков ПО может быть гораздо более интенсивным у организаций, которые выпускают несколько разнонаправленных приложений. Методология фокусируется на стандартизации окружений разработки с целью быстрого переноса программного обеспечения через стадии жизненного цикла ПО, способствуя быстрому выпуску версий программного продукта. В идеале, системы автоматизации сборки и выпуска должны быть доступны всем разработчикам в любом окружении, и у разработчиков должен быть контроль над окружением разработки, а информационно-технологическая инфраструктура должна становиться более сфокусированной на приложении. Задача инженеров автоматизации технологических процессов сборки, настройки и развёртывания программного обеспечения (DevOps engineers) — сделать процессы разработки и поставки программного обеспечения согласованным с эксплуатацией, объединив их в единое целое с помощью инструментов автоматизации. Движение за автоматизацию технологических процессов сборки, настройки и развёртывания программного обеспечения (DevOps-движение) возникло в 2009 году и было призвано решить проблемы взаимодействия команд разработки и эксплуатации программных продуктов. В том же году в Бельгии была организована серия конференций «DevOps Days»[1][2]. Затем «DevOps-дни» проходили в различных городах и странах мира.", false);
                    addArticleToFirebase("Тренировка", "Интенсивная тренировка для здоровья", "https://get.wallhere.com/photo/2560x1600-px-clear-sky-forest-landscape-pine-trees-road-sky-summer-1413157.jpg", "Интенсивность в тренировках — это уровень усилий, которые вы прикладываете во время тренировки.  Например, вы бежите медленным темпом, ваши шаги легкие, и вы чувствуете, что можете поддерживать разговор во время бега, это тренировка с низкой интенсивностью. Если вы начинаете бежать быстрым темпом, то сердце бьется сильнее, вам становится тяжелее говорить, это более интенсивная тренировочная нагрузка.  Помимо степени интенсивности тренировки есть еще понятие «моторной плотности», которое относится к объему нагрузки за какое-то время.  Если вы фокусируетесь на выполнении большого количества повторений в течение определенного времени, но не прикладываете много усилий во время тренировки, значит, моторная плотность высокая, а интенсивность низкая.  Если повторений не так много, но вы берете тяжелые веса, получается обратная ситуация — низкая моторная плотность и высокая интенсивность.  Высокая интенсивность больше помогает развить силу, а моторная плотность — выносливость. Опытные спортсмены обычно стремятся сочетать и то, и другое. Так они развивают сразу несколько аспектов физической формы, добиваются рельефа и поддерживают разнообразие в тренировках. ", false);
                    addArticleToFirebase("Медитация", "Расслабляющая медитация", "https://get.wallhere.com/photo/2560x1600-px-clear-sky-forest-landscape-pine-trees-road-sky-summer-1413157.jpg", "Есть несколько навыков, благодаря которым вы сможете осознать, что идёте по верному пути и делаете всё правильно:  дыхание приобретает чёткий ритм, становится спокойным и глубоким; паузы между входом и выдохом длиннее, чем в обычном состоянии; мысли не «скачут», а словно «плывут» в голове, создавая ощущение внутреннего спокойствия и комфорта; во время медитации вас покидают тревога и растерянность, а после выхода из состояния вы становитесь более решительным и собранным; тело учится расслабляться. Вскоре вы сможете медитировать в любой позе, но даже если позу лотоса освоить не получится, вы приобретёте навык духовных практик, находясь в любом месте и положении; в теле появляется лёгкость, оно приобретает «невесомость», вы его почти не ощущаете. Иногда вначале люди отмечают обратный эффект: у них возникает ощущение тяжести в теле, которое постепенно проходит, а весь организм наполняется ощущением счастья и умиротворённости. Используя простые техники, вы сможете приблизиться к истинной сути медитации и понять её на практике. Конечно, любой начинающий сталкивается со сложностями, но не стоит разочаровываться: если вы будете регулярно тренироваться, у вас непременно всё получится.", false);
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
                        if (favoriteButtonAll.isSelected()) {
                            if (article.isFavorite()) {
                                addArticleToUI(article, postSnapshot.getKey());
                            }
                        } else {
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
