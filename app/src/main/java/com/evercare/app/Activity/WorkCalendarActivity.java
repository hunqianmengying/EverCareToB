package com.evercare.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.MonthWorkAdapter;
import com.evercare.app.adapter.WorkCalendarAdapter;
import com.evercare.app.model.WorkCalendarInfo;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DateTool;
import com.evercare.app.util.DividerItemDecoration;
import com.evercare.app.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

/**
 * 作者：LXQ on 2016-10-28 10:36
 * 邮箱：842202389@qq.com
 * 工作日历Activity
 */
public class WorkCalendarActivity extends Activity {

    private GestureDetector gestureDetector = null;
    private WorkCalendarAdapter calendarAdapter = null;
    private ViewFlipper flipper = null;
    private GridView gridView = null;
    private int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
    private int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;

    private MonthWorkAdapter adapter;

    private String currentDate;

    private TextView txt_left;
    private TextView txt_center;
    private ImageView img_right;

    private RecyclerView rlv_work_detail;

    /**
     * 当前的年月，现在日历顶端
     */
    private TextView txt_current_month;
    private TextView txt_before_month;
    private TextView txt_next_month;

    private static String[] WEEK = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    private String selectDate;
    private Context context;

    private View select_View;

    private ProgressBar loading_progress;

    private Handler myHandler;

