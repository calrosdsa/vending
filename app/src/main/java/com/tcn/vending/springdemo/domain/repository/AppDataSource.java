package com.tcn.vending.springdemo.domain.repository;

import com.tcn.vending.springdemo.data.dto.ActivoDto;
import com.tcn.vending.springdemo.data.dto.RequestConfirm;
import com.tcn.vending.springdemo.data.dto.RequestDispensar;
import com.tcn.vending.springdemo.data.dto.RequestItem;
import com.tcn.vending.springdemo.data.dto.RequestItemResponse;
import com.tcn.vending.springdemo.data.dto.RequestRollback;
import com.tcn.vending.springdemo.data.models.Activo;
import com.tcn.vending.springdemo.data.models.Celda;
import com.tcn.vending.springdemo.data.models.Shipment;
import com.tcn.vending.springdemo.data.models.User;

import java.io.IOException;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.Response;

public interface AppDataSource {
    //celdas
//    Flowable<List<Celda>> observeCeldas();
//    void insertAllCeldas(List<Celda> celdas);
//    Celda mergeCelda(Celda celda);
//    Celda splitCelda(Celda celda);

//    void updateShipmentState(int estado,String id);
    //apicalls
    Call<RequestItemResponse> requestActivo(RequestItem r);
    Call<RequestItemResponse> rollback(RequestRollback r);
    Call<RequestItemResponse> dispensarSuccess(RequestConfirm r);

//    Call<RequestItemResponse> updateActivo(RequestDispensar r);


//    Call<ActivoDto> getActivos(String codeUser);

//    void syncUnverifiedShipments();
}
