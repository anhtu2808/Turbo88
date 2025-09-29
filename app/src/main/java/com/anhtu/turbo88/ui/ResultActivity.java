package com.anhtu.turbo88.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anhtu.turbo88.R;
import com.anhtu.turbo88.data.AppDatabase;
import com.anhtu.turbo88.data.dao.UserDao;
import com.anhtu.turbo88.data.entity.User;
import com.anhtu.turbo88.util.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private TextView tvBetResult, tvBalance;
    private Button btnClose;

    private ImageView imgFirst, imgSecond, imgThird, imgFourth;
    private TextView tvFirstName, tvSecondName, tvThirdName, tvFourthName;
    private TextView tvFirstPayout, tvSecondPayout, tvThirdPayout, tvFourthPayout;

    private SessionManager session;
    private UserDao userDao;

    private Map<Integer, Integer> betMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        bindViews();

        session = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDao();

        List<String> ranking = getIntent().getStringArrayListExtra("RANKING");
        String betResult = getIntent().getStringExtra("BET_RESULT");
        double newBalance = getIntent().getDoubleExtra("NEW_BALANCE", 0);

        // Lấy mảng bet
        int[] betIds = getIntent().getIntArrayExtra("BET_IDS");
        int[] betValues = getIntent().getIntArrayExtra("BET_VALUES");
        if (betIds != null && betValues != null && betIds.length == betValues.length) {
            for (int i = 0; i < betIds.length; i++) {
                betMap.put(betIds[i], betValues[i]);
            }
        }

        setupPodium(ranking);

        tvBetResult.setText(betResult != null ? betResult : "");
        tvBalance.setText(String.format(Locale.US, "New Balance: %.2f$", newBalance));

        // Update DB
        String username = session.getUsername();
        if (username != null) {
            new Thread(() -> {
                User user = userDao.findByUsername(username);
                if (user != null) {
                    user.setBalance(newBalance);
                    userDao.updateUser(user);
                }
            }).start();
        }

        btnClose.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, BettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void bindViews() {
        tvBetResult = findViewById(R.id.tvBetResult);
        tvBalance = findViewById(R.id.tvBalance);
        btnClose = findViewById(R.id.btnClose);

        imgFirst = findViewById(R.id.imgFirst);
        imgSecond = findViewById(R.id.imgSecond);
        imgThird = findViewById(R.id.imgThird);
        imgFourth = findViewById(R.id.imgFourth);

        tvFirstName = findViewById(R.id.tvFirstName);
        tvSecondName = findViewById(R.id.tvSecondName);
        tvThirdName = findViewById(R.id.tvThirdName);
        tvFourthName = findViewById(R.id.tvFourthName);

        tvFirstPayout = findViewById(R.id.tvFirstPayout);
        tvSecondPayout = findViewById(R.id.tvSecondPayout);
        tvThirdPayout = findViewById(R.id.tvThirdPayout);
        tvFourthPayout = findViewById(R.id.tvFourthPayout);
    }

    private void setupPodium(List<String> ranking) {
        if (ranking == null || ranking.isEmpty()) {
            setEmpty(imgFirst, tvFirstName, tvFirstPayout);
            setEmpty(imgSecond, tvSecondName, tvSecondPayout);
            setEmpty(imgThird, tvThirdName, tvThirdPayout);
            setEmpty(imgFourth, tvFourthName, tvFourthPayout);
            return;
        }

        int firstId = ranking.size() >= 1 ? extractId(ranking.get(0)) : -1;
        int secondId = ranking.size() >= 2 ? extractId(ranking.get(1)) : -1;

        // First
        if (firstId != -1) {
            tvFirstName.setText(getDisplayName("Snail " + firstId));
            imgFirst.setImageResource(getSnailDrawable(firstId));
            displayPayout(firstId, 1, tvFirstPayout);
        } else setEmpty(imgFirst, tvFirstName, tvFirstPayout);

        // Second
        int id2 = ranking.size() >= 2 ? extractId(ranking.get(1)) : -1;
        if (id2 != -1) {
            tvSecondName.setText(getDisplayName("Snail " + id2));
            imgSecond.setImageResource(getSnailDrawable(id2));
            displayPayout(id2, 2, tvSecondPayout);
        } else setEmpty(imgSecond, tvSecondName, tvSecondPayout);

        // Third
        int id3 = ranking.size() >= 3 ? extractId(ranking.get(2)) : -1;
        if (id3 != -1) {
            tvThirdName.setText(getDisplayName("Snail " + id3));
            imgThird.setImageResource(getSnailDrawable(id3));
            displayPayout(id3, 3, tvThirdPayout);
        } else setEmpty(imgThird, tvThirdName, tvThirdPayout);

        // Fourth
        int id4 = ranking.size() >= 4 ? extractId(ranking.get(3)) : -1;
        if (id4 != -1) {
            tvFourthName.setText(getDisplayName("Snail " + id4));
            imgFourth.setImageResource(getSnailDrawable(id4));
            displayPayout(id4, 4, tvFourthPayout);
        } else setEmpty(imgFourth, tvFourthName, tvFourthPayout);
    }

    private void displayPayout(int snailId, int position, TextView tvPayout) {
        if (!betMap.containsKey(snailId)) {
            tvPayout.setText(""); // Không cược, không hiển thị
            return;
        }
        int bet = betMap.get(snailId);
        double net; // lãi/lỗ
        switch (position) {
            case 1:
                // tổng nhận: bet*2 => net = +bet
                net = bet;
                break;
            case 2:
                // tổng nhận: bet*1.5 => net = +0.5*bet
                net = bet * 0.5;
                break;
            default:
                net = -bet;
        }
        if (net > 0) {
            tvPayout.setText(String.format(Locale.US, "+%.2f$", net));
            tvPayout.setTextColor(0xFF00FF66); // xanh lời
        } else if (net < 0) {
            tvPayout.setText(String.format(Locale.US, "-%.2f$", Math.abs(net)));
            tvPayout.setTextColor(0xFFFF5555); // đỏ lỗ
        } else {
            tvPayout.setText("0$");
            tvPayout.setTextColor(0xFFFFFFFF);
        }
    }

    private void setEmpty(ImageView img, TextView name, TextView payout) {
        img.setAlpha(0.3f);
        name.setText("-");
        payout.setText("");
    }

    private int extractId(String raw) {
        if (raw == null) return -1;
        String digits = raw.replaceAll("\\D+", "");
        if (digits.isEmpty()) return -1;
        try { return Integer.parseInt(digits); } catch (NumberFormatException e) { return -1; }
    }

    private int getSnailDrawable(int id) {
        switch (id) {
            case 1: return R.drawable.turbo_logo;
            case 2: return R.drawable.nizza;
            case 3: return R.drawable.chuppy;
            case 4: return R.drawable.lady;
            default: return R.drawable.ic_snail_placeholder;
        }
    }

    private String getDisplayName(String raw) {
        if (raw == null) return "-";
        switch (raw) {
            case "Snail 1": return "Turbo";
            case "Snail 2": return "Nizza";
            case "Snail 3": return "Chuppy";
            case "Snail 4": return "Burn";
            default: return raw;
        }
    }
}