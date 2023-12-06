package com.tcn.vending.springdemo.data.dao;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.RxRoom;
import android.database.Cursor;
import com.tcn.vending.springdemo.data.models.Activo;
import io.reactivex.Flowable;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("unchecked")
public class ActivoDao_Impl implements ActivoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfActivo;

  public ActivoDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfActivo = new EntityInsertionAdapter<Activo>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `activo`(`idCelda`,`slotN`,`celdaName`,`activoId`,`activoName`,`activoType`,`activoBrand`,`activoMode`,`cantidad`,`celdas`,`row`,`enabled`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Activo value) {
        if (value.idCelda == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.idCelda);
        }
        stmt.bindLong(2, value.slotN);
        if (value.celdaName == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.celdaName);
        }
        if (value.activoId == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.activoId);
        }
        if (value.activoName == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.activoName);
        }
        if (value.activoType == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.activoType);
        }
        if (value.activoBrand == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.activoBrand);
        }
        if (value.activoMode == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.activoMode);
        }
        stmt.bindLong(9, value.cantidad);
        stmt.bindDouble(10, value.celdas);
        stmt.bindLong(11, value.row);
        final int _tmp;
        _tmp = value.enabled ? 1 : 0;
        stmt.bindLong(12, _tmp);
      }
    };
  }

  @Override
  public void insertActivos(List<Activo> activos) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfActivo.insert(activos);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Flowable<List<Activo>> observeActivos() {
    final String _sql = "select * from activo";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, new String[]{"activo"}, new Callable<List<Activo>>() {
      @Override
      public List<Activo> call() throws Exception {
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfIdCelda = _cursor.getColumnIndexOrThrow("idCelda");
          final int _cursorIndexOfSlotN = _cursor.getColumnIndexOrThrow("slotN");
          final int _cursorIndexOfCeldaName = _cursor.getColumnIndexOrThrow("celdaName");
          final int _cursorIndexOfActivoId = _cursor.getColumnIndexOrThrow("activoId");
          final int _cursorIndexOfActivoName = _cursor.getColumnIndexOrThrow("activoName");
          final int _cursorIndexOfActivoType = _cursor.getColumnIndexOrThrow("activoType");
          final int _cursorIndexOfActivoBrand = _cursor.getColumnIndexOrThrow("activoBrand");
          final int _cursorIndexOfActivoMode = _cursor.getColumnIndexOrThrow("activoMode");
          final int _cursorIndexOfCantidad = _cursor.getColumnIndexOrThrow("cantidad");
          final int _cursorIndexOfCeldas = _cursor.getColumnIndexOrThrow("celdas");
          final int _cursorIndexOfRow = _cursor.getColumnIndexOrThrow("row");
          final int _cursorIndexOfEnabled = _cursor.getColumnIndexOrThrow("enabled");
          final List<Activo> _result = new ArrayList<Activo>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Activo _item;
            _item = new Activo();
            _item.idCelda = _cursor.getString(_cursorIndexOfIdCelda);
            _item.slotN = _cursor.getInt(_cursorIndexOfSlotN);
            _item.celdaName = _cursor.getString(_cursorIndexOfCeldaName);
            _item.activoId = _cursor.getString(_cursorIndexOfActivoId);
            _item.activoName = _cursor.getString(_cursorIndexOfActivoName);
            _item.activoType = _cursor.getString(_cursorIndexOfActivoType);
            _item.activoBrand = _cursor.getString(_cursorIndexOfActivoBrand);
            _item.activoMode = _cursor.getString(_cursorIndexOfActivoMode);
            _item.cantidad = _cursor.getInt(_cursorIndexOfCantidad);
            _item.celdas = _cursor.getDouble(_cursorIndexOfCeldas);
            _item.row = _cursor.getInt(_cursorIndexOfRow);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEnabled);
            _item.enabled = _tmp != 0;
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }
}
