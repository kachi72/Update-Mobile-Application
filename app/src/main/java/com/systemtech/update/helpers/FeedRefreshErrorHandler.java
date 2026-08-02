package com.systemtech.update.helpers;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkInfo;

import com.systemtech.update.R;
import com.systemtech.update.backgroundTasks.ArticleRefreshWorker;

public final class FeedRefreshErrorHandler {

    private FeedRefreshErrorHandler() {
    }

    public static boolean canStartRefresh(@NonNull AppCompatActivity activity) {
        if (NetworkStatus.hasValidatedInternet(activity)) {
            return true;
        }

        showAndClose(activity, R.string.feed_error_no_internet);
        return false;
    }

    public static void handleFailure(
            @NonNull AppCompatActivity activity,
            @NonNull WorkInfo workInfo
    ) {
        String reason = workInfo.getOutputData().getString(
                ArticleRefreshWorker.KEY_FAILURE_REASON
        );

        @StringRes int message = R.string.feed_error_generic;
        if (ArticleRefreshWorker.FAILURE_NO_INTERNET.equals(reason)) {
            message = R.string.feed_error_no_internet;
        } else if (ArticleRefreshWorker.FAILURE_TIMEOUT.equals(reason)) {
            message = R.string.feed_error_slow_connection;
        }

        showAndClose(activity, message);
    }

    private static void showAndClose(
            @NonNull AppCompatActivity activity,
            @StringRes int message
    ) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        BrandedToast.show(activity, message, Toast.LENGTH_LONG);
        activity.finish();
    }
}
