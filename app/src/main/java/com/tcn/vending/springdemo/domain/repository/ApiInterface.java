package com.tcn.vending.springdemo.domain.repository;

import com.tcn.vending.springdemo.data.dto.ActivoDto;
import com.tcn.vending.springdemo.data.dto.DispensarResponse;
import com.tcn.vending.springdemo.data.dto.RequestActivos;
import com.tcn.vending.springdemo.data.dto.RequestConfirm;
import com.tcn.vending.springdemo.data.dto.RequestDispensar;
import com.tcn.vending.springdemo.data.dto.RequestItem;
import com.tcn.vending.springdemo.data.dto.RequestItemResponse;
import com.tcn.vending.springdemo.data.dto.RequestRollback;
import com.tcn.vending.springdemo.data.models.Activo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {
    //use
    @POST("/validar/Dispensar")
    Call<RequestItemResponse> requestActivo(@Body RequestItem requestItem);
    @POST("/dispensar/Rollback")
    Call<RequestItemResponse> rollback(@Body RequestRollback r);

    @POST("/dispensar/Success")
    Call<RequestItemResponse> dispensarSuccess(@Body RequestConfirm r);
//    @POST("/dispensar/DispensarActivo")
//    Call<DispensarResponse> requestDispensar(@Body RequestDispensar requestDispensar);

//    @POST
//    Call<RequestItemResponse> updateDispensar(@Body RequestDispensar requestDispensar);

    @POST("/validar/ObtenerArray")
    Call<ActivoDto> getActivos(@Body RequestActivos requestActivos);


}
