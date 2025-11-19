package com.example.nakotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout mainMenu = findViewById(R.id.main_menu);
        LinearLayout modeScreen = findViewById(R.id.start_mode_screen);

        Button startBtn = findViewById(R.id.btn_start);
        Button exitBtn = findViewById(R.id.btn_exit);
        Button settingsBtn = findViewById(R.id.btnSettings);
        Button mode3 = findViewById(R.id.btn_mode_3);
        Button mode4 = findViewById(R.id.btn_mode_4);
        Button backBtn = findViewById(R.id.btn_back);

        startBtn.setOnClickListener(v -> {
            mainMenu.setVisibility(View.GONE);
            modeScreen.setVisibility(View.VISIBLE);
        });

        mode3.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        mode4.setOnClickListener(v -> {
            Intent intent = new Intent(this, RPGMainActivity.class);
            startActivity(intent);
        });

        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> {
            modeScreen.setVisibility(View.GONE);
            mainMenu.setVisibility(View.VISIBLE);
        });

        exitBtn.setOnClickListener(v -> finishAffinity());
    }
}
