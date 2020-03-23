package com.effseele.effilearn.UserAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.effseele.effilearn.Payment.PaymentActivity;
import com.effseele.effilearn.R;
import com.effseele.effilearn.RoomDatabase.DatabaseClient;
import com.effseele.effilearn.TestEmptyActivity;
import com.effseele.effilearn.session.SessionManager;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    VideoView videoView;
    SessionManager session;
    Button skipBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.videoView);
        skipBTN=findViewById(R.id.skipBTN);
        session = new SessionManager(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkpink));
        }

        DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.startscreen);
        videoView.setVideoURI(uri);
        videoView.start();


        skipBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (session.isLoggedIn()) {
                    if (session.getLoginSession().get(SessionManager.KEY_ISPAYMENT).equalsIgnoreCase("0"))
                    {
                        Intent intent=new Intent(MainActivity.this, PaymentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);

                    }
                    else
                    {
                        Intent intent=new Intent(MainActivity.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);

                    }
                } else

                    startActivity(new Intent(MainActivity.this, CheckMobileNoActivity.class));
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                finish();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {


                if (session.isLoggedIn()) {
                    if (session.getLoginSession().get(SessionManager.KEY_ISPAYMENT).equalsIgnoreCase("0"))
                    {
                        Intent intent=new Intent(MainActivity.this, PaymentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);

                    }
                    else
                    {
                        Intent intent=new Intent(MainActivity.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);

                    }
                } else

                    startActivity(new Intent(MainActivity.this, CheckMobileNoActivity.class));
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                finish();
            }
        });

      //  deleteTask();


    }

//    private void deleteTask()
//    {
//        class DeleteTask extends AsyncTask<Void, Void, Void> {
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
//                        .taskDao()
//                        .deleteAll();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//
//            }
//        }
//
//        DeleteTask dt = new DeleteTask();
//        dt.execute();
//
//    }
}
