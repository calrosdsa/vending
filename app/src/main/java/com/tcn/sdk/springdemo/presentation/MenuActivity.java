package com.tcn.sdk.springdemo.presentation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tcn.sdk.springdemo.Injection;
import com.tcn.sdk.springdemo.R;
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
    int currentRow = -1;
    Celda mCelda;
    List<Celda> mCeldas;
    TextView mPosition;
    AppPreferences preferences;
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
        TcnVendIF.getInstance().registerListener(m_vendListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TcnVendIF.getInstance().unregisterListener(m_vendListener);
    }

    protected void  initView(){
        setRecyclerView();

        mPosition = findViewById(R.id.position);

        splitButton = findViewById(R.id.splitButton);
        mergeButton = findViewById(R.id.mergeButton);
        splitAllB = findViewById(R.id.splitAllB);
        Button testSlotB = findViewById(R.id.testSlot);
        testSlotB.setOnClickListener(view -> {
            int slotNo = currentPosition;//出货的货道号 dispensing slot number
            String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
            String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
            String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
            TcnVendIF.getInstance().ship(slotNo, shipMethod, amount, tradeNo);
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

        loadingDialog = new SpotsDialog.Builder().setContext(MenuActivity.this)
                .build();

        messageDialog = new AlertDialog.Builder(MenuActivity.this)
                .setTitle("Ocurrio un error")
                .setMessage("No se pudo dispensar el activo porfavor comuniquese con el proveedor")
                .setPositiveButton("OK", null)
//                .setNegativeButton("Cancel", null)
                .create();
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
            list.add(new Celda(60, 60, 2,false));
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
                Log.d("DEBUG_APP", "ITEMLOBGCLICKED");
                // do whatever
            }
        });
    }

    protected void selectCurrentSlot(int position,int row){
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
        currentPosition = position;
        currentRow = row;
        mPosition.setText(String.format("%s",mCelda.mSlotNumber));
    }

    protected void mergeCelda() {
        Log.d("DEBUG_APP_C",String.format("%s --- %s",mCelda.mSlotNumber,mCelda.mCanMerged));
        if(mCeldas.size() > currentPosition){
        Celda nextSlot = mCeldas.get(currentPosition+1);
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
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Map<Integer, List<Celda>> groupCeldas =
                                celdas.stream().collect(Collectors.groupingBy(w -> w.mRowNumber));

                        for (Map.Entry<Integer, List<Celda>> entry : groupCeldas.entrySet()) {
                            switch (entry.getKey()) {
                                case 1:
                                    adapter = new CeldaAdapter(entry.getValue());
                                    recyclerView1.setAdapter(adapter);
                                case 2:
                                    adapter = new CeldaAdapter(entry.getValue());
                                    recyclerView2.setAdapter(adapter);
                                case 3:
                                    adapter = new CeldaAdapter(entry.getValue());
                                    recyclerView3.setAdapter(adapter);
                                case 4:
                                    adapter = new CeldaAdapter(entry.getValue());
                                    recyclerView4.setAdapter(adapter);
                                case 5:
                                    adapter = new CeldaAdapter(entry.getValue());
                                    recyclerView5.setAdapter(adapter);
                                case 6:
                                    adapter = new CeldaAdapter(entry.getValue());
                                    recyclerView6.setAdapter(adapter);
                            }
//                        if(entry.getKey() == 1){
//                            adapter = new CeldaAdapter(entry.getValue());
//                            recyclerView1.setAdapter(adapter);
//                        }
                        }

                    }

                }, thorwable -> Log.d("DEBUG_APP_ERR", "DASDAS", thorwable)));
    }


    private MenuActivity.VendListener m_vendListener = new VendListener();
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
                    showMessage(String.format("Command_SHIPMENT FAILURE %s",cEventInfo.m_lParam4));
                    AsyncTask.execute(()-> {
                        dataSource.updateShipmentState(ShipmentState.FAILURE.ordinal(), cEventInfo.m_lParam4);
                    });
                    loadingDialog.dismiss();
                    messageDialog.show();
//                    TcnVendIF.getInstance().closeTrade(true);
//                    TcnUtilityUI.getToast(MenuActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
                case TcnVendEventID.COMMAND_SHIPMENT_FAULT:
                    showMessage(String.format("Command_SHIPMENT FAULT %s",cEventInfo.m_lParam4));
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.FAULT.ordinal(),cEventInfo.m_lParam4);
                    });
                    loadingDialog.dismiss();
//                    TcnUtilityUI.getToast(MenuActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPPING:
                    showMessage(String.format("Command_SHIPPING %s -- %s -- %s",cEventInfo.m_lParam4,cEventInfo.m_lParam3,cEventInfo.m_lParam2));
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SHIPPING.ordinal(),cEventInfo.m_lParam4);
                    });
                    loadingDialog.setMessage("Dispensando activo...");

//                    Log.d("DEBUG_APP_",String.format("%s - %s - %s - %s",cEventInfo.m_lParam5.toString(),cEventInfo.m_lParam3,cEventInfo.m_lParam2,cEventInfo.m_lParam1));
//                    AsyncTask.execute(()-> {
//                        Shipment shipment = new Shipment() ;
//                    });
//                    TcnUtilityUI.getToast(MenuActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                    showMessage(String.format("Command_SHIPMENT SUCCESS %s -- %s -- %s",cEventInfo.m_lParam4,cEventInfo.m_lParam3,cEventInfo.m_lParam2));
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SUCCESS.ordinal(),cEventInfo.m_lParam4);
                    });
                    loadingDialog.dismiss();
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
}
