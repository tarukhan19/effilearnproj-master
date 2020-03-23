package com.effseele.effilearn.UserAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.effseele.effilearn.R;
import com.effseele.effilearn.Utils.HideKeyboard;
import com.effseele.effilearn.databinding.ActivityOtpBinding;
import com.effseele.effilearn.session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    private String verificationId;
    private FirebaseAuth mAuth;
    ActivityOtpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);

        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkpink));
        }

        progressDialog = new ProgressDialog(OtpActivity.this);
        queue = Volley.newRequestQueue(OtpActivity.this);
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();


        binding.verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HideKeyboard.hideKeyboard(OtpActivity.this);

                otpValidation();

                            }
        });
        binding.dontrecieveotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.otp.setText("");
                sendVerificationCode("+91"+sessionManager.getMobileNo().get(SessionManager.KEY_MOBILENO));
            }
        });

        sendVerificationCode("+91"+sessionManager.getMobileNo().get(SessionManager.KEY_MOBILENO));

    }

    private void otpValidation()
    {
        String otp = binding.otp.getText().toString().trim();
        HideKeyboard.hideKeyboard(OtpActivity.this);
        if (otp.isEmpty())
        {
            binding.otpWrapper.setError("Please enter valid OTP");
            binding.otp.requestFocus();
        }
        else
        {
            verifyCode(otp);
        }
    }

    private void sendVerificationCode(String number) {
        Log.e("number", number);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                120,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
//            Log.e("code", code);
            Toast.makeText(OtpActivity.this, "OTP sent on given number", Toast.LENGTH_SHORT).show();
            if (code != null)
            {

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this,"Invalid mobile number", Toast.LENGTH_LONG).show();
            Log.e("e.getMessage()", e + "");
        }
    };


    private void verifyCode(String code)
    {
        progressDialog.setMessage("OTP Verifying ...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        }
        catch (Exception e)
        {

        }

    }


    private void signInWithCredential(PhoneAuthCredential credential)
    {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();

                                Intent in = new Intent(OtpActivity.this, RegistrationActivity.class);


                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(in);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);

                        } else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(OtpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



}
