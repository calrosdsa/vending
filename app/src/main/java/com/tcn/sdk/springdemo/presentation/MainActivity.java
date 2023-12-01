package com.tcn.sdk.springdemo.presentation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import androidx.work.Constraints;
import androidx.work.NetworkType;

import com.tcn.sdk.springdemo.Injection;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.data.AppDatabase;
import com.tcn.sdk.springdemo.data.Backup;
import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.Activo;
import com.tcn.sdk.springdemo.data.models.Shipment;
import com.tcn.sdk.springdemo.domain.util.FileLogger;
import com.tcn.sdk.springdemo.presentation.adapter.ActivoAdapter;
import com.tcn.sdk.springdemo.presentation.adapter.RecyclerItemClickListener;
import com.tcn.sdk.springdemo.data.models.ShipmentState;
import com.tcn.sdk.springdemo.data.models.User;
import com.tcn.sdk.springdemo.domain.repository.AppDataSource;
import com.tcn.sdk.springdemo.presentation.components.buttons.HelpDialog;
import com.tcn.springboard.control.PayMethod;
import com.tcn.springboard.control.TcnVendEventID;
import com.tcn.springboard.control.TcnVendIF;
import com.tcn.springboard.control.VendEventInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import controller.OverlayService;
import controller.SyncShipmentsSchedule;
import controller.VendService;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends TcnMainActivity {
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
    AlertDialog exitDialog;
    AlertDialog passworDialog;
    TextView mUseText;
    Button hideButton;
    EditText input;
    List<Activo> mActivos;
    CountDownTimer timer;
    CountDownTimer limitTimer;
    long counterTimer = 30000;
//    private Slider slider;
    private OutDialog m_OutDialog = null;
    private LoadingDialog m_LoadingDialog = null;
    Activo activo = null;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
//    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
//    FileLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        restoreDB();
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }else{
//            startOverlayService();
        };
        dataSource = Injection.provideUserDataSource(this);

        mUseText = findViewById(R.id.mtitle);
        hideButton = findViewById(R.id.hiddenButton);
        hideButton.setOnLongClickListener(view -> {
            passworDialog.show();
            input.setText("");
            makeToast("PRESS HIDE BUTTON");
            return false;
        });
        mUseText.setOnClickListener(view -> {
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
//            CountDownTimer initTimer = startTimer();
//            timer = initTimer.start();

//                currrentUser = new User("jorge","123123123");
            mUseText.setVisibility(View.VISIBLE);
            mUseText.setText(name);

        }
    }
    private CountDownTimer startTimer(){
        TextView mTextField = findViewById(R.id.text_counter);
             return  new CountDownTimer(counterTimer, 1000) {

                @SuppressLint("SuspiciousIndentation")
                public void onTick(long millisUntilFinished) {
                counterTimer -= 1000;
                    Log.d("DEBUG_APP_TIMER",String.format("%s  --- %s",counterTimer,millisUntilFinished));
                    mTextField.setText(String.format("%s",counterTimer / 1000));
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    mTextField.setText("");
                    clearAppData();
//                    closeSession();
                    Log.d("DEBUG_APP_TIMER","456");
                }

            };
    }
    private void startOverlayService() {
        Intent intent = new Intent(this, OverlayService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(intent);
        } else {
            startService(intent);
        }
    }

    private void clearAppData() {
        try {
//            Backup.backupDatabase(this);
//            String packageName = getApplicationContext().getPackageName();
//            Runtime runtime = Runtime.getRuntime();
//            runtime.exec("pm clear "+packageName);
            stopService(new Intent(getApplication(), VendService.class));
            System.exit(0);
        } catch (Exception e) {
            Log.d("DEBUG_APP_EXCP",e.getLocalizedMessage());
        }
    }

    private void restoreDB() {
        try {
            Log.d("DEBUG_APP_BACK","CLICKED");
            File file = new File(Backup.getPath());
//            Log.d("DEBUG_APP_BACKUP",String.format("%s",file.getTotalSpace()));
//                String ewe = "dasdas";
//                FileInputStream inputStream = new FileInputStream("/document/primary:Files/vending");

//            AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
//            appDatabase.close();
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
        resumeTimer();
        TcnVendIF.getInstance().registerListener(m_vendListener);
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
        mUseText.setVisibility(View.GONE);
        currrentUser = null;
//        TcnVendIF.getInstance().unregisterListener(m_vendListener);
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
//        return true;
        if (currrentUser == null) return false;

        long now = System.currentTimeMillis();
        long now2 = currrentUser.mCreatedAt;
        long diff = now - now2;
//        Log.d("DEBUG_APP_SESSION", String.format("%d --- %d = %d", now, now2, TimeUnit.MILLISECONDS.toSeconds(diff)));
        return TimeUnit.MILLISECONDS.toSeconds(diff) < 120;
    }

    private void initView() {
        loadingDialog = new SpotsDialog.Builder().setContext(MainActivity.this)
                .build();

        exitDialog = new AlertDialog.Builder(MainActivity.this)
//                .setTitle("Ocurrio un error")
                .setMessage("Por favor, confirma si deseas regresar a la pantalla de inicio.")
                .setPositiveButton("OK", (dialog, id) -> {
                   clearAppData();
                })
//                .setNegativeButton("Cancel", null)
                .create();

        setRecyclerView();
        setDialogPassword();

        if (m_OutDialog == null) {
            m_OutDialog = new OutDialog(this, "", "");
            m_OutDialog.setShowTime(30);
        }
        if (m_LoadingDialog == null) {
            m_LoadingDialog = new LoadingDialog(MainActivity.this, "", "",this::clearAppData);
        }


        findViewById(R.id.exit_btn).setOnClickListener(view -> {
            exitDialog.show();
        });

//        findViewById(R.id.testSlot).setOnClickListener(view -> {
//            if(timer == null) {
//                Log.d("DEBUG_APP","TIMER IS NULL");
//                return;
//            }
//            timer.cancel();
//            timer = null;
//
////                TcnVendIF.getInstance().startWorkThread();
////                TcnVendIF.getInstance().registerListener(m_vendListener);
//            int slotNo = 1;//出货的货道号 dispensing slot number
//            String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
//            String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
//            String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
//            TcnVendIF.getInstance().reqShip(slotNo, shipMethod, amount, tradeNo);
//        });

        Dialog helpDialog = new HelpDialog(this,"Dialog text","Dialog title");
        helpDialog.setCanceledOnTouchOutside(true);
        helpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Log.d("DEBUG_APP_","CANCEL_DIALOG");
                    if(timer == null){
                    resumeTimer();
                    }
                }
            });
        findViewById(R.id.helpButton).setOnClickListener(view->{
        if(timer != null){
            stopTimer();
        }
            helpDialog.show();
        });

