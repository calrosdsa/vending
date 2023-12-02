package com.tcn.sdk.springdemo.domain.repository;

import static com.tcn.sdk.springdemo.data.APIClient.hostUrlTest;

import com.tcn.sdk.springdemo.data.APIClient;
import com.tcn.sdk.springdemo.data.dto.DispensarResponse;
import com.tcn.sdk.springdemo.data.dto.RequestDispensar;
import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.Activo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {
    @POST
    Call<RequestItemResponse> requestActivo(@Url String url,@Body RequestItem requestItem);
    @POST
    Call<DispensarResponse> requestDispensar(@Url String url, @Body RequestDispensar requestDispensar);

    @POST
    Call<RequestItemResponse> updateDispensar(@Url String url, @Body RequestDispensar requestDispensar);

    @GET("activos/")
    Call<List<Activo>> getActivos();


}
