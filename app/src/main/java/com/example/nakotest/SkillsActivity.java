package com.example.nakotest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SkillsActivity extends Activity {

        LinearLayout overlay;
        TextView skillTitle, skillDesc, skillMana;
        Button btnUseSkill, btnClose, btnReturn;

        int manaCostSkill1 = 75;
        int manaCostSkill2 = 25;
        int manaCostSkill3 = 75;
        int manaCostSkill4 = 50;
        int manaCostSkill5 = 25;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_skills);

            overlay = findViewById(R.id.skill_overlay);
            skillTitle = findViewById(R.id.skill_title);
            skillDesc = findViewById(R.id.skill_description);
            skillMana = findViewById(R.id.skill_mana_cost);
            btnUseSkill = findViewById(R.id.btn_use_skill);
            btnClose = findViewById(R.id.btn_close_skill);
            btnReturn = findViewById(R.id.btn_return_skills);

            Button s1 = findViewById(R.id.skill1);
            Button s2 = findViewById(R.id.skill2);
            Button s3 = findViewById(R.id.skill3);
            Button s4 = findViewById(R.id.skill4);
            Button s5 = findViewById(R.id.skill5);

            // ПРИКЛАД — відкриття першого скіла
            s1.setOnClickListener(v -> openSkill("Time Stop",
                    "Змушує пропустити наступний хід суперника.",
                    manaCostSkill1));

            s2.setOnClickListener(v -> openSkill("Ice Shield",
                    "Знімає всі негативні еффекти здібностей суперника та дає імунітет на 2 раунди.",
                    manaCostSkill2));

            s3.setOnClickListener(v -> openSkill("Thunder Strike",
                    "Руйнує 1 поставлений знак на полі (може знищити, як свій поставлений знак, так і знак суперника).",
                    manaCostSkill3));

            s4.setOnClickListener(v -> openSkill("Sleeping Potion",
                    "Зменшує час ходу суперника до 5 секунд(на 3 раунди).",
                    manaCostSkill4));

            s5.setOnClickListener(v -> openSkill("Meditation",
                    "Регенерує 50 MP.",
                    manaCostSkill5));

            btnClose.setOnClickListener(v -> overlay.setVisibility(View.GONE));
            btnReturn.setOnClickListener(v -> finish());
        }

        private void openSkill(String title, String desc, int manaCost) {
            skillTitle.setText(title);
            skillDesc.setText(desc);
            skillMana.setText("Mana cost: " + manaCost);
            overlay.setVisibility(View.VISIBLE);
        }

}
