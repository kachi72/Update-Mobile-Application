package com.systemtech.update.backgroundTasks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.systemtech.update.feeds.ArticleRepository;
import com.systemtech.update.feeds.FeedSource;

import java.io.IOException;

public final class ArticleRefreshWorker extends Worker {

    public static final String KEY_FEED_SOURCE = "feed_source";
    public static final String TAG_REFRESH = "article_refresh";

    private static final String LOG_TAG = "ArticleRefreshWorker";
    private static final int MAX_ATTEMPTS = 3;

    public ArticleRefreshWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParameters
    ) {
        super(context, workerParameters);
    }

    @NonNull
    public static OneTimeWorkRequest createRequest(
            @NonNull FeedSource source,
            @NonNull Constraints constraints
    ) {
        Data input = new Data.Builder()
                .putString(KEY_FEED_SOURCE, source.getKey())
                .build();

        return new OneTimeWorkRequest.Builder(ArticleRefreshWorker.class)
                .setInputData(input)
                .setConstraints(constraints)
                .addTag(TAG_REFRESH)
                .addTag(TAG_REFRESH + ":" + source.getKey())
                .build();
    }

    @NonNull
    @Override
    public Result doWork() {
        FeedSource source = FeedSource.fromKey(getInputData().getString(KEY_FEED_SOURCE));
        if (source == null) {
            Log.e(LOG_TAG, "Cannot refresh articles without a valid feed source");
            return Result.failure();
        }

        try {
            int count = ArticleRepository.create(getApplicationContext()).refresh(source).size();
            Log.i(LOG_TAG, "Refreshed " + count + " articles for " + source.getKey());
            return Result.success();
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Unable to refresh " + source.getKey(), exception);
            return getRunAttemptCount() + 1 < MAX_ATTEMPTS
                    ? Result.retry()
                    : Result.failure();
        } catch (RuntimeException exception) {
            Log.e(LOG_TAG, "Unexpected refresh failure for " + source.getKey(), exception);
            return Result.failure();
        }
    }
}