//        Slider.init(new PicassoImageLoadingService(this));
//        slider = findViewById(R.id.banner_slider1);
//        slider.setAdapter(new MainSliderAdapter());
//        slider.setInterval(5000);
//        slider.setLoopSlides(true);
//        slider.setAnimateIndicators(true);
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
                    stopTimer();
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    input.setText("");
                    Intent mainIntent = new Intent(MainActivity.this, MenuActivity.class);
                    MainActivity.this.startActivity(mainIntent);
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

    private CountDownTimer checkIsInactive(){
        return  new CountDownTimer(8000, 1000) {

            @SuppressLint("SuspiciousIndentation")
            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                if(timer == null){
                clearAppData();
                }
                Log.d("DEBUG_APP_TIMER","456");
            }

        };
    }
    Activo getActivoByRowAndPosition(int position,int row){
        ArrayList<Activo> activos = new ArrayList<>();
        for (int i = 0;i < mActivos.size();i++){
            Activo currentActivo = mActivos.get(i);
            if(currentActivo.row == row){
                activos.add(currentActivo);
            }
        }
        return activos.get(position);
    }
    RecyclerItemClickListener clickItem(RecyclerView recycler) {
       return  new RecyclerItemClickListener(this, recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, int position) {
                if(timer == null) {
                    Log.d("DEBUG_APP","TIMER IS NULL");
                    return;
                }
                switch (recycler.getId()){
                    case R.id.row_1:
                        activo =getActivoByRowAndPosition(position,1);
                        break;
                    case R.id.row_2:
                        activo =getActivoByRowAndPosition(position,2);
                        break;
                    case R.id.row_3:
                        activo =getActivoByRowAndPosition(position,3);
                        break;
                    case R.id.row_4:
                        activo =getActivoByRowAndPosition(position,4);
                        break;
                    case R.id.row_5:
                        activo =getActivoByRowAndPosition(position,5);
                        break;
                    case R.id.row_6:
                        activo =getActivoByRowAndPosition(position,6);
                        break;
                }
                if (activo== null) return;
                if(!activo.enabled) {
                    showAlertMessage("El activo que has seleccionado parece no estar en tu inventario.","");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Para continuar confirme su solicitud");
                builder.setMessage("Si desea continuar con la dispensacion de un Maouse Rowel porfavor presione confirmar");
                builder.setCancelable(true);
                builder.setPositiveButton("Confirmar", (dialogInterface, i) -> {
                    shipItem();
                });
//                builder.setOnCancelListener(dialogInterface -> resumeTimer());
                builder.setNegativeButton("Cancelar", null);
                builder.show();




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
//                                                initLoader(slotNo);
//                                                String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
//                                                String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
//                                                String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
//                                                Shipment shipment = new Shipment(activo.idCelda,currrentUser.mId,tradeNo,res.result.idActivo,res.result.keyActivo,activo.objectType);
//                                                dataSource.insertShipment(shipment);
//                                                TcnVendIF.getInstance().ship(slotNo, shipMethod, amount, tradeNo);
//                                            } else {
//                                                Snackbar.make(view, "El usuario --- no tiene asignado este activo",
//                                                        Snackbar.LENGTH_LONG).show();
//                                                resumeTimer();
//                                            }
//                                        }
//                                    }else{
//                                        Snackbar.make(view, "Ocurrio un error inesperado",
//                                                Snackbar.LENGTH_LONG).show();
//                                    }
//                                    }catch(Exception e){
//                                        FileLogger.logError("Request_item_onResponse",e.getLocalizedMessage());
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<RequestItemResponse> call, Throwable t) {
//                                    resumeTimer();
//                                    FileLogger.logError("RequestItem_onFailure",t.getLocalizedMessage());
//                                    Log.d("DEBUG_APP_API",t.getLocalizedMessage());
//                                    Snackbar.make(view, "Ocurrio un error inesperado", Snackbar.LENGTH_LONG).show();
//                                    call.cancel();
//                                }
//                            });
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

   private void shipItem(){
       stopTimer();
       int slotNo = activo.slotN;//出货的货道号 dispensing slot number
       initLoader(slotNo);
       if(slotNo == 34){
           Handler handler = new Handler();
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   // Do something after 5s = 5000ms
                   String content = "Error de conexión. Por favor, inténtelo nuevamente o comuníquese con el servicio de soporte.";
                   showAlertMessage(content,"Error");
                   resumeTimer();
               }
           }, 3000);

           return;
       }

       if(slotNo == 41){
           Handler handler = new Handler();
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   // Do something after 5s = 5000ms
                   String content = "Se produjo un error inesperado al intentar sincronizar los datos. Por favor, inténtelo nuevamente o póngase en contacto con el servicio de soporte.";
                   showAlertMessage(content,"Error");
                   resumeTimer();
               }
           }, 3000);

           return;
       }

       String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
       String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
       String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
       TcnVendIF.getInstance().reqShip(slotNo, shipMethod, amount, tradeNo);
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
                case TcnVendEventID.COMMAND_SHIPMENT_FAULT:
                    if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(MainActivity.this, String.valueOf(cEventInfo.m_lParam1), cEventInfo.m_lParam4);
                        } else {
                            m_OutDialog.setText(cEventInfo.m_lParam4);
                        }
                        m_OutDialog.cleanData();
                    } else {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(MainActivity.this, String.valueOf(cEventInfo.m_lParam1), getString(R.string.ui_base_notify_shipping));
                        } else {
                            m_OutDialog.setText("Porfavor spere");
                        }
                    }
                    m_OutDialog.setNumber(String.valueOf(cEventInfo.m_lParam1));
                    m_OutDialog.show();
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.FAULT.ordinal(),cEventInfo.m_lParam4);
                    });
