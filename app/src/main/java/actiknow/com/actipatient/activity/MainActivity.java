package actiknow.com.actipatient.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bugsnag.android.Bugsnag;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import actiknow.com.actipatient.R;
import actiknow.com.actipatient.adapter.CustomSpinnerAdapter;
import actiknow.com.actipatient.model.Question;
import actiknow.com.actipatient.model.QuestionOption;
import actiknow.com.actipatient.model.SurveyType;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.SetTypeFace;
import actiknow.com.actipatient.utils.TypefaceSpan;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;


public class MainActivity extends AppCompatActivity {
    public static int survey_type_id_temp = 0;
    protected PowerManager.WakeLock mWakeLock;
    UserDetailsPref userDetailPref;
    String android_id;
    int version_code;
    CoordinatorLayout clMain;
    RelativeLayout rlMain;
    TextView tvHeading;
    TextView tvStart;
    FloatingActionButton fabSettings;
    TextView tvHindi;
    TextView tvEnglish;
    TextView tvSurveyType;
    ImageView ivLogo;
    ProgressDialog progressDialog;
    CustomSpinnerAdapter adapter;
    ProgressBar progressBar;

    Configuration config;

    @SuppressLint("NewApi")
    public static final void recreateActivityCompat (final Activity a) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            a.recreate ();
//        } else {
        final Intent intent = a.getIntent ();
        intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
        a.finish ();
        a.overridePendingTransition (0, 0);
        a.startActivity (intent);
        a.overridePendingTransition (0, 0);
//        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initView ();
        initData ();
        initListener ();
        isLogin ();
        getSurveyTypes ();
        initApplication ();

