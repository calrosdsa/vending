package com.tcn.sdk.springdemo.presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.domain.util.FileLogger;
import com.tcn.sdk.springdemo.presentation.adapter.LogsAdapter;
import com.tcn.sdk.springdemo.presentation.adapter.RecyclerItemClickListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class LogActivity extends  TcnMainActivity {
    RecyclerView logsRecycler;
    LogsAdapter logsAdapter;
    FileLogger logger;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);


        initView();
        logger = new FileLogger();
        FileLogger.init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logsAdapter = new LogsAdapter(logger.getFiles());
        logsRecycler.setAdapter(logsAdapter);
        FileLogger.logError("Activivty main","error!!!");

//        text.setText(logger.getLogs(logger.get));
    }

    protected void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        text =  findViewById(R.id.log_text);

        setRecyclerView();
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
    protected void setRecyclerView(){
        logsRecycler
                = findViewById(
                R.id.log_files);

        RecyclerView.LayoutManager RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        logsRecycler.setLayoutManager(
                RecyclerViewLayoutManager);
        logsRecycler.addOnItemTouchListener(clickItem(logsRecycler));
    }

    RecyclerItemClickListener clickItem(RecyclerView currentRecycler) {
        return  new RecyclerItemClickListener(this, currentRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File file = logger.getFiles().get(position);
                Log.d("DEBUG_APP_CLICK",file.getName());
                Log.d("DEBUG_APP_CLICK",logger.getLogs(file.getName()).toString());

                text.setText(logger.getLogs(file.getName()));
            }

            @Override
            public void onLongItemClick(View view, int position) {
                //TODO()
            }
        });
    }
}
