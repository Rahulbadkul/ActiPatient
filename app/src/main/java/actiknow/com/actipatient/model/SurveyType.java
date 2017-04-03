package actiknow.com.actipatient.model;

/**
 * Created by actiknow on 3/27/17.
 */

public class SurveyType {
    int survey_type_id;
    String survey_type_text;
    boolean survey_status;

    public SurveyType(int survey_type_id, String survey_type_text, boolean survey_status) {
        this.survey_type_id = survey_type_id;
        this.survey_type_text = survey_type_text;
        this.survey_status = survey_status;
    }

    public SurveyType() {

    }

    public int getSurvey_type_id() {
        return survey_type_id;
    }

    public void setSurvey_type_id(int survey_type_id) {
        this.survey_type_id = survey_type_id;
    }

    public String getSurvey_type_text () {
        return survey_type_text;
    }

    public void setSurvey_type_text (String survey_type_text) {
        this.survey_type_text = survey_type_text;
    }


    public boolean isSurvey_status() {
        return survey_status;
    }

    public void setSurvey_status(boolean survey_status) {
        this.survey_status = survey_status;
    }


}

