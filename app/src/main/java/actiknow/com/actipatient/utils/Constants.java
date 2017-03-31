package actiknow.com.actipatient.utils;


import java.util.ArrayList;
import java.util.List;

import actiknow.com.actipatient.model.AnswerResponse;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.SurveyType;

import static android.R.attr.key;

public class Constants {


    public static String font_name = "CenturyGothic.ttf";
    public static List<Question> questionsList = new ArrayList<Question>();
    public static List<SurveyType> surveyTypeList = new ArrayList<SurveyType>();
    public static ArrayList<AnswerResponse>answerResponseList = new ArrayList<>();
    public static boolean show_log = true;
    public static String server_time = "";
    public static String status = "";
    public static String survey_id;
    public static String language = "";
    public static String survey_type = "";
    public static String api_key = "7abc118e222be9dbe8225f504ff6fcbc";
    public static String hospital_login_key = "9e3d710529e11ab2be4e39402ae544ce";
    public static String patient_id = "MH123456";

}
