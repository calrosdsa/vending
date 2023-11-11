package com.tcn.sdk.springdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tcn.sdk.springdemo.adapter.CeldaAdapter;
import com.tcn.sdk.springdemo.adapter.CustomAdapter;
import com.tcn.sdk.springdemo.adapter.RecyclerItemClickListener;
import com.tcn.sdk.springdemo.data.DataModel;
import com.tcn.springboard.control.PayMethod;
import com.tcn.springboard.control.TcnVendEventID;
import com.tcn.springboard.control.TcnVendEventResultID;
import com.tcn.springboard.control.TcnVendIF;
import com.tcn.springboard.control.VendEventInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ComposeActivity extends TcnMainActivity{
    // Recycler View object
    RecyclerView recyclerView;

    // Array list for recycler view data source
    ArrayList<String> source;

    // Layout Manager
    RecyclerView.LayoutManager RecyclerViewLayoutManager;

    // adapter class object
    CeldaAdapter adapter;

    // Linear Layout Manager
    LinearLayoutManager HorizontalLayout;

    View ChildView;
    int RecyclerViewItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialisation with id's
        recyclerView
                = (RecyclerView)findViewById(
                R.id.recyclerview);
        RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getApplicationContext());

        // Set LayoutManager on Recycler View
        recyclerView.setLayoutManager(
                RecyclerViewLayoutManager);

        AddItemsToRecyclerViewArrayList();
        adapter = new CeldaAdapter(source);




            HorizontalLayout
                = new LinearLayoutManager(
                ComposeActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(HorizontalLayout);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
//                        TcnVendIF.getInstance().startWorkThread();
                        Toast.makeText(ComposeActivity.this,String.format("Ship item %d",position),Toast.LENGTH_SHORT).show();
                        int slotNo = position + 1;//出货的货道号 dispensing slot number
                        String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
                        String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
                        String tradeNo =  UUID.randomUUID().toString();//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
                        TcnVendIF.getInstance().ship(slotNo,shipMethod,amount,tradeNo);
                        Log.d("DEBUG_APP","ITEMCLICKED");
                        // do whatever
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Log.d("DEBUG_APP","ITEMLOBGCLICKED");
                        Intent mainIntent = new Intent(ComposeActivity.this,MainAct.class);
                        ComposeActivity.this.startActivity(mainIntent);
                        // do whatever
                    }
                })
        );

        // Set adapter on recycler view
        recyclerView.setAdapter(adapter);
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
    // Function to add items in RecyclerView.
    public void AddItemsToRecyclerViewArrayList()
    {
        // Adding items to ArrayList
        source = new ArrayList<>();
        source.add("1");
        source.add("2");
        source.add("3");
        source.add("4");
        source.add("5");
        source.add("1");
        source.add("1");
        source.add("1");
        source.add("1");
        source.add("1");



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
//                    TcnVendIF.getInstance().closeTrade(true);
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
                case TcnVendEventID.COMMAND_SHIPMENT_FAULT:
                    makeToast(String.format("Command_SHIPMENT FAULT %s",cEventInfo.m_lParam4));
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPPING:
                    makeToast(String.format("Command_SHIPMENTNG %s",cEventInfo.m_lParam4));
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;

                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:
                    makeToast(String.format("Command_SHIPMENT SUCCESS %s",cEventInfo.m_lParam4));
//                    TcnUtilityUI.getToast(ComposeActivity.this, cEventInfo.m_lParam4, 20).show();
                    break;
            }

        }
    }
}
