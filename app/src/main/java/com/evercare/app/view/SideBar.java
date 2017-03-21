package com.evercare.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.evercare.app.R;
import com.evercare.app.util.LetterComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * 作者：LXQ on 2016-10-13 13:43
 * 邮箱：842202389@qq.com
 * 客户通讯录，右侧滑动控件
 */

public class SideBar extends View {

    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    private HashSet<String> letters = new HashSet<>();

    // 26个字母
    public String[] A_Z = {};/*"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"*/
    private int choose = -1;// 选中
    private Paint paint = new Paint();

    private TextView mTextDialog;

    /**
     * 为SideBar设置显示字母的TextView
     *
     * @param mTextDialog
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public void initLetter(HashSet<String> temLetters) {
        List<String> list = new ArrayList<>();
        list.addAll(temLetters);

        Collections.sort(list, new LetterComparator());

        this.A_Z = list.toArray(new String[0]);

        this.invalidate();
    }


    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (A_Z.length > 0) {
            // 获取焦点改变背景颜色.
            int height = getHeight() - 100;// 获取对应高度
            int width = getWidth(); // 获取对应宽度
            int singleHeight = height / A_Z.length - 5;// 获取每一个字母的高度  (这里-2仅仅是为了好看而已)

            for (int i = 0; i < A_Z.length; i++) {
                paint.setColor(Color.rgb(250, 141, 82));  //设置字体颜色
                paint.setTypeface(Typeface.DEFAULT_BOLD);  //设置字体
                paint.setAntiAlias(true);  //设置抗锯齿
                paint.setTextSize(30);  //设置字母字体大小
                // 选中的状态
                if (i == choose) {
                    paint.setColor(Color.parseColor("#3399ff"));  //选中的字母改变颜色
                    paint.setFakeBoldText(true);  //设置字体为粗体
                }
                // x坐标等于中间-字符串宽度的一半.
                float xPos = width / 2 - paint.measureText(A_Z[i]) / 2;
                double yPos = singleHeight * i + singleHeight / 2.0;
                canvas.drawText(A_Z[i], xPos, (float) yPos, paint);  //绘制所有的字母
                paint.reset();// 重置画笔
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (A_Z.length > 0) {
            final int action = event.getAction();
            final float y = event.getY();// 点击y坐标
            final int oldChoose = choose;
            final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
            final int c = (int) (y / getHeight() * A_Z.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

            switch (action) {
                case MotionEvent.ACTION_UP:
                    setBackgroundDrawable(new ColorDrawable(0x00000000));
                    choose = -1;//
                    invalidate();
                    if (mTextDialog != null) {
                        mTextDialog.setVisibility(View.INVISIBLE);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    setBackgroundDrawable(new ColorDrawable(0x00000000));
                    choose = -1;//
                    invalidate();
                    if (mTextDialog != null) {
                        mTextDialog.setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    //setBackgroundResource(R.drawable.sidebar_background);
                    if (oldChoose != c) {  //判断选中字母是否发生改变
                        if (c >= 0 && c < A_Z.length) {
                            if (listener != null) {
                                listener.onTouchingLetterChanged(A_Z[c]);
                            }
                            if (mTextDialog != null) {
                                mTextDialog.setText(A_Z[c]);
                                mTextDialog.setVisibility(View.VISIBLE);
                            }

                            choose = c;
                            invalidate();
                        }
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

}
