package com.tcn.sdk.springdemo.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.User;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDao {

    @Query("select * from user")
    Flowable<User> observeUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
}
