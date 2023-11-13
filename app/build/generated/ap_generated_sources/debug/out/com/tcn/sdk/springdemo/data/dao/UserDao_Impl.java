package com.tcn.sdk.springdemo.data.dao;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.RxRoom;
import android.database.Cursor;
import com.tcn.sdk.springdemo.data.models.User;
import io.reactivex.Flowable;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.concurrent.Callable;

@SuppressWarnings("unchecked")
public class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfUser;

  public UserDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUser = new EntityInsertionAdapter<User>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `user`(`mId`,`mName`,`mCreatedAt`) VALUES (?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, User value) {
        if (value.mId == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.mId);
        }
        if (value.mName == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.mName);
        }
        stmt.bindLong(3, value.mCreatedAt);
      }
    };
  }

  @Override
  public void insert(User user) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfUser.insert(user);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Flowable<User> observeUser() {
    final String _sql = "select * from user";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, new String[]{"user"}, new Callable<User>() {
      @Override
      public User call() throws Exception {
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfMId = _cursor.getColumnIndexOrThrow("mId");
          final int _cursorIndexOfMName = _cursor.getColumnIndexOrThrow("mName");
          final int _cursorIndexOfMCreatedAt = _cursor.getColumnIndexOrThrow("mCreatedAt");
          final User _result;
          if(_cursor.moveToFirst()) {
            _result = new User();
            _result.mId = _cursor.getString(_cursorIndexOfMId);
            _result.mName = _cursor.getString(_cursorIndexOfMName);
            _result.mCreatedAt = _cursor.getLong(_cursorIndexOfMCreatedAt);
          } else {
            _result = null;
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
