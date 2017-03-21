package com.evercare.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.fragment.Consult_ReviewFragment;
import com.evercare.app.fragment.Order_ProjectFragment;
import com.evercare.app.fragment.Sale_CureFragment;
import com.evercare.app.model.PersonDetailInfo;
import com.evercare.app.util.AppBarStateChangeListener;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DateTool;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.view.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.rong.imkit.RongIM;

import static com.evercare.app.R.id.txt_phone;

/**
 * 作者：LXQ on 2016-11-3 2016-11-3
 * 邮箱：842202389@qq.com
 * 客户详情，咨询/回访,消费/治疗
 */
public class CustomerInfoViewPagerActivity extends AppCompatActivity {

    ViewPager mViewPager;
    List<Fragment> mFragments;
    Toolbar mToolbar;

    private TextView txt_customername;
    private TextView custom_phone;

    private AppBarLayout my_appbar;
    private String[] mTitles;/* = new String[]{
            "咨询/回访", "消费记录", "订购项目表"
    };*/
    private PersonDetailInfo personInfo;

    private FloatingActionButton fab_add_task;

    private Context context;

    private ViewPager viewPager;


    private ImageView img_touxiang;
    private TextView txt_age;
    private TextView txt_birth;
    private TextView txt_customer_card;
    private TextView txt_totalamount;
    private TextView txt_advanceremaining;
    private TextView txt_happybremaining;
    private TextView txt_lastdoctor;
    private TextView txt_lastconsumedate;
    private TextView txt_lastcuredate;
    private TextView txt_lastfollowdate;
    private TextView remark_txt;//备注
    private Boolean startFlag;

    private ImageView img_open_order;

    private boolean isAppBarExpend = false;

    private boolean isConsult_Review = true;//ViewPager当前选中项是否是 咨询/回访

    private String custom_id;
    private String business_id;

    private ImageView img_open_chat;
    private TextView txt_illcase;

