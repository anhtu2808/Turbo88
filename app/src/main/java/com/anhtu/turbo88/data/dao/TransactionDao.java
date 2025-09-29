package com.anhtu.turbo88.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.anhtu.turbo88.data.entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insertTransaction(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    List<Transaction> getTransactionsByUserId(int userId);
}
