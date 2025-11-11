package com.example.nakotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstancіeState) {
        super.onCreate(savedInstancіeState);
        setContentView(R.layout.activity_main);

        Button startBtn = findViewById(R.id.btn_start);
        Button exitBtn = findViewById(R.id.btn_exit);

        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        exitBtn.setOnClickListener(v -> finishAffinity());
    }
}
