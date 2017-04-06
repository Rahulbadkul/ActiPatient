package actiknow.com.actipatient.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import actiknow.com.actipatient.R;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.QuestionOption;
import actiknow.com.actipatient.model.SurveyResponse;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.SetTypeFace;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;

import static actiknow.com.actipatient.utils.Constants.patient_id;

public class SurveyActivity extends AppCompatActivity {
    LinearLayout llButtons;
    LinearLayout llSmiley;
    TextView tvQues;
    Question question;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    ImageView ivVeryHappy;
    ImageView ivHappy;
    ImageView ivNeutral;
    ImageView ivSad;
    ImageView ivVerySad;
    SurveyResponse surveyResponse = new SurveyResponse ();
    int noques = 1;
    UserDetailsPref userDetailsPref;
    ImageView imLogo;

    LinearLayout llMain;
    LinearLayout llThankYou;
    CoordinatorLayout clMain;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_survey);
        initView ();
        initData ();
        initListener ();
    }

    private void initView () {
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        llMain = (LinearLayout) findViewById (R.id.llMain);
        llThankYou = (LinearLayout) findViewById (R.id.llThankYou);
        tvQues = (TextView) findViewById (R.id.tvQues);
        llButtons = (LinearLayout) findViewById (R.id.llButtons);
        llSmiley = (LinearLayout) findViewById (R.id.llSmiley);
        tv1 = (TextView) findViewById (R.id.tv1);
        tv2 = (TextView) findViewById (R.id.tv2);
        tv3 = (TextView) findViewById (R.id.tv3);
        tv4 = (TextView) findViewById (R.id.tv4);
        tv5 = (TextView) findViewById (R.id.tv5);
        imLogo = (ImageView) findViewById (R.id.ivHospitalLogo);
        ivVeryHappy = (ImageView) findViewById (R.id.imExtremelyHappy);
        ivHappy = (ImageView) findViewById (R.id.imHappy);
        ivNeutral = (ImageView) findViewById (R.id.imOk);
        ivSad = (ImageView) findViewById (R.id.imSad);
        ivVerySad = (ImageView) findViewById (R.id.imExtremelySad);
    }

    private void initData () {
        progressDialog = new ProgressDialog (this);
        userDetailsPref = UserDetailsPref.getInstance ();
        Utils.setTypefaceToAllViews (this, tv1);
        Picasso.with (this).load (userDetailsPref.getStringPref (this, UserDetailsPref.HOSPITAL_LOGO)).into (imLogo);

        Constants.surveyResponseList.clear ();

        llSmiley.setVisibility (View.VISIBLE);
        llButtons.setVisibility (View.GONE);

//        Log.e ("Questionsize", "" + MainActivity.QuestionList.size ());
        question = MainActivity.QuestionList.get (0);
//        Log.e ("sud", "" + question.getQuestion_category_id ());
//        Log.e ("question", "" + question.getQuestion_text ());

        tvQues.setText (question.getQuestion_text ());

        for (int j = 0; j < question.getQuestionOptionList ().size (); j++) {
            QuestionOption surveyresponse = question.getQuestionOptionList ().get (j);
            switch (j) {
                case 0:
                    ivVeryHappy.setId (surveyresponse.getOption_id ());
                    break;
                case 1:
                    ivHappy.setId (surveyresponse.getOption_id ());
                    break;
                case 2:
                    ivNeutral.setId (surveyresponse.getOption_id ());
                    break;
                case 3:
                    ivSad.setId (surveyresponse.getOption_id ());
                    break;
                case 4:
                    ivVerySad.setId (surveyresponse.getOption_id ());
                    break;
            }
        }
    }

    private void initListener () {
        ivVeryHappy.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (ivVeryHappy.getId ());
//                Log.e ("Answer", "" + ivVeryHappy.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                getNewData ();
            }
        });
        ivHappy.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (ivHappy.getId ());
//                Log.e ("Answer", "" + ivHappy.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                getNewData ();
            }
        });
        ivNeutral.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (ivNeutral.getId ());
//                Log.e ("Answer", "" + ivNeutral.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                getNewData ();
            }
        });
        ivSad.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (ivSad.getId ());
