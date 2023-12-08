package com.tcn.vending.springdemo.data;


import com.tcn.vending.springdemo.domain.repository.ApiInterface;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
//    public static final String hostUrlTest = "http://10.22.4.41:91";
    public static final String hostUrlTest = "http://172.20.20.76:92";

    public static ApiInterface getRestService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(hostUrlTest)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(ApiInterface.class);
    }

}