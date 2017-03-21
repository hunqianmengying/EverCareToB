package com.evercare.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.evercare.app.R;


public class DialogManager {
	private Activity mActivity;
	private LayoutInflater mInflater;

	private DialogManager(Activity activity) {
		mActivity = activity;
		mInflater = mActivity.getLayoutInflater();
	}

	public static DialogManager getInstance(Activity activity) {
		DialogManager da = new DialogManager(activity);
		return da;
	}

	/**
	 * 请求数据 ProgressDialog
	 */
	public Dialog createProgressDialog(CharSequence title) {
		Dialog dialog = new Dialog(mActivity, R.style.NoDialogTitle);
		// dialog.setCancelable(false);
		View v = mInflater.inflate(R.layout.pop_progress, null);
		TextView tvMessage = (TextView) v.findViewById(R.id.textview_message);
		tvMessage.setText(title);
		dialog.setContentView(v);
		dialog.show();
		return dialog;
	}




}
