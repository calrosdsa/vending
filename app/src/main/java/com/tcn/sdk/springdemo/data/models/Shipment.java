package com.tcn.sdk.springdemo.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "shipment")
public class Shipment {
    @NonNull
    @PrimaryKey
    public String mId;
    public long mCreatedAt;
    public int mSlotNumber;
    public String mIdUser;
    public boolean mIsVerified = false;
    public int estado = ShipmentState.SHIPPING.ordinal();


    @Ignore
    public Shipment(int slotNumber,String idUser,String id){
        this.mCreatedAt = System.currentTimeMillis();
        this.mSlotNumber = slotNumber;
        this.mIdUser = idUser;
        this.mId = id;
    }

    public Shipment() {

    }
//    public
}
