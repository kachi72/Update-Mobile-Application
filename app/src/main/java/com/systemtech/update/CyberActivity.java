package com.systemtech.update;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.systemtech.update.adapters.ArticleAdapter;
import com.systemtech.update.backgroundTasks.ArticleRefreshWorker;
import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;
import com.systemtech.update.feeds.FeedSource;

import java.util.ArrayList;
import java.util.List;

public class CyberActivity extends AppCompatActivity {
    private static final String TAG = "CyberActivity";

    private RelativeLayout child;

    private RecyclerView recycler;
    private ArrayList<Article> articles;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cyber);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initViews();


        showLoadingIcon();

        initWorker();

    }

    private void initViews() {
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(this);
        recycler = findViewById(R.id.recycler);
        child = findViewById(R.id.child);
    }

    private void hideLoadingIcon() {
        child.setVisibility(View.GONE);
    }

    // function to show a loading icon while application fetches data from internet
    private void showLoadingIcon() {
        if (articles.isEmpty()){
            child.setVisibility(View.VISIBLE);
        }
    }

    // function to schedule the background task
    private void initWorker() {
        Constraints constraint = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest fetchDataRequest = ArticleRefreshWorker.createRequest(
                FeedSource.CYBER_SECURITY,
                constraint
        );

        WorkManager.getInstance(this).enqueue(fetchDataRequest);

        // observe the background task to be able to handle logic when it's done
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(fetchDataRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                // get the data from the database after it has been confirmed that the background work was successful
                if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    Log.d(TAG, "onChanged: inside observer for background task,task successful");
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    db.articleDao().getAllArticles("CyberSecurity").observe(CyberActivity.this, articles -> {
                       updateRecycler(articles);
                    });
                }
                else if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED){
                    Toast.makeText(CyberActivity.this, "Error fetching live news, check your internet connection and try again", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CyberActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    private void updateRecycler(List<Article> articles) {
        Log.d(TAG, "updateRecycler: updating recycler rn");

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setArticles(articles);
        adapter.notifyDataSetChanged();
        hideLoadingIcon();
    }
}
