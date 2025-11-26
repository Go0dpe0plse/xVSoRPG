package com.example.nakotest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout mainMenu = findViewById(R.id.main_menu);
        LinearLayout modeScreen = findViewById(R.id.start_mode_screen);
        LinearLayout settings = findViewById(R.id.settings);

        Button settingsBtn = findViewById(R.id.btnSettings);

        Button startBtn = findViewById(R.id.btn_start);
        Button exitBtn = findViewById(R.id.btn_exit);
        Button mode1 = findViewById(R.id.btn_mode_1);
        Button mode2 = findViewById(R.id.btn_mode_2);
        Button backBtn = findViewById(R.id.btn_back);

        RadioGroup aiGroup = findViewById(R.id.aiGroup);
        RadioButton easyBtn = findViewById(R.id.aiEasy);
        RadioButton mediumBtn = findViewById(R.id.aiMedium);
        RadioButton hardBtn = findViewById(R.id.aiHard);
        Button returnBtn = findViewById(R.id.btn_return_settings);

        startBtn.setOnClickListener(v -> {
            mainMenu.setVisibility(View.GONE);
            modeScreen.setVisibility(View.VISIBLE);
        });

        mode1.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        mode2.setOnClickListener(v -> {
            Intent intent = new Intent(this, RPGgameActivity.class);
            startActivity(intent);
        });

        settingsBtn.setOnClickListener(v -> {
            mainMenu.setVisibility(View.GONE);
            settings.setVisibility(View.VISIBLE);
        });

        backBtn.setOnClickListener(v -> {
            modeScreen.setVisibility(View.GONE);
            mainMenu.setVisibility(View.VISIBLE);
        });

        returnBtn.setOnClickListener(v -> {
            settings.setVisibility(View.GONE);
            mainMenu.setVisibility(View.VISIBLE);
        });

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int level = prefs.getInt("ai_level", 1);

        if (level == 0) easyBtn.setChecked(true);
        else if (level == 1) mediumBtn.setChecked(true);
        else hardBtn.setChecked(true);

        aiGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int value;
            if (checkedId == R.id.aiEasy) value = 0;
            else if (checkedId == R.id.aiMedium) value = 1;
            else value = 2;

            prefs.edit().putInt("ai_level", value).apply();
        });


        exitBtn.setOnClickListener(v -> finishAffinity());
    }
}
