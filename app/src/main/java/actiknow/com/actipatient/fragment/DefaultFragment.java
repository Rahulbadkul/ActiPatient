package actiknow.com.actipatient.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import actiknow.com.actipatient.R;
import actiknow.com.actipatient.activity.LoginActivity;
import actiknow.com.actipatient.activity.MainActivity;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.QuestionOptions;
import actiknow.com.actipatient.model.SurveyType;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.SetTypeFace;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;
import fr.ganfra.materialspinner.MaterialSpinner;

import static actiknow.com.actipatient.activity.MainActivity.QuestionList;
import static android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by actiknow on 3/10/17.
 */

public class DefaultFragment extends Fragment {
    TextView tvThanku;
    TextView tvStart;
    ImageView ivSettings;
    TextView tvHindi;
    TextView tvEnglish;
    TextView tvSurveyType;
    ImageView ivLogo;
    String english_language_code = "en";
    String hindi_language_code = "hi";

    UserDetailsPref userDetailPref;
    ProgressDialog progressDialog;
    CoordinatorLayout clMain;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.fragment_start, null);
        initView (v);
        initData ();
        getPref ();
        initListener ();
        return v;
    }

    private void initView (View v) {
        clMain = (CoordinatorLayout) v.findViewById (R.id.clMain);
        tvThanku = (TextView) v.findViewById (R.id.tvThanku);
        tvStart = (TextView) v.findViewById (R.id.tvStart);
        ivSettings = (ImageView) v.findViewById (R.id.imSettings);
        tvHindi = (TextView) v.findViewById (R.id.tvHindi);
        tvEnglish = (TextView) v.findViewById (R.id.tvEnglish);
        tvSurveyType = (TextView) v.findViewById (R.id.tvSurveyType);
        ivLogo = (ImageView) v.findViewById (R.id.imLogo);
    }

    private void initData () {
        userDetailPref = UserDetailsPref.getInstance ();
        progressDialog = new ProgressDialog (getActivity ());
        Utils.setTypefaceToAllViews (getActivity (), tvThanku);
        try {
            Picasso.with (getActivity ()).load (userDetailPref.getStringPref (getActivity (), UserDetailsPref.HOSPITAL_LOGO)).into (ivLogo);
        } catch (Exception e){
            e.printStackTrace ();
        }
        switch (userDetailPref.getStringPref (getActivity (), UserDetailsPref.LANGUAGE)) {
            case "hi":
                tvThanku.setText (userDetailPref.getStringPref (getActivity (), AppConfigTags.HOSPITAL_NAME) + " " + getResources ().getString (R.string.welcome));
                tvStart.setText (R.string.start);
                changeLanguage (hindi_language_code);
                tvEnglish.setBackgroundResource (R.drawable.button_white);
                tvHindi.setBackgroundResource (R.drawable.button_green);
                break;
            case "en":
                tvThanku.setText (getResources ().getString (R.string.welcome) + " " + userDetailPref.getStringPref (getActivity (), AppConfigTags.HOSPITAL_NAME));
                tvStart.setText (R.string.start);
                changeLanguage (english_language_code);
                tvHindi.setBackgroundResource (R.drawable.button_white);
                tvEnglish.setBackgroundResource (R.drawable.button_green);
                break;
        }
    }

    private void getPref () {

        for (int i = 0; i < Constants.surveyTypeList.size (); i++) {
            SurveyType surveyType = Constants.surveyTypeList.get (i);
            if (surveyType.getSurvey_type_id () == userDetailPref.getIntPref (getActivity (), UserDetailsPref.SURVEY_TYPE_ID)) {
                tvSurveyType.setText (surveyType.getSurvey_type_text ());
            }
        }

//        if(Constants.survey_type.equalsIgnoreCase("2")){
//            tvSurveyType.setText("OP");
//        }else{
//            tvSurveyType.setText("IP");
//        }

        if (Constants.status.equalsIgnoreCase ("thanku")) {
            tvStart.setVisibility (View.GONE);
            tvThanku.setText (R.string.thanku);
            Constants.status = "";
        }

    }


    private void initListener () {
        ivSettings.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                showAccessPINDialog ();
            }
        });
        tvStart.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                generateSurvey ();
            }
        });
        tvHindi.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                changeLanguage (hindi_language_code);
                tvThanku.setText (userDetailPref.getStringPref (getActivity (), AppConfigTags.HOSPITAL_NAME) + " " + getResources ().getString (R.string.welcome));
                tvEnglish.setBackgroundResource (R.drawable.button_white);
                tvHindi.setBackgroundResource (R.drawable.button_green);
                fragmentreload ();

            }
        });
        tvEnglish.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                changeLanguage (english_language_code);
                tvThanku.setText (getResources ().getString (R.string.welcome) + " " + userDetailPref.getStringPref (getActivity (), AppConfigTags.HOSPITAL_NAME));
                tvHindi.setBackgroundResource (R.drawable.button_white);
                tvEnglish.setBackgroundResource (R.drawable.button_green);
                fragmentreload ();
            }
        });
    }

    private void showAccessPINDialog () {
        final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder (getActivity ())
                .title ("Please enter your Access PIN")
                .typeface (SetTypeFace.getTypeface (getActivity ()), SetTypeFace.getTypeface (getActivity ()))
                .positiveText ("OK");
//                .alwaysCallInputCallback ();

        mBuilder.input ("", "", new MaterialDialog.InputCallback () {
            @Override
            public void onInput (MaterialDialog dialog, CharSequence input) {
                if (input.toString ().length () == String.valueOf (userDetailPref.getIntPref (getActivity (), AppConfigTags.HOSPITAL_ACCESS_PIN)).length () && Integer.parseInt (input.toString ()) == userDetailPref.getIntPref (getActivity (), AppConfigTags.HOSPITAL_ACCESS_PIN)){
                    Utils.hideSoftKeyboard (getActivity ());
                    dialog.dismiss ();
                    showSettingDialog ();
                } else {
                    Utils.hideSoftKeyboard (getActivity ());
                    Utils.showSnackBar (getActivity (), clMain, "Invalid Access PIN", Snackbar.LENGTH_LONG, null, null);
                }
            }
        }).show ();
    }

    private void fragmentreload () {
        Fragment ThankuFragment = new DefaultFragment ();
        FragmentManager fragmentManager = getActivity ().getFragmentManager ();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ();
        fragmentTransaction.replace (R.id.fragment_container, ThankuFragment);
        fragmentTransaction.addToBackStack (null);
        fragmentTransaction.commit ();
    }


    private void changeLanguage (String language) {
        userDetailPref.putStringPref (getActivity (), UserDetailsPref.LANGUAGE, language);
        String languageToLoad = language; // your language
        Locale locale = new Locale (languageToLoad);
        Locale.setDefault (locale);
        Configuration config = new Configuration ();
        config.locale = locale;
        getActivity ().getResources ().updateConfiguration (config, getActivity ().getResources ().getDisplayMetrics ());
    }

    public static int survey_type_id_temp = 0;

    public void showSettingDialog () {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder (getActivity ());
        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        final View dialogView = inflater.inflate (R.layout.dialog_settings, null);
        dialogBuilder.setView (dialogView);
        final TextView tvSubscriptionExpiry = (TextView) dialogView.findViewById (R.id.tvSubscriptionExpiry);
        final TextView tvCategory = (TextView) dialogView.findViewById (R.id.tvCategory);
        final TextView tvExpiry = (TextView) dialogView.findViewById (R.id.tvExpiry);
        final TextView tvSubscriptionStatus = (TextView) dialogView.findViewById (R.id.tvSubscriptionStatus);
        final TextView tvStatus = (TextView) dialogView.findViewById (R.id.tvStatus);

        Utils.setTypefaceToAllViews (getActivity (), tvCategory);
        final MaterialSpinner spCategory = (MaterialSpinner) dialogView.findViewById (R.id.spCategory);

        tvSubscriptionStatus.setText (userDetailPref.getStringPref (getActivity (), UserDetailsPref.SUBSCRIPTION_STATUS));

        tvSubscriptionExpiry.setText (Utils.convertTimeFormat (userDetailPref.getStringPref (getActivity (), UserDetailsPref.SUBSCRIPTION_EXPIRES), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy"));

        ArrayList<String> surveyTypes = new ArrayList<> ();
        SurveyType surveyType = new SurveyType ();

        surveyTypes.clear ();
        for (int i = 0; i < Constants.surveyTypeList.size (); i++) {
            surveyType = Constants.surveyTypeList.get (i);
            surveyTypes.add (surveyType.getSurvey_type_text ());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String> (getActivity (), android.R.layout.simple_spinner_item, surveyTypes);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter (adapter);
        spCategory.setOnItemSelectedListener (new OnItemSelectedListener () {
            @Override
            public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) {
                SurveyType surveyType = Constants.surveyTypeList.get (i);
                survey_type_id_temp = surveyType.getSurvey_type_id ();
            }

            @Override
            public void onNothingSelected (AdapterView<?> adapterView) {
            }
        });

        dialogBuilder.setPositiveButton ("OK", new DialogInterface.OnClickListener () {
            public void onClick (DialogInterface dialog, int whichButton) {
                for (int i=0;i<Constants.surveyTypeList.size ();i++){
                    SurveyType surveyType = Constants.surveyTypeList.get (i);
                    if(surveyType.getSurvey_type_id () == survey_type_id_temp){
                        userDetailPref.putIntPref (getActivity (), UserDetailsPref.SURVEY_TYPE_ID, surveyType.getSurvey_type_id ());
                        tvSurveyType.setText (surveyType.getSurvey_type_text ());
                    }
                }
            }
        });

        dialogBuilder.setNegativeButton ("CANCEL", new DialogInterface.OnClickListener () {
            public void onClick (DialogInterface dialog, int whichButton) {
            }
        });

        dialogBuilder.setNeutralButton ("LOGOUT", new DialogInterface.OnClickListener () {
            public void onClick (DialogInterface dialog, int whichButton) {
                logOutFromDevice (userDetailPref.getIntPref (getActivity (), UserDetailsPref.DEVICE_ID));
            }
        });
        AlertDialog b = dialogBuilder.create ();
        b.show ();
    }

    private void logOutFromDevice (final int device_id) {
        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
            Utils.showProgressDialog (progressDialog, "Logging Out...", true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_LOGOUT, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_LOGOUT,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, "" + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);

                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);

                                    userDetailPref.putIntPref (getActivity (), UserDetailsPref.DEVICE_ID, 0);
                                    userDetailPref.putStringPref (getActivity (), UserDetailsPref.DEVICE_LOCATION, "");
                                    userDetailPref.putStringPref (getActivity (), UserDetailsPref.HOSPITAL_NAME, "");
                                    userDetailPref.putStringPref (getActivity (), UserDetailsPref.HOSPITAL_LOGIN_KEY, "");
                                    userDetailPref.putIntPref (getActivity (), UserDetailsPref.HOSPITAL_ACCESS_PIN, 0);
                                    userDetailPref.putStringPref (getActivity (), UserDetailsPref.SUBSCRIPTION_STATUS, "");
                                    userDetailPref.putStringPref (getActivity (), UserDetailsPref.SUBSCRIPTION_STARTS, "");
                                    userDetailPref.putStringPref (getActivity (), UserDetailsPref.SUBSCRIPTION_EXPIRES, "");
                                    userDetailPref.putIntPref (getActivity (), UserDetailsPref.SURVEY_TYPE_ID, 0);
                                    Intent intent = new Intent (getActivity (), LoginActivity.class);
                                    intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity (intent);
                                    getActivity ().overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            progressDialog.dismiss ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.DEVICE_ID, String.valueOf (device_id));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailPref.getStringPref (getActivity (), UserDetailsPref.HOSPITAL_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
            progressDialog.dismiss ();
            Utils.showSnackBar (getActivity (), clMain, "No Internet Connection available", Snackbar.LENGTH_LONG, "GO TO SETTINGS", new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent (android.provider.Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }

    private void generateSurvey () {
        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
            Utils.showProgressDialog (progressDialog, "Loading...", true);
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_GETQUESTION + "/" + userDetailPref.getStringPref (getActivity (), AppConfigTags.DEVICE_ID) + "/" + userDetailPref.getStringPref (getActivity (), UserDetailsPref.LANGUAGE) + "/" + userDetailPref.getIntPref (getActivity (), UserDetailsPref.SURVEY_TYPE_ID) + "/" + Constants.patient_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_GETQUESTION + "/" + userDetailPref.getStringPref (getActivity (), AppConfigTags.DEVICE_ID) + "/" + userDetailPref.getStringPref (getActivity (), UserDetailsPref.LANGUAGE) + "/" + userDetailPref.getIntPref (getActivity (), UserDetailsPref.SURVEY_TYPE_ID) + "/" + Constants.patient_id,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            QuestionList.clear ();
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.SURVEY_DETAILS);
                                    Constants.survey_id = jsonObj.getString ("survey_id");
                                    for (int i = 0; i < jsonArray.length (); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                                        Question question = new Question ();
                                        question.setQuestion_id (jsonObject.getInt (AppConfigTags.QUESTION_ID));
                                        question.setQuestion_text (new String (jsonObject.getString (AppConfigTags.QUESTION_TEXT).getBytes ("ISO-8859-1"), "UTF-8"));
                                        question.setQuestion_category_id (jsonObject.getInt (AppConfigTags.QUESTION_CATEGORY_ID));
                                        JSONArray jsonArrayOptions = jsonObject.getJSONArray (AppConfigTags.OPTIONS);
                                        for (int j = 0; j < jsonArrayOptions.length (); j++) {
                                            JSONObject jsonObjectNew = jsonArrayOptions.getJSONObject (j);
                                            QuestionOptions questionOptions = new QuestionOptions ();
                                            questionOptions.setOption_id (jsonObjectNew.getInt ("option_id"));
                                            questionOptions.setOption_text (new String (jsonObjectNew.getString ("option_text").getBytes ("ISO-8859-1"), "UTF-8").toUpperCase ());
                                            question.addQuestionOption (questionOptions);
                                        }
                                        MainActivity.QuestionList.add (question);
                                    }
                                    progressDialog.dismiss ();
                                    loadQuestionsFragment ();
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                } catch (UnsupportedEncodingException e) {
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
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailsPref.getStringPref (getActivity (), UserDetailsPref.HOSPITAL_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 30);
        } else {
            Utils.showSnackBar (getActivity (), clMain, "No Internet Connection available", Snackbar.LENGTH_LONG, "GO TO SETTINGS", new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent (android.provider.Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }

    private void loadQuestionsFragment () {
        Fragment QuestionFragment = new QuestionFragment ();
        FragmentManager fragmentManager = getActivity ().getFragmentManager ();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ();
        fragmentTransaction.replace (R.id.fragment_container, QuestionFragment);
        fragmentTransaction.addToBackStack (null);
        fragmentTransaction.commit ();

    }
}
