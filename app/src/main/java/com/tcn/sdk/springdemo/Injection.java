package com.tcn.sdk.springdemo;

import android.content.Context;

import com.tcn.sdk.springdemo.data.APIClient;
import com.tcn.sdk.springdemo.data.AppDatabase;
import com.tcn.sdk.springdemo.data.repository.LocalAppDataSource;
import com.tcn.sdk.springdemo.domain.repository.ApiInterface;
import com.tcn.sdk.springdemo.domain.repository.AppDataSource;

public class Injection {
    public static AppDataSource provideUserDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        ApiInterface restService = APIClient.getRestService();
        return new LocalAppDataSource(
                database.celdaDao(),
                database.userDao(),
                restService,
                database.shipmentDao(),
                database.activoDao()
        );
    }
}
