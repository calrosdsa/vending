package com.tcn.sdk.springdemo.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
@Entity(tableName = "celda")
public class Celda {
    @PrimaryKey
    public int mId;
    public int mSlotNumber;
    public int mRowNumber;
    public boolean mIsMerged = false;
    public boolean mCanMerged = false;
    public boolean enabled = true;

    public boolean mCanShow = true;

    @Ignore
    public Celda(int id,int slotNumber,int rowNumber,boolean canMerged){
        this.mId = id;
        this.mSlotNumber = slotNumber;
        this.mRowNumber = rowNumber;
        this.mCanMerged = canMerged;
    }

    public Celda() {
    }
//
//    public Celda() {
//
//    }
//
//
//    public int getId() {
//        return id;
//    }
//
//    public int getSlotNumber() {
//        return slotNumber;
//    }
}
