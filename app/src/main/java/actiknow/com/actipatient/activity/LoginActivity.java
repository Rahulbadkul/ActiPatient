package actiknow.com.actipatient.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import actiknow.com.actipatient.R;
import actiknow.com.actipatient.utils.AppConfigTags;
import actiknow.com.actipatient.utils.AppConfigURL;
import actiknow.com.actipatient.utils.Constants;
import actiknow.com.actipatient.utils.NetworkConnection;
import actiknow.com.actipatient.utils.SetTypeFace;
import actiknow.com.actipatient.utils.TypefaceSpan;
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;

public class LoginActivity extends AppCompatActivity {
    protected PowerManager.WakeLock mWakeLock;
    EditText etUsername;
    EditText etPassword;
    TextView tvWelcome;
    TextView tvPleaseLogin;
    TextView tvUsername;
    TextView tvPassword;
    TextView tvForgotPassword;
    TextView tvLogin;
    String android_id;
    CoordinatorLayout clMain;
    ProgressDialog progressDialog;
    Configuration config;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        initView ();
        initData();
        initListener ();
    }

    private void initData () {
        android_id = Settings.Secure.getString (this.getContentResolver (), Settings.Secure.ANDROID_ID);
        progressDialog = new ProgressDialog (LoginActivity.this);
        config = getResources ().getConfiguration ();

           /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService (Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock (PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire ();
    }

    private void initView () {
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        etUsername = (EditText) findViewById (R.id.etUsername);
        etPassword = (EditText) findViewById (R.id.etPassword);
        tvLogin = (TextView) findViewById (R.id.tvLogin);
        tvWelcome = (TextView) findViewById (R.id.tvWelcome);
        tvPleaseLogin = (TextView) findViewById (R.id.tvPleaseLogin);
        tvUsername = (TextView) findViewById (R.id.tvUsername);
        tvPassword = (TextView) findViewById (R.id.tvPassword);
        tvForgotPassword = (TextView) findViewById (R.id.tvForgotPassword);
        tvForgotPassword.setPaintFlags (tvForgotPassword.getPaintFlags () | Paint.UNDERLINE_TEXT_FLAG);

        Utils.setTypefaceToAllViews (this, tvForgotPassword);
    }

    private void initListener () {
        tvLogin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                SpannableString s = new SpannableString (getResources ().getString (R.string.please_enter_username));
                s.setSpan (new TypefaceSpan (LoginActivity.this, Constants.font_name), 0, s.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s2 = new SpannableString (getResources ().getString (R.string.please_enter_password));
                s2.setSpan (new TypefaceSpan (LoginActivity.this, Constants.font_name), 0, s.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (etUsername.getText ().toString ().trim ().length () == 0 && etPassword.getText ().toString ().length () == 0) {
                    etUsername.setError (s);
                    etPassword.setError (s2);
                } else if (etUsername.getText ().toString ().trim ().length () == 0) {
                    etUsername.setError (s);
                } else if (etPassword.getText ().toString ().trim ().length () == 0) {
                    etPassword.setError (s2);
                } else {
                    sendLoginDetailsToServer (etUsername.getText ().toString ().trim (), etPassword.getText ().toString ().trim ());
                }
            }
        });
        tvForgotPassword.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                showForgotPasswordDialog ();
            }
        });
        etUsername.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etUsername.setError (null);
                }
            }

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged (Editable s) {
            }
        });
        etPassword.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etPassword.setError (null);
                }
            }

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged (Editable s) {
            }
        });
    }

    private void showActiveSessionDialog (final String username, final String password) {
        MaterialDialog dialog = new MaterialDialog.Builder (this)
                .content (getResources ().getString (R.string.dialog_text_another_session))
                .positiveColor (getResources ().getColor (R.color.app_text_color))
                .contentColor (getResources ().getColor (R.color.app_text_color))
                .negativeColor (getResources ().getColor (R.color.app_text_color))
                .positiveText (getResources ().getString (R.string.dialog_action_yes))
                .negativeText (getResources ().getString (R.string.dialog_action_no))
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        logoutActiveSessions (username, password);
                    }
                })
                .onNegative (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .build ();

//        if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp <= 720) {
//            dialog.getActionButton (DialogAction.POSITIVE).setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getActionButton (DialogAction.NEGATIVE).setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getContentView ().setTextSize (TypedValue.COMPLEX_UNIT_SP, getResources ().getDimension (R.dimen.text_size_medium));
//        } else {
            // fall-back code goes here
//        }


        dialog.show ();

    }

    private void showForgotPasswordDialog () {
        MaterialDialog dialog = new MaterialDialog.Builder (this)
                .content (getResources ().getString (R.string.dialog_text_enter_username))
                .contentColor (getResources ().getColor (R.color.colorPrimary))
                .inputType (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .typeface (SetTypeFace.getTypeface (this), SetTypeFace.getTypeface (this))
                .input ("", "", new MaterialDialog.InputCallback () {
                    @Override
                    public void onInput (MaterialDialog dialog, CharSequence input) {
                        dialog.dismiss ();
                        sendForgotPasswordRequestToServer (input.toString ());
                    }
                }).build ();

//        if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp <= 720) {
//            dialog.getInputEditText ().setTextSize (getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getActionButton (DialogAction.POSITIVE).setTextSize (getResources ().getDimension (R.dimen.text_size_medium));
//            dialog.getContentView ().setTextSize (getResources ().getDimension (R.dimen.text_size_medium));
//        } else {
            // fall-back code goes here
//        }


        dialog.show ();
    }

    private void sendLoginDetailsToServer (final String username, final String password) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_logging_in), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_LOGIN_DEVICE, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_LOGIN_DEVICE,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    int login_status = jsonObj.getInt (AppConfigTags.LOGIN_STATUS);
                                    switch (login_status) {
                                        case 0:
                                            Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                            break;

                                        case 1:
                                            UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                                            userDetailsPref.putIntPref (LoginActivity.this, UserDetailsPref.DEVICE_ID, jsonObj.getInt (AppConfigTags.DEVICE_ID));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.DEVICE_LOCATION, jsonObj.getString (AppConfigTags.DEVICE_LOCATION));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.HOSPITAL_NAME, jsonObj.getString (AppConfigTags.HOSPITAL_NAME));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.HOSPITAL_LOGO, jsonObj.getString (AppConfigTags.HOSPITAL_LOGO));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.HOSPITAL_LOGIN_KEY, jsonObj.getString (AppConfigTags.HOSPITAL_LOGIN_KEY));
                                            userDetailsPref.putIntPref (LoginActivity.this, UserDetailsPref.HOSPITAL_ACCESS_PIN, jsonObj.getInt (AppConfigTags.HOSPITAL_ACCESS_PIN));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.HOSPITAL_DEFAULT_PATIENT_ID, jsonObj.getString (AppConfigTags.HOSPITAL_DEFAULT_PATIENT_ID));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.SUBSCRIPTION_STATUS, jsonObj.getString (AppConfigTags.SUBSCRIPTION_STATUS));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.SUBSCRIPTION_STARTS, jsonObj.getString (AppConfigTags.SUBSCRIPTION_STARTS));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.SUBSCRIPTION_EXPIRES, jsonObj.getString (AppConfigTags.SUBSCRIPTION_EXPIRES));
                                            userDetailsPref.putStringPref (LoginActivity.this, UserDetailsPref.LANGUAGE, AppConfigTags.english_language_code);

                                            Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                                            intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity (intent);
                                            overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);

                                            break;
                                        case 2:
                                            Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                            break;
                                        case 3:
                                            showActiveSessionDialog (username, password);
                                            break;
                                        case 4:
                                            Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_retry), new View.OnClickListener () {
                                                @Override
                                                public void onClick (View v) {
                                                    sendLoginDetailsToServer (username, password);
                                                }
                                            });
                                            break;
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.USERNAME, username);
                    params.put (AppConfigTags.PASSWORD, password);
                    params.put (AppConfigTags.DEVICE_IDENTIICATION, android_id);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
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

    private void sendForgotPasswordRequestToServer (final String username) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_sending_request), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_FORGET_PASSWORD, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_FORGET_PASSWORD,
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
                                        Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    } else {
                                        Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_retry), new View.OnClickListener () {
                                            @Override
                                            public void onClick (View v) {
                                                showForgotPasswordDialog ();
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                                Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.USERNAME, username);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
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

    private void logoutActiveSessions (final String username, final String password) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_LOGOUT_ACTIVE_SESSION, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_LOGOUT_ACTIVE_SESSION,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                                Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.USERNAME, username);
                    params.put (AppConfigTags.PASSWORD, password);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
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
    public void onDestroy () {
        this.mWakeLock.release ();
        super.onDestroy ();
    }
}
