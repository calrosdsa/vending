package com.tcn.sdk.springdemo.data.dto;

public class RequestDispensar {
    String idCelda;
    String idUsuario;
    String idActivo;
    String keyActivo;
    String objectType;
    public RequestDispensar(String mIdCelda,String mIdUsario,String mIdActivo,String mKeyActivo,String mObjectType) {
        this.idCelda = mIdCelda;
        this.idUsuario = mIdUsario;
        this.idActivo = mIdActivo;
        this.keyActivo = mKeyActivo;
        this.objectType = mObjectType;
    }
}