        if (! userDetailPref.getBooleanPref (this, UserDetailsPref.LOGGED_IN_SESSION)) {
            checkVersionUpdate ();
        }
    }

    private void initView () {
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        rlMain = (RelativeLayout) findViewById (R.id.rlMain);
        tvHeading = (TextView) findViewById (R.id.tvHeading);
        tvStart = (TextView) findViewById (R.id.tvStart);
        fabSettings = (FloatingActionButton) findViewById (R.id.fabSettings);
        tvHindi = (TextView) findViewById (R.id.tvHindi);
        tvEnglish = (TextView) findViewById (R.id.tvEnglish);
        tvSurveyType = (TextView) findViewById (R.id.tvSurveyType);
        progressBar = (ProgressBar) findViewById (R.id.progressBar);
        ivLogo = (ImageView) findViewById (R.id.ivHospitalLogo);
    }

    private void initData () {
        Bugsnag.init (this);
        userDetailPref = UserDetailsPref.getInstance ();
        progressDialog = new ProgressDialog (this);
//        try {
//            Log.e ("Karman Encrypt", "" + Utils.encrypt ("karman"));
//            Log.e ("Karman Decrypt", "" + Utils.decrypt (Utils.encrypt ("karman")));
//        } catch (Exception e) {
//            e.printStackTrace ();
//        }
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager ().getPackageInfo (getPackageName (), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace ();
        }
        version_code = pInfo.versionCode;

        config = getResources ().getConfiguration ();

          /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService (Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock (PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire ();

        adapter = new CustomSpinnerAdapter (this, R.layout.spinner_item, Constants.surveyTypeList);
        Utils.setTypefaceToAllViews (this, tvHeading);

        Glide.with (this)
                .load (userDetailPref.getStringPref (this, UserDetailsPref.HOSPITAL_LOGO))
                .listener (new RequestListener<String, GlideDrawable> () {
                    @Override
                    public boolean onException (Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility (View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady (GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility (View.GONE);
                        return false;
                    }
                })
                .into (ivLogo);


        switch (userDetailPref.getStringPref (this, UserDetailsPref.LANGUAGE)) {
            case AppConfigTags.hindi_language_code:
                changeLanguage (userDetailPref.getStringPref (this, UserDetailsPref.LANGUAGE));
                tvHeading.setText (userDetailPref.getStringPref (this, AppConfigTags.HOSPITAL_NAME) + " " + getResources ().getString (R.string.activity_main_text_heading));
                tvStart.setText (getResources ().getString (R.string.activity_main_button_start_survey));
                changeLanguage (AppConfigTags.hindi_language_code);
                tvHindi.setTextColor (getResources ().getColor (R.color.text_color_white));
                tvEnglish.setTextColor (getResources ().getColor (R.color.app_text_color));
                tvEnglish.setBackgroundResource (R.drawable.button_white);
                tvHindi.setBackgroundResource (R.drawable.button_language);
                break;
            case AppConfigTags.english_language_code:
                changeLanguage (userDetailPref.getStringPref (this, UserDetailsPref.LANGUAGE));
                tvHeading.setText (getResources ().getString (R.string.activity_main_text_heading) + " " + userDetailPref.getStringPref (MainActivity.this, AppConfigTags.HOSPITAL_NAME));
                tvStart.setText (getResources ().getString (R.string.activity_main_button_start_survey));
                changeLanguage (AppConfigTags.english_language_code);
                tvHindi.setTextColor (getResources ().getColor (R.color.app_text_color));
                tvEnglish.setTextColor (getResources ().getColor (R.color.text_color_white));
                tvHindi.setBackgroundResource (R.drawable.button_white);
                tvEnglish.setBackgroundResource (R.drawable.button_language);
                break;
        }
    }

    private void initListener () {
        fabSettings.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                showAccessPINDialog ();
            }
        });
        tvStart.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                showPatientIDInputDialog ();
            }
        });
        tvHindi.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                changeLanguage (AppConfigTags.hindi_language_code);
                tvHeading.setText (userDetailPref.getStringPref (MainActivity.this, AppConfigTags.HOSPITAL_NAME) + " " + getResources ().getString (R.string.activity_main_text_heading));
                tvStart.setText (getResources ().getString (R.string.activity_main_button_start_survey));
                tvHindi.setTextColor (getResources ().getColor (R.color.text_color_white));
                tvEnglish.setTextColor (getResources ().getColor (R.color.app_text_color));
                tvEnglish.setBackgroundResource (R.drawable.button_white);
                tvHindi.setBackgroundResource (R.drawable.button_language);

            }
        });
        tvEnglish.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                changeLanguage (AppConfigTags.english_language_code);
                tvHeading.setText (getResources ().getString (R.string.activity_main_text_heading) + " " + userDetailPref.getStringPref (MainActivity.this, AppConfigTags.HOSPITAL_NAME));
                tvStart.setText (getResources ().getString (R.string.activity_main_button_start_survey));
                tvHindi.setTextColor (getResources ().getColor (R.color.app_text_color));
                tvEnglish.setTextColor (getResources ().getColor (R.color.text_color_white));
                tvHindi.setBackgroundResource (R.drawable.button_white);
                tvEnglish.setBackgroundResource (R.drawable.button_language);
            }
        });
    }

    private void checkVersionUpdate () {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_CHECK_VERSION, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.GET, AppConfigURL.URL_CHECK_VERSION,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, "" + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);

                                    if (! error) {
                                        int db_version_code = jsonObj.getInt (AppConfigTags.VERSION_CODE);
                                        String db_version_name = jsonObj.getString (AppConfigTags.VERSION_NAME);
                                        String version_updated_on = jsonObj.getString (AppConfigTags.VERSION_UPDATED_ON);
                                        int version_update_critical = jsonObj.getInt (AppConfigTags.VERSION_UPDATE_CRITICAL);

                                        if (db_version_code > version_code) {
                                            switch (version_update_critical) {
                                                case 0:
                                                    userDetailPref.putBooleanPref (MainActivity.this, userDetailPref.LOGGED_IN_SESSION, true);
                                                    new MaterialDialog.Builder (MainActivity.this)
                                                            .content (R.string.dialog_text_new_version_available)
                                                            .positiveColor (getResources ().getColor (R.color.app_text_color))
                                                            .contentColor (getResources ().getColor (R.color.app_text_color))
                                                            .negativeColor (getResources ().getColor (R.color.app_text_color))
                                                            .typeface (SetTypeFace.getTypeface (MainActivity.this), SetTypeFace.getTypeface (MainActivity.this))
                                                            .canceledOnTouchOutside (false)
                                                            .cancelable (false)
                                                            .positiveText (R.string.dialog_action_update)
                                                            .negativeText (R.string.dialog_action_ignore)
                                                            .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                                @Override
                                                                public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                    final String appPackageName = getPackageName (); // getPackageName() from Context or Activity object
                                                                    try {
                                                                        startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
                                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                                        startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                    }
                                                                }
                                                            })
                                                            .onNegative (new MaterialDialog.SingleButtonCallback () {
                                                                @Override
                                                                public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                    dialog.dismiss ();
                                                                }
                                                            }).show ();
                                                    break;
                                                case 1:
                                                    new MaterialDialog.Builder (MainActivity.this)
                                                            .content (R.string.dialog_text_new_version_available)
                                                            .positiveColor (getResources ().getColor (R.color.app_text_color))
                                                            .contentColor (getResources ().getColor (R.color.app_text_color))
                                                            .negativeColor (getResources ().getColor (R.color.app_text_color))
                                                            .typeface (SetTypeFace.getTypeface (MainActivity.this), SetTypeFace.getTypeface (MainActivity.this))
                                                            .canceledOnTouchOutside (false)
                                                            .cancelable (false)

                                                            .cancelListener (new DialogInterface.OnCancelListener () {
                                                                @Override
                                                                public void onCancel (DialogInterface dialog) {

                                                                }
                                                            })
                                                            .positiveText (R.string.dialog_action_update)
