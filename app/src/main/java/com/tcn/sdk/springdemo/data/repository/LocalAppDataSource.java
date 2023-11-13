package com.tcn.sdk.springdemo.data.repository;

import android.os.AsyncTask;
import android.util.Log;

import com.tcn.sdk.springdemo.data.dao.ActivoDao;
import com.tcn.sdk.springdemo.data.dao.CeldaDao;
import com.tcn.sdk.springdemo.data.dao.ShipmentDao;
import com.tcn.sdk.springdemo.data.dao.UserDao;
import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.Activo;
import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.Shipment;
import com.tcn.sdk.springdemo.data.models.User;
import com.tcn.sdk.springdemo.domain.repository.ApiInterface;
import com.tcn.sdk.springdemo.domain.repository.AppDataSource;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalAppDataSource implements AppDataSource {
    private final CeldaDao mCeldaDao;
    private final ActivoDao mActivoDao;
    private final UserDao mUserDao;
    private final ShipmentDao mShipmentDao;
    private final ApiInterface mRestService;

    public LocalAppDataSource(CeldaDao celdaDao, UserDao userDao,
    ApiInterface restService, ShipmentDao shipmentDao,ActivoDao activoDao) {
        mCeldaDao = celdaDao;
        mUserDao = userDao;
        mRestService = restService;
        mShipmentDao = shipmentDao;
        mActivoDao = activoDao;

    }

    //API
    @Override
    public Call<RequestItemResponse> requestActivo(RequestItem r){
       return mRestService.requestActivo(r);
    }

    @Override
    public void getActivos() {
        Call call = mRestService.getActivos();
        call.enqueue(new Callback<List<Activo>>() {
            @Override
            public void onResponse(Call<List<Activo>> call, Response<List<Activo>> response) {
                try{
                if(response.isSuccessful()){
                    List<Activo> activos = response.body();
                    AsyncTask.execute(()-> mActivoDao.insertActivos(activos));
                }else{
                    Log.d("DEBUG_APP_ERR","NO SUCCESS");
                    call.cancel();
                }
                }catch(Exception e){
                    Log.d("DEBUG_APP_ERR",e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<List<Activo>> call, Throwable t) {
                Log.d("DEBUG_APP_API",t.getLocalizedMessage());
                call.cancel();
            }
        });
    }

    //ACTIVOS
    @Override
    public Flowable<List<Activo>> observeActivos() {
        return mActivoDao.observeActivos();
    }

    //CELDAS
    @Override
    public Flowable<List<Celda>> observeCeldas() {
        return mCeldaDao.observeCeldas();
    }

    @Override
    public void insertCelda(Celda celdas) {
        mCeldaDao.insert(celdas);
    }

    @Override
    public void insertAllCeldas(List<Celda> celdas) {
        mCeldaDao.insertAll(celdas);
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
