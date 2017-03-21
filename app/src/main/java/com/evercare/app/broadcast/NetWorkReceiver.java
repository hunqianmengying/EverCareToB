package com.evercare.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.evercare.app.util.NetUtil;

import java.util.ArrayList;

/**
 * 作者：xlren on 2016-12-29 15:49
 * 邮箱：renxianliang@126.com
 * Javadoc
 */
public class NetWorkReceiver  extends BroadcastReceiver {
    public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

    public static abstract interface EventHandler {

        public abstract void onNetChange(boolean isNetConnected);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            boolean isNetConnected = NetUtil.isNetConnected(context);
            for (int i = 0; i < ehList.size(); i++)
                ((EventHandler) ehList.get(i)).onNetChange(isNetConnected);
        }
    }

}
