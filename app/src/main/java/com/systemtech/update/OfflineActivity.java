package com.systemtech.update;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.systemtech.update.offlineMode.OfflineAiActivity;
import com.systemtech.update.offlineMode.OfflineCyberActivity;
import com.systemtech.update.offlineMode.OfflineDataScienceActivity;
import com.systemtech.update.offlineMode.OfflineNetworkActivity;
import com.systemtech.update.offlineMode.OfflineSoftwareActivity;
import com.systemtech.update.offlineMode.OfflineUiActivity;
import com.systemtech.update.database.AppDatabase;
import com.systemtech.update.database.Article;

import java.util.List;

public class OfflineActivity extends AppCompatActivity {

    private View cyber, ai, softwareEng, network, dataScience, ui, home, savedPreferences;
    private TextView cyberCount, aiCount, softwareCount, networkCount, dataCount, uiCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offline);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setOnClickButtons();
        observeCachedArticleCounts();
    }

    public void cyber(){
        Intent intent = new Intent(OfflineActivity.this, OfflineCyberActivity.class);
        startActivity(intent);
    }

    public void ai(){
        Intent intent = new Intent(OfflineActivity.this, OfflineAiActivity.class);
        startActivity(intent);
    }

    public void softwareEng(){
        Intent intent = new Intent(OfflineActivity.this, OfflineSoftwareActivity.class);
        startActivity(intent);
    }

    public void networking(){
        Intent intent = new Intent(OfflineActivity.this, OfflineNetworkActivity.class);
        startActivity(intent);
    }

    public void dataScience(){
        Intent intent = new Intent(OfflineActivity.this, OfflineDataScienceActivity.class);
        startActivity(intent);
    }

    public void ui(){
        Intent intent = new Intent(OfflineActivity.this, OfflineUiActivity.class);
        startActivity(intent);
    }

    public void savedPreferences(){
        Intent intent = new Intent(OfflineActivity.this, SavedPreferencesActivity.class);
        startActivity(intent);
    }

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

        savedPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedPreferences();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OfflineActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OfflineActivity.this);
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

    // initialize all ui elements
    public void initViews(){
        cyber = findViewById(R.id.btnCyber);
        ai = findViewById(R.id.btnAI);
        softwareEng = findViewById(R.id.btnSoftwareEng);
        network = findViewById(R.id.btnNetworking);
        dataScience = findViewById(R.id.btnDataScience);
        ui = findViewById(R.id.btnUI);
        home = findViewById(R.id.navHome);
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
}