//                    closeSession();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;


                case TcnVendEventID.COMMAND_SHIPPING:
                    if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
                        initLoader(cEventInfo.m_lParam1);
                    }
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SHIPPING.ordinal(),cEventInfo.m_lParam4);
                    });
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                    String loadText = "Error de dispensación. Por favor, póngase en contacto con el servicio de soporte.";
                    String loadTitle = "Error";
                    showAlertMessage(loadText,loadTitle);


                    AsyncTask.execute(()-> {
                        dataSource.updateShipmentState(ShipmentState.FAILURE.ordinal(), cEventInfo.m_lParam4);
                    });
//                    closeSession();
                    resumeTimer();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                    showAlertMessage("Entrega Exitosa","Recoge tu producto");
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SUCCESS.ordinal(),cEventInfo.m_lParam4);
                        dataSource.requestDispensar(cEventInfo.m_lParam4);
                    });
                   resumeTimer();
//                    closeSession();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
            }

        }
    }

    private void showAlertMessage(String loadText,String loadTitle){
        if (null != m_OutDialog) {
            m_OutDialog.cancel();
        }
       /* if (null == m_LoadingDialog) {
            m_LoadingDialog = new LoadingDialog(MainActivity.this, loadText, loadTitle, this::clearAppData);
        }*/
        m_LoadingDialog.setLoadText(loadText);
        m_LoadingDialog.setTitle(loadTitle);
        m_LoadingDialog.setShowTime(4);
        m_LoadingDialog.show();
    }

    private void initLoader(int slotNumber){
        if (m_OutDialog == null) {
            m_OutDialog = new OutDialog(MainActivity.this, String.valueOf(slotNumber), "Porfavor espere");
        } else {
            m_OutDialog.setText("Porfavor espere");
        }
        m_OutDialog.cleanData();
//        } else {
//            if (m_OutDialog == null) {
//                m_OutDialog = new OutDialog(MainActivity.this, String.valueOf(cEventInfo.m_lParam1), "Porfavor espere");
//            } else {
//                m_OutDialog.setText("Porfavor espere");
//            }
//        }
        m_OutDialog.setNumber(String.valueOf(slotNumber));
        m_OutDialog.show();
    }
    private void resumeTimer(){
        CountDownTimer initTimer = startTimer();
        timer = initTimer.start();
    }
    private void stopTimer(){
        timer.cancel();
        timer = null;
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
                .setPeriodic(15 * 60 * 1000 )
                .build();

        if (jobScheduler != null) {
            jobScheduler.schedule(jobInfo);
        }
    }

        @Override
    public void onBackPressed(){
        m_OutDialog.cleanData();
            if (null != m_OutDialog) {
                m_OutDialog.cancel();
            }
        Toast.makeText(getApplicationContext(),"You Are Not Allowed to Exit the App", Toast.LENGTH_SHORT).show();
    }
}
