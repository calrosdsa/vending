package com.tcn.vending.springdemo.presentation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.NetworkType;

import com.tcn.vending.springdemo.Injection;
import com.tcn.vending.springdemo.R;
import com.tcn.vending.springdemo.data.models.Activo;
import com.tcn.vending.springdemo.domain.util.FileLogger;
import com.tcn.vending.springdemo.presentation.adapter.ActivoAdapter;
import com.tcn.vending.springdemo.presentation.adapter.RecyclerItemClickListener;
import com.tcn.vending.springdemo.data.models.ShipmentState;
import com.tcn.vending.springdemo.data.models.User;
import com.tcn.vending.springdemo.domain.repository.AppDataSource;
import com.tcn.vending.springdemo.presentation.components.buttons.CustomAlertDialog;
import com.tcn.springboard.control.PayMethod;
import com.tcn.springboard.control.TcnVendEventID;
import com.tcn.springboard.control.TcnVendIF;
import com.tcn.springboard.control.VendEventInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import controller.SyncShipmentsSchedule;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends TcnMainActivity {
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;
    RecyclerView recyclerView4;
    RecyclerView recyclerView5;
    RecyclerView recyclerView6;
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
    ProgressBar progressBar;
    long counterTimer = 60000;
    private OutDialog m_OutDialog = null;
    private LoadingDialog m_LoadingDialog = null;
    private CustomAlertDialog alertDialog = null;
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

        new CountDownTimer(60000, 1000) {
            @SuppressLint("SuspiciousIndentation")
            public void onTick(long millisUntilFinished) {
//                counterTimer -= 1000;
                Log.d("DEBUG_APP_TIMER",String.format("%s  --- %s",counterTimer,millisUntilFinished));

                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                clearAppData();
//                    closeSession();
                Log.d("DEBUG_APP_TIMER","456");
            }
        }.start();


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

    private void clearAppData() {
        try {
//            Backup.backupDatabase(this);
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);
//            stopService(new Intent(getApplication(), VendService.class));
//            System.exit(0);
        } catch (Exception e) {
            Log.d("DEBUG_APP_EXCP",e.getLocalizedMessage());
        }
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

    private void initView() {
        loadingDialog = new SpotsDialog.Builder().setContext(MainActivity.this)
                .build();
        alertDialog = new CustomAlertDialog(this,"","");


        setRecyclerView();
        setDialogPassword();

        if (m_OutDialog == null) {
            m_OutDialog = new OutDialog(this, "", "");
            m_OutDialog.setShowTime(30);
        }
        if (m_LoadingDialog == null) {
            m_LoadingDialog = new LoadingDialog(MainActivity.this, "", "",this::clearAppData);
//            m_LoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialogInterface) {
//                    clearAppData();
//                }
//            });
            m_LoadingDialog.setListeners(dialogInterface -> clearAppData());

        }
        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.exit_btn).setOnClickListener(view -> {
            alertDialog.setLoadText("Por favor, confirma si deseas regresar a la pantalla de inicio.");
            alertDialog.setTitle("Salir");
            alertDialog.setConfirmOnclick(view1 -> clearAppData());
            alertDialog.setHeight(380);
            alertDialog.show();
        });

//        findViewById(R.id.testSlot2).setOnClickListener(view -> {
//            RequestDispensar r = new RequestDispensar("3","900000100","39236","IY-39236","0");
//
//            Call<RequestItemResponse> call = dataSource.updateActivo(r);
//            call.enqueue(new Callback<RequestItemResponse>() {
//                @Override
//                public void onResponse(Call<RequestItemResponse> call, Response<RequestItemResponse> response) {
//                    try{
//                        Log.d("DEBUG_APP_API",response.message());
//                        Log.d("DEBUG_APP_API",response.body().toString());
//                        Log.d("DEBUG_APP_API",String.format("%s", response.isSuccessful()));
//                        if (response.isSuccessful()) {
//                            RequestItemResponse res =response.body();
//                            assert res != null;
////                                        showAlertMessage(res.Result.idActivo,"REQUEST ACTIVO");
//                            if (res.Result != null) {
//                                if (res.Result.disponible) {
//                                    showAlertMessage(res.Result.keyActivo,"REQUEST ACTIVO Disponible");
//                                } else {
//                                    showAlertMessage("El usuario --- no tiene asignado este activo","");
////                                                resumeTimer();
//                                }
//                            }
//                        }else{
//                            showAlertMessage("Response no successful","");
//                        }
//                    }catch(Exception e){
//                        Log.d("DEBUG_APP_API",e.getLocalizedMessage());
//                        showAlertMessage(String.format("Exception %s",e.getLocalizedMessage()),"");
//                        FileLogger.logError("Request_item_onResponse",e.getLocalizedMessage());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<RequestItemResponse> call, Throwable t) {
////                                    resumeTimer();
//                    FileLogger.logError("RequestItem_onFailure",t.getLocalizedMessage());
//                    showAlertMessage(String.format("onFailure %s",t.getLocalizedMessage()),"");
//                    Log.d("DEBUG_APP_API",t.getLocalizedMessage());
//                    Snackbar.make(view, "Ocurrio un error inesperado", Snackbar.LENGTH_LONG).show();
//                    call.cancel();
//                }
//            });
//                 });
//
//        findViewById(R.id.testSlot1).setOnClickListener(view -> {
////            if(timer == null) {
////                Log.d("DEBUG_APP","TIMER IS NULL");
////                return;
////            }
//
//            RequestItem r = new RequestItem("3","900000100");
//
//                            Call<RequestItemResponse> call = dataSource.requestActivo(r);
//                            call.enqueue(new Callback<RequestItemResponse>() {
//                                @Override
//                                public void onResponse(Call<RequestItemResponse> call, Response<RequestItemResponse> response) {
//                                    try{
//                                    Log.d("DEBUG_APP_API",response.message());
//                                        Log.d("DEBUG_APP_API",response.body().toString());
//                                        Log.d("DEBUG_APP_API",String.format("%s", response.isSuccessful()));
//                                    if (response.isSuccessful()) {
//                                        RequestItemResponse res =response.body();
//                                        assert res != null;
////                                        showAlertMessage(res.Result.idActivo,"REQUEST ACTIVO");
//                                        if (res.Result != null) {
//                                            if (res.Result.disponible) {
//                                                showAlertMessage(res.Result.keyActivo,"REQUEST ACTIVO Disponible");
//                                            } else {
//                                                showAlertMessage("El usuario --- no tiene asignado este activo","");
////                                                resumeTimer();
//                                            }
//                                        }
//                                    }else{
//                                        showAlertMessage("Response no successful","");
//                                    }
//                                    }catch(Exception e){
//                                        Log.d("DEBUG_APP_API",e.getLocalizedMessage());
//                                        showAlertMessage(String.format("Exception %s",e.getLocalizedMessage()),"");
//                                        FileLogger.logError("Request_item_onResponse",e.getLocalizedMessage());
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<RequestItemResponse> call, Throwable t) {
////                                    resumeTimer();
//                                    FileLogger.logError("RequestItem_onFailure",t.getLocalizedMessage());
//                                    showAlertMessage(String.format("onFailure %s",t.getLocalizedMessage()),"");
//                                    Log.d("DEBUG_APP_API",t.getLocalizedMessage());
//                                    Snackbar.make(view, "Ocurrio un error inesperado", Snackbar.LENGTH_LONG).show();
//                                    call.cancel();
//                                }
//                            });
//
//
//        });


//        helpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialogInterface) {
//                    Log.d("DEBUG_APP_","CANCEL_DIALOG");
//                    if(timer == null){
//                    resumeTimer();
//                    }
//                }
//            });
//        findViewById(R.id.helpButton).setOnClickListener(view->{
//        if(timer != null){
//            stopTimer();
//        }
//            helpDialog.show();
//        });

//        Slider.init(new PicassoImageLoadingService(this));
//        slider = findViewById(R.id.banner_slider1);
//        slider.setAdapter(new MainSliderAdapter());
//        slider.setInterval(5000);
//        slider.setLoopSlides(true);
//        slider.setAnimateIndicators(true);
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
//                    showAlertMessage("El activo que has seleccionado parece no estar en tu inventario.","No permitido");
                    return;
                }

                alertDialog.setTitle("Para continuar confirme su solicitud");
                alertDialog.setLoadText(getString(R.string.confirmar_dipensar_text,activo.activoName));
                alertDialog.setConfirmOnclick(view1 -> shipItem());
                alertDialog.setHeight(450);
                alertDialog.show();

