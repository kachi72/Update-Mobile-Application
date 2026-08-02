package com.systemtech.update.backgroundTasks;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ArticleRefreshWorkerTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build();
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config);
    }

    @Test
    public void doWork_failsWithoutFeedSourceInput() throws Exception {
        ListenableWorker worker = TestListenableWorkerBuilder
                .from(context, ArticleRefreshWorker.class)
                .build();

        ListenableWorker.Result result = worker.startWork().get(10, TimeUnit.SECONDS);

        assertThat(result).isEqualTo(ListenableWorker.Result.failure());
    }
}
