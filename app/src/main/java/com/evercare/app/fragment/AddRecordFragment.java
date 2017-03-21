package com.evercare.app.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Activity.BaseFragment;
import com.evercare.app.Activity.LoginActivity;
import com.evercare.app.Activity.SearchProjectActivity;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.util.CommonUtil;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.view.View.VISIBLE;

/**
 * 作者：LXQ on 2016-12-6 13:37
 * 邮箱：842202389@qq.com
 * 创建咨询
 */
public class AddRecordFragment extends BaseFragment {

    private TextView txt_projectname;
    private EditText edt_remark;
    private Button btn_savetask;
    private String project_name;
    private String business_id;//客户id
    private String project_id;//项目ID
    private View mProgressView;
    private String customer_name;
    private RelativeLayout layout_tem;
    private Context context;

    private Toast mToast;

    public static AddRecordFragment newInstance(String business_cuid, String customer_name) {
        AddRecordFragment fragment = new AddRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString("business_cuid", business_cuid);
        bundle.putString("customer_name", customer_name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view) {
        context = getActivity();
        Bundle bundle = getArguments();
        business_id = bundle.getString("business_cuid");
        customer_name = bundle.getString("customer_name");
        layout_tem = (RelativeLayout) view.findViewById(R.id.layout_tem);
        txt_projectname = (TextView) view.findViewById(R.id.txt_projectname);
        edt_remark = (EditText) view.findViewById(R.id.edt_remark);
        btn_savetask = (Button) view.findViewById(R.id.btn_savetask);

        mProgressView = view.findViewById(R.id.login_progress);
        layout_tem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchProjectActivity.class);
                startActivityForResult(intent, 6);
            }
        });
        btn_savetask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存记录
                MobclickAgent.onEvent(context, "consultant_addconsult");
                save();
            }
        });
    }


    /**
     * 添加记录
     */
    private void save() {
        if (mProgressView.getVisibility() == VISIBLE) {
            return;
        }
        showProgress(true);

        if (TextUtils.isEmpty(project_name) || "请选择关注项目".equals(project_name)) {
            showToast(context, "请选择关注项目");
            showProgress(false);
            return;
        }
        if (TextUtils.isEmpty(edt_remark.getText().toString().trim())) {
            showToast(context, "备注信息不能为空");
            showProgress(false);
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("business_cuid", business_id);//客户源id
            jsonObject.put("custom_name", customer_name);//时间
            jsonObject.put("productType", project_id);
            jsonObject.put("content", edt_remark.getText());//备注
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.CONSULTANT_ADDCONSULT, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            String data = response.getString("data");
                            if (!TextUtils.isEmpty(data) && "ok".equals(data)) {

                                showToast(context, "创建咨询成功！");
                                Intent intent = new Intent();
                                intent.setAction("AddRecordFragment");
                                context.sendBroadcast(intent);
                                getActivity().finish();
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                showToast(context, "您的帐号在其他设备登录");
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                showToast(context, "创建咨询失败！");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        showToast(context, "网络不稳定稍后再试");
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        showToast(context, "网络不稳定稍后再试");
                        showProgress(false);
                    }
                }
        );
    }

    private void showToast(Context context, String str) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6 && data != null) {
            project_id = data.getStringExtra("business_id");
            project_name = data.getStringExtra("name");
            txt_projectname.setText(project_name);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_addrecord;
    }

    @Override
    public void fetchData() {

    }

    private void showProgress(final boolean show) {
        if (isAdded()) {
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
    }
}
