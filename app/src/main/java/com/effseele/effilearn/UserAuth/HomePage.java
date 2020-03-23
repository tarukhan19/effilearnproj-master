package com.effseele.effilearn.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.effseele.effilearn.CallScheduleActivity;
import com.effseele.effilearn.ModelTestActivity;
import com.effseele.effilearn.R;
import com.effseele.effilearn.databinding.ActivityHomePageBinding;

public class HomePage extends AppCompatActivity {
ActivityHomePageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);

        binding.callscheduleCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomePage.this, CallScheduleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);

            }
        });

        binding.mocktestCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomePage.this, ModelTestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);

            }
        });


    }
}
