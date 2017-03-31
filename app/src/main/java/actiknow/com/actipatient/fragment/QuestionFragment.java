package actiknow.com.actipatient.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import actiknow.com.actipatient.activity.LoginActivity;
import actiknow.com.actipatient.activity.MainActivity;
import actiknow.com.actipatient.model.AnswerResponse;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.SurveyResponse;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.SetTypeFace;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;

import static actiknow.com.actipatient.R.id.imLogo;
import static actiknow.com.actipatient.utils.Constants.answerResponseList;
import static android.R.id.input;


/**
 * Created by actiknow on 3/16/17.
 */

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
    ImageView imExtremelyHappy;
    ImageView imHappy;
    ImageView imOk;
    ImageView imSad;
    ImageView imExtremelySad;
    JSONObject jsonObject1;
    JSONArray jsonArray3;
    String patient_id = "MH123456";
    AnswerResponse answerResponse = new AnswerResponse();
    int noques = 1;
    UserDetailsPref userDetailsPref;
    ImageView imLogo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question, null);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initView(v);
        getPref();
        initData();
        backPress(v);
        return v;

    }


    private void getPref() {
        userDetailsPref = UserDetailsPref.getInstance ();
        try {
            Picasso.with(getActivity()).load(userDetailsPref.getStringPref(getActivity(), UserDetailsPref.HOSPITAL_LOGO)).into(imLogo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        llSmiley.setVisibility(View.VISIBLE);
        llbutton.setVisibility(View.GONE);
        Log.e("Questionsize", "" + MainActivity.QuestionList.size());
        question = MainActivity.QuestionList.get(0);
        Log.e("sud", "" + question.getQuestion_category_id());
        Log.e("question", "" + question.getQuestion_name());

        tvQues.setText(question.getQuestion_name());

        for (int j = 0; j < question.getListOption().size(); j++) {
            SurveyResponse surveyresponse = question.getListOption().get(j);
            switch (j) {
                case 0:
                    imExtremelyHappy.setId(surveyresponse.getId());
                    break;
                case 1:
                    imHappy.setId(surveyresponse.getId());
                    break;
                case 2:
                    imOk.setId(surveyresponse.getId());
                    break;
                case 3:
                    imSad.setId(surveyresponse.getId());
                    break;
                case 4:
                    imExtremelySad.setId(surveyresponse.getId());
                    break;
            }
        }


        imExtremelyHappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerResponse = new AnswerResponse();
                answerResponse.setQuestion_id(question.getId());
                answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                answerResponse.setResponse_id(imExtremelyHappy.getId());
                Log.e("Answer", "" + imExtremelyHappy.getId());
                Constants.answerResponseList.add(answerResponse);
                getNewData();
            }
        });
        imHappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerResponse = new AnswerResponse();
                answerResponse.setQuestion_id(question.getId());
                answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                answerResponse.setResponse_id(imHappy.getId());
                Log.e("Answer", "" + imHappy.getId());
                Constants.answerResponseList.add(answerResponse);
                getNewData();
            }
        });
        imOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerResponse = new AnswerResponse();
                answerResponse.setQuestion_id(question.getId());
                answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                answerResponse.setResponse_id(imOk.getId());
                Log.e("Answer", "" + imOk.getId());
                Constants.answerResponseList.add(answerResponse);
                getNewData();
            }
        });
        imSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerResponse = new AnswerResponse();
                answerResponse.setQuestion_id(question.getId());
                answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                answerResponse.setResponse_id(imSad.getId());
                Log.e("Answer", "" + imSad.getId());
                Constants.answerResponseList.add(answerResponse);
                getNewData();
            }
        });
        imExtremelySad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerResponse = new AnswerResponse();
                answerResponse.setQuestion_id(question.getId());
                answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                answerResponse.setResponse_id(imExtremelySad.getId());
                Log.e("Answer", "" + imExtremelySad.getId());
                Constants.answerResponseList.add(answerResponse);
                getNewData();
            }
        });
    }

    private void getNewData() {
        llSmiley.setVisibility(View.GONE);
        llbutton.setVisibility(View.VISIBLE);
        Log.e("Questionnumber",""+noques);
            question = MainActivity.QuestionList.get(noques);
            Log.e("karman", "" + question.getQuestion_name());
            tvQues.setText(question.getQuestion_name());
        Log.e("sud", "" + question.getQuestion_category_id());
                Log.e("Karman", "Size" + question.getListOption().size());
        if(question.getListOption().size() > 4) {
            tv2.setBackgroundResource(R.drawable.button_light_blue);
            tv3.setBackgroundResource(R.drawable.button_light_yellow);
            tv4.setBackgroundResource(R.drawable.button_light_pink);
            tv5.setBackgroundResource(R.drawable.button_red);
            llbutton.setWeightSum(5);
            tv3.setVisibility(View.VISIBLE);
            tv4.setVisibility(View.VISIBLE);
            tv5.setVisibility(View.VISIBLE);
        }else if(question.getListOption().size() > 3) {
            tv2.setBackgroundResource(R.drawable.button_light_blue);
            tv3.setBackgroundResource(R.drawable.button_light_yellow);
            tv4.setBackgroundResource(R.drawable.button_red);
            llbutton.setWeightSum(4);
            tv3.setVisibility(View.VISIBLE);
            tv4.setVisibility(View.VISIBLE);
        }else if(question.getListOption().size() > 2){
            tv2.setBackgroundResource(R.drawable.button_light_yellow);
            tv3.setBackgroundResource(R.drawable.button_red);
            llbutton.setWeightSum(3);
            tv4.setVisibility(View.GONE);
            tv3.setVisibility(View.VISIBLE);
        }else{
            tv2.setBackgroundResource(R.drawable.button_red);
            llbutton.setWeightSum(2);
            tv4.setVisibility(View.GONE);
            tv3.setVisibility(View.GONE);
        }
            for (int j = 0; j < question.getListOption().size(); j++) {
                SurveyResponse surveyresponse = question.getListOption().get(j);
                Log.e("Karman", "12" + surveyresponse.getResponse());

                switch (j) {
                    case 0:
                        tv1.setText(surveyresponse.getResponse());
                        tv1.setId(surveyresponse.getId());
                        Log.e("ResponseId", "" + surveyresponse.getId());
                        break;
                    case 1:
                        tv2.setText(surveyresponse.getResponse());
                        tv2.setId(surveyresponse.getId());
                        break;
                    case 2:
                        tv3.setText(surveyresponse.getResponse());
                        tv3.setId(surveyresponse.getId());
                        break;
                    case 3:
                        tv4.setText(surveyresponse.getResponse());
                        tv4.setId(surveyresponse.getId());
                        break;
                    case 4:
                        tv5.setText(surveyresponse.getResponse());
                        tv5.setId(surveyresponse.getId());
                        break;
                }
            }

                tv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answerResponse = new AnswerResponse();
                        answerResponse.setQuestion_id(question.getId());
                        answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                        answerResponse.setResponse_id(tv1.getId());
                        Constants.answerResponseList.add(answerResponse);
                        if(noques == MainActivity.QuestionList.size()-1) {
                            showInputDialog();
                            //noques++;
                            //getNewData();
                        }else{
                            noques++;
                            getNewData();
                        }

                    }
                });
                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answerResponse = new AnswerResponse();
                        answerResponse.setQuestion_id(question.getId());
                        answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                        answerResponse.setResponse_id(tv2.getId());
                        Constants.answerResponseList.add(answerResponse);
                        if(noques == MainActivity.QuestionList.size()-1) {
                            showInputDialog();
                            //noques++;
                            //getNewData();
                        }else{
                            noques++;
                            getNewData();
                        }
                    }
                });
                tv3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answerResponse = new AnswerResponse();
                        answerResponse.setQuestion_id(question.getId());
                        answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                        answerResponse.setResponse_id(tv3.getId());
                        Constants.answerResponseList.add(answerResponse);
                        if(noques == MainActivity.QuestionList.size()-1) {
                            showInputDialog();
                            //noques++;
                            //getNewData();
                        }else{
                            noques++;
                            getNewData();
                        }
                    }
                });
                tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        answerResponse = new AnswerResponse();
                        answerResponse.setQuestion_id(question.getId());
                        answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                        answerResponse.setResponse_id(tv4.getId());
                        Constants.answerResponseList.add(answerResponse);

                        if(noques == MainActivity.QuestionList.size()-1) {
                            showInputDialog();
                            //noques++;
                            //getNewData();
                        }else{
                            noques++;
                            getNewData();
                        }
                    }
                });
                tv5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answerResponse = new AnswerResponse();
                        answerResponse.setQuestion_id(question.getId());
                        answerResponse.setQuestion_category_id(question.getQuestion_category_id());
                        answerResponse.setResponse_id(tv5.getId());
                        Constants.answerResponseList.add(answerResponse);
                        if(noques == MainActivity.QuestionList.size()-1) {
                            showInputDialog();
                            //noques++;
                            //getNewData();
                        }else{
                            noques++;
                            getNewData();

                        }
                    }
                });

    }



    public void showInputDialog() {

        String comment;

        final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(getActivity())
                .title(R.string.comment)
                .typeface(SetTypeFace.getTypeface(getActivity()), SetTypeFace.getTypeface(getActivity()))
                .inputRangeRes(0, 140, R.color.fab_donate_pressed)
                .alwaysCallInputCallback()
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .positiveText("Skip");


                mBuilder.input(getResources().getString(R.string.optional), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something

                        if(input.toString().length() == 0){
                            mBuilder.positiveText("Skip");
                        } else {
                            mBuilder.positiveText("Submit");
                        }

//                        Toast.makeText(getActivity(), input, Toast.LENGTH_SHORT).show();
                    }
                });
        mBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

               setJsonArray();
               UploadResponseToServer(dialog.getInputEditText().getText().toString(),jsonObject1.toString(),String.valueOf(patient_id));
            }
        });

        mBuilder.show();
      /*  AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        dialogBuilder.setMessage(R.string.comment);
        dialogBuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                setJsonArray();
                UploadResponseToServer(edt.getText().toString().trim(),jsonObject1.toString(),String.valueOf(patient_id));
                defaultFragment();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();*/
    }

    private void initView(View v) {
        tvQues = (TextView)v.findViewById(R.id.tvQues);
        llbutton = (LinearLayout) v.findViewById(R.id.llbutton);
        llSmiley = (LinearLayout) v.findViewById(R.id.llSmiley);
        tv1 = (TextView)v.findViewById(R.id.tv1);
        tv2 = (TextView)v.findViewById(R.id.tv2);
        tv3 = (TextView)v.findViewById(R.id.tv3);
        tv4 = (TextView)v.findViewById(R.id.tv4);
        tv5 = (TextView)v.findViewById(R.id.tv5);
        imLogo = (ImageView)v.findViewById(R.id.imLogo);
        imExtremelyHappy = (ImageView) v.findViewById(R.id.imExtremelyHappy);
        imHappy          = (ImageView) v.findViewById(R.id.imHappy);
        imOk             = (ImageView) v.findViewById(R.id.imOk);
        imSad            = (ImageView) v.findViewById(R.id.imSad);
        imExtremelySad   = (ImageView) v.findViewById(R.id.imExtremelySad);
        tvQues.setTypeface (SetTypeFace.getTypeface (getActivity()));
        tv1.setTypeface(SetTypeFace.getTypeface (getActivity()));
        tv2.setTypeface(SetTypeFace.getTypeface (getActivity()));
        tv3.setTypeface(SetTypeFace.getTypeface (getActivity()));
        tv4.setTypeface(SetTypeFace.getTypeface (getActivity()));
        tv5.setTypeface(SetTypeFace.getTypeface(getActivity()));
    }

    private void setJsonArray() {
        jsonObject1 = new JSONObject();
        jsonArray3 = new JSONArray();
        for (int i = 0; i < answerResponseList.size(); i++){
            AnswerResponse answerresponse = answerResponseList.get(i);
            Log.e("sud category_id", ""+answerresponse.getQuestion_category_id());
            Log.e("sud question", ""+answerresponse.getQuestion_id());
            Log.e("sud response", ""+answerresponse.getResponse_id());
            JSONObject jsonObj=new JSONObject();
            try
            {
                jsonObj.put(AppConfigTags.QUESTION_CATEGORY_ID,answerresponse.getQuestion_category_id());
                jsonObj.put(AppConfigTags.QUESTION_ID, answerresponse.getQuestion_id());
                jsonObj.put("option_id", answerresponse.getResponse_id());
                jsonArray3.put(jsonObj);
            } catch(Exception e) {
                Log.d("Exec", e.getMessage());
            }
        }

        try {
            jsonObject1.put("responses", jsonArray3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("karman response" , jsonObject1.toString());
    }

    private void UploadResponseToServer(final String comment, final String array, final String patient_id) {
        Log.d("survey_id",""+Constants.survey_id);
        Log.d("patient_id",""+patient_id);
        Log.d("responses_json",""+array);
        Log.d("comments",""+comment);
        if (NetworkConnection.isNetworkAvailable (getActivity())) {
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_POSTSURVEY + "/" + Constants.survey_id, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_POSTSURVEY + "/" + Constants.survey_id,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, "offline " + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    defaultFragment();

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
                    params.put("patient_id", patient_id);
                    params.put("responses_json", array);
                    params.put("comments", comment);
//                        Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
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
        }else {

            Toast.makeText(getActivity(),"Seems like there is no internet connection, the app will continue in Offline mode",Toast.LENGTH_LONG).show();
        }
    }



    private void defaultFragment() {
        Constants.status = "thanku";
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        StartFragment f11 = new StartFragment();
        Bundle args = new Bundle();
        args.putString(AppConfigTags.STATUS, "thanku");
        f11.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, f11, "fragment11");
        fragmentTransaction.commit();
    }

    private void backPress(View v) {
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    exitByBackKey();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
    protected void exitByBackKey() {
        new AlertDialog.Builder(getActivity())
                .setMessage("HELLO")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction;
                        fragmentTransaction = fragmentManager.beginTransaction();
                        StartFragment f11 = new StartFragment();
                        fragmentTransaction.replace(R.id.fragment_container, f11, "fragment11");
                        fragmentTransaction.commit();
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


