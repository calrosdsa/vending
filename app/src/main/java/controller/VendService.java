package controller;


import android.content.res.Configuration;
import android.util.Log;

import com.tcn.springboard.TcnService;
import com.tcn.springboard.control.TcnVendIF;

import java.io.PrintWriter;
import java.io.StringWriter;

public class VendService extends TcnService {
    private static final String TAG = "VendService";
    private Thread.UncaughtExceptionHandler m_UncaughHandler = null;


    @Override
    public void onCreate() {
        super.onCreate();
        m_UncaughHandler = (thread, ex) -> {
            //任意一个线程异常后统一的处理 Unified processing after any thread exception
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            ex.printStackTrace(writer); // 打印到输出流 Print to output stream
            String exception =stringWriter.toString();
            TcnVendIF.getInstance().LoggerErrorForce(TAG, "setDefaultUncaughtExceptionHandler exception: "+exception);
        };
        ////捕捉异常，并将具体异常信息写入日志中 Catching exceptions and writing specific exception information to logs
        Thread.setDefaultUncaughtExceptionHandler(m_UncaughHandler);

        TcnVendIF.getInstance().LoggerInfoForce(TAG, "onCreate()");
        VendIF.getInstance().initialize();
        Log.d("DEBUG_APP_S","onCreate Service");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        TcnVendIF.getInstance().LoggerInfoForce(TAG, "onConfigurationChanged newConfig: "+newConfig.orientation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VendIF.getInstance().deInitialize();
        m_UncaughHandler = null;
        Thread.setDefaultUncaughtExceptionHandler(null);
        TcnVendIF.getInstance().LoggerInfoForce(TAG, "onDestroy()");
        Log.d("DEBUG_APP_S","onDestroy Service");

    }
}
