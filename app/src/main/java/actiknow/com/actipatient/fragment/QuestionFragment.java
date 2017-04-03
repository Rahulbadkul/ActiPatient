package actiknow.com.actipatient.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import actiknow.com.actipatient.activity.MainActivity;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.QuestionOptions;
import actiknow.com.actipatient.model.SurveyResponse;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.SetTypeFace;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;

import static actiknow.com.actipatient.utils.Constants.patient_id;

public class QuestionFragment extends Fragment {
    LinearLayout llbutton;
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

    CoordinatorLayout clMain;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.fragment_question, null);
        initView (v);
        initData ();
        backPress (v);
        return v;
    }

    private void initView (View v) {
        clMain = (CoordinatorLayout) v.findViewById (R.id.clMain);
        tvQues = (TextView) v.findViewById (R.id.tvQues);
        llbutton = (LinearLayout) v.findViewById (R.id.llbutton);
        llSmiley = (LinearLayout) v.findViewById (R.id.llSmiley);
        tv1 = (TextView) v.findViewById (R.id.tv1);
        tv2 = (TextView) v.findViewById (R.id.tv2);
        tv3 = (TextView) v.findViewById (R.id.tv3);
        tv4 = (TextView) v.findViewById (R.id.tv4);
        tv5 = (TextView) v.findViewById (R.id.tv5);
        imLogo = (ImageView) v.findViewById (R.id.imLogo);
        ivVeryHappy = (ImageView) v.findViewById (R.id.imExtremelyHappy);
        ivHappy = (ImageView) v.findViewById (R.id.imHappy);
        ivNeutral = (ImageView) v.findViewById (R.id.imOk);
        ivSad = (ImageView) v.findViewById (R.id.imSad);
        ivVerySad = (ImageView) v.findViewById (R.id.imExtremelySad);
    }

    private void initData () {
        userDetailsPref = UserDetailsPref.getInstance ();
        Utils.setTypefaceToAllViews (getActivity (), tv1);
        Picasso.with (getActivity ()).load (userDetailsPref.getStringPref (getActivity (), UserDetailsPref.HOSPITAL_LOGO)).into (imLogo);

        Constants.surveyResponseList.clear ();

        llSmiley.setVisibility (View.VISIBLE);
        llbutton.setVisibility (View.GONE);

        Log.e ("Questionsize", "" + MainActivity.QuestionList.size ());
        question = MainActivity.QuestionList.get (0);
        Log.e ("sud", "" + question.getQuestion_category_id ());
        Log.e ("question", "" + question.getQuestion_text ());

        tvQues.setText (question.getQuestion_text ());

        for (int j = 0; j < question.getQuestionOptionList ().size (); j++) {
            QuestionOptions surveyresponse = question.getQuestionOptionList ().get (j);
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

        ivVeryHappy.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                surveyResponse = new SurveyResponse ();
                surveyResponse.setQuestion_id (question.getQuestion_id ());
                surveyResponse.setQuestion_category_id (question.getQuestion_category_id ());
                surveyResponse.setOption_id (ivVeryHappy.getId ());
                Log.e ("Answer", "" + ivVeryHappy.getId ());
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
                Log.e ("Answer", "" + ivHappy.getId ());
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
                Log.e ("Answer", "" + ivNeutral.getId ());
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
                Log.e ("Answer", "" + ivSad.getId ());
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
                Log.e ("Answer", "" + ivVerySad.getId ());
                Constants.surveyResponseList.add (surveyResponse);
                getNewData ();
            }
        });
    }

    private void getNewData () {
        llSmiley.setVisibility (View.GONE);
        llbutton.setVisibility (View.VISIBLE);
        Log.e ("Questionnumber", "" + noques);
        question = MainActivity.QuestionList.get (noques);
        Log.e ("karman", "" + question.getQuestion_text ());
        tvQues.setText (question.getQuestion_text ());
        Log.e ("sud", "" + question.getQuestion_category_id ());
        Log.e ("Karman", "Size" + question.getQuestionOptionList ().size ());
        if (question.getQuestionOptionList ().size () > 4) {
            tv2.setBackgroundResource (R.drawable.button_light_blue);
            tv3.setBackgroundResource (R.drawable.button_light_yellow);
            tv4.setBackgroundResource (R.drawable.button_light_pink);
            tv5.setBackgroundResource (R.drawable.button_red);
            llbutton.setWeightSum (5);
            tv3.setVisibility (View.VISIBLE);
            tv4.setVisibility (View.VISIBLE);
            tv5.setVisibility (View.VISIBLE);
        } else if (question.getQuestionOptionList ().size () > 3) {
            tv2.setBackgroundResource (R.drawable.button_light_blue);
            tv3.setBackgroundResource (R.drawable.button_light_yellow);
            tv4.setBackgroundResource (R.drawable.button_red);
            llbutton.setWeightSum (4);
            tv3.setVisibility (View.VISIBLE);
            tv4.setVisibility (View.VISIBLE);
        } else if (question.getQuestionOptionList ().size () > 2) {
            tv2.setBackgroundResource (R.drawable.button_light_yellow);
            tv3.setBackgroundResource (R.drawable.button_red);
            llbutton.setWeightSum (3);
            tv4.setVisibility (View.GONE);
            tv3.setVisibility (View.VISIBLE);
        } else {
            tv2.setBackgroundResource (R.drawable.button_red);
            llbutton.setWeightSum (2);
            tv4.setVisibility (View.GONE);
            tv3.setVisibility (View.GONE);
        }
        for (int j = 0; j < question.getQuestionOptionList ().size (); j++) {
            QuestionOptions surveyresponse = question.getQuestionOptionList ().get (j);
            Log.e ("Karman", "12" + surveyresponse.getOption_text ());

            switch (j) {
                case 0:
                    tv1.setText (surveyresponse.getOption_text ());
                    tv1.setId (surveyresponse.getOption_id ());
                    Log.e ("ResponseId", "" + surveyresponse.getOption_id ());
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
        String comment;
        final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder (getActivity ())
                .title (R.string.comment)
                .typeface (SetTypeFace.getTypeface (getActivity ()), SetTypeFace.getTypeface (getActivity ()))
                .inputRangeRes (0, 140, R.color.fab_donate_pressed)
                .alwaysCallInputCallback ()
                .canceledOnTouchOutside (false)
                .cancelable (false)
                .positiveText ("SKIP");

        mBuilder.input (getResources ().getString (R.string.optional), null, new MaterialDialog.InputCallback () {
            @Override
            public void onInput (MaterialDialog dialog, CharSequence input) {
                // Do something

                if (input.toString ().length () == 0) {
                    mBuilder.positiveText ("SKIP");
                } else {
                    mBuilder.positiveText ("SUBMIT");
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
                Utils.showLog (Log.WARN, "EXCEPTION", "JSON Exception while generating ResponsesJSON", true);
            }
        }
        try {
            jsonObject.put ("responses", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return jsonObject.toString ();
    }

    private void UploadResponseToServer (final String comment, final String array, final String patient_id) {
        if (NetworkConnection.isNetworkAvailable (getActivity ())) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_POSTSURVEY + "/" + Constants.survey_id, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_POSTSURVEY + "/" + Constants.survey_id,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    defaultFragment ();
                                } catch (JSONException e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put ("patient_id", patient_id);
                    params.put ("responses_json", array);
                    params.put ("comments", comment);
                    return params;
                }

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
            Utils.sendRequest (strRequest1, 60);
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

    private void defaultFragment () {
        Constants.status = "thanku";
        FragmentManager fragmentManager = getFragmentManager ();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction ();
        DefaultFragment f11 = new DefaultFragment ();
        Bundle args = new Bundle ();
        args.putString (AppConfigTags.STATUS, "thanku");
        f11.setArguments (args);
        fragmentTransaction.replace (R.id.fragment_container, f11, "fragment11");
        fragmentTransaction.commit ();
    }

    private void backPress (View v) {
        v.setFocusableInTouchMode (true);
        v.requestFocus ();
        v.setOnKeyListener (new View.OnKeyListener () {
            @Override
            public boolean onKey (View v, int keyCode, KeyEvent event) {
                if (event.getAction () != KeyEvent.ACTION_DOWN) {
                    exitByBackKey ();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    protected void exitByBackKey () {
        new AlertDialog.Builder (getActivity ())
                .setMessage (R.string.quit)
                .setPositiveButton (R.string.yes, new DialogInterface.OnClickListener () {
                    // do something when the button is clicked
                    public void onClick (DialogInterface arg0, int arg1) {
                        FragmentManager fragmentManager = getFragmentManager ();
                        FragmentTransaction fragmentTransaction;
                        fragmentTransaction = fragmentManager.beginTransaction ();
                        DefaultFragment f11 = new DefaultFragment ();
                        fragmentTransaction.replace (R.id.fragment_container, f11, "fragment11");
                        fragmentTransaction.commit ();
                    }
                })
                .setNegativeButton (R.string.no, new DialogInterface.OnClickListener () {
                    public void onClick (DialogInterface arg0, int arg1) {
                    }
                })
                .show ();
    }
}