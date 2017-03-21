package com.evercare.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.evercare.app.util.CrashHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.model.Conversation;
import com.evercare.app.provider.ActivityProvider;
import com.evercare.app.provider.ProjectCaseProvider;

/**
 * 作者：xlren on 2016/8/29 13:25
 * 邮箱：renxianliang@126.com
 */

public class MyApplication extends MultiDexApplication {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Config.isUmengSina = true;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        mContext = this;

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现

        RongIM.init(this);
        InputProvider.ExtendProvider[] ep = {new ProjectCaseProvider(RongContext.getInstance()),new ActivityProvider(RongContext.getInstance()),
                new ImageInputProvider(RongContext.getInstance())
                ,new CameraInputProvider(RongContext.getInstance())};
        //我需要让他在什么会话类型中的 bar 展示
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, ep);

        Fresco.initialize(this);

        UMShareAPI.get(this);
        //如果您使用了新浪微博，需要在这里设置回调地址：
        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
        PlatformConfig.setWeixin("wx0befbf707409f3f3", "993256574202f17ad64b089ff52d1538");
        PlatformConfig.setSinaWeibo("1818685083", "11a1ebcd053394c5d4d494f2e9f7c858");
        PlatformConfig.setQQZone("1105924842", "YLrqKHEkjugrYuet");
    }
}
