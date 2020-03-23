package com.effseele.effilearn.Results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Ques {

    @SerializedName("QuestionId")
    @Expose
    private String QuestionId;

    @SerializedName("Question")
    @Expose
    private String Question;

    @SerializedName("Marks")
    @Expose
    private String Marks;


    @SerializedName("options")
    @Expose
    private ArrayList<Answers> answersArrayList;

    public void setQuestionId(String questionId) {
        QuestionId = questionId;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public void setMarks(String marks) {
        Marks = marks;
    }

    public void setAnswersArrayList(ArrayList<Answers> answersArrayList) {
        this.answersArrayList = answersArrayList;
    }

    public Ques(String questionId, String question, String marks, ArrayList<Answers> answersArrayList) {
        QuestionId = questionId;
        Question = question;
        Marks = marks;
        this.answersArrayList = answersArrayList;
    }

    public Ques() {

    }

    public String getQuestionId() {
        return QuestionId;
    }

    public String getQuestion() {
        return Question;
    }

    public String getMarks() {
        return Marks;
    }

    public ArrayList<Answers> getAnswersArrayList() {
        return answersArrayList;
    }
}
