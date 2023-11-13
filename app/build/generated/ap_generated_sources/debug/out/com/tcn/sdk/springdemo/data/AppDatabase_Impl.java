package com.tcn.sdk.springdemo.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import com.tcn.sdk.springdemo.data.dao.ActivoDao;
import com.tcn.sdk.springdemo.data.dao.ActivoDao_Impl;
import com.tcn.sdk.springdemo.data.dao.CeldaDao;
import com.tcn.sdk.springdemo.data.dao.CeldaDao_Impl;
import com.tcn.sdk.springdemo.data.dao.ShipmentDao;
import com.tcn.sdk.springdemo.data.dao.ShipmentDao_Impl;
import com.tcn.sdk.springdemo.data.dao.UserDao;
import com.tcn.sdk.springdemo.data.dao.UserDao_Impl;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class AppDatabase_Impl extends AppDatabase {
  private volatile CeldaDao _celdaDao;

  private volatile UserDao _userDao;

  private volatile ShipmentDao _shipmentDao;

  private volatile ActivoDao _activoDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `celda` (`mId` INTEGER NOT NULL, `mSlotNumber` INTEGER NOT NULL, `mRowNumber` INTEGER NOT NULL, `mIsMerged` INTEGER NOT NULL, `mCanMerged` INTEGER NOT NULL, `enabled` INTEGER NOT NULL, `mCanShow` INTEGER NOT NULL, PRIMARY KEY(`mId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `user` (`mId` TEXT NOT NULL, `mName` TEXT, `mCreatedAt` INTEGER NOT NULL, PRIMARY KEY(`mId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `shipment` (`mId` TEXT NOT NULL, `mCreatedAt` INTEGER NOT NULL, `mSlotNumber` INTEGER NOT NULL, `mIdUser` TEXT, `mIsVerified` INTEGER NOT NULL, `estado` INTEGER NOT NULL, PRIMARY KEY(`mId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `activo` (`id` TEXT NOT NULL, `slotN` INTEGER NOT NULL, `name` TEXT, `cantidad` INTEGER NOT NULL, `celdas` INTEGER NOT NULL, `row` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"f2d465a0a1c2cbc2015a81e69b18f5c6\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `celda`");
        _db.execSQL("DROP TABLE IF EXISTS `user`");
        _db.execSQL("DROP TABLE IF EXISTS `shipment`");
        _db.execSQL("DROP TABLE IF EXISTS `activo`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsCelda = new HashMap<String, TableInfo.Column>(7);
        _columnsCelda.put("mId", new TableInfo.Column("mId", "INTEGER", true, 1));
        _columnsCelda.put("mSlotNumber", new TableInfo.Column("mSlotNumber", "INTEGER", true, 0));
        _columnsCelda.put("mRowNumber", new TableInfo.Column("mRowNumber", "INTEGER", true, 0));
        _columnsCelda.put("mIsMerged", new TableInfo.Column("mIsMerged", "INTEGER", true, 0));
        _columnsCelda.put("mCanMerged", new TableInfo.Column("mCanMerged", "INTEGER", true, 0));
        _columnsCelda.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0));
        _columnsCelda.put("mCanShow", new TableInfo.Column("mCanShow", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCelda = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCelda = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCelda = new TableInfo("celda", _columnsCelda, _foreignKeysCelda, _indicesCelda);
        final TableInfo _existingCelda = TableInfo.read(_db, "celda");
        if (! _infoCelda.equals(_existingCelda)) {
          throw new IllegalStateException("Migration didn't properly handle celda(com.tcn.sdk.springdemo.data.models.Celda).\n"
                  + " Expected:\n" + _infoCelda + "\n"
                  + " Found:\n" + _existingCelda);
        }
        final HashMap<String, TableInfo.Column> _columnsUser = new HashMap<String, TableInfo.Column>(3);
        _columnsUser.put("mId", new TableInfo.Column("mId", "TEXT", true, 1));
        _columnsUser.put("mName", new TableInfo.Column("mName", "TEXT", false, 0));
        _columnsUser.put("mCreatedAt", new TableInfo.Column("mCreatedAt", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUser = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUser = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUser = new TableInfo("user", _columnsUser, _foreignKeysUser, _indicesUser);
        final TableInfo _existingUser = TableInfo.read(_db, "user");
        if (! _infoUser.equals(_existingUser)) {
          throw new IllegalStateException("Migration didn't properly handle user(com.tcn.sdk.springdemo.data.models.User).\n"
                  + " Expected:\n" + _infoUser + "\n"
                  + " Found:\n" + _existingUser);
        }
        final HashMap<String, TableInfo.Column> _columnsShipment = new HashMap<String, TableInfo.Column>(6);
        _columnsShipment.put("mId", new TableInfo.Column("mId", "TEXT", true, 1));
        _columnsShipment.put("mCreatedAt", new TableInfo.Column("mCreatedAt", "INTEGER", true, 0));
        _columnsShipment.put("mSlotNumber", new TableInfo.Column("mSlotNumber", "INTEGER", true, 0));
        _columnsShipment.put("mIdUser", new TableInfo.Column("mIdUser", "TEXT", false, 0));
        _columnsShipment.put("mIsVerified", new TableInfo.Column("mIsVerified", "INTEGER", true, 0));
        _columnsShipment.put("estado", new TableInfo.Column("estado", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysShipment = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesShipment = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoShipment = new TableInfo("shipment", _columnsShipment, _foreignKeysShipment, _indicesShipment);
        final TableInfo _existingShipment = TableInfo.read(_db, "shipment");
        if (! _infoShipment.equals(_existingShipment)) {
          throw new IllegalStateException("Migration didn't properly handle shipment(com.tcn.sdk.springdemo.data.models.Shipment).\n"
                  + " Expected:\n" + _infoShipment + "\n"
                  + " Found:\n" + _existingShipment);
        }
        final HashMap<String, TableInfo.Column> _columnsActivo = new HashMap<String, TableInfo.Column>(6);
        _columnsActivo.put("id", new TableInfo.Column("id", "TEXT", true, 1));
        _columnsActivo.put("slotN", new TableInfo.Column("slotN", "INTEGER", true, 0));
        _columnsActivo.put("name", new TableInfo.Column("name", "TEXT", false, 0));
        _columnsActivo.put("cantidad", new TableInfo.Column("cantidad", "INTEGER", true, 0));
        _columnsActivo.put("celdas", new TableInfo.Column("celdas", "INTEGER", true, 0));
        _columnsActivo.put("row", new TableInfo.Column("row", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysActivo = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesActivo = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoActivo = new TableInfo("activo", _columnsActivo, _foreignKeysActivo, _indicesActivo);
        final TableInfo _existingActivo = TableInfo.read(_db, "activo");
        if (! _infoActivo.equals(_existingActivo)) {
          throw new IllegalStateException("Migration didn't properly handle activo(com.tcn.sdk.springdemo.data.models.Activo).\n"
                  + " Expected:\n" + _infoActivo + "\n"
                  + " Found:\n" + _existingActivo);
        }
      }
    }, "f2d465a0a1c2cbc2015a81e69b18f5c6", "30c9b1b5e56811c662bef2161efb0200");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "celda","user","shipment","activo");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `celda`");
      _db.execSQL("DELETE FROM `user`");
      _db.execSQL("DELETE FROM `shipment`");
      _db.execSQL("DELETE FROM `activo`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public CeldaDao celdaDao() {
    if (_celdaDao != null) {
      return _celdaDao;
    } else {
      synchronized(this) {
        if(_celdaDao == null) {
          _celdaDao = new CeldaDao_Impl(this);
        }
        return _celdaDao;
      }
    }
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public ShipmentDao shipmentDao() {
    if (_shipmentDao != null) {
      return _shipmentDao;
    } else {
      synchronized(this) {
        if(_shipmentDao == null) {
          _shipmentDao = new ShipmentDao_Impl(this);
        }
        return _shipmentDao;
      }
    }
  }

  @Override
  public ActivoDao activoDao() {
    if (_activoDao != null) {
      return _activoDao;
    } else {
      synchronized(this) {
        if(_activoDao == null) {
          _activoDao = new ActivoDao_Impl(this);
        }
        return _activoDao;
      }
    }
  }
}
