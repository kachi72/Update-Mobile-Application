package com.systemtech.update;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;
import com.systemtech.update.helpers.NetworkStatus;
import com.systemtech.update.helpers.OfflineModeNavigator;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private View cyber, ai, softwareEng, network, dataScience, ui, offline, savedPreferences, help;
    private TextView cyberCount, aiCount, softwareCount, networkCount, dataCount, uiCount;
    private AlertDialog internetDialog;

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

        help.setOnClickListener(view -> showHelpDialog(MainActivity.this));

    }


     //   build the alert dialog to show when the help button is clicked
    public static void showHelpDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("App Functionalities");
        builder.setMessage("- This app delivers breaking IT-based news specifically tailored to 6 key domains\n" +
                "- Offline mode becomes available after the app downloads at least one article\n" +
                "- Long press on an article post to save it to Saved Articles\n" +
                "- In Saved Articles Mode, long press on an article to delete it from your Saved Articles\n" +
                "- In Offline Mode, you cannot view full articles, only the short description\n" +
                "                           Enjoy learning :)");
        builder.setNegativeButton("Dismiss", (dialogInterface, which) -> dialogInterface.dismiss());

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
    private void showInternetDialog() {
        if (internetDialog != null && internetDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.internet_dialog_title);
        builder.setMessage(R.string.internet_dialog_message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.internet_dialog_settings, (dialog, which) ->
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        builder.setNegativeButton(R.string.internet_dialog_retry, (dialog, which) -> {
            internetDialog = null;
            findViewById(R.id.main).post(this::checkForInternetConnectivity);
        });
        builder.setNeutralButton(R.string.internet_dialog_offline,
                (dialogInterface, which) -> offline());

        internetDialog = builder.create();
        internetDialog.setOnDismissListener(dialog -> internetDialog = null);
        internetDialog.show();
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
        help = findViewById(R.id.navHelp);
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
        OfflineModeNavigator.open(this);
    }

    public void savedPreferences(){
        Intent intent = new Intent(MainActivity.this, SavedPreferencesActivity.class);
        startActivity(intent);
    }

    /**
     * Checks whether Android has validated the active network for internet access.
     */
    private void checkForInternetConnectivity(){
        if (isFinishing() || isDestroyed()) {
            return;
        }

        if (!NetworkStatus.hasValidatedInternet(this)) {
            showInternetDialog();
        } else if (internetDialog != null && internetDialog.isShowing()) {
            internetDialog.dismiss();
        }
    }

}
