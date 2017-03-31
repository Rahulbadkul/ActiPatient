package actiknow.com.actipatient.model;

import android.util.Log;

import static android.R.attr.id;

/**
 * Created by actiknow on 3/16/17.
 */

public class AnswerResponse {
    int question_id;
    int response_id;
    String question_category_id;

    public AnswerResponse(int question_id, int response_id, String question_category_id){
        this.question_id = question_id;
        this.response_id = response_id;
        this.question_category_id = question_category_id;
    }

    public AnswerResponse() {

    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getResponse_id() {
        return response_id;
    }

    public void setResponse_id(int response_id) {
        this.response_id = response_id;
    }


    public String getQuestion_category_id() {
        return question_category_id;
    }

    public void setQuestion_category_id(String question_category_id) {
        this.question_category_id = question_category_id;
        Log.e("QuestionCategoryId",question_category_id);
    }

}
