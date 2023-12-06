package controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

import com.tcn.vending.springdemo.presentation.DrawerLayout;

public class OverlayService extends Service implements OnTouchListener, OnClickListener {
    private static String TAG = "OverlayService";
    private WindowManager wm;
    private Button button;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();


        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
//                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//                : WindowManager.LayoutParams.TYPE_PHONE;
//
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                type,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//                PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.START | Gravity.TOP;
//        params.x = 0;
//        params.y = 0;
        WindowManager.LayoutParams params = OverlayViewLayoutParams.get();
        wm.addView(
                new DrawerLayout(
                        getApplicationContext(),
                        (DrawerLayout view)->wm.removeView(view)
                ),
                params);
//        return START_NOT_STICKY;
        return START_NOT_STICKY;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, " ++++ On touch");
        return false;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, " ++++ On click");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (button != null) {
            wm.removeView(button);
            button = null;
        }
    }
    
    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

}


