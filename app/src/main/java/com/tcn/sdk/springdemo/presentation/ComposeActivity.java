package com.tcn.sdk.springdemo.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.tcn.sdk.springdemo.Injection;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.presentation.adapter.CeldaAdapter;
import com.tcn.sdk.springdemo.presentation.adapter.MainSliderAdapter;
import com.tcn.sdk.springdemo.presentation.adapter.PicassoImageLoadingService;
import com.tcn.sdk.springdemo.presentation.adapter.RecyclerItemClickListener;
import com.tcn.sdk.springdemo.data.dto.Marcacion;
import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.ShipmentState;
import com.tcn.sdk.springdemo.data.models.User;
import com.tcn.sdk.springdemo.domain.repository.AppDataSource;
import com.tcn.springboard.control.PayMethod;
import com.tcn.springboard.control.TcnVendEventID;
import com.tcn.springboard.control.TcnVendIF;
import com.tcn.springboard.control.VendEventInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
//    RecyclerView recyclerView1;
    CeldaAdapter adapter;
    AppDataSource dataSource;
    User currrentUser = null;
    AlertDialog loadingDialog;
    AlertDialog messageDialog;
    AlertDialog passworDialog;
    Button mButton;
    Button hideButton;
    EditText input;
    private Slider slider;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
            new Thread(()->{
                TcnVendIF.getInstance().reqShip(1, shipMethod, "1", UUID.randomUUID().toString());
            }).start();
            new Thread(()->{
                TcnVendIF.getInstance().reqShip(3, shipMethod, "1", UUID.randomUUID().toString());
            }).start();