    public WorkCalendarActivity() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date); // 当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workcalendar);

        context = WorkCalendarActivity.this;
        initView();
    }

    /**
     * 获取某天的数据
     *
     * @param time
     */
    private void getWorkData(String time) {

        showProgress(true);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "calendar");
            jsonObject.put("current_time", time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(context, Constant.TASK_INDEX, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            String data = response.getString("data");
                            if (!TextUtils.isEmpty(data)) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<WorkCalendarInfo>>() {
                                }.getType();

                                JsonResult<WorkCalendarInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                WorkCalendarInfo workCalendarInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && workCalendarInfo != null) {
                                    adapter = new MonthWorkAdapter(context, R.layout.day_work_item, workCalendarInfo.getDaydata());
                                    rlv_work_detail.setAdapter(adapter);

                                    if (workCalendarInfo.getMonthwork() != null && workCalendarInfo.getMonthwork().size() > 0 && select_View != null) {

                                        for (int i = 0; i < workCalendarInfo.getMonthwork().size(); i++) {
                                            int index = calendarAdapter.getSomeDayIndex(DateTool.strToDate(workCalendarInfo.getMonthwork().get(i).getDays(), "yyyy-MM-dd"));
                                            if (index >= 0) {
                                                View someView = gridView.getChildAt(index);
                                                RelativeLayout rl_work_num = (RelativeLayout) someView.findViewById(R.id.rl_work_num);
                                                rl_work_num.setVisibility(View.VISIBLE);
                                                TextView txt_up_num = (TextView) someView.findViewById(R.id.txt_up_num);
                                                txt_up_num.setText(workCalendarInfo.getMonthwork().get(i).getTask_num());
                                                TextView txt_face_num = (TextView) someView.findViewById(R.id.txt_face_num);
                                                txt_face_num.setText(workCalendarInfo.getMonthwork().get(i).getAppm_num());
                                            }
                                        }
                                    }
                                } else {
                                    if (adapter != null) {
                                        adapter.clearData();
                                    }
                                    Toast.makeText(context, "未设置日程安排", Toast.LENGTH_LONG).show();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                if (adapter != null) {
                                    adapter.clearData();
                                }
                                Toast.makeText(context, " 未设置日程安排", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable
                            throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(context, " 未设置日程安排", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String
                            responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(context, " 未设置日程安排", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                }
        );
    }


    private void initView() {
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);

        img_right = (ImageView) findViewById(R.id.right_image);
        img_right.setImageResource(R.drawable.ic_calendar);
        img_right.setVisibility(View.VISIBLE);
        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterCurrentMonth();
            }
        });

        rlv_work_detail = (RecyclerView) findViewById(R.id.rlv_work_detail);

        LinearLayoutManager layoutManager = new LinearLayoutManager(WorkCalendarActivity.this, LinearLayoutManager.VERTICAL, false);
        rlv_work_detail.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        rlv_work_detail.addItemDecoration(decoration);

        txt_left = (TextView) findViewById(R.id.left_text);
        txt_center = (TextView) findViewById(R.id.center_text);
        txt_left.setText("返回");
        txt_left.setVisibility(View.VISIBLE);
        txt_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txt_center.setText("日历");

        txt_current_month = (TextView) findViewById(R.id.txt_current_month);
        txt_before_month = (TextView) findViewById(R.id.txt_before_month);
        txt_next_month = (TextView) findViewById(R.id.txt_next_month);

        txt_before_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPrevMonth();
            }
        });

        txt_next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNextMonth();
            }
        });

        gestureDetector = new GestureDetector(this, new MyGestureListener());
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.removeAllViews();
        calendarAdapter = new WorkCalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, true);
        addGridView();
        gridView.setAdapter(calendarAdapter);
        flipper.addView(gridView, 0);
        addTextToTopTextView(txt_current_month);

        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x1222) {
                    setTodaySelected();
                }
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //新启动的线程无法访问该Activity里的组件
                //所以需要通过Handler发送信息
                Message msg = new Message();
                msg.what = 0x1222;
                //发送消息
                myHandler.sendMessage(msg);
            }
        }, (int) (0.5 * 1000));
    }

    private class MyGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
            if (e1.getX() - e2.getX() > 120) {
                // 像左滑动
                enterNextMonth();
                return true;
            } else if (e1.getX() - e2.getX() < -120) {
                // 向右滑动
                enterPrevMonth();
                return true;
            }
            return false;
        }
    }

    /**
     * 移动到下一个月
     *
     * @param
     */
    private void enterNextMonth() {
        addGridView(); // 添加一个gridView
        jumpMonth++; // 下一个月

        calendarAdapter = new WorkCalendarAdapter(this, this.getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, true);
        gridView.setAdapter(calendarAdapter);
        addTextToTopTextView(txt_current_month); // 移动到下一月后，将当月显示在头标题中
        //gvFlag++;
        flipper.addView(gridView, 1);
        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
        flipper.showNext();
        flipper.removeViewAt(0);
    }

    private void enterCurrentMonth() {
        if (jumpMonth > 0) {
            addGridView(); // 添加一个gridView
            jumpMonth = 0; // 上一个月

            calendarAdapter = new WorkCalendarAdapter(this, this.getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, true);
            gridView.setAdapter(calendarAdapter);

            addTextToTopTextView(txt_current_month); //移动到上一月后，将当月显示在头标题中
            flipper.addView(gridView, 1);

            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
            flipper.showPrevious();
            flipper.removeViewAt(0);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //新启动的线程无法访问该Activity里的组件
                    //所以需要通过Handler发送信息
                    Message msg = new Message();
                    msg.what = 0x1222;
                    //发送消息
                    myHandler.sendMessage(msg);
                }
            }, (int) (0.5 * 1000));
        } else if (jumpMonth < 0) {
            addGridView(); // 添加一个gridView
            jumpMonth = 0; // 下一个月

            calendarAdapter = new WorkCalendarAdapter(this, this.getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, true);
            gridView.setAdapter(calendarAdapter);
            addTextToTopTextView(txt_current_month); // 移动到下一月后，将当月显示在头标题中
            //gvFlag++;
            flipper.addView(gridView, 1);
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
            flipper.showNext();
            flipper.removeViewAt(0);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //新启动的线程无法访问该Activity里的组件
                    //所以需要通过Handler发送信息
                    Message msg = new Message();
                    msg.what = 0x1222;
                    //发送消息
                    myHandler.sendMessage(msg);
                }
            }, (int) (0.5 * 1000));
        } else {
            setTodaySelected();
        }
    }

    private void setTodaySelected() {

        int position = calendarAdapter.getTodayIndex();

        select_View = gridView.getChildAt(position);
        if (select_View != null) {
            TextView tem_text = (TextView) select_View.findViewById(R.id.txt_day_number);
            if (tem_text != null && (boolean) tem_text.getTag(R.id.tag_click_able)) {

                // 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
                int startPosition = calendarAdapter.getStartPositon();
                int endPosition = calendarAdapter.getEndPosition();
                if (startPosition <= position + 7 && position <= endPosition - 7) {
                    String scheduleDay = "" + calendarAdapter.getDateByClickItem(position).getDay();
                    //这一天的阴历
                    String scheduleYear = calendarAdapter.getShowYear();
                    String scheduleMonth = calendarAdapter.getShowMonth();
                    selectDate = scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;

                    getWorkData(selectDate);
                }

                for (int i = 0; i < gridView.getCount(); i++) {
                    View v = gridView.getChildAt(i);
                    TextView txt = (TextView) v.findViewById(R.id.txt_day_number);
                    RelativeLayout rl_work_num = (RelativeLayout) v.findViewById(R.id.rl_work_num);
                    if (position == i) {//当前选中的Item改变背景颜色
                        txt.setTextColor(ContextCompat.getColor(WorkCalendarActivity.this, R.color.white));
                        txt.setBackgroundResource(R.drawable.txt_circle_bg);
                        rl_work_num.setVisibility(View.VISIBLE);
                    } else {
                        int color = (int) txt.getTag(R.id.tag_color);
                        txt.setTextColor(ContextCompat.getColor(WorkCalendarActivity.this, color));
                        txt.setBackgroundResource(R.color.transparent);
                        rl_work_num.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    /**
     * 移动到上一个月
     *
     * @param
     */
    private void enterPrevMonth() {
        addGridView(); // 添加一个gridView
        jumpMonth--; // 上一个月

        calendarAdapter = new WorkCalendarAdapter(this, this.getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, true);
        gridView.setAdapter(calendarAdapter);

        addTextToTopTextView(txt_current_month); //移动到上一月后，将当月显示在头标题中
        flipper.addView(gridView, 1);

        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
        flipper.showPrevious();
        flipper.removeViewAt(0);
    }

    /**
     * 添加头部的年份 闰哪月等信息
     *
     * @param view
     */
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        textDate.append(calendarAdapter.getShowYear()).append("年").append(calendarAdapter.getShowMonth()).append("月").append("\t");
        view.setText(textDate);
    }

    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // 取得屏幕的宽度和高度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth();
        int Height = display.getHeight();

        gridView = new GridView(this);
        gridView.setNumColumns(7);
        gridView.setColumnWidth(Width / 7);
        if (Width == 720 && Height == 1280) {
            gridView.setColumnWidth(40);
        }
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 去除gridView边框
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setOnTouchListener(new OnTouchListener() {
            // 将gridview中的触摸事件回传给gestureDetector
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return WorkCalendarActivity.this.gestureDetector.onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                select_View = arg0.getChildAt(position);
                TextView tem_text = (TextView) select_View.findViewById(R.id.txt_day_number);
                if ((boolean) tem_text.getTag(R.id.tag_click_able)) {

                    // 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
                    int startPosition = calendarAdapter.getStartPositon();
                    int endPosition = calendarAdapter.getEndPosition();
                    if (startPosition <= position + 7 && position <= endPosition - 7) {
                        String scheduleDay = "" + calendarAdapter.getDateByClickItem(position).getDay();
                        //这一天的阴历
                        String scheduleYear = calendarAdapter.getShowYear();
                        String scheduleMonth = calendarAdapter.getShowMonth();
                        selectDate = scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;

                        getWorkData(selectDate);
                    }

                    for (int i = 0; i < arg0.getCount(); i++) {
                        View v = arg0.getChildAt(i);
                        TextView txt = (TextView) v.findViewById(R.id.txt_day_number);
                        RelativeLayout rl_work_num = (RelativeLayout) v.findViewById(R.id.rl_work_num);
                        if (position == i) {//当前选中的Item改变背景颜色
                            txt.setTextColor(ContextCompat.getColor(WorkCalendarActivity.this, R.color.white));
                            txt.setBackgroundResource(R.drawable.txt_circle_bg);
                            rl_work_num.setVisibility(View.VISIBLE);
                        } else {
                            int color = (int) txt.getTag(R.id.tag_color);
                            txt.setTextColor(ContextCompat.getColor(WorkCalendarActivity.this, color));
                            txt.setBackgroundResource(R.color.transparent);
                            rl_work_num.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
        gridView.setLayoutParams(params);
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