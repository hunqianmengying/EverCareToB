package com.evercare.app.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.util.PrefUtils;
import com.evercare.app.util.RWTool;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 作者：xlren on 2016/8/29 13:22
 * 邮箱：renxianliang@126.com
 * 设置窗口
 */

public class SettingActivity extends BaseActivity {

    private TextView left_text;
    private TextView version_text;
    private Button btn_logout;//退出登录
    private TextView center_text;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(SettingActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(SettingActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        RWTool.appActivityList.add(this);
        initView();
        initData();
    }

    private void initView() {
        left_text = (TextView) findViewById(R.id.left_text);
        left_text.setVisibility(View.VISIBLE);
        center_text = (TextView) findViewById(R.id.center_text);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        left_text.setText("返回");
        left_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        version_text = (TextView) findViewById(R.id.version_text);

        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version_text.setText("V" + info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        center_text.setText("设置");
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MobclickAgent.onEvent(SettingActivity.this, "logout");
                //退出登录
                PrefUtils.setDefaults("token", null, getApplicationContext());
                JPushInterface.setAlias(SettingActivity.this, "", new TagAliasCallback() {
                    @Override
                    public void gotResult(int code, String s, Set<String> set) {
                    }
                });
                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        RWTool.exitApp();
        //finish();
    }
}
