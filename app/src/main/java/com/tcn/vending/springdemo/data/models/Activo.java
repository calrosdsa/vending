package com.tcn.vending.springdemo.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "activo")
public class Activo {
    @NonNull
    @PrimaryKey
    public String idCelda;
    public int slotN;
    public String celdaName;
    public String activoId;
    public String activoName;
    public String activoType;
    public String activoBrand;
    public String activoMode;
    public int cantidad;
    public double celdas;
    public int row;
    public boolean enabled;

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
