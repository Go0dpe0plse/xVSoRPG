package com.example.nakotest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class GameActivity extends Activity {

    private boolean xTurn = true;
    private boolean gameOver = false;
    private final Handler handler = new Handler(); // ✅ глобальний handler для керування анімаціями
    private ImageView[][] cells = new ImageView[3][3];
    private FrameLayout[][] containers = new FrameLayout[3][3];
    private Drawable xDrawable, oDrawable, plateDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        xDrawable = getDrawable(R.drawable.x);
        oDrawable = getDrawable(R.drawable.o);
        plateDrawable = getDrawable(R.drawable.plate);

        GridLayout grid = findViewById(R.id.grid);
        int size = 250;

        // === створення поля ===
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                FrameLayout cellContainer = new FrameLayout(this);
                ImageView plate = new ImageView(this);
                ImageView symbol = new ImageView(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;
                cellContainer.setLayoutParams(params);

                plate.setImageDrawable(plateDrawable);
                symbol.setImageDrawable(null);

                cellContainer.addView(plate);
                cellContainer.addView(symbol);

                int r = row, c = col;

                // === клік по клітинці ===
                cellContainer.setOnClickListener(v -> {
                    if (gameOver) return;
                    if (symbol.getTag() != null) return; // клітинка вже зайнята

                    int animRes = xTurn ? R.drawable.xanim : R.drawable.oanim;
                    int finalResId = xTurn ? R.drawable.x : R.drawable.o;

                    // створюємо копію анімації
                    AnimationDrawable animation =
                            (AnimationDrawable) ContextCompat.getDrawable(this, animRes).mutate();

                    symbol.setImageDrawable(animation);
                    symbol.setVisibility(View.VISIBLE);
                    symbol.bringToFront();
                    symbol.setTag(xTurn ? "X" : "O");

                    animation.start();

                    int totalDuration = 0;
                    for (int i = 0; i < animation.getNumberOfFrames(); i++) {
                        totalDuration += animation.getDuration(i);
                    }

                    // ✅ Використовуємо глобальний handler (щоб потім можна було зупинити)
                    handler.postDelayed(() -> {
                        if (!gameOver && symbol.getTag() != null)
                            symbol.setImageResource(finalResId);
                    }, totalDuration);

                    // === перевірка перемоги ===
                    if (checkWinner()) {
                        Toast.makeText(this, "Переміг " + (xTurn ? "X" : "O"), Toast.LENGTH_SHORT).show();
                        gameOver = true;
                        disableBoard();
                    } else if (isFull()) {
                        Toast.makeText(this, "Нічия!", Toast.LENGTH_SHORT).show();
                        gameOver = true;
                    } else {
                        xTurn = !xTurn;
                    }
                });

                cells[row][col] = symbol;
                containers[row][col] = cellContainer;
                grid.addView(cellContainer);
            }
        }

        // === кнопка "Нова гра" ===
        Button restart = findViewById(R.id.btn_restart);
        restart.setOnClickListener(v -> resetBoard());

        // === кнопка "Головне меню" ===
        Button menu = findViewById(R.id.btn_menu);
        menu.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    // === перевірка перемоги ===
    private boolean checkWinner() {
        // горизонталі та вертикалі
        for (int i = 0; i < 3; i++) {
            if (equal(cells[i][0], cells[i][1], cells[i][2])) return true;
            if (equal(cells[0][i], cells[1][i], cells[2][i])) return true;
        }
        // діагоналі
        return equal(cells[0][0], cells[1][1], cells[2][2]) ||
                equal(cells[0][2], cells[1][1], cells[2][0]);
    }

    private boolean equal(ImageView a, ImageView b, ImageView c) {
        if (a.getTag() == null || b.getTag() == null || c.getTag() == null)
            return false;
        return a.getTag().equals(b.getTag()) && a.getTag().equals(c.getTag());
    }

    // === перевірка заповненості поля ===
    private boolean isFull() {
        for (ImageView[] row : cells)
            for (ImageView cell : row)
                if (cell.getTag() == null)
                    return false;
        return true;
    }

    // === блокування кліків ===
    private void disableBoard() {
        for (FrameLayout[] row : containers)
            for (FrameLayout container : row)
                container.setEnabled(false);
    }

    // === скидання гри ===
    private void resetBoard() {
        // ✅ скасувати всі заплановані анімації
        handler.removeCallbacksAndMessages(null);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                ImageView cell = cells[row][col];
                cell.setImageDrawable(null);
                cell.setTag(null);
                containers[row][col].setEnabled(true);
            }
        }
        xTurn = true;
        gameOver = false;
    }
}
