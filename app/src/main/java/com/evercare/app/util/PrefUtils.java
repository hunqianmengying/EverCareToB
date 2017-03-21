package com.evercare.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * 作者：xlren on 2016/8/29 13:25
 * 邮箱：renxianliang@126.com
 * SharedPreferences 存储
 */

public class PrefUtils {
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }
    public static void set(String filename,String key, String value, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(filename, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String get(String filename,String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(filename, MODE_PRIVATE);
        return prefs.getString(key, "");
    }
    public static void remove(String filename,String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(filename, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }
    public static  void setList(Context context,List<String> list,String tag){
        SharedPreferences.Editor editor = context.getSharedPreferences(tag, MODE_PRIVATE).edit();
        editor.putInt("EnvironNums", list.size());
        for (int i = 0; i < list.size(); i++)
        {
            editor.putString("item_"+i, list.get(i));
        }
        editor.commit();
    }
    public static  List<String> getList(Context context,String tag){
        List<String> environmentList = new ArrayList<String>();
        SharedPreferences preferDataList = context.getSharedPreferences(tag, MODE_PRIVATE);
        int environNums = preferDataList.getInt("EnvironNums", 0);
        for (int i = 0; i < environNums; i++)
        {
            String environItem = preferDataList.getString("item_"+i, null);
            environmentList.add(environItem);
        }
        return environmentList;
    }
}