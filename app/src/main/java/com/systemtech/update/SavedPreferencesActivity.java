package com.systemtech.update;

import android.content.Intent;
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
import com.systemtech.update.helpers.OfflineModeNavigator;

import java.util.ArrayList;

public class SavedPreferencesActivity extends AppCompatActivity {

    private RecyclerView recycler;

    private TextView txtSavedCount;

    private View emptyState, homeNavigation, offlineNavigation, helpNavigation;

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
        emptyState = findViewById(R.id.emptyState);
        txtSavedCount = findViewById(R.id.txtSavedCount);
        homeNavigation = findViewById(R.id.navHome);
        offlineNavigation = findViewById(R.id.navOffline);
        helpNavigation = findViewById(R.id.navHelp);

        // load the articles from savedPreferences
        articles = Utils.getInstance(this).getUserFav();

        adapter = new SavedPreferencesArticleAdapter(this, this::handleEmptyList);
        recycler = findViewById(R.id.recycler);

        homeNavigation.setOnClickListener(view -> {
            Intent intent = new Intent(SavedPreferencesActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        offlineNavigation.setOnClickListener(view ->
                OfflineModeNavigator.open(SavedPreferencesActivity.this));
        helpNavigation.setOnClickListener(view ->
                MainActivity.showHelpDialog(SavedPreferencesActivity.this));

        updateRecycler();
        handleEmptyList();
    }

    /**
     * updates the recycler with articles fetched from the saved preferences
     */
    private void updateRecycler() {
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setArticles(articles);
    }

    /**
     *  display a text saying no saved articles if there are no saved articles of the user
     */
    private void handleEmptyList() {
        int savedCount = adapter == null ? articles.size() : adapter.getItemCount();
        txtSavedCount.setText(getResources().getQuantityString(
                R.plurals.saved_article_count, savedCount, savedCount));

        if (savedCount == 0){
            recycler.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
        else{
            recycler.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }
}
