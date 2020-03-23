package com.effseele.effilearn.RetroPack;

import android.net.nsd.NsdManager;

import com.effseele.effilearn.Results.CallScheduleResult;
import com.effseele.effilearn.Results.CheckMobileNoResult;
import com.effseele.effilearn.Results.LoginResult;
import com.effseele.effilearn.Results.ModelTestResult;
import com.effseele.effilearn.Results.PaymentResult;
import com.effseele.effilearn.Results.RegistrationResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {

    @FormUrlEncoded
    @POST("Check_Effi_Learn_MobileNo.ashx?")
    Call<CheckMobileNoResult> checkMobileNo(
            @Field("MobileNo") String mobileNo);

    @FormUrlEncoded
    @POST("Effi_Learn_Login.ashx?")
    Call<LoginResult> loginUser(
            @Field("MobileNo") String mobileNo,
            @Field("Password") String password);


    @Multipart
    @POST("Effi_Learn_Registration.ashx?")
    Call<RegistrationResult> registrationUser(
            @Part("Name") RequestBody name,
            @Part("Email") RequestBody email,
            @Part("MobileNo") RequestBody mobileNo,
            @Part("Address") RequestBody address,
            @Part("District") RequestBody district,
            @Part("State") RequestBody state,
            @Part("Pincode") RequestBody pincode,
            @Part("Password") RequestBody password,
            @Part("City") RequestBody city,
            @Part("Gender") RequestBody gender,
            @Part("DeviceType") RequestBody deviceType,
            @Part("DeviceId") RequestBody deviceId,
            @Part MultipartBody.Part adharcard,
            @Part MultipartBody.Part pancard);

    @FormUrlEncoded
    @POST("Effi_Learn_Expert_Call.ashx?")
    Call<CallScheduleResult> submitCallSchedule(
            @Field("UserId") String UserId,
            @Field("Name") String name,
            @Field("Email") String email,
            @Field("MobileNo") String mobileno,
            @Field("ScheduleDate") String date,
            @Field("ScheduleTime") String time);



    @FormUrlEncoded
    @POST("Effi_Learn_Payment.ashx?")
    Call<PaymentResult> submitPayment(
            @Field("UserId") String UserId,
            @Field("PaymentId") String PaymentId,
            @Field("Utr") String Utr);

    @FormUrlEncoded
    @POST("Effi_Learn_Show_Questions.ashx?")
    Call<ModelTestResult> loadTest(
            @Field("UserId") String UserId,
            @Field("TestId") String TestId);


}
