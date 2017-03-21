package com.evercare.app.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evercare.app.R;

import io.rong.imkit.RongIM;

/**
 * 作者：LXQ on 2016-9-21 13:33
 * 邮箱：842202389@qq.com
 * 咨询界面
 */
public class ConversationActivity extends BaseFragmentActivity {
    private TextView returnImage;
    private TextView center_text;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        returnImage = (TextView) findViewById(R.id.left_text);
        returnImage.setText("返回");
        returnImage.setVisibility(View.VISIBLE);
        returnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        center_text = (TextView) findViewById(R.id.center_text);
       String title =  getIntent().getData().getQueryParameter("title");//获取消息title
        center_text.setText(title);
        //启动会话界面
/*        if (RongIM.getInstance() != null)
            RongIM.getInstance().startPrivateChat(this, "26594", "title");*/
    }
}
