package com.evercare.app.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.AddressListAdapter;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.PersonInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.PinyinUtils;
import com.evercare.app.util.PrefUtils;
import com.evercare.app.util.SpellingComparator;
import com.evercare.app.view.FlowLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-11-2 15:36
 * 邮箱：842202389@qq.com
 * 客户搜索
 */
public class SearchCustomerActivity extends Activity {

    private RecyclerView customerRecyclerView;
    private EditText edt_keyword;
    private TextView txt_search;

    private ImageView img_delete_all;

    private List<String> customerList = new ArrayList<String>();
    private FlowLayout flowlayout_recent_search;

    private TextView txt_left;
    private TextView txt_center;
    private String type;
    private List<PersonInfo> personInfos;

    private AddressListAdapter adapter;
    private Context context;
    private RelativeLayout search_layout;
    private View mProgressView;


    private View no_value_view;
    private TextView txt_title_info;
    private ImageView img_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_customer);
        context = SearchCustomerActivity.this;
        initView();
        initData();
    }

    private void initData() {
        customerList = PrefUtils.getList(context, "search_list");
        // 循环添加TextView到容器
        for (int i = 0; i < customerList.size(); i++) {
            addSearchedNametoFlow(customerList.get(i));
        }
        if (type.equals(Constant.TEMPORARY)) {
            txt_center.setText("临时库搜索");
            txt_title_info.setText("临时库");
        } else if (type.equals(Constant.PRIVATE_SEA)) {
            txt_center.setText("私有库搜索");
            txt_title_info.setText("私有库");
        } else if (type.equals(Constant.RELATECUSTOMER)) {
            txt_center.setText("关联客户搜索");
            txt_title_info.setText("关联客户");
        }
        txt_left.setText("返回");
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_keyword.setText("");
            }
        });
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 先隐藏键盘
                ((InputMethodManager) edt_keyword.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(SearchCustomerActivity.this
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                if (TextUtils.equals(edt_keyword.getText().toString(), "lianjiedizhi")) {
                    Toast.makeText(SearchCustomerActivity.this, Constant.BASEURL, Toast.LENGTH_SHORT).show();
                } else {
                    mProgressView.setVisibility(View.VISIBLE);

                    if (type.equals(Constant.RELATECUSTOMER)) {
                        searchRelateData(edt_keyword.getText().toString());
                    } else {
                        searchData(edt_keyword.getText().toString());
                    }
                    search_layout.setVisibility(View.GONE);

                    customerList = PrefUtils.getList(context, "search_list");
                    if (!TextUtils.isEmpty(edt_keyword.getText()) && !customerList.contains(edt_keyword.getText().toString())) {
                        if (customerList.size() < 5) {
                            customerList.add(edt_keyword.getText().toString());
                            PrefUtils.setList(context, customerList, "search_list");
                        } else {
                            customerList.remove(0);
                            customerList.add(edt_keyword.getText().toString());
                            PrefUtils.setList(context, customerList, "search_list");
                        }
                    }
                }
            }
        });

        edt_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) edt_keyword.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchCustomerActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    if (TextUtils.equals(edt_keyword.getText().toString(), "lianjiedizhi")) {
                        Toast.makeText(SearchCustomerActivity.this, Constant.BASEURL, Toast.LENGTH_SHORT).show();
                    } else {
                        if (type.equals(Constant.RELATECUSTOMER)) {
                            searchRelateData(edt_keyword.getText().toString());
                        } else {
                            searchData(edt_keyword.getText().toString());
                        }
                        if (!TextUtils.isEmpty(edt_keyword.getText()) && !customerList.contains(edt_keyword.getText().toString())) {
                            customerList.add(edt_keyword.getText().toString());
                            addSearchedNametoFlow(edt_keyword.getText().toString());
                        }
                        if (actionId == 3) {
                            search_layout.setVisibility(View.GONE);
                            mProgressView.setVisibility(View.VISIBLE);
                            customerList = PrefUtils.getList(context, "search_list");
                            if (!TextUtils.isEmpty(edt_keyword.getText()) && !customerList.contains(edt_keyword.getText().toString())) {
                                if (customerList.size() < 5) {
                                    customerList.add(edt_keyword.getText().toString());
                                    PrefUtils.setList(context, customerList, "search_list");
                                } else {
                                    customerList.remove(0);
                                    customerList.add(edt_keyword.getText().toString());
                                    PrefUtils.setList(context, customerList, "search_list");
                                }
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        //清空最近搜索
        img_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flowlayout_recent_search.removeAllViews();
                customerList.clear();
                PrefUtils.setList(context, customerList, "search_list");
            }
        });
    }

    /**
     * 获取所有用户的信息
     *
     * @param personList
     * @return
     */
    private List<PersonInfo> getPersonList(List<PersonInfo> personList) {
        List<PersonInfo> personInfos = new ArrayList<PersonInfo>();
        for (int i = 0; i < personList.size(); i++) {

            String spelliing = PinyinUtils.getPingYin(personList.get(i).getName());
            String firstletter = spelliing.substring(0, 1).toUpperCase();

            PersonInfo personInfo = new PersonInfo();
            personInfo.setName(personList.get(i).getName());
            personInfo.setSpelling(spelliing);
            personInfo.setMobile(personList.get(i).getMobile());
            personInfo.setCustom_id(personList.get(i).getCustom_id());
            personInfo.setAge(personList.get(i).getAge());
            personInfo.setBirthday(personList.get(i).getBirthday());
            personInfo.setCustom_card(personList.get(i).getCustom_card());
            personInfo.setSex(personList.get(i).getSex());
            personInfo.setOcean(personList.get(i).getOcean());
            personInfo.setOverlap(personList.get(i).getOverlap());
            if (firstletter.matches("[A-Z]")) {
                personInfo.setFirstLetter(firstletter);
            } else {
                personInfo.setFirstLetter("#");
            }
            personInfos.add(personInfo);
        }
        return personInfos;
    }

    private void initView() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        no_value_view = findViewById(R.id.no_value_view);
        txt_title_info = (TextView) findViewById(R.id.txt_title_info);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        search_layout = (RelativeLayout) findViewById(R.id.search_layout);
        edt_keyword = (EditText) findViewById(R.id.edt_keyword);
        mProgressView = findViewById(R.id.login_progress);
        img_delete_all = (ImageView) findViewById(R.id.img_delete_all);
        txt_search = (TextView) findViewById(R.id.txt_search);
        txt_left = (TextView) findViewById(R.id.left_text);
        txt_left.setVisibility(View.VISIBLE);
        txt_center = (TextView) findViewById(R.id.center_text);
        customerRecyclerView = (RecyclerView) findViewById(R.id.customerRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchCustomerActivity.this, LinearLayoutManager.VERTICAL, false);
        customerRecyclerView.setLayoutManager(layoutManager);
        flowlayout_recent_search = (FlowLayout) findViewById(R.id.flowlayout_recent_search);

    }

    private void addSearchedNametoFlow(String name) {
        final TextView view = new TextView(this);
        view.setText(name);
        view.setTextColor(getResources().getColor(R.color.gray_7));
        view.setPadding(15, 15, 15, 15);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(14);

        view.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 设置点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_keyword.setText(view.getText());
            }
        });

        // 设置背景选择器到TextView上
        view.setBackgroundResource(R.drawable.txt_white_circle_rectangle_bg);
        flowlayout_recent_search.addView(view);
    }

    private void searchData(String keyword) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyword", keyword);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.postWithJson(context, Constant.CUSTOM_INDEX, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<PersonInfo>>() {
                        }.getType();
                        JsonListResult<PersonInfo> jsonListResult = gson.fromJson(response.toString(), type);

                        String code = jsonListResult.getCode();
                        personInfos = jsonListResult.getData();
                        if (android.text.TextUtils.equals(code, Result.SUCCESS) && personInfos != null && personInfos.size() > 0) {

                            no_value_view.setVisibility(View.GONE);
                            customerRecyclerView.setVisibility(View.VISIBLE);
                            personInfos = getPersonList(personInfos);
                            Collections.sort(personInfos, new SpellingComparator());
                            adapter = new AddressListAdapter(SearchCustomerActivity.this, "SearchCustomerActivity", personInfos, new CustomerItemClickListener() {
                                @Override
                                public void onItemClick(View view, int postion) {
                                    Intent intent = new Intent(SearchCustomerActivity.this, CustomerInfoViewPagerActivity.class);
                                    intent.putExtra("custom_id", personInfos.get(postion).getCustom_id());
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongClick(View view, int position) {

                                }
                            });
                            customerRecyclerView.setAdapter(adapter);

                        } else {
                            customerRecyclerView.setVisibility(View.GONE);
                            no_value_view.setVisibility(View.VISIBLE);
                        }
                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        customerRecyclerView.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mProgressView.setVisibility(View.GONE);
                customerRecyclerView.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mProgressView.setVisibility(View.GONE);
                customerRecyclerView.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
            }
        });
    }


    private void searchRelateData(String keyword) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyword", keyword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.postWithJson(context, Constant.CUSTOM_RELATION, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<PersonInfo>>() {
                        }.getType();
                        JsonListResult<PersonInfo> jsonListResult = gson.fromJson(response.toString(), type);

                        String code = jsonListResult.getCode();
                        personInfos = jsonListResult.getData();
                        if (android.text.TextUtils.equals(code, Result.SUCCESS) && personInfos != null && personInfos.size() > 0) {

                            no_value_view.setVisibility(View.GONE);
                            customerRecyclerView.setVisibility(View.VISIBLE);
                            personInfos = getPersonList(personInfos);
                            Collections.sort(personInfos, new SpellingComparator());
                            adapter = new AddressListAdapter(SearchCustomerActivity.this, "SearchCustomerActivity", personInfos, new CustomerItemClickListener() {
                                @Override
                                public void onItemClick(View view, int postion) {
                                    Intent intent = new Intent(SearchCustomerActivity.this, CustomerInfoViewPagerActivity.class);
                                    intent.putExtra("custom_id", personInfos.get(postion).getCustom_id());
                                    intent.putExtra("isRelateCustomer", true);
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongClick(View view, int position) {

                                }
                            });
                            customerRecyclerView.setAdapter(adapter);

                        } else {
                            customerRecyclerView.setVisibility(View.GONE);
                            no_value_view.setVisibility(View.VISIBLE);
                        }
                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        customerRecyclerView.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mProgressView.setVisibility(View.GONE);
                customerRecyclerView.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mProgressView.setVisibility(View.GONE);
                customerRecyclerView.setVisibility(View.GONE);
                no_value_view.setVisibility(View.VISIBLE);
            }
        });
    }
}
