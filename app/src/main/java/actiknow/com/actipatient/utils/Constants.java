package actiknow.com.actipatient.utils;


import java.util.ArrayList;
import java.util.List;

import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.SurveyResponse;
import actiknow.com.actipatient.model.SurveyType;

public class Constants {


    public static String font_name = "CenturyGothic.ttf";
    public static ArrayList<Question> QuestionList = new ArrayList<> ();
    public static List<SurveyType> surveyTypeList = new ArrayList<SurveyType>();
    public static ArrayList<SurveyResponse> surveyResponseList = new ArrayList<>();
    public static boolean show_log = true;
    public static String server_time = "";
    public static String status = "";
    public static String survey_id;
    public static String patient_id;


    public static String encryption_key = "actipatient12345";
    public static String api_key = "9e3d710529e11ab2be4e39402ae544ce";
//    public static String hospital_login_key = "9e3d710529e11ab2be4e39402ae544ce";

}