//                                                            .negativeText (R.string.dialog_action_close)
                                                            .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                                @Override
                                                                public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                    final String appPackageName = getPackageName (); // getPackageName() from Context or Activity object
                                                                    try {
                                                                        startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
                                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                                        startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                    }
                                                                }
                                                            })
//                                                            .onNegative (new MaterialDialog.SingleButtonCallback () {
//                                                                @Override
//                                                                public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                                    finish ();
//                                                                    overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
//                                                                }
//                                                            })
                                                            .show ();
                                                    break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    }

                    ,
                    new Response.ErrorListener ()

                    {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }

            )

            {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
            checkVersionUpdate ();
        }
    }

    private void showAccessPINDialog () {
        final MaterialDialog dialog = new MaterialDialog.Builder (this)
                .customView (R.layout.dialog_password, true)
                .build ();

        TextView tvForgotPIN = (TextView) dialog.getCustomView ().findViewById (R.id.tvForgotPIN);
        final EditText etAccessPIN = (EditText) dialog.getCustomView ().findViewById (R.id.etAccessPIN);
        final EditText etAccessPINTemp = (EditText) dialog.getCustomView ().findViewById (R.id.etAccessPINTemp);
        etAccessPIN.setEnabled (false);
        Utils.setTypefaceToAllViews (this, etAccessPIN);
        etAccessPINTemp.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (s.toString ().trim ().length () == 4) {
                    if (Integer.parseInt (s.toString ().trim ()) == userDetailPref.getIntPref (MainActivity.this, UserDetailsPref.HOSPITAL_ACCESS_PIN)) {
                        Utils.hideSoftKeyboard (MainActivity.this);
                        dialog.dismiss ();
                        showSettingDialog ();
                    } else {
                        Utils.shakeEditText (MainActivity.this, etAccessPIN);
//                        etAccessPIN.setText ("camy― ― ― ―");
                        etAccessPINTemp.setText (etAccessPINTemp.getText ().toString ().substring (0, 0));
                        SpannableString s2 = new SpannableString (getResources ().getString (R.string.snackbar_text_invalid_access_pin));
                        s2.setSpan (new TypefaceSpan (MainActivity.this, Constants.font_name), 0, s.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        etAccessPINTemp.setError (s2);
                        Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_invalid_access_pin), Snackbar.LENGTH_LONG, null, null);
                    }
                }
            }

            @Override
            public void afterTextChanged (Editable s) {
                etAccessPINTemp.setError (null);
                switch (s.toString ().trim ().length ()) {
//                    case 0:
//                        etAccessPIN.setText ("― ― ― ―");
//                        etAccessPIN.setText ("― ― ― ―");
//                        etAccessPIN.setText (Html.fromHtml ("\\u2015 \\u2015 \\u2015 \\u2015"));
//                        break;
                    case 1:
//                        etAccessPIN.setText ("* ― ― ―");
                        etAccessPIN.setText ("• ― ― ―");
//                        etAccessPIN.setText (Html.fromHtml ("\\u2605 \\u2015 \\u2015 \\u2015"));
                        break;
                    case 2:
//                        etAccessPIN.setText ("* * ― ―");
                        etAccessPIN.setText ("• • ― ―");
//                        etAccessPIN.setText (Html.fromHtml ("\\u2605 \\u2605 \\u2015 \\u2015"));
                        break;
                    case 3:
//                        etAccessPIN.setText ("* * * ―");
                        etAccessPIN.setText ("• • • ―");
//                        etAccessPIN.setText (Html.fromHtml ("\\u2605 \\u2605 \\u2605 \\u2015"));
                        break;
//                    case 4:
//                        etAccessPIN.setText ("* * * *");
//                        etAccessPIN.setText ("★ ★ ★ ★");
//                        etAccessPIN.setText ("\\u2605 \\u2605 \\u2605 \\u2605");
//                        break;
                    default:
//                        etAccessPIN.setText ("― ― ― ―");
                        etAccessPIN.setText ("― ― ― ―");
//                        etAccessPIN.setText (Html.fromHtml ("\\u2015 \\u2015 \\u2015 \\u2015"));
                        break;
                }
            }
        });

        tvForgotPIN.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                sendForgotPINRequestToServer ();
            }
        });

        final TextView tv0 = (TextView) dialog.getCustomView ().findViewById (R.id.tv0);
        final TextView tv1 = (TextView) dialog.getCustomView ().findViewById (R.id.tv1);
        final TextView tv2 = (TextView) dialog.getCustomView ().findViewById (R.id.tv2);
        final TextView tv3 = (TextView) dialog.getCustomView ().findViewById (R.id.tv3);
        final TextView tv4 = (TextView) dialog.getCustomView ().findViewById (R.id.tv4);
        final TextView tv5 = (TextView) dialog.getCustomView ().findViewById (R.id.tv5);
        final TextView tv6 = (TextView) dialog.getCustomView ().findViewById (R.id.tv6);
        final TextView tv7 = (TextView) dialog.getCustomView ().findViewById (R.id.tv7);
        final TextView tv8 = (TextView) dialog.getCustomView ().findViewById (R.id.tv8);
        final TextView tv9 = (TextView) dialog.getCustomView ().findViewById (R.id.tv9);
        final ImageView ivBack = (ImageView) dialog.getCustomView ().findViewById (R.id.ivBack);

        ivBack.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    ivBack.setBackgroundResource (R.drawable.button_filled);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivBack.setImageDrawable (getResources ().getDrawable (R.drawable.ic_delete_white, getApplicationContext ().getTheme ()));
                    } else {
                        ivBack.setImageDrawable (getResources ().getDrawable (R.drawable.ic_delete_white));
                    }
                    if (etAccessPINTemp.getText ().toString ().length () > 0) {
                        etAccessPINTemp.setText (etAccessPINTemp.getText ().toString ().substring (0, etAccessPINTemp.getText ().toString ().length () - 1));
                    }
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivBack.setImageDrawable (getResources ().getDrawable (R.drawable.ic_delete, getApplicationContext ().getTheme ()));
                    } else {
                        ivBack.setImageDrawable (getResources ().getDrawable (R.drawable.ic_delete));
                    }
                    ivBack.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });

        tv0.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv0.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv0.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "0");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv0.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv0.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });

        tv1.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv1.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv1.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "1");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv1.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv1.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });
        tv2.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv2.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv2.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "2");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv2.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv2.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });
        tv3.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv3.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv3.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "3");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv3.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv3.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });
        tv4.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv4.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv4.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "4");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv4.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv4.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });
        tv5.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv5.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv5.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "5");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv5.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv5.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });
        tv6.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv6.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv6.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "6");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv6.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv6.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });
        tv7.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv7.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv7.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "7");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv7.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv7.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });
        tv8.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv8.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv8.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "8");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv8.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv8.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });
        tv9.setOnTouchListener (new View.OnTouchListener () {
            @Override
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction () == MotionEvent.ACTION_DOWN) {
                    tv9.setTextColor (getResources ().getColor (R.color.text_color_white));
                    tv9.setBackgroundResource (R.drawable.button_filled);
                    etAccessPINTemp.setText (etAccessPINTemp.getText ().toString () + "9");
                    return true;
                } else if (event.getAction () == MotionEvent.ACTION_UP) {
                    tv9.setTextColor (getResources ().getColor (R.color.colorPrimary));
                    tv9.setBackgroundResource (R.drawable.button_empty);
                    return true;
                }
                return false;
            }
        });

        dialog.getWindow ().setLayout (CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        dialog.show ();

        Utils.hideSoftKeyboard (this);
    }

    private void changeLanguage (String language) {
        userDetailPref.putStringPref (this, UserDetailsPref.LANGUAGE, language);
        Locale locale = new Locale (language);
        Locale.setDefault (locale);
        Configuration config = new Configuration ();
        config.locale = locale;
        getResources ().updateConfiguration (config, getResources ().getDisplayMetrics ());
    }

    public void showSettingDialog () {
        final MaterialDialog dialog = new MaterialDialog.Builder (this)
                .customView (R.layout.dialog_settings, true)
                .positiveText (getResources ().getString (R.string.dialog_action_ok))
                .negativeText (getResources ().getString (R.string.dialog_action_cancel))
                .neutralText (getResources ().getString (R.string.dialog_action_logout))
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .build ();

//        if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp <= 720) {
//            dialog.getActionButton (DialogAction.POSITIVE).setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getActionButton (DialogAction.NEGATIVE).setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getActionButton (DialogAction.NEUTRAL).setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//        } else {
        // fall-back code goes here
//        }

        TextView tvSubscriptionExpiry = (TextView) dialog.getCustomView ().findViewById (R.id.tvSubscriptionExpiry);
        TextView tvSubscriptionStatus = (TextView) dialog.getCustomView ().findViewById (R.id.tvSubscriptionStatus);
        Spinner spCategory = (Spinner) dialog.getCustomView ().findViewById (R.id.spSurveyType);

        Utils.setTypefaceToAllViews (this, tvSubscriptionExpiry);

        tvSubscriptionStatus.setText (userDetailPref.getStringPref (this, UserDetailsPref.SUBSCRIPTION_STATUS));
        tvSubscriptionExpiry.setText (Utils.convertTimeFormat (userDetailPref.getStringPref (this, UserDetailsPref.SUBSCRIPTION_EXPIRES), "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy"));

        spCategory.setAdapter (adapter);

        for (int i = 0; i < Constants.surveyTypeList.size (); i++) {
            SurveyType surveyType = Constants.surveyTypeList.get (i);
            if (userDetailPref.getIntPref (this, UserDetailsPref.SURVEY_TYPE_ID) == surveyType.getSurvey_type_id ()) {
                spCategory.setSelection (i);
            }
        }


        spCategory.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected (AdapterView<?> parentView, View v, int position, long id) {
                survey_type_id_temp = ((Integer.parseInt (((TextView) v.findViewById (R.id.tvSurveyTypeID)).getText ().toString ())));
            }

            @Override
            public void onNothingSelected (AdapterView<?> parentView) {
            }
        });

        dialog.getActionButton (DialogAction.POSITIVE).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                for (int i = 0; i < Constants.surveyTypeList.size (); i++) {
                    SurveyType surveyType = Constants.surveyTypeList.get (i);
                    if (surveyType.getSurvey_type_id () == survey_type_id_temp) {
                        userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID, surveyType.getSurvey_type_id ());
                        switch (surveyType.getSurvey_type_text ()) {
                            case "IN-PATIENT":
                                tvSurveyType.setText ("IP");
                                break;
                            case "OUT-PATIENT":
                                tvSurveyType.setText ("OP");
                                break;
                        }

                    }
                }
                dialog.dismiss ();
            }
        });

        dialog.getActionButton (DialogAction.NEGATIVE).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                dialog.dismiss ();
            }
        });

        dialog.getActionButton (DialogAction.NEUTRAL).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                logOutFromDevice (userDetailPref.getIntPref (MainActivity.this, UserDetailsPref.DEVICE_ID));
                dialog.dismiss ();
            }
        });

        dialog.getWindow ().setLayout (CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);

        dialog.show ();
    }

    public void showPatientIDInputDialog () {
        final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder (this)
                .content (R.string.dialog_text_enter_patient_id)
                .contentColor (getResources ().getColor (R.color.app_text_color))
                .positiveColor (getResources ().getColor (R.color.app_text_color))
                .neutralColor (getResources ().getColor (R.color.app_text_color))
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .inputRangeRes (1, 20, R.color.input_error_colour)
                .inputType (InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                .alwaysCallInputCallback ()
                .canceledOnTouchOutside (true)
                .cancelable (true)
                .positiveText (R.string.dialog_action_proceed)
                .neutralText (R.string.dialog_action_continue_anonymous);

        mBuilder.input (null, null, new MaterialDialog.InputCallback () {
            @Override
            public void onInput (MaterialDialog dialog, CharSequence input) {
            }
        });

        mBuilder.onPositive (new MaterialDialog.SingleButtonCallback () {
            @Override
            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                generateSurvey (dialog.getInputEditText ().getText ().toString ());
            }
        });

        mBuilder.onNeutral (new MaterialDialog.SingleButtonCallback () {
            @Override
            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                generateSurvey (userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_DEFAULT_PATIENT_ID));
            }
        });


        MaterialDialog dialog = mBuilder.build ();

