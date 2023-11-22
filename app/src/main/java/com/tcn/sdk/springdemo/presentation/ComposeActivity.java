package com.tcn.sdk.springdemo.presentation;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.gson.Gson;
import com.tcn.sdk.springdemo.Injection;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.data.AppDatabase;
import com.tcn.sdk.springdemo.data.Backup;
import com.tcn.sdk.springdemo.data.models.Activo;
import com.tcn.sdk.springdemo.data.models.Shipment;
import com.tcn.sdk.springdemo.domain.util.FileLogger;
import com.tcn.sdk.springdemo.presentation.adapter.ActivoAdapter;
import com.tcn.sdk.springdemo.presentation.adapter.MainSliderAdapter;
import com.tcn.sdk.springdemo.presentation.adapter.PicassoImageLoadingService;
import com.tcn.sdk.springdemo.presentation.adapter.RecyclerItemClickListener;
import com.tcn.sdk.springdemo.data.dto.Marcacion;
import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.ShipmentState;
import com.tcn.sdk.springdemo.data.models.User;
import com.tcn.sdk.springdemo.domain.repository.AppDataSource;
import com.tcn.springboard.control.PayMethod;
import com.tcn.springboard.control.TcnShareUseData;
import com.tcn.springboard.control.TcnVendEventID;
import com.tcn.springboard.control.TcnVendIF;
import com.tcn.springboard.control.VendEventInfo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import android_serialport_api.SerialPortController;
import controller.OverlayService;
import controller.SyncShipmentsSchedule;
import controller.VendApplication;
import controller.VendIF;
import dev.gustavoavila.websocketclient.WebSocketClient;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class ComposeActivity extends TcnMainActivity {
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;
    RecyclerView recyclerView4;
    RecyclerView recyclerView5;
    RecyclerView recyclerView6;
//    RecyclerView recyclerView1;
    ActivoAdapter adapter;
    AppDataSource dataSource;
    User currrentUser = null;
    AlertDialog loadingDialog;
    AlertDialog messageDialog;
    AlertDialog passworDialog;
    Button mButton;
    Button hideButton;
    EditText input;
    List<Activo> mActivos;
    private Slider slider;
    private OutDialog m_OutDialog = null;
    private LoadingDialog m_LoadingDialog = null;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
//    FileLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }else{
//            startOverlayService();
        };
        dataSource = Injection.provideUserDataSource(this);

        mButton = findViewById(R.id.mtitle);
        hideButton = findViewById(R.id.hiddenButton);
        hideButton.setOnLongClickListener(view -> {
            passworDialog.show();
            input.setText("");
            makeToast("PRESS HIDE BUTTON");
            return false;
        });
        mButton.setOnClickListener(view -> {
//            wSocker.start();
//            makeToast(hubConnection.getConnectionState().name());
        });


