package com.example.chessalarm;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chessalarm.alarm.AlarmController;
import com.example.chessalarm.alarm.AlarmStateListener;
import com.example.chessalarm.puzzle.ChessPuzzle;
import com.example.chessalarm.puzzle.LichessPuzzleRepository;
import com.example.chessalarm.puzzle.MateInOneSolver;
import com.example.chessalarm.puzzle.PuzzleSolver;
import com.example.chessalarm.puzzle.PuzzleSource;

public class PuzzleActivity extends AppCompatActivity implements AlarmStateListener {

    private final PuzzleSolver solver = new MateInOneSolver();
    private PuzzleSource puzzleSource;
    private ChessPuzzle currentPuzzle;

    private TextView fenView;
    private TextView feedbackView;
    private EditText answerInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        fenView = findViewById(R.id.fenView);
        feedbackView = findViewById(R.id.feedbackView);
        answerInput = findViewById(R.id.answerInput);
        Button submit = findViewById(R.id.submitButton);

        puzzleSource = new LichessPuzzleRepository(this);
        currentPuzzle = puzzleSource.random();
        fenView.setText(currentPuzzle.getFen());

        AlarmController.getInstance(this).addListener(this);

        submit.setOnClickListener(v -> {
            String move = answerInput.getText().toString();
            if (solver.checkAnswer(currentPuzzle, move)) {
                AlarmController.getInstance(this).dismiss();
            } else {
                feedbackView.setText(R.string.try_again);
            }
        });
    }

    @Override
    protected void onDestroy() {
        AlarmController.getInstance(this).removeListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Intentionally no-op: user must solve the puzzle to dismiss the alarm.
    }

    @Override
    public void onAlarmTriggered() {
        // Activity is already in foreground when triggered; no-op.
    }

    @Override
    public void onAlarmDismissed() {
        finish();
    }
}
