package com.effseele.effilearn.RoomDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity
public class SavedOption implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "questionid")
    private String questionid;

    @ColumnInfo(name = "answerid")
    private String answerid;

    @ColumnInfo(name = "correctanswerid")
    private String correctanswerid;

    @ColumnInfo(name = "marks")
    private String marks;

    public String getCorrectanswerid() {
        return correctanswerid;
    }

    public void setCorrectanswerid(String correctanswerid) {
        this.correctanswerid = correctanswerid;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionid() {
        return questionid;
    }

    public void setQuestionid(String questionid) {
        this.questionid = questionid;
    }

    public String getAnswerid() {
        return answerid;
    }

    public void setAnswerid(String answerid) {
        this.answerid = answerid;
    }
}
