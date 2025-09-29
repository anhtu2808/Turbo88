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

import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private TextView tvBetResult, tvBalance;
    private Button btnClose;

    private ImageView imgFirst, imgSecond, imgThird;
    private TextView tvFirstName, tvSecondName, tvThirdName;

    private SessionManager session;
    private UserDao userDao;


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
        tvFirstName = findViewById(R.id.tvFirstName);
        tvSecondName = findViewById(R.id.tvSecondName);
        tvThirdName = findViewById(R.id.tvThirdName);

        session = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDao();

        // Nhận dữ liệu từ Intent
        List<String> ranking = getIntent().getStringArrayListExtra("RANKING");
        String betResult = getIntent().getStringExtra("BET_RESULT");
        double newBalance = getIntent().getDoubleExtra("NEW_BALANCE", 0);

        // Hiển thị podium TOP 3
        setupPodium(ranking);

        tvBetResult.setText(betResult != null ? betResult : "");
        tvBalance.setText(String.format(Locale.US, "New Balance: %.2f$", newBalance));

        // Cập nhật DB (balance)
        String username = session.getUsername();
        if (username != null) {
            new Thread(() -> {
                User user = userDao.findByUsername(username);
                if (user != null) {
                    user.balance = newBalance;
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

    private void setupPodium(List<String> ranking) {
        // Ẩn/hiện theo dữ liệu
        if (ranking == null || ranking.isEmpty()) {
            imgFirst.setAlpha(0.3f);
            imgSecond.setAlpha(0.3f);
            imgThird.setAlpha(0.3f);
            tvFirstName.setText("-");
            tvSecondName.setText("-");
            tvThirdName.setText("-");
            return;
        }

        if (ranking.size() >= 1) {
            String first = ranking.get(0);
            tvFirstName.setText(getDisplayName(first));
            imgFirst.setImageResource(getSnailDrawable(first));
        } else {
            tvFirstName.setText("-");
            imgFirst.setAlpha(0.3f);
        }

        if (ranking.size() >= 2) {
            String second = ranking.get(1);
            tvSecondName.setText(getDisplayName(second));
            imgSecond.setImageResource(getSnailDrawable(second));
        } else {
            tvSecondName.setText("-");
            imgSecond.setAlpha(0.3f);
        }

        if (ranking.size() >= 3) {
            String third = ranking.get(2);
            tvThirdName.setText(getDisplayName(third));
            imgThird.setImageResource(getSnailDrawable(third));
        } else {
            tvThirdName.setText("-");
            imgThird.setAlpha(0.3f);
        }
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
                return R.drawable.lady; // TODO: đổi thành R.drawable.snail_burn nếu bạn rename file
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