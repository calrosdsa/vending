package com.tcn.vending.springdemo.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.tcn.vending.springdemo.data.models.Activo;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ActivoDao {
    @Query("select * from activo")
    Flowable<List<Activo>> observeActivos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivos(List<Activo> activos);
}
