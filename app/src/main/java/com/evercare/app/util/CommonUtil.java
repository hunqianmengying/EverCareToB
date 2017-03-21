package com.evercare.app.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：xlren on 16/10/14 14:44
 * 邮箱：renxianliang@126.com
 * 通用工具类
 */
public class CommonUtil {

//    /**
//     * 沉浸式状态栏
//     */
//    public static void initState(Activity activity) {
//        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
////            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
//    }


    /**
     * 获取设备Id
     *
     * @return deviceId
     */
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (imei != null) {
            return imei;
        }
        return "";
    }


    /**
     * 截取时间
     */
    public static String stringToDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat test = new SimpleDateFormat("yyyy-MM-dd");
        return test.format(date);
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static String getLocalVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        StringBuilder sb = new StringBuilder();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(context),
                    0);
            // 版本名
            sb.append(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        context = null;
        return sb.toString();
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static void setListViewHeightBasedOnChildren(RecyclerView listView) {
        // 获取ListView对应的Adapter
//        ListAdapter listAdapter = listView.getAdapter();
//        if(listAdapter == null) {
//            return;
//        }
//        int totalHeight = 0;
//        for(int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0); // 计算子项View 的宽高
//            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight
//                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        // listView.getDividerHeight()获取子项间分隔符占用的高度
//        // params.height最后得到整个ListView完整显示需要的高度
//        listView.setLayoutParams(params);
    }
}
