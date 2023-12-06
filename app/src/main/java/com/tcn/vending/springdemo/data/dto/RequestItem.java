package com.tcn.vending.springdemo.data.dto;

public class RequestItem {
    public String idCelda;
    public String codeUsuario;

    public RequestItem(String mIdCelda,String mIdUsario) {
        this.idCelda = mIdCelda;
        this.codeUsuario = mIdUsario;
    }
}
