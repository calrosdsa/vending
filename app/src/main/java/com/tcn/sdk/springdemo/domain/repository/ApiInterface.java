package com.tcn.sdk.springdemo.domain.repository;

import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/api/GenetecApi/validar/ValidarPeticion")
    Call<RequestItemResponse> requestActivo(@Body RequestItem requestItem);
}
