package com.evercare.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.ProjectListAdapter;
import com.evercare.app.model.CustomerItemClickListener;
import com.evercare.app.model.ProjectInfoItem;
import com.evercare.app.model.TaskDetailInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.RWTool;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-12-6 16:16
 * 邮箱：842202389@qq.com
 * 订单列表界面
 */
public class ProjectListActivity extends BaseActivity {
    private RecyclerView rlv_project_list;
    private Button btn_confirm;
    private TextView txt_price_count;
    private TextView txt_center;
    private TextView txt_left;

    private TextView txt_right;

    private List<ProjectInfoItem> datas;
    private ProjectListAdapter adapter;

    private Context context;

    private String name;
    private String price;
    private String productID;
    private String frequency;


    private String business_id;
    private String business_cuid;
    private String business_project_id;
    private String custom_name;
    private String description;

    private ProgressBar loading_progress;
    private boolean isPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        context = ProjectListActivity.this;

        RWTool.activityList.add(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        price = intent.getStringExtra("price");
        productID = intent.getStringExtra("ProductID");
        frequency = intent.getStringExtra("frequency");
        isPackage = intent.getBooleanExtra("isPackage", false);

        business_cuid = intent.getStringExtra("business_cuid");
        business_id = intent.getStringExtra("business_id");
        business_project_id = intent.getStringExtra("business_project_id");
        custom_name = intent.getStringExtra("custom_name");
        description = intent.getStringExtra("description");


        initView();
        initData();
        addItem();
    }

    private void addItem() {
        ProjectInfoItem item = new ProjectInfoItem();
        item.setProject_name(name);
        item.setPricePerUnit(price);
        item.setProductID(productID);
        item.setOldPrice(price);
        item.setNewNum(frequency);
        item.setPackage(isPackage);
        item.setQuantity("1");
        datas.add(item);
        adapter.notifyDataSetChanged();
        getPriceCount();
    }

    private void initView() {

        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);

        rlv_project_list = (RecyclerView) findViewById(R.id.rlv_project_list);
        rlv_project_list.setLayoutManager(new LinearLayoutManager(this));
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        txt_right = (TextView) findViewById(R.id.right_text);
        txt_right.setText("编辑");
        txt_right.setVisibility(View.VISIBLE);
        txt_price_count = (TextView) findViewById(R.id.txt_price_count);
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_left = (TextView) findViewById(R.id.left_text);

        txt_center.setText("订单列表");
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setText("返回");

        txt_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (txt_right.getText().toString()) {
                    case "编辑":
                        for (int i = 0; i < datas.size(); i++) {
                            datas.get(i).setShowImg(true);
                        }
                        txt_right.setText("完成");
                        break;
                    case "完成":
                        for (int i = 0; i < datas.size(); i++) {
                            datas.get(i).setShowImg(false);
                        }
                        txt_right.setText("编辑");
                        break;
                }
                getPriceCount();
                adapter.notifyDataSetChanged();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrder();
            }
        });

        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {

        datas = new ArrayList<>();

        adapter = new ProjectListAdapter(ProjectListActivity.this, datas, new CustomerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= datas.size()) {
                    Intent intent = new Intent(ProjectListActivity.this, PriceListActivity.class);
                    intent.putExtra("isFromProjectList", true);
                    intent.putExtra("isSelectProject", true);
                    startActivityForResult(intent, 2);
                } else {
                    switch (view.getId()) {
                        case R.id.img_delete_project:
                            datas.remove(position);
                            getPriceCount();
                            adapter.notifyDataSetChanged();
                            break;
//                        case R.id.txt_project_num:
//                        case R.id.txt_project_price:
                        default:
                            Intent intent = new Intent(ProjectListActivity.this, EditNumberActivity.class);
                            intent.putExtra("number", datas.get(position).getQuantity());
                            intent.putExtra("price", datas.get(position).getPricePerUnit());
                            intent.putExtra("isPackage", datas.get(position).isPackage());
                            intent.putExtra("oldprice", datas.get(position).getOldPrice());
                            intent.putExtra("remark", datas.get(position).getRemark());
                            intent.putExtra("index", position);
                            startActivityForResult(intent, 1);
                            break;
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        getPriceCount();
        rlv_project_list.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                //修改单价和数量
                int index = data.getIntExtra("index", 0);
                String price = data.getStringExtra("price");
                String number = data.getStringExtra("number");
                String remark = data.getStringExtra("remark");

                ProjectInfoItem item = datas.get(index);
                item.setPricePerUnit(price);
                item.setQuantity(number);
                item.setRemark(remark);

                adapter.notifyDataSetChanged();
                getPriceCount();
            }
        } else if (requestCode == 2) {
            //点击添加项目
            if (resultCode == 1) {
                ProjectInfoItem item = new ProjectInfoItem();
                item.setProject_name(data.getStringExtra("name"));
                item.setPricePerUnit(data.getStringExtra("price"));
                item.setProductID(data.getStringExtra("ProductID"));
                item.setOldPrice(data.getStringExtra("price"));
                item.setPackage(data.getBooleanExtra("isPackage", false));
                item.setNewNum(data.getStringExtra("frequency"));
                item.setQuantity("1");
                datas.add(item);
                adapter.notifyDataSetChanged();
                getPriceCount();
            }
        }
    }

    private void getPriceCount() {
        double count = 0;
        for (int i = 0; i < datas.size(); i++) {
            count += Double.parseDouble(datas.get(i).getQuantity()) * Double.parseDouble(datas.get(i).getPricePerUnit());
        }
        txt_price_count.setText(String.valueOf(count));
    }


    /**
     * 开单事件
     */
    private void openOrder() {

        if (datas == null || datas.size() <= 0) {
            Toast.makeText(context, "订单列表不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            loading_progress.setVisibility(View.VISIBLE);
            JSONArray jArray = new JSONArray();

            try {
                for (int i = 0; i < datas.size(); i++) {
                    JSONObject jObj = new JSONObject();
                    jObj.put("ProductID", datas.get(i).getProductID());
                    jObj.put("Quantity", datas.get(i).getQuantity());
                    jObj.put("IsPriceOverridden", datas.get(i).isPriceOverridden());
                    jObj.put("newNum", datas.get(i).getNewNum());
                    jObj.put("newNursingType", datas.get(i).getNewNursingType());
                    jObj.put("PricePerUnit", datas.get(i).getPricePerUnit());
                    jObj.put("Description", datas.get(i).getRemark());
                    jArray.put(jObj);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("business_id", business_id);
                //jsonObject.put("business_cuid", "B59299B1-1E2A-E611-80D0-8759DE0BACBC");
                jsonObject.put("business_cuid", business_cuid);
                jsonObject.put("custom_name", custom_name);
                //jsonObject.put("price_level_id", taskDetailInfo.getCustom_name());
                jsonObject.put("description", description);
                jsonObject.put("business_project_id", business_project_id);
                jsonObject.put("sales_order_detail", jArray);

            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpUtils.postWithJson(context, Constant.ARRIVERTRIAGE_CREATE, jsonObject, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                if (!TextUtils.isEmpty(response.getString("data"))) {
                                    if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                        Toast.makeText(context, "开单成功！", Toast.LENGTH_SHORT).show();
                                        RWTool.exitClient(ProjectListActivity.this);
                                    } else {
                                        Toast.makeText(context, "开单失败！", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                    Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(context, "开单失败！", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            loading_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                            loading_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            loading_progress.setVisibility(View.GONE);
                            Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}
