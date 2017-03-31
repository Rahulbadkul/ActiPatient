package actiknow.com.actipatient.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
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
import actiknow.com.actipatient.utils.UserDetailsPref;
import actiknow.com.actipatient.utils.Utils;

/**
 * Created by actiknow on 3/28/17.
 */

public class LoginActivity extends AppCompatActivity {
    EditText etUsername;
    EditText etPassword;
    TextView tvWelcome;
    TextView tvPleaseLogin;
    TextView tvUsername;
    TextView tvPassword;
    TextView tvforgetPassword;
    TextView tvLogin;
    String android_id;
    CoordinatorLayout clMain;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_login);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Android","Android ID : "+android_id);
        initView();
        initListener();
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
        tvforgetPassword = (TextView) findViewById (R.id.tvforgetPassword);
        tvforgetPassword.setPaintFlags (tvforgetPassword.getPaintFlags () | Paint.UNDERLINE_TEXT_FLAG);

        Utils.setTypefaceToAllViews (this, tvforgetPassword);
    }

    private void initListener() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etUsername.getText ().toString ().trim ().length () == 0 && etPassword.getText ().toString ().length () == 0){
                    etUsername.setError ("Please enter username");
                    etPassword.setError ("Please enter password");
                } else if(etUsername.getText ().toString ().trim ().length () == 0){
                    etUsername.setError ("Please enter username");
                } else if(etPassword.getText ().toString ().trim ().length () == 0){
                    etPassword.setError ("Please enter password");
                } else {
                    sendLoginDetailsToServer (etUsername.getText ().toString ().trim (), etPassword.getText ().toString ().trim ());
                }
            }
        });
        tvforgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
    }

    private void sendLoginDetailsToServer(final String username, final String password) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            mDialog = ProgressDialog.show(LoginActivity.this,"", "Logging In..", true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_LOGIN, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_LOGIN,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, "" + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    int login_status = jsonObj.getInt(AppConfigTags.LOGIN_STATUS);
                                    Log.e(AppConfigTags.STATUS,""+login_status);
                                    switch (login_status){
                                        case 0:
                                            Toast.makeText(LoginActivity.this, "Invalid Login Credentials",Toast.LENGTH_LONG).show();
                                            mDialog.dismiss();
                                            break;

                                        case 1:
                                            UserDetailsPref userDetailsPref = UserDetailsPref.getInstance ();
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.DEVICE_ID,String.valueOf(jsonObj.getInt(AppConfigTags.DEVICE_ID)));
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.DEVICE_LOCATION,jsonObj.getString(AppConfigTags.DEVICE_LOCATION));
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.HOSPITAL_NAME,jsonObj.getString(AppConfigTags.HOSPITAL_NAME));
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.HOSPITAL_LOGO,jsonObj.getString(AppConfigTags.HOSPITAL_LOGO));
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.HOSPITAL_LOGIN_KEY,jsonObj.getString(AppConfigTags.HOSPITAL_LOGIN_KEY));
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.HOSPITAL_ACCESS_PIN,String.valueOf(jsonObj.getString(AppConfigTags.HOSPITAL_ACCESS_PIN)));
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.SUBSCRIPTION_STATUS,jsonObj.getString(AppConfigTags.SUBSCRIPTION_STATUS));
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.SUBSCRIPTION_STARTS,jsonObj.getString(AppConfigTags.SUBSCRIPTION_STARTS));
                                            userDetailsPref.putStringPref(LoginActivity.this,UserDetailsPref.SUBSCRIPTION_EXPIRES,jsonObj.getString(AppConfigTags.SUBSCRIPTION_EXPIRES));
                                            SendToMainActivity();
                                            mDialog.dismiss();
                                            break;

                                        case 2:
                                            Toast.makeText(LoginActivity.this, "Subscription Expired",Toast.LENGTH_LONG).show();
                                            mDialog.dismiss();
                                            break;

                                        case 3:
                                            Toast.makeText(LoginActivity.this, "Session Already Exist",Toast.LENGTH_LONG).show();
                                            showActiveSessionDialog (username, password);
                                            mDialog.dismiss();
                                            break;

                                        case 4:
                                            Toast.makeText(LoginActivity.this, "Error occured Try Again Later",Toast.LENGTH_LONG).show();
                                            mDialog.dismiss();
                                            break;
                                    }


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
                    params.put(AppConfigTags.USERNAME, username);
                    params.put(AppConfigTags.PASSWORD, password);
                    params.put(AppConfigTags.DEVICE_IDENTIICATION,android_id);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {

            Toast.makeText(LoginActivity.this,"Seems like there is no internet connection, the app will continue in Offline mode",Toast.LENGTH_LONG).show();
        }
    }

    private void SendToMainActivity() {
        Intent sendToMainActivity = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(sendToMainActivity);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void showActiveSessionDialog(final String username, final String password) {
        new MaterialDialog.Builder(this)
                .content("Another session with same login already exist. Do you wish to logout from other sessions.")
                .positiveText("YES")
                .negativeText("NO")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        LogoutActiveSession(username,password);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .typeface(SetTypeFace.getTypeface(this), SetTypeFace.getTypeface(this))
                .show();
    }

    private void LogoutActiveSession(final String username, final String password) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            mDialog = ProgressDialog.show(LoginActivity.this,"", "Logout from other device..", true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_LOGOUT_ACTIVESESSION, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_LOGOUT_ACTIVESESSION,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, "offline " + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    mDialog.dismiss();
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
                    params.put(AppConfigTags.USERNAME, username);
                    params.put(AppConfigTags.PASSWORD, password);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, Constants.hospital_login_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {

            Toast.makeText(LoginActivity.this,"Seems like there is no internet connection, the app will continue in Offline mode",Toast.LENGTH_LONG).show();
        }

    }

    private void forgotPassword(){
        new MaterialDialog.Builder(this)
                .title("Please enter your username")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .typeface(SetTypeFace.getTypeface(this), SetTypeFace.getTypeface(this))
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        sendUserNameToServer(String.valueOf(input));
                    }
                }).show();
    }

    private void sendUserNameToServer(final String username) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            mDialog = ProgressDialog.show(LoginActivity.this,"", "Sending Username To Server", true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_FORGET_PASSWORD, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_FORGET_PASSWORD,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, "" + AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean("error");
                                    if(!error)
                                    {
                                       Toast.makeText(LoginActivity.this,"Login credentials sent successfully on registered email",Toast.LENGTH_LONG).show();
                                        mDialog.dismiss();
                                    }else {
                                        Toast.makeText(LoginActivity.this,"Invalid Username",Toast.LENGTH_LONG).show();
                                        mDialog.dismiss();
                                    }
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
                    params.put(AppConfigTags.USERNAME, username);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_HOSPITAL_LOGIN_KEY, Constants.hospital_login_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {

            Toast.makeText(LoginActivity.this,"Seems like there is no internet connection, the app will continue in Offline mode",Toast.LENGTH_LONG).show();
        }

    }


}
