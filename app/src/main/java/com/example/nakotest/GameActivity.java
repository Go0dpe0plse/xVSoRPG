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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class GameActivity extends Activity {

    private TextView resultText;
    private Button restartBtn, menuBtn;
    private LinearLayout gameLayout;
    private LinearLayout endScreen;
    private FrameLayout gridHolder;
    private GridLayout grid;

    private boolean xTurn = true;
    private boolean gameOver = false;
    private final Handler handler = new Handler();

    private ImageView[][] cells = new ImageView[3][3];
    private FrameLayout[][] containers = new FrameLayout[3][3];
    private Drawable xDrawable, oDrawable, plateDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // UI elements
        grid = findViewById(R.id.grid);
        endScreen = findViewById(R.id.end_screen);

        restartBtn = findViewById(R.id.btn_restart);
        menuBtn = findViewById(R.id.btn_menu);
        resultText = findViewById(R.id.resultText);

        xDrawable = getDrawable(R.drawable.x);
        oDrawable = getDrawable(R.drawable.o);
        plateDrawable = getDrawable(R.drawable.plate);

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

                // click
                cellContainer.setOnClickListener(v -> {
                    if (gameOver) return;
                    if (symbol.getTag() != null) return;

                    int animRes = xTurn ? R.drawable.xanim : R.drawable.oanim;
                    int finalResId = xTurn ? R.drawable.x : R.drawable.o;

                    AnimationDrawable animation = (AnimationDrawable) ContextCompat.getDrawable(this, animRes).mutate();
                    symbol.setImageDrawable(animation);
                    symbol.setVisibility(View.VISIBLE);
                    symbol.setTag(xTurn ? "X" : "O");
                    animation.start();

                    int totalDuration = 0;
                    for (int i = 0; i < animation.getNumberOfFrames(); i++) {
                        totalDuration += animation.getDuration(i);
                    }

                    handler.postDelayed(() -> {
                        if (!gameOver && symbol.getTag() != null)
                            symbol.setImageResource(finalResId);
                    }, totalDuration);

                    if (checkWinner()) {
                        showResult(xTurn ? "WIN X" : "WIN O");
                    } else if (isFull()) {
                        showResult("DRAW");
                    } else {
                        xTurn = !xTurn;
                    }
                });

                cells[row][col] = symbol;
                containers[row][col] = cellContainer;
                grid.addView(cellContainer);
            }
        }

        restartBtn.setOnClickListener(v -> resetBoard());
        menuBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private boolean checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (equal(cells[i][0], cells[i][1], cells[i][2])) return true;
            if (equal(cells[0][i], cells[1][i], cells[2][i])) return true;
        }
        return equal(cells[0][0], cells[1][1], cells[2][2]) ||
                equal(cells[0][2], cells[1][1], cells[2][0]);
    }

    private boolean equal(ImageView a, ImageView b, ImageView c) {
        if (a.getTag() == null || b.getTag() == null || c.getTag() == null)
            return false;
        return a.getTag().equals(b.getTag()) && a.getTag().equals(c.getTag());
    }

    private boolean isFull() {
        for (ImageView[] row : cells)
            for (ImageView cell : row)
                if (cell.getTag() == null)
                    return false;
        return true;
    }

    private void disableBoard() {
        for (FrameLayout[] row : containers)
            for (FrameLayout container : row)
                container.setEnabled(false);
    }

    private void showResult(String text) {
        gameOver = true;
        disableBoard();

        resultText.setText(text);
        endScreen.setVisibility(View.VISIBLE);

        // Анімація затемнення (0 → 1 за 350мс)
        endScreen.setAlpha(0f);
        endScreen.setVisibility(View.VISIBLE);
        endScreen.animate().alpha(1f).setDuration(350).start();
    }

    private void resetBoard() {
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

        // Ховаємо затемнення та кнопки назад
        endScreen.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> endScreen.setVisibility(View.GONE))
                .start();
    }
}
