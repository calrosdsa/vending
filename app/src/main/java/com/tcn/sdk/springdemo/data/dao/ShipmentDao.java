package com.tcn.sdk.springdemo.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.Shipment;
@Dao
public interface ShipmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Shipment shipment);

    @Query("update shipment set estado = :estado where mId = :id")
    void updateShipmentState(int estado,String id);

}
