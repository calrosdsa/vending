package com.tcn.vending.springdemo.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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

import com.google.gson.Gson;
import com.tcn.vending.springdemo.Injection;
import com.tcn.vending.springdemo.R;
import com.tcn.vending.springdemo.data.dto.ActivoDto;
import com.tcn.vending.springdemo.data.dto.RequestConfirm;
import com.tcn.vending.springdemo.data.dto.RequestItem;
import com.tcn.vending.springdemo.data.dto.RequestItemResponse;
import com.tcn.vending.springdemo.data.dto.RequestRollback;
import com.tcn.vending.springdemo.data.dto.Result;
import com.tcn.vending.springdemo.data.models.Activo;
import com.tcn.vending.springdemo.domain.util.FileLogger;
import com.tcn.vending.springdemo.presentation.adapter.ActivoAdapter;
import com.tcn.vending.springdemo.presentation.adapter.RecyclerItemClickListener;
import com.tcn.vending.springdemo.data.models.User;
import com.tcn.vending.springdemo.domain.repository.AppDataSource;
import com.tcn.vending.springdemo.presentation.components.CustomAlertDialog;
import com.tcn.springboard.control.PayMethod;
import com.tcn.springboard.control.TcnVendEventID;
import com.tcn.springboard.control.TcnVendIF;
import com.tcn.springboard.control.VendEventInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import io.reactivex.disposables.CompositeDisposable;
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
    ActivoAdapter adapter;
    AppDataSource dataSource;
    User currrentUser = null;
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
    Result result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
        dataSource = Injection.provideUserDataSource(this);

        mUseText = findViewById(R.id.mtitle);
        hideButton = findViewById(R.id.hiddenButton);
        hideButton.setOnLongClickListener(view -> {
            passworDialog.show();
            input.setText("");
            return false;
        });
        initView();
//        scheduleJob();
        FileLogger.init();
        if (extras != null) {
            Log.d("DEBUG_APP_EXTRA","INIT");
            String name = extras.getString("name");
            String id = extras.getString("id");
            String data = extras.getString("data");
            if(data != null){
            Log.d("DEBUG_APP_GSON_2",data);
                ActivoDto dataActivos = new Gson().fromJson(data,ActivoDto.class);
                observeActivos(dataActivos.Result);

//            Log.d("DEBUG_APP_GSON_2",String.format("%s",dataActivos.Result));
//            AsyncTask.execute(()-> dataSource.insertActivos(dataActivos.Result));
//            getActivos(id);
            Log.d("DEBUG_APP_",name+id);
            currrentUser = new User(id,name);
            logError("onStart",String.format("UserName: %s - UserCode: %s",currrentUser.mName,currrentUser.mId));
            mUseText.setVisibility(View.VISIBLE);
            mUseText.setText(name);
            }
        }
