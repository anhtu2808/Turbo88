package com.anhtu.turbo88.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anhtu.turbo88.R;
import com.anhtu.turbo88.data.entity.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    public TransactionAdapter(@NonNull Context context, @NonNull List<Transaction> transactions) {
        super(context, 0, transactions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Transaction transaction = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_transaction, parent, false);
        }

        // Lookup view for data population
        TextView tvType = convertView.findViewById(R.id.tv_transaction_type);
        TextView tvTimestamp = convertView.findViewById(R.id.tv_transaction_timestamp);
        TextView tvAmount = convertView.findViewById(R.id.tv_transaction_amount);

        // Populate the data into the template view using the data object
        if (transaction != null) {
            tvType.setText(transaction.getType());

            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateString = sdf.format(new Date(transaction.getTimestamp()));
            tvTimestamp.setText(dateString);

            // Format amount and set color
            if ("TOPUP".equalsIgnoreCase(transaction.getType())) {
                tvAmount.setText(String.format(Locale.US, "+$%.2f", transaction.getAmount()));
                tvAmount.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else { // WITHDRAW
                tvAmount.setText(String.format(Locale.US, "-$%.2f", transaction.getAmount()));
                tvAmount.setTextColor(Color.parseColor("#F44336")); // Red
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
