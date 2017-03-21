package com.evercare.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Activity.BaseFragment;
import com.evercare.app.Activity.LoginActivity;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.ConsultRecordAdapter;
import com.evercare.app.model.ConsultReviewInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.delay;

/**
 * 作者：LXQ on 2016-11-3 15:31
 * 邮箱：842202389@qq.com
 * 咨询/回访
 */
public class Consult_ReviewFragment extends BaseFragment {

    private RecyclerView rlv_consult_review;
    private static final String KEY = "key";
    private static String ARGUMENT = "custom_id";

    private String title = "咨询/回访";

    private Button btn_select_date;

    private ConsultRecordAdapter adapter;

    private ListView lsv_hospitals;
    private List<String> hospitalList;

    private ListView lsv_type;
    private List<String> typeList;

    private ArrayAdapter hospitalsAdapter;
    private ArrayAdapter typeAdapter;

    private PopupWindow popupWindow;
    private PopupWindow typePopupWindow;

    private ImageView img_select_item;

    private Context context;

    private ConsultReviewInfo consultReviewInfo;

    private String custom_id;
    private String org_id;
    private String type;

    private View no_value_view;
    private TextView txt_title_info;

    private ProgressBar loading_progress;
    private boolean isRelateCustomer;

    public static Consult_ReviewFragment newInstance(String custom_id, boolean isrelateCustomer) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, custom_id);
        bundle.putBoolean("isRelateCustomer", isrelateCustomer);
        Consult_ReviewFragment contentFragment = new Consult_ReviewFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    private void showProgressBar(boolean isShow) {
        loading_progress.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    @Override
    protected void initView(View view) {
        context = getContext();

        Bundle arguments = getArguments();
        if (arguments != null) {
            custom_id = arguments.getString(ARGUMENT, "");
            isRelateCustomer = arguments.getBoolean("isRelateCustomer");
        }
        txt_title_info = (TextView) view.findViewById(R.id.txt_title_info);

        img_select_item = (ImageView) view.findViewById(R.id.img_select_item);

        if (isRelateCustomer) {
            txt_title_info.setText("咨询");
            img_select_item.setVisibility(View.INVISIBLE);
        } else {
            txt_title_info.setText("咨询/回访");
            img_select_item.setVisibility(View.VISIBLE);
            img_select_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTypePopupWindow(v);
                }
            });
        }

        no_value_view = (View) view.findViewById(R.id.no_value_view);

        loading_progress = (ProgressBar) view.findViewById(R.id.loading_progress);

        rlv_consult_review = (RecyclerView) view.findViewById(R.id.rlv_consult_review);

        btn_select_date = (Button) view.findViewById(R.id.btn_select_date);
        btn_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });



        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL);
        rlv_consult_review.setLayoutManager(layoutManager);
        if (NetUtil.isNetConnected(context)) {
            showProgressBar(true);
            searchData("", "");
        } else {
            Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_consult_review;
    }

    @Override
    public void fetchData() {

    }

    private void searchData(String orgId, String type) {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custom_id", custom_id);

            if (!TextUtils.equals(orgId, "")) {
                jsonObject.put("_org_id", orgId);
            }

            if (isRelateCustomer) {
                jsonObject.put("type", "1");
            } else {
                if (!TextUtils.equals(type, "")) {
                    jsonObject.put("type", type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.CUSTOM_CONSULT, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<ConsultReviewInfo>>() {
                                }.getType();
                                JsonResult<ConsultReviewInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                consultReviewInfo = jsonResult.getData();

                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && consultReviewInfo != null) {
                                    if (consultReviewInfo.getOrgs() != null && consultReviewInfo.getOrgs().size() > 0) {
                                        hospitalList.clear();

                                        for (int i = 0; i < consultReviewInfo.getOrgs().size(); i++) {
                                            hospitalList.add(consultReviewInfo.getOrgs().get(i).getName());
                                        }
                                        btn_select_date.setText(hospitalList.get(0));
                                        hospitalsAdapter.notifyDataSetChanged();
                                    } else {
                                        //btn_select_date.setText("暂无数据");
                                    }

                                    if (consultReviewInfo.getList() != null && consultReviewInfo.getList().size() > 0) {
                                        adapter = new ConsultRecordAdapter(context, R.layout.consultrecorditem, consultReviewInfo.getList());
                                        no_value_view.setVisibility(View.GONE);
                                        rlv_consult_review.setVisibility(View.VISIBLE);
                                        rlv_consult_review.setAdapter(adapter);
                                        btn_select_date.setVisibility(View.VISIBLE);
                                        //img_select_item.setVisibility(View.VISIBLE);
                                    } else {
                                        no_value_view.setVisibility(View.VISIBLE);
                                        rlv_consult_review.setVisibility(View.GONE);
                                        btn_select_date.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    no_value_view.setVisibility(View.VISIBLE);
                                    rlv_consult_review.setVisibility(View.GONE);
                                    btn_select_date.setVisibility(View.INVISIBLE);
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                no_value_view.setVisibility(View.VISIBLE);
                                rlv_consult_review.setVisibility(View.GONE);
                                btn_select_date.setVisibility(View.INVISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgressBar(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        loading_progress.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                        rlv_consult_review.setVisibility(View.GONE);
                        btn_select_date.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading_progress.setVisibility(View.GONE);
                        no_value_view.setVisibility(View.VISIBLE);
                        rlv_consult_review.setVisibility(View.GONE);
                        btn_select_date.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }


    private void showPopupWindow(View view) {

        if (hospitalList.size() > 0) {
            // 设置好参数之后再show
            popupWindow.showAsDropDown(view);
        }
    }

    private void initPopupWindow() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.dateselectpopup, null);

        lsv_hospitals = (ListView) contentView.findViewById(R.id.lsv_hospitals);
        lsv_hospitals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                org_id = consultReviewInfo.getOrgs().get(position).getId();

                btn_select_date.setText(consultReviewInfo.getOrgs().get(position).getName());
                showProgressBar(true);
                searchData(org_id, type);
                popupWindow.dismiss();
            }
        });

        lsv_hospitals.setAdapter(hospitalsAdapter);
        popupWindow = new PopupWindow(contentView,
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

    }


    public void initData() {
        hospitalList = new ArrayList<>();
        typeList = new ArrayList<>();

        typeList.add("全部");
        typeList.add("咨询");
        typeList.add("预约");
        typeList.add("任务");

        hospitalsAdapter = new ArrayAdapter(getContext(), R.layout.listtext_item, hospitalList);
        typeAdapter = new ArrayAdapter(getContext(), R.layout.listtext_item, typeList);

        initPopupWindow();
        initTypePopupWindow();

        MyBoradcast myBoradcast = new MyBoradcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("AddRecordFragment");
        context.registerReceiver(myBoradcast, filter);


    }

    private void showTypePopupWindow(View view) {
        typePopupWindow.showAsDropDown(view);
    }

    private void initTypePopupWindow() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.dateselectpopup, null);

        lsv_type = (ListView) contentView.findViewById(R.id.lsv_hospitals);

        lsv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = position == 0 ? "" : String.valueOf(position);
                showProgressBar(true);
                searchData(org_id, type);
                typePopupWindow.dismiss();
            }
        });
        lsv_type.setAdapter(typeAdapter);

        typePopupWindow = new PopupWindow(contentView,
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);

        typePopupWindow.setTouchable(true);
        typePopupWindow.setOutsideTouchable(true);
        typePopupWindow.setFocusable(true);
        typePopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    final Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                searchData(org_id, type);
            }
        }
    };


    public class MyBoradcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (null != intent) {
                if ("AddRecordFragment".equals(intent.getAction())) {
                    TimerTask task = new TimerTask() {
                        public void run() {
                            Message msg = m_handler.obtainMessage();
                            msg.arg1 = 0;
                            m_handler.sendMessage(msg);
                        }
                    };
                    //管管产品说以后后台统一 这里就不需要6秒延迟
                    Timer timer = new Timer();
                    timer.schedule(task, 6000);
                }
            }
        }
    }

}
