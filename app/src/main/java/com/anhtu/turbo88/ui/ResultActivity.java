package com.anhtu.turbo88.ui;

import android.content.Intent;
import android.graphics.Color;
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

import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private TextView tvBetResult, tvBalance;
    private Button btnClose;

    private ImageView imgFirst, imgSecond, imgThird, imgFourth;
    private TextView tvFirstName, tvSecondName, tvThirdName, tvFourthName;

    private SessionManager session;
    private UserDao userDao;

    private List<Integer> betSnails; // danh sách ID ốc đã cược

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

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

        session = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDao();

        List<String> ranking = getIntent().getStringArrayListExtra("RANKING");
        String betResult = getIntent().getStringExtra("BET_RESULT");
        double newBalance = getIntent().getDoubleExtra("NEW_BALANCE", 0);

        betSnails = getIntent().getIntegerArrayListExtra("BET_SNAILS");

        setupPodium(ranking, betSnails);

        tvBetResult.setText(betResult != null ? betResult : "");
        tvBalance.setText(String.format(Locale.US, "New Balance: %.2f$", newBalance));

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

    private void setupPodium(List<String> ranking, List<Integer> betSnails) {
        // Reset mờ nếu không có dữ liệu
        if (ranking == null || ranking.isEmpty()) {
            dimAll();
            return;
        }

        // First
        if (ranking.size() >= 1) {
            String first = ranking.get(0);
            tvFirstName.setText(getDisplayName(first));
            imgFirst.setImageResource(getSnailDrawable(first));
            highlightIfBet(first, betSnails, imgFirst, tvFirstName);
        } else {
            setSlotEmpty(imgFirst, tvFirstName);
        }

        // Second
        if (ranking.size() >= 2) {
            String second = ranking.get(1);
            tvSecondName.setText(getDisplayName(second));
            imgSecond.setImageResource(getSnailDrawable(second));
            highlightIfBet(second, betSnails, imgSecond, tvSecondName);
        } else {
            setSlotEmpty(imgSecond, tvSecondName);
        }

        // Third
        if (ranking.size() >= 3) {
            String third = ranking.get(2);
            tvThirdName.setText(getDisplayName(third));
            imgThird.setImageResource(getSnailDrawable(third));
            highlightIfBet(third, betSnails, imgThird, tvThirdName);
        } else {
            setSlotEmpty(imgThird, tvThirdName);
        }

        // Fourth
        if (ranking.size() >= 4) {
            String fourth = ranking.get(3);
            tvFourthName.setText(getDisplayName(fourth));
            imgFourth.setImageResource(getSnailDrawable(fourth));
            highlightIfBet(fourth, betSnails, imgFourth, tvFourthName);
        } else {
            setSlotEmpty(imgFourth, tvFourthName);
        }
    }

    private void dimAll() {
        setDim(imgFirst, tvFirstName);
        setDim(imgSecond, tvSecondName);
        setDim(imgThird, tvThirdName);
        setDim(imgFourth, tvFourthName);
    }

    private void setDim(ImageView img, TextView tv) {
        img.setAlpha(0.3f);
        tv.setText("-");
    }

    private void setSlotEmpty(ImageView img, TextView tv) {
        img.setAlpha(0.3f);
        tv.setText("-");
    }

    private void highlightIfBet(String snailName, List<Integer> betSnails, ImageView img, TextView tvName) {
        if (isBetSnail(snailName, betSnails)) {
            img.setBackgroundResource(R.drawable.bg_highlight);
            tvName.setTextColor(Color.parseColor("#FFD700"));
            img.setAlpha(1f);
        } else {
            img.setBackground(null);
            tvName.setTextColor(Color.WHITE);
            img.setAlpha(1f);
        }
    }

    private boolean isBetSnail(String snailName, List<Integer> betSnails) {
        if (snailName == null || betSnails == null) return false;
        String digits = snailName.replaceAll("\\D+", "");
        if (digits.isEmpty()) return false;
        int id;
        try {
            id = Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return false;
        }
        return betSnails.contains(id);
    }

    private int getSnailDrawable(String name) {
        if (name == null) return R.drawable.ic_snail_placeholder;
        switch (name.trim().toLowerCase(Locale.US)) {
            case "snail 1":
                return R.drawable.turbo_logo;
            case "snail 2":
                return R.drawable.nizza;
            case "snail 3":
                return R.drawable.chuppy;
            case "snail 4":
                return R.drawable.lady; // hoặc Burn nếu bạn đổi tên resource
            default:
                return R.drawable.ic_snail_placeholder;
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