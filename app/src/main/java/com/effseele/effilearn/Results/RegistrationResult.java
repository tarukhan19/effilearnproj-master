package com.effseele.effilearn.Results;

public class RegistrationResult {
  private   String id,status,TotalAmount,UserId;

    public RegistrationResult(String id, String status, String TotalAmount,String UserId) {
        this.id = id;
        this.status = status;
        this.TotalAmount = TotalAmount;
        this.UserId=UserId;

    }
    public String getUserId() {
        return UserId;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }
}
