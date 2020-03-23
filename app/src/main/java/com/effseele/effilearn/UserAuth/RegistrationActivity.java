package com.effseele.effilearn.UserAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.effseele.effilearn.Model.StateDistrictDTO;
import com.effseele.effilearn.Payment.PaymentActivity;
import com.effseele.effilearn.R;
import com.effseele.effilearn.Results.LoginResult;
import com.effseele.effilearn.Results.RegistrationResult;
import com.effseele.effilearn.RetroPack.APIService;
import com.effseele.effilearn.RetroPack.RetrofitFactory;
import com.effseele.effilearn.Utils.EndPoints;
import com.effseele.effilearn.Utils.FileUtils;
import com.effseele.effilearn.Utils.HideKeyboard;
import com.effseele.effilearn.Utils.ImageLoadingUtils;
import com.effseele.effilearn.Utils.UIValidation;
import com.effseele.effilearn.Utils.VolleyMultipartRequest;
import com.effseele.effilearn.databinding.ActivityRegistrationBinding;
import com.effseele.effilearn.session.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

public class RegistrationActivity extends AppCompatActivity {
    ActivityRegistrationBinding binding;
    ProgressDialog progressDialog;
    RequestQueue queue;
    String regId, message;
    SessionManager session;
    Bitmap scaledBitmap;
    Uri picUri;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    ImageLoadingUtils utils;
    String mobileNo, email, fullName, state = "", address, district = "", password, confpassword, city, pincode, gender, from,
            adharPicturePath,panPicturePath;

    RecyclerView staterv, districtrv;
    Dialog stateDialog, districtDialog;
    AlertDialog.Builder statebuilder, districtbuilder;
    View stateView, districtView;

    ArrayList<StateDistrictDTO> stateDTOArrayList, districtDTOArrayList;

