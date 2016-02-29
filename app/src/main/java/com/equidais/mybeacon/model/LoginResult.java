package com.equidais.mybeacon.model;

public class LoginResult {
    public int UserID;
    public boolean IsSuccess;
    public String Message;

    public LoginResult(){
        UserID = 0;
        IsSuccess = false;
        Message = "";
    }
}
