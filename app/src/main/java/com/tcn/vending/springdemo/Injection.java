package com.tcn.vending.springdemo;

import android.content.Context;
import android.util.Log;

import com.tcn.vending.springdemo.data.APIClient;
import com.tcn.vending.springdemo.data.AppDatabase;
import com.tcn.vending.springdemo.data.Backup;
import com.tcn.vending.springdemo.data.repository.LocalAppDataSource;
import com.tcn.vending.springdemo.domain.repository.ApiInterface;
import com.tcn.vending.springdemo.domain.repository.AppDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Injection {
    public static AppDataSource provideUserDataSource(Context context) {
//        AppDatabase database = AppDatabase.getInstance(context);
        ApiInterface restService = APIClient.getRestService();
        return new LocalAppDataSource(
//                database.celdaDao(),
//                database.userDao(),
                restService
//                database.shipmentDao(),
//                database.activoDao()
        );
    }
}

//    private void restoreDB() {
//        try {
//            Log.d("DEBUG_APP_BACK","CLICKED");
//            File file = new File(Backup.getPath());
////            Log.d("DEBUG_APP_BACKUP",String.format("%s",file.getTotalSpace()));
////                String ewe = "dasdas";
////                FileInputStream inputStream = new FileInputStream("/document/primary:Files/vending");
//
////            AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
////            appDatabase.close();
//            InputStream inputStream = new FileInputStream(file);
//            Log.d("DEBUG_APP_BACK",inputStream.toString());
////            if (Backup.validFile(this,fileUri)) {
//            Backup.restoreDatabase(this,inputStream);
////            } else {
////                makeToast("Fail to restore");
////                    Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.restore_failed), 1);
////            }
//            if (inputStream != null) {
//                inputStream.close();
//            }
//        } catch (IOException e) {
//            Log.d("DEBUG_APP_ERROR",e.getLocalizedMessage());
//            e.printStackTrace();
//        }
////        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
////        i.setType("*/*");
////        startActivityForResult(Intent.createChooser(i, "Select DB File"), 12);
//    }
