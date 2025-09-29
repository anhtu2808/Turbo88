package com.anhtu.turbo88.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anhtu.turbo88.R;
import com.anhtu.turbo88.data.AppDatabase;
import com.anhtu.turbo88.data.dao.TransactionDao;
import com.anhtu.turbo88.data.dao.UserDao;
import com.anhtu.turbo88.data.entity.Transaction;
import com.anhtu.turbo88.data.entity.User;
import com.anhtu.turbo88.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class TopUpActivity extends AppCompatActivity {

    private TextView tvBalance;
    private TextInputEditText etAmount;
    private Button btnTopUp, btnWithdraw, btnHistory;

    private AppDatabase db;
    private UserDao userDao;
    private TransactionDao transactionDao;
    private User currentUser;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize DB, DAOs and SessionManager
        db = AppDatabase.getInstance(this);
        userDao = db.userDao();
        transactionDao = db.transactionDao();
        sessionManager = new SessionManager(this);

        // Get current user by username from session
        String username = sessionManager.getUsername();
        if (username == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUser = userDao.findByUsername(username);

        if (currentUser == null) {
            Toast.makeText(this, "Error: Could not find user data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find views
        tvBalance = findViewById(R.id.tv_balance);
        etAmount = findViewById(R.id.et_amount);
        btnTopUp = findViewById(R.id.btn_top_up);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnHistory = findViewById(R.id.btn_history);

        updateBalanceDisplay();

        // Set click listeners
        btnTopUp.setOnClickListener(v -> handleTransaction("TOPUP"));
        btnWithdraw.setOnClickListener(v -> handleTransaction("WITHDRAW"));
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(TopUpActivity.this, TransactionHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void updateBalanceDisplay() {
        // Refresh user data from DB before displaying
        currentUser = userDao.findById(currentUser.getId());
        tvBalance.setText(String.format(Locale.US, "$%.2f", currentUser.getBalance()));
    }

    private void handleTransaction(String type) {
        String amountStr = etAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Amount must be positive", Toast.LENGTH_SHORT).show();
            return;
        }

        if (type.equals("WITHDRAW")) {
            if (currentUser.getBalance() < amount) {
                Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                return;
            }
            currentUser.setBalance(currentUser.getBalance() - amount);
        } else { // TOPUP
            currentUser.setBalance(currentUser.getBalance() + amount);
        }

        // Update user in DB
        userDao.updateUser(currentUser);

        // Create and insert transaction record
        Transaction transaction = new Transaction(currentUser.getId(), type, amount, System.currentTimeMillis());
        transactionDao.insertTransaction(transaction);

        // Update UI
        updateBalanceDisplay();
        etAmount.setText("");
        Toast.makeText(this, type + " successful!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            updateBalanceDisplay();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
