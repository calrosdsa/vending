package com.tcn.sdk.springdemo.data.dao;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.SharedSQLiteStatement;
import com.tcn.sdk.springdemo.data.models.Shipment;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;

@SuppressWarnings("unchecked")
public class ShipmentDao_Impl implements ShipmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfShipment;

  private final SharedSQLiteStatement __preparedStmtOfUpdateShipmentState;

  public ShipmentDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfShipment = new EntityInsertionAdapter<Shipment>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `shipment`(`mId`,`mCreatedAt`,`mSlotNumber`,`mIdUser`,`mIsVerified`,`estado`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Shipment value) {
        if (value.mId == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.mId);
        }
        stmt.bindLong(2, value.mCreatedAt);
        stmt.bindLong(3, value.mSlotNumber);
        if (value.mIdUser == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.mIdUser);
        }
        final int _tmp;
        _tmp = value.mIsVerified ? 1 : 0;
        stmt.bindLong(5, _tmp);
        stmt.bindLong(6, value.estado);
      }
    };
    this.__preparedStmtOfUpdateShipmentState = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "update shipment set estado = ? where mId = ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(Shipment shipment) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfShipment.insert(shipment);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateShipmentState(int estado, String id) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateShipmentState.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      _stmt.bindLong(_argIndex, estado);
      _argIndex = 2;
      if (id == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, id);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateShipmentState.release(_stmt);
    }
  }
}
