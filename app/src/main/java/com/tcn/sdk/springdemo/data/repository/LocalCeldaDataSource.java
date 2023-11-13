package com.tcn.sdk.springdemo.data.repository;

import android.util.Log;

import com.tcn.sdk.springdemo.data.dao.CeldaDao;
import com.tcn.sdk.springdemo.data.dao.ShipmentDao;
import com.tcn.sdk.springdemo.data.dao.UserDao;
import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.Shipment;
import com.tcn.sdk.springdemo.data.models.User;
import com.tcn.sdk.springdemo.domain.repository.ApiInterface;
import com.tcn.sdk.springdemo.domain.repository.CeldaDataSource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LocalCeldaDataSource implements CeldaDataSource {
    private final CeldaDao mCeldaDao;
    private final UserDao mUserDao;
    private final ShipmentDao mShipmentDao;

    private final ApiInterface mRestService;

    public LocalCeldaDataSource(CeldaDao celdaDao,UserDao userDao,
    ApiInterface restService,ShipmentDao shipmentDao) {
        mCeldaDao = celdaDao;
        mUserDao = userDao;
        mRestService = restService;
        mShipmentDao = shipmentDao;
    }

    @Override
    public Call<RequestItemResponse> requestActivo(RequestItem r){
       return mRestService.requestActivo(r);
    }

    @Override
    public Flowable<List<Celda>> observeCeldas() {
        return mCeldaDao.observeCeldas();
    }

    @Override
    public void insertCelda(Celda celdas) {
        mCeldaDao.insert(celdas);
    }

    @Override
    public Celda mergeCelda(Celda celda) {

        mCeldaDao.mergeCelda(true,true,celda.mSlotNumber);
        mCeldaDao.mergeCelda(true,false,celda.mSlotNumber + 1);
        celda.mIsMerged = true;
        celda.mCanMerged = false;
        return celda;
    }
    @Override
    public Celda splitCelda(Celda celda) {
        mCeldaDao.mergeCelda(false,true,celda.mSlotNumber);
        mCeldaDao.mergeCelda(false,true,celda.mSlotNumber + 1);
        celda.mIsMerged = false;
        return celda;
    }

    @Override
    public Flowable<User> observeUser() {
        return  mUserDao.observeUser();
    }

    @Override
    public void insertUser(User user) {
        mUserDao.insert(user);
    }

    @Override
    public void insertShipment(Shipment shipment) {
        mShipmentDao.insert(shipment);
    }

    @Override
    public void updateShipmentState(int estado, String id) {
        mShipmentDao.updateShipmentState(estado,id);
    }


}
