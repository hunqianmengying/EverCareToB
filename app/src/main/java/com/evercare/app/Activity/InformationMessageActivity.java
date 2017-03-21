package com.evercare.app.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evercare.app.R;
import com.evercare.app.util.NetworkUtil;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.editorpage.ShareActivity;
import com.umeng.socialize.media.UMImage;

import java.util.Map;

/**
 * 作者：LXQ on 2016-11-9 2016-11-9
 * 邮箱：842202389@qq.com
 * 活动/案例详细信息界面
 */
public class InformationMessageActivity extends BaseActivity implements OnClickListener {

    private TextView center_text;
    private TextView left_text;
    private ImageView right_image;

    private LayoutInflater inflater;
    private View mainlayout;
    private WebView web;
    private PopupWindow citypopupWindow;

    private String messageName;
    private String messageUrl;
    private UMImage umImage;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(InformationMessageActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(InformationMessageActivity.this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        mainlayout = inflater.inflate(R.layout.activity_informationmessage_layout, null);
        umImage = new UMImage(InformationMessageActivity.this, R.drawable.ic_logo);
        messageName = getIntent().getStringExtra("name");
        messageUrl = getIntent().getStringExtra("url");
        setContentView(mainlayout);
        init();

    }

    @SuppressLint("NewApi")
    public void init() {
        center_text = (TextView) findViewById(R.id.center_text);
        web = (WebView) findViewById(R.id.WebView_WV);
        //mTopTitle.setText(name);
        center_text.setText(messageName);

        left_text = (TextView) findViewById(R.id.left_text);
        left_text.setVisibility(View.VISIBLE);
        left_text.setText("返回");
        left_text.setOnClickListener(this);

        right_image = (ImageView) findViewById(R.id.right_image);
        right_image.setImageResource(R.drawable.ic_share);
        right_image.setVisibility(View.VISIBLE);
        right_image.setOnClickListener(this);
        //}
        createWeb();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_text:
                finish();
                break;
            case R.id.right_image:
                MobclickAgent.onEvent(InformationMessageActivity.this, "share");
                Shares();
                break;
        }
    }

    private void createWeb() {
        WebSettings settings = web.getSettings();

        // 设置是否支持变焦
        // settings.setSupportZoom(true);
        // 设置支持缩放
        settings.setBuiltInZoomControls(true);

        // 支持插件
        //settings.setPluginsEnabled(true);
        // 关闭webview中缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 多窗口
        settings.supportMultipleWindows();

        // 当webview调用requestFocus时为webview设置节点
        settings.setNeedInitialFocus(true);
        // 支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置在解码时时候用的默认编码
        settings.setDefaultTextEncodingName("utf-8");
        // 设置此属性，可任意比例缩放,将图片调整到适合webview的大小
        settings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);
        // 支持内容重新布局 设置布局方式 LayoutAlgorithm.SINGLE_COLUMN
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        settings.setSaveFormData(true);
        // //启用地理定位
        settings.setGeolocationEnabled(true);
        // 设置渲染优先级
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 设置定位的数据库路径
        // settings.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
        settings.setDomStorageEnabled(true);
        // 支持js
        settings.setJavaScriptEnabled(true);
        // 设置可以访问文件
        settings.setAllowFileAccess(true);
        // 6.如果webView中需要用户手动输入用户名、密码或其他，则webview必须设置支持获取手势焦点。
        //web.requestFocusFromTouch();             //设置之后有些三星手机输入框没有光标
        //web.requestFocus(View.FOCUSABLES_TOUCH_MODE);      //设置之后有些三星手机输入框没有光标
        web.isFocusableInTouchMode();


        // 取消Vertical ScrollBar显示
        web.setVerticalScrollBarEnabled(false);
        // 取消Horizontal ScrollBar显示
        web.setHorizontalScrollBarEnabled(false);
        // 设置缩放比例
        web.setInitialScale(100);

        // 设置滚动条隐藏
        // web.setLayerType(View.LAYER_TYPE_SOFTWARE, new Paint());
        web.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        web.setMapTrackballToArrowKeys(false);

