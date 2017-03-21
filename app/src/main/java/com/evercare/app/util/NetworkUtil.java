package com.evercare.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkUtil {
	
	/**
     * 判断网络状态是否可用
     * @return true: 网络可用 ; false: 网络不可用
     */ 
    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager conManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable(); 
        } 
        return false; 
    } 
    
    public String getNetworkType(Context ctx)  {
        ConnectivityManager connManager = (ConnectivityManager)ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(state == State.CONNECTED || state == State.CONNECTING){
        	  //System.out.println("wifi");
            return "wifi";  
        }  
      
        //3G网络判断  
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if(state == State.CONNECTED || state == State.CONNECTING){
            return "mobile";  
        }  
        return "none";  
    }
}
