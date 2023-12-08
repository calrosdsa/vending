package com.tcn.vending.springdemo.domain.repository;

import com.tcn.vending.springdemo.data.dto.ActivoDto;
import com.tcn.vending.springdemo.data.dto.RequestDispensar;
import com.tcn.vending.springdemo.data.dto.RequestItem;
import com.tcn.vending.springdemo.data.dto.RequestItemResponse;
import com.tcn.vending.springdemo.data.models.Activo;
import com.tcn.vending.springdemo.data.models.Celda;
import com.tcn.vending.springdemo.data.models.Shipment;
import com.tcn.vending.springdemo.data.models.User;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Call;

public interface AppDataSource {
    //celdas
    Flowable<List<Celda>> observeCeldas();
    void insertCelda(Celda celdas);
    void insertAllCeldas(List<Celda> celdas);
    Celda mergeCelda(Celda celda);
    Celda splitCelda(Celda celda);

    //Activos
    Flowable<List<Activo>> observeActivos();

    //user
    Flowable<User> observeUser();
    void insertUser(User user);

    void insertActivos(List<Activo> activos);

    //Shipment
    void insertShipment(Shipment shipment);

    void updateShipmentState(int estado,String id);

    //apicalls
    Call<RequestItemResponse> requestActivo(RequestItem r);
    Call<RequestItemResponse> updateActivo(RequestDispensar r);

    void requestDispensar(String id);


    Call<ActivoDto> getActivos(String codeUser);

    void syncUnverifiedShipments();
}
