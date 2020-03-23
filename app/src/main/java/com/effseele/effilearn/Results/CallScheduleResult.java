package com.effseele.effilearn.Results;

public class CallScheduleResult {
String id,status;

    public CallScheduleResult(String id, String status) {
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
