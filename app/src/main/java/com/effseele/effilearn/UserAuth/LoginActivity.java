package com.effseele.effilearn.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.effseele.effilearn.Payment.PaymentActivity;
import com.effseele.effilearn.R;
import com.effseele.effilearn.RetroPack.APIService;
import com.effseele.effilearn.Results.LoginResult;
import com.effseele.effilearn.RetroPack.RetrofitFactory;
import com.effseele.effilearn.Utils.HideKeyboard;
import com.effseele.effilearn.Utils.UIValidation;
import com.effseele.effilearn.databinding.ActivityLoginBinding;
import com.effseele.effilearn.session.SessionManager;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    String mobileNo, password;
////////////////////STEP 1////////////////////
//Create a Factory class that’ll give you the same Retrofit object whenever you ask for it.


    ////////////////////STEP 2////////////////////
    /////Define a Pojo class into which JSON data will be parsed automatically by Retrofit.

    /////////////STEP 3//////////////////////////
    //Define an interface having method to login as follows. Note all API calls will be represented by each method.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.passwordET.setTransformationMethod(new PasswordTransformationMethod());
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkpink));
        }


        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(LoginActivity.this);
        session = new SessionManager(getApplicationContext());
        mobileNo = session.getMobileNo().get(SessionManager.KEY_MOBILENO);
        binding.mobile.setText(mobileNo);
        binding.passwordET.addTextChangedListener(new TextWatc(binding.passwordET));

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard.hideKeyboard(LoginActivity.this);
                checkLogin();
            }
        });


    }

    private void checkLogin() {
        if (!validatePassword()) {
            return;
        } else {
            login();
        }
    }

    private void login() {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        /////Step 4) Call this method when you want to login.//////
        Retrofit retrofit = RetrofitFactory.getRetrofit();
        APIService service = retrofit.create(APIService.class);
        service.loginUser(mobileNo, password);
        Call<LoginResult> call = service.loginUser(mobileNo, password);



        ///Step 5) Define callback methods to be called when API sends response.

        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                progressDialog.dismiss();

                LoginResult loginResult = response.body();
                if (loginResult.getId().equalsIgnoreCase("1") &&
                        loginResult.getStatus().equalsIgnoreCase("Success"))
                {
                    if (loginResult.getUser().getIsActive().equalsIgnoreCase("1"))
                    {
                        session.setMobileNo(mobileNo);
                        session.setPaymentAmount( loginResult.getUser().getTotalAmount());
                        session.setLoginSession(loginResult.getUser().getUserId(),
                                loginResult.getUser().getName(),
                                loginResult.getUser().getEmailId(),
                                loginResult.getUser().getIsPayment(),
                                loginResult.getUser().getIsActive());
                        if (loginResult.getUser().getIsPayment().equalsIgnoreCase("0"))
                        {
                            Intent intent = new Intent(LoginActivity.this, PaymentActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in,
                                    R.anim.trans_left_out);

                        } else
                            {
                            Intent intent = new Intent(LoginActivity.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in,
                                    R.anim.trans_left_out);

                        }


                    } else {
                        openDialog("deactivated");
                    }
                }

                else if (loginResult.getId().equalsIgnoreCase("2")
                        && loginResult.getStatus().equalsIgnoreCase("Incorrect Password."))
                            {

                                openDialog("Incorrect Password.");
                            }




            }


            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("errorrrmessageeee", t.getMessage());

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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
                case R.id.passwordET:
                    validatePassword();
                    break;
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    private boolean validatePassword() {
        password = binding.passwordET.getText().toString();
        String passwordNoValidateMSG = UIValidation.nameValidate(password, true, "Password is required");
        if (!passwordNoValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            binding.passWrapper.setError(passwordNoValidateMSG);
            binding.passWrapper.requestFocus();
            return false;
        } else {
            binding.passWrapper.setErrorEnabled(false);
        }
        return true;
    }

    private void openDialog(final String message) {
        final Dialog dialog = new Dialog(LoginActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Button ok = dialog.findViewById(R.id.okTV);
        TextView msgTV = dialog.findViewById(R.id.msgTV);
        ImageView crossIV = dialog.findViewById(R.id.crossIV);
        final ImageView imageIV = dialog.findViewById(R.id.imageIV);

        msgTV.setText(message);
        if (message.equalsIgnoreCase("deactivated")) {
            imageIV.setImageResource(R.drawable.warning);
            msgTV.setText("Your account is deactivated. Kindly contact the customer care.");
        } else if (message.equalsIgnoreCase("Incorrect Password.")) {
            imageIV.setImageResource(R.drawable.warning);
            msgTV.setText("Your password is incorrect");

        }


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.equalsIgnoreCase("deactivated")) {
                    dialog.dismiss();
                } else if (message.equalsIgnoreCase("Incorrect Password.")) {
                    dialog.dismiss();

                }

            }
        });
        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });
    }


}
