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

public class GameActivity extends Activity {

    // Текстові поля інтерфейсу: поточний гравець та таймер
    TextView currentPlayerText;
    TextView turnText;
    private TextView timerText;

    // Логіка таймера ходу
    private int timeLeft = 10;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    // Екран із результатом гри
    private TextView resultText;
    private Button restartBtn, menuBtn;
    private LinearLayout endScreen;

    // Сітка поля
    private GridLayout grid;
    private boolean xTurn = true;
    private boolean gameOver = false;

    // Анімації символів
    private final Handler handler = new Handler();
    private ImageView[][] cells = new ImageView[3][3];
    private FrameLayout[][] containers = new FrameLayout[3][3];

    // Зображення X, O та фону клітинки
    private Drawable xDrawable, oDrawable, plateDrawable;

    // Логіка паузи
    private Button pauseBtn;
    private LinearLayout pauseScreen;
    private Button continueBtn, pauseRestartBtn, pauseMenuBtn;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Ініціалізація текстових елементів
        currentPlayerText = findViewById(R.id.currentPlayerText);
        turnText = findViewById(R.id.currentPlayerText);
        timerText = findViewById(R.id.timerText);

        // Запуск таймера ходу
        startTurnTimer();

        // Ініціалізація UI елементів
        grid = findViewById(R.id.grid);
        endScreen = findViewById(R.id.end_screen);
        restartBtn = findViewById(R.id.btn_restart);
        menuBtn = findViewById(R.id.btn_menu);
        resultText = findViewById(R.id.resultText);

        // Завантаження ресурсів
        xDrawable = getDrawable(R.drawable.x);
        oDrawable = getDrawable(R.drawable.o);
        plateDrawable = getDrawable(R.drawable.plate);

        // Створення ігрового поля 3×3
        int size = 250;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {

                // Контейнер клітинки
                FrameLayout cellContainer = new FrameLayout(this);
                ImageView plate = new ImageView(this);
                ImageView symbol = new ImageView(this);

                // Параметри розміру клітинки
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;
                cellContainer.setLayoutParams(params);

                // Заповнюємо фон клітинки
                plate.setImageDrawable(plateDrawable);
                symbol.setImageDrawable(null);

                // Додаємо фон та символ у контейнер
                cellContainer.addView(plate);
                cellContainer.addView(symbol);

                // Обробка натискання на клітинку
                cellContainer.setOnClickListener(v -> {
                    if (gameOver || isPaused) return;
                    if (symbol.getTag() != null) return;

                    // Вибір анімації залежно від гравця
                    int animRes = xTurn ? R.drawable.xanim : R.drawable.oanim;
                    int finalResId = xTurn ? R.drawable.x : R.drawable.o;

                    // Включення анімації появи символу
                    AnimationDrawable animation = (AnimationDrawable) ContextCompat.getDrawable(this, animRes).mutate();
                    symbol.setImageDrawable(animation);
                    symbol.setVisibility(View.VISIBLE);
                    symbol.setTag(xTurn ? "X" : "O");
                    animation.start();

                    // Після завершення анімації ставим фінальне зображення
                    int totalDuration = 0;
                    for (int i = 0; i < animation.getNumberOfFrames(); i++) {
                        totalDuration += animation.getDuration(i);
                    }

                    handler.postDelayed(() -> {
                        if (!gameOver && symbol.getTag() != null)
                            symbol.setImageResource(finalResId);
                        startTurnTimer();
                    }, totalDuration);

                    // Перевірка на перемогу або нічию
                    if (checkWinner()) {
                        showResult(xTurn ? "WIN X" : "WIN O");
                    } else if (isFull()) {
                        showResult("DRAW");
                    } else {
                        // Зміна гравця
                        xTurn = !xTurn;
                        if (xTurn)
                            animateCurrentPlayer("Turn: X", Color.RED);
                        else
                            animateCurrentPlayer("Turn: O", Color.BLUE);

                        startTurnTimer();
                    }
                });

                // Зберігаємо клітинку в масиві
                cells[row][col] = symbol;
                containers[row][col] = cellContainer;

                // Додаємо клітинку в GridLayout
                grid.addView(cellContainer);
            }
        }

        // Кнопка рестарту гри
        restartBtn.setOnClickListener(v -> resetBoard());

        // Кнопка переходу в меню
        menuBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // === Кнопка паузи у правому верхньому куті ===
        pauseBtn = new Button(this);
        pauseBtn.setText("≡");
        pauseBtn.setTextSize(24f);
        pauseBtn.setTextColor(Color.WHITE);
        pauseBtn.setBackgroundColor(Color.parseColor("#444444"));

        FrameLayout.LayoutParams pauseParams = new FrameLayout.LayoutParams(
                120, 120, Gravity.TOP | Gravity.END);
        pauseParams.setMargins(0, 20, 20, 0);
        pauseBtn.setLayoutParams(pauseParams);

        // Додаємо кнопку на головний Layout
        ((FrameLayout)findViewById(R.id.root_layout)).addView(pauseBtn);

        // === Екран паузи та кнопки в ньому ===
        pauseScreen = findViewById(R.id.pause_screen);
        continueBtn = findViewById(R.id.btn_continue);
        pauseRestartBtn = findViewById(R.id.btn_pause_restart);
        pauseMenuBtn = findViewById(R.id.btn_pause_menu);

        // Відкрити паузу
        pauseBtn.setOnClickListener(v -> {
            isPaused = true;
            pauseBtn.setVisibility(View.GONE);
            pauseScreen.setVisibility(View.VISIBLE);
        });

        // Продовжити гру
        continueBtn.setOnClickListener(v -> {
            isPaused = false;
            pauseScreen.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
        });

        // Рестарт із екрана паузи
        pauseRestartBtn.setOnClickListener(v -> {
            resetBoard();
            isPaused = false;
            pauseScreen.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
        });

        // Вихід у меню
        pauseMenuBtn.setOnClickListener(v -> {
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
        turnText.setVisibility(View.GONE);

        pauseBtn.setVisibility(View.GONE);

        endScreen.setAlpha(0f);
        endScreen.setVisibility(View.VISIBLE);
        endScreen.animate().alpha(1f).setDuration(350).start();
    }

    private void resetBoard() {

        handler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacks(timerRunnable);

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

                    if (xTurn)
                        animateCurrentPlayer("Turn: X", Color.RED);
                    else
                        animateCurrentPlayer("Turn: O", Color.BLUE);

                    startTurnTimer();
                    return;
                }

                timerHandler.postDelayed(this, 1000);
            }
        };

        timerHandler.postDelayed(timerRunnable, 1000);
        if (gameOver || isPaused) return;
    }

    private void updateTurnText() {
        if (xTurn) {
            turnText.setText("Turn: X");
            turnText.setTextColor(0xFFFF4444);
        } else {
            turnText.setText("Turn: O");
            turnText.setTextColor(0xFF3A86FF);
        }
    }

    private void animateCurrentPlayer(String text, int color) {
        currentPlayerText.animate().alpha(0f).setDuration(250).withEndAction(() -> {
            currentPlayerText.setText(text);
            currentPlayerText.setTextColor(color);
            currentPlayerText.animate().alpha(1f).setDuration(250).start();
        }).start();
    }
}
