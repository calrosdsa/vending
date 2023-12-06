package com.tcn.vending.springdemo.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.tcn.vending.springdemo.data.models.Shipment;

import java.util.List;

@Dao
public interface ShipmentDao {
    @Query("select * from shipment where mIsVerified = 0 and estado = :estado")
    List<Shipment> getUnverifiedSuccessfullShipments(int estado);

    @Query("select * from shipment where mId = :id")
    Shipment getShipment(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Shipment shipment);

    @Query("update shipment set estado = :estado where mId = :id")
    void updateShipmentState(int estado,String id);
    @Query("update shipment set mIsVerified = 1 where mId = :id")
    void setSuccessfullShipment(String id);

}
