package com.tcn.sdk.springdemo.data.repository;

import android.os.AsyncTask;
import android.util.Log;

import com.tcn.sdk.springdemo.data.dao.ActivoDao;
import com.tcn.sdk.springdemo.data.dao.CeldaDao;
import com.tcn.sdk.springdemo.data.dao.ShipmentDao;
import com.tcn.sdk.springdemo.data.dao.UserDao;
import com.tcn.sdk.springdemo.data.dto.DispensarResponse;
import com.tcn.sdk.springdemo.data.dto.RequestDispensar;
import com.tcn.sdk.springdemo.data.dto.RequestItem;
import com.tcn.sdk.springdemo.data.dto.RequestItemResponse;
import com.tcn.sdk.springdemo.data.models.Activo;
import com.tcn.sdk.springdemo.data.models.Celda;
import com.tcn.sdk.springdemo.data.models.Shipment;
import com.tcn.sdk.springdemo.data.models.ShipmentState;
import com.tcn.sdk.springdemo.data.models.User;
import com.tcn.sdk.springdemo.domain.repository.ApiInterface;
import com.tcn.sdk.springdemo.domain.repository.AppDataSource;
import com.tcn.sdk.springdemo.domain.util.FileLogger;

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
       return mRestService.requestActivo("http://10.0.3.82:91/validar/ValidarPeticion",r);
    }

    @Override
    public Call<RequestItemResponse> updateActivo(RequestDispensar r){
        return mRestService.updateDispensar ("http://10.22.4.34:92/dispensar/DispensarActivo",r);
    }
    @Override
    public void requestDispensar(String id) {
        Shipment shipment = mShipmentDao.getShipment(id);
        RequestDispensar request = new RequestDispensar(
                shipment.mIdCelda,
                shipment.mCodeUsuario,
                shipment.mIdActivo,
                shipment.mKeyActivo,
                shipment.mObjectType
                );
        Call<DispensarResponse> call = mRestService.requestDispensar("http://10.22.4.34:92/dispensar/DispensarActivo",request);
        call.enqueue(new Callback<DispensarResponse>() {
            @Override
            public void onResponse(Call<DispensarResponse> call, Response<DispensarResponse> response) {
                try{
                    if(response.isSuccessful()){
                        DispensarResponse res = response.body();
                        assert res != null;
                        if(res.isSuccess){
                            mShipmentDao.setSuccessfullShipment(id);
                        }
                    }else{
                        Log.d("DEBUG_APP_ERR","NO SUCCESS");
                        FileLogger.logError("requestDispenser_onResponse","Unsuccessfull response");
                        call.cancel();
                    }
                }catch(Exception e){
                    FileLogger.logError("requestDispenser_onResponse",e.getLocalizedMessage());
                    Log.d("DEBUG_APP_ERR",e.getLocalizedMessage());
                }
            }
            @Override
            public void onFailure(Call<DispensarResponse> call, Throwable t) {
                Log.d("DEBUG_APP_API",t.getLocalizedMessage());
                FileLogger.logError("requestDispenser_onFailure",t.getLocalizedMessage());
                call.cancel();
            }
        });
    }

    @Override
    public void getActivos() {
        Call<List<Activo>> call = mRestService.getActivos();
        call.enqueue(new Callback<List<Activo>>() {
            @Override
            public void onResponse(Call<List<Activo>> call, Response<List<Activo>> response) {
                try{
                if(response.isSuccessful()){
                    List<Activo> activos = response.body();
                    AsyncTask.execute(()-> mActivoDao.insertActivos(activos));
                }else{
                    FileLogger.logError("getActivos_onResponse","Unsuccessfull response");
                    Log.d("DEBUG_APP_ERR","NO SUCCESS");
                    call.cancel();
                }
                }catch(Exception e){
                    FileLogger.logError("getActivos_onResponse",e.getLocalizedMessage());
                    Log.d("DEBUG_APP_ERR",e.getLocalizedMessage());
                }
            }
            @Override
            public void onFailure(Call<List<Activo>> call, Throwable t) {
                FileLogger.logError("getActivos_onFailure",t.getLocalizedMessage());
                Log.d("DEBUG_APP_API",t.getLocalizedMessage());
                call.cancel();
            }
        });
    }

    @Override
    public void syncUnverifiedShipments() {
        List<Shipment> shipments = mShipmentDao.getUnverifiedSuccessfullShipments(ShipmentState.SUCCESS.ordinal());
        for(int i = 0;i<shipments.size();i++) {
            requestDispensar(shipments.get(i).mId);
        }
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
