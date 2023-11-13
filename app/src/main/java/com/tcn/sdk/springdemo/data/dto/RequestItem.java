package com.tcn.sdk.springdemo.data.dto;

public class RequestItem {
    public String idCelda;
    public String idUsuario;

    public RequestItem(String mIdCelda,String mIdUsario) {
        this.idCelda = mIdCelda;
        this.idUsuario = mIdUsario;
    }
}
