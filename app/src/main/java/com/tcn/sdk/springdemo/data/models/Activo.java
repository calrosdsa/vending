package com.tcn.sdk.springdemo.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "activo")
public class Activo {
    @NonNull
    @PrimaryKey
    public String id;
    public int slotN;
    public String name;
    public int cantidad;
    public int celdas;
    public int row;

//    @Ignore
//    public Activo(int id,int slotNumber,int rowNumber){
//        this.mId = id;
//        this.mSlotNumber = slotNumber;
//        this.mRowNumber = rowNumber;
//        this.mCanMerged = canMerged;
//    }

    public Activo() {
    }

}
