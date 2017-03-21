package com.evercare.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evercare.app.Activity.LoginActivity;
import com.evercare.app.Entity.JsonResult;
import com.evercare.app.Entity.Result;
import com.evercare.app.model.LoginInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * 作者：xlren on 2016/8/29 13:25
 * 邮箱：renxianliang@126.com
 * Http帮助类
 */

public class HttpUtils {

    public static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, AsyncHttpResponseHandler responseHandler) {
        //client.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        //String str = Constant.BASEURL + relativeUrl;
        return Constant.BASEURL + relativeUrl;
    }

    public static void getImage(String url, ResponseHandlerInterface responseHandler) {
        client.get(url, responseHandler);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 调用前参数先按照首字母排序
     *
     * @param context
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void postWithAuth(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        String sha1 = Sha1.SHA1(params.toString()+Constant.SIGNATURE);// 加密后的参数
//        params.put("sign",sha1);
//        String TOKEN=PrefUtils.get("user","token",context);
//        client.addHeader("Tp-Authorize", "token="+TOKEN);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * 后台接口 不能适应非json格式的传参
     *
     * @param context
     * @param url
     * @param jsonObject
     * @param responseHandler
     */
    public static void postWithJson(Context context, String url, JSONObject jsonObject, JsonHttpResponseHandler responseHandler) {
        client.setTimeout(30 * 1000);
        client.setConnectTimeout(30 * 1000);
        client.setResponseTimeout(30 * 1000);
        long sysTime = System.currentTimeMillis() / 1000;//本机时间
        long difference = 0;//本地存储时间差
        if (!TextUtils.isEmpty(PrefUtils.getDefaults("time", context))) {
            difference = Long.parseLong(PrefUtils.getDefaults("time", context));
        }
        long newTime = sysTime + difference;
        String md5Str = "";
        try {
            md5Str = Md5Utils.getMD5(jsonObject.toString() + PrefUtils.getDefaults("token", context) + PrefUtils.getDefaults("userId", context) + String.valueOf(newTime));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context, getAbsoluteUrl(url + "?" + "counselor_id=" + PrefUtils.getDefaults("userId", context) + "&time=" + newTime + "&sign=" + md5Str
        ), entity, "application/json", responseHandler);
    }

    public static void postWithAuth(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        String JSESSIONID = PrefUtils.get("user", "session", context);
        client.addHeader("Tp-Authorize", "token=" + JSESSIONID);
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    public static void getWithAuth(Context context, String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String JSESSIONID = PrefUtils.get("user", "session", context);
        client.addHeader("Tp-Authorize", "token=" + JSESSIONID);
        client.get(getAbsoluteUrl(url), asyncHttpResponseHandler);
    }

}
