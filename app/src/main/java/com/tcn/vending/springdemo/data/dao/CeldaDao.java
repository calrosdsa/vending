package com.tcn.vending.springdemo.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.tcn.vending.springdemo.data.models.Celda;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CeldaDao {

    @Query("select * from celda")
    Flowable<List<Celda>> observeCeldas();


    @Query("update celda set mIsMerged = :isMerged ,mCanShow = :show where mSlotNumber = :slotN")
    void mergeCelda(boolean isMerged,boolean show,int slotN);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     void insert(Celda celdas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Celda> celdas);
}
