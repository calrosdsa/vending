package com.tcn.vending.springdemo.data.dto;

public class RequestConfirm {
    public int codeUsuario;
    public String keyTicket;
    public String idCelda;

    public RequestConfirm(int arg1, String arg2,String arg3) {
        this.codeUsuario = arg1;
        this.keyTicket = arg2;
        this.idCelda = arg3;
    }
}
