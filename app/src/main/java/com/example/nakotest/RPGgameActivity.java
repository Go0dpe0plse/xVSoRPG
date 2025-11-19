package com.example.nakotest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class RPGgameActivity extends Activity {

    TextView currentPlayerText;
    TextView turnText;
    private TextView timerText;

    private int timeLeft = 10;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private TextView resultText;
    private Button restartBtn, menuBtn;
    private LinearLayout endScreen;

    private GridLayout grid;
    private boolean xTurn = true;
    private boolean gameOver = false;
    private boolean isPaused = false;

    private final Handler handler = new Handler();

    private ImageView[][] cells = new ImageView[4][4];
    private FrameLayout[][] containers = new FrameLayout[4][4];

    private Drawable xDrawable, oDrawable, plateDrawable;

    private Button pauseBtn;
    private LinearLayout pauseScreen;
    private Button continueBtn, pauseRestartBtn, pauseMenuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rpg_game_activity);

        currentPlayerText = findViewById(R.id.currentPlayerText);
        turnText = findViewById(R.id.currentPlayerText);
        timerText = findViewById(R.id.timerText);

        startTurnTimer();

        grid = findViewById(R.id.grid);
        endScreen = findViewById(R.id.end_screen);

        restartBtn = findViewById(R.id.btn_restart);
        menuBtn = findViewById(R.id.btn_menu);
        resultText = findViewById(R.id.resultText);

        xDrawable = getDrawable(R.drawable.x);
        oDrawable = getDrawable(R.drawable.o);
        plateDrawable = getDrawable(R.drawable.plate);

        int size = 200; // трохи менші клітинки для 4×4

        // === створюємо поле 4×4 ===
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {

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

                cellContainer.setOnClickListener(v -> {
                    if (gameOver || isPaused) return;
                    if (symbol.getTag() != null) return;

                    int animRes = xTurn ? R.drawable.xanim : R.drawable.oanim;
                    int finalResId = xTurn ? R.drawable.x : R.drawable.o;

                    AnimationDrawable animation = (AnimationDrawable)
                            ContextCompat.getDrawable(this, animRes).mutate();

                    symbol.setImageDrawable(animation);
                    symbol.setVisibility(View.VISIBLE);
                    symbol.setTag(xTurn ? "X" : "O");
                    animation.start();

                    int totalDuration = 0;
                    for (int i = 0; i < animation.getNumberOfFrames(); i++)
                        totalDuration += animation.getDuration(i);

                    handler.postDelayed(() -> {
                        if (!gameOver && symbol.getTag() != null)
                            symbol.setImageResource(finalResId);
                        startTurnTimer();
                    }, totalDuration);

                    if (checkWinner()) {
                        showResult(xTurn ? "WIN X" : "WIN O");
                    } else if (isFull()) {
                        showResult("DRAW");
                    } else {
                        xTurn = !xTurn;
                        updateTurnText();
                        startTurnTimer();
                    }
                });

                cells[row][col] = symbol;
                containers[row][col] = cellContainer;
                grid.addView(cellContainer);
            }
        }

        // Restart
        restartBtn.setOnClickListener(v -> resetBoard());

        // Меню
        menuBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, RPGMainActivity.class));
            finish();
        });

        // === Пауза ===
        pauseBtn = new Button(this);
        pauseBtn.setText("≡");
        pauseBtn.setTextSize(24f);
        pauseBtn.setTextColor(Color.WHITE);
        pauseBtn.setBackgroundColor(Color.parseColor("#444444"));

        FrameLayout.LayoutParams pauseParams = new FrameLayout.LayoutParams(
                120, 120, Gravity.TOP | Gravity.END);
        pauseParams.setMargins(0, 20, 20, 0);
        pauseBtn.setLayoutParams(pauseParams);
        ((FrameLayout)findViewById(R.id.root_layout)).addView(pauseBtn);

        pauseScreen = findViewById(R.id.pause_screen);
        continueBtn = findViewById(R.id.btn_continue);
        pauseRestartBtn = findViewById(R.id.btn_pause_restart);
        pauseMenuBtn = findViewById(R.id.btn_pause_menu);

        pauseBtn.setOnClickListener(v -> {
            isPaused = true;
            pauseBtn.setVisibility(View.GONE);
            pauseScreen.setVisibility(View.VISIBLE);
        });

        continueBtn.setOnClickListener(v -> {
            isPaused = false;
            pauseScreen.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
        });

        pauseRestartBtn.setOnClickListener(v -> {
            resetBoard();
            isPaused = false;
            pauseScreen.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
        });

        pauseMenuBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, RPGMainActivity.class));
            finish();
        });
    }

    // === Логіка для 4×4 ===
    private boolean checkWinner() {
        for (int i = 0; i < 4; i++) {
            // рядки
            if (equal(cells[i][0], cells[i][1], cells[i][2], cells[i][3])) return true;
            // колонки
            if (equal(cells[0][i], cells[1][i], cells[2][i], cells[3][i])) return true;
        }

        // діагоналі 4×4
        return equal(cells[0][0], cells[1][1], cells[2][2], cells[3][3]) ||
                equal(cells[0][3], cells[1][2], cells[2][1], cells[3][0]);
    }

    private boolean equal(ImageView a, ImageView b, ImageView c, ImageView d) {
        if (a.getTag() == null || b.getTag() == null || c.getTag() == null || d.getTag() == null)
            return false;
        return a.getTag().equals(b.getTag())
                && a.getTag().equals(c.getTag())
                && a.getTag().equals(d.getTag());
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
        turnText.setVisibility(View.GONE);

        pauseBtn.setVisibility(View.GONE);

        endScreen.setAlpha(0f);
        endScreen.animate().alpha(1f).setDuration(350).start();
    }

    private void resetBoard() {
        handler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacks(timerRunnable);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                ImageView cell = cells[row][col];
                cell.setImageDrawable(null);
                cell.setTag(null);
                containers[row][col].setEnabled(true);
            }
        }

        xTurn = true;
        gameOver = false;
        isPaused = false;

        endScreen.setVisibility(View.GONE);
        pauseScreen.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.VISIBLE);
        turnText.setVisibility(View.VISIBLE);

        updateTurnText();
        startTurnTimer();
    }

    private void startTurnTimer() {
        timeLeft = 10;
        timerText.setText(String.valueOf(timeLeft));

        if (timerRunnable != null)
            timerHandler.removeCallbacks(timerRunnable);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (gameOver || isPaused) return;

                timeLeft--;
                timerText.setText(String.valueOf(timeLeft));

                if (timeLeft <= 0) {
                    xTurn = !xTurn;
                    updateTurnText();
                    startTurnTimer();
                    return;
                }

                timerHandler.postDelayed(this, 1000);
            }
        };

        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void updateTurnText() {
        if (xTurn) {
            currentPlayerText.setText("Turn: X");
            currentPlayerText.setTextColor(Color.RED);
        } else {
            currentPlayerText.setText("Turn: O");
            currentPlayerText.setTextColor(Color.BLUE);
        }
    }
}
