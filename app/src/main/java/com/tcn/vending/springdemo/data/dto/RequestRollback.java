package com.tcn.vending.springdemo.data.dto;

public class RequestRollback {
    public int codeUsuario;
    public String keyTicket;
    public String keyActivoAntiguo;
    public String keyActivoNuevo;

    public RequestRollback(int arg1,String arg2,String arg3,String arg4) {
        this.codeUsuario = arg1;
        this.keyTicket = arg2;
        this.keyActivoAntiguo = arg3;
        this.keyActivoNuevo = arg4;
    }
}
