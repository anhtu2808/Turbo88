package com.anhtu.turbo88.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.anhtu.turbo88.data.entity.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE username = :u AND password = :p LIMIT 1")
    User login(String u, String p);

    @Query("SELECT * FROM users WHERE username = :u LIMIT 1")
    User findByUsername(String u);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User findById(int id);

    @Update
    void updateUser(User user);


}

