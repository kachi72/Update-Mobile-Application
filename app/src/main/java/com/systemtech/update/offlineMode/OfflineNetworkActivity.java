package com.systemtech.update.offlineMode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.systemtech.update.R;
import com.systemtech.update.adapters.OfflineModeArticleAdapter;
import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;

import java.util.ArrayList;
import java.util.List;

public class OfflineNetworkActivity extends AppCompatActivity {

    private RelativeLayout child;

    private RecyclerView recycler;
    private List<Article> articles;

    private OfflineModeArticleAdapter adapter;

    private TextView noArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offline_network);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        hideNoArticleText();

        showLoadingIcon();

        initWorker();
    }


    // creating a worker thread for background database operations
    private void initWorker() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                articles = db.articleDao().getArticlesByCategory("Networking");
                if (articles.isEmpty()){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            hideLoadingIcon();
                            Toast.makeText(OfflineNetworkActivity.this, "There are no saved posts in the database for this category", Toast.LENGTH_LONG).show();
                            showNoArticleText();
                        }
                    });
                }
                else{
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            updateRecycler(articles);
                        }
                    });
                }
            }
        }).start();

    }

    private void showNoArticleText() {
        noArticle.setVisibility(View.VISIBLE);
    }

    private void hideNoArticleText(){
        noArticle.setVisibility(View.GONE);
    }

    private void updateRecycler(List<Article> articles) {
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setArticles(articles);
        adapter.notifyDataSetChanged();
        hideLoadingIcon();
    }

    private void hideLoadingIcon() {
        child.setVisibility(View.GONE);
    }

    private void showLoadingIcon() {
        if (articles.isEmpty()){
            child.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        articles = new ArrayList<>();
        adapter = new OfflineModeArticleAdapter(this);
        recycler = findViewById(R.id.recycler);
        child = findViewById(R.id.child);
        noArticle = findViewById(R.id.txtNoArticle);
    }
}