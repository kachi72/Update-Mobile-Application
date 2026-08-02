package com.systemtech.update;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MILLIS = 5_000L;
    private static final String STATE_LAUNCH_TIME = "splash_launch_time";

    private View splashRoot;
    private TextView countdown;
    private long launchTime;
    private boolean hasOpenedMain;

    private final Runnable countdownTick = new Runnable() {
        @Override
        public void run() {
            if (isFinishing() || isDestroyed() || hasOpenedMain) {
                return;
            }

            long remainingMillis = launchTime - SystemClock.elapsedRealtime();
            if (remainingMillis <= 0L) {
                openMainActivity();
                return;
            }

            int remainingSeconds = (int) Math.ceil(remainingMillis / 1_000.0);
            countdown.setText(getString(R.string.splash_countdown_format, remainingSeconds));
            countdown.setContentDescription(getString(
                    R.string.splash_countdown_description,
                    remainingSeconds
            ));
            splashRoot.postDelayed(this, Math.min(1_000L, remainingMillis));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        splashRoot = findViewById(R.id.splashRoot);
        countdown = findViewById(R.id.txtSplashCountdown);
        ViewCompat.setOnApplyWindowInsetsListener(splashRoot, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        launchTime = savedInstanceState == null
                ? SystemClock.elapsedRealtime() + SPLASH_DURATION_MILLIS
                : savedInstanceState.getLong(
                        STATE_LAUNCH_TIME,
                        SystemClock.elapsedRealtime() + SPLASH_DURATION_MILLIS
                );
        countdownTick.run();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(STATE_LAUNCH_TIME, launchTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (splashRoot != null) {
            splashRoot.removeCallbacks(countdownTick);
        }
        super.onDestroy();
    }

    private void openMainActivity() {
        hasOpenedMain = true;
        startActivity(new Intent(this, MainActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
