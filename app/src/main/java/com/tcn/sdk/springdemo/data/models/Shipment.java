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
    public String mIdCelda;
    public String mIdActivo;
    public String mKeyActivo;
    public String mObjectType;
    public String mIdUser;
    public boolean mIsVerified = false;
    public int estado = ShipmentState.SHIPPING.ordinal();


    @Ignore
    public Shipment(String idCelda,String idUser,String id,String idActivo,String keyActivo,String objectType){
        this.mCreatedAt = System.currentTimeMillis();
        this.mIdCelda = idCelda;
        this.mIdUser = idUser;
        this.mId = id;
        this.mKeyActivo = keyActivo;
        this.mIdActivo = idActivo;
        this.mObjectType = objectType;
    }

    public Shipment() {

    }
//    public
}
