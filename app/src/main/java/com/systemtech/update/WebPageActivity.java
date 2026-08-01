package com.systemtech.update;

import static com.systemtech.update.adapters.ArticleAdapter.WEB_VIEW_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.systemtech.update.helpers.AppExecutors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebPageActivity extends AppCompatActivity {

    private static final String TAG = "WebPageActivity";

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "onCreate: Inside webPage Activity");

        webView = findViewById(R.id.webView);
        Intent intent = getIntent();

        if (intent != null) {
            String url = intent.getStringExtra(WEB_VIEW_URL);
            if (url != null) {
                AppExecutors executors = AppExecutors.getInstance();
                executors.networkIO().execute(() -> {
                    boolean hasInternet = hasRealInternetAccess();
                    if (!hasInternet) {
                        showNoInternetMessage();
                    } else {
                        executors.mainThread().execute(() -> {
                            if (isFinishing() || isDestroyed()) {
                                return;
                            }
                            webView.setWebViewClient(new WebViewClient());
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.loadUrl(url);
                        });
                    }
                });
            }
        }
    }

    /**
     * Checks if device is connected to internet
     * @return true if connected to internet
     */
    private boolean hasRealInternetAccess() {
        Log.d(TAG, "hasRealInternetAccess: Inside method to check for internet access");
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    (new URL("https://www.google.com").openConnection());
            urlConnection.setRequestProperty("User-Agent", "ConnectionTest");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(3000);
            urlConnection.connect();
            return (urlConnection.getResponseCode() == 200);
        } catch (IOException e) {
            Log.e(TAG, "hasRealInternetAccess: Internet check failed", e);
            return false;
        }
    }

    /**
     * Show error message when no internet connection is available
     */
    private void showNoInternetMessage() {
        Log.d(TAG, "showNoInternetMessage: inside method to show message for no internet");
        AppExecutors.getInstance().mainThread().execute(() -> {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(WebPageActivity.this, "No internet connection available. Please check your network settings.",
                        Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }
    
}
