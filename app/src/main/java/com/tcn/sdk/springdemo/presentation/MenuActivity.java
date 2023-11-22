package com.tcn.sdk.springdemo.presentation;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tcn.sdk.springdemo.Injection;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.domain.util.FileLogger;
import com.tcn.sdk.springdemo.presentation.adapter.CeldaAdapter;
import com.tcn.sdk.springdemo.presentation.adapter.RecyclerItemClickListener;
import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.ShipmentState;
import com.tcn.sdk.springdemo.data.repository.AppPreferences;
import com.tcn.sdk.springdemo.domain.repository.AppDataSource;
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

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MenuActivity extends TcnMainActivity {
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;
    RecyclerView recyclerView4;
    RecyclerView recyclerView5;
    RecyclerView recyclerView6;

    CeldaAdapter adapter;
    AppDataSource dataSource;
    Button mergeButton;
    Button splitButton;
    Button splitAllB;
    AlertDialog loadingDialog;
    AlertDialog messageDialog;
    int currentPosition = -1;
    int positionSlot = -1;
    int currentRow = -1;
    Celda mCelda;
    List<Celda> mCeldas;
    TextView mPosition;
    AppPreferences preferences;
    private OutDialog m_OutDialog = null;
    private LoadingDialog m_LoadingDialog = null;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_menu);
        dataSource = Injection.provideUserDataSource(this);
        preferences = AppPreferences.getInstance(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        observeCeldas();
        if(!preferences.getIsDbPopulated()){
            populateDb();
            showMessage("Alredy inserted");
            preferences.setIsDbPopulated(true);
        }
//        wSocker.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUG_APP_LIST_2","REGISTER LISTENER");
        TcnVendIF.getInstance().registerListener(m_vendListener);
//        TcnVendIF.getInstance().setOnShipListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DEBUG_APP_LIST_2","UNREGISTER LISTENER");
        TcnVendIF.getInstance().unregisterListener(m_vendListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_vendListener = null;
        Log.d("DEBUG_APP_LIST_2","DESTROY LISTENER");
    }


    protected void  initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setRecyclerView();

        if (m_OutDialog == null) {
            m_OutDialog = new OutDialog(MenuActivity.this, "", getString(R.string.background_drive_setting));
            m_OutDialog.setShowTime(10000);
        }

        mPosition = findViewById(R.id.position);
        splitButton = findViewById(R.id.splitButton);
        mergeButton = findViewById(R.id.mergeButton);
        splitAllB = findViewById(R.id.splitAllB);
        Button testSlotB = findViewById(R.id.testSlot);
        Button viewLogs = findViewById(R.id.view_logs);
        testSlotB.setOnClickListener(view -> {

            int slotNo = positionSlot+1;//出货的货道号 dispensing slot number
            String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
            String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
            String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
            TcnVendIF.getInstance().reqShip(slotNo, shipMethod, amount, tradeNo);
        });
        splitAllB.setOnClickListener(view -> {
            splitAll();
        });
        mergeButton.setOnClickListener(view -> {
            mergeCelda();
        });
        splitButton.setOnClickListener(view -> {
            splitCelda();
//            wSocker.start();
//            showMessage();(hubConnection.getConnectionState().name());
        });
        viewLogs.setOnClickListener(view-> {

//            Intent intent = new Intent(this,LogActivity.class);
//            startActivity(intent);
            clearAppData();
        });

        loadingDialog = new SpotsDialog.Builder().setContext(MenuActivity.this)
                .build();

        messageDialog = new AlertDialog.Builder(MenuActivity.this)
                .setTitle("Ocurrio un error")
                .setMessage("No se pudo dispensar el activo porfavor comuniquese con el proveedor")
                .setPositiveButton("OK", null)
//                .setNegativeButton("Cancel", null)
                .create();
    }

    private void clearAppData() {
        try {
            // clearing app data
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void populateDb() {
        AsyncTask.execute(() -> {
            ArrayList<Celda> list = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                Celda celda = new Celda(i, i, 1,true);
                list.add(celda);
            }
             list.add(new Celda(10, 10, 1,false));
            for (int i = 11; i < 20; i++) {
                Celda celda = new Celda(i, i, 2,true);
                list.add(celda);
            }
            list.add(new Celda(20, 20, 2,false));

            for (int i = 21; i < 30; i++) {
                Celda celda = new Celda(i, i, 3,true);
                list.add(celda);
            }
            list.add(new Celda(30, 30, 3,false));

            for (int i = 31; i < 40; i++) {
                Celda celda = new Celda(i, i, 4,true);
                list.add(celda);
            }
            list.add(new Celda(40, 40, 4,false));

            for (int i = 41; i < 50; i++) {
                Celda celda = new Celda(i, i, 5,true);
                list.add(celda);
            }
            list.add(new Celda(50, 50, 5,false));

            for (int i = 51; i < 60; i++) {
                Celda celda = new Celda(i, i, 6,true);
                list.add(celda);
            }
            list.add(new Celda(60, 60, 6,false));
            dataSource.insertAllCeldas(list);
        });
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

    RecyclerItemClickListener clickItem(RecyclerView currentRecycler) {
        return  new RecyclerItemClickListener(this, currentRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemClick(View view, int position) {
                switch (currentRecycler.getId()){
                    case R.id.row_1:
                        selectCurrentSlot(position,1);
                        break;
                    case R.id.row_2:
                        selectCurrentSlot(position,2);
                        break;
                    case R.id.row_3:
                        selectCurrentSlot(position,3);
                        break;
                    case R.id.row_4:
                        selectCurrentSlot(position,4);
                        break;
                    case R.id.row_5:
                        selectCurrentSlot(position,5);
                        break;
                    case R.id.row_6:
                        selectCurrentSlot(position,6);
                        break;
                }
//                int slotNo = position + 1;//出货的货道号 dispensing slot number
//                String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
//                String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
//                String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
//                TcnVendIF.getInstance().ship(slotNo, shipMethod, amount, tradeNo);
//                AsyncTask.execute(() -> {
//                });
            }

            @Override
            public void onLongItemClick(View view, int position) {
//                Intent mainIntent = new Intent(MenuActivity.this, MainAct.class);
//                MenuActivity.this.startActivity(mainIntent);
                Log.d("DEBUG_APP", "ITEMLOBGCLICKED");
                // do whatever
            }
        });
    }

    protected void selectCurrentSlot(int position,int row){
        try{

        if(currentPosition != -1 && currentRow != -1){
            showMessage("REMOVE ----");
            Drawable card = ContextCompat.getDrawable(this,R.drawable.card);
            switch (currentRow){
                case 1:
                    showMessage("REMOVE");
                    View oldView =  recyclerView1.getLayoutManager().findViewByPosition(currentPosition);
                    oldView.setBackground(card);
//                    recyclerView1.getLayoutManager().findViewByPosition(position).setBackground(null);
                    break;
                case 2:
                    showMessage("REMOVE2");
                    recyclerView2.getLayoutManager().findViewByPosition(currentPosition).setBackground(card);
                    break;
                case 3:
                    showMessage("REMOVE2");
                    recyclerView3.getLayoutManager().findViewByPosition(currentPosition).setBackground(card);
                    break;
                case 4:
                    showMessage("REMOVE2");
                    recyclerView4.getLayoutManager().findViewByPosition(currentPosition).setBackground(card);
                    break;
                case 5:
                    showMessage("REMOVE2");
                    recyclerView5.getLayoutManager().findViewByPosition(currentPosition).setBackground(card);
                    break;
                case 6:
                    showMessage("REMOVE2");
                    recyclerView6.getLayoutManager().findViewByPosition(currentPosition).setBackground(card);
                    break;
            }

        }
        Drawable res = ContextCompat.getDrawable(this,R.drawable.selectedcard);
        switch (row){
            case 1:
                recyclerView1.getLayoutManager().findViewByPosition(position).setBackground(res);
                break;
            case 2:
                recyclerView2.getLayoutManager().findViewByPosition(position).setBackground(res);
                break;
            case 3:
                recyclerView3.getLayoutManager().findViewByPosition(position).setBackground(res);
                break;
            case 4:
                recyclerView4.getLayoutManager().findViewByPosition(position).setBackground(res);
                break;
            case 5:
                recyclerView5.getLayoutManager().findViewByPosition(position).setBackground(res);
                break;
            case 6:
                recyclerView6.getLayoutManager().findViewByPosition(position).setBackground(res);
                break;
        }
        List<Celda> filterRows = mCeldas.stream()
                .filter(p -> p.mRowNumber == row).collect(Collectors.toList());
        mCelda = filterRows.get(position);
        positionSlot = mCelda.mSlotNumber - 1;
        currentPosition = position;
        currentRow = row;
        mPosition.setText(String.format("%s",mCelda.mSlotNumber));
        }catch (Exception e){
            FileLogger.logError("selectCurrentSlot",e.getLocalizedMessage());
        }
    }

    protected void mergeCelda() {
        try{

        Log.d("DEBUG_APP_C",String.format("%s --- %s---",mCeldas.size(),positionSlot));
        if(60 > positionSlot){
        Celda nextSlot = mCeldas.get(positionSlot +1);
        if(nextSlot.mIsMerged){
            showMessage("La siguiente celda ya ha sido unida");
            return;
        }
        }
        if (mCelda.mCanMerged) {
            if (mCelda.mIsMerged) {
                showMessage("Esta celda ya ha sido unida");
            } else {
                AsyncTask.execute(() -> {
                    mCelda = dataSource.mergeCelda(mCelda);
                });
                TcnVendIF.getInstance().reqDoubleSlot(mCelda.mSlotNumber);
            }
        }else {
            showMessage("No se puede unir con la siguiente celda");
        }
        }catch (Exception e){
            FileLogger.logError("mergeCelda",e.getLocalizedMessage());
        }
    }

    protected void splitAll(){
        TcnVendIF.getInstance().reqSingleAllSlot(-1);
        populateDb();
    }
    protected void splitCelda() {
        if(mCelda.mIsMerged){
            AsyncTask.execute(() -> {
                mCelda = dataSource.splitCelda(mCelda);
            });
            TcnVendIF.getInstance().reqSingleSlot(mCelda.mSlotNumber);
        }else{
            showMessage("Esta celda ya es unica");
        }
    }

    protected void  showMessage(String message){
        View view=findViewById(android.R.id.content).getRootView();
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
    protected void observeCeldas() {
        mDisposable.add(dataSource.observeCeldas()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(celdas -> {
                    mCeldas = celdas;
                    Log.d("DEBUG_APP","RETRIEVE CELDAS");
                    Map<Integer, List<Celda>> groupCeldas =
                            celdas.stream().collect(Collectors.groupingBy(w -> w.mRowNumber));
                    Log.d("DEBUG_APP",String.format("RETRIEVE CELDAS %s",groupCeldas.size()));
                    for (Map.Entry<Integer, List<Celda>> entry : groupCeldas.entrySet()) {
                        switch (entry.getKey()) {
                            case 1:
                                Log.d("DEBUG_APP","1");
                                adapter = new CeldaAdapter(entry.getValue());
                                recyclerView1.setAdapter(adapter);
                                break;
                            case 2:
                                Log.d("DEBUG_APP","2");
                                adapter = new CeldaAdapter(entry.getValue());
                                recyclerView2.setAdapter(adapter);
                                break;
                            case 3:
                                Log.d("DEBUG_APP","3");
                                adapter = new CeldaAdapter(entry.getValue());
                                recyclerView3.setAdapter(adapter);
                                break;
                            case 4:
                                Log.d("DEBUG_APP","4");
                                adapter = new CeldaAdapter(entry.getValue());
                                recyclerView4.setAdapter(adapter);
                                break;
                            case 5:
                                Log.d("DEBUG_APP","5");
                                adapter = new CeldaAdapter(entry.getValue());
                                recyclerView5.setAdapter(adapter);
                                break;
                            case 6:
                                Log.d("DEBUG_APP","6");
                                adapter = new CeldaAdapter(entry.getValue());
                                recyclerView6.setAdapter(adapter);
                                break;
                        }
                    }

                }, thorwable -> FileLogger.logError("observeCeldas",thorwable.getLocalizedMessage())));
    }



    private VendListener m_vendListener = new VendListener();
    private class VendListener implements TcnVendIF.VendEventListener {
        @Override
        public void VendEvent(VendEventInfo cEventInfo) {
            if (null == cEventInfo) {
                TcnVendIF.getInstance().LoggerError("DEBUG_APP", "VendListener cEventInfo is null");
//                return;
            }

            switch (Objects.requireNonNull(cEventInfo).m_iEventID) {
                case TcnVendEventID.COMMAND_SYSTEM_BUSY:
                    showMessage(String.format("Command_system_busy %s",cEventInfo.m_lParam4));
//                    TcnUtilityUI.getToast(MenuActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:
                    String loadText = "Error de dispensación, llame al servicio de atención al cliente";
                    String loadTitle = "";
                    if (null != m_OutDialog) {
                        m_OutDialog.cancel();
                    }
                    if (null == m_LoadingDialog) {
                        m_LoadingDialog = new LoadingDialog(MenuActivity.this, loadText, loadTitle);
                    }
                    m_LoadingDialog.setLoadText(loadText);
                    m_LoadingDialog.setTitle(loadTitle);
                    m_LoadingDialog.setShowTime(3);
                    m_LoadingDialog.show();
                    TcnVendIF.getInstance().unregisterListener(m_vendListener);
                    break;
                case TcnVendEventID.COMMAND_SHIPMENT_FAULT:
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.FAULT.ordinal(),cEventInfo.m_lParam4);
                    });
                    if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(MenuActivity.this, String.valueOf(cEventInfo.m_lParam1), cEventInfo.m_lParam4);
                        } else {
                            m_OutDialog.setText(cEventInfo.m_lParam4);
                        }
                        m_OutDialog.cleanData();
                    } else {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(MenuActivity.this, String.valueOf(cEventInfo.m_lParam1), getString(R.string.ui_base_notify_shipping));
                        } else {
                            m_OutDialog.setText("Porfavor spere");
                        }
                    }
                    m_OutDialog.setNumber(String.valueOf(cEventInfo.m_lParam1));
                    m_OutDialog.show();
                    break;

                case TcnVendEventID.COMMAND_SHIPPING:
                    if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(MenuActivity.this, String.valueOf(cEventInfo.m_lParam1), cEventInfo.m_lParam4);
                        } else {
                            m_OutDialog.setText(cEventInfo.m_lParam4);
                        }
                        m_OutDialog.cleanData();
                    } else {
                        if (m_OutDialog == null) {
                            m_OutDialog = new OutDialog(MenuActivity.this, String.valueOf(cEventInfo.m_lParam1), getString(R.string.ui_base_notify_shipping));
                        } else {
                            m_OutDialog.setText(MenuActivity.this.getString(R.string.ui_base_notify_shipping));
                        }
                    }
                    m_OutDialog.setNumber(String.valueOf(cEventInfo.m_lParam1));
                    m_OutDialog.show();
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SHIPPING.ordinal(),cEventInfo.m_lParam4);
                    });
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:

                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SUCCESS.ordinal(),cEventInfo.m_lParam4);
                    });
                    if (null != m_OutDialog) {
                        m_OutDialog.cancel();
                    }
                    if (m_LoadingDialog == null) {
                        m_LoadingDialog = new LoadingDialog(MenuActivity.this, "Entrega exitosa", "Recoge tu producto");
                    } else {
                        m_LoadingDialog.setLoadText("Entrega exitosa");
                        m_LoadingDialog.setTitle("Recoge tu producto");
                    }
                    m_LoadingDialog.setShowTime(3);
                    m_LoadingDialog.show();
                    TcnVendIF.getInstance().unregisterListener(m_vendListener);
