package com.evercare.app.Activity;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evercare.app.R;
import com.evercare.app.broadcast.NetWorkReceiver;
import com.evercare.app.fragment.ConsultationFragment;
import com.evercare.app.fragment.CustomerFragment;
import com.evercare.app.fragment.FindFragment;
import com.evercare.app.fragment.HomeFragment;
import com.evercare.app.fragment.MeFragment;
import com.evercare.app.util.NetUtil;
import com.evercare.app.util.PrefUtils;
import com.evercare.app.util.RWTool;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.umeng.analytics.MobclickAgent;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * 作者：xlren on 16/9/13 上午11:02
 * 邮箱：renxianliang@126.com
 * 首页导航
 */

public class MainActivity extends FragmentActivity implements  NetWorkReceiver.EventHandler {
    private static String TAG = "MainActivity";
    private BottomBar mBottomBar;
    private int currentTab;
    private long firstTime;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private CustomerFragment customerFragment;
    private FindFragment findFragment;
    private ConsultationFragment consultationFragment;
    private MeFragment meFragment;
    private View mNetErrorView;

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (!isNetConnected) {
            mNetErrorView.setVisibility(View.VISIBLE);
        } else {
            mNetErrorView.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(MainActivity.this);
        if (!NetUtil.isNetConnected(this))
            mNetErrorView.setVisibility(View.VISIBLE);
        else
            mNetErrorView.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onResume(MainActivity.this);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        mNetErrorView = findViewById(R.id.net_status_bar_top);
        NetWorkReceiver.ehList.add(this);
        RWTool.appActivityList.add(this);

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                int index = 0;
                switch (tabId) {
                    case R.id.bottomBarHome:
                        MobclickAgent.onEvent(MainActivity.this, "home_page");
                        index = 0;
                        break;
                    case R.id.bottomBarLocation:
                        MobclickAgent.onEvent(MainActivity.this, "customer_page");
                        index = 1;
                        break;
                    case R.id.bottomBarMessage:
                        MobclickAgent.onEvent(MainActivity.this, "find_page");
                        index = 2;
                        break;
//                    case R.id.bottomConsult:
//                        MobclickAgent.onEvent(MainActivity.this, "consult_page");
//
//                        index = 3;
//                        break;
                    case R.id.bottomBarMe:
                        MobclickAgent.onEvent(MainActivity.this, "me_page");

                        index = 3;
                        break;
                }
                currentTab = index;
                setTabState(currentTab);
                setTabSelection(currentTab);
            }
        });
        mBottomBar.getTabAtPosition(0).setSelected(true);
        initRong(PrefUtils.getDefaults("rong_token", MainActivity.this));

    }


    /**
     * 设置选中Tab状态
     */
    private void setTabState(int curremttab) {
        mBottomBar.getTabAtPosition(0).setSelected(curremttab == 0);
        mBottomBar.getTabAtPosition(1).setSelected(curremttab == 1);
        mBottomBar.getTabAtPosition(2).setSelected(curremttab == 2);
        mBottomBar.getTabAtPosition(3).setSelected(curremttab == 3);
//        mBottomBar.getTabAtPosition(4).setSelected(curremttab == 4);
    }


    private void initRong(final String rong_token) {
        new Thread() {
            @Override
            public void run() {

                Log.w(TAG, "链接融云开始..........");

                RongIM.connect(rong_token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        Log.w(TAG, "链接融云,onTokenIncorrect..........");

                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.w(TAG, "链接融云,onSuccess.........." + s);
                        // 1
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().setCurrentUserInfo(new UserInfo(s, PrefUtils.getDefaults("userName", MainActivity.this),
                                    Uri.parse(PrefUtils.getDefaults("portraits", MainActivity.this))));
                            RongIM.getInstance().setMessageAttachedUserInfo(true);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.w(TAG, "链接融云,onError.........." + errorCode);

                    }

                });
            }
        }.start();
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     */
    private void setTabSelection(int index) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.main_fragment, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                break;
            case 1:
                if (customerFragment == null) {
                    customerFragment = new CustomerFragment();
                    transaction.add(R.id.main_fragment, customerFragment);
                } else {
                    transaction.show(customerFragment);
                }
                break;
            case 2:
                if (findFragment == null) {
                    findFragment = new FindFragment();
                    transaction.add(R.id.main_fragment, findFragment);
                } else {
                    transaction.show(findFragment);
                }
                break;
//            case 3:
//                if (consultationFragment == null) {
//                    consultationFragment = new ConsultationFragment();
//                    transaction.add(R.id.main_fragment, consultationFragment);
//                } else {
//                    transaction.show(consultationFragment);
//                }
//                break;
            case 3:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    transaction.add(R.id.main_fragment, meFragment);
                } else {
                    transaction.show(meFragment);
                }
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (customerFragment != null) {
            transaction.hide(customerFragment);
        }
        if (findFragment != null) {
            transaction.hide(findFragment);
        }
//        if (consultationFragment != null) {
//            transaction.hide(consultationFragment);
//        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }

    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show();
            firstTime = secondTime;
        } else {
            finish();
        }
    }
}
