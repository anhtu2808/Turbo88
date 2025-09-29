package com.anhtu.turbo88.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
import com.anhtu.turbo88.ui.adapter.TransactionAdapter;
import com.anhtu.turbo88.util.SessionManager;

import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private ListView lvTransactions;
    private TextView tvNoTransactions;

    private AppDatabase db;
    private TransactionDao transactionDao;
    private UserDao userDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize DB and SessionManager
        db = AppDatabase.getInstance(this);
        transactionDao = db.transactionDao();
        userDao = db.userDao();
        sessionManager = new SessionManager(this);

        // Find views
        lvTransactions = findViewById(R.id.lv_transactions);
        tvNoTransactions = findViewById(R.id.tv_no_transactions);

        // Get current user from session username
        String username = sessionManager.getUsername();
        if (username == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        User currentUser = userDao.findByUsername(username);
        if (currentUser == null) {
            Toast.makeText(this, "Error: User not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load and display transactions
        List<Transaction> transactions = transactionDao.getTransactionsByUserId(currentUser.getId());

        if (transactions == null || transactions.isEmpty()) {
            tvNoTransactions.setVisibility(View.VISIBLE);
            lvTransactions.setVisibility(View.GONE);
        } else {
            tvNoTransactions.setVisibility(View.GONE);
            lvTransactions.setVisibility(View.VISIBLE);
            TransactionAdapter adapter = new TransactionAdapter(this, transactions);
            lvTransactions.setAdapter(adapter);
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
