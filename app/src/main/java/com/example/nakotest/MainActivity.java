package com.example.nakotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstancіeState) {
        super.onCreate(savedInstancіeState);
        setContentView(R.layout.activity_main);

        // Головне меню
        LinearLayout mainMenu = findViewById(R.id.main_menu);

        // Екран вибору режимів
        LinearLayout modeScreen = findViewById(R.id.start_mode_screen);

        // Кнопки
        Button startBtn = findViewById(R.id.btn_start);
        Button exitBtn = findViewById(R.id.btn_exit);
        Button mode3 = findViewById(R.id.btn_mode_3);
        Button mode4 = findViewById(R.id.btn_mode_4);
        Button backBtn = findViewById(R.id.btn_back);

        // Показ екрану режимів
        startBtn.setOnClickListener(v -> {
            mainMenu.setVisibility(View.GONE);
            modeScreen.setVisibility(View.VISIBLE);
        });

        // Game 3×3 → відкриває GameActivity
        mode3.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        // Game 4×4 — поки не робимо (додамо пізніше)
        mode4.setOnClickListener(v -> {
            Intent intent = new Intent(this, RPGMainActivity.class);
            startActivity(intent);
        });

        // Повернутись назад
        backBtn.setOnClickListener(v -> {
            modeScreen.setVisibility(View.GONE);
            mainMenu.setVisibility(View.VISIBLE);
        });

        exitBtn.setOnClickListener(v -> finishAffinity());
    }
}
