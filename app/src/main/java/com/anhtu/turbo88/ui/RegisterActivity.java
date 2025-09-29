package com.anhtu.turbo88.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import com.anhtu.turbo88.util.SoundManager;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etConfirm;
    private Button btnRegister;

    private UserDao userDao;

    private SoundManager soundManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirm = findViewById(R.id.etRegConfirm);
        btnRegister = findViewById(R.id.btnRegisterAction);

        userDao = AppDatabase.getInstance(this).userDao();

        // Nhạc nền
        soundManager = SoundManager.getInstance(this);
        soundManager.playBgm();

        btnRegister.setOnClickListener(v -> {
            final String u = etUsername.getText().toString().trim();
            final String p = etPassword.getText().toString().trim();
            final String c = etConfirm.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty() || c.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!p.equals(c)) {
                Toast.makeText(this, "Password và Confirm không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // background: kiểm tra tồn tại rồi insert
            new Thread(() -> {
                User exist = userDao.findByUsername(u);
                runOnUiThread(() -> {
                    if (exist != null) {
                        Toast.makeText(RegisterActivity.this, "Username đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(() -> {
                            try {
                                User newUser = new User(u, p);
                                newUser.balance = 1000.0; // khởi tạo balance mặc định
                                userDao.insertUser(newUser);
                                runOnUiThread(() -> {
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công. Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
                                    finish(); // trở về login
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                runOnUiThread(() ->
                                        Toast.makeText(RegisterActivity.this, "Lỗi khi đăng ký: " + ex.getMessage(), Toast.LENGTH_LONG).show()
                                );
                            }
                        }).start();
                    }
                });
            }).start();
        });
    }
}