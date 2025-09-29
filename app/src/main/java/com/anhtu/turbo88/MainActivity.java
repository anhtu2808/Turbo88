package com.anhtu.turbo88;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer; // Added import
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView; // Added import
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    SeekBar snail1, snail2, snail3, snail4;
    Handler handler = new Handler();
    boolean isRunning = false;
    int progress1 = 0, progress2 = 0, progress3 = 0, progress4 = 0;
    int max = 300;

    MediaPlayer bgMusic;

    ImageView flame1, flame2, flame3, flame4;

    private TextView tvCountdown; // Added for countdown
    private Button btnReset, btnBack; // Added for enabling/disabling


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        snail1 = findViewById(R.id.snail1);
        snail2 = findViewById(R.id.snail2);
        snail3 = findViewById(R.id.snail3);
        snail4 = findViewById(R.id.snail4);

        flame1 = findViewById(R.id.flame1);
        flame2 = findViewById(R.id.flame2);
        flame3 = findViewById(R.id.flame3);
        flame4 = findViewById(R.id.flame4);

        tvCountdown = findViewById(R.id.tvCountdown); // Initialize countdown TextView
        btnReset = findViewById(R.id.btnReset);       // Initialize Reset Button
        btnBack = findViewById(R.id.btnBack);         // Initialize Back Button

        snail1.setThumb(getResources().getDrawable(R.drawable.snail_animation));
        snail2.setThumb(getResources().getDrawable(R.drawable.snail_animation1));
        snail3.setThumb(getResources().getDrawable(R.drawable.snail_animation3));
        snail4.setThumb(getResources().getDrawable(R.drawable.snail_animation4));

        startSnailAnimation(snail1);
        startSnailAnimation(snail2);
        startSnailAnimation(snail3);
        startSnailAnimation(snail4);

        // Listener for btnBack (now it just finishes the activity)
        btnBack.setOnClickListener(v -> finish());

        btnReset.setOnClickListener(v -> {
            if (!isRunning) { // Only reset if not actively counting down or racing
                progress1 = progress2 = progress3 = progress4 = 0;
                snail1.setProgress(progress1);
                snail2.setProgress(progress2);
                snail3.setProgress(progress3);
                snail4.setProgress(progress4);

                snail1.getProgressDrawable().setColorFilter(null);
                snail2.getProgressDrawable().setColorFilter(null);
                snail3.getProgressDrawable().setColorFilter(null);
                snail4.getProgressDrawable().setColorFilter(null);
            } else {
                Toast.makeText(MainActivity.this, "Cannot reset during race or countdown", Toast.LENGTH_SHORT).show();
            }
        });

        bgMusic = MediaPlayer.create(this, R.raw.tokyo);
        bgMusic.setLooping(true);
        bgMusic.start();

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
        );

        // Check if we need to start the countdown
        if (getIntent().getBooleanExtra("START_COUNTDOWN", false)) {
            startRaceCountdown();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startRaceCountdown() {
        tvCountdown.setVisibility(View.VISIBLE);
        btnReset.setEnabled(false);
        btnBack.setEnabled(false);
        // Disable SeekBars interaction during countdown if desired
        snail1.setEnabled(false);
        snail2.setEnabled(false);
        snail3.setEnabled(false);
        snail4.setEnabled(false);

        new CountDownTimer(3999, 1000) { // 3.999 seconds, tick every second
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = (millisUntilFinished / 1000) + 1;
                 if (secondsRemaining > 3) secondsRemaining = 3; // Cap at 3 for display
                tvCountdown.setText(String.valueOf(secondsRemaining));
            }

            public void onFinish() {
                tvCountdown.setVisibility(View.GONE);
                btnReset.setEnabled(true);
                btnBack.setEnabled(true);
                // Re-enable SeekBars if they were disabled
                snail1.setEnabled(true);
                snail2.setEnabled(true);
                snail3.setEnabled(true);
                snail4.setEnabled(true);

                if (!isRunning) { // Start race if not already running
                    isRunning = true;
                    startRace();
                }
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgMusic != null) {
            bgMusic.release();
            bgMusic = null;
        }
        handler.removeCallbacksAndMessages(null); // Stop any pending race updates
    }

    private void startRace() {
        // Reset progress just before starting a new race if it's a fresh start
        // If continuing, this might not be needed, but for a clean start from countdown:
        // progress1 = progress2 = progress3 = progress4 = 0;
        // snail1.setProgress(progress1);
        // snail2.setProgress(progress2);
        // snail3.setProgress(progress3);
        // snail4.setProgress(progress4);
        // snail1.getProgressDrawable().setColorFilter(null); // etc for others

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    if (progress1 < max)
                        progress1 = updateProgress(progress1, max, flame1);
                    moveFlame(flame1, snail1);
                    if (progress2 < max)
                        progress2 = updateProgress(progress2, max, flame2);
                    moveFlame(flame2, snail2);
                    if (progress3 < max)
                        progress3 = updateProgress(progress3, max, flame3);
                    moveFlame(flame3, snail3);
                    if (progress4 < max)
                        progress4 = updateProgress(progress4, max, flame4);
                    moveFlame(flame4, snail4);

                    snail1.setProgress(progress1);
                    snail2.setProgress(progress2);
                    snail3.setProgress(progress3);
                    snail4.setProgress(progress4);

                    checkWinner();
                    if (isRunning) // Continue race if not finished
                        handler.postDelayed(this, 150);
                }
            }
        }, 150);
    }

    private int updateProgress(int progress, int max, ImageView flame) {
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
                step = random.nextInt(2) + 2;
            }

            int maxStep = (remaining > 250) ? 3 :
                    (remaining > 150) ? 4 :
                            (remaining > 50)  ? 6 : 3;

            if (step == maxStep) {
                showFlame(flame);
            } else {
                hideFlame(flame);
            }

            progress += step;
            progress = Math.min(progress, max);
        }
        return progress;
    }

    @SuppressWarnings("unchecked")
    private void checkWinner() {
        if (!isRunning) return; // Don't proceed if race was stopped/reset

        if (progress1 >= max || progress2 >= max || progress3 >= max || progress4 >= max) {
            isRunning = false; // Stop the race loop

            HashMap<Integer, Integer> bets =
                    (HashMap<Integer, Integer>) getIntent().getSerializableExtra("BETS_MAP");
            double balance = getIntent().getDoubleExtra("BALANCE", 0);

            if (bets == null) { // Handle case where bets might not have been passed (e.g. direct start)
                bets = new HashMap<>();
                 Toast.makeText(this, "No bet data found, running for fun!", Toast.LENGTH_LONG).show();
            }

            ArrayList<String> ranking = getRanking();

            int firstId = extractSnailId(ranking.get(0));
            int secondId = ranking.size() >= 2 ? extractSnailId(ranking.get(1)) : -1;

            double totalWinAmount = 0;
            if (bets.containsKey(firstId)) {
                totalWinAmount += bets.get(firstId) * 2.0;
            }
            if (secondId != -1 && bets.containsKey(secondId)) {
                totalWinAmount += bets.get(secondId) * 1.5;
            }

            double totalBet = 0;
            for (int betAmount : bets.values()) totalBet += betAmount;

            double profitOrLoss = totalWinAmount - totalBet;
            double newBalance = balance - totalBet + totalWinAmount;

            StringBuilder betResultBuilder = new StringBuilder();
            if (profitOrLoss > 0) {
                betResultBuilder.append("🎉 Bạn thắng! +").append(profitOrLoss).append("$");
            } else if(profitOrLoss < 0){
                betResultBuilder.append("😢 Thua cược! ").append(profitOrLoss).append("$");
            } else{
                 if (!bets.isEmpty()) betResultBuilder.append("Bạn hoà vốn");
                 else betResultBuilder.append("Cuộc đua kết thúc!");
            }
            String betResult = betResultBuilder.toString();

            Intent intent = new Intent(this, com.anhtu.turbo88.ui.ResultActivity.class);
            intent.putStringArrayListExtra("RANKING", new ArrayList<>(ranking));
            intent.putExtra("BET_RESULT", betResult);
            intent.putExtra("NEW_BALANCE", newBalance);
            intent.putExtra("HAS_BETS", !bets.isEmpty());


            // Gửi danh sách ID đã cược
            intent.putIntegerArrayListExtra("BET_SNAILS", new ArrayList<>(bets.keySet()));

            // Gửi riêng mảng ID và giá trị để tái dựng map (an toàn & nhẹ)
            int size = bets.size();
            int[] betIds = new int[size];
            int[] betValues = new int[size];
            int idx = 0;
            for (Integer k : bets.keySet()) {
                betIds[idx] = k;
                betValues[idx] = bets.get(k);
                idx++;
            }
            intent.putExtra("BET_IDS", betIds);
            intent.putExtra("BET_VALUES", betValues);

            startActivity(intent);
            finish(); // Finish MainActivity after navigating to ResultActivity
        }
    }

    private int extractSnailId(String line) {
        if (line == null) return -1;
        String digits = line.replaceAll("\\D+", "");
        if (digits.isEmpty()) return -1;
        try { return Integer.parseInt(digits); } catch (NumberFormatException e) { return -1; }
    }

    private ArrayList<String> getRanking() {
        ArrayList<String> ranking = new ArrayList<>();
        List<int[]> results = new ArrayList<>();
        results.add(new int[]{progress1, 1});
        results.add(new int[]{progress2, 2});
        results.add(new int[]{progress3, 3});
        results.add(new int[]{progress4, 4});

        results.sort((a, b) -> Integer.compare(b[0], a[0]));

        for (int[] res : results) {
            ranking.add("Snail " + res[1]);
        }

        return ranking;
    }

    private void showFlame(ImageView flame) {
        flame.setVisibility(View.VISIBLE);
        Drawable drawable = flame.getDrawable();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable ani = (AnimationDrawable) drawable;
            ani.setOneShot(false);
            ani.start();
        }
    }

    private void hideFlame(ImageView flame) {
        flame.setVisibility(View.GONE);
    }

    private void moveFlame(ImageView flame, SeekBar snail) {
        int progress = snail.getProgress();
        int max = snail.getMax();
        float ratio = (float) progress / max;
        int width = snail.getWidth();
        float flameX = ratio * width - flame.getWidth();
        float flameY = snail.getY() + (snail.getHeight() - flame.getHeight()) / 2f;
        flame.setX(flameX);
        flame.setY(flameY);
    }

    private void startSnailAnimation(SeekBar seekBar) {
        Drawable drawable = seekBar.getThumb();
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable ani = (AnimationDrawable) drawable;
            ani.setOneShot(false);
            ani.start();
        }
    }
}
