package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.model.LoginInfo;
import com.evercare.app.util.CommonUtil;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.PrefUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cz.msebera.android.httpclient.Header;

/**
 * 作者：xlren on 2016/8/29 13:21
 * 邮箱：renxianliang@126.com
 * 登录页面
 */

public class LoginActivity extends Activity {
    protected static final String TAG = "LoginActivity";
    private EditText mPhoneView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSignInButton;
    private CheckBox chb_eye;
    private Context context;


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        mPhoneView = (EditText) findViewById(R.id.et_phone);
        mPasswordView = (EditText) findViewById(R.id.et_password);
        mSignInButton = (Button) findViewById(R.id.btn_login_register);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(context, "login");
                attemptLogin();
            }
        });

        chb_eye = (CheckBox) findViewById(R.id.chb_eye);
        chb_eye.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int selection = mPasswordView.getSelectionStart();
                if (chb_eye.isChecked()) {
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mPasswordView.setSelection(selection);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        //默认登录帐号
        mPhoneView.setText(PrefUtils.getDefaults("login_id", context));
    }


    /**
     * 用户名密码输入校验
     */
    private void attemptLogin() {
        final String phone = mPhoneView.getText().toString();
        final String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(context, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, getString(R.string.prompt_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (HttpUtils.isNetworkConnected(LoginActivity.this)) {
            PrefUtils.setDefaults("login_id", phone, context);
            showProgress(true);
            login(password, phone);
        }else{
            Toast.makeText(LoginActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 登录
     *
     * @param password
     * @param phone
     */
    private void login(String password, String phone) {
        {
            final JSONObject jsonObject = new JSONObject();
            try {
              /*  jsonObject.put("test", "isNotNull");
                jsonObject.put("counselor_id", "4874");*/

                jsonObject.put("account", phone);
                jsonObject.put("password", password);
                jsonObject.put("imei", CommonUtil.getDeviceId(context));
                jsonObject.put("platform", "0");//平台（0-安卓，1-ios）
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpUtils.postWithJson(context, Constant.LOGIN, jsonObject, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        if (!TextUtils.isEmpty(response.getString("data"))) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<JsonResult<LoginInfo>>() {
                            }.getType();
                            JsonResult<LoginInfo> jsonResult = gson.fromJson(response.toString(), type);
                            String code = jsonResult.getCode();
                            long time = Long.parseLong(jsonResult.getTime());//服务器返回时间
                            long sysTime = System.currentTimeMillis() / 1000;//本地时间
                            long difference = time - sysTime;//时间差
                            PrefUtils.setDefaults("time", String.valueOf(difference), getApplicationContext());
                            final LoginInfo userInfo = jsonResult.getData();
                            if (TextUtils.equals(code, Result.SUCCESS) && userInfo != null) {
                                PrefUtils.setDefaults("token", userInfo.getToken(), getApplicationContext());
                                PrefUtils.setDefaults("rong_token", userInfo.getRongcloud_token(), getApplicationContext());
                                PrefUtils.setDefaults("userId", userInfo.getCounselor_id(), getApplicationContext());
                                PrefUtils.setDefaults("userName", userInfo.getUserName(), getApplicationContext());
                                PrefUtils.setDefaults("portraits", userInfo.getPortraits(), getApplicationContext());
//                                if (TextUtils.isEmpty(userInfo.getPush_alias_id())) {
                                //设置别名
                                JPushInterface.setAlias(LoginActivity.this, userInfo.getAlias(), new TagAliasCallback() {
                                    @Override
                                    public void gotResult(int code, String s, Set<String> set) {
                                        if (code == 0) {
                                            //告诉后台设置好别名了
                                            if (TextUtils.isEmpty(userInfo.getPush_alias_id())) {
                                                final JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("jpush_id", s);
                                                    setAlias(jsonObject);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }

                                    }
                                });
//                                }
                                startMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, jsonResult.getMsg() + code, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showProgress(false);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(LoginActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(LoginActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    /**
     * 通知后台推送ID设置成功
     *
     * @param jsonObject
     */
    private void setAlias(JSONObject jsonObject) {
        HttpUtils.postWithJson(context, Constant.CONSULTANT_EDIT_ALIAS_ID, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        //无需处理
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        System.exit(0);
    }
}