//        mButton.setVisibility(View.INVISIBLE);
        AsyncTask.execute(()->dataSource.getActivos());
        initView();
        scheduleJob();


        if (extras != null) {
            String name = extras.getString("name");
            String id = extras.getString("id");
            Log.d("DEBUG_APP_",name+id);
            currrentUser = new User(id,name);
//                currrentUser = new User("jorge","123123123");
            mButton.setVisibility(View.VISIBLE);
            mButton.setText(name);
            Runnable delayedTask = () -> {
                closeSession();
                clearAppData();
                Log.d("DEBUG_APP_TIMER","456");
            };
            mainThreadHandler.postDelayed(delayedTask, 10000);

            //The key argument here must match that used in the other activity
        }
    }
    private void startOverlayService() {
        Intent intent = new Intent(this, OverlayService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void clearAppData() {
        try {
//            Backup.backupDatabase(this);
            // clearing app data
//            PendingIntent intent = PendingIntent.getActivity(
//                    getApplicationContext(),
//                    0,
//                    new Intent(getApplicationContext(),ComposeActivity.class),
//                    PendingIntent.FLAG_IMMUTABLE);
//            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            assert mgr != null;
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, intent);
//            System.exit(0);
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);

        } catch (Exception e) {
            Log.d("DEBUG_APP_EXCP",e.getLocalizedMessage());
        }
    }



    private void restoreDBIntent() {
        try {
            Log.d("DEBUG_APP_BACK","CLICKED");
            File file = new File(Backup.getPath());
//            Log.d("DEBUG_APP_BACKUP",String.format("%s",file.getTotalSpace()));
//                String ewe = "dasdas";
//                FileInputStream inputStream = new FileInputStream("/document/primary:Files/vending");
            AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
            appDatabase.close();
            InputStream inputStream = new FileInputStream(file);
            Log.d("DEBUG_APP_BACK",inputStream.toString());
//            if (Backup.validFile(this,fileUri)) {
                Backup.restoreDatabase(this,inputStream);
//            } else {
//                makeToast("Fail to restore");
//                    Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.restore_failed), 1);
//            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            Log.d("DEBUG_APP_ERROR",e.getLocalizedMessage());
            e.printStackTrace();
        }
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.setType("*/*");
//        startActivityForResult(Intent.createChooser(i, "Select DB File"), 12);
    }

    @Override
    protected void onStart() {
        super.onStart();
        observeActivos();
        FileLogger.init();
//        FileLogger.addRecordToLog("LOG TEST");
//        wSocker.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUG_APP_LIST_1","REGISTER LISTENER");
//        TcnVendIF.getInstance().registerListener(m_vendListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DEBUG_APP_LIST_1","UNREGISTER LISTENER");
        TcnVendIF.getInstance().unregisterListener(m_vendListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_vendListener = null;
        Log.d("DEBUG_APP_LIST_1","DESTROY LISTENER");
    }

    protected void closeSession(){
        mButton.setVisibility(View.GONE);
        currrentUser = null;
        TcnVendIF.getInstance().unregisterListener(m_vendListener);
    }

    protected void observeActivos() {
        mDisposable.add(dataSource.observeActivos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(activos -> {
                    mActivos = activos;
                    Map<Integer, List<Activo>> groupCeldas =
                            activos.stream().collect(Collectors.groupingBy(w -> w.row));

                    for (Map.Entry<Integer, List<Activo>> entry : groupCeldas.entrySet()) {
                        switch (entry.getKey()) {
                            case 1:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView1.setAdapter(adapter);
                                break;
                            case 2:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView2.setAdapter(adapter);
                                break;
                            case 3:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView3.setAdapter(adapter);
                                break;
                            case 4:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView4.setAdapter(adapter);
                                break;
                            case 5:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView5.setAdapter(adapter);
                                break;
                            case 6:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView6.setAdapter(adapter);
                                break;
                        }
//                        if(entry.getKey() == 1){
//                            adapter = new ActivoAdapter(entry.getValue());
//                            recyclerView1.setAdapter(adapter);
//                        }
                    }

                }, thorwable -> {
                    Log.d("DEBUG_APP_ERR", "DASDAS", thorwable);
                    FileLogger.logError("observeActivos",thorwable.getLocalizedMessage());
                }));
    }

    public boolean isSessionEnabled() {
        return true;
//        if (currrentUser == null) return false;
//
//        long now = System.currentTimeMillis();
//        long now2 = currrentUser.mCreatedAt;
//        long diff = now - now2;
////        Log.d("DEBUG_APP_SESSION", String.format("%d --- %d = %d", now, now2, TimeUnit.MILLISECONDS.toSeconds(diff)));
//        return TimeUnit.MILLISECONDS.toSeconds(diff) < 120;
    }

    private void initView() {
        loadingDialog = new SpotsDialog.Builder().setContext(ComposeActivity.this)
                .build();

        messageDialog = new AlertDialog.Builder(ComposeActivity.this)
                .setTitle("Ocurrio un error")
                .setMessage("No se pudo dispensar el activo porfavor comuniquese con el proveedor")
                .setPositiveButton("OK", null)
//                .setNegativeButton("Cancel", null)
                .create();
        setRecyclerView();
        setDialogPassword();

        if (m_OutDialog == null) {
            m_OutDialog = new OutDialog(this, "", "");
            m_OutDialog.setShowTime(10000);
        }

        Slider.init(new PicassoImageLoadingService(this));
        slider = findViewById(R.id.banner_slider1);
        slider.setAdapter(new MainSliderAdapter());
        slider.setInterval(5000);
        slider.setLoopSlides(true);
        slider.setAnimateIndicators(true);

    }

    private void setDialogPassword() {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
            View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_password, null, false);
// Set up the input
            input = (EditText) viewInflated.findViewById(R.id.input);
            final TextView textError = (TextView) viewInflated.findViewById(R.id.error_text);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(viewInflated);

// Set up the buttons
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                if (Objects.equals(input.getText().toString(), "123")) {
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    input.setText("");
                    Intent mainIntent = new Intent(ComposeActivity.this, MenuActivity.class);
                    ComposeActivity.this.startActivity(mainIntent);
                    dialog.dismiss();

                } else {
                    textError.setText("Contraseña incorrecta");
                }
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
            passworDialog = builder.create();
        } catch (Exception e) {
            FileLogger.logError("setDialogPassword", e.getLocalizedMessage());
        }
    }

    private void setRecyclerView() {
        recyclerView1
                = findViewById(
                R.id.row_1);
        recyclerView2
                = findViewById(
                R.id.row_2);
        recyclerView3
                = findViewById(
                R.id.row_3);
        recyclerView4
                = findViewById(
                R.id.row_4);
        recyclerView5
                = findViewById(
                R.id.row_5);
        recyclerView6
                = findViewById(
                R.id.row_6);

        RecyclerView.LayoutManager RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        RecyclerView.LayoutManager RecyclerViewLayoutManager2
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        RecyclerView.LayoutManager RecyclerViewLayoutManager3
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        RecyclerView.LayoutManager RecyclerViewLayoutManager4
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        RecyclerView.LayoutManager RecyclerViewLayoutManager5
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        RecyclerView.LayoutManager RecyclerViewLayoutManager6
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        // Set LayoutManager on Recycler View
        recyclerView1.setLayoutManager(
                RecyclerViewLayoutManager);
        recyclerView1.addOnItemTouchListener(clickItem(recyclerView1));

        recyclerView2.setLayoutManager(
                RecyclerViewLayoutManager2);
        recyclerView2.addOnItemTouchListener(clickItem(recyclerView2));

        recyclerView3.setLayoutManager(
                RecyclerViewLayoutManager3);
        recyclerView3.addOnItemTouchListener(clickItem(recyclerView3));

        recyclerView4.setLayoutManager(
                RecyclerViewLayoutManager4);
        recyclerView4.addOnItemTouchListener(clickItem(recyclerView4));

        recyclerView5.setLayoutManager(
                RecyclerViewLayoutManager5);
        recyclerView5.addOnItemTouchListener(clickItem(recyclerView5));

        recyclerView6.setLayoutManager(
                RecyclerViewLayoutManager6);
        recyclerView6.addOnItemTouchListener(clickItem(recyclerView6));
    }

    RecyclerItemClickListener clickItem(RecyclerView recycler) {
       return  new RecyclerItemClickListener(this, recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                TcnVendIF.getInstance().startWorkThread();
                TcnVendIF.getInstance().registerListener(m_vendListener);
                int slotNo = position + 1;//出货的货道号 dispensing slot number
                String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
                String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
                String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
                TcnVendIF.getInstance().reqShip(slotNo, shipMethod, amount, tradeNo);
//                        if(isSessionEnabled()) {
//                            Activo activo = mActivos.get(position);
//                            RequestItem r = new RequestItem(activo.idCelda,currrentUser.mId);
//
//                            Call<RequestItemResponse> call = dataSource.requestActivo(r);
//                            call.enqueue(new Callback<RequestItemResponse>() {
//                                @Override
//                                public void onResponse(Call<RequestItemResponse> call, Response<RequestItemResponse> response) {
//                                    try{
//                                    Log.d("DEBUG_APP_API",response.message());
//                                    Log.d("DEBUG_APP_API",String.format("%s", response.isSuccessful()));
//                                    if (response.isSuccessful()) {
//                                        RequestItemResponse res =response.body();
//                                        assert res != null;
//                                        if (res.result != null) {
//                                            if (res.result.disponible) {
//                                                int slotNo = position + 1;//出货的货道号 dispensing slot number
//                                                String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
//                                                String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
//                                                String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
//                                                Shipment shipment = new Shipment(activo.idCelda,currrentUser.mId,tradeNo,res.result.idActivo,res.result.keyActivo,activo.objectType);
//                                                dataSource.insertShipment(shipment);
//                                                TcnVendIF.getInstance().ship(slotNo, shipMethod, amount, tradeNo);
//                                            } else {
//                                                Snackbar.make(view, "El usuario --- no tiene asignado este activo",
//                                                        Snackbar.LENGTH_LONG).show();
//                                            }
//                                        }
//                                    }else{
//                                        Snackbar.make(view, "Ocurrio un error inesperado",
//                                                Snackbar.LENGTH_LONG).show();
//                                    }
//                                    loadingDialog.dismiss();
//                                    }catch(Exception e){
//                                        FileLogger.logError("Request_item_onResponse",e.getLocalizedMessage());
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<RequestItemResponse> call, Throwable t) {
//                                    FileLogger.logError("RequestItem_onFailure",t.getLocalizedMessage());
//                                    Log.d("DEBUG_APP_API",t.getLocalizedMessage());
//                                    Snackbar.make(view, "Ocurrio un error inesperado", Snackbar.LENGTH_LONG).show();
//                                    call.cancel();
//                                }
//                            });
//                        }else {
//                            Snackbar.make(view,"Por favor, antes de solicitar un activo, realice una marcación con su credencial",
//                                    Snackbar.LENGTH_LONG).show();
//                        }
//
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Log.d("DEBUG_APP", "ITEMLOBGCLICKED");
                TcnVendIF.getInstance().stopWorkThread();

//                Intent mainIntent = new Intent(ComposeActivity.this, MainAct.class);
//                ComposeActivity.this.startActivity(mainIntent);
                // do whatever
            }
        });
}




    private void makeToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }


    private VendListener m_vendListener = new VendListener();
    private class VendListener implements TcnVendIF.VendEventListener {
        @Override
        public void VendEvent(VendEventInfo cEventInfo) {
            if (null == cEventInfo) {
                TcnVendIF.getInstance().LoggerError("DEBUG_APP", "VendListener cEventInfo is null");
//                return;
            }

            switch (cEventInfo.m_iEventID) {
                case TcnVendEventID.COMMAND_SYSTEM_BUSY:
                    makeToast(String.format("Command_system_busy %s",cEventInfo.m_lParam4));
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                    String loadText = "Error de dispensación, llame al servicio de atención al cliente";
                    String loadTitle = "";
                    if (null != m_OutDialog) {
                        m_OutDialog.cancel();
                    }
                    if (null == m_LoadingDialog) {
                        m_LoadingDialog = new LoadingDialog(ComposeActivity.this, loadText, loadTitle);
                    }
                    m_LoadingDialog.setLoadText(loadText);
                    m_LoadingDialog.setTitle(loadTitle);
                    m_LoadingDialog.setShowTime(3);
                    m_LoadingDialog.show();
                    AsyncTask.execute(()-> {
                                dataSource.updateShipmentState(ShipmentState.FAILURE.ordinal(), cEventInfo.m_lParam4);
                            });
                    closeSession();
//                    TcnVendIF.getInstance().closeTrade(true);
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
                case TcnVendEventID.COMMAND_SHIPMENT_FAULT:
                    if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(ComposeActivity.this, String.valueOf(cEventInfo.m_lParam1), cEventInfo.m_lParam4);
                        } else {
                            m_OutDialog.setText(cEventInfo.m_lParam4);
                        }
                        m_OutDialog.cleanData();
                    } else {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(ComposeActivity.this, String.valueOf(cEventInfo.m_lParam1), getString(R.string.ui_base_notify_shipping));
                        } else {
                            m_OutDialog.setText("Porfavor spere");
                        }
                    }
                    m_OutDialog.setNumber(String.valueOf(cEventInfo.m_lParam1));
                    m_OutDialog.show();
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.FAULT.ordinal(),cEventInfo.m_lParam4);
                    });
                    closeSession();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPPING:
                    if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(ComposeActivity.this, String.valueOf(cEventInfo.m_lParam1), cEventInfo.m_lParam4);
                        } else {
                            m_OutDialog.setText(cEventInfo.m_lParam4);
                        }
                        m_OutDialog.cleanData();
                    } else {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(ComposeActivity.this, String.valueOf(cEventInfo.m_lParam1), "Porfavor espere");
                        } else {
                            m_OutDialog.setText("Porfavor espere");
                        }
                    }
                    m_OutDialog.setNumber(String.valueOf(cEventInfo.m_lParam1));
                    m_OutDialog.show();
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SHIPPING.ordinal(),cEventInfo.m_lParam4);
                    });
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                    if (null != m_OutDialog) {
                        m_OutDialog.cancel();
                    }
                    if (m_LoadingDialog == null) {
                        m_LoadingDialog = new LoadingDialog(ComposeActivity.this, "Entrega exitosa", "Recoge tu producto");
                    } else {
                        m_LoadingDialog.setLoadText("Entrega exitosa");
                        m_LoadingDialog.setTitle("Recoge tu producto");
                    }
                    m_LoadingDialog.setShowTime(3);
                    m_LoadingDialog.show();
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SUCCESS.ordinal(),cEventInfo.m_lParam4);
                        dataSource.requestDispensar(cEventInfo.m_lParam4);
                    });
                    closeSession();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
            }

        }
    }

    @Override // com.tcn.app_common.view.TcnMainActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return super.dispatchTouchEvent(motionEvent);
    }
    protected Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build();

    private void scheduleJob() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this, SyncShipmentsSchedule.class);

        JobInfo jobInfo = new JobInfo.Builder(1001, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(1 * 60 * 1000 )
                .build();

        if (jobScheduler != null) {
            jobScheduler.schedule(jobInfo);
        }
    }

        @Override
    public void onBackPressed(){
        Toast.makeText(getApplicationContext(),"You Are Not Allowed to Exit the App", Toast.LENGTH_SHORT).show();
    }
}