//        if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp <= 720) {
//            dialog.getActionButton (DialogAction.POSITIVE).setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getContentView ().setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getInputEditText ().setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//        } else {
        // fall-back code goes here
//        }

        dialog.show ();
    }

    private void logOutFromDevice (final int device_id) {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_logging_out), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_LOGOUT_DEVICE, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_LOGOUT_DEVICE,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, "" + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.LANGUAGE, AppConfigTags.english_language_code);
                                    userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.DEVICE_ID, 0);
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.DEVICE_LOCATION, "");
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_NAME, "");
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGO, "");
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY, "");
                                    userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.HOSPITAL_ACCESS_PIN, 0);
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_DEFAULT_PATIENT_ID, "");
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STATUS, "");
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STARTS, "");
                                    userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_EXPIRES, "");
                                    userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID, 0);

                                    changeLanguage (AppConfigTags.english_language_code);

                                    Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                                    intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity (intent);
                                    overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            progressDialog.dismiss ();
                            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
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
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
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

    private void generateSurvey (String patient_id) {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_generating_survey), true);
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SURVEY_GENERATE + "/" + userDetailPref.getIntPref (this, AppConfigTags.DEVICE_ID) + "/" + userDetailPref.getStringPref (this, UserDetailsPref.LANGUAGE) + "/" + userDetailPref.getIntPref (this, UserDetailsPref.SURVEY_TYPE_ID) + "/" + patient_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_SURVEY_GENERATE + "/" + userDetailPref.getIntPref (this, AppConfigTags.DEVICE_ID) + "/" + userDetailPref.getStringPref (this, UserDetailsPref.LANGUAGE) + "/" + userDetailPref.getIntPref (this, UserDetailsPref.SURVEY_TYPE_ID) + "/" + patient_id,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Constants.QuestionList.clear ();
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.SURVEY_DETAILS);
                                        Constants.survey_id = jsonObj.getString (AppConfigTags.SURVEY_ID);
                                        Constants.patient_id = jsonObj.getString (AppConfigTags.PATIENT_ID);
                                        for (int i = 0; i < jsonArray.length (); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject (i);
                                            Question question = new Question ();
                                            question.setQuestion_id (jsonObject.getInt (AppConfigTags.QUESTION_ID));
                                            question.setQuestion_text (new String (jsonObject.getString (AppConfigTags.QUESTION_TEXT).getBytes ("ISO-8859-1"), "UTF-8"));
                                            question.setQuestion_category_id (jsonObject.getInt (AppConfigTags.QUESTION_CATEGORY_ID));
                                            JSONArray jsonArrayOptions = jsonObject.getJSONArray (AppConfigTags.OPTIONS);
                                            for (int j = 0; j < jsonArrayOptions.length (); j++) {
                                                JSONObject jsonObjectNew = jsonArrayOptions.getJSONObject (j);
                                                QuestionOption questionOption = new QuestionOption ();
                                                questionOption.setOption_id (jsonObjectNew.getInt (AppConfigTags.OPTION_ID));
                                                questionOption.setOption_text (new String (jsonObjectNew.getString (AppConfigTags.OPTION_TEXT).getBytes ("ISO-8859-1"), "UTF-8").toUpperCase ());
                                                question.addQuestionOption (questionOption);
                                            }
                                            Constants.QuestionList.add (question);
                                        }
                                        Intent intent = new Intent (MainActivity.this, SurveyActivity.class);
                                        startActivity (intent);
                                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                    } else {
                                        Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailsPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 30);
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

    private void isLogin () {
        if (userDetailPref.getIntPref (MainActivity.this, UserDetailsPref.DEVICE_ID) == 0 || userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY) == "") {// || userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_DEFAULT_PATIENT_ID) == "") {
            Intent myIntent = new Intent (this, LoginActivity.class);
            startActivity (myIntent);
        }
        if (userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY) == "")// || userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_DEFAULT_PATIENT_ID) == "")
            finish ();
    }

    private void initApplication () {
        android_id = Settings.Secure.getString (this.getContentResolver (), Settings.Secure.ANDROID_ID);
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_initializing), false);
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_CHECK_STATUS_APPLICATION + "/" + version_code + "/" + userDetailPref.getIntPref (this, UserDetailsPref.DEVICE_ID) + "/" + android_id, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_CHECK_STATUS_APPLICATION + "/" + version_code + "/" + userDetailPref.getIntPref (this, UserDetailsPref.DEVICE_ID) + "/" + android_id,
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
                                        initApplication ();
                                    } else if (status != 1) {
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.LANGUAGE, AppConfigTags.english_language_code);
                                        userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.DEVICE_ID, 0);
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.DEVICE_LOCATION, "");
                                        userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID, 0);
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_NAME, "");
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGO, "");
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY, "");
                                        userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.HOSPITAL_ACCESS_PIN, 0);
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_DEFAULT_PATIENT_ID, "");
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STATUS, "");
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_STARTS, "");
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.SUBSCRIPTION_EXPIRES, "");

                                        changeLanguage (AppConfigTags.english_language_code);

                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        Utils.showToast (MainActivity.this, message, true);

                                        Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                                        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity (intent);
                                        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                    }
                                    if (status == 1) {
                                        userDetailPref.putStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_DEFAULT_PATIENT_ID, jsonObj.getString (AppConfigTags.HOSPITAL_DEFAULT_PATIENT_ID));
                                        rlMain.setVisibility (View.VISIBLE);
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    e.printStackTrace ();
                                }
                            } else {
                                progressDialog.dismiss ();
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
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
            progressDialog.dismiss ();
//            initApplication ();
        }
    }

    private void getSurveyTypes () {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_SURVEY_TYPE, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.URL_SURVEY_TYPE,
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
                                            if (userDetailPref.getIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID) == 0) {
                                                userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID, surveytype.getSurvey_type_id ());
                                            }
                                        }
                                        Constants.surveyTypeList.add (surveytype);
                                    }