//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Para continuar confirme su solicitud");
//                builder.setMessage("Si desea continuar con la dispensacion de un Maouse Rowel porfavor presione confirmar");
//                builder.setCancelable(true);
//                builder.setPositiveButton("Confirmar", (dialogInterface, i) -> {
//                    shipItem();
//                });
////                builder.setOnCancelListener(dialogInterface -> resumeTimer());
//                builder.setNegativeButton("Cancelar", null);
//                builder.show();




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
                   showAlertMessage(content,"Error",false);
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
                   String content = "Se produjo un error inesperado. Por favor, inténtelo nuevamente o póngase en contacto con el servicio de soporte.";
                   showAlertMessage(content,"Error",false);
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
                    String loadText = "Error al dispensar. Por favor, póngase en contacto con el servicio de soporte.";
                    String loadTitle = "Fault";
                    showAlertMessage(loadText,loadTitle,true);

//                    closeSession();
                    resumeTimer();
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
                    String loadText2 = "Error al dispensar. Por favor, póngase en contacto con el servicio de soporte.";
                    String loadTitle1 = "Error";
                    showAlertMessage(loadText2,loadTitle1,true);

//                    closeSession();
                    resumeTimer();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                    showAlertMessage("Se ha dispensado con éxito y se ha actualizado su inventario","Operación exitosa",true);
