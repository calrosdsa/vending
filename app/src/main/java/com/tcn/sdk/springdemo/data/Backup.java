package com.tcn.sdk.springdemo.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Backup {


    private static String DATABASE_NAME = "vending.db";
    private static String LOGGER = "DEBUG_APP_BACKUP";
    private static String FILE_NAME = "vending";

    public static  String  getPath(){
        File sdir = new File(Environment.getExternalStorageDirectory().getPath()+ "/Files");
        String fileName = FILE_NAME;
        String sfpath = sdir.getPath() + File.separator + fileName;
//        File file = new File(sfpath);
        return sfpath;
    }

    public static void backupDatabase(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        appDatabase.close();
        File dbfile = context.getDatabasePath(DATABASE_NAME);
        File sdir = new File(Environment.getExternalStorageDirectory().getPath()+ "/Files");
        String fileName = FILE_NAME;
        String sfpath = sdir.getPath() + File.separator + fileName;
        if (!sdir.exists()) {
            sdir.mkdirs();
        } else {
            //Directory Exists. Delete a file if count is 5 already. Because we will be creating a new.
            //This will create a conflict if the last backup file was also on the same date. In that case,
            //we will reduce it to 4 with the function call but the below code will again delete one more file.
            checkAndDeleteBackupFile(sdir, sfpath);
        }
        File savefile = new File(sfpath);
        if (savefile.exists()) {
            Log.d(LOGGER, "File exists. Deleting it and then creating new file.");
            savefile.delete();
        }
        try {
            if (savefile.createNewFile()) {
                int buffersize = 8 * 1024;
                byte[] buffer = new byte[buffersize];
                int bytes_read;
                OutputStream savedb = new FileOutputStream(sfpath);
                InputStream indb = new FileInputStream(dbfile);
                while ((bytes_read = indb.read(buffer, 0, buffersize)) > 0) {
                    savedb.write(buffer, 0, bytes_read);
                }
                savedb.flush();
                indb.close();
                savedb.close();
//                SharedPreferences sharedPreferences = context.getSharedPreferences("backup", MODE_PRIVATE);
//                sharedPreferences.edit().putString("backupFileName", fileName).apply();
//                updateLastBackupTime(sharedPreferences);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOGGER, "ex: " + e);
        }
    }

    public static void restoreDatabase(Context context, InputStream inputStreamNewDB) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        appDatabase.close();

        //Delete the existing restoreFile and create a new one.
//        sharedPreferences.edit().putBoolean("restoringDatabase", true).apply();
//        deleteRestoreBackupFile(getApplicationContext());
//        backupDatabaseForRestore(this, getApplicationContext());

        File oldDB = context.getDatabasePath(DATABASE_NAME);
        if (inputStreamNewDB != null) {
            try {
                copyFile((FileInputStream) inputStreamNewDB, new FileOutputStream(oldDB));
//                Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.restore_success), 1);
                //Take the user to home screen and there we will validate if the database file was actually restored correctly.
            } catch (IOException e) {
                Log.d(LOGGER, "ex for is of restore: " + e);
//                e.printStackTrace();
            }
        } else {
            Log.d(LOGGER, "Restore - file does not exists");
        }
    }

//    public static void restoreDatabase(Context context, InputStream inputStreamNewDB) {
//        AppDatabase appDatabase = AppDatabase.getInstance(context);
//        appDatabase.close();
//
//        //Delete the existing restoreFile and create a new one.
////        sharedPreferences.edit().putBoolean("restoringDatabase", true).apply();
////        deleteRestoreBackupFile(getApplicationContext());
////        backupDatabaseForRestore(this, getApplicationContext());
//
//        File oldDB = context.getDatabasePath(DATABASE_NAME);
//        if (inputStreamNewDB != null) {
//            try {
//                copyFile((FileInputStream) inputStreamNewDB, new FileOutputStream(oldDB));
////                Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.restore_success), 1);
//                //Take the user to home screen and there we will validate if the database file was actually restored correctly.
//            } catch (IOException e) {
//                Log.d(LOGGER, "ex for is of restore: " + e);
////                e.printStackTrace();
//            }
//        } else {
//            Log.d(LOGGER, "Restore - file does not exists");
//        }
//    }

    public static boolean validFile(Context context,Uri fileUri) {
        ContentResolver cr = context.getContentResolver();
        String mime = cr.getType(fileUri);
        return "application/octet-stream".equals(mime);
    }
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    public static void checkAndDeleteBackupFile(File directory, String path) {
        //This is to prevent deleting extra file being deleted which is mentioned in previous comment lines.
        File currentDateFile = new File(path);
        int fileIndex = -1;
        long lastModifiedTime = System.currentTimeMillis();

        if (!currentDateFile.exists()) {
            File[] files = directory.listFiles();
            if (files != null && files.length >= 1) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    long fileLastModifiedTime = file.lastModified();
                    if (fileLastModifiedTime < lastModifiedTime) {
                        lastModifiedTime = fileLastModifiedTime;
                        fileIndex = i;
                    }
                }

                if (fileIndex != -1) {
                    File deletingFile = files[fileIndex];
                    if (deletingFile.exists()) {
                        deletingFile.delete();
                    }
                }
            }
        }
    }
    public static String getDateFromMillisForBackup(long millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm", Locale.getDefault());
        return simpleDateFormat.format(new Date(millis));
    }


}
