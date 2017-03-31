package actiknow.com.actipatient.model;

import static android.R.attr.id;
import static android.icu.text.RelativeDateTimeFormatter.Direction.THIS;

/**
 * Created by actiknow on 3/27/17.
 */

public class SurveyType {
    int survey_type_id;
    String survey_type;
    boolean survey_status;

    public SurveyType(int survey_type_id, String survey_type, boolean survey_status) {
        this.survey_type_id = survey_type_id;
        this.survey_type = survey_type;
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

    public String getSurvey_type() {
        return survey_type;
    }

    public void setSurvey_type(String survey_type) {
        this.survey_type = survey_type;
    }

    public boolean isSurvey_status() {
        return survey_status;
    }

    public void setSurvey_status(boolean survey_status) {
        this.survey_status = survey_status;
    }


}

