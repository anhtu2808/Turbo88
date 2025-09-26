package com.anhtu.turbo88.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anhtu.turbo88.R;
import com.anhtu.turbo88.util.SessionManager;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SessionManager session = new SessionManager(SplashActivity.this);
            if (session.isLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, MenuActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_MS);
    }
}