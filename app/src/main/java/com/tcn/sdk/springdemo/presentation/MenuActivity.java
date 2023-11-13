package com.tcn.sdk.springdemo.presentation;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tcn.sdk.springdemo.Injection;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.adapter.CeldaAdapter;
import com.tcn.sdk.springdemo.adapter.RecyclerItemClickListener;
import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.ShipmentState;
import com.tcn.sdk.springdemo.domain.repository.CeldaDataSource;
import com.tcn.springboard.control.PayMethod;
import com.tcn.springboard.control.TcnVendEventID;
import com.tcn.springboard.control.TcnVendIF;
import com.tcn.springboard.control.VendEventInfo;

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
    CeldaAdapter adapter;
    CeldaDataSource dataSource;
    Button mergeButton;
    Button splitButton;
    Button splitAllB;
    AlertDialog loadingDialog;
    AlertDialog messageDialog;
    int currentPosition = -1;
    Celda mCelda;
    List<Celda> mCeldas;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_menu);
        dataSource = Injection.provideUserDataSource(this);
        populateDb();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        observeCeldas();
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
            for (int i = 0; i < 4; i++) {
                Celda celda = new Celda(i, i, 1,true);
                dataSource.insertCelda(celda);
            }
            Celda celda1 = new Celda(4, 4, 1,false);
            dataSource.insertCelda(celda1);
            for (int i = 5; i < 9; i++) {
                Celda celda = new Celda(i, i, 2,true);
                dataSource.insertCelda(celda);
            }
        });
    }


    private void setRecyclerView() {
        recyclerView1
                = (RecyclerView) findViewById(
                R.id.recyclerview);
        recyclerView2
                = (RecyclerView) findViewById(
                R.id.recyclerview2);


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

        // Set LayoutManager on Recycler View
        recyclerView1.setLayoutManager(
                RecyclerViewLayoutManager);

        recyclerView2.setLayoutManager(
                RecyclerViewLayoutManager2);
        recyclerView1.addOnItemTouchListener(clickItem(recyclerView1));
        recyclerView2.addOnItemTouchListener(clickItem(recyclerView2));
    }

    RecyclerItemClickListener clickItem(RecyclerView recycler) {
        return  new RecyclerItemClickListener(this, recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectCurrentSlot(position);

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

    protected void selectCurrentSlot(int position){
        if(currentPosition != -1){
        View oldView =  recyclerView1.getLayoutManager().findViewByPosition(currentPosition);
        oldView.setBackground(null);
        }
        View view =  recyclerView1.getLayoutManager().findViewByPosition(position);
        Drawable res = MenuActivity.this.getResources().getDrawable(R.drawable.selectedcard);
        view.setBackground(res);
        mCelda = mCeldas.get(position);
        currentPosition = position;
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
