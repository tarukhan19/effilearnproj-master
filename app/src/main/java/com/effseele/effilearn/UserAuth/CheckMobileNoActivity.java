package com.effseele.effilearn.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.effseele.effilearn.R;
import com.effseele.effilearn.Results.CheckMobileNoResult;
import com.effseele.effilearn.RetroPack.APIService;
import com.effseele.effilearn.RetroPack.RetrofitFactory;
import com.effseele.effilearn.Utils.EndPoints;
import com.effseele.effilearn.Utils.HideKeyboard;
import com.effseele.effilearn.Utils.UIValidation;
import com.effseele.effilearn.databinding.ActivityCheckMobileNoBinding;
import com.effseele.effilearn.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CheckMobileNoActivity extends AppCompatActivity {
ActivityCheckMobileNoBinding binding;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    String mobileNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_mobile_no);

        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkpink));
        }

        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(CheckMobileNoActivity.this);
        session = new SessionManager(getApplicationContext());
        binding.mobileET.addTextChangedListener(new TextWatc(binding.mobileET));

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard.hideKeyboard(CheckMobileNoActivity.this);
                registration();
            }
        });


    }

    private void registration()
    {
        if (!validateMobile()) {
            return;
        }
        else
        {
            checkMobileNo();
        }
    }

    private void checkMobileNo()
    {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = RetrofitFactory.getRetrofit();
        APIService service = retrofit.create(APIService.class);
        service.checkMobileNo(mobileNo);
        Call<CheckMobileNoResult> call = service.checkMobileNo(mobileNo);

        call.enqueue(new Callback<CheckMobileNoResult>() {
            @Override
            public void onResponse(Call<CheckMobileNoResult> call, Response<CheckMobileNoResult> response) {
                progressDialog.dismiss();

                CheckMobileNoResult checkMobileNoResult = response.body();
                if (checkMobileNoResult.getId().equalsIgnoreCase("0")
                        && checkMobileNoResult.getStatus().equalsIgnoreCase("This mobile no. does not exists."))
                {
                    Intent intent = new Intent(CheckMobileNoActivity.this, OtpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                    session.setMobileNo(mobileNo);
                }
                else if (checkMobileNoResult.getId().equalsIgnoreCase("1")
                        && checkMobileNoResult.getStatus().equalsIgnoreCase("This mobile no. is already registered.")) {
                    Intent intent = new Intent(CheckMobileNoActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                    session.setMobileNo(mobileNo);
                }

            }

            @Override
            public void onFailure(Call<CheckMobileNoResult> call, Throwable t) {
                progressDialog.dismiss();

            }
        });

    }

    private class TextWatc implements TextWatcher {
        private View view;

        public TextWatc(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {


                case R.id.mobileET:

                    validateMobile();

                    break;


            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    }

    private boolean validateMobile()
    {

        mobileNo = binding.mobileET.getText().toString();
        String mobileNoValidateMSG = UIValidation.mobileValidate(mobileNo, true);
        if (!mobileNoValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            binding.mobileWrapper.setError(mobileNoValidateMSG);
            binding.mobileET.requestFocus();
            return false;
        } else {
            binding.mobileWrapper.setErrorEnabled(false);
        }
        return true;
    }

}
