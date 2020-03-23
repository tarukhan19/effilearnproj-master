package com.effseele.effilearn.Results;

public class PaymentResult {
    String id,status;

    public PaymentResult(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
