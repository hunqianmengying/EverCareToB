package com.evercare.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者：xlren on 2016-10-19 15:05
 * 邮箱：renxianliang@126.com
 * activity基础类 左滑可以关闭
 */
public class BaseFragmentActivity extends FragmentActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    GestureDetector mGestureDetector;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector((GestureDetector.OnGestureListener) this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    private int verticalMinDistance = 20;
    private int minVelocity = 0;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(e2.getY() - e1.getY()) > 200) {//这里是避免有scroolview的页面上划也关闭
            return false;
        }
        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {//左滑操作
        } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {//右滑操作
            this.finish();
        }

        return false;
    }
}