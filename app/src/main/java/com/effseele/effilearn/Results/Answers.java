package com.effseele.effilearn.Results;

public class Answers {
 private String OptionId,Option,IsAns;
    private boolean isChecked;

    public void setOptionId(String optionId) {
        OptionId = optionId;
    }

    public void setOption(String option) {
        Option = option;
    }

    public void setIsAns(String isAns) {
        IsAns = isAns;
    }

    public Answers(String optionId, String option, String isAns) {
        OptionId = optionId;
        Option = option;
        IsAns = isAns;
    }

    public Answers() {

    }

    public String getOptionId() {
        return OptionId;
    }

    public String getOption() {
        return Option;
    }

    public String getIsAns() {
        return IsAns;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