        web.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return;
            }
        });

        /***打开本地缓存提供JS调用**/
        web.getSettings().setDomStorageEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        //设置应用缓存的最大尺寸
        web.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);


        Log.w("getCacheDir-----", "开始==================");
        // This next one is crazy. It's the DEFAULT location for your app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        //设置应用缓存的路径

        Log.w("getCacheDir-----", "结束==================");

        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        //开启应用程序缓存
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);

		/*
         *
		 * WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
		 * onJsAlert (WebView上alert是弹不出来东西的，需要定制你的WebChromeClient处理弹出)
		 * onCloseWindow 关闭WebView onCreateWindow 创建WebView onJsAlert
		 * 处理Javascript中的Alert对话框 onJsConfirm 处理Javascript中的Confirm对话框
		 * onJsPrompt 处理Javascript中的Prompt对话框 onProgressChanged 加载进度条改变
		 * onReceivedlcon 网页图标更改 onReceivedTitle 网页Title更改 onRequestFocus
		 * WebView 显示焦点
		 *
		 * 比如可以添加进度条，使得界面更友好 webview1.setWebChromeClient(new WebChromeClient() {
		 * public void onProgressChanged(WebView view, int progress) {
		 * setProgress(progress * 100); if(progress == 100){
		 * imageView1.setVisibility(View.GONE); tv1.setVisibility(View.GONE);
		 * pb1.setVisibility(View.GONE); fy1.setVisibility(View.GONE); } } } );
		 */
        // 此方法可以处理webview 在加载时和加载完成时一些操作
        // 就是怎么知道网页的加载进度和加载网页时，点击网页里面的链接还是在当前的webview里跳转，不想跳到浏览器那边，解决办法如下：
        // 是否显示网络图像
        settings.setBlockNetworkImage(false);
        web.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    // 这里是设置activity的标题， 也可以根据自己的需求做一些其他的操作
                    // title.setText("加载完成");
                    // Log.i("hanshuai","加载完成");
                } else {
                    // Log.i("hanshuai","加载中…….");
                    // title.setText("加载中…….");
                }
            }

        });

		/*
         * setWebChromeClient主要处理解析，渲染网页等浏览器做的事情 doUpdateVisitedHistory(WebView
		 * view, String url, boolean isReload) //(更新历史记录)
		 * onFormResubmission(WebView view, Message dontResend, Message resend)
		 * //(应用程序重新请求网页数据)onLoadResource(WebView view, String url) //
		 * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次 onPageStarted(WebView view, String
		 * url, Bitmap favicon)
		 * //这个事件就是开始载入页面调用的，通常我们可以在这设定一个loading的页面，告诉用户程序在等待网络响应。
		 * onPageFinished(WebView view, String url)
		 * //在页面加载结束时调用。同样道理，我们知道一个页面载入完成，于是我们可以关闭loading 条，切换程序动作。
		 * onReceivedError(WebView view, int errorCode, String description,
		 * String failingUrl)// (报告错误信息) onReceivedHttpAuthRequest(WebView view,
		 * HttpAuthHandler handler, String host,String realm)//（获取返回信息授权请求）
		 * onReceivedSslError(WebView view, SslErrorHandler handler, SslError
		 * error) //重写此方法可以让webview处理https请求。 onScaleChanged(WebView view, float
		 * oldScale, float newScale) // (WebView发生改变时调用)
		 * onUnhandledKeyEvent(WebView view, KeyEvent event) //（Key事件未被加载时调用）
		 * shouldOverrideKeyEvent(WebView view, KeyEvent
		 * event)//重写此方法才能够处理在浏览器中的按键事件。 shouldOverrideUrlLoading(WebView view,
		 * String url)
		 * //在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
		 * 。这个函数我们可以做很多操作
		 * ，比如我们读取到某些特殊的URL，于是就可以不打开地址，取消这个操作，进行预先定义的其他操作，这对一个程序是非常必要的。
		 */
        web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // ||url.equals("file://"+url3)
			/*	if (url.equals(baseurl + url1) || url.equals(baseurl + url2)
						|| url.equals(baseurl + url3)
						|| url.equals(baseurl + url4)
						|| url.equals(baseurl + url5)) {

					isfinish = false;
				} else {
					isfinish = true;
				}*/
                //Log.i("hanshuai", web.getUrl() + " onPageFinished  " + url+ isfinish);

                //Log.i("hanshuai", web.getUrl() + " onPageFinished  " + url+ isfinish);

                if (NetworkUtil.isNetworkConnected(InformationMessageActivity.this)) {
                } else {
                    //showNoNetWorkDialog();
                }
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边

                view.loadUrl(url);
                return true;

            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, android.net.http.SslError error) {
                // 重写此方法可以让webview处理https请求
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //Log.i("hanshuai", "-MyWebViewClient->onReceivedError()--\n errorCode="+errorCode+" \ndescription="+description+" \nfailingUrl="+failingUrl);
                //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
                //  view.loadData(errorHtml, "text/html", "UTF-8");
                //web.loadUrl("file:///android_asset/404.htm");
            }
        });
        web.post(new Runnable() {
            @Override
            public void run() {
                web.loadUrl(messageUrl);
            }
        });
        web.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
    }


    public void Shares() {
        initPopWindow();
    }

    public void initPopWindow() {

        final String Title = "伊美尔";
        View layout = inflater.inflate(R.layout.popup_share, null);
        RelativeLayout wechat_management = (RelativeLayout) layout.findViewById(R.id.wechat_management);
        RelativeLayout wxcircle_management = (RelativeLayout) layout.findViewById(R.id.wxcircle_management);
        RelativeLayout kongjian_management = (RelativeLayout) layout.findViewById(R.id.kongjian_management);
        RelativeLayout qq_management = (RelativeLayout) layout.findViewById(R.id.qq_management);
        RelativeLayout weibo_management = (RelativeLayout) layout.findViewById(R.id.weibo_management);
        TextView quxiao_text = (TextView) layout.findViewById(R.id.quxiao_text);
        wechat_management.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(InformationMessageActivity.this, "weixin_share");
                new ShareAction(InformationMessageActivity.this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
                        .withExtra(new UMImage(InformationMessageActivity.this, R.drawable.ic_launcher))
                        .withText(messageName)
                        .withTitle(Title)
                        .withTargetUrl(messageUrl)
                        .withMedia(umImage)
                        .share();

            }
        });
        wxcircle_management.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(InformationMessageActivity.this, "weixin_circle_share");
                new ShareAction(InformationMessageActivity.this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
                        .withExtra(new UMImage(InformationMessageActivity.this, R.drawable.ic_launcher))
                        .withText(messageName)
                        .withTitle(Title)
                        .withTargetUrl(messageUrl)
                        .withMedia(umImage)
                        .share();

            }
        });
        qq_management.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(InformationMessageActivity.this, "qq_share");
                new ShareAction(InformationMessageActivity.this).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener)
                        .withExtra(new UMImage(InformationMessageActivity.this, R.drawable.ic_launcher))
                        .withText(messageName)
                        .withTitle(Title)
                        .withTargetUrl(messageUrl)
                        .withMedia(umImage)
                        .share();

            }
        });
        kongjian_management.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(InformationMessageActivity.this, "qzone_share");
                new ShareAction(InformationMessageActivity.this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener)
                        .withExtra(new UMImage(InformationMessageActivity.this, R.drawable.ic_launcher))
                        .withText(messageName)
                        .withTitle(Title)
                        .withTargetUrl(messageUrl)
                        .withMedia(umImage)
                        .share();
            }
        });
        weibo_management.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(InformationMessageActivity.this, "sina_share");
                /** shareaction need setplatform , callbacklistener,and content(text,image).then share it **/
                new ShareAction(InformationMessageActivity.this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
                        .withExtra(new UMImage(InformationMessageActivity.this, R.drawable.ic_launcher))
                        .withText(messageName)
                        .withTitle(Title)
                        .withTargetUrl(messageUrl)
                        .withMedia(umImage)
                        .share();
            }
        });
        quxiao_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                citypopupWindow.dismiss();
            }
        });

        layout.setOnKeyListener(new android.view.View.OnKeyListener() {
            private int i;

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_MENU) && (citypopupWindow.isShowing()) && i == 1) {
                    citypopupWindow.dismiss();
                    return false;
                }
                i = 1;
                return false;
            }
        });
        // layout.getBackground().setAlpha(130);
        layout.invalidate();
        citypopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        citypopupWindow.setFocusable(true);
        citypopupWindow.setBackgroundDrawable(new BitmapDrawable());
        citypopupWindow.setAnimationStyle(R.style.PopupAnimation);
        citypopupWindow.showAtLocation(mainlayout, Gravity.BOTTOM, 0, 0);
        citypopupWindow.update();
//        UMShareAPI  mShareAPI = UMShareAPI.get( InformationMessageActivity.this );
//        mShareAPI.doOauthVerify(InformationMessageActivity.this, SHARE_MEDIA.SINA, umAuthListener);

    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            //Log.d("plat","platform"+platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(InformationMessageActivity.this, " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(InformationMessageActivity.this, " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(InformationMessageActivity.this, " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(InformationMessageActivity.this, " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
