package actiknow.com.actipatient.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bugsnag.android.Bugsnag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import actiknow.com.actipatient.R;
import actiknow.com.actipatient.fragment.DefaultFragment;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.SurveyType;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;


public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    public static ArrayList<Question> QuestionList = new ArrayList<> ();
    UserDetailsPref userDetailPref;
    String android_id;
    CoordinatorLayout clMain;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView (R.layout.activity_main);
        initView ();
        initData ();
        isLogin ();
        getSurveyTypes ();
        checkApplicationStatus ();
        loadDefaultFragment ();
    }

    private void initView () {
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        progressBar = (ProgressBar) findViewById (R.id.progressbar);
    }

    private void initData () {
        Bugsnag.init (this);
        userDetailPref = UserDetailsPref.getInstance ();
        userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.LANGUAGE);
//        Log.e (AppConfigTags.LANGUAGE, Constants.language);
//        Log.e (AppConfigTags.DEVICE_ID, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.DEVICE_ID));
//        Log.e (AppConfigTags.DEVICE_LOCATION, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.DEVICE_LOCATION));
//        Log.e (AppConfigTags.HOSPITAL_NAME, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_NAME));
//        Log.e (AppConfigTags.HOSPITAL_LOGIN_KEY, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
//        Log.e (AppConfigTags.HOSPITAL_ACCESS_PIN, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_ACCESS_PIN));
//        Log.e (AppConfigTags.SUBSCRIPTION_STATUS, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STATUS));
//        Log.e (AppConfigTags.SUBSCRIPTION_STARTS, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STARTS));
//        Log.e (AppConfigTags.SUBSCRIPTION_EXPIRES, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_EXPIRES));
        if (userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.LANGUAGE).equalsIgnoreCase ("")) {
            String languageToLoad = "en"; // your language
            Locale locale = new Locale (languageToLoad);
            Locale.setDefault (locale);
            Configuration config = new Configuration ();
            config.locale = locale;
            MainActivity.this.getResources ().updateConfiguration (config, MainActivity.this.getResources ().getDisplayMetrics ());
        } else {
            String languageToLoad = userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.LANGUAGE);
            Locale locale = new Locale (languageToLoad);
            Locale.setDefault (locale);
            Configuration config = new Configuration ();
            config.locale = locale;
            MainActivity.this.getResources ().updateConfiguration (config, MainActivity.this.getResources ().getDisplayMetrics ());
        }
    }

    private void isLogin () {
        if (userDetailPref.getIntPref (MainActivity.this, UserDetailsPref.DEVICE_ID) == 0 || userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY) == "") {
            Intent myIntent = new Intent (this, LoginActivity.class);
            startActivity (myIntent);
        }
        if (userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY) == "")
            finish ();
    }

    private void checkApplicationStatus () {
        android_id = Settings.Secure.getString (this.getContentResolver (), Settings.Secure.ANDROID_ID);
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_CHECKLOGIN + "/" + userDetailPref.getIntPref (this, UserDetailsPref.DEVICE_ID) + "/" + android_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_CHECKLOGIN + "/" + userDetailPref.getIntPref (this, UserDetailsPref.DEVICE_ID) + "/" + android_id,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    int status = jsonObj.getInt (AppConfigTags.STATUS);
                                    if (status == 0) {
                                        checkApplicationStatus ();
                                    } else if (status != 1){
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        logOutApplication ();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            strRequest.setRetryPolicy (new DefaultRetryPolicy (DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Utils.sendRequest (strRequest, 30);

        } else {
            checkApplicationStatus ();
        }
    }

    private void logOutApplication () {
        userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.DEVICE_ID, 0);
        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.DEVICE_LOCATION, "");
        userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID, 0);
        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_NAME, "");
        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY, "");
        userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.HOSPITAL_ACCESS_PIN, 0);
        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STATUS, "");
        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STARTS, "");
        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_EXPIRES, "");
        Intent intent = new Intent (MainActivity.this, LoginActivity.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity (intent);
        MainActivity.this.overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getSurveyTypes () {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SURVEYTYPE, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_SURVEYTYPE,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Constants.surveyTypeList.clear ();
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.SURVEY_TYPE);
                                    for (int i = 0; i < jsonArray.length (); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                                        SurveyType surveytype = new SurveyType ();
                                        surveytype.setSurvey_status (jsonObject.getBoolean (AppConfigTags.SURVEY_TYPE_ACTIVE));
                                        surveytype.setSurvey_type_id (jsonObject.getInt (AppConfigTags.SURVEY_TYPE_ID));
                                        surveytype.setSurvey_type_text (jsonObject.getString (AppConfigTags.SURVEY_TYPE_TEXT));
                                        if (surveytype.isSurvey_status ()) {
                                            userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID, surveytype.getSurvey_type_id ());
                                        }
                                        Constants.surveyTypeList.add (surveytype);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            strRequest.setRetryPolicy (new DefaultRetryPolicy (DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Utils.sendRequest (strRequest, 30);

        } else {
            if (userDetailPref.getIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID) == 0) {
                userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID, 1);
            }
        }
    }

    private void loadDefaultFragment () {
        FragmentManager fragmentManager = getFragmentManager ();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction ();
        DefaultFragment f1 = new DefaultFragment ();
        Bundle args = new Bundle ();
        args.putString (AppConfigTags.STATUS, "welcome");
        f1.setArguments (args);
        fragmentTransaction.replace (R.id.fragment_container, f1, "fragment1");
        fragmentTransaction.commit ();
    }
}
