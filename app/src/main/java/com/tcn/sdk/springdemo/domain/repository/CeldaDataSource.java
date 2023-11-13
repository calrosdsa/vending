package com.tcn.sdk.springdemo.domain.repository;

import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.Shipment;
import com.tcn.sdk.springdemo.data.models.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import retrofit2.Call;

public interface CeldaDataSource {
    //celdas
    Flowable<List<Celda>> observeCeldas();
    void insertCelda(Celda celdas);
    Celda mergeCelda(Celda celda);
    Celda splitCelda(Celda celda);

    //user
    Flowable<User> observeUser();
    void insertUser(User user);

    //Shipment
    void insertShipment(Shipment shipment);

    void updateShipmentState(int estado,String id);

    //apicallas

    Call<RequestItemResponse> requestActivo(RequestItem r);
}
