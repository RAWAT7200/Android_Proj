package com.paril.mlaclientapp.model;

/**
 * Created by paril on 7/16/2017.
 */
public class MLAUserWithCheckbox {
    String userId;
    String userName;
    String emailId;
    Boolean check;


    public String getUserFLname() {
        return userName;
    }


    public String getEmail() {
        return emailId;
    }

    public void setEmail(String email) {
        this.emailId = email;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MLAUserWithCheckbox(String userName, String emailId, Boolean check, String userId) {

        this.userId = userId;
        this.userName = userName;
        this.emailId = emailId;
        this.check = check;
    }
}