//                                    String[] survey_types = getResources ().getStringArray (R.array.survey_types);
                                    for (int i = 0; i < Constants.surveyTypeList.size (); i++) {
                                        SurveyType surveyType = Constants.surveyTypeList.get (i);
                                        if (surveyType.getSurvey_type_id () == userDetailPref.getIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID)) {
                                            switch (surveyType.getSurvey_type_text ()) {
                                                case "IN-PATIENT":
                                                    tvSurveyType.setText ("IP");
                                                    tvSurveyType.setVisibility (View.VISIBLE);
                                                    break;
                                                case "OUT-PATIENT":
                                                    tvSurveyType.setText ("OP");
                                                    tvSurveyType.setVisibility (View.VISIBLE);
                                                    break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
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
//TODO ye karna hai theek se abhi judagu hai
            if (userDetailPref.getIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID) == 0) {
                userDetailPref.putIntPref (MainActivity.this, UserDetailsPref.SURVEY_TYPE_ID, 1);
                tvSurveyType.setText ("IP");
                tvSurveyType.setVisibility (View.VISIBLE);
            }
        }
    }

    private void sendForgotPINRequestToServer () {
        if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_sending_request), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_FORGET_PIN, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.GET, AppConfigURL.URL_FORGET_PIN,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, "" + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
