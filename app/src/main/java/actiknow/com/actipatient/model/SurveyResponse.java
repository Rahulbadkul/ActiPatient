package actiknow.com.actipatient.model;

import android.util.Log;

/**
 * Created by actiknow on 3/16/17.
 */

public class SurveyResponse {
    int id;
    String response;


    public SurveyResponse(int id, String response){
        this.id = id;
        this.response = response;
    }

    public SurveyResponse() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
