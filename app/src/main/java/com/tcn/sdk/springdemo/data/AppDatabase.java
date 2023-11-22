package com.tcn.sdk.springdemo.data;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.tcn.sdk.springdemo.data.dao.ActivoDao;
import com.tcn.sdk.springdemo.data.dao.CeldaDao;
import com.tcn.sdk.springdemo.data.dao.ShipmentDao;
import com.tcn.sdk.springdemo.data.dao.UserDao;
import com.tcn.sdk.springdemo.data.models.Activo;
import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.Shipment;
import com.tcn.sdk.springdemo.data.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Celda.class,
                User.class,
                Shipment.class,
                Activo.class,
        }, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract CeldaDao celdaDao();
    public abstract UserDao userDao();
    public abstract ShipmentDao shipmentDao();
    public abstract ActivoDao activoDao();

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "vending.db")
                            .setJournalMode(JournalMode.TRUNCATE)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


