package com.evercare.app.fragment;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.Activity.AchievmentActivity;
import com.evercare.app.Activity.AddCustomerActivity;
import com.evercare.app.Activity.BackReviewActivity;
import com.evercare.app.Activity.CreateTaskActivity;
import com.evercare.app.Activity.CustomerBirthdayActivity;
import com.evercare.app.Activity.FollowDiagnoseActivity;
import com.evercare.app.Activity.LoginActivity;
import com.evercare.app.Activity.MaturingCustomerActivity;
import com.evercare.app.Activity.NewsActivity;
import com.evercare.app.Activity.ProjectListActivity;
import com.evercare.app.Activity.TodayOpenOrderActivity;
import com.evercare.app.Activity.TodayWorkActivity;
import com.evercare.app.Activity.TradeRatesActivity;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.R;
import com.evercare.app.adapter.MenuAdapter;
import com.evercare.app.model.HomeModelInfo;
import com.evercare.app.model.LoginInfo;
import com.evercare.app.model.MenuItemInfo;
import com.evercare.app.model.UpdateInfo;
import com.evercare.app.util.CommonUtil;
import com.evercare.app.util.Constant;
import com.evercare.app.util.DownloadUtils;
import com.evercare.app.util.HttpUtils;
import com.evercare.app.util.PrefUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cz.msebera.android.httpclient.Header;

import static android.R.attr.password;
import static com.evercare.app.R.id.loading_progress;
import static com.umeng.socialize.a.j.S;
import static com.umeng.socialize.utils.ContextUtil.getContext;
import static io.rong.imlib.statistics.UserData.phone;

/**
 * 作者：xlren on 2016/8/29 13:23
 * 邮箱：renxianliang@126.com
 * 首页显示销售额／成交率饼状图 以及客户相关任务
 */