//                    TcnUtilityUI.getToast(MenuActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.SET_SLOTNO_SINGLE:
                    showMessage(String.format("SET_SLOTNO_SINGLE %s -- %s -- %s",cEventInfo.m_lParam4,cEventInfo.m_lParam3,cEventInfo.m_lParam2));
                    break;
                case TcnVendEventID.SET_SLOTNO_DOUBLE:
                    showMessage(String.format("SET_SLOTNO_DOUBLE %s -- %s -- %s",cEventInfo.m_lParam4,cEventInfo.m_lParam3,cEventInfo.m_lParam2));
                    break;
                case TcnVendEventID.SET_SLOTNO_ALL_SINGLE:
                    showMessage(String.format("SET_SLOTNO_ALL_SINGLE SUCCESS %s -- %s -- %s",cEventInfo.m_lParam4,cEventInfo.m_lParam3,cEventInfo.m_lParam2));
                    break;
            }

        }
    }

//    protected void restoreAppData(){
//        final RoomBackup roomBackup = new RoomBackup(MainActivityJava.this);
//        roomBackup.database(FruitDatabase.Companion.getInstance(getApplicationContext()));
//        roomBackup.enableLogDebug(enableLog);
//        roomBackup.backupIsEncrypted(encryptBackup);
//        roomBackup.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_INTERNAL);
//        roomBackup.maxFileCount(5);
//        roomBackup.onCompleteListener((success, message, exitCode) -> {
//            Log.d(TAG, "success: " + success + ", message: " + message + ", exitCode: " + exitCode);
//            if (success) roomBackup.restartApp(new Intent(getApplicationContext(), MainActivityJava.class));
//        });
//        roomBackup.backup();    }
}
