package actiknow.com.actipatient.model;
public class SurveyResponse {
    int question_id;
    int option_id;
    int question_category_id;

    public SurveyResponse (int question_id, int option_id, int question_category_id){
        this.question_id = question_id;
        this.option_id = option_id;
        this.question_category_id = question_category_id;
    }

    public SurveyResponse () {

    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getOption_id () {
        return option_id;
    }

    public void setOption_id (int option_id) {
        this.option_id = option_id;
    }

    public int getQuestion_category_id () {
        return question_category_id;
    }

    public void setQuestion_category_id (int question_category_id) {
        this.question_category_id = question_category_id;
    }
}
