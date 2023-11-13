package com.tcn.sdk.springdemo.domain.repository;

import static com.tcn.sdk.springdemo.data.APIClient.hostUrl;
import static com.tcn.sdk.springdemo.data.APIClient.hostUrlTest;

import com.tcn.sdk.springdemo.data.APIClient;
import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.Activo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/api/GenetecApi/validar/ValidarPeticion")
    Call<RequestItemResponse> requestActivo(@Body RequestItem requestItem);

    @GET("activos/")
    Call<List<Activo>> getActivos();


}
