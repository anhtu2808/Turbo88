package com.anhtu.turbo88.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "racers")
public class Racer {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "code")
    public String code;

    @NonNull
    @ColumnInfo(name = "url-image")
    public String urlImage;

    @ColumnInfo(name = "speed")
    public double speed;

    public Racer(@NonNull String code, @NonNull String urlImage, double speed) {
        this.code = code;
        this.urlImage = urlImage;
        this.speed = speed;
    }

}