//            wSocker.start();
//            makeToast(hubConnection.getConnectionState().name());
        });
        AsyncTask.execute(()->dataSource.getActivos());
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        observeCeldas();
        createWebSocketClient();
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


    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("ws://172.20.20.76:8000/v1/ws/suscribe/user/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        //                webSocketClient.send("Hello, World!");
        WebSocketClient webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.d("DEBUG_APP_WS", "onOpen");
//                webSocketClient.send("Hello, World!");
            }

            @Override
            public void onTextReceived(String message) {
                Gson gson = new Gson();
                Marcacion obj = gson.fromJson(message, Marcacion.class);
                Log.d("DEBUG_APP_WS", "onTextReceived" + obj.name);
                currrentUser = new User(obj.id,obj.name);
//                currrentUser = new User("jorge","123123123");
                runOnUiThread(() -> {
                    mButton.setText(obj.name);
                    // Stuff that updates the UI

                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                Log.d("DEBUG_APP_WS", "onTextReceived" + Arrays.toString(data));
            }

            @Override
            public void onPingReceived(byte[] data) {
                Log.d("DEBUG_APP_WS", "onTextReceived" + Arrays.toString(data));
            }

            @Override
            public void onPongReceived(byte[] data) {
                Log.d("DEBUG_APP_WS", "onPongReceived" + Arrays.toString(data));
            }

            @Override
            public void onException(Exception e) {
                Log.d("DEBUG_APP_WS", e.getLocalizedMessage());
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived(int reason, String description) {
                System.out.println("onCloseReceived");
                Log.d("DEBUG_APP_WS", description);

            }

        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
//        webSocketClient.addHeader("Origin", "http://developer.example.com");
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }


    protected void observeCeldas() {
        mDisposable.add(dataSource.observeCeldas()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(celdas -> {

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

    public boolean isSessionEnabled() {
        if (currrentUser == null) return false;

        long now = System.currentTimeMillis();
        long now2 = currrentUser.mCreatedAt;
        long diff = now - now2;
        Log.d("DEBUG_APP_SESSION", String.format("%d --- %d = %d", now, now2, TimeUnit.MILLISECONDS.toSeconds(diff)));
        return TimeUnit.MILLISECONDS.toSeconds(diff) < 120;
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

        Slider.init(new PicassoImageLoadingService(this));
        slider = findViewById(R.id.banner_slider1);
        slider.setAdapter(new MainSliderAdapter());
        slider.setInterval(5000);
        slider.setLoopSlides(true);
        slider.setAnimateIndicators(true);

    }

    private void setDialogPassword() {
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
    }

    private void setRecyclerView() {
        recyclerView1
                = findViewById(
                R.id.row_1);
        recyclerView2
                = findViewById(
                R.id.row_2);


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
                if (loadingDialog.isShowing()) return;
                loadingDialog.show();

                        if(isSessionEnabled()) {
                            RequestItem r = new RequestItem(String.valueOf(position+1),"12122");

                            Call<RequestItemResponse> call = dataSource.requestActivo(r);
                            call.enqueue(new Callback<RequestItemResponse>() {
                                @Override
                                public void onResponse(Call<RequestItemResponse> call, Response<RequestItemResponse> response) {
                                    loadingDialog.show();
                                    Log.d("DEBUG_APP_API",response.message());
                                    Log.d("DEBUG_APP_API",String.format("%s", response.isSuccessful()));
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        if (response.body().result != null) {
                                            if (response.body().result.disponible) {
                                                Toast.makeText(ComposeActivity.this, String.format("Ship item %d", position), Toast.LENGTH_SHORT).show();
                                                int slotNo = position + 1;//出货的货道号 dispensing slot number
                                                String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
                                                String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
                                                String tradeNo = UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
                                                TcnVendIF.getInstance().ship(slotNo, shipMethod, amount, tradeNo);
                                            } else {
                                                Snackbar.make(view, "El usuario --- no tiene asignado este activo",
                                                        Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    }else{
                                        Snackbar.make(view, "Ocurrio un error inesperado",
                                                Snackbar.LENGTH_LONG).show();
                                    }
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<RequestItemResponse> call, Throwable t) {
                                    loadingDialog.dismiss();
                                    Log.d("DEBUG_APP_API",t.getLocalizedMessage());
                                    Snackbar.make(view, "Ocurrio un error inesperado",
                                            Snackbar.LENGTH_LONG).show();
                                    call.cancel();
                                }
                            });


                            Log.d("DEBUG_APP", "ITEMCLICKED");

                        }else {
                            loadingDialog.dismiss();
                            Snackbar.make(view,"Por favor, antes de solicitar un activo, realice una marcación con su credencial",
                                    Snackbar.LENGTH_LONG).show();
                        }
//
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Log.d("DEBUG_APP", "ITEMLOBGCLICKED");
                Intent mainIntent = new Intent(ComposeActivity.this, MainAct.class);
                ComposeActivity.this.startActivity(mainIntent);
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
                    makeToast(String.format("Command_SHIPMENT FAILURE %s",cEventInfo.m_lParam4));
                    AsyncTask.execute(()-> {
                                dataSource.updateShipmentState(ShipmentState.FAILURE.ordinal(), cEventInfo.m_lParam4);
                            });
                    loadingDialog.dismiss();
                    messageDialog.show();
//                    TcnVendIF.getInstance().closeTrade(true);
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
                case TcnVendEventID.COMMAND_SHIPMENT_FAULT:
                    makeToast(String.format("Command_SHIPMENT FAULT %s",cEventInfo.m_lParam4));
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.FAULT.ordinal(),cEventInfo.m_lParam4);
                    });
                    loadingDialog.dismiss();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPPING:
                    makeToast(String.format("Command_SHIPPING %s -- %s -- %s",cEventInfo.m_lParam4,cEventInfo.m_lParam3,cEventInfo.m_lParam2));
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SHIPPING.ordinal(),cEventInfo.m_lParam4);
                    });
                    loadingDialog.setMessage("Dispensando activo...");

                    Log.d("DEBUG_APP_SHIPPING",cEventInfo.m_lParam4);
//                    AsyncTask.execute(()-> {
//                        Shipment shipment = new Shipment() ;
//                    });
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                    makeToast(String.format("Command_SHIPMENT SUCCESS %s -- %s -- %s",cEventInfo.m_lParam4,cEventInfo.m_lParam3,cEventInfo.m_lParam2));
                    AsyncTask.execute(()->{
                        dataSource.updateShipmentState(ShipmentState.SUCCESS.ordinal(),cEventInfo.m_lParam4);
                    });
                    loadingDialog.dismiss();
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
            }

        }
    }

    @Override // com.tcn.app_common.view.TcnMainActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return super.dispatchTouchEvent(motionEvent);

    }

        @Override
    public void onBackPressed(){
        Toast.makeText(getApplicationContext(),"You Are Not Allowed to Exit the App", Toast.LENGTH_SHORT).show();
    }
}
