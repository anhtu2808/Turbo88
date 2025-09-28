package com.anhtu.turbo88.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anhtu.turbo88.R;
import com.anhtu.turbo88.util.SessionManager;
import com.anhtu.turbo88.util.SoundManager;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SeekBar seekMusic, seekSfx;
    private Switch switchMute;
    private EditText etCurrentUsername, etNewUsername;
    private Button btnChangeUsername, btnSave;
    private LinearLayout btnPrivacy, btnTerms;

    private SharedPreferences preferences;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // SharedPreferences + Session
        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        session = new SessionManager(this);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Sound controls
        seekMusic = findViewById(R.id.seek_music);
        seekSfx = findViewById(R.id.seek_sfx);
        switchMute = findViewById(R.id.switch_mute);

        // Account controls
        etCurrentUsername = findViewById(R.id.et_current_username);
        etNewUsername = findViewById(R.id.et_new_username);
        btnChangeUsername = findViewById(R.id.btn_change_username);

        // Other controls
        btnPrivacy = findViewById(R.id.btn_privacy);
        btnTerms = findViewById(R.id.btn_terms);
        btnSave = findViewById(R.id.btn_save);

        // Load saved settings
        loadSettings();

        // Change username
        btnChangeUsername.setOnClickListener(v -> changeUsername());

        // Privacy
        btnPrivacy.setOnClickListener(v -> openPrivacyPolicy());

        // Terms
        btnTerms.setOnClickListener(v -> openTermsOfService());

        // Save
        btnSave.setOnClickListener(v -> saveSettings());

        // Listener realtime cho SeekBar
        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    applySettings(progress, seekSfx.getProgress(), switchMute.isChecked());
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekSfx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    applySettings(seekMusic.getProgress(), progress, switchMute.isChecked());
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        switchMute.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applySettings(seekMusic.getProgress(), seekSfx.getProgress(), isChecked);
        });
    }

    // === FUNCTION XỬ LÝ ===

    private void changeUsername() {
        String current = etCurrentUsername.getText().toString().trim();
        String newUser = etNewUsername.getText().toString().trim();

        if (newUser.isEmpty()) {
            Toast.makeText(this, "Please enter new username", Toast.LENGTH_SHORT).show();
        } else if (newUser.equals(current)) {
            Toast.makeText(this, "New username is the same as current", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username changed from " + current + " to " + newUser, Toast.LENGTH_SHORT).show();
            etCurrentUsername.setText(newUser);
            etNewUsername.setText("");
            session.setUsername(newUser);
        }
    }


    private void openPrivacyPolicy() {
        Intent intent = new Intent(this, PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    private void openTermsOfService() {
        Intent intent = new Intent(this, TermsOfServiceActivity.class);
        startActivity(intent);
    }

    private void saveSettings() {
        int musicLevel = seekMusic.getProgress();
        int sfxLevel = seekSfx.getProgress();
        boolean isMuted = switchMute.isChecked();

        // Lưu và apply ngay
        applySettings(musicLevel, sfxLevel, isMuted);

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }

    private void loadSettings() {
        // Ưu tiên lấy từ session
        String username = session.getUsername();
        if (username == null || username.isEmpty()) {
            username = preferences.getString("username", "Guest");
        }

        int musicLevel = preferences.getInt("music_level", 50);
        int sfxLevel = preferences.getInt("sfx_level", 50);
        boolean isMuted = preferences.getBoolean("mute", false);

        etCurrentUsername.setText(username);
        etCurrentUsername.setEnabled(false); // chỉ hiển thị, không cho sửa
        seekMusic.setProgress(musicLevel);
        seekSfx.setProgress(sfxLevel);
        switchMute.setChecked(isMuted);

        // Áp dụng settings khi mở trang
        applySettings(musicLevel, sfxLevel, isMuted);
    }

    private void applySettings(int musicLevel, int sfxLevel, boolean isMuted) {
        // Lưu vào SharedPreferences
        preferences.edit()
                .putInt("music_level", musicLevel)
                .putInt("sfx_level", sfxLevel)
                .putBoolean("mute", isMuted)
                .apply();

        // Gọi SoundManager để cập nhật ngay
        SoundManager sm = SoundManager.getInstance(this);
        sm.loadSettings(); // đọc lại từ SharedPreferences và áp dụng volume
    }
}
