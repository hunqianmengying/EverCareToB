package com.evercare.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.evercare.app.R;

/**
 * 作者：LXQ on 2016-11-8 15:45
 * 邮箱：842202389@qq.com
 * Javadoc
 */
public class OceanActionDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private String name;
    private String type;
    public ClickListenerInterface listener;

    public OceanActionDialog(Context context, String name, String type, ClickListenerInterface clickListener) {
        super(context);
        this.context = context;
        this.name = name;
        this.type = type;
        this.listener = clickListener;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_oceanaction);
        TextView txt_name = (TextView) findViewById(R.id.txt_name);
        txt_name.setText(this.name);

        this.setTitle("43434343");

        TextView txt_cancel = (TextView) findViewById(R.id.txt_cancel);
        TextView txt_back_public = (TextView) findViewById(R.id.txt_back_public);
        TextView txt_turn_private = (TextView) findViewById(R.id.txt_turn_private);

        if (type == "临时库") {
            txt_turn_private.setVisibility(View.VISIBLE);
        } else if (type == "私有库") {
            txt_turn_private.setVisibility(View.INVISIBLE);
        }

        txt_cancel.setOnClickListener(this);
        txt_back_public.setOnClickListener(this);
        txt_turn_private.setOnClickListener(this);


        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.8);
        layoutParams.height = (int) (displayMetrics.heightPixels * 0.36);
        window.setAttributes(layoutParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_turn_private://转到私海
                listener.doTurnToPrivate();
                break;
            case R.id.txt_back_public://退回公海
                listener.doBackToPublic();
                break;
            case R.id.txt_cancel://取消
                listener.doCancel();
                break;
        }
        dismiss();
    }


    public interface ClickListenerInterface {
        public void doCancel();

        public void doBackToPublic();

        public void doTurnToPrivate();
    }
}
