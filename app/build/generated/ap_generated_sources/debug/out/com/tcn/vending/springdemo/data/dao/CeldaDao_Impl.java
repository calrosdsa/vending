package com.tcn.vending.springdemo.data.dao;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.RxRoom;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.database.Cursor;
import com.tcn.vending.springdemo.data.models.Celda;
import io.reactivex.Flowable;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("unchecked")
public class CeldaDao_Impl implements CeldaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfCelda;

  private final SharedSQLiteStatement __preparedStmtOfMergeCelda;

  public CeldaDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCelda = new EntityInsertionAdapter<Celda>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `celda`(`mId`,`mSlotNumber`,`mRowNumber`,`mIsMerged`,`mCanMerged`,`enabled`,`mCanShow`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Celda value) {
        stmt.bindLong(1, value.mId);
        stmt.bindLong(2, value.mSlotNumber);
        stmt.bindLong(3, value.mRowNumber);
        final int _tmp;
        _tmp = value.mIsMerged ? 1 : 0;
        stmt.bindLong(4, _tmp);
        final int _tmp_1;
        _tmp_1 = value.mCanMerged ? 1 : 0;
        stmt.bindLong(5, _tmp_1);
        final int _tmp_2;
        _tmp_2 = value.enabled ? 1 : 0;
        stmt.bindLong(6, _tmp_2);
        final int _tmp_3;
        _tmp_3 = value.mCanShow ? 1 : 0;
        stmt.bindLong(7, _tmp_3);
      }
    };
    this.__preparedStmtOfMergeCelda = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "update celda set mIsMerged = ? ,mCanShow = ? where mSlotNumber = ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(Celda celdas) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfCelda.insert(celdas);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertAll(List<Celda> celdas) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfCelda.insert(celdas);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void mergeCelda(boolean isMerged, boolean show, int slotN) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfMergeCelda.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      final int _tmp;
      _tmp = isMerged ? 1 : 0;
      _stmt.bindLong(_argIndex, _tmp);
      _argIndex = 2;
      final int _tmp_1;
      _tmp_1 = show ? 1 : 0;
      _stmt.bindLong(_argIndex, _tmp_1);
      _argIndex = 3;
      _stmt.bindLong(_argIndex, slotN);
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfMergeCelda.release(_stmt);
    }
  }

  @Override
  public Flowable<List<Celda>> observeCeldas() {
    final String _sql = "select * from celda";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, new String[]{"celda"}, new Callable<List<Celda>>() {
      @Override
      public List<Celda> call() throws Exception {
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfMId = _cursor.getColumnIndexOrThrow("mId");
          final int _cursorIndexOfMSlotNumber = _cursor.getColumnIndexOrThrow("mSlotNumber");
          final int _cursorIndexOfMRowNumber = _cursor.getColumnIndexOrThrow("mRowNumber");
          final int _cursorIndexOfMIsMerged = _cursor.getColumnIndexOrThrow("mIsMerged");
          final int _cursorIndexOfMCanMerged = _cursor.getColumnIndexOrThrow("mCanMerged");
          final int _cursorIndexOfEnabled = _cursor.getColumnIndexOrThrow("enabled");
          final int _cursorIndexOfMCanShow = _cursor.getColumnIndexOrThrow("mCanShow");
          final List<Celda> _result = new ArrayList<Celda>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Celda _item;
            _item = new Celda();
            _item.mId = _cursor.getInt(_cursorIndexOfMId);
            _item.mSlotNumber = _cursor.getInt(_cursorIndexOfMSlotNumber);
            _item.mRowNumber = _cursor.getInt(_cursorIndexOfMRowNumber);
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfMIsMerged);
            _item.mIsMerged = _tmp != 0;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMCanMerged);
            _item.mCanMerged = _tmp_1 != 0;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfEnabled);
            _item.enabled = _tmp_2 != 0;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfMCanShow);
            _item.mCanShow = _tmp_3 != 0;
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
