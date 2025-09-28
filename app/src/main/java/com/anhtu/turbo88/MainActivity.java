package com.anhtu.turbo88;

import android.content.Intent;
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
import java.util.HashMap;
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

    @SuppressWarnings("unchecked")
    private void checkWinner() {
        if (progress1 >= max || progress2 >= max || progress3 >= max || progress4 >= max) {
            isRunning = false;

            // Nhận dữ liệu cược từ BettingActivity
            HashMap<Integer, Integer> bets =
                    (HashMap<Integer, Integer>) getIntent().getSerializableExtra("BETS_MAP");
            double balance = getIntent().getDoubleExtra("BALANCE", 0);

            // Tạo ranking
            ArrayList<String> ranking = getRanking();

            // Xác định con thắng
            String winnerLine = ranking.get(0);
            int winnerId = Integer.parseInt(winnerLine.replaceAll("\\D+", ""));

            // Tính toán tiền
            double winAmount = 0;
            if (bets.containsKey(winnerId)) {
                winAmount = bets.get(winnerId) * 2.0; // Ví dụ thắng gấp đôi
            }

            double totalBet = 0;
            for (int bet : bets.values()) {
                totalBet += bet;
            }

            double newBalance = balance - totalBet + winAmount;
            String betResult = winAmount > 0 ? "🎉 You Win! +" + winAmount + "$" : "😢 You Lose! -" + totalBet + "$";

            // Gửi sang ResultActivity
            Intent intent = new Intent(this, com.anhtu.turbo88.ui.ResultActivity.class);
            intent.putStringArrayListExtra("RANKING", new ArrayList<>(ranking));
            intent.putExtra("BET_RESULT", betResult);
            intent.putExtra("NEW_BALANCE", newBalance);
            startActivity(intent);
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
    private ArrayList<String> getRanking() {
        ArrayList<String> ranking = new ArrayList<>();
        // Gom progress vào list tạm
        List<int[]> results = new ArrayList<>();
        results.add(new int[]{progress1, 1});
        results.add(new int[]{progress2, 2});
        results.add(new int[]{progress3, 3});
        results.add(new int[]{progress4, 4});

        // Sắp xếp giảm dần theo progress
        results.sort((a, b) -> Integer.compare(b[0], a[0]));

        for (int[] res : results) {
            ranking.add("Snail " + res[1]);
        }

        return ranking;
    }


}