public class HomeFragment extends Fragment implements
        OnChartValueSelectedListener {
    protected static final String TAG = "FindFragment";

    private ProgressBar progressBar;
    private TextView center_text;
    private ImageView right_image;
    //销售额
    private PieChart salesAmountChart;
    //新客成交率
    private PieChart new_turnoverRate;

    //老客成交率
    private PieChart old_turnoverRate;

    //菜单gridview
    private GridView menu_grid_view;

    private List<MenuItemInfo> menuItemList;
    private View bottom_line;

    private Context context;

    private HomeModelInfo homeModelInfo;

    private MenuAdapter adapter;
    private DecimalFormat df = new DecimalFormat("#0.#");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        initViews(view);
        //默认显示数据
        initChart("<b>0</b><br/>" + "<small>0%</small>", salesAmountChart, 0);
        initChart("<b>0</b><br/>" + "<small>0%</small>", new_turnoverRate, 1);
        initChart("<b>0</b><br/>" + "<small>0%</small>", old_turnoverRate, 2);
        initData();
        checkUpdate();
        return view;
    }


    private void initViews(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        center_text = (TextView) view.findViewById(R.id.center_text);
        right_image = (ImageView) view.findViewById(R.id.right_image);
        salesAmountChart = (PieChart) view.findViewById(R.id.pieChart_sales_amount);
        new_turnoverRate = (PieChart) view.findViewById(R.id.pieChart_newturnover_rate);
        old_turnoverRate = (PieChart) view.findViewById(R.id.pieChart_oldturnover_rate);
        menu_grid_view = (GridView) view.findViewById(R.id.menu_grid_view);
        bottom_line = view.findViewById(R.id.bottom_line);
    }

    private void initChart(String name, PieChart chart, int type) {
        chart.setUsePercentValues(true);
        chart.setDescription("");
        chart.setExtraOffsets(5, 0, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterText(Html.fromHtml(name));
        chart.setCenterTextColor(Color.WHITE);

        chart.setDrawCenterText(true);
        chart.setDrawHoleEnabled(true);

        //内圈文字背景
        //chart.setHoleColor(Color.WHITE);
        chart.setHoleColor(ContextCompat.getColor(getContext(), R.color.header_bg));

        //内圈圆环背景
        chart.setTransparentCircleColor(Color.BLACK);
        chart.setTransparentCircleAlpha(110);
        //内环大小
        chart.setHoleRadius(88f);
        chart.setTransparentCircleRadius(30f);


        chart.setRotationAngle(90);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false);
        setData(chart, type);

        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);


        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f);
        legend.setEnabled(false);
        chart.setDrawEntryLabels(false);
    }


    private void initData() {
        center_text.setText("首页");
        right_image.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        bottom_line.setVisibility(View.GONE);

        right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(context, "home_news");
                Intent intent = new Intent(context, NewsActivity.class);
                startActivity(intent);
            }
        });
        initMenuList();
        //销售额详情
        salesAmountChart.setHighlightPerTapEnabled(false);
        salesAmountChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    MobclickAgent.onEvent(context, "sales_volume");
                    Intent intent = new Intent(context, AchievmentActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        //新客成交率详情
        new_turnoverRate.setHighlightPerTapEnabled(false);
        new_turnoverRate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    MobclickAgent.onEvent(context, "new_deal_rate");

                    Intent intent = new Intent(context, TradeRatesActivity.class);
                    intent.putExtra("type", "新客成交率");
                    startActivity(intent);
                }
                return true;
            }
        });
        //老客成交率
        old_turnoverRate.setHighlightPerTapEnabled(false);
        old_turnoverRate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    MobclickAgent.onEvent(context, "old_deal_rate");
                    Intent intent = new Intent(context, TradeRatesActivity.class);
                    intent.putExtra("type", "老客成交率");
                    startActivity(intent);
                }
                return true;
            }
        });
        try {
            adapter = new MenuAdapter(getContext(), menuItemList);
            menu_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (menuItemList.get(position).getMenuDescription()) {
                        case "今日可开单":
                            MobclickAgent.onEvent(context, "today_open_order");
                            Intent intent = new Intent(context, TodayOpenOrderActivity.class);
                            startActivity(intent);
                            break;
                        case "今日工作":
                            MobclickAgent.onEvent(context, "today_work");
                            Intent intent4 = new Intent(context, TodayWorkActivity.class);
                            startActivity(intent4);
                            break;
                        case "生日客户":
                            MobclickAgent.onEvent(context, "birthday_customer");
                            Intent intent1 = new Intent(context, CustomerBirthdayActivity.class);
                            startActivity(intent1);
                            break;
                        case "活动回访":
                            MobclickAgent.onEvent(context, "activity_review");
                            Intent intent5 = new Intent(context, BackReviewActivity.class);
                            intent5.putExtra("type", "activity");
                            startActivity(intent5);
                            break;
                        case "其他回访":
                            MobclickAgent.onEvent(context, "other_review");
                            Intent intent6 = new Intent(context, BackReviewActivity.class);
                            intent6.putExtra("type", "otheractivity");
                            startActivity(intent6);
                            break;
                        case "新分客户":
                            MobclickAgent.onEvent(context, "add_new_customer");
                            Intent intent7 = new Intent(context, AddCustomerActivity.class);
                            startActivity(intent7);
                            break;
//                        case "随诊客户":
//                            MobclickAgent.onEvent(context, "follow_diagnose_customer");
//                            Intent intent3 = new Intent(context, FollowDiagnoseActivity.class);
//                            startActivity(intent3);
//                            break;
                        case "即将到期回公海":
                            MobclickAgent.onEvent(context, "back_public_ocean");
                            Intent intent2 = new Intent(context, MaturingCustomerActivity.class);
                            startActivity(intent2);
                            break;
                    }
                }
            });
            menu_grid_view.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMenuList() {
        menuItemList = new ArrayList<MenuItemInfo>();

        MenuItemInfo menuItemInfo = new MenuItemInfo();
        menuItemInfo.setMenuDescription("今日可开单");
        menuItemInfo.setMessageNumber(0);
        menuItemList.add(menuItemInfo);

        MenuItemInfo menuItemInfo1 = new MenuItemInfo();
        menuItemInfo1.setMenuDescription("今日工作");
        menuItemInfo1.setMessageNumber(0);
        menuItemList.add(menuItemInfo1);

        MenuItemInfo menuItemInfo2 = new MenuItemInfo();
        menuItemInfo2.setMenuDescription("生日客户");
        menuItemInfo2.setMessageNumber(0);
        menuItemList.add(menuItemInfo2);

        MenuItemInfo menuItemInfo3 = new MenuItemInfo();
        menuItemInfo3.setMenuDescription("活动回访");
        menuItemInfo3.setMessageNumber(0);
        menuItemList.add(menuItemInfo3);

        MenuItemInfo menuItemInfo4 = new MenuItemInfo();
        menuItemInfo4.setMenuDescription("其他回访");
        menuItemInfo4.setMessageNumber(0);
        menuItemList.add(menuItemInfo4);

        MenuItemInfo menuItemInfo5 = new MenuItemInfo();
        menuItemInfo5.setMenuDescription("新分客户");
        menuItemInfo5.setMessageNumber(0);
        menuItemList.add(menuItemInfo5);

//        MenuItemInfo menuItemInfo6 = new MenuItemInfo();
//        menuItemInfo6.setMenuDescription("随诊客户");
//        menuItemInfo6.setMessageNumber(0);
//        menuItemList.add(menuItemInfo6);

        MenuItemInfo menuItemInfo7 = new MenuItemInfo();
        menuItemInfo7.setMenuDescription("即将到期回公海");
        menuItemInfo7.setMessageNumber(0);
        menuItemList.add(menuItemInfo7);
    }

    private void getData() {
        context = getContext();
        final JSONObject jsonObject = new JSONObject();
        HttpUtils.postWithJson(context, Constant.INDEX_INDEX, jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            if (!TextUtils.isEmpty(response.getString("data"))) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<JsonResult<HomeModelInfo>>() {
                                }.getType();
                                JsonResult<HomeModelInfo> jsonResult = gson.fromJson(response.toString(), type);

                                String code = jsonResult.getCode();
                                homeModelInfo = jsonResult.getData();
                                if (android.text.TextUtils.equals(code, Result.SUCCESS) && homeModelInfo != null) {
//                                    df.format(Double.valueOf(homeModelInfo.getSale_total()))

                                    double num = ((int) (Double.valueOf(homeModelInfo.getSale_total()) * 10)) / 10.0;
                                    initChart("<b>" + num + "万</b><br/>" + "<small>" + df.format(Double.valueOf(homeModelInfo.getSale_percent())) + "%</small>", salesAmountChart, 0);
                                    initChart("<b>" + homeModelInfo.getNew_total() + "个</b><br/>" + "<small>" + df.format(Double.valueOf(homeModelInfo.getNew_percent())) + "%</small>", new_turnoverRate, 1);
                                    initChart("<b>" + homeModelInfo.getOld_total() + "个</b><br/>" + "<small>" + df.format(Double.valueOf(homeModelInfo.getOld_percent())) + "%</small>", old_turnoverRate, 2);

                                    menuItemList.get(0).setMessageNumber(Integer.parseInt(homeModelInfo.getOpenordernum()));
                                    menuItemList.get(1).setMessageNumber(Integer.parseInt(homeModelInfo.getWorknum()));
                                    menuItemList.get(2).setMessageNumber(Integer.parseInt(homeModelInfo.getBirthdaynum()));
                                    menuItemList.get(3).setMessageNumber(Integer.parseInt(homeModelInfo.getActivitynum()));
                                    menuItemList.get(4).setMessageNumber(Integer.parseInt(homeModelInfo.getOtheractivitynum()));
                                    menuItemList.get(5).setMessageNumber(Integer.parseInt(homeModelInfo.getNewnum()));
//                                    menuItemList.get(6).setMessageNumber(Integer.parseInt(homeModelInfo.getAnswer_total()));
                                    menuItemList.get(6).setMessageNumber(Integer.parseInt(homeModelInfo.getExpiringnum()));
                                    adapter.notifyDataSetChanged();
                                }
                            } else if (Result.SIGNATURE_ERROR.equals(response.getString("code"))) {
                                Toast.makeText(context, "您的帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        //Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        //Toast.makeText(context, "网络不稳定稍后再试", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setData(PieChart chart, int type) {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        if (homeModelInfo != null) {
            switch (type) {
                case 0:
                    //已完成
                    float salePercent = Float.valueOf(homeModelInfo.getSale_percent());
                    if (salePercent >= 100) {
                        entries.add(new PieEntry(100f, "已完成"));
                        //未完成
                        entries.add(new PieEntry(0, "未完成"));
                    } else {
                        entries.add(new PieEntry(Float.valueOf(homeModelInfo.getSale_percent()), "已完成"));
                        //未完成
                        entries.add(new PieEntry((100 - Float.valueOf(homeModelInfo.getSale_percent())), "未完成"));
                    }
                    break;
                case 1:
                    float newPercent = Float.valueOf(homeModelInfo.getNew_percent());
                    if (newPercent >= 100) {
                        //已完成
                        entries.add(new PieEntry(100, "已完成"));
                        //未完成
                        entries.add(new PieEntry(0, "未完成"));
                    } else {
                        //已完成
                        entries.add(new PieEntry(Float.valueOf(homeModelInfo.getNew_percent()), "已完成"));
                        //未完成
                        entries.add(new PieEntry((100 - Float.valueOf(homeModelInfo.getNew_percent())), "未完成"));
                    }
                    break;
                default:
                    float oldPercent = Float.valueOf(homeModelInfo.getOld_percent());
                    if (oldPercent >= 100) {
                        entries.add(new PieEntry(100, "已完成"));
                        //未完成
                        entries.add(new PieEntry(0, "未完成"));
                    } else {
                        entries.add(new PieEntry(Float.valueOf(homeModelInfo.getOld_percent()), "已完成"));
                        //未完成
                        entries.add(new PieEntry((100 - Float.valueOf(homeModelInfo.getOld_percent())), "未完成"));
                    }
                    break;
            }
        } else {
            entries.add(new PieEntry(0f, "已完成"));
            //未完成
            entries.add(new PieEntry((100f), "未完成"));

        }
        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(3f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
       /* for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);*/
        if (type == 0) {
            colors.add(Color.rgb(252, 237, 108));
        } else if (type == 1) {
            colors.add(Color.rgb(157, 254, 147));
        } else {
            //#8BFFBE
            colors.add(Color.rgb(139, 255, 190));
        }

        colors.add(Color.rgb(253, 176, 135));
        colors.add(Color.rgb(253, 176, 135));

        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        //设置是否显示区域百分比的值
        for (IDataSet<?> set : chart.getData().getDataSets()) {
            set.setDrawValues(false);
        }

        // undo all highlights
        chart.highlightValues(null);
        chart.invalidate();
    }

    /**
     * 检测版本升级
     */
    private void checkUpdate() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "1");//平台（0-ios，1-安卓）
            jsonObject.put("name", "TO-B");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.postWithJson(getContext(), Constant.CHECK_UPDATE, jsonObject, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!TextUtils.isEmpty(response.getString("data"))) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<JsonResult<UpdateInfo>>() {
                        }.getType();
                        JsonResult<UpdateInfo> jsonResult = gson.fromJson(response.toString(), type);
                        String code = jsonResult.getCode();
                        final UpdateInfo updateInfo = jsonResult.getData();
                        if (TextUtils.equals(code, Result.SUCCESS) && updateInfo != null) {
                            String localVersion = DownloadUtils.getServerVersion(getContext());
                            if (!localVersion.equals(updateInfo.getVersion())) {
                                shwoUpdateDialog(updateInfo.getUrl(), updateInfo.getDesciption());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }
        });
    }


    private void shwoUpdateDialog(final String url, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("升级提示");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            builder.setMessage("检测到最新版本，请及时更新！" +
//                    "如果无法正常更新，请打开设置-》应用管理-》伊美尔-》权限，手动打开【存储】功能。");
//        } else {
//            builder.setMessage("检测到最新版本，请及时更新！");
//        }
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("更新", "下载Apk，更新App");
                downLoadApk(Constant.BASEURL_IMG + url);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void downLoadApk(final String filepath) {
        final ProgressDialog pd;
        pd = new ProgressDialog(getContext());
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = DownloadUtils.getFileFromServer(filepath, pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        getData();

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

    }

    @Override
    public void onNothingSelected() {

    }
}
