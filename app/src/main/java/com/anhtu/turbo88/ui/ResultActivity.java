package com.anhtu.turbo88.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anhtu.turbo88.R;
import com.anhtu.turbo88.data.AppDatabase;
import com.anhtu.turbo88.data.dao.UserDao;
import com.anhtu.turbo88.data.entity.User;
import com.anhtu.turbo88.util.SessionManager;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private TextView tvRanking, tvBetResult, tvBalance;
    private Button btnClose;
    private SessionManager session;
    private UserDao userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvRanking = findViewById(R.id.tvRanking);
        tvBetResult = findViewById(R.id.tvBetResult);
        tvBalance = findViewById(R.id.tvBalance);
        btnClose = findViewById(R.id.btnClose);

        session = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDao();

        // Nhận dữ liệu từ MainActivity

        List<String> ranking = getIntent().getStringArrayListExtra("RANKING");
        String betResult = getIntent().getStringExtra("BET_RESULT");
        double newBalance = getIntent().getDoubleExtra("NEW_BALANCE", 0);

        // Hiển thị ranking
        if (ranking != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ranking.size(); i++) {
                sb.append((i + 1)).append(". ").append(ranking.get(i)).append("\n");
            }
            tvRanking.setText(sb.toString());
        }

        tvBetResult.setText(betResult);
        tvBalance.setText("New Balance: " + newBalance + "$");

        // Cập nhật DB
        String username = new SessionManager(this).getUsername();
        if (username != null) {
            new Thread(() -> {
                UserDao userDao = AppDatabase.getInstance(this).userDao();
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


}
