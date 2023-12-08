package com.tcn.vending.springdemo.data.dto;

import java.util.List;

public class RequestItemResponse {
    public boolean IsSuccess;
    public String DisplayMessage;
    public List<String> ErrorMessages;
    public Result Result;
}
