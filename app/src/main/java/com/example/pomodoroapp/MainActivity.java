package com.example.pomodoroapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_MINUTES = 120;

    private TextView timerText, titleText;
    private ImageButton btnIncrease, btnDecrease;
    private int minutes = 0; // Start time in minutes
    private CountDownTimer countDownTimer;
    private long timeInMillis = minutes * 60 * 1000;

    private Button startButton;

    private Boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleText = findViewById(R.id.title);
        timerText = findViewById(R.id.timerText);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        startButton = findViewById(R.id.startButton);

        updateTimerText();

        btnIncrease.setOnClickListener(v -> changeTime(1));
        btnDecrease.setOnClickListener(v -> changeTime(-1));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning){
                    reset();
                }else{
                    start();
                }
            }
        });
    }


    private void reset(){

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        isRunning = false;
        //enable the increase/decrease buttons
        btnIncrease.setClickable(true);
        btnDecrease.setClickable(true);

        titleText.setText(R.string.title_start);
        startButton.setText(R.string.start);

        minutes = 0;
        timeInMillis = minutes * 60 * 1000;
        updateTimerText();

    }

    private void start(){

        isRunning = true;

        //start the time
        countDownTimer = new CountDownTimer(timeInMillis, 1000 ) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeInMillis = millisUntilFinished;

                if(timeInMillis < 120000){
                    titleText.setText(R.string.title_almost_done);
                }
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
            }
        }.start();


        titleText.setText(R.string.title_countdown);
        startButton.setText(R.string.quit);

        //stop the increase.decrease buttons
        btnIncrease.setClickable(false);
        btnDecrease.setClickable(false);
    }

    private void changeTime(int i) {
        minutes += i;
        if (minutes < 0) minutes = 1;
        if(minutes > MAX_MINUTES) minutes = 120;
        timeInMillis = minutes * 60 * 1000;
        updateTimerText();
    }

    @SuppressLint("DefaultLocale")
    private void updateTimerText() {
        int mins = (int) (timeInMillis / 1000 / 60);
        int secs = (int) ((timeInMillis / 1000) % 60);

        timerText.setText(String.format("%02d:%02d", mins, secs));
    }
}