    StateAdapter stateAdapter;
    DistrictAdapter districtAdapter;

    Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkpink));
        }

        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        session = new SessionManager(this);
        utils = new ImageLoadingUtils(this);

        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/nunito-sans.extralight.ttf");
        binding.passwordET.setTypeface(font);
        binding.confirmPasswordET.setTypeface(font);
        binding.maleRB.setTypeface(font);
        binding.femaleRB.setTypeface(font);
        mobileNo = session.getMobileNo().get(SessionManager.KEY_MOBILENO);
        binding.mobileET.setText(session.getMobileNo().get(SessionManager.KEY_MOBILENO));

        binding.passwordET.setTransformationMethod(new PasswordTransformationMethod());
        binding.confirmPasswordET.setTransformationMethod(new PasswordTransformationMethod());


        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        binding.nameET.addTextChangedListener(new TextWatc(binding.nameET));
        binding.emailET.addTextChangedListener(new TextWatc(binding.emailET));
        binding.addressET.addTextChangedListener(new TextWatc(binding.addressET));
        binding.passwordET.addTextChangedListener(new TextWatc(binding.passwordET));
        binding.confirmPasswordET.addTextChangedListener(new TextWatc(binding.confirmPasswordET));
        binding.cityET.addTextChangedListener(new TextWatc(binding.cityET));
        binding.pincodeET.addTextChangedListener(new TextWatc(binding.pincodeET));

        stateDTOArrayList = new ArrayList<>();
        districtDTOArrayList = new ArrayList<>();

        stateAdapter = new StateAdapter(this, stateDTOArrayList);
        districtAdapter = new DistrictAdapter(this, districtDTOArrayList);

        statebuilder = new AlertDialog.Builder(this);
        stateView = LayoutInflater.from(this).inflate(R.layout.item_rv, null);
        statebuilder.setView(stateView);
        stateDialog = statebuilder.create();
        staterv = (RecyclerView) stateView.findViewById(R.id.rv);
        staterv.setLayoutManager(new LinearLayoutManager(this));
        staterv.setHasFixedSize(true);
        staterv.setAdapter(stateAdapter);

        binding.stateLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statebuilder.setView(staterv);
                stateDialog.show();
            }
        });


        districtbuilder = new AlertDialog.Builder(this);
        districtView = LayoutInflater.from(this).inflate(R.layout.item_rv, null);
        districtbuilder.setView(districtView);
        districtDialog = districtbuilder.create();
        districtrv = (RecyclerView) districtView.findViewById(R.id.rv);
        districtrv.setLayoutManager(new LinearLayoutManager(this));
        districtrv.setHasFixedSize(true);
        districtrv.setAdapter(districtAdapter);

        binding.districtLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state.isEmpty()) {
                    binding.stateWrapper.setError("Select state first.");
                } else {
                    districtbuilder.setView(districtrv);
                    districtDialog.show();
                }

            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        binding.pancardcompanyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                from = "pancard";
                selectImage(RegistrationActivity.this);

            }
        });
        binding.adharcardcompanyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                from = "adharcard";
                selectImage(RegistrationActivity.this);


            }
        });


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(RegistrationActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                regId = instanceIdResult.getToken();
                Log.e("Token", regId);
            }
        });

        readJsonFile();
    }


    private String readJsonFile() {

        String json = null;
        try {
            InputStream is = getAssets().open("statesdistricts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray statearray = jsonObject.getJSONArray("states");
            for (int i = 0; i < statearray.length(); i++) {
                StateDistrictDTO stateDistrictDTO = new StateDistrictDTO();
                JSONObject stateobj = statearray.getJSONObject(i);
                String statename = stateobj.getString("state");
                stateDistrictDTO.setStateName(statename);
                stateDTOArrayList.add(stateDistrictDTO);


            }
            stateAdapter.notifyDataSetChanged();

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;

    }


    private String readDistrict() {
        String json = null;
        try {
            InputStream is = getAssets().open("statesdistricts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            districtDTOArrayList.clear();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray statearray = jsonObject.getJSONArray("states");
            for (int i = 0; i < statearray.length(); i++) {
                JSONObject stateobj = statearray.getJSONObject(i);
                String statename = stateobj.getString("state");
                if (state.equalsIgnoreCase(statename)) {
                    JSONArray distarray = stateobj.getJSONArray("districts");
                    for (int j = 0; j < distarray.length(); j++) {
                        StateDistrictDTO stateDistrictDTO = new StateDistrictDTO();
                        String district = distarray.getString(j);
                        stateDistrictDTO.setDistrictName(district);
                        districtDTOArrayList.add(stateDistrictDTO);
                    }
                }

                districtAdapter.notifyDataSetChanged();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;

    }


    public void submit() {

        if (!validateFname()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }

        if (!validateState()) {
            return;
        }
        if (!validateDistrict()) {
            return;
        }
        if (!validateAddress()) {
            return;
        }
        if (!validateAddress()) {
            return;
        }
        if (!validateCity()) {
            return;
        }
        if (!validatePinCode()) {
            return;
        }

        if (!validateConfPassword()) {
            return;
        }
        if (panPicturePath.isEmpty()) {
            binding.pancardTIL.setError("Upload pancard");

            return;
        }
        if (adharPicturePath.isEmpty()) {
            binding.adharcardTIL.setError("Upload adhar card");
            return;
        } else {
            if (binding.maleRB.isChecked()) {
                gender = "M";
            } else {
                gender = "F";
            }
            registration();
        }
    }

    private void registration() {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        RequestBody rbname = RequestBody.create(MediaType.parse("multipart/form-data"), fullName);
        RequestBody rbemail = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody rbmobileNo = RequestBody.create(MediaType.parse("multipart/form-data"), mobileNo);
        RequestBody rbstate = RequestBody.create(MediaType.parse("multipart/form-data"), state);
        RequestBody rbaddress = RequestBody.create(MediaType.parse("multipart/form-data"), address);
        RequestBody rbdistrict = RequestBody.create(MediaType.parse("multipart/form-data"), district);
        RequestBody rbpassword = RequestBody.create(MediaType.parse("multipart/form-data"), password);
        RequestBody rbcity = RequestBody.create(MediaType.parse("multipart/form-data"), city);
        RequestBody rbpincode = RequestBody.create(MediaType.parse("multipart/form-data"), pincode);
        RequestBody rbgender = RequestBody.create(MediaType.parse("multipart/form-data"), gender);
        RequestBody rbdeviceid = RequestBody.create(MediaType.parse("multipart/form-data"), regId);
        RequestBody rbdevicetype = RequestBody.create(MediaType.parse("multipart/form-data"), "android");

        File adharFile=new File(adharPicturePath);
        RequestBody adharrequestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        adharFile
                );
        MultipartBody.Part adharcardbody =
                MultipartBody.Part.createFormData("AadharCard", adharFile.getName(), adharrequestFile);
        Log.e("adharcardbody",adharFile.getName()+"  >>>>"+adharrequestFile );

        File panFile=new File(panPicturePath);
        RequestBody panrequestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        panFile
                );
        MultipartBody.Part pancardbody =
                MultipartBody.Part.createFormData("PanCard", panFile.getName(), panrequestFile);
        Log.e("adharcardbody",panFile.getName()+"  >>>>"+adharrequestFile );



        Retrofit retrofit = RetrofitFactory.getRetrofit();
        APIService service = retrofit.create(APIService.class);

        Call<RegistrationResult> call = service.registrationUser(rbname, rbemail, rbmobileNo, rbaddress, rbdistrict, rbstate, rbpincode
                , rbpassword, rbcity, rbgender, rbdevicetype, rbdeviceid, adharcardbody, pancardbody);

        call.enqueue(new Callback<RegistrationResult>() {
            @Override
            public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                Log.e("response.isSuccessful()",response.isSuccessful()+"");
                progressDialog.dismiss();
                RegistrationResult  registrationResult = response.body();
                Log.e("registrationResult",registrationResult.getTotalAmount());
                if (registrationResult.getId().equalsIgnoreCase("2")
                        && registrationResult.getStatus().equalsIgnoreCase("Registered successfully"))
                {

                    session.setPaymentAmount(registrationResult.getTotalAmount());
                    session.setLoginSession(registrationResult.getUserId(),
                            fullName,
                            email,
                            "0",
                            "");
                    openDialog("Registered successfully");
                } else if (registrationResult.getId().equalsIgnoreCase("0")
                        && registrationResult.getStatus().equalsIgnoreCase("Mobile no. already registered.")) {
                    openDialog("Mobile no. already registered.");
                } else if (registrationResult.getId().equalsIgnoreCase("1")
                        && registrationResult.getStatus().equalsIgnoreCase("Email Id already registered.")) {
                    openDialog("Email Id already registered.");
                }
            }

            @Override
            public void onFailure(Call<RegistrationResult> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("registrationerrort",t.toString());
            }
        });


    }


    private void openDialog(final String message) {
        final Dialog dialog = new Dialog(RegistrationActivity.this, R.style.CustomDialog);
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
        if (message.equalsIgnoreCase("Mobile no. already registered.")) {
            imageIV.setImageResource(R.drawable.sorry);
            msgTV.setText("This mobile no. is already registered.");
        } else if (message.equalsIgnoreCase("Email Id already registered.")) {
            imageIV.setImageResource(R.drawable.sorry);
            msgTV.setText("This Email-Id is already registered.");

        } else if (message.equalsIgnoreCase("Registered successfully")) {

            imageIV.setImageResource(R.drawable.congrates);
            msgTV. setText("Your registeration is successful. Please do payment of "+session.getPaymentAmount().get(SessionManager.KEY_PAYMENTAMOUNT));

        }


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.equalsIgnoreCase("Mobile no. already registered.")) {
                    dialog.dismiss();
                } else if (message.equalsIgnoreCase("Email Id already registered.")) {
                    dialog.dismiss();

                } else if (message.equalsIgnoreCase("Registered successfully")) {
                    dialog.dismiss();
                    Intent in = new Intent(RegistrationActivity.this, PaymentActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(in);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);

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


    private boolean validateDistrict() {
        district = binding.districtTV.getText().toString();
        String districtValidateMSG = UIValidation.nameValidate(district,
                true, "District is required");
        if (!districtValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            binding.districtWrapper.setError(districtValidateMSG);
            return false;
        } else {
            binding.districtWrapper.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress() {
        address = binding.addressET.getText().toString();
        String addressValidateMSG = UIValidation.nameValidate(address,
                true, "Address is required");
        if (!addressValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            binding.addressWrapper.setError(addressValidateMSG);
            return false;
        } else {
            binding.addressWrapper.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateCity() {
        city = binding.cityET.getText().toString();
        String cityValidateMSG = UIValidation.nameValidate(city,
                true, "City is required");
        if (!cityValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            binding.cityWrapper.setError(cityValidateMSG);
            return false;
        } else {
            binding.cityWrapper.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePinCode() {
        pincode = binding.pincodeET.getText().toString();
        String pincodeValidateMSG = UIValidation.nameValidate(pincode,
                true, "Pincode is required");
        if (!pincodeValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            binding.pincodeWrapper.setError(pincodeValidateMSG);
            return false;
        } else {
            binding.pincodeWrapper.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateState() {
        state = binding.stateTV.getText().toString();
        String stateValidateMSG = UIValidation.nameValidate(state,
                true, "State is required");
        if (!stateValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {

            binding.stateWrapper.setError(stateValidateMSG);
            return false;
        } else {
            binding.stateWrapper.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validateEmail() {
        email = binding.emailET.getText().toString();
        String emailValidateMSG = UIValidation.emailValidate(email, true);
        if (!emailValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            binding.emailWrapper.setError(emailValidateMSG);
            binding.emailET.requestFocus();
            return false;
        } else {
            binding.emailWrapper.setErrorEnabled(false);

        }
        return true;
    }


    private boolean validateFname() {
        fullName = binding.nameET.getText().toString();
        String fnameValidateMSG = UIValidation.nameValidate(fullName, true, "Name is required");
        if (!fnameValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {

            binding.nameWrapper.setError(fnameValidateMSG);
            binding.nameET.requestFocus();
            return false;
        } else {
            binding.nameWrapper.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        password = binding.passwordET.getText().toString();
        String passValidateMSG = UIValidation.passwordValidate(password, true, "pass");
        if (!passValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {

            binding.passwordWrapper.setError(passValidateMSG);
            binding.passwordET.requestFocus();
            return false;
        } else {
            binding.passwordWrapper.setErrorEnabled(false);

        }
        return true;
    }

    private boolean validateConfPassword() {
        confpassword = binding.confirmPasswordET.getText().toString();
        password = binding.passwordET.getText().toString();
        String conpassValidateMSG = UIValidation.confirmPasswordValidate(confpassword, password,
                true, "New Password does not match");
        if (!conpassValidateMSG.equalsIgnoreCase(UIValidation.SUCCESS)) {
            binding.cPassWrapper.setError(conpassValidateMSG);
            binding.confirmPasswordET.requestFocus();
            return false;
        } else {
            binding.cPassWrapper.setErrorEnabled(false);
        }
        return true;
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
                case R.id.nameET:
                    validateFname();
                    break;
                case R.id.emailET:
                    validateEmail();
                    break;
                case R.id.addressET:
                    validateAddress();
                    break;
                case R.id.passwordET:
                    validatePassword();
                    break;
                case R.id.confirmPasswordET:
                    validateConfPassword();
                    break;

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    }


    private class StateAdapter extends RecyclerView.Adapter<StateAdapter.CustomViewHodler> {
        private Context mContext;
        ArrayList<StateDistrictDTO> stateDistrictDTOArrayList;

        public StateAdapter(Context mContext, ArrayList<StateDistrictDTO> stateDistrictDTOArrayList) {
            this.mContext = mContext;
            this.stateDistrictDTOArrayList = stateDistrictDTOArrayList;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final StateDistrictDTO stateDistrictDTO = stateDistrictDTOArrayList.get(position);
            holder.text.setText(stateDistrictDTO.getStateName());
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.stateTV.setText(stateDistrictDTO.getStateName());
                    state = stateDistrictDTO.getStateName();
                    binding.districtTV.setText("");
                    if (binding.stateWrapper.isErrorEnabled()) {
                        binding.stateWrapper.setErrorEnabled(false);
                    }
                    readDistrict();
                    stateDialog.dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return stateDistrictDTOArrayList == null ? 0 : stateDistrictDTOArrayList.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll = itemView.findViewById(R.id.ll);
            }
        }
    }


    private class DistrictAdapter extends RecyclerView.Adapter<DistrictAdapter.CustomViewHodler> {
        private Context mContext;
        ArrayList<StateDistrictDTO> stateDistrictDTOArrayList;

        public DistrictAdapter(Context mContext, ArrayList<StateDistrictDTO> stateDistrictDTOArrayList) {
            this.mContext = mContext;
            this.stateDistrictDTOArrayList = stateDistrictDTOArrayList;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final StateDistrictDTO stateDistrictDTO = stateDistrictDTOArrayList.get(position);
            holder.text.setText(stateDistrictDTO.getDistrictName());
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.districtTV.setText(stateDistrictDTO.getDistrictName());
                    district = stateDistrictDTO.getDistrictName();
                    districtDialog.dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return stateDistrictDTOArrayList == null ? 0 : stateDistrictDTOArrayList.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll = itemView.findViewById(R.id.ll);
            }
        }
    }


    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                        getImageUri(this,selectedImageBitmap);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedImage = data.getData();
                        getImagePath();
                    }
                    break;
            }
        }
    }
    public void getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        selectedImage=Uri.parse(path);
        getImagePath();
    }

    public void getImagePath()
    {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        if (selectedImage != null)
        {
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor != null)
            {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                if (from.equalsIgnoreCase("adharcard")) {
                    adharPicturePath = cursor.getString(columnIndex);
                    Log.e("picturepath",adharPicturePath);
                    binding.adharcardcompanyIV.setImageBitmap(BitmapFactory.decodeFile(adharPicturePath));

                } else {
                    panPicturePath = cursor.getString(columnIndex);
                    Log.e("picturepath",panPicturePath);
                    binding.pancardcompanyIV.setImageBitmap(BitmapFactory.decodeFile(panPicturePath));

                }
                cursor.close();
            }
        }
    }



    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

}
