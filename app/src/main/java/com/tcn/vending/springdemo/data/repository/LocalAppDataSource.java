package com.tcn.vending.springdemo.data.repository;

import android.os.AsyncTask;
import android.util.Log;

import com.tcn.vending.springdemo.data.dao.ActivoDao;
import com.tcn.vending.springdemo.data.dao.CeldaDao;
import com.tcn.vending.springdemo.data.dao.ShipmentDao;
import com.tcn.vending.springdemo.data.dao.UserDao;
import com.tcn.vending.springdemo.data.dto.ActivoDto;
import com.tcn.vending.springdemo.data.dto.DispensarResponse;
import com.tcn.vending.springdemo.data.dto.RequestActivos;
import com.tcn.vending.springdemo.data.dto.RequestConfirm;
import com.tcn.vending.springdemo.data.dto.RequestDispensar;
import com.tcn.vending.springdemo.data.dto.RequestItem;
import com.tcn.vending.springdemo.data.dto.RequestItemResponse;
import com.tcn.vending.springdemo.data.dto.RequestRollback;
import com.tcn.vending.springdemo.data.models.Activo;
import com.tcn.vending.springdemo.data.models.Celda;
import com.tcn.vending.springdemo.data.models.Shipment;
import com.tcn.vending.springdemo.data.models.ShipmentState;
import com.tcn.vending.springdemo.data.models.User;
import com.tcn.vending.springdemo.domain.repository.ApiInterface;
import com.tcn.vending.springdemo.domain.repository.AppDataSource;
import com.tcn.vending.springdemo.domain.util.FileLogger;

import java.io.IOException;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalAppDataSource implements AppDataSource {
//    private final CeldaDao mCeldaDao;
//    private final ShipmentDao mShipmentDao;
    private final ApiInterface mRestService;

    public LocalAppDataSource( ApiInterface restService) {
//        mCeldaDao = celdaDao;
        mRestService = restService;
//        mShipmentDao = shipmentDao;

    }

    //API
    @Override
    public Call<RequestItemResponse> requestActivo(RequestItem r){
       return mRestService.requestActivo(r);
    }
    @Override
    public Call<RequestItemResponse> rollback(RequestRollback r)  {
        return mRestService.rollback(r);
    }

    @Override
    public Call<RequestItemResponse> dispensarSuccess(RequestConfirm r) {
        return mRestService.dispensarSuccess(r);
    }


//    @Override
//    public Call<ActivoDto> getActivos(String codeUser) {
//        RequestActivos request = new RequestActivos(codeUser);
//        return mRestService.getActivos(request);
//    }

//    @Override
//    public void syncUnverifiedShipments() {
//        List<Shipment> shipments = mShipmentDao.getUnverifiedSuccessfullShipments(ShipmentState.SUCCESS.ordinal());
//        for(int i = 0;i<shipments.size();i++) {
//            requestDispensar(shipments.get(i).mId);
//        }
//    }


    //CELDAS
//    @Override
//    public Flowable<List<Celda>> observeCeldas() {
//        return mCeldaDao.observeCeldas();
//    }
//
//    @Override
//    public void insertAllCeldas(List<Celda> celdas) {
//        mCeldaDao.insertAll(celdas);
//    }
//
//
//    @Override
//    public Celda mergeCelda(Celda celda) {
//
//        mCeldaDao.mergeCelda(true,true,celda.mSlotNumber);
//        mCeldaDao.mergeCelda(true,false,celda.mSlotNumber + 1);
//        celda.mIsMerged = true;
//        celda.mCanMerged = false;
//        return celda;
//    }
//    @Override
//    public Celda splitCelda(Celda celda) {
//        mCeldaDao.mergeCelda(false,true,celda.mSlotNumber);
//        mCeldaDao.mergeCelda(false,true,celda.mSlotNumber + 1);
//        celda.mIsMerged = false;
//        return celda;
//    }



//    @Override
//    public void insertShipment(Shipment shipment) {
//        mShipmentDao.insert(shipment);
//    }

//    @Override
//    public void updateShipmentState(int estado, String id) {
//        mShipmentDao.updateShipmentState(estado,id);
//    }


}
