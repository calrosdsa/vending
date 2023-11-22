package com.tcn.sdk.springdemo.presentation;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tcn.sdk.springdemo.R;

public class DrawerLayout extends LinearLayoutCompat {
    public DrawerLayout(Context context,CloseDrawer close) {
        super(context);
        initView(context,close);
    }



    private void initView(Context context,CloseDrawer closeDrawer) {
        View.inflate(context, R.layout.drawer_layout, this);
        findViewById(R.id.init_button).setOnClickListener(view->{
            clearAppData(context);
        });
        findViewById(R.id.close_button).setOnClickListener(view->{
            closeDrawer.onClick(this);
        });

    }

    public interface CloseDrawer {
        void onClick(DrawerLayout var1);
    }

    private void clearAppData(Context context) {
        try {
//            Backup.backupDatabase(this);
            // clearing app data
//            Activity activity = (Activity) context;
            PendingIntent intent = PendingIntent.getActivity(
                    context,
                    0,
                    new Intent(context,ComposeActivity.class),
                    PendingIntent.FLAG_IMMUTABLE);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            assert mgr != null;
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, intent);
            System.exit(0);
            String packageName =  context.getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);

        } catch (Exception e) {
            Log.d("DEBUG_APP_EXCP",e.getLocalizedMessage());
        }
    }



}
