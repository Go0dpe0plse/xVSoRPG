package com.example.nakotest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RPGMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rpg_game_main);

        Button btnStart = findViewById(R.id.btnStartGame);
        Button btnSkills = findViewById(R.id.btnSkills);
        Button btnReturn = findViewById(R.id.btnReturn);

        // Кнопка Start Game
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(RPGMainActivity.this, RPGgameActivity.class);
            startActivity(intent);
        });

        // Кнопка Skills
        btnSkills.setOnClickListener(v -> {
            Intent intent = new Intent(RPGMainActivity.this, Skills.class);
            startActivity(intent);
        });

        // Кнопка Return — повертає до вибору режиму
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(RPGMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
