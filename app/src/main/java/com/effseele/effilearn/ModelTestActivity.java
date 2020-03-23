package com.effseele.effilearn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.res.Resources;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.effseele.effilearn.Adapter.AnswersAdapter;
import com.effseele.effilearn.Results.Answers;
import com.effseele.effilearn.Results.ModelTestResult;
import com.effseele.effilearn.Results.Ques;
import com.effseele.effilearn.RetroPack.APIService;
import com.effseele.effilearn.RetroPack.RetrofitFactory;
import com.effseele.effilearn.RoomDatabase.AppDatabase;
import com.effseele.effilearn.RoomDatabase.DatabaseClient;
import com.effseele.effilearn.databinding.ActivityModelTestBinding;
import com.effseele.effilearn.session.SessionManager;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ModelTestActivity extends AppCompatActivity {
    ActivityModelTestBinding binding;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    ArrayList<Ques> quesArrayList;
    private int questionTracker;
    private double marks=0.0;
    AnswersAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_model_test);
        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkpink));
        }

        initialize();
        loadQuestion();

    }



    private void showQuestion()
    {
        if (questionTracker==0)
        {
            binding.previousBTN.setVisibility(View.INVISIBLE);
            binding.nextBTN.setVisibility(View.VISIBLE);
            binding.submitBTN.setVisibility(View.INVISIBLE);
        }
        else if (questionTracker==quesArrayList.size()-1)
        {
            binding.previousBTN.setVisibility(View.VISIBLE);
            binding.nextBTN.setVisibility(View.GONE);
            binding.submitBTN.setVisibility(View.VISIBLE);

        }
        else
        {
            binding.previousBTN.setVisibility(View.VISIBLE);
            binding.nextBTN.setVisibility(View.VISIBLE);
            binding.submitBTN.setVisibility(View.INVISIBLE);

        }
        binding.tvQuestion.setText(quesArrayList.get(questionTracker).getQuestion());
        adapter = new AnswersAdapter(ModelTestActivity.this, quesArrayList.get(questionTracker).getAnswersArrayList(),
                quesArrayList.get(questionTracker).getMarks(),quesArrayList.get(questionTracker).getQuestionId(),
                binding.nextBTN,binding.previousBTN,binding.submitBTN);
        binding.answerRV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public  void updateNextQuestion()
    {
        if(questionTracker+1 < quesArrayList.size()) {
            questionTracker++;
            showQuestion();
        }
    }
    public void updatePreviousQuestion()
    {
        if(questionTracker-1 < quesArrayList.size()) {
            questionTracker--;
            showQuestion();
        }

    }

    public void addMarks()
    {
        marks=Double.parseDouble(quesArrayList.get(questionTracker).getMarks())+marks;
        Toast.makeText(this, marks+" ", Toast.LENGTH_SHORT).show();
    }
    private void initialize()
    {
        questionTracker = 0;
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(ModelTestActivity.this);
        session = new SessionManager(getApplicationContext());
        quesArrayList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        binding.answerRV.setLayoutManager(mLayoutManager);
        binding.answerRV.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        binding.answerRV.setItemAnimator(new DefaultItemAnimator());
        binding.answerRV.setNestedScrollingEnabled(false);
    }

    private void loadQuestion()
    {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = RetrofitFactory.getRetrofit();
        APIService service = retrofit.create(APIService.class);
        service.loadTest(session.getLoginSession().get(SessionManager.KEY_USERID), "3");
        Call<ModelTestResult> call = service.loadTest(session.getLoginSession().get(SessionManager.KEY_USERID), "1");


        call.enqueue(new Callback<ModelTestResult>() {
            @Override
            public void onResponse(Call<ModelTestResult> call, Response<ModelTestResult> response) {
                progressDialog.dismiss();
                ModelTestResult modelTestResult = response.body();
                if (modelTestResult.getId().equalsIgnoreCase("1") && modelTestResult.getStatus().equalsIgnoreCase("Success"))
                {
                    for (int i = 0; i < modelTestResult.getQuesArrayList().size(); i++) {
                        Ques ques = new Ques();
                        ques.setQuestionId(modelTestResult.getQuesArrayList().get(i).getQuestionId());
                        ques.setQuestion(modelTestResult.getQuesArrayList().get(i).getQuestion());
                        ques.setMarks(modelTestResult.getQuesArrayList().get(i).getMarks());
                        ArrayList<Answers> answersArrayList = new ArrayList<>();

                        for (int j = 0; j < modelTestResult.getQuesArrayList().get(i).getAnswersArrayList().size(); j++)
                        {
                            Answers answers = new Answers();
                            answers.setIsAns(modelTestResult.getQuesArrayList().get(i).getAnswersArrayList().get(j).getIsAns());
                            answers.setOptionId(modelTestResult.getQuesArrayList().get(i).getAnswersArrayList().get(j).getOptionId());
                            answers.setOption(modelTestResult.getQuesArrayList().get(i).getAnswersArrayList().get(j).getOption());
                            try
                            {
                                String qstnID= DatabaseClient
                                        .getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .taskDao()
                                        .getQstnID(String.valueOf(i+1));

                                String ansID= DatabaseClient
                                        .getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .taskDao()
                                        .getAnsID(qstnID);

                                    if (ansID.equalsIgnoreCase(modelTestResult.getQuesArrayList().get(i).getAnswersArrayList().get(j).getOptionId()))
                                    {
                                        answers.setChecked(true);
                                    }


                            }
                            catch (Exception e)
                            {
                                Log.e("getLocalizedMessage",e.getLocalizedMessage());
                            }
                            answersArrayList.add(answers);
                            ques.setAnswersArrayList(answersArrayList);

                        }
                        quesArrayList.add(ques);
                    }
                    showQuestion();
                }

            }


            @Override
            public void onFailure(Call<ModelTestResult> call, Throwable t) {
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration
    {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


    private int dpToPx(int dp)
    {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
