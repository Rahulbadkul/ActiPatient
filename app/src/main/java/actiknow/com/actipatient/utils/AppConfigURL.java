package actiknow.com.actipatient.utils;

public class AppConfigURL {

    public static String HOST = "https://actipatient-api-cammy92.c9users.io/api/";

//    public static String HOST = "http://actipatient.com/api/";
//    public static String HOST = "http://ec2-52-42-89-17.us-west-2.compute.amazonaws.com/actipatient/api/";
    
    public static String VERSION_NAME = "v1.2.2";


    public static String URL_SURVEY_GENERATE = HOST + VERSION_NAME + "/survey/generate";
    public static String URL_SURVEY_SUBMIT = HOST + VERSION_NAME + "/survey/submit";
    public static String URL_SURVEY_TYPE = HOST + VERSION_NAME + "/survey/types";
    public static String URL_LOGIN_DEVICE = HOST + VERSION_NAME + "/login/device";
    public static String URL_LOGOUT_ACTIVE_SESSION = HOST + VERSION_NAME + "/logout/active_session";
    public static String URL_LOGOUT_DEVICE = HOST + VERSION_NAME + "/logout/device";
    public static String URL_FORGET_PASSWORD = HOST + VERSION_NAME + "/login/device/forgot/password";
    public static String URL_FORGET_PIN = HOST + VERSION_NAME + "/login/hospital/forgot/PIN";
    public static String URL_CHECK_STATUS_APPLICATION = HOST + VERSION_NAME + "/check/status/application";
    public static String URL_CHECK_VERSION = HOST + VERSION_NAME + "/check/status/version";
}
