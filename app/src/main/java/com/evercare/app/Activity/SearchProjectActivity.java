package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.SearchProjectAdapter;
import com.evercare.app.model.ConsultingProductInfo;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.evercare.app.R.id.search_et_input;
import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * 作者：LXQ on 2016-10-28 15:26
 * 邮箱：842202389@qq.com
 * 搜索项目
 */
public class SearchProjectActivity extends Activity {
    private RecyclerView lsv_projects;
    private EditText edt_keyword;
    private ImageView img_delete;

    private TextView txt_left;
    private TextView txt_center;
    private View no_value_view;
    private View bottom_line;
    private List<ConsultingProductInfo> consultingProductInfos;
    private ProgressBar loading_progress;
    private Context context;
    private TextView txt_title_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchproject);
        context = SearchProjectActivity.this;

        initView();

        getProject(new JSONObject());
    }

    private void initView() {
        lsv_projects = (RecyclerView) findViewById(R.id.lsv_projects);
        lsv_projects.setLayoutManager(new LinearLayoutManager(this));
        edt_keyword = (EditText) findViewById(R.id.edt_keyword);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        bottom_line = (View) findViewById(R.id.bottom_line);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);
        bottom_line.setVisibility(View.GONE);
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_center.setText("关注项目");
        txt_left = (TextView) findViewById(R.id.left_text);
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setText("返回");
        txt_title_info.setText("关注项目");
        no_value_view = findViewById(R.id.no_value_view);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edt_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("key_word", edt_keyword.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getProject(jsonObject);
                    return true;
                }

                return false;
            }
        });
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_keyword.setText("");
                getProject(new JSONObject());
            }
        });
    }

    /**
     * 关注项目列表
     *
     * @param jsonObject
     */
    private void getProject(JSONObject jsonObject) {
        showProgress(true);
        HttpUtils.postWithJson(context, Constant.CONSULTINGPRODUCT_LIST, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<ConsultingProductInfo>>() {
                        }.getType();
                        JsonListResult<ConsultingProductInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        consultingProductInfos = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && consultingProductInfos != null && consultingProductInfos.size() > 0) {

                            SearchProjectAdapter adapter = new SearchProjectAdapter(SearchProjectActivity.this, consultingProductInfos, new CustomerItemClickListener() {
                                @Override
                                public void onItemClick(View view, int postion) {
                                    Intent intent = new Intent();
                                    intent.putExtra("business_id", consultingProductInfos.get(postion).getBusiness_id());
                                    intent.putExtra("name", consultingProductInfos.get(postion).getName());
                                    setResult(6, intent);
                                    finish();
                                }

                                @Override
                                public void onItemLongClick(View view, int position) {

                                }
                            });
                            lsv_projects.setAdapter(adapter);
                            lsv_projects.setVisibility(View.VISIBLE);
                            no_value_view.setVisibility(View.GONE);
                        } else {
                            no_value_view.setVisibility(View.VISIBLE);
                            lsv_projects.setVisibility(View.GONE);
                        }
                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        no_value_view.setVisibility(View.VISIBLE);
                        lsv_projects.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                showProgress(false);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loading_progress.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
                lsv_projects.setVisibility(View.GONE);
                showProgress(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loading_progress.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
                lsv_projects.setVisibility(View.GONE);
                showProgress(false);
            }
        });

    }


    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            loading_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            loading_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loading_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            loading_progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
