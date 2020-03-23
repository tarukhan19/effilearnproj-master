package com.effseele.effilearn.Payment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.effseele.effilearn.R;
import com.effseele.effilearn.Results.PaymentResult;
import com.effseele.effilearn.RetroPack.APIService;
import com.effseele.effilearn.RetroPack.RetrofitFactory;
import com.effseele.effilearn.UserAuth.HomePage;
import com.effseele.effilearn.Utils.EndPoints;
import com.effseele.effilearn.session.SessionManager;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class PaymentActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    private boolean isDisableExitConfirmation = false;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    double amount = 0.0;
    String paymentid, transactionid, emailid;
    Intent intent;
    String status = "";
    TextView statusTV;
    Button goBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        progressDialog = new ProgressDialog(PaymentActivity.this);
        queue = Volley.newRequestQueue(PaymentActivity.this);
        session = new SessionManager(getApplicationContext());
        intent = getIntent();

        String amounts = session.getPaymentAmount().get(SessionManager.KEY_PAYMENTAMOUNT);
        amount=Double.parseDouble(amounts);
        if (intent.hasExtra("emailid"))
        {
            emailid=intent.getStringExtra("emailid");
        }
        else
        {
            emailid = session.getLoginSession().get(SessionManager.KEY_EMAILID);

        }
        statusTV=findViewById(R.id.statusTV);
        goBtn=findViewById(R.id.goBtn);
        launchPayUMoneyFlow();

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }


    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
        payUmoneyConfig.setPayUmoneyActivityTitle("PayUmoney");
        payUmoneyConfig.disableExitConfirmation(isDisableExitConfirmation);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        String txnId = System.currentTimeMillis() + "";
        String phone = session.getMobileNo().get(SessionManager.KEY_MOBILENO);
        String productName = "effi learn";
        String firstName = session.getLoginSession().get(SessionManager.KEY_NAME) ;


        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(emailid)
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(false)
//                .setKey("QHr0AJ4Q")
//                .setMerchantId("5947136");
                .setKey("5wkRRQhp")
                .setMerchantId("6230818");


        try {
            mPaymentParams = builder.build();
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);
            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, this, R.style.PayumoneyAppTheme,
                    false);

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.e("getLocalizedMessage", e.getLocalizedMessage());
            //payNowButton.setEnabled(true);
        }


    }


    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1
            (final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");
        Log.e("payuparams", params.toString());
        stringBuilder.append("ho1ml6f3uC");
      // stringBuilder.append("tCXo7Y2mJq");


        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);
        return paymentParam;
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == Activity.RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
            Log.e("onActivityResult", requestCode + "   " + resultCode);
            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction

                } else {
                    //Failure Transaction
                }
                String payuResponse = transactionResponse.getPayuResponse();
                String merchantResponse = transactionResponse.getTransactionDetails();

                Log.e("payuresponse",payuResponse);
                Log.e("merchantResponse",merchantResponse
                );


                try {
                    JSONObject jsonObject = new JSONObject(payuResponse);
                    JSONObject result = jsonObject.getJSONObject("result");
                    status = result.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        statusTV.setText("Payment Successfull");

                        paymentid = result.getString("paymentId");
                        transactionid = result.getString("txnid");

                        paymentEntry();

                    }
                    else
                    {
                        TextView statusTV=findViewById(R.id.statusTV);
                        statusTV.setText("FAILED");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (resultModel != null && resultModel.getError() != null) {
                Log.e("TAG", "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.e("TAG", "Both objects are null!");
            }
        }
    }

    private void paymentEntry() {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = RetrofitFactory.getRetrofit();
        APIService service = retrofit.create(APIService.class);
        service.submitPayment(session.getLoginSession().get(SessionManager.KEY_USERID),paymentid,transactionid);
        Call<PaymentResult> call = service.submitPayment(session.getLoginSession().get(SessionManager.KEY_USERID),
                paymentid,transactionid);

        call.enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, retrofit2.Response<PaymentResult> response) {
                progressDialog.dismiss();

                PaymentResult paymentResult = response.body();
                if (paymentResult.getId().equalsIgnoreCase("1")
                        && paymentResult.getStatus().equalsIgnoreCase("Successs"))
                {
                    session.setLoginSession(session.getLoginSession().get(SessionManager.KEY_USERID),
                            session.getLoginSession().get(SessionManager.KEY_NAME)
                            ,session.getLoginSession().get(SessionManager.KEY_EMAILID),"1",
                            session.getLoginSession().get(SessionManager.KEY_ISACTIVE));
                    openDialog("Your payment has been received successfully.");
                }
                else  {
                    openDialog(paymentResult.getStatus());

                }

            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                progressDialog.dismiss();

            }
        });


    }

    private void openDialog(final String message) {
        final Dialog dialog = new Dialog(PaymentActivity.this, R.style.CustomDialog);
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
        if (message.equalsIgnoreCase("Your payment has been received successfully."))
        {
            imageIV.setImageResource(R.drawable.thanku);
            msgTV.setText("Your payment has been received successfully.");
        }


        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                if (message.equalsIgnoreCase("Your payment has been received successfully."))
                {
                    dialog.dismiss();
                    Intent intent=new Intent(PaymentActivity.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }


            }
        });
        crossIV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });
    }
}
