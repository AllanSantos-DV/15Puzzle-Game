package com.allan.puzzlegame;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView lblTimer, lblMoveCounter;
    private Button btnStart, btnReset;
    private FrameLayout overlay;
    private TextView txtOverlayMessage;

    private final List<String> targetWords = Arrays.asList("MATO", "AMOR", "COCA", "UVA");
    private final List<Button> puzzleButtons = new ArrayList<>();
    private final HashMap<Button, int[]> buttonPositions = new HashMap<>();
    private int emptyRow = 3, emptyCol = 3;
    private int elapsedTime = 0, moveCount = 0;

    private final Handler handler = new Handler();
    private boolean isGameRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        initializePuzzleButtons();
        setupListeners();
    }

    private void initializeUI() {
        gridLayout = findViewById(R.id.gridLayout);
        lblTimer = findViewById(R.id.txtTempo);
        lblMoveCounter = findViewById(R.id.txtMovimentos);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);
        overlay = findViewById(R.id.overlay);
        txtOverlayMessage = findViewById(R.id.txtOverlayMessage);
    }

    private void initializePuzzleButtons() {
        gridLayout.post(() -> {
            List<String> letras = Arrays.asList(
                    "M", "A", "T", "O",
                    "A", "M", "O", "R",
                    "C", "O", "C", "A",
                    "U", "V", "A" // Último vazio
            );

            for (int i = 0; i < letras.size(); i++) {
                Button button = createPuzzleButton(letras.get(i));
                int row = i / 4;
                int col = i % 4;

                buttonPositions.put(button, new int[]{row, col});
                button.setLayoutParams(getParams(row, col));

                button.setEnabled(false);
                gridLayout.addView(button);
                puzzleButtons.add(button);
            }
        });
    }

    private Button createPuzzleButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(Color.CYAN);
        button.setTextColor(Color.BLACK);
        button.setOnClickListener(v -> {
            if (isMoveValid(button)) {
                moveButton(button);
                updateMoveCounter();
            }
        });
        return button;
    }

    private GridLayout.LayoutParams getParams(int row, int col) {
        int cellSize = (gridLayout.getWidth() / 4) - 10;
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = cellSize;
        params.height = cellSize;
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(col);
        params.setMargins(4, 4, 4, 4);
        return params;
    }

    private void setupListeners() {
        btnStart.setOnClickListener(v -> startGame());
        btnReset.setOnClickListener(v -> resetGame());
    }

    private boolean isMoveValid(Button button) {
        int[] position = buttonPositions.get(button);
        int row = position[0];
        int col = position[1];

        return Math.abs(row - emptyRow) + Math.abs(col - emptyCol) == 1;
    }

    private void moveButton(Button button) {
        int[] position = buttonPositions.get(button);
        int newEmptyRow = position[0];
        int newEmptyCol = position[1];

        buttonPositions.put(button, new int[]{emptyRow, emptyCol});
        gridLayout.removeView(button);
        button.setLayoutParams(getParams(emptyRow, emptyCol));
        gridLayout.addView(button);

        emptyRow = newEmptyRow;
        emptyCol = newEmptyCol;
    }

    private void startGame() {
        isGameRunning = true;
        elapsedTime = 0;
        moveCount = 0;
        lblTimer.setText("00:00");
        lblMoveCounter.setText("0");
        overlay.setVisibility(View.GONE);
        puzzleButtons.forEach(button -> button.setEnabled(true));
        shuffleAndPlaceButtons();
        startTimer();
    }

    private void resetGame() {
        isGameRunning = false;
        handler.removeCallbacksAndMessages(null);
        elapsedTime = 0;
        moveCount = 0;
        lblTimer.setText("00:00");
        lblMoveCounter.setText("0");
        overlay.setVisibility(View.VISIBLE);
        puzzleButtons.forEach(button -> button.setEnabled(false));
        placeButtonsOnGrid(puzzleButtons);
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isGameRunning) {
                    elapsedTime++;
                    lblTimer.setText(formatElapsedTime(elapsedTime));
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void shuffleAndPlaceButtons() {
        List<Button> shuffledButtons = new ArrayList<>(puzzleButtons);
        Collections.shuffle(shuffledButtons);
        placeButtonsOnGrid(shuffledButtons);
    }

    private void placeButtonsOnGrid(List<Button> buttonList) {
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (index < buttonList.size()) {
                    Button button = buttonList.get(index);
                    buttonPositions.put(button, new int[]{row, col});
                    button.setLayoutParams(getParams(row, col));
                    if (button.getParent() == null) {
                        gridLayout.addView(button);
                    }
                    index++;
                }
            }
        }
        emptyRow = 3;
        emptyCol = 3;
    }

    private void updateMoveCounter() {
        moveCount++;
        lblMoveCounter.setText(String.valueOf(moveCount));
        checkIfPuzzleIsSolved();
    }

    private void checkIfPuzzleIsSolved() {
        StringBuilder currentText = new StringBuilder();

        buttonPositions.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue()[0] * 4 + entry.getValue()[1]))
                .forEachOrdered(entry -> currentText.append(entry.getKey().getText()));

        String targetText = String.join("", targetWords);

        if (currentText.toString().equals(targetText) && (emptyRow == 3 && emptyCol == 3)) {
            isGameRunning = false;
            handler.removeCallbacksAndMessages(null);
            showVictoryDialog();
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatElapsedTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private void showVictoryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Vitória!")
                .setMessage("Parabéns! Você venceu!\nTempo: " + lblTimer.getText() + "\nMovimentos: " + lblMoveCounter.getText())
                .setPositiveButton("OK", (dialog, which) -> resetGame())
                .show();
    }
}