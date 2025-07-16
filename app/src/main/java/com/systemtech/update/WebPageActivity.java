package com.systemtech.update;

import static com.systemtech.update.adapters.ArticleAdapter.WEB_VIEW_URL;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
                // Run full internet check in background
                new Thread(() -> {
                    boolean hasInternet = hasRealInternetAccess();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (!hasInternet) {
                            showNoInternetMessage();
                        } else {
                            webView.setWebViewClient(new WebViewClient());
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.loadUrl(url);
                        }
                    });
                }).start();
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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WebPageActivity.this, "No internet connection available. Please check your network settings.",
                        Toast.LENGTH_LONG).show();

                // Optionally finish the activity or load a local error page
                finish(); // This will close the activity and return to previous screen
            }
        });


        // Alternative: Load a local HTML error page instead
        // webView.loadData("<html><body><h2>No Internet Connection</h2><p>Please check your network settings and try again.</p></body></html>", "text/html", "UTF-8");
    }
    
}