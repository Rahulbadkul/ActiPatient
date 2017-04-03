package actiknow.com.actipatient.model;
import android.util.Log;

import java.util.ArrayList;

public class Question {
    int question_id;
    String question_text;
    int question_category_id;
    ArrayList<QuestionOptions> questionOptionList =new ArrayList<>();

    public Question(int question_id, String question_text, int question_category_id){
        this.question_id = question_id;
        this.question_text = question_text;
        this.question_category_id = question_category_id;
    }

    public int getQuestion_category_id () {
        return question_category_id;
    }

    public void setQuestion_category_id (int question_category_id) {
        this.question_category_id = question_category_id;
    }

    public Question() {


    }

    public int getQuestion_id () {
        return question_id;
    }

    public void setQuestion_id (int question_id) {
        this.question_id = question_id;
    }

    public ArrayList<QuestionOptions> getQuestionOptionList () {
        return questionOptionList;
     }

    public void setQuestionOptionList (ArrayList<QuestionOptions> questionOptionList) {
        this.questionOptionList = questionOptionList;
    }

    public void addQuestionOption (QuestionOptions option) {
        this.questionOptionList.add(option);
    }

    public String getQuestion_text () {
        return question_text;
    }

    public void setQuestion_text (String question_text) {
        this.question_text = question_text;
        Log.e("QUESTIONMODEL",""+ question_text);
    }
}