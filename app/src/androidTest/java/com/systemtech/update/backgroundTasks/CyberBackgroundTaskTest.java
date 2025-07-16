package com.systemtech.update.backgroundTasks;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.ListenableWorker;
import androidx.work.testing.WorkManagerTestInitHelper;
import androidx.work.testing.TestListenableWorkerBuilder;

import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class CyberBackgroundTaskTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Initialize WorkManager for testing
        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build();
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config);
    }

    @Test
    public void testCyberBackgroundTask_runsSuccessfully_andStoresData() throws Exception {
        // Build the worker
        ListenableWorker worker = TestListenableWorkerBuilder.from(context, CyberBackgroundTask.class).build();

        // Run the worker synchronously
        ListenableWorker.Result result = worker.startWork().get(10, TimeUnit.SECONDS);

        // Assert the result is success
        assertThat(result).isEqualTo(ListenableWorker.Result.success());

        // Check that database now has articles
        AppDatabase db = AppDatabase.getInstance(context);
        List<Article> articles = db.articleDao().getArticlesByCategory("CyberSecurity");
        assertThat(articles).isNotEmpty();
    }
}

