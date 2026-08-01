package com.systemtech.update;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;
import com.systemtech.update.helpers.AppExecutors;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private View cyber, ai, softwareEng, network, dataScience, ui, offline, savedPreferences;
    private TextView cyberCount, aiCount, softwareCount, networkCount, dataCount, uiCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setOnClickButtons();
        observeCachedArticleCounts();

        // initialize savedPreferences
        Utils.getInstance(this);


        // check if the device is connected to the internet
        checkForInternetConnectivity();


    }

    // set onClick listeners for all ui buttons
    private void setOnClickButtons(){
        cyber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cyber();
            }
        });

        ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ai();
            }
        });

        softwareEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                softwareEng();
            }
        });

        network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networking();
            }
        });

        dataScience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataScience();
            }
        });

        ui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ui();
            }
        });

        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offline();
            }
        });

        savedPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedPreferences();
            }
        });

    }


     //   build the alert dialog to show when the help button is clicked
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("App Functionalities");
        builder.setMessage("- This app delivers breaking IT-based news specifically tailored to 6 key domains\n" +
                "- Offline mode only works after you have connected to the internet at least once\n" +
                "- Long press on an article post to save it to Saved Articles\n" +
                "- In Saved Articles Mode, long press on an article to delete it from your Saved Articles\n" +
                "- In Offline Mode, you cannot view full articles, only the short description\n" +
                "                           Enjoy learning :)");
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // left empty to dismiss the dialog when clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // make sure the app is connected to internet if it was in background and now in foreground
    @Override
    protected void onResume() {
        super.onResume();

        checkForInternetConnectivity();
    }


    // shows an alert dialog to make sure device is connected to internet
    private void showInternetDialog(MainActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("This app requires internet access. Please connect to continue.");
        builder.setCancelable(false); // User cannot dismiss by tapping outside or pressing back
        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        });
        builder.setNegativeButton("Retry", (dialog, which) -> {
            checkForInternetConnectivity();
        });

        builder.setNeutralButton("Offline Mode", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                offline();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // initialize all ui elements
    public void initViews(){
        cyber = findViewById(R.id.btnCyber);
        ai = findViewById(R.id.btnAI);
        softwareEng = findViewById(R.id.btnSoftwareEng);
        network = findViewById(R.id.btnNetworking);
        dataScience = findViewById(R.id.btnDataScience);
        ui = findViewById(R.id.btnUI);
        offline = findViewById(R.id.btnOffline);
        savedPreferences = findViewById(R.id.btnSavedPreferences);
        cyberCount = findViewById(R.id.txtCyberCount);
        aiCount = findViewById(R.id.txtAiCount);
        softwareCount = findViewById(R.id.txtSoftwareCount);
        networkCount = findViewById(R.id.txtNetworkCount);
        dataCount = findViewById(R.id.txtDataCount);
        uiCount = findViewById(R.id.txtUiCount);
    }

    private void observeCachedArticleCounts() {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        db.articleDao().getAllArticles("CyberSecurity").observe(this,
                articles -> setArticleCount(cyberCount, articles));
        db.articleDao().getAllArticles("AI/ML").observe(this,
                articles -> setArticleCount(aiCount, articles));
        db.articleDao().getAllArticles("Software Engineering").observe(this,
                articles -> setArticleCount(softwareCount, articles));
        db.articleDao().getAllArticles("Networking").observe(this,
                articles -> setArticleCount(networkCount, articles));
        db.articleDao().getAllArticles("Data Science").observe(this,
                articles -> setArticleCount(dataCount, articles));
        db.articleDao().getAllArticles("UI/UX").observe(this,
                articles -> setArticleCount(uiCount, articles));
    }

    private void setArticleCount(TextView countView, List<Article> articles) {
        int count = articles == null ? 0 : articles.size();
        countView.setText(getResources().getQuantityString(R.plurals.article_count, count, count));
    }

    // TODO: 23/04/2025 add an offline mode //
    // TODO: 14/05/2025 add a notification system 
    // TODO: 24/04/2025 add a new category called general for general IT-related news //
    // TODO: 27/04/2025 set on long press listener in adapter to add articles to savedPreferences //
    // TODO: 06/05/2025 test when a network connection times out if the toast message shows //
    // TODO: 27/04/2025 handle logic for offline mode //
    // TODO: 11/05/2025 add a help and about button to home page showing controls and the rest //
    // TODO: 27/04/2025 add a category feature to article constructor and change the uses in background tasks //
    // TODO: 28/04/2025 create new list item for savedPreferences to show the category of the news //
    // TODO: 10/06/2025 networking and software engineering category needs to be fixed, body shows html code//
    // TODO: 10/06/2025 ui/ux category has no short description//

    public void cyber(){
        Intent intent = new Intent(MainActivity.this, CyberActivity.class);
        startActivity(intent);
    }

    public void ai(){
        Intent intent = new Intent(MainActivity.this, AiActivity.class);
        startActivity(intent);
    }

    public void softwareEng(){
        Intent intent = new Intent(MainActivity.this, SoftwareActivity.class);
        startActivity(intent);
    }

    public void networking(){
        Intent intent = new Intent(MainActivity.this, NetworkActivity.class);
        startActivity(intent);
    }

    public void dataScience(){
        Intent intent = new Intent(MainActivity.this, DataScienceActivity.class);
        startActivity(intent);
    }

    public void ui(){
        Intent intent = new Intent(MainActivity.this, UiActivity.class);
        startActivity(intent);
    }

    public void offline(){

        Log.d(TAG, "offline: inside offline method");

        AppExecutors executors = AppExecutors.getInstance();
        executors.diskIO().execute(() -> {
            Log.d(TAG, "offline: reading cached articles");
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<Article> articles = db.articleDao().getAllArticlesSync();

            executors.mainThread().execute(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (articles.isEmpty()) {
                    Toast.makeText(
                            getApplicationContext(),
                            "You need to connect to the Internet first, no saved articles",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    startActivity(new Intent(MainActivity.this, OfflineActivity.class));
                }
            });
        });

    }

    public void savedPreferences(){
        Intent intent = new Intent(MainActivity.this, SavedPreferencesActivity.class);
        startActivity(intent);
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        }
        return false;
    }

    /**
     * checks if host device has internet connectivity
     * @return true if the device has internet connectivity
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
     * check if the device is connected to the internet in background
     */
    private void checkForInternetConnectivity(){
        AppExecutors executors = AppExecutors.getInstance();
        executors.networkIO().execute(() -> {
            boolean isConnected = isConnectedToInternet(MainActivity.this);
            boolean hasInternet = hasRealInternetAccess();

            executors.mainThread().execute(() -> {
                if (!isFinishing() && !isDestroyed() && (!isConnected || !hasInternet)) {
                    showInternetDialog(MainActivity.this);
                }
            });
        });
    }

}
