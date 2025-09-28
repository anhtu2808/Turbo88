package com.anhtu.turbo88.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anhtu.turbo88.R;
import com.anhtu.turbo88.data.AppDatabase;
import com.anhtu.turbo88.data.dao.UserDao;
import com.anhtu.turbo88.data.entity.User;
import com.anhtu.turbo88.util.SessionManager;

public class MenuActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnPlay, btnSettings, btnLogout, btnExit;

    private SessionManager session;
    private UserDao userDao;

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

        String username = session.getUsername();
        if (username == null) {
            // chưa login -> về login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvWelcome.setText("Welcome, " + username);


        btnPlay.setOnClickListener(v -> {
            startActivity(new Intent(this, BettingActivity.class));
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            // khi có SettingsActivity: startActivity(new Intent(this, SettingsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(MenuActivity.this, LoginActivity.class));
            finish();
        });

        btnExit.setOnClickListener(v -> {
            finishAffinity();
        });
    }
}