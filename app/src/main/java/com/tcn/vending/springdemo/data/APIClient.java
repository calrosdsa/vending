package com.tcn.vending.springdemo.data;


import com.tcn.vending.springdemo.domain.repository.ApiInterface;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    public static final String hostUrlTest = "http://10.0.3.82:91"; //PRODUCTION IP

//    public static final String hostUrlTest = "http://172.20.20.57:91";


    public static ApiInterface getRestService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(hostUrlTest)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(ApiInterface.class);
    }

}