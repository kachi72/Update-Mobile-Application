package com.systemtech.update;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button cyber, ai, softwareEng, network, dataScience, ui, offline, savedPreferences, help;

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

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
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
        help = findViewById(R.id.btnHelp);
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

        // run database operations in the background
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "run: about to run database operation");
                AppDatabase dp = AppDatabase.getInstance(getApplicationContext());
                List<Article> articles = new ArrayList<>();
                articles = dp.articleDao().getAllArticlesSync();

                Log.d(TAG, "run: done w database operation. No of articles: " + articles.size());
                if (articles.isEmpty()){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: back on main thread");
                            Toast.makeText(getApplicationContext(), "You need to connect to the Internet first, no saved articles", Toast.LENGTH_LONG).show();
//                            showInternetDialog(MainActivity.this);
                        }
                    });
                }
                else{
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                                Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
                                startActivity(intent);
                        }
                    });

                }
            }
        }).start();

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
        new Thread(() -> {
            boolean isConnected = isConnectedToInternet(MainActivity.this);
            boolean hasInternet = hasRealInternetAccess();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (!isConnected || !hasInternet) {
                    showInternetDialog(MainActivity.this);
                }
            });

        }).start();
    }

}