//        String jsonVal = "{\n" +
//                "\t\"IsSuccess\": true,\n" +
//                "\t\"Result\": [\n" +
//                "        {\n" +
//                "            \"idCelda\": 2,\n" +
//                "            \"slotN\": 3,\n" +
//                "            \"celdaName\": \"2\",\n" +
//                "            \"activoName\": \"Cable de red\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"CABLE\",\n" +
//                "            \"activoBrand\": \"DESCONOCIDO\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 10,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 1,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 1,\n" +
//                "            \"slotN\": 1,\n" +
//                "            \"celdaName\": \"1\",\n" +
//                "            \"activoName\": \"Cable de red\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"CABLE\",\n" +
//                "            \"activoBrand\": \"DESCONOCIDO\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 0,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 1,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 3,\n" +
//                "            \"slotN\": 6,\n" +
//                "            \"celdaName\": \"3\",\n" +
//                "            \"activoName\": \"Mouse Pad\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"MOUSE PAD\",\n" +
//                "            \"activoBrand\": \"ADVANTECH\",\n" +
//                "            \"activoMode\": \"KMP-100\",\n" +
//                "            \"cantidad\": 3,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 1,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 4,\n" +
//                "            \"slotN\": 9,\n" +
//                "            \"celdaName\": \"4\",\n" +
//                "            \"activoName\": \"Mouse Pad\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"MOUSE PAD\",\n" +
//                "            \"activoBrand\": \"ADVANTECH\",\n" +
//                "            \"activoMode\": \"KMP-100\",\n" +
//                "            \"cantidad\": 3,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 1,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 5,\n" +
//                "            \"slotN\": 10,\n" +
//                "            \"celdaName\": \"5\",\n" +
//                "            \"activoName\": \"Cable de red\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"CABLE\",\n" +
//                "            \"activoBrand\": \"DESCONOCIDO\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 0,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 1,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 9,\n" +
//                "            \"slotN\": 11,\n" +
//                "            \"celdaName\": \"6\",\n" +
//                "            \"activoName\": \"Defcon \",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"LAPTOP HARD LOCK\",\n" +
//                "            \"activoBrand\": \"Targus\",\n" +
//                "            \"activoMode\": \"ASP71GLX-S\",\n" +
//                "            \"cantidad\": 1,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 2,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 10,\n" +
//                "            \"slotN\": 13,\n" +
//                "            \"celdaName\": \"7\",\n" +
//                "            \"activoName\": \"Defcon \",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"LAPTOP HARD LOCK\",\n" +
//                "            \"activoBrand\": \"Targus\",\n" +
//                "            \"activoMode\": \"ASP71GLX-S\",\n" +
//                "            \"cantidad\": 1,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 2,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 11,\n" +
//                "            \"slotN\": 15,\n" +
//                "            \"celdaName\": \"8\",\n" +
//                "            \"activoName\": \"Defcon \",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"LAPTOP HARD LOCK\",\n" +
//                "            \"activoBrand\": \"Targus\",\n" +
//                "            \"activoMode\": \"ASP71GLX-S\",\n" +
//                "            \"cantidad\": 1,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 2,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 12,\n" +
//                "            \"slotN\": 17,\n" +
//                "            \"celdaName\": \"9\",\n" +
//                "            \"activoName\": \"Defcon\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"LAPTOP HARD LOCK\",\n" +
//                "            \"activoBrand\": \"Targus\",\n" +
//                "            \"activoMode\": \"ASP71GLX-S\",\n" +
//                "            \"cantidad\": 1,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 2,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 13,\n" +
//                "            \"slotN\": 19,\n" +
//                "            \"celdaName\": \"10\",\n" +
//                "            \"activoName\": \"Defcon \",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"LAPTOP HARD LOCK\",\n" +
//                "            \"activoBrand\": \"Targus\",\n" +
//                "            \"activoMode\": \"ASP71GLX-S\",\n" +
//                "            \"cantidad\": 1,\n" +
//                "            \"celdas\": 2.0,\n" +
//                "            \"row\": 2,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 14,\n" +
//                "            \"slotN\": 21,\n" +
//                "            \"celdaName\": \"11\",\n" +
//                "            \"activoName\": \"Mouse \",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"TECLADO\",\n" +
//                "            \"activoBrand\": \"HP\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 4,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 3,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 15,\n" +
//                "            \"slotN\": 22,\n" +
//                "            \"celdaName\": \"12\",\n" +
//                "            \"activoName\": \"Teclado\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"MOUSE\",\n" +
//                "            \"activoBrand\": \"HP\",\n" +
//                "            \"activoMode\": \"125 Wired\",\n" +
//                "            \"cantidad\": 2,\n" +
//                "            \"celdas\": 7.0,\n" +
//                "            \"row\": 3,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 16,\n" +
//                "            \"slotN\": 29,\n" +
//                "            \"celdaName\": \"13\",\n" +
//                "            \"activoName\": \"Mouse\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"MOUSE\",\n" +
//                "            \"activoBrand\": \"HP\",\n" +
//                "            \"activoMode\": \"125 Wired\",\n" +
//                "            \"cantidad\": 2,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 3,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 17,\n" +
//                "            \"slotN\": 30,\n" +
//                "            \"celdaName\": \"14\",\n" +
//                "            \"activoName\": \"Camara Web\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"CAMARA WEB\",\n" +
//                "            \"activoBrand\": \"LOGITECH\",\n" +
//                "            \"activoMode\": \"C920\",\n" +
//                "            \"cantidad\": 4,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 3,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 18,\n" +
//                "            \"slotN\": 31,\n" +
//                "            \"celdaName\": \"15\",\n" +
//                "            \"activoName\": \"Camara Web \",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"TECLADO\",\n" +
//                "            \"activoBrand\": \"HP\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 5,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 4,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 19,\n" +
//                "            \"slotN\": 33,\n" +
//                "            \"celdaName\": \"16\",\n" +
//                "            \"activoName\": \"Teclado\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"CAMARA WEB\",\n" +
//                "            \"activoBrand\": \"LOGITECH\",\n" +
//                "            \"activoMode\": \"C920\",\n" +
//                "            \"cantidad\": 3,\n" +
//                "            \"celdas\": 7.0,\n" +
//                "            \"row\": 4,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 20,\n" +
//                "            \"slotN\": 39,\n" +
//                "            \"celdaName\": \"17\",\n" +
//                "            \"activoName\": \"Camara Web\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"CAMARA WEB\",\n" +
//                "            \"activoBrand\": \"LOGITECH\",\n" +
//                "            \"activoMode\": \"C920\",\n" +
//                "            \"cantidad\": 0,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 4,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 21,\n" +
//                "            \"slotN\": 40,\n" +
//                "            \"celdaName\": \"18\",\n" +
//                "            \"activoName\": \"Camara Web\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"CAMARA WEB\",\n" +
//                "            \"activoBrand\": \"LOGITECH\",\n" +
//                "            \"activoMode\": \"C920\",\n" +
//                "            \"cantidad\": 2,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 4,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 22,\n" +
//                "            \"slotN\": 41,\n" +
//                "            \"celdaName\": \"19\",\n" +
//                "            \"activoName\": \"Tomatodo\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"SOUVENIR\",\n" +
//                "            \"activoBrand\": \"DESCONOCIDO\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 0,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 5,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 23,\n" +
//                "            \"slotN\": 42,\n" +
//                "            \"celdaName\": \"20\",\n" +
//                "            \"activoName\": \"Tomatodo\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"SOUVENIR\",\n" +
//                "            \"activoBrand\": \"DESCONOCIDO\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 2,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 5,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 24,\n" +
//                "            \"slotN\": 44,\n" +
//                "            \"celdaName\": \"21\",\n" +
//                "            \"activoName\": \"Auricular \",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"AUDIFONO\",\n" +
//                "            \"activoBrand\": \"LOGITECH\",\n" +
//                "            \"activoMode\": \"h390\",\n" +
//                "            \"cantidad\": 0,\n" +
//                "            \"celdas\": 3.0,\n" +
//                "            \"row\": 5,\n" +
//                "            \"enabled\": false\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 25,\n" +
//                "            \"slotN\": 47,\n" +
//                "            \"celdaName\": \"22\",\n" +
//                "            \"activoName\": \"Tomatodo\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"SOUVENIR\",\n" +
//                "            \"activoBrand\": \"DESCONOCIDO\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 2,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 5,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 26,\n" +
//                "            \"slotN\": 51,\n" +
//                "            \"celdaName\": \"25\",\n" +
//                "            \"activoName\": \"Celular\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"Celular\",\n" +
//                "            \"activoBrand\": \"SAMSUNG\",\n" +
//                "            \"activoMode\": \"Galaxy A12\",\n" +
//                "            \"cantidad\": 5,\n" +
//                "            \"celdas\": 1.4,\n" +
//                "            \"row\": 6,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 27,\n" +
//                "            \"slotN\": 52,\n" +
//                "            \"celdaName\": \"26\",\n" +
//                "            \"activoName\": \"Celular\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"Celular\",\n" +
//                "            \"activoBrand\": \"SAMSUNG\",\n" +
//                "            \"activoMode\": \"Galaxy A12\",\n" +
//                "            \"cantidad\": 6,\n" +
//                "            \"celdas\": 1.4,\n" +
//                "            \"row\": 6,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 28,\n" +
//                "            \"slotN\": 54,\n" +
//                "            \"celdaName\": \"27\",\n" +
//                "            \"activoName\": \"Celular\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"Celular\",\n" +
//                "            \"activoBrand\": \"SAMSUNG\",\n" +
//                "            \"activoMode\": \"Galaxy A12\",\n" +
//                "            \"cantidad\": 6,\n" +
//                "            \"celdas\": 1.4,\n" +
//                "            \"row\": 6,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 29,\n" +
//                "            \"slotN\": 55,\n" +
//                "            \"celdaName\": \"28\",\n" +
//                "            \"activoName\": \"Celular\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"Celular\",\n" +
//                "            \"activoBrand\": \"SAMSUNG\",\n" +
//                "            \"activoMode\": \"Galaxy A12\",\n" +
//                "            \"cantidad\": 6,\n" +
//                "            \"celdas\": 1.4,\n" +
//                "            \"row\": 6,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 30,\n" +
//                "            \"slotN\": 56,\n" +
//                "            \"celdaName\": \"29\",\n" +
//                "            \"activoName\": \"Celular\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"Celular\",\n" +
//                "            \"activoBrand\": \"SAMSUNG\",\n" +
//                "            \"activoMode\": \"Galaxy A12\",\n" +
//                "            \"cantidad\": 6,\n" +
//                "            \"celdas\": 1.4,\n" +
//                "            \"row\": 6,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 32,\n" +
//                "            \"slotN\": 59,\n" +
//                "            \"celdaName\": \"31\",\n" +
//                "            \"activoName\": \"Celular\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"Celular\",\n" +
//                "            \"activoBrand\": \"SAMSUNG\",\n" +
//                "            \"activoMode\": \"Galaxy A12\",\n" +
//                "            \"cantidad\": 3,\n" +
//                "            \"celdas\": 1.4,\n" +
//                "            \"row\": 6,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 31,\n" +
//                "            \"slotN\": 58,\n" +
//                "            \"celdaName\": \"30\",\n" +
//                "            \"activoName\": \"Celular\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"Celular\",\n" +
//                "            \"activoBrand\": \"SAMSUNG\",\n" +
//                "            \"activoMode\": \"Galaxy A12\",\n" +
//                "            \"cantidad\": 3,\n" +
//                "            \"celdas\": 1.4,\n" +
//                "            \"row\": 6,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 33,\n" +
//                "            \"slotN\": 48,\n" +
//                "            \"celdaName\": \"23\",\n" +
//                "            \"activoName\": \"Tomatodo\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"SOUVENIR\",\n" +
//                "            \"activoBrand\": \"DESCONOCIDO\",\n" +
//                "            \"activoMode\": \"DESCONOCIDO\",\n" +
//                "            \"cantidad\": 2,\n" +
//                "            \"celdas\": 1.0,\n" +
//                "            \"row\": 5,\n" +
//                "            \"enabled\": true\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"idCelda\": 34,\n" +
//                "            \"slotN\": 49,\n" +
//                "            \"celdaName\": \"24\",\n" +
//                "            \"activoName\": \"Mouse\",\n" +
//                "            \"activoId\": null,\n" +
//                "            \"activoType\": \"MOUSE\",\n" +
//                "            \"activoBrand\": \"HP\",\n" +
//                "            \"activoMode\": \"125 Wired\",\n" +
//                "            \"cantidad\": 2,\n" +
//                "            \"celdas\": 3.0,\n" +
//                "            \"row\": 5,\n" +
//                "            \"enabled\": false\n" +
//                "        }\n" +
//                "    ],\n" +
//                "\t\"DisplayMessage\": \"\"\n" +
//                "}";
//        ActivoDto dataActivos = new Gson().fromJson(jsonVal,ActivoDto.class);
//        observeActivos(dataActivos.Result);
    }



    private CountDownTimer startTimer(){
        Log.d("DEBUG_APP_TIMER","START TIMER");
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
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);
        } catch (Exception e) {
            Log.d("DEBUG_APP_EXCP",e.getLocalizedMessage());
        }
    }



    @Override
    protected void onStart() {
        logError("onStart","onStart");
        super.onStart();
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
        alertDialog = new CustomAlertDialog(this,"","");
        setRecyclerView();
        setDialogPassword();
        if (m_OutDialog == null) {
            m_OutDialog = new OutDialog(this, "", "");
            m_OutDialog.setShowTime(30);
        }
        if (m_LoadingDialog == null) {
            m_LoadingDialog = new LoadingDialog(MainActivity.this, "", "",this::clearAppData);
            m_LoadingDialog.setListeners(dialogInterface -> clearAppData());

        }
        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.exit_btn).setOnClickListener(view -> {
            alertDialog.setLoadText("Por favor, confirma si deseas regresar a la pantalla de inicio.");
            alertDialog.setTitle("Salir");
            alertDialog.setConfirmOnclick(view1 -> clearAppData());
            alertDialog.show();
        });


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
                alertDialog.show();
            }
            @Override
            public void onLongItemClick(View view, int position) {
                Log.d("DEBUG_APP", "ITEMLOBGCLICKED");
            }
        });
}

   private void shipItem(){
       stopTimer();
       int slotNo = activo.slotN;//出货的货道号 dispensing slot number
       initLoader(slotNo);
       RequestItem r = new RequestItem(activo.idCelda,currrentUser.mId);
       Call<RequestItemResponse> call = dataSource.requestActivo(r);
       call.enqueue(new Callback<RequestItemResponse>() {
           @Override
           public void onResponse(Call<RequestItemResponse> call, Response<RequestItemResponse> response) {
               try{
                                    if (response.isSuccessful()) {
                                        RequestItemResponse res =response.body();
                                        assert res != null;
                                            if (res.IsSuccess) {
                                                result = res.Result;
                                                int slotNo = activo.slotN;//出货的货道号 dispensing slot number
                                                String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
                                                String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
                                                String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
                                                TcnVendIF.getInstance().ship(slotNo, shipMethod, amount, tradeNo);
                                                logError("ShipItem",String.format("Ship item id:%s - slotN:%s - userName:%s - userCode:%s",tradeNo,slotNo,currrentUser.mName,currrentUser.mId));
                                            } else {
                                                String errorMessages = new Gson().toJson(res.ErrorMessages);
//                                                Log.d("DEBUG_APP_API",errorMessages);
                                                logError("ShipItem",String.format("res IsSuccess = false :%s",errorMessages));
                                                showAlertMessage(getString(R.string.unexpected_error),"Error",false);
                                                resumeTimer();
                                            }
                                    }else{
                                        logError("ShipItem","response IsSuccessful = false");
                                        showAlertMessage(getString(R.string.unexpected_error),"Error",false);
                                        resumeTimer();
                                    }
                                    }catch(Exception e){
                                         resumeTimer();
                                        FileLogger.logError("Request_item_onResponse",e.getLocalizedMessage());
                                       showAlertMessage(getString(R.string.unexpected_error), "Error",false);
                                    }
                                }

                                @Override
                                public void onFailure(Call<RequestItemResponse> call, Throwable t) {
                                    resumeTimer();
                                    logError("RequestItem_onFailure",t.getLocalizedMessage());
                                    showAlertMessage(getString(R.string.connection_err),
                                            "Error",false);
                                    call.cancel();
                                }
                            });
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
//                    makeToast(String.format("Command_system_busy %s",cEventInfo.m_lParam4));
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
                case TcnVendEventID.COMMAND_SHIPMENT_FAULT:
                    rollback(cEventInfo.m_lParam4);
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;


                case TcnVendEventID.COMMAND_SHIPPING:
                    if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
                        initLoader(cEventInfo.m_lParam1);
                    }
                    logError("COMMAND_SHIPPING", cEventInfo.m_lParam4);
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                    rollback(cEventInfo.m_lParam4);
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                    dispensarSuccess(cEventInfo.m_lParam4);
                    break;
            }

        }
    }

    private void dispensarSuccess(String param){
        RequestConfirm requestDispensarSuccess = new RequestConfirm(result.codeUsuario,result.keyTicket,activo.idCelda);
        Call<RequestItemResponse> response =  dataSource.dispensarSuccess(requestDispensarSuccess);
        response.enqueue(new Callback<RequestItemResponse>() {
            @Override
            public void onResponse(Call<RequestItemResponse> call, Response<RequestItemResponse> response) {
                showAlertMessage(getString(R.string.successfull_message),"Operación exitosa",true);
                logError("COMMAND_SHIPPMENT_SUCCESS", param);
                resumeTimer();
                try {
                    if (response.isSuccessful()) {
                        RequestItemResponse res = response.body();
                        assert res != null;
                        if(res.IsSuccess){
                            Log.d("DEBUG_APP_API","SUCCESS");
                            logError("DispensarSuccess",String.format("IsSuccess(True) %s",param));
                        }else{
                            String errorMessages = new Gson().toJson(res.ErrorMessages);
                            Log.d("DEBUG_APP_API","UNSUCCESS");
                            logError("DispensarSuccess",String.format("IsSuccess(False) %s",errorMessages));
                        }
                    }else{
                        RequestItemResponse res = response.body();
                        String errorMessages = new Gson().toJson(res.ErrorMessages);
                        logError("DispensarSuccess",String.format("IsSuccess(False) %s",errorMessages));

                        logError("DispensaSuccess",String.format("UnSuccessful %s - Message:%s",param,response.message()));
                        Log.d("DEBUG_APP_API",String.format("res unseccessful %s",response.message()));
                    }
                }catch (Exception e){
                    logError("DispensaSuccess",String.format("Exception %s",e.getLocalizedMessage()));
                    Log.d("DEBUG_APP_API_ERR",e.getLocalizedMessage());

                }
            }

            @Override
            public void onFailure(Call<RequestItemResponse> call, Throwable t) {

                logError("dispensarSuccess_onFailure",String.format("%s -  %s",t.getLocalizedMessage(),param));
                Log.d("DEBUG_APP_API_ERR_F",t.getLocalizedMessage());
                showAlertMessage(getString(R.string.successfull_message),"Operación exitosa",true);
                resumeTimer();
                call.cancel();
            }
        });
    }

    private void rollback(String param){
            RequestRollback requestRollback = new RequestRollback(result.codeUsuario,result.keyTicket,result.keyActivoAntiguo,result.keyActivoNuevo);
            Call<RequestItemResponse> response =  dataSource.rollback(requestRollback);
            response.enqueue(new Callback<RequestItemResponse>() {
                @Override
                public void onResponse(Call<RequestItemResponse> call, Response<RequestItemResponse> response) {
                    String loadText2 = "Error al dispensar. Por favor, póngase en contacto con el servicio de soporte.";
                    String loadTitle1 = "Error";
                    showAlertMessage(loadText2,loadTitle1, true);
                    logError("COMMAND_SHIPPMENT_FAILURE", param);
                    resumeTimer();
                    try {
                        if (response.isSuccessful()) {
                            RequestItemResponse res = response.body();
                            assert res != null;
                            if(res.IsSuccess){
                                Log.d("DEBUG_APP_API","SUCCESS");
                                logError("Rollback",String.format("IsSuccess(True) %s",param));
                            }else{
                                Log.d("DEBUG_APP_API","UNSUCCESS");
                                logError("Rollback",String.format("IsSuccess(False) %s",param));
                                String errorMessages = new Gson().toJson(res.ErrorMessages);
                                logError("Rollback",String.format("IsSuccess(False) %s",errorMessages));
                            }
                        }else{
                            logError("Rollback",String.format("UnSuccessful %s - Message:%s",param,response.message()));
                            Log.d("DEBUG_APP_API",String.format("res unseccessful %s",response.message()));
                            RequestItemResponse res = response.body();
                            assert res != null;
                            String errorMessages = new Gson().toJson(res.ErrorMessages);
                            logError("Rollback",String.format("IsSuccess(False) %s",errorMessages));
                        }
                    }catch (Exception e){
                        logError("Rollback",String.format("Exception %s",e.getLocalizedMessage()));
                        Log.d("DEBUG_APP_API_ERR",e.getLocalizedMessage());

                    }
                }

                @Override
                public void onFailure(Call<RequestItemResponse> call, Throwable t) {
                    String loadText2 = "Error al dispensar. Por favor, póngase en contacto con el servicio de soporte.";
                    String loadTitle1 = "Error";
                    logError("COMMAND_SHIPPMENT_FAILURE", param);

                    logError("rollback_onFailure",String.format("%s -  %s",t.getLocalizedMessage(),param));
                   Log.d("DEBUG_APP_API_ERR_F",t.getLocalizedMessage());
                    showAlertMessage(loadText2,loadTitle1, true);
                    resumeTimer();
                    call.cancel();
                }
            });
    }

    private void logError(String origin,String message){
        FileLogger.logError(origin, message);
    }

    private void showAlertMessage(String loadText,String loadTitle,boolean closeApp){
        if (null != m_OutDialog) {
            m_OutDialog.cancel();
        }
        m_LoadingDialog.setLoadText(loadText);
        m_LoadingDialog.setTitle(loadTitle);
        m_LoadingDialog.setEnableCloseApp(closeApp);
        m_LoadingDialog.setShowTime(5);
        m_LoadingDialog.show();
    }

    private void initLoader(int slotNumber){
        if (m_OutDialog == null) {
            m_OutDialog = new OutDialog(MainActivity.this, String.valueOf(slotNumber), "Por favor, espere");
        } else {
            m_OutDialog.setText("Por favor, espere");
        }
        m_OutDialog.cleanData();
        m_OutDialog.setNumber(String.valueOf(slotNumber));
        m_OutDialog.show();
    }
    private void resumeTimer(){
        if(timer != null) return;
        CountDownTimer initTimer = startTimer();
        timer = initTimer.start();
    }
    private void stopTimer(){
        if(timer == null) return;
        timer.cancel();
        timer = null;
    }

    @Override // com.tcn.app_common.view.TcnMainActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return super.dispatchTouchEvent(motionEvent);
    }
