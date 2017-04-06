package actiknow.com.actipatient.model;

public class QuestionOption {
    int option_id;
    String option_text;

    public QuestionOption (int option_id, String option_text) {
        this.option_id = option_id;
        this.option_text = option_text;
    }

    public QuestionOption () {
    }

    public int getOption_id () {
        return option_id;
    }

    public void setOption_id (int option_id) {
        this.option_id = option_id;
    }

    public String getOption_text () {
        return option_text;
    }

    public void setOption_text (String option_text) {
        this.option_text = option_text;
    }
}
