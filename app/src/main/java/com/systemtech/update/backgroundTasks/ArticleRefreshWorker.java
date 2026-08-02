package com.systemtech.update.backgroundTasks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.systemtech.update.feeds.ArticleRepository;
import com.systemtech.update.feeds.FeedSource;
import com.systemtech.update.helpers.NetworkStatus;

import java.io.IOException;
import java.io.InterruptedIOException;

public final class ArticleRefreshWorker extends Worker {

    public static final String KEY_FEED_SOURCE = "feed_source";
    public static final String KEY_FAILURE_REASON = "failure_reason";
    public static final String TAG_REFRESH = "article_refresh";
    public static final String FAILURE_NO_INTERNET = "no_internet";
    public static final String FAILURE_TIMEOUT = "timeout";
    public static final String FAILURE_GENERIC = "generic";

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
            @NonNull FeedSource source
    ) {
        Data input = new Data.Builder()
                .putString(KEY_FEED_SOURCE, source.getKey())
                .build();

        return new OneTimeWorkRequest.Builder(ArticleRefreshWorker.class)
                .setInputData(input)
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
            return failure(FAILURE_GENERIC);
        }

        if (!NetworkStatus.hasValidatedInternet(getApplicationContext())) {
            Log.w(LOG_TAG, "Cannot refresh articles without validated internet access");
            return failure(FAILURE_NO_INTERNET);
        }

        try {
            int count = ArticleRepository.create(getApplicationContext()).refresh(source).size();
            Log.i(LOG_TAG, "Refreshed " + count + " articles for " + source.getKey());
            return Result.success();
        } catch (InterruptedIOException exception) {
            Log.e(LOG_TAG, "Feed request timed out for " + source.getKey(), exception);
            return failure(FAILURE_TIMEOUT);
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Unable to refresh " + source.getKey(), exception);
            if (!NetworkStatus.hasValidatedInternet(getApplicationContext())) {
                return failure(FAILURE_NO_INTERNET);
            }
            return getRunAttemptCount() + 1 < MAX_ATTEMPTS
                    ? Result.retry()
                    : failure(FAILURE_GENERIC);
        } catch (RuntimeException exception) {
            Log.e(LOG_TAG, "Unexpected refresh failure for " + source.getKey(), exception);
            return failure(FAILURE_GENERIC);
        }
    }

    private Result failure(@NonNull String reason) {
        Data output = new Data.Builder()
                .putString(KEY_FAILURE_REASON, reason)
                .build();
        return Result.failure(output);
    }
}
