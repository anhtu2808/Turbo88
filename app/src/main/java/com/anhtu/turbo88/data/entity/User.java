package com.anhtu.turbo88.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    public String username;


    @NonNull
    @ColumnInfo(name = "password")
    public String password;


    @ColumnInfo(name = "balance", defaultValue = "0")
    public double balance;


    public User(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }
}