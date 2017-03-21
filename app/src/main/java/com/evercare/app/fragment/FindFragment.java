package com.evercare.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Activity.InformationMessageActivity;
import com.evercare.app.Activity.PreferenceActivity;
import com.evercare.app.Activity.PriceListActivity;
import com.evercare.app.Entity.JsonListResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.model.ActivityItemInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：xlren on 2016/8/29 13:23
 * 邮箱：renxianliang@126.com
 * 发现
 */

public class FindFragment extends Fragment {

    protected static final String TAG = "FindFragment";
    private TextView center_text;
    private SimpleDraweeView find_image;
    private RelativeLayout image_view;
    private TextView txt_content;

    private GridView contentGridView;
    private List<ActivityItemInfo> activityItemInfos;
    private String[] iconNames = {"价目", "案例", "活动"};
    private int[] icons = {R.drawable.ic_pricelist, R.drawable.ic_case, R.drawable.ic_activity};

    private List<Map<String, Object>> dataList;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        context = getContext();
        initView(view);
        initData();
        initListener();
        return view;
    }

    private void initView(View view) {
        find_image = (SimpleDraweeView) view.findViewById(R.id.find_image);
        center_text = (TextView) view.findViewById(R.id.center_text);
        contentGridView = (GridView) view.findViewById(R.id.contentGridView);
        image_view = (RelativeLayout) view.findViewById(R.id.image_view);
        txt_content = (TextView) view.findViewById(R.id.txt_content);
    }


    private void initData() {
        center_text.setText("发现");
        dataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icons[i]);
            map.put("text", iconNames[i]);
            dataList.add(map);
        }

        String[] from = {"image", "text"};

        int[] to = {R.id.image, R.id.text};

        SimpleAdapter adapter = new SimpleAdapter(context, dataList, R.layout.imagebuttonitem, from, to);
        contentGridView.setAdapter(adapter);

        contentGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = iconNames[position];
                switch (str) {
                    case "活动":
                    case "案例":
                        if (TextUtils.equals(str, "活动")) {
                            MobclickAgent.onEvent(context, "activity_page");
                        } else if (TextUtils.equals(str, "案例")) {
                            MobclickAgent.onEvent(context, "case_page");
                        }
                        Intent intent = new Intent(context, PreferenceActivity.class);
                        intent.putExtra("type", str);
                        startActivity(intent);
                        break;
                    case "价目":
                        MobclickAgent.onEvent(context, "price_list_page");
                        Intent intent1 = new Intent(context, PriceListActivity.class);
                        startActivity(intent1);
                        break;
                }
            }
        });

        getData();
    }

    /**
     * 调用接口，显示数据
     */
    private void getData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("page", "1");
            jsonObject.put("row", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getActivity(jsonObject);
    }


    /**
     * 获取活动列表中的权重最高的
     * page row 均为1
     *
     * @param jsonObject
     */
    private void getActivity(JSONObject jsonObject) {
        HttpUtils.postWithJson(context, Constant.GET_ACTIVITY_LIST, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonListResult<ActivityItemInfo>>() {
                        }.getType();
                        JsonListResult<ActivityItemInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        activityItemInfos = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && activityItemInfos != null && activityItemInfos.size() > 0) {
                            txt_content.setText(activityItemInfos.get(0).getTitle());
                            find_image.setImageURI(Constant.BASEURL_IMG + activityItemInfos.get(0).getImage());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    String str = errorResponse.toString();
                    Toast.makeText(context, str + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initListener() {
        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityItemInfos.size() > 0) {
                    MobclickAgent.onEvent(context, "show_header_view");
                    Intent intent = new Intent(context, InformationMessageActivity.class);
                    intent.putExtra("name", "活动详情");
                    intent.putExtra("url", activityItemInfos.get(0).getLink());
                    startActivity(intent);
                }
            }
        });
    }
}
