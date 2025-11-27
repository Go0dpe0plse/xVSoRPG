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
        // Встановлюємо основний макет активності
        setContentView(R.layout.activity_main);

        // Отримуємо посилання на різні екрани/розділи меню
        LinearLayout mainMenu = findViewById(R.id.main_menu);       // Головне меню
        LinearLayout modeScreen = findViewById(R.id.start_mode_screen); // Екран вибору режиму гри
        LinearLayout settings = findViewById(R.id.settings);        // Екран налаштувань

        // Кнопка для відкриття налаштувань
        Button settingsBtn = findViewById(R.id.btnSettings);

        // Кнопки головного меню
        Button startBtn = findViewById(R.id.btn_start); // Почати гру
        Button exitBtn = findViewById(R.id.btn_exit);   // Вийти з програми

        // Кнопки вибору режиму гри
        Button mode1 = findViewById(R.id.btn_mode_1);   // Перший режим гри
        Button mode2 = findViewById(R.id.btn_mode_2);   // Другий режим гри

        // Кнопка повернення назад з режиму вибору
        Button backBtn = findViewById(R.id.btn_back);

        // Елементи для налаштування AI
        RadioGroup aiGroup = findViewById(R.id.aiGroup); // Група радіо кнопок для вибору складності
        RadioButton easyBtn = findViewById(R.id.aiEasy);
        RadioButton mediumBtn = findViewById(R.id.aiMedium);
        RadioButton hardBtn = findViewById(R.id.aiHard);
        Button returnBtn = findViewById(R.id.btn_return_settings); // Повернення з налаштувань до головного меню

        // Обробка натискання кнопки "Почати гру"
        startBtn.setOnClickListener(v -> {
            mainMenu.setVisibility(View.GONE);   // Ховаємо головне меню
            modeScreen.setVisibility(View.VISIBLE); // Показуємо екран вибору режиму
        });

        // Вибір першого режиму гри
        mode1.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class); // Створюємо намір для запуску GameActivity
            startActivity(intent); // Запускаємо гру
        });

        // Вибір другого режиму гри (RPG)
        mode2.setOnClickListener(v -> {
            Intent intent = new Intent(this, RPGgameActivity.class); // Намір для RPG гри
            startActivity(intent); // Запуск RPG гри
        });

        // Відкриття налаштувань з головного меню
        settingsBtn.setOnClickListener(v -> {
            mainMenu.setVisibility(View.GONE); // Ховаємо головне меню
            settings.setVisibility(View.VISIBLE); // Показуємо екран налаштувань
        });

        // Повернення з екрану вибору режиму до головного меню
        backBtn.setOnClickListener(v -> {
            modeScreen.setVisibility(View.GONE); // Ховаємо екран режимів
            mainMenu.setVisibility(View.VISIBLE); // Показуємо головне меню
        });

        // Повернення з налаштувань до головного меню
        returnBtn.setOnClickListener(v -> {
            settings.setVisibility(View.GONE); // Ховаємо налаштування
            mainMenu.setVisibility(View.VISIBLE); // Показуємо головне меню
        });

        // Отримуємо збережений рівень AI з SharedPreferences
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int level = prefs.getInt("ai_level", 1); // За замовчуванням Medium (1)

        // Встановлюємо відповідну радіо-кнопку при старті програми
        if (level == 0) easyBtn.setChecked(true);
        else if (level == 1) mediumBtn.setChecked(true);
        else hardBtn.setChecked(true);

        // Обробка зміни вибору складності AI
        aiGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int value;
            if (checkedId == R.id.aiEasy) value = 0;
            else if (checkedId == R.id.aiMedium) value = 1;
            else value = 2;

            // Зберігаємо вибір у SharedPreferences
            prefs.edit().putInt("ai_level", value).apply();
        });

        // Вихід з програми при натисканні кнопки "Вийти"
        exitBtn.setOnClickListener(v -> finishAffinity());
    }
}