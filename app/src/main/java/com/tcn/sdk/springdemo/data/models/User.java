package com.tcn.sdk.springdemo.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;


@Entity(tableName = "user")
public class User {
    @NonNull
    @PrimaryKey
    public String mId;

    public String mName;
    public long mCreatedAt;


    @Ignore
    public User(String id,String name){
        this.mId = id;
        this.mName = name;
        this.mCreatedAt = System.currentTimeMillis();
    }


    public User() {

    }
//
//    public Celda() {
//
//    }
//
//
//    public int getId() {
//        return id;
//    }
//
//    public int getSlotNumber() {
//        return slotNumber;
//    }
}
