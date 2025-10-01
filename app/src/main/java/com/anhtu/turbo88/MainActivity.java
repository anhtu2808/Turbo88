package com.anhtu.turbo88;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anhtu.turbo88.data.AppDatabase;
import com.anhtu.turbo88.data.dao.TransactionDao;
import com.anhtu.turbo88.data.dao.UserDao;
import com.anhtu.turbo88.data.entity.Transaction;
import com.anhtu.turbo88.data.entity.User;
import com.anhtu.turbo88.util.SessionManager;

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

    private TextView tvCountdown;

    private TransactionDao transactionDao;
    private UserDao userDao;
    private SessionManager sessionManager;
    private User currentUser;

    private Button btnReset, btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Database, DAOs, and SessionManager
        AppDatabase db = AppDatabase.getInstance(this);
        userDao = db.userDao();
        transactionDao = db.transactionDao();
        sessionManager = new SessionManager(this);

        // Get current user by username from session
        String username = sessionManager.getUsername();
        if (username == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUser = userDao.findByUsername(username);

        if (currentUser == null) {
            Toast.makeText(this, "Error: Could not find user data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        snail1 = findViewById(R.id.snail1);
        snail2 = findViewById(R.id.snail2);
        snail3 = findViewById(R.id.snail3);
        snail4 = findViewById(R.id.snail4);

        flame1 = findViewById(R.id.flame1);
        flame2 = findViewById(R.id.flame2);
        flame3 = findViewById(R.id.flame3);
        flame4 = findViewById(R.id.flame4);

        tvCountdown = findViewById(R.id.tvCountdown);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);

        snail1.setThumb(getResources().getDrawable(R.drawable.snail_animation));
        snail2.setThumb(getResources().getDrawable(R.drawable.snail_animation1));
        snail3.setThumb(getResources().getDrawable(R.drawable.snail_animation3));
        snail4.setThumb(getResources().getDrawable(R.drawable.snail_animation4));

        startSnailAnimation(snail1);
        startSnailAnimation(snail2);
        startSnailAnimation(snail3);
        startSnailAnimation(snail4);

        btnBack.setOnClickListener(v -> finish());

        btnReset.setOnClickListener(v -> {
            if (!isRunning) {
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
        snail1.setEnabled(false);
        snail2.setEnabled(false);
        snail3.setEnabled(false);
        snail4.setEnabled(false);

        new CountDownTimer(3999, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = (millisUntilFinished / 1000) + 1;
                 if (secondsRemaining > 3) secondsRemaining = 3;
                tvCountdown.setText(String.valueOf(secondsRemaining));
            }

            public void onFinish() {
                tvCountdown.setVisibility(View.GONE);
                btnReset.setEnabled(true);
                btnBack.setEnabled(true);
                snail1.setEnabled(true);
                snail2.setEnabled(true);
                snail3.setEnabled(true);
                snail4.setEnabled(true);

                if (!isRunning) {
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
        handler.removeCallbacksAndMessages(null);
    }

    private void startRace() {
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
                    if (isRunning)
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
        if (!isRunning) return;

        if (progress1 >= max || progress2 >= max || progress3 >= max || progress4 >= max) {
            isRunning = false;

            HashMap<Integer, Integer> bets =
                    (HashMap<Integer, Integer>) getIntent().getSerializableExtra("BETS_MAP");
            double balance = getIntent().getDoubleExtra("BALANCE", 0);

            if (bets == null) {
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

            if (currentUser != null && !bets.isEmpty()) {
                String transactionType = profitOrLoss >= 0 ? "WIN" : "LOSS";
                Transaction transaction = new Transaction(currentUser.getId(), transactionType, Math.abs(profitOrLoss), System.currentTimeMillis());
                transactionDao.insertTransaction(transaction);
            }

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

            intent.putIntegerArrayListExtra("BET_SNAILS", new ArrayList<>(bets.keySet()));

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
            finish();
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