//                    AsyncTask.execute(()->{
//                        dataSource.updateShipmentState(ShipmentState.SUCCESS.ordinal(),cEventInfo.m_lParam4);
////                        dataSource.requestDispensar(cEventInfo.m_lParam4);
//                    });
                   resumeTimer();
//                    closeSession();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
            }

        }
    }

    private void showAlertMessage(String loadText,String loadTitle,boolean closeApp){
        if (null != m_OutDialog) {
            m_OutDialog.cancel();
        }
       /* if (null == m_LoadingDialog) {
            m_LoadingDialog = new LoadingDialog(MainActivity.this, loadText, loadTitle, this::clearAppData);
        }*/
        m_LoadingDialog.setLoadText(loadText);
        m_LoadingDialog.setTitle(loadTitle);
        m_LoadingDialog.setEnableCloseApp(closeApp);
        m_LoadingDialog.setShowTime(5);
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
                                recyclerView1.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView2.setAdapter(adapter);
                                recyclerView2.setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView3.setAdapter(adapter);
                                recyclerView3.setVisibility(View.VISIBLE);
                                break;
                            case 4:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView4.setAdapter(adapter);
                                recyclerView4.setVisibility(View.VISIBLE);
                                break;
                            case 5:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView5.setAdapter(adapter);
                                recyclerView5.setVisibility(View.VISIBLE);
                                break;
                            case 6:
                                adapter = new ActivoAdapter(entry.getValue());
                                recyclerView6.setAdapter(adapter);
                                recyclerView6.setVisibility(View.VISIBLE);
                                break;
                        }
//                        if(entry.getKey() == 1){
//                            adapter = new ActivoAdapter(entry.getValue());
//                            recyclerView1.setAdapter(adapter);
//                        }
                    }
                    if(!activos.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    }
                }, thorwable -> {
                    Log.d("DEBUG_APP_ERR", "DASDAS", thorwable);
                    FileLogger.logError("observeActivos",thorwable.getLocalizedMessage());
                }));
    }

    boolean openSecondApp2(){
        return true;
    }

}