    private boolean isRelateCustomer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_parallax_snap);
        context = CustomerInfoViewPagerActivity.this;
        Intent intent = getIntent();

        custom_id = intent.getStringExtra("custom_id");
        isRelateCustomer = intent.getBooleanExtra("isRelateCustomer", false);
        startFlag = intent.getBooleanExtra("startFlag", false);
        business_id = intent.getStringExtra("business_id");
        initView();
        arriveTriageStart();
        initData();
    }

    private void arriveTriageStart() {
        if (startFlag) {
            final JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("business_id", "0f26af49-e0bd-e611-80c6-82b73a4d1c19");
                jsonObject.put("business_id", business_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpUtils.postWithJson(context, Constant.ARRIVETRIAGE_START, jsonObject, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                if (!TextUtils.isEmpty(response.getString("data"))) {
                                    if (TextUtils.equals("ok", response.getString("data")) || response.getBoolean("data")) {
                                        //Toast.makeText(OpenOrderActivity.this, "跟进任务推迟成功！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Toast.makeText(OpenOrderActivity.this, "跟进任务推迟失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                                errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                                throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    }
            );
        }
    }


    private void initView() {
        img_open_order = (ImageView) findViewById(R.id.img_open_order);
        img_open_chat = (ImageView) findViewById(R.id.img_open_chat);
        txt_customername = (TextView) findViewById(R.id.txt_customername);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        my_appbar = (AppBarLayout) findViewById(R.id.my_appbar);
        custom_phone = (TextView) findViewById(R.id.custom_phone);
        fab_add_task = (FloatingActionButton) findViewById(R.id.fab_add_task);

        img_touxiang = (ImageView) findViewById(R.id.img_touxiang);
        txt_age = (TextView) findViewById(R.id.txt_age);
        txt_birth = (TextView) findViewById(R.id.txt_birth);
        txt_customer_card = (TextView) findViewById(R.id.txt_customer_card);
        remark_txt = (TextView) findViewById(R.id.remark_txt);
        txt_totalamount = (TextView) findViewById(R.id.txt_totalamount);
        txt_advanceremaining = (TextView) findViewById(R.id.txt_advanceremaining);
        txt_happybremaining = (TextView) findViewById(R.id.txt_happybremaining);
        txt_lastdoctor = (TextView) findViewById(R.id.txt_lastdoctor);
        txt_lastconsumedate = (TextView) findViewById(R.id.txt_lastconsumedate);
        txt_lastcuredate = (TextView) findViewById(R.id.txt_lastcuredate);
        txt_lastfollowdate = (TextView) findViewById(R.id.txt_lastfollowdate);
        txt_illcase = (TextView) findViewById(R.id.txt_illcase);

        fab_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personInfo != null) {
                    Intent intent = new Intent(CustomerInfoViewPagerActivity.this, CreateTaskActivity.class);
                    intent.putExtra("business_cuid", personInfo.getBusiness_id());
                    intent.putExtra("custom_card_id", txt_customer_card.getText());
                    intent.putExtra("custom_name", personInfo.getName());
                    startActivity(intent);
                }
            }
        });

        img_open_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RongIM.getInstance() != null && personInfo != null) {
                    RongIM.getInstance().startPrivateChat(CustomerInfoViewPagerActivity.this, personInfo.getRongcloud_id(),
                            personInfo.getName());
                }
            }
        });

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerInfoViewPagerActivity.this.finish();
            }
        });


        custom_phone.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (personInfo != null && !TextUtils.isEmpty(personInfo.getMobile())) {
                                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + personInfo.getMobile()));
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            }
                                        }

        );
        my_appbar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                                                 @Override
                                                 public void onStateChanged(AppBarLayout appBarLayout, State state) {
                                                     if (!isRelateCustomer) {
                                                         if (state == State.EXPANDED) {
                                                             //展开状态
                                                             fab_add_task.setVisibility(View.GONE);
                                                             //bottom_layout.setVisibility(View.GONE);
                                                             isAppBarExpend = true;

                                                         } else if (state == State.COLLAPSED) {
                                                             if (isConsult_Review) {
                                                                 //折叠状态
                                                                 fab_add_task.setVisibility(View.VISIBLE);
                                                                 //bottom_layout.setVisibility(View.VISIBLE);
                                                             } else {
                                                                 fab_add_task.setVisibility(View.GONE);
                                                                 //bottom_layout.setVisibility(View.GONE);
                                                             }
                                                             isAppBarExpend = false;

                                                         } else {
                                                             //中间状态
                                                             fab_add_task.setVisibility(View.GONE);
                                                             //bottom_layout.setVisibility(View.GONE);
                                                         }
                                                     } else {
                                                         fab_add_task.setVisibility(View.GONE);
                                                     }
                                                 }
                                             }

        );
        mToolbar.hasExpandedActionView();

        setupViewPager();

        my_appbar.setExpanded(false, false);//默认折叠
    }


    private void initData() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("custom_id", custom_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.CUSTOM_DETAIL, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonResult<PersonDetailInfo>>() {
                        }.getType();
                        JsonResult<PersonDetailInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        personInfo = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && personInfo != null) {
                            //无数据 产品让显示--  老子也不愿意这么写代码
                            if (TextUtils.isEmpty(personInfo.getName())) {
                                txt_customername.setText("--");
                            } else {
                                txt_customername.setText(personInfo.getName());
                            }
                            if (TextUtils.isEmpty(personInfo.getMobile())) {
                                custom_phone.setText("--");
                            } else {
                                custom_phone.setText(personInfo.getMobile());
                            }
                            if (personInfo.getSex().equals("0")) {
                                img_touxiang.setImageResource(R.drawable.ic_girl);
                            } else {
                                img_touxiang.setImageResource(R.drawable.ic_boy);
                            }

                            if (TextUtils.isEmpty(personInfo.getAge())) {
                                txt_age.setText("--");
                            } else {
                                txt_age.setText(personInfo.getAge());
                            }
                            //格式化生日到年月日
                            if ("0000-00-00 00:00:00".equals(personInfo.getBirthday())) {
                                txt_birth.setText("--");
                            } else if (!TextUtils.isEmpty(personInfo.getBirthday())) {
                                txt_birth.setText(DateTool.strYmd(personInfo.getBirthday(), "yyyy-MM-dd"));
                            }
                            if (TextUtils.isEmpty(personInfo.getCustom_card())) {
                                txt_customer_card.setText("--");
                            } else {
                                txt_customer_card.setText(personInfo.getCustom_card());
                            }
//                            if (TextUtils.isEmpty(personInfo.getMobile())) {
//                                custom_phone.setVisibility(View.INVISIBLE);
//                            }
                            if (Integer.parseInt(personInfo.getExit_accout()) > 0) {
                                img_open_chat.setVisibility(View.VISIBLE);
                            } else {
                                img_open_chat.setVisibility(View.INVISIBLE);
                            }
                            if (TextUtils.isEmpty(personInfo.getRecord_code())) {
                                txt_illcase.setText("--");
                            } else {
                                txt_illcase.setText(personInfo.getRecord_code());
                            }
                            if (TextUtils.isEmpty(personInfo.getConsume_amount())) {
                                txt_totalamount.setText("--");
                            } else {
                                txt_totalamount.setText(personInfo.getConsume_amount());
                            }
                            if (TextUtils.isEmpty(personInfo.getRemaining())) {
                                txt_advanceremaining.setText("--");
                            } else {
                                txt_advanceremaining.setText(personInfo.getRemaining());
                            }
                            if (TextUtils.isEmpty(personInfo.getHappiness())) {
                                txt_happybremaining.setText("--");
                            } else {
                                txt_happybremaining.setText(personInfo.getHappiness());
                            }
                            if (TextUtils.isEmpty(personInfo.getDoctor())) {
                                txt_lastdoctor.setText("--");
                            } else {
                                txt_lastdoctor.setText(personInfo.getDoctor());
                            }

                            if (TextUtils.isEmpty(personInfo.getFirst_consume_time()) || "0000-00-00 00:00:00".equals(personInfo.getFirst_consume_time())) {
                                txt_lastconsumedate.setText("--");
                            } else {
                                txt_lastconsumedate.setText(personInfo.getFirst_consume_time());
                            }
                            if (TextUtils.isEmpty(personInfo.getLast_cure_time()) || "0000-00-00 00:00:00".equals(personInfo.getLast_cure_time())) {
                                txt_lastcuredate.setText("--");
                            } else {
                                txt_lastcuredate.setText(personInfo.getLast_cure_time());
                            }
                            if (TextUtils.isEmpty(personInfo.getLastFollowDate())) {
                                txt_lastfollowdate.setText("--");
                            } else {
                                txt_lastfollowdate.setText(personInfo.getLastFollowDate());
                            }
                            if (TextUtils.isEmpty(personInfo.getRemark())) {
                                remark_txt.setText("备注: --" + personInfo.getRemark());
                            } else {
                                remark_txt.setText("备注: " + personInfo.getRemark());
                            }
                        } else {
                            custom_phone.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, jsonResult.getMsg() + code, Toast.LENGTH_SHORT).show();
                        }
                    } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                        Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        custom_phone.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Toast.makeText(CustomerInfoViewPagerActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                //loading_progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Toast.makeText(CustomerInfoViewPagerActivity.this, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                //loading_progress.setVisibility(View.GONE);
            }
        });
    }

    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        PagerSlidingTabStrip tabLayout = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabLayout.setViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {

        if (isRelateCustomer) {
            mTitles = new String[]{"咨询记录", "消费记录", "订购项目表"};
        } else {
            mTitles = new String[]{"咨询/回访", "消费记录", "订购项目表"};
        }

        final Consult_ReviewFragment consultReviewFragment = Consult_ReviewFragment.newInstance(custom_id, isRelateCustomer);
        final Sale_CureFragment salecureFragment = Sale_CureFragment.newInstance(custom_id);
        final Order_ProjectFragment order_projectFragment = Order_ProjectFragment.newInstance(custom_id);


        mFragments = new ArrayList<>();
        mFragments.add(consultReviewFragment);
        mFragments.add(salecureFragment);
        mFragments.add(order_projectFragment);

        BaseFragmentAdapter adapter =
                new BaseFragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!isRelateCustomer) {
                    if (position == 0) {
                        isConsult_Review = true;

                        if (!isAppBarExpend) {
                            fab_add_task.setVisibility(View.VISIBLE);
                        }
                    } else {
                        isConsult_Review = false;
                        //salecureFragment.firstLoadData();
                        fab_add_task.setVisibility(View.INVISIBLE);
                    }
                } else {
                    fab_add_task.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
