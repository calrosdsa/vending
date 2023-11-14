package com.tcn.sdk.springdemo.data.dao;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.RxRoom;
import android.database.Cursor;
import com.tcn.sdk.springdemo.data.models.Activo;
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
        return "INSERT OR REPLACE INTO `activo`(`idCelda`,`slotN`,`name`,`cantidad`,`celdas`,`row`,`objectType`,`enabled`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Activo value) {
        if (value.idCelda == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.idCelda);
        }
        stmt.bindLong(2, value.slotN);
        if (value.name == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.name);
        }
        stmt.bindLong(4, value.cantidad);
        stmt.bindLong(5, value.celdas);
        stmt.bindLong(6, value.row);
        if (value.objectType == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.objectType);
        }
        final int _tmp;
        _tmp = value.enabled ? 1 : 0;
        stmt.bindLong(8, _tmp);
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
          final int _cursorIndexOfName = _cursor.getColumnIndexOrThrow("name");
          final int _cursorIndexOfCantidad = _cursor.getColumnIndexOrThrow("cantidad");
          final int _cursorIndexOfCeldas = _cursor.getColumnIndexOrThrow("celdas");
          final int _cursorIndexOfRow = _cursor.getColumnIndexOrThrow("row");
          final int _cursorIndexOfObjectType = _cursor.getColumnIndexOrThrow("objectType");
          final int _cursorIndexOfEnabled = _cursor.getColumnIndexOrThrow("enabled");
          final List<Activo> _result = new ArrayList<Activo>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Activo _item;
            _item = new Activo();
            _item.idCelda = _cursor.getString(_cursorIndexOfIdCelda);
            _item.slotN = _cursor.getInt(_cursorIndexOfSlotN);
            _item.name = _cursor.getString(_cursorIndexOfName);
            _item.cantidad = _cursor.getInt(_cursorIndexOfCantidad);
            _item.celdas = _cursor.getInt(_cursorIndexOfCeldas);
            _item.row = _cursor.getInt(_cursorIndexOfRow);
            _item.objectType = _cursor.getString(_cursorIndexOfObjectType);
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
