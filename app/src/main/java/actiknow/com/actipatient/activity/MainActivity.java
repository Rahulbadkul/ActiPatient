package actiknow.com.actipatient.activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import actiknow.com.actipatient.R;
import actiknow.com.actipatient.fragment.StartFragment;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.SurveyType;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;

import static actiknow.com.actipatient.utils.Constants.language;
import static android.R.attr.name;


public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    public static ArrayList<Question>QuestionList = new ArrayList<>();
    UserDetailsPref userDetailPref;
    String android_id;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView (R.layout.activity_main);
        initView ();
        initData();
        checkLogin();
        getSurveyType();
        getPref();
        isLogin ();

    }

    private void checkLogin() {
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Android","Android ID : "+android_id);
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_CHECKLOGIN+"/"+userDetailPref.getStringPref(this,AppConfigTags.DEVICE_ID)+"/"+android_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_CHECKLOGIN+"/"+userDetailPref.getStringPref(this,AppConfigTags.DEVICE_ID)+"/"+android_id,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Constants.surveyTypeList.clear ();
                            int json_array_len = 0;
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    int status = jsonObj.getInt(AppConfigTags.STATUS);
                                    switch(status){
                                        case 0:
                                            Toast.makeText(MainActivity.this,"Error occured, Try again",Toast.LENGTH_LONG).show();
                                            checkLogin();
                                            break;

                                        case 1:
                                            break;

                                        case 2:
                                            Toast.makeText(MainActivity.this,"Your subscription has expired or suspended",Toast.LENGTH_LONG).show();
                                            LogOutPref();
                                            break;

                                        case 3:
                                            Toast.makeText(MainActivity.this,"Device Login not found",Toast.LENGTH_LONG).show();
                                            LogOutPref();
                                            break;

                                        case 4:
                                            Toast.makeText(MainActivity.this,"Device does not exist",Toast.LENGTH_LONG).show();
                                            LogOutPref();
                                            break;

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            progressBar.setVisibility (View.GONE);
                            //  listViewAllAtm.setVisibility (View.VISIBLE);
                            //  getAtmListFromLocalDatabase ();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, Constants.hospital_login_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            strRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Utils.sendRequest (strRequest, 30);

        } else {
            progressBar.setVisibility (View.GONE);
            Toast.makeText(MainActivity.this,"Seems like there is no internet connection, the app will continue in Offline mode",Toast.LENGTH_LONG).show();
        }
    }

    private void LogOutPref() {
        userDetailPref.putStringPref (MainActivity.this, userDetailPref.DEVICE_ID, "");
        userDetailPref.putStringPref (MainActivity.this, userDetailPref.DEVICE_LOCATION, "");
        userDetailPref.putStringPref (MainActivity.this, userDetailPref.HOSPITAL_NAME, "");
        userDetailPref.putStringPref(MainActivity.this, userDetailPref.HOSPITAL_LOGIN_KEY, "");
        userDetailPref.putStringPref (MainActivity.this, userDetailPref.HOSPITAL_ACCESS_PIN, "");
        userDetailPref.putStringPref (MainActivity.this, userDetailPref.SUBSCRIPTION_STATUS, "");
        userDetailPref.putStringPref (MainActivity.this, userDetailPref.SUBSCRIPTION_STARTS, "");
        userDetailPref.putStringPref (MainActivity.this, userDetailPref.SUBSCRIPTION_EXPIRES, "");
        Intent intent = new Intent (MainActivity.this, LoginActivity.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity (intent);
        MainActivity.this.overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void initData() {
        userDetailPref = UserDetailsPref.getInstance ();
    }

    private void getSurveyType() {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SURVEYTYPE, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_SURVEYTYPE,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Constants.surveyTypeList.clear ();
                            int json_array_len = 0;
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    JSONArray jsonArray = jsonObj.getJSONArray(AppConfigTags.SURVEY_TYPE);
                                    json_array_len = jsonArray.length();
                                    for (int i = 0; i < json_array_len; i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        SurveyType surveytype = new SurveyType();
                                        surveytype.setSurvey_type_id(jsonObject.getInt(AppConfigTags.SURVEYTYPE_ID));
                                        surveytype.setSurvey_type(new String(jsonObject.getString(AppConfigTags.SURVEYTYPE_TEXT)));
                                        Constants.surveyTypeList.add(surveytype);

                                    }
                                    setPreferences();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            progressBar.setVisibility (View.GONE);
                            //  listViewAllAtm.setVisibility (View.VISIBLE);
                            //  getAtmListFromLocalDatabase ();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, Constants.hospital_login_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            strRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Utils.sendRequest (strRequest, 30);

        } else {
            progressBar.setVisibility (View.GONE);
            // listViewAllAtm.setVisibility (View.VISIBLE);
            //  getAtmListFromLocalDatabase ();
            Toast.makeText(MainActivity.this,"Seems like there is no internet connection, the app will continue in Offline mode",Toast.LENGTH_LONG).show();
        }
        }

    private void getPref() {
        Constants.language = userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.LANGUAGE);
        Log.e(AppConfigTags.LANGUAGE,Constants.language);
        Log.e(AppConfigTags.DEVICE_ID,userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.DEVICE_ID));
        Log.e(AppConfigTags.DEVICE_LOCATION,userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.DEVICE_LOCATION));
        Log.e(AppConfigTags.HOSPITAL_NAME,userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_NAME));
        Log.e(AppConfigTags.HOSPITAL_LOGIN_KEY,userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
        Log.e(AppConfigTags.HOSPITAL_ACCESS_PIN,userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_ACCESS_PIN));
        Log.e(AppConfigTags.SUBSCRIPTION_STATUS,userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STATUS));
        Log.e(AppConfigTags.SUBSCRIPTION_STARTS,userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STARTS));
        Log.e(AppConfigTags.SUBSCRIPTION_EXPIRES,userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_EXPIRES));
        defaultFragment();
        if(language.equalsIgnoreCase("")){
            String languageToLoad = "en"; // your language
            Locale locale = new Locale (languageToLoad);
            Locale.setDefault (locale);
            Configuration config = new Configuration ();
            config.locale = locale;
            MainActivity.this.getResources ().updateConfiguration (config, MainActivity.this.getResources ().getDisplayMetrics ());
        }
    }

    private void isLogin () {
        if (userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.DEVICE_ID) == "" || userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY) == "") {
            Intent myIntent = new Intent (this, LoginActivity.class);
            startActivity (myIntent);
        }
        if (userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY) == "")
            finish ();
    }



    private void defaultFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        StartFragment f1 = new StartFragment();
        Bundle args = new Bundle();
        args.putString(AppConfigTags.STATUS, "welcome");
        f1.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, f1, "fragment1");
        fragmentTransaction.commit();
    }

    private void setPreferences() {
        UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
        Log.e("SURVEY_TYPE_ID","SUD"+Constants.survey_type);
        if(Constants.survey_type.equalsIgnoreCase("")) {
            userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.SURVEY_TYPE, "1");
        }
    }

    private void initView () {
        progressBar = (ProgressBar) findViewById (R.id.progressbar);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage(R.string.quit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        defaultFragment();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }

}