//                Log.e ("Answer", "" + ivSad.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                getNewData ();
            }
        });
        ivVerySad.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (ivVerySad.getId ());
//                Log.e ("Answer", "" + ivVerySad.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                getNewData ();
            }
        });
    }

    private void getNewData () {
        llSmiley.setVisibility (View.GONE);
        llButtons.setVisibility (View.VISIBLE);
//        Log.e ("Questionnumber", "" + noques);
        question = MainActivity.QuestionList.get (noques);
//        Log.e ("karman", "" + question.getQuestion_text ());
        tvQues.setText (question.getQuestion_text ());
//        Log.e ("sud", "" + question.getQuestion_category_id ());
//        Log.e ("Karman", "Size" + question.getQuestionOptionList ().size ());
        if (question.getQuestionOptionList ().size () > 4) {
            tv2.setBackgroundResource (R.drawable.button_blue);
            tv3.setBackgroundResource (R.drawable.button_yellow);
            tv4.setBackgroundResource (R.drawable.button_pink);
            tv5.setBackgroundResource (R.drawable.button_red);
            llButtons.setWeightSum (5);
            tv3.setVisibility (View.VISIBLE);
            tv4.setVisibility (View.VISIBLE);
            tv5.setVisibility (View.VISIBLE);
        } else if (question.getQuestionOptionList ().size () > 3) {
            tv2.setBackgroundResource (R.drawable.button_blue);
            tv3.setBackgroundResource (R.drawable.button_yellow);
            tv4.setBackgroundResource (R.drawable.button_red);
            llButtons.setWeightSum (4);
            tv3.setVisibility (View.VISIBLE);
            tv4.setVisibility (View.VISIBLE);
        } else if (question.getQuestionOptionList ().size () > 2) {
            tv2.setBackgroundResource (R.drawable.button_yellow);
            tv3.setBackgroundResource (R.drawable.button_red);
            llButtons.setWeightSum (3);
            tv4.setVisibility (View.GONE);
            tv3.setVisibility (View.VISIBLE);
        } else {
            tv2.setBackgroundResource (R.drawable.button_red);
            llButtons.setWeightSum (2);
            tv4.setVisibility (View.GONE);
            tv3.setVisibility (View.GONE);
        }
        for (int j = 0; j < question.getQuestionOptionList ().size (); j++) {
            QuestionOption surveyresponse = question.getQuestionOptionList ().get (j);
//            Log.e ("Karman", "12" + surveyresponse.getOption_text ());
            switch (j) {
                case 0:
                    tv1.setText (surveyresponse.getOption_text ());
                    tv1.setId (surveyresponse.getOption_id ());
//                    Log.e ("ResponseId", "" + surveyresponse.getOption_id ());
                    break;
                case 1:
                    tv2.setText (surveyresponse.getOption_text ());
                    tv2.setId (surveyresponse.getOption_id ());
                    break;
                case 2:
                    tv3.setText (surveyresponse.getOption_text ());
                    tv3.setId (surveyresponse.getOption_id ());
                    break;
                case 3:
                    tv4.setText (surveyresponse.getOption_text ());
                    tv4.setId (surveyresponse.getOption_id ());
                    break;
                case 4:
                    tv5.setText (surveyresponse.getOption_text ());
                    tv5.setId (surveyresponse.getOption_id ());
                    break;
            }
        }

        tv1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (tv1.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                if (noques == MainActivity.QuestionList.size () - 1) {
                    showInputDialog ();
                    //noques++;
                    //getNewData();
                } else {
                    noques++;
                    getNewData ();
                }

            }
        });
        tv2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (tv2.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                if (noques == MainActivity.QuestionList.size () - 1) {
                    showInputDialog ();
                    //noques++;
                    //getNewData();
                } else {
                    noques++;
                    getNewData ();
                }
            }
        });
        tv3.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (tv3.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                if (noques == MainActivity.QuestionList.size () - 1) {
                    showInputDialog ();
                    //noques++;
                    //getNewData();
                } else {
                    noques++;
                    getNewData ();
                }
            }
        });
        tv4.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (tv4.getId ());
                Constants.surveyResponseList.add (surveyResponse);

                if (noques == MainActivity.QuestionList.size () - 1) {
                    showInputDialog ();
                    //noques++;
                    //getNewData();
                } else {
                    noques++;
                    getNewData ();
                }
            }
        });
        tv5.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (tv5.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                if (noques == MainActivity.QuestionList.size () - 1) {
                    showInputDialog ();
                    //noques++;
                    //getNewData();
                } else {
                    noques++;
                    getNewData ();

                }
            }
        });
    }

    public void showInputDialog () {
        final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder (this)
                .title (R.string.dialog_text_would_you_like_to_comment)
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .inputRangeRes (0, 140, R.color.input_error_colour)
                .alwaysCallInputCallback ()
                .canceledOnTouchOutside (false)
                .cancelable (false)
                .positiveText (R.string.dialog_action_submit);

        mBuilder.input (getResources ().getString (R.string.dialog_hint_optional), null, new MaterialDialog.InputCallback () {
            @Override
            public void onInput (MaterialDialog dialog, CharSequence input) {
                // Do something
                if (input.toString ().length () == 0) {
                    mBuilder.positiveText (R.string.dialog_action_submit);
                } else {
                    mBuilder.positiveText (R.string.dialog_action_submit);
                }
            }
        });

        mBuilder.onPositive (new MaterialDialog.SingleButtonCallback () {
            @Override
            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                UploadResponseToServer (dialog.getInputEditText ().getText ().toString (), getResponsesJSON (), String.valueOf (patient_id));
            }
        });
        mBuilder.show ();
    }

    private String getResponsesJSON () {
        JSONObject jsonObject = new JSONObject ();
        JSONArray jsonArray = new JSONArray ();
        for (int i = 0; i < Constants.surveyResponseList.size (); i++) {
            SurveyResponse surveyResponse = Constants.surveyResponseList.get (i);
            JSONObject jsonObj = new JSONObject ();
            try {
                jsonObj.put (AppConfigTags.QUESTION_CATEGORY_ID, surveyResponse.getQuestion_category_id ());
                jsonObj.put (AppConfigTags.QUESTION_ID, surveyResponse.getQuestion_id ());
                jsonObj.put (AppConfigTags.OPTION_ID, surveyResponse.getOption_id ());
                jsonArray.put (jsonObj);
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
        try {
            jsonObject.put (AppConfigTags.RESPONSES, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return jsonObject.toString ();
    }

    private void UploadResponseToServer (final String comment, final String array, final String patient_id) {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_submitting_responses), false);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_POSTSURVEY + "/" + Constants.survey_id, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_POSTSURVEY + "/" + Constants.survey_id,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        llMain.setVisibility (View.GONE);
                                        llThankYou.setVisibility (View.VISIBLE);
                                        Utils.showSnackBar (SurveyActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        final Handler handler = new Handler ();
                                        handler.postDelayed (new Runnable () {
                                            @Override
                                            public void run () {
                                                finish ();
                                                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                            }
                                        }, 4000);
                                    } else {
                                        Utils.showSnackBar (SurveyActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_retry), new View.OnClickListener () {
                                            @Override
                                            public void onClick (View v) {
                                                UploadResponseToServer (comment, array, patient_id);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                    Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    progressDialog.dismiss ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                                Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.PATIENT_ID, patient_id);
                    params.put (AppConfigTags.RESPONSES_JSON, array);
                    params.put (AppConfigTags.COMMENTS, comment);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailsPref.getStringPref (SurveyActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
            Utils.showSnackBar (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent (android.provider.Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }

    @Override
    public void onBackPressed () {
        new MaterialDialog.Builder (this)
                .content (R.string.dialog_text_quit_survey)
                .positiveColor (getResources ().getColor (R.color.colorPrimary))
                .contentColor (getResources ().getColor (R.color.colorPrimary))
                .negativeColor (getResources ().getColor (R.color.colorPrimary))
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .canceledOnTouchOutside (false)
                .cancelable (false)
                .positiveText (R.string.dialog_action_yes)
                .negativeText (R.string.dialog_action_no)
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish ();
                        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }).show ();
    }
}
