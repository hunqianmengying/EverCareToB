package com.evercare.app.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;

import com.evercare.app.Activity.PreferenceActivity;
import com.evercare.app.R;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.InputProvider.ExtendProvider;
import io.rong.imlib.RongIMClient;
import io.rong.message.TextMessage;


/**
 * 作者：xlren on 2016-11-30 15:52
 * 邮箱：renxianliang@126.com
 * 项目案例
 */
public class ProjectCaseProvider extends ExtendProvider {
    HandlerThread mWorkThread;
    Handler mUploadHandler;
    private Context mContext;

    public ProjectCaseProvider(RongContext context) {
        super(context);
        this.mContext = context;
        mWorkThread = new HandlerThread("evercare");
        mWorkThread.start();
        mUploadHandler = new Handler(mWorkThread.getLooper());
    }
    //设置 展示的图标
    @Override
    public Drawable obtainPluginDrawable(Context arg0) {
        return arg0.getResources().getDrawable(R.drawable.ic_project_case);
    }
    //设置 图标下的title
    @Override
    public CharSequence obtainPluginTitle(Context arg0) {
        return "项目案例";
    }
    //点击事件
    @Override
    public void onPluginClick(View view) {
        Intent intent = new Intent(view.getContext(), PreferenceActivity.class);
        intent.putExtra("type", "案例");
        intent.putExtra("TAG","ProjectCaseProvider");
        this.startActivityForResult(intent,3);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3 && data!=null){
            String name = data.getStringExtra("name");
            String url = data.getStringExtra("url");
            String iamge = data.getStringExtra("iamge");
            mUploadHandler.post(new ProjectCaseProvider.MyRunnable(iamge,name,url));
        }
    }


    class MyRunnable implements Runnable {

        String mName;
        String mUrl;
        String mImage;

        public MyRunnable(String image,String name,String url) {
            mName = name;
            mUrl = url;
            mImage = image;
        }

        @Override
        public void run() {
            String showMessage = mName + "\n" + mUrl;
            final TextMessage content = TextMessage.obtain(showMessage);

            if (RongIM.getInstance().getRongIMClient() != null)

                RongIM.getInstance().getRongIMClient().sendMessage(getCurrentConversation().getConversationType(), getCurrentConversation().getTargetId(), content, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {

                    }
                });
        }
    }
} 