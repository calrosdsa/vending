package com.tcn.sdk.springdemo.data.dto;

public class RequestDispensar {
    String idCelda;
    String idUsuario;
    String codeUsuario;
    String idActivo;
    String keyActivo;
    String objectType;
    public RequestDispensar(String mIdCelda,String mCodeUsuario,String mIdActivo,String mKeyActivo,String mObjectType) {
        this.idCelda = mIdCelda;
        this.idUsuario = "0";
        this.codeUsuario = mCodeUsuario;
        this.idActivo = mIdActivo;
        this.keyActivo = mKeyActivo;
        this.objectType = mObjectType;
    }
}