//    protected Constraints constraints = new Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.UNMETERED)
//            .build();
//
//    private void scheduleJob() {
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        ComponentName componentName = new ComponentName(this, SyncShipmentsSchedule.class);
//
//        JobInfo jobInfo = new JobInfo.Builder(1001, componentName)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .setPeriodic(15 * 60 * 1000 )
//                .build();
//
//        if (jobScheduler != null) {
//            jobScheduler.schedule(jobInfo);
//        }
//    }

        @Override
    public void onBackPressed(){
        m_OutDialog.cleanData();
            if (null != m_OutDialog) {
                m_OutDialog.cancel();
            }
//        Toast.makeText(getApplicationContext(),"You Are Not Allowed to Exit the App", Toast.LENGTH_SHORT).show();
    }


    private void setDialogPassword() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");
            View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_password, null, false);
            input = viewInflated.findViewById(R.id.input);
            final TextView textError = (TextView) viewInflated.findViewById(R.id.error_text);
            builder.setView(viewInflated);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                if (Objects.equals(input.getText().toString(), "6605")) {
                    stopTimer();
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    input.setText("");
                    Intent mainIntent = new Intent(MainActivity.this, MenuConfigActivity.class);
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

    private List<Activo> sortActivosRow(List<Activo> activos) {
        Collections.sort(activos, new Comparator<Activo>() {
            @Override
            public int compare(Activo activo, Activo t1) {
                return Integer.compare(activo.slotN, t1.slotN);
            }
        });
        return activos;
    }

    protected void observeActivos(List<Activo> activos) {
        mActivos = activos;
        Map<Integer, List<Activo>> groupCeldas =
                activos.stream().collect(Collectors.groupingBy(w -> w.row));
        List<Activo> rowActivos;
        for (Map.Entry<Integer, List<Activo>> entry : groupCeldas.entrySet()) {
            switch (entry.getKey()) {
                case 1:
                    rowActivos = sortActivosRow(entry.getValue());
                    adapter = new ActivoAdapter(rowActivos);
                    recyclerView1.setAdapter(adapter);
                    recyclerView1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    rowActivos = sortActivosRow(entry.getValue());
                    adapter = new ActivoAdapter(rowActivos);
                    recyclerView2.setAdapter(adapter);
                    recyclerView2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    rowActivos = sortActivosRow(entry.getValue());
                    adapter = new ActivoAdapter(rowActivos);
                    recyclerView3.setAdapter(adapter);
                    recyclerView3.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    rowActivos = sortActivosRow(entry.getValue());
                    adapter = new ActivoAdapter(rowActivos);
                    recyclerView4.setAdapter(adapter);
                    recyclerView4.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    rowActivos = sortActivosRow(entry.getValue());
                    adapter = new ActivoAdapter(rowActivos);
                    recyclerView5.setAdapter(adapter);
                    recyclerView5.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    rowActivos = sortActivosRow(entry.getValue());
                    adapter = new ActivoAdapter(rowActivos);
                    recyclerView6.setAdapter(adapter);
                    recyclerView6.setVisibility(View.VISIBLE);
                    break;
            }
//                        if(entry.getKey() == 1){
//                            adapter = new ActivoAdapter(entry.getValue());
//                            recyclerView1.setAdapter(adapter);
//                        }
        }
        if (!activos.isEmpty()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}
