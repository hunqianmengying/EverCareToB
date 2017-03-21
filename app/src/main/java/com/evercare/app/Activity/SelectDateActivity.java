package com.evercare.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.evercare.app.R;
import com.evercare.app.util.DateTool;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 作者：LXQ on 2016-10-31 10:26
 * 邮箱：842202389@qq.com
 * 选择任务时间
 */
public class SelectDateActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_cancle;
    private RadioButton rdb_tomorrow;
    private RadioButton rdb_day_after_tomorrow;
    private RadioButton rdb_next_week;
    private RadioButton rdb_custom_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdate);
        initView();
    }

    private void initView() {
        btn_cancle = (Button) findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(this);

        rdb_tomorrow = (RadioButton) findViewById(R.id.rdb_tomorrow);
        rdb_tomorrow.setOnClickListener(this);

        rdb_day_after_tomorrow = (RadioButton) findViewById(R.id.rdb_day_after_tomorrow);
        rdb_day_after_tomorrow.setOnClickListener(this);


        rdb_next_week = (RadioButton) findViewById(R.id.rdb_next_week);
        rdb_next_week.setOnClickListener(this);

        rdb_custom_settings = (RadioButton) findViewById(R.id.rdb_custom_settings);
        rdb_custom_settings.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        switch (v.getId()) {
            case R.id.btn_cancle:
                finish();
                break;
            case R.id.rdb_tomorrow:
                calendar.add(Calendar.DATE, 1);
                Intent intent1 = new Intent();
                intent1.putExtra("selectDate", DateTool.dateToString(calendar.getTime(), "yyyy-MM-dd"));
                setResult(1, intent1);
                finish();
                break;
            case R.id.rdb_day_after_tomorrow:
                calendar.add(Calendar.DATE, 2);
                Intent intent2 = new Intent();
                intent2.putExtra("selectDate", DateTool.dateToString(calendar.getTime(), "yyyy-MM-dd"));
                setResult(1, intent2);
                finish();
                break;
            case R.id.rdb_next_week:
                calendar.add(Calendar.DATE, 7);
                Intent intent3 = new Intent();
                intent3.putExtra("selectDate", DateTool.dateToString(calendar.getTime(), "yyyy-MM-dd"));
                setResult(1, intent3);
                finish();
                break;
            case R.id.rdb_custom_settings:
                Intent intent = new Intent(SelectDateActivity.this, CalendarActivity.class);
                intent.putExtra("currentTime", DateTool.getCurrentTime("yyyy-MM-dd"));
                startActivityForResult(intent, 1);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                setResult(1, data);
                finish();
            }
        }
    }
}
