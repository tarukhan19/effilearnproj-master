package com.effseele.effilearn;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.effseele.effilearn.Results.CallScheduleResult;
import com.effseele.effilearn.RetroPack.APIService;
import com.effseele.effilearn.RetroPack.RetrofitFactory;
import com.effseele.effilearn.UserAuth.HomePage;
import com.effseele.effilearn.databinding.ActivityCallScheduleBinding;
import com.effseele.effilearn.session.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

 public class CallScheduleActivity extends AppCompatActivity {
ActivityCallScheduleBinding binding;
    int mYear, mMonth, mDay;
    String date="",time="",name,mobileno;
    RecyclerView timerv;
    Dialog timeDialog;
    AlertDialog.Builder timebuilder;
    View timeView;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    ArrayList<String> timeDTOArrayList;

    TimeAdpater timeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_schedule);
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(CallScheduleActivity.this);
        session = new SessionManager(getApplicationContext());
        timeDTOArrayList = new ArrayList<>();
        timeAdapter = new TimeAdpater(this, timeDTOArrayList);

        timeDTOArrayList.add("10:00 AM to 01:00 PM");
        timeDTOArrayList.add("01:00 PM to 04:00 PM");
        timeDTOArrayList.add("04:00 PM to 07:00 PM");


        timebuilder = new AlertDialog.Builder(this);
        timeView = LayoutInflater.from(this).inflate(R.layout.item_rv, null);
        timebuilder.setView(timeView);
        timeDialog = timebuilder.create();
        timerv = timeView.findViewById(R.id.rv);
        timerv.setLayoutManager(new LinearLayoutManager(this));
        timerv.setHasFixedSize(true);
        timerv.setAdapter(timeAdapter);

        binding.timeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timebuilder.setView(timerv);
                timeDialog.show();
            }
        });
        binding.dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDobDialog();
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        Toolbar toolbar = findViewById(R.id.toolbarmain);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        ImageView backIMG =  toolbar.findViewById(R.id.backIMG);
        mTitle.setText("Call Schedule");
        backIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CallScheduleActivity.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });
        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=binding.nameET.getText().toString();
                mobileno=binding.mobileET.getText().toString();

                if (name.isEmpty())
                {
                    openDialog("Enter your name");
                }
                else if (mobileno.isEmpty())
                {
                    openDialog("Enter your mobile no");
                }
                else if (date.isEmpty())
                {
                    openDialog("Select date");
                }
                else if (time.isEmpty())
                {
                    openDialog("Select time");
                }

                else
                {
                    submit();
                }
            }
        });
        //UserId=&Name=&Email=&MobileNo=&ScheduleDate=&ScheduleTime=

    }

     @Override
     public void onBackPressed() {
         super.onBackPressed();
         Intent intent=new Intent(CallScheduleActivity.this, HomePage.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(intent);
         overridePendingTransition(R.anim.trans_left_in,
                 R.anim.trans_left_out);
     }

     private void submit()
    {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        /////Step 4) Call this method when you want to login.//////
        Retrofit retrofit = RetrofitFactory.getRetrofit();
        APIService service = retrofit.create(APIService.class);
        service.submitCallSchedule(session.getLoginSession().get(SessionManager.KEY_USERID),session.getLoginSession().get(SessionManager.KEY_EMAILID),name, mobileno,date, time);
        Call<CallScheduleResult> call = service.submitCallSchedule(session.getLoginSession().get(SessionManager.KEY_USERID),session.getLoginSession().get(SessionManager.KEY_EMAILID),
                name, mobileno,date, time);

        call.enqueue(new Callback<CallScheduleResult>() {
            @Override
            public void onResponse(Call<CallScheduleResult> call, Response<CallScheduleResult> response)
            {
                progressDialog.dismiss();

                CallScheduleResult callScheduleResult = response.body();
                if (callScheduleResult.getId().equalsIgnoreCase("1") &&
                        callScheduleResult.getStatus().equalsIgnoreCase("Success"))
                {

                 openDialog("Your call has been scheduled. We will get back to you soon.");


                }



            }


            @Override
            public void onFailure(Call<CallScheduleResult> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("errorrrmessageeee", t.getMessage());

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openDialog(final String from) {
        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        Button ok = dialog.findViewById(R.id.okTV);
        TextView msg = dialog.findViewById(R.id.msgTV);
        ImageView image = dialog.findViewById(R.id.imageIV);

       if (from.equalsIgnoreCase("Your call has been scheduled. We will get back to you soon."))
        {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.success );
            msg.setText("Your call has been scheduled. We will get back to you soon.");
        }
       else
       {
           image.setVisibility(View.VISIBLE);
           image.setImageResource(R.drawable.warning);
           msg.setText(from);
       }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (from.equalsIgnoreCase("Your call has been scheduled. We will get back to you soon."))
                {
                    dialog.dismiss();
                    Intent in7 = new Intent(CallScheduleActivity.this, HomePage.class);
                    in7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(in7);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }

                else
                {
                    dialog.dismiss();
                }


            }
        });
    }
    private void openDobDialog()
    {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, selectedyear);
                myCalendar.set(Calendar.MONTH, selectedmonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                String myFormat = "yyyy-MM-dd"; //Change as you need
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

                date = sdf.format(myCalendar.getTime());
                try {
                    Date dateObj = sdf.parse(date);

                    Log.e("dateObj",dateObj+"");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                binding.dateTV.setText(sdf.format(myCalendar.getTime()));

                mDay = selectedday;
                mMonth = selectedmonth;
                mYear = selectedyear;
            }
        }, mYear, mMonth, mDay);
        mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());

        mDatePicker.show();
    }
    private class TimeAdpater extends RecyclerView.Adapter<TimeAdpater.CustomViewHodler> {
        private Context mContext;
        ArrayList<String> timeDTOArrayList;

        public TimeAdpater(Context mContext, ArrayList<String> timeDTOArrayList) {
            this.mContext = mContext;
            this.timeDTOArrayList = timeDTOArrayList;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            holder.text.setText(timeDTOArrayList.get(position));
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    binding.timeTV.setText(timeDTOArrayList.get(position));
                    time = timeDTOArrayList.get(position);

                    timeDialog.dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return timeDTOArrayList == null ? 0 : timeDTOArrayList.size();
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
}
