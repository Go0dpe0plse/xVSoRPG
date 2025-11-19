package com.example.nakotest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RadioGroup aiGroup = findViewById(R.id.aiGroup);
        RadioButton easyBtn = findViewById(R.id.aiEasy);
        RadioButton mediumBtn = findViewById(R.id.aiMedium);
        RadioButton hardBtn = findViewById(R.id.aiHard);
        Button returnBtn = findViewById(R.id.btn_return_settings);

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

        returnBtn.setOnClickListener(v -> finish());
    }
}
