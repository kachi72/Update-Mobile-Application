package com.systemtech.update;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

public class OfflineActivity extends AppCompatActivity {

    private Button cyber, ai, softwareEng, network, dataScience,ui, savedPreferences, help;

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

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
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
        savedPreferences = findViewById(R.id.btnSavedPreferences);
        help = findViewById(R.id.btnHelp);
    }
}