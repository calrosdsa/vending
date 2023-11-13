package com.tcn.sdk.springdemo;

import android.content.Context;

import com.tcn.sdk.springdemo.data.APIClient;
import com.tcn.sdk.springdemo.data.AppDatabase;
import com.tcn.sdk.springdemo.data.repository.LocalCeldaDataSource;
import com.tcn.sdk.springdemo.domain.repository.ApiInterface;
import com.tcn.sdk.springdemo.domain.repository.CeldaDataSource;

public class Injection {
    public static CeldaDataSource provideUserDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        ApiInterface restService = APIClient.getRestService();
        return new LocalCeldaDataSource(database.celdaDao(),database.userDao(),
                restService,database.shipmentDao());
    }
}
