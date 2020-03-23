package com.effseele.effilearn.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.effseele.effilearn.ModelTestActivity;
import com.effseele.effilearn.R;
import com.effseele.effilearn.ResultActivity;
import com.effseele.effilearn.Results.Answers;
import com.effseele.effilearn.RoomDatabase.DatabaseClient;
import com.effseele.effilearn.RoomDatabase.SavedOption;
import com.effseele.effilearn.cusFnt.ButtonAirenRegular;
import com.effseele.effilearn.databinding.ItemAnswersBinding;
import com.effseele.effilearn.session.SessionManager;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mcontex;
    private ModelTestActivity activity;
    private SessionManager sessionManager;
    private ItemAnswersBinding binding;
    private ArrayList<Answers> answersArrayList;
    private ButtonAirenRegular nextBTN, previousBTN, submitBTN;
    private String selectedoption = "", databaseSelectOption = "";
    private int mSelectedItemPosition = -1;
    private int pos = -1;
    private String marks, questionId;

    public AnswersAdapter(ModelTestActivity mcontex, ArrayList<Answers> answersArrayList, String marks,
                          String questionId, ButtonAirenRegular nextBTN,
                          ButtonAirenRegular previousBTN, ButtonAirenRegular submitBTN)
    {
        this.mcontex = mcontex;
        activity = mcontex;
        sessionManager = new SessionManager(mcontex.getApplicationContext());
        this.answersArrayList = answersArrayList;
        this.nextBTN = nextBTN;
        this.previousBTN = previousBTN;
        this.submitBTN = submitBTN;
        this.marks = marks;
        this.questionId = questionId;
    }


    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_answers, parent, false);
        return new ViewHolderPollAdapter(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder h, final int position)
    {
        if (h instanceof ViewHolderPollAdapter)
        {
            final ViewHolderPollAdapter holder = (ViewHolderPollAdapter) h;
            holder.binding.optionTV.setText(answersArrayList.get(position).getOption());
            if (answersArrayList.get(position).isChecked())
            {
                binding.optionTV.setBackgroundResource(R.drawable.selectansbutton);
                mSelectedItemPosition = position;
                try
                {
                    selectedoption = DatabaseClient
                            .getInstance(mcontex.getApplicationContext())
                            .getAppDatabase()
                            .taskDao()
                            .getAnsID(questionId);
                    Log.e("selectedoption",selectedoption);
                    databaseSelectOption = selectedoption;

                } catch (Exception e) {
                }
            } else {
                binding.optionTV.setBackgroundResource(R.drawable.button);
            }


            nextBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (selectedoption.isEmpty()) {
                            Toast.makeText(mcontex, "Select one ans", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            checkQuestion(selectedoption, position);
                            answersArrayList.get(position).setChecked(!answersArrayList.get(position).isChecked());
                            activity.updateNextQuestion();
                            if (databaseSelectOption.isEmpty()) {
                                SaveTask saveTask = new SaveTask();
                                saveTask.execute();
                            } else {
                                UpdateTask updateTask = new UpdateTask();
                                updateTask.execute();
                            }
                        }

                    } catch (Exception e) {
                    }

                }
            });

            submitBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (selectedoption.isEmpty()) {
                            Toast.makeText(mcontex, "Select one ans", Toast.LENGTH_SHORT).show();
                        } else {
                            checkQuestion(selectedoption, position);
                            answersArrayList.get(position).setChecked(!answersArrayList.get(position).isChecked());
                            if (databaseSelectOption.isEmpty()) {
                                SaveTask saveTask = new SaveTask();
                                saveTask.execute();
                            } else {
                                UpdateTask updateTask = new UpdateTask();
                                updateTask.execute();
                            }
                        }
                        Intent intent = new Intent(mcontex, ResultActivity.class);
                        activity.startActivity(intent);

                    } catch (Exception e) {
                    }
                }
            });


            previousBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        activity.updatePreviousQuestion();
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            });
            binding.optionTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedoption = answersArrayList.get(position).getOptionId();
                    int previousSelectState = mSelectedItemPosition;
                    mSelectedItemPosition = holder.getAdapterPosition();
                    notifyItemChanged(previousSelectState);
                    notifyItemChanged(mSelectedItemPosition);
                    notifyDataSetChanged();

                }
            });
            holder.bindDataWithViewHolder(answersArrayList.get(position), position);

        }

    }

    private void checkQuestion(final String options, int pos) {
        for (int i = 0; i < answersArrayList.size(); i++) {
            if (options.trim().equals(answersArrayList.get(pos).getOptionId())) {
                if (answersArrayList.get(pos).getIsAns().equalsIgnoreCase("1")) {
                    activity.addMarks();
                    break;
                }

            }
        }

    }

    @Override
    public int getItemCount() {
        return answersArrayList != null ? answersArrayList.size() : 0;
    }


    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {
        private ItemAnswersBinding binding;
        private Answers answers;

        private ViewHolderPollAdapter(ItemAnswersBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        private void bindDataWithViewHolder(Answers answers, int position) {

            this.answers = answers;


            if (position == mSelectedItemPosition) {
                binding.optionTV.setBackgroundResource(R.drawable.selectansbutton);
            } else {
                binding.optionTV.setBackgroundResource(R.drawable.button);
            }

        }
    }


    private class SaveTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            SavedOption task = new SavedOption();
            task.setQuestionid(questionId);
            task.setAnswerid(selectedoption);
            for (int i = 0; i < answersArrayList.size(); i++) {
                if (answersArrayList.get(i).getIsAns().equalsIgnoreCase("1")) {
                    task.setCorrectanswerid(answersArrayList.get(i).getOptionId());
                    break;
                }
            }
            task.setMarks(marks);
            DatabaseClient.getInstance(mcontex.getApplicationContext()).getAppDatabase()
                    .taskDao()
                    .insert(task);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(mcontex, "success", Toast.LENGTH_SHORT).show();
        }
    }

   private class UpdateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SavedOption task = new SavedOption();
            task.setQuestionid(questionId);
            task.setAnswerid(selectedoption);
            for (int i = 0; i < answersArrayList.size(); i++) {
                if (answersArrayList.get(i).getIsAns().equalsIgnoreCase("1")) {
                    task.setCorrectanswerid(answersArrayList.get(i).getOptionId());
                    break;

                }
            }
            task.setMarks(marks);
            DatabaseClient.getInstance(mcontex.getApplicationContext()).getAppDatabase()
                    .taskDao()
                    .update(task);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(mcontex, "updatesuccess", Toast.LENGTH_SHORT).show();

        }
    }


}