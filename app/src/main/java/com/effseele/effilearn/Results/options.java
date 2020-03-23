package com.effseele.effilearn.Results;

public class options {
    String OptionId,Option,IsAns;

    public options(String optionId, String option, String isAns) {
        OptionId = optionId;
        Option = option;
        IsAns = isAns;
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
}
