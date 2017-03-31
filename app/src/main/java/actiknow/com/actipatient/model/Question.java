package actiknow.com.actipatient.model;


/**
 * Created by actiknow on 3/9/17.
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by actiknow on 3/9/17.
 */

public class Question {
    int id;
    String question_name;
    String question_category_id;
    ArrayList<SurveyResponse> listOption=new ArrayList<>();

    public Question(int id, String question_name, String question_category_id){
        this.id = id;
        this.question_name = question_name;
        this.question_category_id = question_category_id;
        //this.listOption = listOption;
    }

    public String getQuestion_category_id() {
        return question_category_id;
    }

    public void setQuestion_category_id(String question_category_id) {
        this.question_category_id = question_category_id;
    }

    public Question() {


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

      public ArrayList<SurveyResponse> getListOption() {
        return listOption;
     }

    public void setListOption(ArrayList<SurveyResponse> listOption) {
        this.listOption = listOption;
        //Log.d("LISTOPTION",""+listOption);
    }

    public void addListOption(SurveyResponse option) {
        this.listOption.add(option);
        //Log.d("LISTOPTION",""+listOption);
    }

    public String getQuestion_name() {
        return question_name;
    }

    public void setQuestion_name(String question_name) {
        this.question_name = question_name;
        Log.e("QUESTIONMODEL",""+question_name);
    }
}