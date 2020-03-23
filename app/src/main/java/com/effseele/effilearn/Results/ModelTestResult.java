package com.effseele.effilearn.Results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelTestResult
{
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("Ques")
    @Expose
    private ArrayList<Ques> quesArrayList;

    public ModelTestResult(String id, String status, ArrayList<Ques> quesArrayList) {
        this.id = id;
        this.status = status;
        this.quesArrayList = quesArrayList;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Ques> getQuesArrayList() {
        return quesArrayList;
    }
}
