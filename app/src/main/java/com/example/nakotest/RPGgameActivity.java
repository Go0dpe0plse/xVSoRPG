package com.example.nakotest;

import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RPGgameActivity extends Activity {

    // === AI складність ===
    public enum Difficulty { EASY, MEDIUM, HARD }
    private Difficulty aiDifficulty = Difficulty.EASY; // за замовчуванням
    private boolean aiThinking = false; // чи думає AI, блокує хід гравця

    // === UI елементи ===
    private TextView currentPlayerText; // показує поточного гравця
    private TextView turnText;          // дубль для ходу (можна об’єднати)
    private TextView timerText;         // таймер на хід
    private int timeLeft = 10;          // секунда для ходу
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private TextView resultText;        // текст кінця гри
    private Button restartBtn, menuBtn; // кнопки кінця гри
    private LinearLayout endScreen;     // екран кінця гри

    private GridLayout grid;            // ігрове поле 4×4
    private boolean xTurn = true;       // чи хід X
    private boolean gameOver = false;   // стан гри
    private boolean isPaused = false;   // чи пауза

    private final Handler handler = new Handler(); // для анімацій AI
    private ImageView[][] cells = new ImageView[4][4];       // символи X/O
    private FrameLayout[][] containers = new FrameLayout[4][4]; // контейнери клітинок

    private Drawable xDrawable, oDrawable, plateDrawable; // ресурси для X, O, фон

    // === Пауза ===
    private Button pauseBtn;
    private LinearLayout pauseScreen;
    private Button continueBtn, pauseRestartBtn, pauseMenuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rpg_game_activity);

        loadAIDifficulty(); // підвантажуємо останню складність AI

        // === Прив’язка UI ===
        currentPlayerText = findViewById(R.id.currentPlayerText);
        turnText = findViewById(R.id.currentPlayerText);
        timerText = findViewById(R.id.timerText);
        startTurnTimer(); // запускаємо таймер ходу

        grid = findViewById(R.id.grid);
        endScreen = findViewById(R.id.end_screen);
        restartBtn = findViewById(R.id.btn_restart);
        menuBtn = findViewById(R.id.btn_menu);
        resultText = findViewById(R.id.resultText);

        xDrawable = getDrawable(R.drawable.x);
        oDrawable = getDrawable(R.drawable.o);
        plateDrawable = getDrawable(R.drawable.plate);

        int size = 200; // розмір клітинки 4×4

        // === Створюємо поле 4×4 ===
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                FrameLayout cellContainer = new FrameLayout(this);
                ImageView plate = new ImageView(this);   // фон клітинки
                ImageView symbol = new ImageView(this);  // символ X або O

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;
                cellContainer.setLayoutParams(params);

                plate.setImageDrawable(plateDrawable);
                symbol.setImageDrawable(null);

                cellContainer.addView(plate);
                cellContainer.addView(symbol);

                int finalRow = row;
                int finalCol = col;
                // Клік по клітинці – хід гравця
                cellContainer.setOnClickListener(v -> playerMove(finalRow, finalCol, symbol));

                cells[row][col] = symbol;
                containers[row][col] = cellContainer;
                grid.addView(cellContainer);
            }
        }

        // === Кнопки рестарту та меню ===
        restartBtn.setOnClickListener(v -> resetBoard());
        menuBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // === Кнопка паузи ===
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

        // === Логіка паузи ===
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
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    // === Хід гравця ===
    private void playerMove(int row, int col, ImageView symbol) {
        if (gameOver || isPaused || !xTurn || aiThinking) return;
        if (symbol.getTag() != null) return; // клітинка зайнята

        int animRes = R.drawable.xanim;
        int finalRes = R.drawable.x;

        AnimationDrawable animation = (AnimationDrawable)
                ContextCompat.getDrawable(this, animRes).mutate();

        symbol.setImageDrawable(animation);
        symbol.setVisibility(View.VISIBLE);
        symbol.setTag("X");
        animation.start();

        int totalDuration = 0;
        for (int i = 0; i < animation.getNumberOfFrames(); i++)
            totalDuration += animation.getDuration(i);

        // Після анімації – передаємо хід AI
        handler.postDelayed(() -> {
            if (!gameOver && symbol.getTag() != null)
                symbol.setImageResource(finalRes);
            xTurn = false;
            aiThinking = true;
            updateTurnText();
            startTurnTimer();
            aiMove();
        }, totalDuration);
    }

    // === Перевірка переможця ===
    private boolean checkWinner() {
        for (int i = 0; i < 4; i++) {
            // рядки
            if (equal(cells[i][0], cells[i][1], cells[i][2], cells[i][3])) return true;
            // колонки
            if (equal(cells[0][i], cells[1][i], cells[2][i], cells[3][i])) return true;
        }
        // діагоналі
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
                if (cell.getTag() == null) return false;
        return true;
    }

    // === Блокування дошки ===
    private void disableBoard() {
        for (FrameLayout[] row : containers)
            for (FrameLayout container : row)
                container.setEnabled(false);
    }

    // === Показ результату гри ===
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

    // === Скидання поля ===
    private void resetBoard() {
        loadAIDifficulty();
        handler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacks(timerRunnable);

        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++) {
                ImageView cell = cells[row][col];
                cell.setImageDrawable(null);
                cell.setTag(null);
                containers[row][col].setEnabled(true);
            }

        xTurn = true;
        gameOver = false;
        isPaused = false;
        aiThinking = false;

        endScreen.setVisibility(View.GONE);
        pauseScreen.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.VISIBLE);
        turnText.setVisibility(View.VISIBLE);

        updateTurnText();
        startTurnTimer();
    }

    // === Таймер ходу ===
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
                    timerHandler.removeCallbacks(timerRunnable);
                    xTurn = !xTurn;
                    if (!xTurn) aiThinking = true;
                    updateTurnText();
                    if (!xTurn) handler.postDelayed(() -> aiMove(), 500);
                    else startTurnTimer();
                    return;
                }
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    // === AI ходи ===
    private void aiMove() {
        if (gameOver || isPaused) return;

        int[] move;
        switch (aiDifficulty) {
            case EASY: move = getRandomMove(); break;
            case MEDIUM: move = getMediumMove(); break;
            case HARD: move = getHardMove(); break;
            default: move = getRandomMove();
        }

        if (move == null) return;

        makeMove(move[0], move[1], "O", R.drawable.oanim, R.drawable.o);
    }

    // === Легкий AI ===
    private int[] getRandomMove() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++)
                if (cells[row][col].getTag() == null)
                    emptyCells.add(new int[]{row, col});
        if (emptyCells.isEmpty()) return null;
        return emptyCells.get(new Random().nextInt(emptyCells.size()));
    }

    // === Середній AI ===
    private int[] getMediumMove() {
        int[] winMove = findWinningMove("O");
        if (winMove != null && new Random().nextBoolean()) return winMove;
        int[] blockMove = findWinningMove("X");
        if (blockMove != null) return blockMove;
        return getRandomMove();
    }

    // === Складний AI ===
    private int[] getHardMove() {
        int[] winMove = findWinningMove("O");
        if (winMove != null) return winMove;
        int[] blockMove = findWinningMove("X");
        if (blockMove != null) return blockMove;
        if (cells[1][1].getTag() == null) return new int[]{1,1};
        if (cells[2][2].getTag() == null) return new int[]{2,2};
        return getRandomMove();
    }

    // === Перевірка виграшного ходу ===
    private int[] findWinningMove(String player) {
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++)
                if (cells[row][col].getTag() == null) {
                    cells[row][col].setTag(player);
                    boolean win = checkWinner();
                    cells[row][col].setTag(null);
                    if (win) return new int[]{row, col};
                }
        return null;
    }

    // === Виконання AI ходу з анімацією ===
    private void makeMove(int row, int col, String player, int animRes, int finalResId) {
        aiThinking = true;
        ImageView symbol = cells[row][col];
        AnimationDrawable animation = (AnimationDrawable)
                ContextCompat.getDrawable(this, animRes).mutate();

        symbol.setImageDrawable(animation);
        symbol.setVisibility(View.VISIBLE);
        symbol.setTag(player);
        animation.start();

        int totalDuration = 0;
        for (int i = 0; i < animation.getNumberOfFrames(); i++)
            totalDuration += animation.getDuration(i);

        handler.postDelayed(() -> {
            if (!gameOver && symbol.getTag() != null)
                symbol.setImageResource(finalResId);

            aiThinking = false;

            if (checkWinner()) showResult("WIN " + player);
            else if (isFull()) showResult("DRAW");
            else {
                xTurn = true;
                updateTurnText();
                startTurnTimer();
            }
        }, totalDuration);
    }

    // === Оновлення тексту ходу ===
    private void updateTurnText() {
        if (xTurn) {
            currentPlayerText.setText("Turn: X");
            currentPlayerText.setTextColor(Color.RED);
        } else {
            currentPlayerText.setText("Turn: O");
            currentPlayerText.setTextColor(Color.BLUE);
        }
    }

    // === Завантаження складності AI ===
    private void loadAIDifficulty() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int level = prefs.getInt("ai_level", 1); // 0 - Easy, 1 - Medium, 2 - Hard
        switch (level) {
            case 0: aiDifficulty = Difficulty.EASY; break;
            case 1: aiDifficulty = Difficulty.MEDIUM; break;
            case 2: aiDifficulty = Difficulty.HARD; break;
            default: aiDifficulty = Difficulty.EASY;
        }
    }
}
