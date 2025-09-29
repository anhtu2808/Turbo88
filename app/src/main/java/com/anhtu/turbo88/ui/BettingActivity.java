package com.anhtu.turbo88.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anhtu.turbo88.MainActivity;
import com.anhtu.turbo88.R;
import com.anhtu.turbo88.data.AppDatabase;
import com.anhtu.turbo88.data.dao.UserDao;
import com.anhtu.turbo88.data.entity.User;
import com.anhtu.turbo88.util.SessionManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BettingActivity extends AppCompatActivity {

    // Khai báo các biến cho view
    private TextView tvBalance;
    private TextView tvTotalBet;
    private Button btnStartRace, btnTopUp;

    private CheckBox cbSnail1, cbSnail2, cbSnail3, cbSnail4;
    private EditText etBetAmount1, etBetAmount2, etBetAmount3, etBetAmount4;

    // Biến lưu trữ trạng thái
    private double currentBalance;
    private final HashMap<Integer, Integer> bets = new HashMap<>(); // Lưu trữ tiền cược

    private SessionManager session;
    private UserDao userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_betting);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Gán các biến với các component trong layout
        initViews();

        session = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDao();

        loadAndSetUserBalance();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndSetUserBalance();
    }

    private void initViews() {
        tvBalance = findViewById(R.id.tvBalance);
        tvTotalBet = findViewById(R.id.tvTotalBet);
        btnStartRace = findViewById(R.id.btnStartRace);
        btnTopUp = findViewById(R.id.btn_top_up);

        cbSnail1 = findViewById(R.id.cbSnail1);
        cbSnail2 = findViewById(R.id.cbSnail2);
        cbSnail3 = findViewById(R.id.cbSnail3);
        cbSnail4 = findViewById(R.id.cbSnail4);

        etBetAmount1 = findViewById(R.id.etBetAmount1);
        etBetAmount2 = findViewById(R.id.etBetAmount2);
        etBetAmount3 = findViewById(R.id.etBetAmount3);
        etBetAmount4 = findViewById(R.id.etBetAmount4);
    }

    private void loadAndSetUserBalance() {
        String username = session.getUsername();
        if (username != null) {
            new Thread(() -> {
                currentUser = userDao.findByUsername(username);
                runOnUiThread(this::setUserBalance);
            }).start();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUserBalance() {
        if (currentUser != null) {
            currentBalance = currentUser.getBalance();
            tvBalance.setText("Balance: " + currentBalance + "$");
        }
    }

    private void setupListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalBet();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etBetAmount1.addTextChangedListener(textWatcher);
        etBetAmount2.addTextChangedListener(textWatcher);
        etBetAmount3.addTextChangedListener(textWatcher);
        etBetAmount4.addTextChangedListener(textWatcher);

        CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> updateTotalBet();

        cbSnail1.setOnCheckedChangeListener(checkedChangeListener);
        cbSnail2.setOnCheckedChangeListener(checkedChangeListener);
        cbSnail3.setOnCheckedChangeListener(checkedChangeListener);
        cbSnail4.setOnCheckedChangeListener(checkedChangeListener);

        btnStartRace.setOnClickListener(v -> handleStartRace());
        btnTopUp.setOnClickListener(v -> {
            Intent intent = new Intent(BettingActivity.this, TopUpActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateTotalBet() {
        int totalBet = 0;
        bets.clear();

        totalBet += addBet(cbSnail1, etBetAmount1, 1);
        totalBet += addBet(cbSnail2, etBetAmount2, 2);
        totalBet += addBet(cbSnail3, etBetAmount3, 3);
        totalBet += addBet(cbSnail4, etBetAmount4, 4);

        tvTotalBet.setText("Total Bet: " + totalBet + "$");
    }

    private int addBet(CheckBox checkBox, EditText editText, int snailId) {
        if (checkBox.isChecked()) {
            String amountStr = editText.getText().toString();
            if (!amountStr.isEmpty()) {
                try {
                    int amount = Integer.parseInt(amountStr);
                    if (amount > 0) {
                        bets.put(snailId, amount);
                        return amount;
                    }
                } catch (NumberFormatException e) {
                    // Not a valid number
                }
            }
        }
        return 0;
    }

    private void handleStartRace() {
        if (currentUser == null) {
            Toast.makeText(this, "User data is loading, please wait.", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalBet = 0;
        for (int amount : bets.values()) {
            totalBet += amount;
        }

        if (bets.isEmpty()) {
            Toast.makeText(this, "Please select a snail and enter a bet amount.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalBet > currentBalance) {
            Toast.makeText(this, "Your total bet exceeds your current balance.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Bets placed! Starting race...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("BETS_MAP", bets); // Serializable
        intent.putExtra("BALANCE", currentBalance);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
