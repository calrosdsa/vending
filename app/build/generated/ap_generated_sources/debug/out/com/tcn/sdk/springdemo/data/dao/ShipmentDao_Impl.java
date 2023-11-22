package com.tcn.sdk.springdemo.data.dao;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import com.tcn.sdk.springdemo.data.models.Shipment;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ShipmentDao_Impl implements ShipmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfShipment;

  private final SharedSQLiteStatement __preparedStmtOfUpdateShipmentState;

  private final SharedSQLiteStatement __preparedStmtOfSetSuccessfullShipment;

  public ShipmentDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfShipment = new EntityInsertionAdapter<Shipment>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `shipment`(`mId`,`mCreatedAt`,`mIdCelda`,`mIdActivo`,`mKeyActivo`,`mObjectType`,`mIdUser`,`mIsVerified`,`estado`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Shipment value) {
        if (value.mId == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.mId);
        }
        stmt.bindLong(2, value.mCreatedAt);
        if (value.mIdCelda == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.mIdCelda);
        }
        if (value.mIdActivo == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.mIdActivo);
        }
        if (value.mKeyActivo == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.mKeyActivo);
        }
        if (value.mObjectType == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.mObjectType);
        }
        if (value.mIdUser == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.mIdUser);
        }
        final int _tmp;
        _tmp = value.mIsVerified ? 1 : 0;
        stmt.bindLong(8, _tmp);
        stmt.bindLong(9, value.estado);
      }
    };
    this.__preparedStmtOfUpdateShipmentState = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "update shipment set estado = ? where mId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetSuccessfullShipment = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "update shipment set mIsVerified = 1 where mId = ?";
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

  @Override
  public void setSuccessfullShipment(String id) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfSetSuccessfullShipment.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (id == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, id);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfSetSuccessfullShipment.release(_stmt);
    }
  }

  @Override
  public List<Shipment> getUnverifiedSuccessfullShipments(int estado) {
    final String _sql = "select * from shipment where mIsVerified = 0 and estado = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, estado);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfMId = _cursor.getColumnIndexOrThrow("mId");
      final int _cursorIndexOfMCreatedAt = _cursor.getColumnIndexOrThrow("mCreatedAt");
      final int _cursorIndexOfMIdCelda = _cursor.getColumnIndexOrThrow("mIdCelda");
      final int _cursorIndexOfMIdActivo = _cursor.getColumnIndexOrThrow("mIdActivo");
      final int _cursorIndexOfMKeyActivo = _cursor.getColumnIndexOrThrow("mKeyActivo");
      final int _cursorIndexOfMObjectType = _cursor.getColumnIndexOrThrow("mObjectType");
      final int _cursorIndexOfMIdUser = _cursor.getColumnIndexOrThrow("mIdUser");
      final int _cursorIndexOfMIsVerified = _cursor.getColumnIndexOrThrow("mIsVerified");
      final int _cursorIndexOfEstado = _cursor.getColumnIndexOrThrow("estado");
      final List<Shipment> _result = new ArrayList<Shipment>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Shipment _item;
        _item = new Shipment();
        _item.mId = _cursor.getString(_cursorIndexOfMId);
        _item.mCreatedAt = _cursor.getLong(_cursorIndexOfMCreatedAt);
        _item.mIdCelda = _cursor.getString(_cursorIndexOfMIdCelda);
        _item.mIdActivo = _cursor.getString(_cursorIndexOfMIdActivo);
        _item.mKeyActivo = _cursor.getString(_cursorIndexOfMKeyActivo);
        _item.mObjectType = _cursor.getString(_cursorIndexOfMObjectType);
        _item.mIdUser = _cursor.getString(_cursorIndexOfMIdUser);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfMIsVerified);
        _item.mIsVerified = _tmp != 0;
        _item.estado = _cursor.getInt(_cursorIndexOfEstado);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Shipment getShipment(String id) {
    final String _sql = "select * from shipment where mId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfMId = _cursor.getColumnIndexOrThrow("mId");
      final int _cursorIndexOfMCreatedAt = _cursor.getColumnIndexOrThrow("mCreatedAt");
      final int _cursorIndexOfMIdCelda = _cursor.getColumnIndexOrThrow("mIdCelda");
      final int _cursorIndexOfMIdActivo = _cursor.getColumnIndexOrThrow("mIdActivo");
      final int _cursorIndexOfMKeyActivo = _cursor.getColumnIndexOrThrow("mKeyActivo");
      final int _cursorIndexOfMObjectType = _cursor.getColumnIndexOrThrow("mObjectType");
      final int _cursorIndexOfMIdUser = _cursor.getColumnIndexOrThrow("mIdUser");
      final int _cursorIndexOfMIsVerified = _cursor.getColumnIndexOrThrow("mIsVerified");
      final int _cursorIndexOfEstado = _cursor.getColumnIndexOrThrow("estado");
      final Shipment _result;
      if(_cursor.moveToFirst()) {
        _result = new Shipment();
        _result.mId = _cursor.getString(_cursorIndexOfMId);
        _result.mCreatedAt = _cursor.getLong(_cursorIndexOfMCreatedAt);
        _result.mIdCelda = _cursor.getString(_cursorIndexOfMIdCelda);
        _result.mIdActivo = _cursor.getString(_cursorIndexOfMIdActivo);
        _result.mKeyActivo = _cursor.getString(_cursorIndexOfMKeyActivo);
        _result.mObjectType = _cursor.getString(_cursorIndexOfMObjectType);
        _result.mIdUser = _cursor.getString(_cursorIndexOfMIdUser);
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfMIsVerified);
        _result.mIsVerified = _tmp != 0;
        _result.estado = _cursor.getInt(_cursorIndexOfEstado);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
