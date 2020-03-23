package com.effseele.effilearn.Results;
//The Model would hold the user’s mobileno and password. The following User.java class does it:
public class User {
    private String UserId, Name,MobileNo,EmailId,Address,District,City,Pincode,State,IsActive,IsPayment,TotalAmount;

    public User(String userId, String name, String mobileNo, String emailId, String address, String district, String city, String pincode, String state, String isActive, String isPayment, String totalAmount) {
        UserId = userId;
        Name = name;
        MobileNo = mobileNo;
        EmailId = emailId;
        Address = address;
        District = district;
        City = city;
        Pincode = pincode;
        State = state;
        IsActive = isActive;
        IsPayment = isPayment;
        TotalAmount = totalAmount;
    }

    public String getUserId() {
        return UserId;
    }

    public String getName() {
        return Name;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public String getEmailId() {
        return EmailId;
    }

    public String getAddress() {
        return Address;
    }

    public String getDistrict() {
        return District;
    }

    public String getCity() {
        return City;
    }

    public String getPincode() {
        return Pincode;
    }

    public String getState() {
        return State;
    }

    public String getIsActive() {
        return IsActive;
    }

    public String getIsPayment() {
        return IsPayment;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }
}
