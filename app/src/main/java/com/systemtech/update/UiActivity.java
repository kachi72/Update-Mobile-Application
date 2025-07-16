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
import com.systemtech.update.backgroundTasks.UiBackgroundTask;
import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;

import java.util.ArrayList;
import java.util.List;

public class UiActivity extends AppCompatActivity {

    private static final String TAG = "UiActivity";

    RecyclerView recycler;
    ArticleAdapter adapter;
    RelativeLayout child;
    ArrayList<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ui);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recycler = findViewById(R.id.recycler);
        child = findViewById(R.id.child);
        adapter = new ArticleAdapter(this);
        articles = new ArrayList<>();

        showLoadingIcon();

        initWorker();
    }

    private void showLoadingIcon() {
        if (articles.isEmpty()){
            child.setVisibility(View.VISIBLE);
        }
    }

    private void initWorker() {
        Constraints constraint = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest fetchDataRequest = new OneTimeWorkRequest.Builder(UiBackgroundTask.class)
                .setConstraints(constraint)
                .addTag("fetchData")
                .build();

        WorkManager.getInstance(this).enqueue(fetchDataRequest);

        // observe the background task to be able to handle logic when it's done
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(fetchDataRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                // get the data from the database after it has been confirmed that the background work was successful
                if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    Log.d(TAG, "onChanged: inside observer for background task,task successful");
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    db.articleDao().getAllArticles("UI/UX").observe(UiActivity.this, articles -> {
                        updateRecycler(articles);
                    });
                }
                else if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED){
                    Toast.makeText(UiActivity.this, "Error fetching live news, check your internet connection and try again", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UiActivity.this, MainActivity.class);
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

    private void hideLoadingIcon() {
        child.setVisibility(View.GONE);
    }
}