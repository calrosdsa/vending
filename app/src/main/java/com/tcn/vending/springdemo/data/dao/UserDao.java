package com.tcn.vending.springdemo.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.tcn.vending.springdemo.data.models.User;

import io.reactivex.Flowable;

@Dao
public interface UserDao {

    @Query("select * from user")
    Flowable<User> observeUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
}
