package com.systemtech.update.helpers;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.systemtech.update.OfflineActivity;
import com.systemtech.update.R;
import com.systemtech.update.database.AppDatabase;

/**
 * Applies the cached-article requirement before opening Offline Mode.
 */
public final class OfflineModeNavigator {

    private OfflineModeNavigator() {
    }

    public static void open(AppCompatActivity activity) {
        AppExecutors executors = AppExecutors.getInstance();
        executors.diskIO().execute(() -> {
            AppDatabase database = AppDatabase.getInstance(activity.getApplicationContext());
            boolean hasCachedArticles = database.articleDao().hasAnyArticles();

            executors.mainThread().execute(() -> {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }

                if (!hasCachedArticles) {
                    BrandedToast.show(
                            activity,
                            R.string.offline_mode_requires_articles,
                            Toast.LENGTH_LONG
                    );
                    return;
                }

                activity.startActivity(new Intent(activity, OfflineActivity.class));
            });
        });
    }
}
