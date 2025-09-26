package com.anhtu.turbo88;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    SeekBar snail1, snail2, snail3, snail4;
    Handler handler = new Handler();
    boolean isRunning = false;
    int progress1 = 0, progress2 = 0, progress3 = 0, progress4 = 0;
    int max = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        snail1 = findViewById(R.id.snail1);
        snail2 = findViewById(R.id.snail2);
        snail3 = findViewById(R.id.snail3);
        snail4 = findViewById(R.id.snail4);

        snail1.setThumb(getResources().getDrawable(R.drawable.snail_animation));
        snail2.setThumb(getResources().getDrawable(R.drawable.snail_animation));
        snail3.setThumb(getResources().getDrawable(R.drawable.snail_animation));
        snail4.setThumb(getResources().getDrawable(R.drawable.snail_animation));

        startSnailAnimation(snail1);
        startSnailAnimation(snail2);
        startSnailAnimation(snail3);
        startSnailAnimation(snail4);

        findViewById(R.id.btnStart).setOnClickListener(v -> {
            if (!isRunning) {
                isRunning = true;
                startRace();
            }
        });

        findViewById(R.id.btnReset).setOnClickListener(v -> {
            isRunning = false;
            progress1 = progress2 = progress3 = progress4 = 0;
            snail1.setProgress(progress1);
            snail2.setProgress(progress2);
            snail3.setProgress(progress3);
            snail4.setProgress(progress4);

            snail1.getProgressDrawable().setColorFilter(null);
            snail2.getProgressDrawable().setColorFilter(null);
            snail3.getProgressDrawable().setColorFilter(null);
            snail4.getProgressDrawable().setColorFilter(null);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startRace() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    if (progress1 < max)
                        progress1 = updateProgress(progress1, max);
                    if (progress2 < max)
                        progress2 = updateProgress(progress2, max);
                    if (progress3 < max)
                        progress3 = updateProgress(progress3, max);
                    if (progress4 < max)
                        progress4 = updateProgress(progress4, max);

                    snail1.setProgress(progress1);
                    snail2.setProgress(progress2);
                    snail3.setProgress(progress3);
                    snail4.setProgress(progress4);

                    checkWinner();
                    if (isRunning)
                        handler.postDelayed(this, 100);
                }
            }
        }, 100);
    }
    private void startSnailAnimation(SeekBar seekBar) {
        Drawable drawable = seekBar.getThumb();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable ani = (AnimationDrawable) drawable;
            ani.setOneShot(false);
            ani.start();
        }
    }

    private int updateProgress(int progress, int max) {
        Random random = new Random();
        if (progress < max) {
            int remaining = max - progress;

            int step;
            if (remaining > 250) {
                step = random.nextInt(3) + 1;
            } else if (remaining > 150) {
                step = random.nextInt(4) + 1;
            } else if (remaining > 50) {
                step = random.nextInt(5) + 2;
            } else {
                step = random.nextInt(2) + 3;
            }

            progress += step;
            progress = Math.min(progress, max);
        }
        return progress;
    }

    private void checkWinner() {
        if (progress1 >= max || progress2 >= max || progress3 >= max || progress4 >= max) {
            isRunning = false;

            String winner = getWinner();

            if (winner.equals("Snail 1")) snail1.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            if (winner.equals("Snail 2")) snail2.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            if (winner.equals("Snail 3")) snail3.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            if (winner.equals("Snail 4")) snail4.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);

            runOnUiThread(() ->
                    showWinnerDialog(winner)
            );
        }
    }

    private String getWinner() {
        int maxProgress = Math.max(Math.max(progress1, progress2), Math.max(progress3, progress4));

        List<String> candidates = new ArrayList<>();
        if (progress1 == maxProgress) candidates.add("Snail 1");
        if (progress2 == maxProgress) candidates.add("Snail 2");
        if (progress3 == maxProgress) candidates.add("Snail 3");
        if (progress4 == maxProgress) candidates.add("Snail 4");

        Random random = new Random();
        return candidates.get(random.nextInt(candidates.size()));
    }

    private void showWinnerDialog(String winner) {
        new AlertDialog.Builder(this)
                .setTitle("Race Finished 🏁")
                .setMessage(winner + " is the winner!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


}