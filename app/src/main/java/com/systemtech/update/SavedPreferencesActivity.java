package com.systemtech.update;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.systemtech.update.adapters.SavedPreferencesArticleAdapter;
import com.systemtech.update.database.Article;

import java.util.ArrayList;

public class SavedPreferencesActivity extends AppCompatActivity {

    private RecyclerView recycler;

    private TextView txtNoArticle;

    private ArrayList<Article> articles;

    private SavedPreferencesArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_preferences);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        articles = new ArrayList<>();
        txtNoArticle = findViewById(R.id.txtNoArticle);

        // load the articles from savedPreferences
        articles = Utils.getInstance(this).getUserFav();

        adapter = new SavedPreferencesArticleAdapter(this);
        recycler = findViewById(R.id.recycler);

        handleEmptyList();
        updateRecycler();
    }

    /**
     * updates the recycler with articles fetched from the saved preferences
     */
    private void updateRecycler() {
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setArticles(articles);
        adapter.notifyDataSetChanged();
    }

    /**
     *  display a text saying no saved articles if there are no saved articles of the user
     */
    private void handleEmptyList() {
        if (articles.isEmpty()){
            recycler.setVisibility(View.GONE);
            txtNoArticle.setVisibility(View.VISIBLE);
        }
        else{
            recycler.setVisibility(View.VISIBLE);
            txtNoArticle.setVisibility(View.GONE);
        }
    }
}