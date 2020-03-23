package com.effseele.effilearn.Results;

public class LoginResult {
    private String status,id;
    private User UserDetails;

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return UserDetails;
    }

    public LoginResult(String id, String status, User UserDetails) {
        this.id = id;
        this.status = status;
        this.UserDetails = UserDetails;


    }
}
