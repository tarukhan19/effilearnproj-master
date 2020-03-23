package com.effseele.effilearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.effseele.effilearn.databinding.ActivityTestEmptyBinding;

public class TestEmptyActivity extends AppCompatActivity {
ActivityTestEmptyBinding binding;
    int x=0,userinput,output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_empty);


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userinput=Integer.valueOf(binding.entervaue.getText().toString());
                callloop();
            }
        });

    }

    private void callloop()
    {
        for (int i=0;i<userinput;i++)
        {
            int n=i-x;
            Log.e("Time interval",i+"-"+(i+1));
            ///time interval i to i+1

            if ((i == 1) || (n==3))
            {
                output=1;
                x=i;
                Log.e("output= ",output+"");

            }
            else
            {
                output=0;
                Log.e("output= ",output+"");

            }
        }
    }
}
