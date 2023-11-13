package com.tcn.sdk.springdemo.data.repository;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.tcn.sdk.springdemo.data.AppDatabase;

public class AppPreferences {
    private static volatile AppPreferences INSTANCE;
    SharedPreferences pref; // 0 - for private mode

    public AppPreferences(Context context){
        this.pref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);

    }

    public static AppPreferences getInstance(Context context){
        if (INSTANCE == null) {
            synchronized (AppPreferences.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppPreferences(context);
                }
            }
        }
        return INSTANCE;
    }

    public static String IS_DB_POPULATED = "is_db_populated";


    public boolean getIsDbPopulated(){
        return pref.getBoolean(IS_DB_POPULATED,false);
    };

    public void setIsDbPopulated(boolean isPopulated){
        pref.edit().putBoolean(IS_DB_POPULATED,isPopulated).apply();
    }


}
