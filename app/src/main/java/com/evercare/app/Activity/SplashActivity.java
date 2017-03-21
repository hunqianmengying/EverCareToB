package com.evercare.app.Activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.evercare.app.R;
import com.evercare.app.util.PrefUtils;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * 作者：LXQ on 2016-9-7 9:27
 * 邮箱：842202389@qq.com
 * app入口启动图
 */
public class SplashActivity extends BaseActivity {
    private ImageView iv_start;
    private ViewPager contentView;
    private View dotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏代码
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        iv_start = (ImageView) findViewById(R.id.iv_start);
        contentView = (ViewPager) findViewById(R.id.contentView);
        dotView = findViewById(R.id.dotView);
        initImage();

    }

    /**
     * 初始化图片
     */
    private void initImage() {
        contentView.setVisibility(View.GONE);
        dotView.setVisibility(View.GONE);
        File dir = getFilesDir();
        final File imgFile = new File(dir, "start.png");
        if (imgFile.exists()) {
            iv_start.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        } else {
            iv_start.setImageResource(R.drawable.start);
        }
        iv_start.setImageResource(R.drawable.start);
        iv_start.setVisibility(View.VISIBLE);

        final ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnim.setFillAfter(true);
        scaleAnim.setDuration(3000);
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showNextActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_start.startAnimation(scaleAnim);
    }


    /**
     * 显示登录窗口/主窗口
     */
    private void showNextActivity() {
        String token = PrefUtils.getDefaults("token",  getApplicationContext());
        if (!TextUtils.isEmpty(token)) {
            startMainActivity();
        } else {
            startLoginActivity();
        }
    }


    private void startLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        finish();
    }


    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(SplashActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(SplashActivity.this);
    }
}
