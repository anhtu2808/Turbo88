package com.anhtu.turbo88.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anhtu.turbo88.R;
import com.anhtu.turbo88.data.AppDatabase;
import com.anhtu.turbo88.data.dao.UserDao;
import com.anhtu.turbo88.util.SessionManager;
import com.anhtu.turbo88.util.SoundManager;

public class MenuActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnPlay, btnSettings, btnLogout, btnExit;

    private SessionManager session;
    private UserDao userDao;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnPlay = findViewById(R.id.btnPlay);
        btnSettings = findViewById(R.id.btnSettings);
        btnLogout = findViewById(R.id.btnLogout);
        btnExit = findViewById(R.id.btnExit);

        session = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDao();
        soundManager = SoundManager.getInstance(this);

        String username = session.getUsername();
        if (username == null) {
            // chưa login -> về login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvWelcome.setText("Welcome, " + username);

        // Bắt đầu nhạc nền menu
        soundManager.playBgm();

        btnPlay.setOnClickListener(v -> {
            startActivity(new Intent(this, BettingActivity.class));
        });

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            soundManager.stopBgm(); // dừng nhạc khi logout
            session.logout();
            startActivity(new Intent(MenuActivity.this, LoginActivity.class));
            finish();
        });

        btnExit.setOnClickListener(v -> {
            soundManager.release(); // giải phóng nhạc khi thoát
            finishAffinity();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Luôn update tên từ session khi quay lại menu
        String username = session.getUsername();
        if (username != null) {
            tvWelcome.setText("Welcome, " + username);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Nếu bạn muốn nhạc chỉ chạy ở menu thôi thì có thể stop ở đây
        // soundManager.stopBgm();
    }
}