//                                        Utils.showToast (MainActivity.this, message, true);
                                    } else {
//                                        Utils.showToast (MainActivity.this, message, true);
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_retry), new View.OnClickListener () {
                                            @Override
                                            public void onClick (View v) {
                                                sendForgotPINRequestToServer ();
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
//                                    Utils.showToast (MainActivity.this, getResources ().getString (R.string.snackbar_text_exception_occurred), true);
                                    Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
//                                Utils.showToast (MainActivity.this, getResources ().getString (R.string.snackbar_text_error_occurred), true);
                                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
//                            Utils.showToast (MainActivity.this, getResources ().getString (R.string.snackbar_text_error_occurred), true);
                            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, userDetailPref.getStringPref (MainActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
//            Utils.showToast (MainActivity.this, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), true);
            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
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
        MaterialDialog dialog = new MaterialDialog.Builder (this)
                .content (R.string.dialog_text_quit_application)
                .positiveColor (getResources ().getColor (R.color.app_text_color))
                .contentColor (getResources ().getColor (R.color.app_text_color))
                .negativeColor (getResources ().getColor (R.color.app_text_color))
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .canceledOnTouchOutside (false)
                .cancelable (false)
                .positiveText (R.string.dialog_action_yes)
                .negativeText (R.string.dialog_action_no)
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userDetailPref.putBooleanPref (MainActivity.this, UserDetailsPref.LOGGED_IN_SESSION, false);
                        finish ();
                        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }).build ();


//        if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp <= 720) {
//            dialog.getActionButton (DialogAction.POSITIVE).setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getActionButton (DialogAction.NEGATIVE).setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getContentView ().setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//        } else {
        // fall-back code goes here
//        }

        dialog.show ();
    }

    @Override
    public void onDestroy () {
        this.mWakeLock.release ();
        super.onDestroy ();
    }
}