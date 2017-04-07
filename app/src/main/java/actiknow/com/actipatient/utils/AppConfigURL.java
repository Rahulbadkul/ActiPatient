package actiknow.com.actipatient.utils;

public class AppConfigURL {

    //    public static String host="https://actipatient-api-cammy92.c9users.io/api/";
    public static String host = "http://ec2-52-42-89-17.us-west-2.compute.amazonaws.com/actipatient/api/";
    
    public static String version_name = "v1.1";


    public static String URL_GETQUESTION = host + version_name + "/survey/generate";
    public static String URL_POSTSURVEY = host + version_name + "/survey/submit";
    public static String URL_SURVEYTYPE = host + version_name + "/survey/types";
    public static String URL_LOGIN = host + version_name + "/login/device";
    public static String URL_LOGOUT_ACTIVESESSION = host + version_name + "/logout/active_session";
    public static String URL_LOGOUT = host + version_name + "/logout/device";
    public static String URL_FORGET_PASSWORD = host + version_name + "/login/device/forgot/password";
    public static String URL_FORGET_PIN = host + version_name + "/login/hospital/forgot/PIN";
    public static String URL_CHECKLOGIN = host + version_name + "/check/status/login";
    public static String URL_CHECKVERSION = host + version_name + "/check/status/version";
}
