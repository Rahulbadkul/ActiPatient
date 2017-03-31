package actiknow.com.actipatient.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import actiknow.com.actipatient.R;
import actiknow.com.actipatient.activity.LoginActivity;
import actiknow.com.actipatient.activity.MainActivity;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.SurveyResponse;
import actiknow.com.actipatient.model.SurveyType;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.SetTypeFace;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;
import fr.ganfra.materialspinner.MaterialSpinner;

import static actiknow.com.actipatient.R.id.tvExpiry;
import static actiknow.com.actipatient.activity.MainActivity.QuestionList;
import static android.widget.AdapterView.*;

/**
 * Created by actiknow on 3/10/17.
 */

public class StartFragment extends Fragment {
    private static int SPLASH_TIME_OUT = 0000;
    TextView tvThanku;
    TextView tvStart;
    ImageView imSettings;
    TextView tvHindi;
    TextView tvEnglish;
    TextView tvSurveyType;
    ImageView imLogo;
    String english = "en";
    String hindi = "hi";
    SurveyType surveytype;
    UserDetailsPref userDetailPref;
    ArrayList<String>addSurveyTypeList = new ArrayList<>();
    UserDetailsPref userDetailsPref;
    private ProgressDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, null);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initView(v);
        getPref();
        initData();
        initListener();
        return v;
    }

    private void getPref() {
        userDetailPref = UserDetailsPref.getInstance ();
        Constants.language = userDetailPref.getStringPref (getActivity(), UserDetailsPref.LANGUAGE);
        Constants.survey_type = userDetailPref.getStringPref(getActivity(), UserDetailsPref.SURVEY_TYPE);
        String mStringDate = "25-11-15 14:23:34";
        String oldFormat= "dd-MM-yy HH:mm:ss";
        String newFormat= "dd-MMM-yyyy HH:mm";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mStringDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        formatedDate = timeFormat.format(myDate);
        Log.e("Date",""+formatedDate);

        try {
                Picasso.with(getActivity()).load(userDetailPref.getStringPref(getActivity(), UserDetailsPref.HOSPITAL_LOGO)).into(imLogo);

            } catch (Exception e) {
                e.printStackTrace();
            }
        Log.e(AppConfigTags.LANGUAGE,Constants.language);
        tvThanku.setText(getResources().getString(R.string.welcome)+" "+userDetailPref.getStringPref(getActivity(),AppConfigTags.HOSPITAL_NAME));

        switch (Constants.language){
            case "en":
                tvThanku.setText(getResources().getString(R.string.welcome)+" "+userDetailPref.getStringPref(getActivity(),AppConfigTags.HOSPITAL_NAME));
                tvStart.setText(R.string.start);
                languageChangeEnglish();
                tvHindi.setBackgroundResource(R.drawable.button_white);
                tvEnglish.setBackgroundResource(R.drawable.button_green);
                break;

            case "hi":
                tvThanku.setText(userDetailPref.getStringPref(getActivity(),AppConfigTags.HOSPITAL_NAME)+ " " +getResources().getString(R.string.welcome));
                tvStart.setText(R.string.start);
                languageChangeHindi();
                tvEnglish.setBackgroundResource(R.drawable.button_white);
                tvHindi.setBackgroundResource(R.drawable.button_green);
                break;
        }
        if(Constants.survey_type.equalsIgnoreCase("2")){
            tvSurveyType.setText("OP");
        }else{
            tvSurveyType.setText("IP");
        }
        if(Constants.status.equalsIgnoreCase("thanku")){
            tvStart.setVisibility(View.GONE);
            tvThanku.setText(R.string.thanku);
            Constants.status = "";
        }
        Constants.answerResponseList.clear();
    }

    private void initView(View v) {
        tvThanku   = (TextView) v.findViewById(R.id.tvThanku);
        tvStart    = (TextView) v.findViewById(R.id.tvStart);
        imSettings = (ImageView)v.findViewById(R.id.imSettings);
        tvHindi    = (TextView)v.findViewById(R.id.tvHindi);
        tvEnglish  = (TextView)v.findViewById(R.id.tvEnglish);
        tvSurveyType = (TextView)v.findViewById(R.id.tvSurveyType);
        imLogo = (ImageView)v.findViewById(R.id.imLogo);
        tvThanku.setTypeface(SetTypeFace.getTypeface(getActivity()));
        tvEnglish.setTypeface(SetTypeFace.getTypeface(getActivity()));
        tvStart.setTypeface(SetTypeFace.getTypeface(getActivity()));
        tvSurveyType.setTypeface(SetTypeFace.getTypeface(getActivity()));
        tvStart.setText(R.string.start);
    }

    private void initListener(){
        imSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordDialog();
                //showDialog();
            }
        });
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQuestionFromServer(Constants.language);

            }
        });
        tvHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageChangeHindi();
                tvThanku.setText(userDetailPref.getStringPref(getActivity(),AppConfigTags.HOSPITAL_NAME)+ " " +getResources().getString(R.string.welcome));
                tvEnglish.setBackgroundResource(R.drawable.button_white);
                tvHindi.setBackgroundResource(R.drawable.button_green);
                setPreferences(hindi);
                fragmentreload();

            }
        });
        tvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageChangeEnglish();
                tvThanku.setText(getResources().getString(R.string.welcome)+" "+userDetailPref.getStringPref(getActivity(),AppConfigTags.HOSPITAL_NAME));
                tvHindi.setBackgroundResource(R.drawable.button_white);
                tvEnglish.setBackgroundResource(R.drawable.button_green);
                setPreferences(english);
                fragmentreload();
            }
        });
    }

    private void showPasswordDialog() {
            new MaterialDialog.Builder(getActivity())
                    .title("Please enter your Access PIN")
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER)
                    .typeface(SetTypeFace.getTypeface(getActivity()), SetTypeFace.getTypeface(getActivity()))
                    .input("", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if(String.valueOf(input).equalsIgnoreCase(userDetailPref.getStringPref(getActivity(),AppConfigTags.HOSPITAL_ACCESS_PIN))) {
                                showDialog();
                            }else{
                                Toast.makeText(getActivity(),"Invalid Password",Toast.LENGTH_LONG).show();
                            }
                            // Do something
                        }
                    }).show();
        }

    private void fragmentreload() {
        Fragment ThankuFragment = new StartFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ThankuFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void languageChangeHindi() {
         String languageToLoad = "hi"; // your language
         Locale locale = new Locale (languageToLoad);
         Locale.setDefault (locale);
         Configuration config = new Configuration ();
         config.locale = locale;
         getActivity ().getResources ().updateConfiguration (config, getActivity ().getResources ().getDisplayMetrics ());

    }

    private void languageChangeEnglish() {
        String languageToLoad = "en"; // your language
        Locale locale = new Locale (languageToLoad);
        Locale.setDefault (locale);
        Configuration config = new Configuration ();
        config.locale = locale;
        getActivity ().getResources ().updateConfiguration (config, getActivity ().getResources ().getDisplayMetrics ());
    }

    private void initData() {
        switch (Constants.language) {
                    case "hi":
                    tvThanku.setText(userDetailPref.getStringPref(getActivity(),AppConfigTags.HOSPITAL_NAME)+ " " +getResources().getString(R.string.welcome));
                    tvStart.setVisibility(View.VISIBLE);
                    tvStart.setText(R.string.start);
                    break;

                    case "en":
                        tvThanku.setText(getResources().getString(R.string.welcome) + " " + userDetailPref.getStringPref(getActivity(), AppConfigTags.HOSPITAL_NAME));
                        tvStart.setVisibility(View.VISIBLE);
                        tvStart.setText(R.string.start);
                        break;
                }
    }

    private void setPreferences(String language) {
        userDetailsPref = UserDetailsPref.getInstance ();
        userDetailsPref.putStringPref(getActivity(),UserDetailsPref.LANGUAGE,language);

    }

    public void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_settings, null);
        dialogBuilder.setView(dialogView);
        final TextView tvSubscriptionExpiry = (TextView) dialogView.findViewById(R.id.tvSubscriptionExpiry);
        final TextView tvCategory = (TextView) dialogView.findViewById(R.id.tvCategory);
        final TextView tvExpiry = (TextView) dialogView.findViewById(R.id.tvExpiry);
        final TextView tvSubscriptionStatus = (TextView)dialogView.findViewById(R.id.tvSubscriptionStatus);
        final TextView tvStatus = (TextView)dialogView.findViewById(R.id.tvStatus);
        tvStatus.setTypeface (SetTypeFace.getTypeface (getActivity()));
        tvSubscriptionStatus.setTypeface (SetTypeFace.getTypeface (getActivity()));
        tvSubscriptionExpiry.setTypeface (SetTypeFace.getTypeface (getActivity()));
        final MaterialSpinner spCategory = (MaterialSpinner) dialogView.findViewById(R.id.spCategory);
        tvSubscriptionStatus.setText(userDetailPref.getStringPref(getActivity(),AppConfigTags.SUBSCRIPTION_STATUS));
        tvCategory.setTypeface (SetTypeFace.getTypeface (getActivity()));
        tvSubscriptionExpiry.setTypeface (SetTypeFace.getTypeface (getActivity()));
        tvExpiry.setTypeface(SetTypeFace.getTypeface (getActivity()));

        setSubscriptionExpiryDate(tvSubscriptionExpiry);

        addSurveyTypeList.clear();
        for(int i = 0; i < Constants.surveyTypeList.size(); i++) {
            surveytype = Constants.surveyTypeList.get(i);
            addSurveyTypeList.add(surveytype.getSurvey_type());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, addSurveyTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
        spCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                     surveytype = Constants.surveyTypeList.get(i);
                     Constants.survey_type = String.valueOf(surveytype.getSurvey_type_id());
                     userDetailPref.putStringPref(getActivity(),UserDetailsPref.SURVEY_TYPE,Constants.survey_type);
                        if(userDetailPref.getStringPref(getActivity(),UserDetailsPref.SURVEY_TYPE).equalsIgnoreCase("2")){
                            tvSurveyType.setText("OP");
                        }else{
                            tvSurveyType.setText("IN");
                        }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }
        });
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //pass
            }
        });
        dialogBuilder.setNeutralButton("LOGOUT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss ();
                UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                LogOutFromDevice(userDetailPref.getStringPref (getActivity(), UserDetailsPref.DEVICE_ID));
                userDetailsPref.putStringPref (getActivity(), userDetailsPref.DEVICE_ID, "");
                userDetailsPref.putStringPref (getActivity(), userDetailsPref.DEVICE_LOCATION, "");
                userDetailsPref.putStringPref (getActivity(), userDetailsPref.HOSPITAL_NAME, "");
                userDetailsPref.putStringPref(getActivity(), userDetailsPref.HOSPITAL_LOGIN_KEY, "");
                userDetailsPref.putStringPref (getActivity(), userDetailsPref.HOSPITAL_ACCESS_PIN, "");
                userDetailsPref.putStringPref (getActivity(), userDetailsPref.SUBSCRIPTION_STATUS, "");
                userDetailsPref.putStringPref (getActivity(), userDetailsPref.SUBSCRIPTION_STARTS, "");
                userDetailsPref.putStringPref (getActivity(), userDetailsPref.SUBSCRIPTION_EXPIRES, "");
                userDetailPref.putStringPref(getActivity(),UserDetailsPref.SURVEY_TYPE,"1");
                Intent intent = new Intent (getActivity(), LoginActivity.class);
                intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity (intent);
                getActivity().overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    private void setSubscriptionExpiryDate(TextView tvSubscriptionExpiry) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(userDetailPref.getStringPref(getActivity(),AppConfigTags.SUBSCRIPTION_EXPIRES));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy");
        tvSubscriptionExpiry.setText(timeFormat.format(myDate));
        Log.e("Date",""+timeFormat.format(myDate));

    }

    private void LogOutFromDevice(final String device_id) {
        if (NetworkConnection.isNetworkAvailable (getActivity())) {
           // mDialog = ProgressDialog.show(getActivity(),"", "Logout from other device..", true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_LOGOUT, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_LOGOUT,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, "" + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);

                                   // mDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.DEVICE_ID, device_id);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, Constants.hospital_login_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {

            Toast.makeText(getActivity(),"Seems like there is no internet connection, the app will continue in Offline mode",Toast.LENGTH_LONG).show();
        }
    }

    private void getQuestionFromServer(final String lang) {
        if (NetworkConnection.isNetworkAvailable (getActivity())) {
            mDialog = ProgressDialog.show(getActivity(),"", "Loading...", true);
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_GETQUESTION+"/"+userDetailPref.getStringPref(getActivity(),AppConfigTags.DEVICE_ID)+"/"+userDetailPref.getStringPref (getActivity(), UserDetailsPref.LANGUAGE)+"/"+Constants.survey_type+"/"+Constants.patient_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_GETQUESTION+"/"+userDetailPref.getStringPref(getActivity(),AppConfigTags.DEVICE_ID)+"/"+userDetailPref.getStringPref (getActivity(), UserDetailsPref.LANGUAGE)+"/"+Constants.survey_type+"/"+Constants.patient_id,

                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            QuestionList.clear ();
                            int json_array_len = 0;
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.SURVEY_DETAILS);
                                    Constants.survey_id = jsonObj.getString("survey_id");
                                    Log.e("SURVEY_ID",""+Constants.survey_id);
                                    json_array_len = jsonArray.length ();
                                    for (int i = 0; i < json_array_len; i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                                        Question question = new Question ();
                                        question.setId (jsonObject.getInt (AppConfigTags.QUESTION_ID));
                                        question.setQuestion_name (new String(jsonObject.getString (AppConfigTags.QUESTION_TEXT).getBytes("ISO-8859-1"), "UTF-8"));
                                        question.setQuestion_category_id (new String(jsonObject.getString (AppConfigTags.QUESTION_CATEGORY_ID).getBytes("ISO-8859-1"), "UTF-8"));
                                        JSONArray jsonarrayROW = jsonObject.getJSONArray(AppConfigTags.OPTIONS);
                                        Log.e("LENGTH",""+jsonarrayROW.length());
                                        for(int j = 0; j < jsonarrayROW.length(); j++) {
                                            JSONObject jsonObjectNew = jsonarrayROW.getJSONObject (j);
                                            SurveyResponse surveyResponse = new SurveyResponse();
                                            surveyResponse.setId(jsonObjectNew.getInt("option_id"));
                                            surveyResponse.setResponse(new String(jsonObjectNew.getString("option_text").getBytes("ISO-8859-1"), "UTF-8").toUpperCase());
                                            question.addListOption(surveyResponse);
                                        }
                                        MainActivity.QuestionList.add (question);
                                        mDialog.dismiss();
                                    }
                                    fragment();
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
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
                            //  listViewAllAtm.setVisibility (View.VISIBLE);
                            //  getAtmListFromLocalDatabase ();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, Constants.hospital_login_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 30);

        } else {
            // listViewAllAtm.setVisibility (View.VISIBLE);
            //  getAtmListFromLocalDatabase ();
            Toast.makeText(getActivity(),"Seems like there is no internet connection, the app will continue in Offline mode",Toast.LENGTH_LONG).show();
        }
    }

    private void fragment() {
        Fragment QuestionFragment = new QuestionFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, QuestionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
